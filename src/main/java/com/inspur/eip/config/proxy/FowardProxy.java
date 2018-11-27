package com.inspur.eip.config.proxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author: jiasirui
 * @date: 2018/9/27 20:59
 * @description:
 */
@Configuration
public class FowardProxy   {


    @Value("${proxy.servlet_url}")
    private String servletUrl;

    @Value("${proxy.target_url}")
    private String targetUrl;

    @Value("${proxy.logging_enabled}")
    private String loggingEnabled;

    private static Log log = LogFactory.getLog(FowardProxy.class);

    @Bean
    public ServletRegistrationBean servletRegistrationBean(){
        log.info("-------ServletRegistrationBean start, set url:"+servletUrl+ "target:" +targetUrl);

        ProxyServlet proxyServlet=new ProxyServlet();
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(proxyServlet, servletUrl);
        servletRegistrationBean.addInitParameter(ProxyServlet.P_TARGET_URI, targetUrl);
        servletRegistrationBean.addInitParameter(ProxyServlet.P_LOG, loggingEnabled);
        return servletRegistrationBean;
    }



}
