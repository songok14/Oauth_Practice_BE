package com.example.oauthPractice.member.service;

import com.example.oauthPractice.member.domain.Member;
import com.example.oauthPractice.member.domain.SocialType;
import com.example.oauthPractice.member.dto.AccessTokenDto;
import com.example.oauthPractice.member.dto.GoogleProfileDto;
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

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    public GoogleService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    public GoogleProfileDto getAccessToken(String code){
//        인가코드, clientId, client_secret, redirect_uri, grant_type

//        Spring6부터 RestTemplate 비추천상태이기에, 대신 RestClient 사용
        RestClient restClient = RestClient.create();
//        MultiValueMap을 통해 자동으로 form-data형식으로 body 조립 가능
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("grant_type", "authorization_code");

        ResponseEntity<AccessTokenDto> response =  restClient.post()
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
        ResponseEntity<GoogleProfileDto> response2 =  restClient.get()
                .uri("https://openidconnect.googleapis.com/v1/userinfo")
                .header("Authorization", "Bearer "+response.getBody())
                .retrieve()
                .toEntity(GoogleProfileDto.class);
        System.out.println("profile JSON" + response2.getBody());
//        return response2.getBody();
        Member originalMember = memberRepository.findBySocialId(response2.getBody().getSub()).orElse(null);
//        if (originalMember == null){
//            originalMember = Member.builder()
//                    .email(response2.getBody().getSub())
//                    .name(this.name)
//                    .national(this.national)
//                    .telNo(this.telNo)
//                    .build();
//
//        }
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
}
