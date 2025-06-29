package com.mysite.sbb.answer;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {

    // 📚 [질문별 답변] 기본 정렬: 작성일 내림차순 (최신순)
    Page<Answer> findByQuestion(Question question, Pageable pageable);

    // 📚 [질문별 답변] 추천 수 많은 순 + 최신순 (복합정렬)
    @Query("""
        SELECT a FROM Answer a
        LEFT JOIN a.voter v
        WHERE a.question = :question
        GROUP BY a
        ORDER BY COUNT(v) DESC, a.createDate DESC
    """)
    Page<Answer> findByQuestionOrderByVoteCount(@Param("question") Question question, Pageable pageable);

    // 📚 [사용자별 답변] 특정 유저가 쓴 답변 전체
    List<Answer> findByAuthor(SiteUser author);

    // 📚 [전체 최신 답변] 전체 답변 중 최근 N개 (예: 메인, 최신 활동)
    List<Answer> findTop10ByOrderByCreateDateDesc();
}
