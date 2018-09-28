package com.inspur.eip.config.proxy;

import com.inspur.eip.config.interceptor.CustomInterceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * @author: jiasirui
 * @date: 2018/9/27 20:59
 * @description:
 */
@Configuration
public class FowardProxy  implements EnvironmentAware {


    private final static Log log = LogFactory.getLog(FowardProxy.class);

    private static Properties properties = new Properties();
    static{
        try {
            properties= PropertiesLoaderUtils.loadAllProperties("test.yml");
            for(Object key:properties.keySet()){
                log.info(key+":"+properties.get(key));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean(){
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new ProxyServlet(), properties.getProperty("servlet_url"));
        servletRegistrationBean.addInitParameter(ProxyServlet.P_TARGET_URI, properties.getProperty("target_url"));
        servletRegistrationBean.addInitParameter(ProxyServlet.P_LOG, properties.getProperty("logging_enabled", "true"));
        return servletRegistrationBean;
    }

    @Override
    public void setEnvironment(Environment environment) {

    }
}
