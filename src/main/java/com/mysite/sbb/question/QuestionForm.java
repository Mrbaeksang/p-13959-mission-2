package com.mysite.sbb.question;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionForm {
    @NotEmpty(message = "제목은 필수항목입니다.")
    @Size(max=200)
    private String subject;

    @NotEmpty(message = "내용은 필수항목입니다.")
    private String content;

    @NotNull(message = "카테고리를 선택해 주세요.") // ★ 필수 검증
    private Long category;
}
