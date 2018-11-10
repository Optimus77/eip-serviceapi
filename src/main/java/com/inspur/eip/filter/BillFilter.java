package com.inspur.eip.filter;


import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.service.EipServiceImpl;
import com.inspur.eip.util.HsConstants;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;


@WebFilter
public class BillFilter implements Filter {

    @Autowired
    private EipServiceImpl eipService;

    private final static Logger log = LoggerFactory.getLogger(BillFilter.class);


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("====BillFilter init===");

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)throws IOException, ServletException {
        log.info("===BillFilter doFilter===");
        HttpServletRequest req= (HttpServletRequest)servletRequest;
        HttpServletResponse response=(HttpServletResponse)servletResponse;
        String method =  req.getMethod();
        if(method.equalsIgnoreCase(HsConstants.POST)  && req.getPathInfo().equals("/v2.0/eips")){
            String requestBody = ReadAsChars(req);
            log.info("get create eip order:{}.",requestBody);
            JSONObject result = eipService.createOrder(requestBody);

            response.setStatus(HttpStatus.SC_ACCEPTED);
            response.setContentType(HsConstants.APPLICATION_JSON);

            response.getWriter().write(result.toJSONString());
        }else if(method.equalsIgnoreCase(HsConstants.DELETE)  && req.getPathInfo().startsWith("/v2.0/eips/")){
            String eipId = req.getPathInfo().substring("/v2.0/eips/".length());
            log.info("get delete eip order,eipId:{}. ",eipId);
            JSONObject result = eipService.deleteEipOrder(eipId);

            response.setStatus(HttpStatus.SC_ACCEPTED);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(result.toJSONString());
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
        log.info("BillFilter destroy");

    }


    public static String ReadAsChars(HttpServletRequest request) {

        StringBuilder sb = new StringBuilder("");
        try {
            BufferedReader br = request.getReader();
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
        } catch (IOException e) {
            log.error("ReadAsChars exception", e);
        }
        return sb.toString();
    }
}
