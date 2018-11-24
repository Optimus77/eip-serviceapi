package com.inspur.eip.filter;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.EipReciveOrder;
import com.inspur.eip.entity.EipSoftDownOrder;
import com.inspur.eip.service.EipServiceImpl;
import com.inspur.eip.util.CommonUtil;
import com.inspur.eip.util.HsConstants;
import com.inspur.eip.util.ReturnStatus;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(2)
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
        String orderUri = "/v1/orders";
        if (req.getHeader("authorization") == null) {
            log.info("get authorization is null ");
            JSONObject result = new JSONObject();
            result.put("code", ReturnStatus.SC_FORBIDDEN);
            result.put("message", "keclock is null.");
            result.put("data", null);
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(result.toJSONString());
            return;
        }

        if(method.equalsIgnoreCase(HsConstants.POST)  && req.getPathInfo().equals("/v1/eips")){
            String requestBody = CommonUtil.readRequestAsChars(req);
            log.info("openapi create:{}.",requestBody);
            JSONObject result = eipService.createOrder(requestBody);

            response.setStatus(HttpStatus.SC_ACCEPTED);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(result.toJSONString());
        }else if(method.equalsIgnoreCase(HsConstants.DELETE)  && req.getPathInfo().startsWith("/v1/eips/") &&
                 req.getPathInfo().length() == "/v1/eips/ff232e65-43bb-4ba4-ad43-f891cab7ce0a".length()){
            String eipId = req.getPathInfo().substring("/v1/eips/".length());
            log.info("openapi delete,eipId:{}. ",eipId);
            JSONObject result = eipService.deleteEipOrder(eipId);

            response.setStatus(HttpStatus.SC_ACCEPTED);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(result.toJSONString());
        }else if(method.equalsIgnoreCase(HsConstants.POST)  && req.getPathInfo().equals(orderUri)) {
            String requestBody = CommonUtil.readRequestAsChars(req);
            log.info("get create eip order:{}.", requestBody);
            EipReciveOrder eipReciveOrder =  JSON.parseObject(requestBody, EipReciveOrder.class);
            JSONObject result = eipService.onReciveCreateOrderResult(eipReciveOrder);
            //todo
            response.setStatus(HttpStatus.SC_OK);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(result.toJSONString());
        }else if(method.equalsIgnoreCase(HsConstants.DELETE)  && req.getPathInfo().startsWith(orderUri)) {
            String requestBody = CommonUtil.readRequestAsChars(req);
            log.info("get delete eip order:{}.", requestBody);
            EipReciveOrder eipReciveOrder =  JSON.parseObject(requestBody, EipReciveOrder.class);
            JSONObject result = eipService.onReciveDeleteOrderResult(eipReciveOrder);

            response.setStatus(HttpStatus.SC_OK);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(result.toJSONString());
        }else if(method.equalsIgnoreCase(HsConstants.PUT)  && req.getPathInfo().startsWith(orderUri) &&
                req.getPathInfo().length() == "/v1/orders/ff232e65-43bb-4ba4-ad43-f891cab7ce0a".length()) {
            String requestBody = CommonUtil.readRequestAsChars(req);
            String eipId = req.getPathInfo().substring("/v1/orders/".length());
            log.info("get delete eip order:{}.", requestBody);
            EipReciveOrder eipReciveOrder = JSON.parseObject(requestBody, EipReciveOrder.class);
            JSONObject result = eipService.onReciveUpdateOrder(eipId, eipReciveOrder);

            response.setStatus(HttpStatus.SC_OK);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(result.toJSONString());
        }else if(method.equalsIgnoreCase(HsConstants.POST)  &&
                req.getPathInfo().equalsIgnoreCase("/v1/orders/softdown")){

            String requestBody = CommonUtil.readRequestAsChars(req);
            log.info("get softdown eip order:{}.", requestBody);
            EipSoftDownOrder eipReciveOrder = JSON.parseObject(requestBody, EipSoftDownOrder.class);
            JSONObject result = eipService.onReciveSoftDownOrder(eipReciveOrder);

            response.setStatus(HttpStatus.SC_OK);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(result.toJSONString());
        }else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
        log.info("BillFilter destroy");

    }

}
