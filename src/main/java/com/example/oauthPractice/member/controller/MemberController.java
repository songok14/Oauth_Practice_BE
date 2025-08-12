package com.example.oauthPractice.member.controller;

import com.example.oauthPractice.common.auth.JwtTokenProvider;
import com.example.oauthPractice.member.domain.Member;
import com.example.oauthPractice.member.domain.SocialType;
import com.example.oauthPractice.member.dto.*;
import com.example.oauthPractice.member.service.GoogleService;
//import com.example.oauth.member.service.KakaoService;
import com.example.oauthPractice.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleService googleService;
//    private final KakaoService kakaoService;

    public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider, GoogleService googleService) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.googleService = googleService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> memberCreate(@RequestBody MemberCreateDto memberCreateDto){
        Member member = memberService.create(memberCreateDto);
        return new ResponseEntity<>(member.getId(), HttpStatus.CREATED);
    }

    @PostMapping("/doLogin")
    public ResponseEntity<?> doLogin(@RequestBody MemberLoginDto memberLoginDto){
//        email, password 일치한지 검증
        Member member = memberService.login(memberLoginDto);

//        일치할 경우 jwt accesstoken 생성
        String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());

        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", member.getId());
        loginInfo.put("token", jwtToken);
        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
    }

    @PostMapping("/google/doLogin")
    public ResponseEntity<?> googleLogin(@RequestBody RedirectDto redirectDto){
        GoogleProfileDto googleProfileDto = googleService.getAccessToken(redirectDto.getCode());
////        accesstoken 발급
//        AccessTokenDto accessTokenDto = googleService.getAccessToken(redirectDto.getCode());
////        사용자정보 얻기
//        GoogleProfileDto googleProfileDto = googleService.getGoogleProfile(accessTokenDto.getAccess_token());
//        회원가입이 되어 있지 않다면 회원가입
        Member originalMember = memberService.getMemberBySocialId(googleProfileDto.getSub());
        if(originalMember == null){
            originalMember = memberService.createOauth(googleProfileDto.getSub(), googleProfileDto.getEmail(), SocialType.GOOGLE);
        }
//        회원가입돼 있는 회원이라면 토큰발급
        String jwtToken = jwtTokenProvider.createToken(originalMember.getEmail(), originalMember.getRole().toString());

        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", originalMember.getId());
        loginInfo.put("token", jwtToken);
        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
    }


//    @PostMapping("/kakao/doLogin")
//    public ResponseEntity<?> kakaoLogin(@RequestBody RedirectDto redirectDto){
//        AccessTokenDto accessTokenDto = kakaoService.getAccessToken(redirectDto.getCode());
//        KakaoProfileDto kakaoProfileDto  = kakaoService.getKakaoProfile(accessTokenDto.getAccess_token());
//        Member originalMember = memberService.getMemberBySocialId(kakaoProfileDto.getId());
//        if(originalMember == null){
//            originalMember = memberService.createOauth(kakaoProfileDto.getId(), kakaoProfileDto.getKakao_account().getEmail(), SocialType.KAKAO);
//        }
//        String jwtToken = jwtTokenProvider.createToken(originalMember.getEmail(), originalMember.getRole().toString());
//
//        Map<String, Object> loginInfo = new HashMap<>();
//        loginInfo.put("id", originalMember.getId());
//        loginInfo.put("token", jwtToken);
//        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
//    }
}








