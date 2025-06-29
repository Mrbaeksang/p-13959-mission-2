package com.mysite.sbb.comment;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    // ✅ 질문에 댓글 작성
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/question/{id}")
    public String createOnQuestion(@PathVariable("id") Integer id,
                                   @Valid CommentForm commentForm,
                                   BindingResult bindingResult,
                                   Principal principal) {
        Question question = this.questionService.getQuestion(id);
        SiteUser author = this.userService.getUser(principal.getName());

        if (bindingResult.hasErrors()) {
            return String.format("redirect:/question/detail/%d", id);
        }

        this.commentService.createOnQuestion(question, commentForm.getContent(), author);
        return String.format("redirect:/question/detail/%d", id);
    }

    // ✅ 답변에 댓글 작성
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/answer/{id}")
    public String createOnAnswer(@PathVariable("id") Integer id,
                                 @Valid CommentForm commentForm,
                                 BindingResult bindingResult,
                                 Principal principal) {
        Answer answer = this.answerService.getAnswer(id);
        SiteUser author = this.userService.getUser(principal.getName());

        if (bindingResult.hasErrors()) {
            return String.format("redirect:/question/detail/%d", answer.getQuestion().getId());
        }

        this.commentService.createOnAnswer(answer, commentForm.getContent(), author);
        return String.format("redirect:/question/detail/%d#answer_%d",
                answer.getQuestion().getId(), answer.getId());
    }

    // ✅ 댓글 수정 폼
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String commentModifyForm(@PathVariable("id") Integer id,
                                    Principal principal,
                                    CommentForm commentForm,
                                    Model model) {
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한 없음");
        }
        commentForm.setContent(comment.getContent());
        model.addAttribute("comment", comment);
        return "comment_form";
    }

    // ✅ 댓글 수정 처리
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String commentModify(@PathVariable("id") Integer id,
                                @Valid CommentForm commentForm,
                                BindingResult bindingResult,
                                Principal principal) {
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한 없음");
        }

        if (bindingResult.hasErrors()) {
            return "comment_form";
        }

        this.commentService.modify(comment, commentForm.getContent());
        return redirectToTarget(comment);
    }

    // ✅ 댓글 삭제
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String commentDelete(@PathVariable("id") Integer id, Principal principal) {
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한 없음");
        }

        this.commentService.delete(comment);
        return redirectToTarget(comment);
    }

    // ✅ 댓글 대상에 따라 리다이렉트
    private String redirectToTarget(Comment comment) {
        if (comment.getQuestion() != null) {
            return "redirect:/question/detail/" + comment.getQuestion().getId();
        } else if (comment.getAnswer() != null) {
            Integer qid = comment.getAnswer().getQuestion().getId();
            return "redirect:/question/detail/" + qid + "#answer_" + comment.getAnswer().getId();
        } else {
            throw new IllegalStateException("댓글 대상 없음");
        }
    }
}
