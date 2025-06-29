package com.mysite.sbb.comment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserCommentDTO {
    private Long questionId; 
    private Long targetId;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate; // 수정일시 추가
    private String targetType;        // "질문"/"답변" 구분
    private String targetSummary;     // 질문 제목 or 답변 본문 일부
}
