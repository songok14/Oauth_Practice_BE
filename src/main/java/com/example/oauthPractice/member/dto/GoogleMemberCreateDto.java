//package com.example.oauthPractice.member.dto;
//
//import com.example.oauthPractice.member.domain.Member;
//import com.example.oauthPractice.member.domain.SocialType;
//import jakarta.persistence.EnumType;
//import jakarta.persistence.Enumerated;
//import jakarta.validation.constraints.NotEmpty;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class GoogleMemberCreateDto {
//    @NotEmpty(message = "성함을 입력해 주세요.")
//    private String name;
//    @NotEmpty(message = "국적을 입력해 주세요.")
//    private String national;
//    @NotEmpty(message = "휴대폰 번호를 입력해 주세요.")
//    private String telNo;
//    @Enumerated(EnumType.STRING)
//    private SocialType socialType = SocialType.GOOSEBUMPS;
//    private String socialId = null;
//
////    public Member toEntity(GoogleMemberCreateDto googleMemberCreateDto){
////        return Member.builder()
////                .email(googleMemberCreateDto.getEmail())
////                .name(googleMemberCreateDto.name)
////                .national(googleMemberCreateDto.national)
////                .telNo(googleMemberCreateDto.telNo)
////                .build();
////    }
//}
