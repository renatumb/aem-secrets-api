package com.renatobonfim.aemblogbackend.security.recaptcha;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
@EnableConfigurationProperties(RecaptchaProperties.class)
public class RecaptchaConfig {

    @Bean
    public RestClient recaptchaRestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(3));

        return RestClient.builder()
                .requestFactory(requestFactory)
                .requestInterceptor((request, body, execution) -> {
                    StringBuilder curl = new StringBuilder("curl");
                    curl.append(" -X ").append(request.getMethod());

                    // Headers
                    request.getHeaders().forEach((name, values) -> {
                        for (String value : values) {
                            curl.append(" -H ").append(shellQuote(name + ": " + value));
                        }
                    });
                    // Body
                    if (body.length > 0) {
                        curl.append(" --data ").append(shellQuote(new String(body, StandardCharsets.UTF_8)));
                    }
                    // URL
                    curl.append(" ").append(shellQuote(request.getURI().toString()));
                    log.trace("Request issued to G-captcha: {} ", curl.toString());

                    ClientHttpResponse execute = execution.execute(request, body);
                    return execute;
                })
                .build();
    }

    private static String shellQuote(String value) {
        return "'" + value.replace("'", "'\\''") + "'";
    }
}
