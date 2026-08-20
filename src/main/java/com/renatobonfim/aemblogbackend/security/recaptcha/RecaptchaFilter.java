package com.renatobonfim.aemblogbackend.security.recaptcha;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renatobonfim.aemblogbackend.config.Constants;
import com.renatobonfim.aemblogbackend.dto.ErrorResponseDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class RecaptchaFilter extends OncePerRequestFilter {

    public static final String RECAPTCHA_TOKEN_HEADER = "X-Recaptcha-Token";

    private static final List<RequestMatcher> PROTECTED_ENDPOINTS = List.of(
            new AntPathRequestMatcher("/api/subscriber", "POST"),
            new AntPathRequestMatcher("/api/contact/send", "POST"),
            new AntPathRequestMatcher("/api/comment", "POST")
    );

    private final RecaptchaProperties properties;
    private final RecaptchaVerificationService recaptchaVerificationService;
    private final ObjectMapper objectMapper;

    public RecaptchaFilter(RecaptchaProperties properties, RecaptchaVerificationService recaptchaVerificationService, ObjectMapper objectMapper) {
        this.properties = properties;
        this.recaptchaVerificationService = recaptchaVerificationService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!properties.isEnabled()) {
            return true;
        }

        boolean dontFilter = PROTECTED_ENDPOINTS.stream().noneMatch(matcher -> matcher.matches(request));

        return dontFilter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader(RECAPTCHA_TOKEN_HEADER);

        if (token == null || token.isBlank()) {
            writeError(response, HttpStatus.BAD_REQUEST, Constants.RECAPTCHA_TOKEN_MISSING);
            return;
        }

        RecaptchaVerificationResult verificationResult = recaptchaVerificationService.verify(token, request.getRemoteAddr());

        if (!verificationResult.success() || !recaptchaVerificationService.isScoreAccepted(verificationResult.score())) {
            log.warn("reCAPTCHA rejected request to {} (success={}, score={})",
                    request.getRequestURI(),
                    verificationResult.success(),
                    verificationResult.score());

            writeError(response, HttpStatus.FORBIDDEN, Constants.RECAPTCHA_VERIFICATION_FAILED);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void writeError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getOutputStream(), ErrorResponseDTO.builder()
                .message(message)
                .timestamp(LocalDateTime.now().toString())
                .build());
    }
}
