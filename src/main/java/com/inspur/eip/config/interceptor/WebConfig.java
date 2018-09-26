package com.inspur.eip.config.interceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final static Log log = LogFactory.getLog(WebConfig.class);

    /**
     * HandlerMethodArgumentResolver     */
//    @Override
//    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
//        resolvers.add(new RequestModelArgumentResolver());
//    }

    /** 注入自定义拦截器HandlerInterceptor
     * @Description: 先add的拦截器会越靠外，即越靠近浏览器
     *
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/index").setViewName("index");
    }*/
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        log.info("***********************addInterceptors***************************************");
        WebMvcConfigurer.super.addInterceptors(registry);
        registry.addInterceptor(new CustomInterceptor()).addPathPatterns("/**/eips**");//拦截url
        log.info(registry);
        log.info("***********************addInterceptors***************************************");
    }

}
