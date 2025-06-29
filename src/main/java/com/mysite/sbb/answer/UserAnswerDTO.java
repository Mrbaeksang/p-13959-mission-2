package com.mysite.sbb.answer;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserAnswerDTO {
    private Long id;
    private Long questionId;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate; // 수정일시 추가
    private String questionSubject;   // 내가 단 답변이 어떤 질문에 달렸는지
}
