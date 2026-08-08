package com.renatobonfim.aemblogbackend.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app.security.access")
public class SecurityAccessProperties {

    private List<String> publicEndpoints = new ArrayList<>();
    private List<String> read = new ArrayList<>();
    private List<String> write = new ArrayList<>();

    public void setPublic(List<String> endpoints) {
        this.publicEndpoints = endpoints;
    }

    public void setRead(List<String> read) {
        this.read = read;
    }

    public void setWrite(List<String> write) {
        this.write = write;
    }

    public RequestMatcher[] getPublicMatchers() {
        return toMatchers(publicEndpoints);
    }

    public RequestMatcher[] getReadMatchers() {
        return toMatchers(read);
    }

    public RequestMatcher[] getWriteMatchers() {
        return toMatchers(write);
    }

    private RequestMatcher[] toMatchers(List<String> entries) {

        RequestMatcher[] requestMatchers = entries.stream()
                .map(entry -> {
                    String[] parts = entry.split(":", 2);
                    HttpMethod method = HttpMethod.valueOf(parts[0].trim().toUpperCase());
                    String pattern = parts[1].trim();
                    return (RequestMatcher) new AntPathRequestMatcher(pattern, method.name());
                })
                .toArray(RequestMatcher[]::new);

        return requestMatchers;
    }
}
