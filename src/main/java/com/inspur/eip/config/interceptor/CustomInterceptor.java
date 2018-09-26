package com.inspur.eip.config.interceptor;


import com.alibaba.fastjson.JSON;
import com.inspur.eip.config.interceptor.annotation.Forword;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

public class CustomInterceptor implements HandlerInterceptor {

    private final static Log log = LogFactory.getLog(CustomInterceptor.class);

    private static Properties properties = new Properties();
    private RestTemplate restTemplate = new RestTemplate();

    static{
        try {
            properties= PropertiesLoaderUtils.loadAllProperties("constant-config.yml");
            for(Object key:properties.keySet()){
                System.out.print(key+":");
                System.out.println(properties.get(key));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 在请求处理之前执行，主要用于权限验证、参数过滤等
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        log.info("CustomInterceptor ==> preHandle method: do request before");
        Forword forword = ((HandlerMethod) handler).getMethod().getAnnotation(Forword.class);
        if (forword != null) {
            log.info("can find @Forword in this uri:" + request.getRequestURI());
            return doForword(request,response,handler);
        }else{
            log.info("can't find @Forword in this uri:"+ request.getRequestURI());
            log.info("url is not contains eip do nothing");
            return doForword(request,response,handler);
        }

    }

    /**
     * 当前请求进行处理之后执行，主要用于日志记录、权限检查、性能监控、通用行为等
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        log.info("CustomInterceptor ==> postHandle method: do request after");
    }

    /**
     * 当前对应的interceptor的perHandle方法的返回值为true时,postHandle执行完成并渲染页面后执行，主要用于资源清理工作
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        log.info("CustomInterceptor ==> afterCompletion method: do request finshed");
    }

    private boolean doForword(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws URISyntaxException, IOException {
        log.info("doForword");
        String ip = properties.getProperty("atomapiIp");
        String url = request.getRequestURI();
        url =ip+url;
        log.info(request.getContextPath());
        log.info(request.getRequestURL());
        log.info(request.getQueryString());
        String queryString=request.getQueryString();
        if( queryString!=null){
            if(!queryString.isEmpty()){
                url=url+"?"+request.getQueryString();
            }
        }
        String method = request.getMethod();
        Map<String, String[]> param= request.getParameterMap();
        log.info(url);
        log.info(param);
        log.info(method);


        HttpMethod httpMethod =HttpMethod.resolve(method);



        BufferedReader streamReader =null;
        String in = null;
        try {
            streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            StringBuilder json = new StringBuilder();
            while ((in = streamReader.readLine()) != null) {
                json.append(in);
            }
            in = json.toString().trim();
            log.info(in);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            streamReader.close();
        }
        HttpHeaders httpHeaders =new HttpHeaders();
        Enumeration em = request.getHeaderNames();
        while (em.hasMoreElements()) {
            String name = (String) em.nextElement();
            String value = request.getParameter(name);
            httpHeaders.set(name,value);
        }
        httpHeaders.setContentType(APPLICATION_JSON_UTF8);
        Cookie[] cookies= request.getCookies();
        HttpSession session = request.getSession();
        String servername= request.getServerName();
        httpHeaders.add("Cookie", JSON.toJSONString(cookies));
        httpHeaders.add("session",JSON.toJSONString(session));
        //httpHeaders.
        HttpEntity httpEntity = new HttpEntity(in,httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url,httpMethod,httpEntity,String.class,param);
        String body=responseEntity.getBody();
        if (body!=null){
            OutputStream stream = response.getOutputStream();
            stream.write(body.getBytes("UTF-8"));
        }
        if (responseEntity.getStatusCode()!=null){
            response.setStatus(responseEntity.getStatusCode().value());
        }
        response.setContentType("application/json");
        return false;
    }
}
