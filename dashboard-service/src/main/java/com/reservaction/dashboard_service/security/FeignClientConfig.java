package com.reservaction.dashboard_service.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import feign.RequestInterceptor;
import feign.RequestTemplate;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class FeignClientConfig implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String token = request.getHeader(HttpHeaders.AUTHORIZATION);
                if (token != null) {
                    template.header(HttpHeaders.AUTHORIZATION, token);
                } else {
                    System.out.println("No token found in request header.");
                }
            }
        } catch (Exception e) {
            System.err.println("Error in extracting token.");
            e.printStackTrace();
        }
    }
}





