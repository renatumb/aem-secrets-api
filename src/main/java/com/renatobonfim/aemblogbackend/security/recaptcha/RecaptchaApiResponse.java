package com.renatobonfim.aemblogbackend.security.recaptcha;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class RecaptchaApiResponse {

    private boolean success;

    private Double score;

    private String action;

    @JsonProperty("challenge_ts")
    private String challengeTs;

    private String hostname;

    @JsonProperty("error-codes")
    private List<String> errorCodes;
}
