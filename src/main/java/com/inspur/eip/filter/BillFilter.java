package com.inspur.eip.filter;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.EipReciveOrder;
import com.inspur.eip.entity.EipSoftDownOrder;
import com.inspur.eip.entity.sbw.SbwCreateRecive;
import com.inspur.eip.service.BssApiService;
import com.inspur.eip.service.EipServiceImpl;
import com.inspur.eip.util.CommonUtil;
import com.inspur.eip.util.HsConstants;
import com.inspur.eip.util.ReturnStatus;
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
public class BillFilter implements Filter {

    @Autowired
    private EipServiceImpl eipService;

    @Autowired
    private BssApiService bssApiService;

    @Override
    public void init(FilterConfig filterConfig)  {
        log.info("====BillFilter init===");

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)throws IOException, ServletException {
        log.info("=====================================================================");
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
            String result = eipService.createOrder(requestBody);

            response.setStatus(HttpStatus.SC_ACCEPTED);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(result);
        }else if(method.equalsIgnoreCase(HsConstants.DELETE)  && req.getPathInfo().startsWith("/v1/eips/") &&
                 req.getPathInfo().length() == "/v1/eips/ff232e65-43bb-4ba4-ad43-f891cab7ce0a".length()){
            String eipId = req.getPathInfo().substring("/v1/eips/".length());
            log.info("openapi delete,eipId:{}. ",eipId);
            String result = eipService.deleteEipOrder(eipId);

            response.setStatus(HttpStatus.SC_ACCEPTED);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(result);
        }else if(method.equalsIgnoreCase(HsConstants.POST)  && req.getPathInfo().equals(orderUri)) {
            String requestBody = CommonUtil.readRequestAsChars(req);
            log.info("get create eip order:{}.", requestBody);
            EipReciveOrder eipReciveOrder =  JSON.parseObject(requestBody, EipReciveOrder.class);
            JSONObject result = bssApiService.onReciveCreateOrderResult(eipReciveOrder);
            //todo
            response.setStatus(HttpStatus.SC_OK);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(result.toJSONString());
        }else if(method.equalsIgnoreCase(HsConstants.DELETE)  && req.getPathInfo().startsWith(orderUri)) {
            String requestBody = CommonUtil.readRequestAsChars(req);
            log.info("get delete eip order:{}.", requestBody);
            EipReciveOrder eipReciveOrder =  JSON.parseObject(requestBody, EipReciveOrder.class);
            JSONObject result = bssApiService.onReciveDeleteOrderResult(eipReciveOrder);

            response.setStatus(HttpStatus.SC_OK);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(result.toJSONString());
        }else if(method.equalsIgnoreCase(HsConstants.POST)  && req.getPathInfo().startsWith(orderUri) &&
                req.getPathInfo().length() == "/v1/orders/ff232e65-43bb-4ba4-ad43-f891cab7ce0a".length()) {
            String requestBody = CommonUtil.readRequestAsChars(req);
            String eipId = req.getPathInfo().substring("/v1/orders/".length());
            log.info("get update eip order:{}.", requestBody);
            EipReciveOrder eipReciveOrder = JSON.parseObject(requestBody, EipReciveOrder.class);
            JSONObject result = bssApiService.onReciveUpdateOrder(eipId, eipReciveOrder);

            response.setStatus(HttpStatus.SC_OK);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(result.toJSONString());
        }else if(method.equalsIgnoreCase(HsConstants.POST)  &&
                req.getPathInfo().equalsIgnoreCase("/v1/orders/softdown")){

            String requestBody = CommonUtil.readRequestAsChars(req);
            log.info("get softdown eip order:{}.", requestBody);
            EipSoftDownOrder eipReciveOrder = JSON.parseObject(requestBody, EipSoftDownOrder.class);
            JSONObject result = bssApiService.onReciveSoftDownOrder(eipReciveOrder);

            response.setStatus(HttpStatus.SC_OK);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(result.toJSONString());
        }else if(method.equalsIgnoreCase(HsConstants.POST)  &&
                req.getPathInfo().equalsIgnoreCase("/v1/loggers/com.inspur.eip")){
            String packages = req.getPathInfo().substring("/v1/loggers/".length());
            String requestBody = CommonUtil.readRequestAsChars(req);
            String result = eipService.setLogLevel(requestBody, packages);
            response.setStatus(HttpStatus.SC_OK);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(result);
        }else  if(method.equalsIgnoreCase(HsConstants.POST)  && req.getPathInfo().equals(HsConstants.SBW_URI)){
            String requestBody = CommonUtil.readRequestAsChars(req);
            log.info("create sbw recive:{}.",requestBody);
            SbwCreateRecive recive =  JSON.parseObject(requestBody, SbwCreateRecive.class);
            JSONObject result = bssApiService.createShareBandWidth(recive);

            response.setStatus(HttpStatus.SC_OK);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(result.toJSONString());
        }else if (method.equalsIgnoreCase(HsConstants.DELETE)  &&req.getPathInfo().startsWith(HsConstants.SBW_URI) &&
                req.getPathInfo().length() == HsConstants.SBW_URI_ID_LENGTH.length()){
            String requestBody = CommonUtil.readRequestAsChars(req);
            log.info("delete shareBandWidth:{}.",requestBody);
            SbwCreateRecive sbwCreateRecive = JSON.parseObject(requestBody, SbwCreateRecive.class);
            JSONObject result = bssApiService.deleteShareBandWidth(sbwCreateRecive);

            response.setStatus(HttpStatus.SC_OK);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(result.toJSONString());

        }else if (method.equalsIgnoreCase(HsConstants.POST)  &&req.getPathInfo().startsWith(HsConstants.SBW_URI) &&
                req.getPathInfo().length() == HsConstants.SBW_URI_ID_LENGTH.length()){
            String requestBody = CommonUtil.readRequestAsChars(req);
            String sbwId = req.getPathInfo().substring("/v1/sbws/".length());
            log.info("update sbw config:{}.", requestBody);
            SbwCreateRecive sbwCreateRecive = JSON.parseObject(requestBody, SbwCreateRecive.class);
            JSONObject result = bssApiService.updateSbwConfig(sbwId, sbwCreateRecive);

            response.setStatus(HttpStatus.SC_OK);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(result.toJSONString());
        }else if(method.equalsIgnoreCase(HsConstants.POST)  &&
                req.getPathInfo().equalsIgnoreCase("/v1/orders/softdown")){

            String requestBody = CommonUtil.readRequestAsChars(req);
            log.info("get softdown eip order:{}.", requestBody);
            EipSoftDownOrder eipReciveOrder = JSON.parseObject(requestBody, EipSoftDownOrder.class);
            JSONObject result = bssApiService.onReciveSoftDownOrder(eipReciveOrder);

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
