package com.mysite.sbb.question;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserQuestionDTO {
    private Long id;
    private String subject;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate; // 수정일시 추가
    // 카테고리명, 답변수 등 추가 가능
}
