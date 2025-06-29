package com.mysite.sbb.comment;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 📚 [질문별 댓글] 질문에 달린 댓글 (작성일순 오름차순)
    List<Comment> findByQuestionOrderByCreateDateAsc(Question question);

    // 📚 [답변별 댓글] 답변에 달린 댓글 (작성일순 오름차순)
    List<Comment> findByAnswerOrderByCreateDateAsc(Answer answer);

    // 📚 [사용자별 댓글] 특정 유저가 작성한 전체 댓글
    List<Comment> findByAuthor(SiteUser author);

    // 📚 [전체 최신 댓글] 전체 댓글 중 최근 N개 (예: 메인, 최신 활동)
    List<Comment> findTop10ByOrderByCreateDateDesc();
}
