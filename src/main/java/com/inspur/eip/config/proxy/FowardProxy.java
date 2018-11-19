package com.inspur.eip.config.proxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.boot.web.servlet.ServletRegistrationBean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * @author: jiasirui
 * @date: 2018/9/27 20:59
 * @description:
 */
@Configuration
public class FowardProxy   {



    private static Log log = LogFactory.getLog(FowardProxy.class);

    private static Properties properties = new Properties();
    static{
        log.info("FowardProxy get properties");
        try {
            properties= PropertiesLoaderUtils.loadAllProperties("application.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean(){
        log.info("-------ServletRegistrationBean start------------------------");

        ProxyServlet proxyServlet=new ProxyServlet();
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(proxyServlet, properties.getProperty("servlet_url"));
        servletRegistrationBean.addInitParameter(ProxyServlet.P_TARGET_URI, properties.getProperty("target_url"));
        servletRegistrationBean.addInitParameter(ProxyServlet.P_LOG, properties.getProperty("logging_enabled"));
        return servletRegistrationBean;
    }



}
