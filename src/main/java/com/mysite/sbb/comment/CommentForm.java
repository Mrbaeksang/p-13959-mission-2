// com.mysite.sbb.comment.CommentForm.java
package com.mysite.sbb.comment;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForm {
    @NotEmpty(message = "내용은 필수입니다.")
    private String content;
}
