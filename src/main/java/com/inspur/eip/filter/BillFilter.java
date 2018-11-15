package com.inspur.eip.filter;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.EipReciveOrder;
import com.inspur.eip.service.EipServiceImpl;
import com.inspur.eip.util.CommonUtil;
import com.inspur.eip.util.HsConstants;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
            String requestBody = CommonUtil.readRequestAsChars(req);
            log.info("get create eip order:{}.",requestBody);
            JSONObject result = eipService.createOrder(requestBody);

            response.setStatus(HttpStatus.SC_ACCEPTED);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(result.toJSONString());
        }else if(method.equalsIgnoreCase(HsConstants.DELETE)  && req.getPathInfo().startsWith("/v2.0/eips/") &&
                 req.getPathInfo().length() == "/v2.0/eips/ff232e65-43bb-4ba4-ad43-f891cab7ce0a".length()){
            String eipId = req.getPathInfo().substring("/v2.0/eips/".length());
            log.info("get delete eip order,eipId:{}. ",eipId);
            JSONObject result = eipService.deleteEipOrder(eipId);

            response.setStatus(HttpStatus.SC_ACCEPTED);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(result.toJSONString());
        }else if(method.equalsIgnoreCase(HsConstants.POST)  && req.getPathInfo().equals("/v2.0/order")) {
            String requestBody = CommonUtil.readRequestAsChars(req);
            log.info("get create eip order:{}.", requestBody);
            EipReciveOrder eipReciveOrder =  JSON.parseObject(requestBody, EipReciveOrder.class);
            JSONObject result = eipService.onReciveCreateOrderResult(eipReciveOrder);

            response.setStatus(HttpStatus.SC_ACCEPTED);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(result.toJSONString());
        }else if(method.equalsIgnoreCase(HsConstants.DELETE)  && req.getPathInfo().startsWith("/v2.0/order")) {
            String requestBody = CommonUtil.readRequestAsChars(req);
            log.info("get delete eip order:{}.", requestBody);
            EipReciveOrder eipReciveOrder =  JSON.parseObject(requestBody, EipReciveOrder.class);
            JSONObject result = eipService.onReciveDeleteOrderResult(eipReciveOrder);

            response.setStatus(HttpStatus.SC_ACCEPTED);
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
