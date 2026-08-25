package com.renatobonfim.aemblogbackend.security.recaptcha;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
public class RecaptchaVerificationService {

    private final RestClient recaptchaRestClient;
    private final RecaptchaProperties properties;

    public RecaptchaVerificationService(RestClient recaptchaRestClient, RecaptchaProperties properties) {
        this.recaptchaRestClient = recaptchaRestClient;
        this.properties = properties;
    }

    public RecaptchaVerificationResult verify(String token, String remoteIp) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("secret", properties.getSecretKey());
        form.add("response", token);

        if (remoteIp != null && !remoteIp.isBlank()) {
            form.add("remoteip", remoteIp);
        }

        try {
            RecaptchaApiResponse response = recaptchaRestClient.post()
                    .uri(properties.getVerifyUrl())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(RecaptchaApiResponse.class);

            if (response == null) {
                log.warn("reCAPTCHA verification returned an empty response");
                return new RecaptchaVerificationResult(false, 0.0, null);
            }

            log.info("reCAPTCHA response: {}", response);

            double score = response.getScore() != null ? response.getScore() : 0.0;
            return new RecaptchaVerificationResult(response.isSuccess(), score, response.getAction());
        } catch (RestClientException ex) {
            log.warn("reCAPTCHA verification request failed: {}", ex.getMessage());
            return new RecaptchaVerificationResult(false, 0.0, null);
        }
    }

    public boolean isScoreAccepted(double score) {
        return score >= properties.getMinScore();
    }
}
