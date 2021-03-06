package com.sx.sxblog.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    private TokenInterceptor tokenInterceptor;

    //构造方法
    public WebConfiguration(TokenInterceptor tokenInterceptor){
        this.tokenInterceptor = tokenInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowedOrigins("*");
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer){
        configurer.setTaskExecutor(new ConcurrentTaskExecutor(Executors.newFixedThreadPool(3)));
        configurer.setDefaultTimeout(30000);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
//        List<String> excludePath = new ArrayList<>();
        //排除拦截，除了注册登录(此时还没token)，其他都拦截
        //设置拦截请求
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/**");//测试不拦截
//                .excludePathPatterns("/error")
//                //user放行
//                .excludePathPatterns("/user/signIn")
//                .excludePathPatterns("/user/signUp")
//                .excludePathPatterns("/user/username_verify")
//                .excludePathPatterns("/user/email_verify")
//                .excludePathPatterns("/user/find_passwd")
//                //blog放行
//                .excludePathPatterns("/getBlogList")
//                .excludePathPatterns("/getBlogById")
//                .excludePathPatterns("/static/**")
//                .excludePathPatterns("/assets/**")
//                //两组tag采用pub前缀
//                .excludePathPatterns("/pub/**");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
