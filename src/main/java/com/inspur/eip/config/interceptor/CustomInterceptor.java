package com.inspur.eip.config.interceptor;


import com.alibaba.fastjson.JSON;
import com.inspur.eip.config.interceptor.annotation.Forword;
import com.inspur.eip.config.pool.http.clientImpl.HttpConnectionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

public class CustomInterceptor implements HandlerInterceptor {

    @Autowired
    HttpConnectionManager httpPoolManager;

    private final static Log log = LogFactory.getLog(CustomInterceptor.class);

    private static Properties properties = new Properties();
    private RestTemplate restTemplate = new RestTemplate();

    static{
        try {
            properties= PropertiesLoaderUtils.loadAllProperties("constant-config.yml");
            for(Object key:properties.keySet()){
                log.info(key+":"+properties.get(key));
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
        return true;
        //return doForword(request,response,handler);
        //Forword forword = ((HandlerMethod) handler).getMethod().getAnnotation(Forword.class);
//        if (forword != null) {
//            log.info("can find @Forword in this uri:" + request.getRequestURI());
//            return doForword(request,response,handler);
//        }else{
//            log.info("can't find @Forword in this uri:"+ request.getRequestURI());
//            log.info("url is not contains eip do nothing");
//        }

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
        String ip = properties.getProperty("atomapiIp");
        String url = request.getRequestURI();
        url =ip+url;

        String queryString=request.getQueryString();
        if( queryString!=null){
            if(!queryString.isEmpty()){
                url=url+"?"+request.getQueryString();
            }
        }
        String method = request.getMethod();
        Map<String, String[]> param= request.getParameterMap();
        log.info("doForword--["+method+"]"+request.getRequestURL()+"?"+request.getQueryString()+"  ===>  "+url);



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
        httpHeaders.add("Cookie", JSON.toJSONString(cookies));
        httpHeaders.add("session",JSON.toJSONString(session));
        //httpHeaders.
        try{
            HttpEntity httpEntity = new HttpEntity(in,httpHeaders);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url,httpMethod,httpEntity,String.class,param);
            HttpHeaders header=responseEntity.getHeaders();
            String     body=responseEntity.getBody();
            //log.info(body);
            if (body!=null){
                OutputStream stream = response.getOutputStream();
                stream.write(body.getBytes("UTF-8"));
            }
            for (String key:header.keySet()){
                //log.info(key+"================"+header.get(key));
                String content=header.get(key).toString();
                //log.info(content.substring(1,content.length()-1));
                response.setHeader(key,content.substring(1,content.length()-1));
            }

        }catch (Exception e){
            log.info("foward get a error"+e.getMessage());
        }
        return false;
    }
}
