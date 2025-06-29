package com.mysite.sbb.comment;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;

    /**
     * 질문에 댓글 생성
     */
    public Comment createOnQuestion(Question question, String content, SiteUser author) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreateDate(LocalDateTime.now());
        comment.setAuthor(author);
        comment.setQuestion(question);
        return commentRepository.save(comment);
    }

    /**
     * 답변에 댓글 생성
     */
    public Comment createOnAnswer(Answer answer, String content, SiteUser author) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreateDate(LocalDateTime.now());
        comment.setAuthor(author);
        comment.setAnswer(answer);
        return commentRepository.save(comment);
    }

    /**
     * 댓글 단건 조회
     */
    public Comment getComment(long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("댓글을 찾을 수 없습니다."));
    }

    /**
     * 댓글 수정
     */
    public void modify(Comment comment, String content) {
        comment.setContent(content);
        comment.setModifyDate(LocalDateTime.now());
        commentRepository.save(comment);
    }

    /**
     * 댓글 삭제
     */
    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }

    /**
     * 질문 댓글 전체 조회 (오래된 순)
     */
    public List<Comment> getCommentsForQuestion(Question question) {
        return commentRepository.findByQuestionOrderByCreateDateAsc(question);
    }

    /**
     * 답변 댓글 전체 조회 (오래된 순)
     */
    public List<Comment> getCommentsForAnswer(Answer answer) {
        return commentRepository.findByAnswerOrderByCreateDateAsc(answer);
    }

    /**
     * 질문에 속한 모든 답변들의 댓글 Map (답변ID → 댓글 리스트)
     */
    public Map<Integer, List<Comment>> getCommentsGroupedByAnswer(Question question) {
        Map<Integer, List<Comment>> commentMap = new HashMap<>();
        for (Answer answer : question.getAnswerList()) {
            List<Comment> comments = commentRepository.findByAnswerOrderByCreateDateAsc(answer);
            commentMap.put(answer.getId(), comments);
        }
        return commentMap;
    }

    /**
     * [내 프로필] 내가 쓴 댓글 목록
     */
    public List<Comment> getCommentsByUser(SiteUser user) {
        return commentRepository.findByAuthor(user);
    }

    /**
     * [최신 댓글] 전체 댓글 중 최근 10개 (최신순)
     */
    public List<Comment> getRecentComments() {
        return commentRepository.findTop10ByOrderByCreateDateDesc();
    }
}
