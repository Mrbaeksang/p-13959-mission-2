package com.mysite.sbb.user;

import com.mysite.sbb.answer.UserAnswerDTO;
import com.mysite.sbb.comment.UserCommentDTO;
import com.mysite.sbb.question.UserQuestionDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserProfileDTO {
    private String username;
    private String email;
    // 프로필에 추가 노출하고 싶으면 닉네임, 프로필이미지 등도 OK

    private List<UserQuestionDTO> questions;
    private List<UserAnswerDTO> answers;
    private List<UserCommentDTO> comments;
}
