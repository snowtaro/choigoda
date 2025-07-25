package com.choigoda.choigoda.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.choigoda.choigoda.security.JwtTokenProvider;
import com.choigoda.choigoda.entity.User;
import com.choigoda.choigoda.repository.UserRepository;
import com.choigoda.choigoda.dto.AuthRequest;
import com.choigoda.choigoda.dto.RegisterRequest;
import com.choigoda.choigoda.dto.AuthResponse;

import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @GetMapping("/login")
    public String login(){
        return "index";
    }

    /** 로그인 **/
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        // 1) 아이디/비밀번호로 인증 시도
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        // 2) 인증이 성공하면 JWT 토큰 생성
        String token = jwtTokenProvider.createToken(auth);
        // 3) 토큰을 클라이언트에 반환
        return ResponseEntity.ok(new AuthResponse(token));
    }

    /** 회원가입 **/
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        // 이미 가입된 사용자명인지 체크
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("이미 사용 중인 사용자명입니다.");
        }
        // 사용자 엔티티 생성 및 저장
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }
}
