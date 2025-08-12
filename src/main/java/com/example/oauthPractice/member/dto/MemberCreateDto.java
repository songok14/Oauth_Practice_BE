package com.example.oauthPractice.member.dto;

import com.example.oauthPractice.member.domain.Member;
import com.example.oauthPractice.member.domain.SocialType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberCreateDto {
    @NotEmpty(message = "이메일을 입력해 주세요.")
    private String email;
    @NotEmpty(message = "비밀번호를 입력해 주세요.")
    private String password;
    @NotEmpty(message = "성함을 입력해 주세요.")
    private String name;
    @NotEmpty(message = "국적을 입력해 주세요.")
    private String national;
    @NotEmpty(message = "휴대폰 번호를 입력해 주세요.")
    private String telNo;
    @Enumerated(EnumType.STRING)
    private SocialType socialType = SocialType.GOOSEBUMPS;
    private String socialId = null;

    public Member toEntity(String encodePw){
        return Member.builder()
                .email(this.email)
                .password(encodePw)
                .name(this.name)
                .national(this.national)
                .telNo(this.telNo)
                .build();
    }
}
