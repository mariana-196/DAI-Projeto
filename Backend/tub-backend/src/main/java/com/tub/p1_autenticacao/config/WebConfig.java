package com.tub.p1_autenticacao.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.tub.p1_autenticacao.interceptor.InterceptorSeguranca;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final InterceptorSeguranca securityInterceptor;

    public WebConfig(InterceptorSeguranca securityInterceptor) {
        this.securityInterceptor = securityInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(securityInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/**",
                        "/api/paineis",
                        "/api/previsoes/paineis-pmd",
                        "/api/previsoes/consulta/**"
                );
    }
}
