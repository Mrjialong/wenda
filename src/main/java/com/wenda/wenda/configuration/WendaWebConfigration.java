package com.wenda.wenda.configuration;

import com.wenda.wenda.interceptor.LoginRequredInterceptor;
import com.wenda.wenda.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
@Component
public class WendaWebConfigration extends WebMvcConfigurationSupport {
    @Autowired
    PassportInterceptor passportInterceptor;
    @Autowired
    LoginRequredInterceptor loginRequredInterceptor;

    /**
     * 添加自定义的拦截器
     * @param registry
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passportInterceptor);
        registry.addInterceptor(loginRequredInterceptor).addPathPatterns("/user/*");
        super.addInterceptors(registry);
    }
}
