package com.mysite.sbb.answer;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AnswerService {
    private final AnswerRepository answerRepository;

    /**
     * 답변 등록
     */
    public Answer create(Question question, String content, SiteUser author) {
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setQuestion(question);
        answer.setAuthor(author);
        this.answerRepository.save(answer);
        return answer;
    }

    /**
     * 답변 단건 조회
     */
    public Answer getAnswer(Integer id) {
        Optional<Answer> answer = answerRepository.findById(id);
        if (answer.isPresent()) {
            return answer.get();
        } else {
            throw new DataNotFoundException("답변을 찾을 수 없습니다.");
        }
    }

    /**
     * 답변 수정
     */
    public void modify(Answer answer, String content) {
        answer.setContent(content);
        answer.setModifyDate(LocalDateTime.now());
        this.answerRepository.save(answer);
    }

    /**
     * 답변 삭제
     */
    public void delete(Answer answer) {
        this.answerRepository.delete(answer);
    }

    /**
     * 답변 추천(투표)
     */
    public void vote(Answer answer, SiteUser siteUser) {
        answer.getVoter().add(siteUser);
        this.answerRepository.save(answer);
    }

    /**
     * [질문 상세] 질문별 답변 목록 (정렬 옵션 포함, 페이징)
     */
    public Page<Answer> getAnswers(Question question, int page, String sort) {
        Pageable pageable = PageRequest.of(page, 5);

        if ("vote".equals(sort)) {
            return answerRepository.findByQuestionOrderByVoteCount(question, pageable);
        } else {
            Sort sortOrder = Sort.by(Sort.Order.desc("createDate"));
            Pageable sortedPageable = PageRequest.of(page, 5, sortOrder);
            return answerRepository.findByQuestion(question, sortedPageable);
        }
    }

    /**
     * [내 프로필] 내가 쓴 답변 목록
     */
    public List<Answer> getAnswersByUser(SiteUser user) {
        return answerRepository.findByAuthor(user);
    }

    /**
     * [최신 답변] 전체 답변 중 최근 10개 (최신순)
     */
    public List<Answer> getRecentAnswers() {
        return answerRepository.findTop10ByOrderByCreateDateDesc();
    }
}
