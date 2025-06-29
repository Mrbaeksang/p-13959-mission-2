package com.mysite.sbb.user;

import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.answer.UserAnswerDTO;
import com.mysite.sbb.comment.CommentService;
import com.mysite.sbb.comment.UserCommentDTO;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.question.UserQuestionDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private  final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final CommentService commentService;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1());
        }catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    @GetMapping("/find-password")
    public String findPasswordForm() {
        return "find_password_form";
    }

    @PostMapping("/find-password")
    public String processFindPassword(@RequestParam String usernameOrEmail, Model model) {
        try {
            userService.resetPassword(usernameOrEmail);
            model.addAttribute("message", "입력하신 메일(또는 가입 아이디)로 임시 비밀번호가 발송되었습니다.");
        } catch (Exception e) {
            model.addAttribute("error", "일치하는 사용자가 없습니다.");
        }
        return "find_password_form";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        SiteUser siteUser = userService.getUser(principal.getName());
        model.addAttribute("siteUser", siteUser);
        return "profile_form";
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String newPasswordConfirm,
                                 Principal principal,
                                 Model model) {
        SiteUser user = userService.getUser(principal.getName());
        // 기존 비밀번호 체크
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            model.addAttribute("error", "현재 비밀번호가 일치하지 않습니다.");
            model.addAttribute("siteUser", user);
            return "profile_form";
        }
        if (!newPassword.equals(newPasswordConfirm)) {
            model.addAttribute("error", "새 비밀번호가 일치하지 않습니다.");
            model.addAttribute("siteUser", user);
            return "profile_form";
        }
        userService.changePassword(user, newPassword);
        model.addAttribute("message", "비밀번호가 성공적으로 변경되었습니다!");
        model.addAttribute("siteUser", user);
        return "profile_form";
    }

    @GetMapping("/profile/{username}")
    public String profile(@PathVariable String username, Model model) {
        SiteUser user = userService.getUser(username);

        UserProfileDTO profile = new UserProfileDTO();
        profile.setUsername(user.getUsername());
        profile.setEmail(user.getEmail());

        // 질문 변환
        List<UserQuestionDTO> questionDTOs = questionService.getQuestionsByUser(user)
                .stream().map(q -> {
                    UserQuestionDTO dto = new UserQuestionDTO();
                    dto.setSubject(q.getSubject());
                    dto.setContent(q.getContent());
                    dto.setCreateDate(q.getCreateDate());
                    dto.setModifyDate(q.getModifyDate());
                    // 필요시 카테고리명 등도 추가
                    return dto;
                }).toList();
        profile.setQuestions(questionDTOs);

        // 답변 변환
        List<UserAnswerDTO> answerDTOs = answerService.getAnswersByUser(user)
                .stream().map(a -> {
                    UserAnswerDTO dto = new UserAnswerDTO();
                    dto.setContent(a.getContent());
                    dto.setCreateDate(a.getCreateDate());
                    dto.setModifyDate(a.getModifyDate());
                    dto.setQuestionSubject(a.getQuestion().getSubject());
                    return dto;
                }).toList();
        profile.setAnswers(answerDTOs);

        // 댓글 변환
        List<UserCommentDTO> commentDTOs = commentService.getCommentsByUser(user)
                .stream().map(c -> {
                    UserCommentDTO dto = new UserCommentDTO();
                    dto.setContent(c.getContent());
                    dto.setCreateDate(c.getCreateDate());
                    dto.setModifyDate(c.getModifyDate());
                    if (c.getQuestion() != null) {
                        dto.setTargetType("질문");
                        dto.setTargetSummary(c.getQuestion().getSubject());
                    } else if (c.getAnswer() != null) {
                        dto.setTargetType("답변");
                        String answerContent = c.getAnswer().getContent();
                        if (answerContent != null && answerContent.length() > 20) {
                            dto.setTargetSummary(answerContent.substring(0, 20) + "..."); // 20자 이상일 경우 자르고 "..." 추가
                        } else {
                            dto.setTargetSummary(answerContent); // 20자 이하일 경우 전체 내용 사용
                        }
                    }
                    return dto;
                }).toList();
        profile.setComments(commentDTOs);

        model.addAttribute("profile", profile);
        return "profile";
    }

}
