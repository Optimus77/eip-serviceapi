package com.inspur.eip.config.filter;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.config.CodeInfo;
import com.inspur.eip.entity.OrderSoftDown;
import com.inspur.eip.service.BssApiService;
import com.inspur.eip.util.HsConstants;
import com.inspur.eip.util.ReturnStatus;
import com.inspur.eip.util.CommonUtil;
import com.inspur.icp.common.util.Base64Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebFilter
@Slf4j
public class KeyClockAuthFilter implements Filter {

    @Autowired
    private BssApiService bssApiService;

    @Override
    public void init(FilterConfig filterConfig)  {
        log.info("******************KeyClockAuthFilter init");

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)throws IOException, ServletException {
        HttpServletRequest req= (HttpServletRequest)servletRequest;
        HttpServletResponse response=(HttpServletResponse)servletResponse;
        String method =  req.getMethod();
        log.debug("requtst:{}, {}",method ,  req.getPathInfo());
        if(req.getHeader("authorization") == null){
            log.info("get authorization is null ");

            JSONObject result=new JSONObject();
            result.put("code", ReturnStatus.SC_FORBIDDEN);
            result.put("message", CodeInfo.getCodeMessage(CodeInfo.KEYCLOAK_NULL));
            result.put("data",null);

            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(result.toJSONString());
            return;
        }else{
            String token = req.getHeader("Authorization");
            log.debug("get authorization {}",token);
            CommonUtil.setKeyClockInfo(Base64Util.decodeUserInfo(token));
        }
        if(method.equalsIgnoreCase(HsConstants.POST)  && req.getPathInfo().equalsIgnoreCase("/v1/orders/softdown")){

            String requestBody = CommonUtil.readRequestAsChars(req);
            log.info("get softdown eip order:{}.", requestBody);
            OrderSoftDown eipReciveOrder = JSON.parseObject(requestBody, OrderSoftDown.class);
            JSONObject result = bssApiService.onReciveSoftDownOrder(eipReciveOrder);

            response.setStatus(HttpStatus.SC_OK);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(result.toJSONString());
        }else if(method.equalsIgnoreCase(HsConstants.POST)  && req.getPathInfo().equalsIgnoreCase("/v1/sbws/softdown")){
            String requestBody = CommonUtil.readRequestAsChars(req);
            log.info("get softDelete sbw order:{}.", requestBody);
            OrderSoftDown softDown = JSON.parseObject(requestBody, OrderSoftDown.class);
            JSONObject result = bssApiService.stopOrSoftDeleteSbw(softDown);

            response.setStatus(HttpStatus.SC_OK);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(result.toJSONString());
        }else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
        if(method.equalsIgnoreCase(HsConstants.POST)  && req.getPathInfo().equalsIgnoreCase("/v1/orders/softdown")){

            String requestBody = com.inspur.eip.util.CommonUtil.readRequestAsChars(req);
            log.info("get softdown eip order:{}.", requestBody);
            OrderSoftDown eipReciveOrder = JSON.parseObject(requestBody, OrderSoftDown.class);
            JSONObject result = bssApiService.onReciveSoftDownOrder(eipReciveOrder);

            response.setStatus(HttpStatus.SC_OK);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(result.toJSONString());
        }else if(method.equalsIgnoreCase(HsConstants.POST)  && req.getPathInfo().equalsIgnoreCase("/v1/sbws/softdown")){
            String requestBody =  com.inspur.eip.util.CommonUtil.readRequestAsChars(req);
            log.info("get softDelete sbw order:{}.", requestBody);
            OrderSoftDown softDown = JSON.parseObject(requestBody, OrderSoftDown.class);
            JSONObject result = bssApiService.stopOrSoftDeleteSbw(softDown);

            response.setStatus(HttpStatus.SC_OK);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(result.toJSONString());
        }else {
            filterChain.doFilter(servletRequest, servletResponse);
        }

    }

    @Override
    public void destroy() {
        log.info("******************KeyClockAuthFilter destroy");

    }
}
