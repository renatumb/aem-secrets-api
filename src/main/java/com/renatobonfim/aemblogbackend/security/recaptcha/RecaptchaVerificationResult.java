package com.renatobonfim.aemblogbackend.security.recaptcha;

public record RecaptchaVerificationResult(boolean success, double score, String action) {
}
