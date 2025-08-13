package com.example.oauthPractice.member.service;

import com.example.oauthPractice.common.auth.JwtTokenProvider;
import com.example.oauthPractice.member.domain.Member;
import com.example.oauthPractice.member.domain.SocialType;
import com.example.oauthPractice.member.dto.AccessTokenDto;
import com.example.oauthPractice.member.dto.GoogleMemberCreateDto;
import com.example.oauthPractice.member.dto.GoogleProfileDto;
import com.example.oauthPractice.member.dto.RedirectDto;
import com.example.oauthPractice.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
@Transactional
public class GoogleService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    public GoogleService(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    public String googleLogin(RedirectDto redirectDto) {
//        인가코드, clientId, client_secret, redirect_uri, grant_type

//        Spring6부터 RestTemplate 비추천상태이기에, 대신 RestClient 사용
        RestClient restClient = RestClient.create();
//        MultiValueMap을 통해 자동으로 form-data형식으로 body 조립 가능
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", redirectDto.getCode());
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("grant_type", "authorization_code");

        ResponseEntity<AccessTokenDto> response = restClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
//                ?code=xxxx&client_id=yyyy&
                .body(params)
//                retrieve:응답 body값만을 추출
                .retrieve()
                .toEntity(AccessTokenDto.class);
        System.out.println("응답 accesstoken JSON " + response.getBody());
//        return response.getBody();
        RestClient restClient2 = RestClient.create();
        ResponseEntity<GoogleProfileDto> response2 = restClient2.get()
                .uri("https://openidconnect.googleapis.com/v1/userinfo")
                .header("Authorization", "Bearer " + response.getBody().getAccess_token())
                .retrieve()
                .toEntity(GoogleProfileDto.class);
        System.out.println("profile JSON" + response2.getBody());
//        return response2.getBody();
        Member originalMember = memberRepository.findBySocialId(response2.getBody().getSub()).orElse(null);
        if (originalMember != null) {
            return jwtTokenProvider.createToken(originalMember.getEmail(), originalMember.getRole().toString());
        } else {
//            createOauth(String socialId, String email, SocialType socialType)
            Member newMember = Member.builder()
                    .email(response2.getBody().getEmail())
                    .socialType(SocialType.GOOGLE)
                    .socialId(response2.getBody().getSub())
                    .build();
            memberRepository.save(newMember);
            return "(create)" + response.getBody();
        }
    }

//    public String googleCreate(GoogleMemberCreateDto googleMemberCreateDto) {
//        ResponseEntity<GoogleProfileDto> response2 = restClient2.get()
//                .uri("https://openidconnect.googleapis.com/v1/userinfo")
//                .header("Authorization", "Bearer " + googleMemberCreateDto.getGoogleToken().getBody().getAccess_token())
//                .retrieve()
//                .toEntity(GoogleProfileDto.class);
//        Member newMember = memberRepository.findBySocialId();
//    }
}

//    public GoogleProfileDto getGoogleProfile(String token){
//        RestClient restClient = RestClient.create();
//        ResponseEntity<GoogleProfileDto> response =  restClient.get()
//                .uri("https://openidconnect.googleapis.com/v1/userinfo")
//                .header("Authorization", "Bearer "+token)
//                .retrieve()
//                .toEntity(GoogleProfileDto.class);
//        System.out.println("profile JSON" + response.getBody());
//        return response.getBody();
//    }
//}
