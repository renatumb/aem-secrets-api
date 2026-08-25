package com.renatobonfim.aemblogbackend.security.recaptcha;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.config.recaptcha")
public class  RecaptchaProperties {

    private Boolean enabled ;
    private String secretKey;
    private Double minScore ;
    private String verifyUrl ;

    public boolean isEnabled() {
        return enabled;
    }
}
