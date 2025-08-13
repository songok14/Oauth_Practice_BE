package com.example.oauthPractice.member.dto;

import com.example.oauthPractice.member.domain.Member;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleMemberCreateDto {
    @NotEmpty(message = "성함을 입력해 주세요.")
    private String name;
    @NotEmpty(message = "국적을 입력해 주세요.")
    private String national;
    @NotEmpty(message = "휴대폰 번호를 입력해 주세요.")
    private String telNo;
    private AccessTokenDto googleToken;
}
