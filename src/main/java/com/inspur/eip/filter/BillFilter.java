package com.inspur.eip.filter;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.ReciveOrder;
import com.inspur.eip.entity.OrderSoftDown;
import com.inspur.eip.service.BssApiService;
import com.inspur.eip.service.EipServiceImpl;
import com.inspur.eip.util.CommonUtil;
import com.inspur.eip.util.HsConstants;
import com.inspur.eip.util.ReturnStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

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
        HttpServletRequest req= (HttpServletRequest)servletRequest;
        HttpServletResponse response=(HttpServletResponse)servletResponse;
        String method =  req.getMethod();
        String orderUri = "/v1/orders";
        log.debug("requtst:{}, {}",method ,  req.getPathInfo());
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
        if(method.equalsIgnoreCase(HsConstants.POST)  &&
                req.getPathInfo().equalsIgnoreCase("/v1/orders/softdown")) {

            String requestBody = CommonUtil.readRequestAsChars(req);
            log.info("get softdown eip order:{}.", requestBody);
            OrderSoftDown eipReciveOrder = JSON.parseObject(requestBody, OrderSoftDown.class);
            ResponseEntity responseEntity = bssApiService.onReciveSoftDownOrder(eipReciveOrder);

            response.setStatus(HttpStatus.SC_OK);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(responseEntity.toString());
        }else if(method.equalsIgnoreCase(HsConstants.POST)  && req.getPathInfo().equalsIgnoreCase("/v1/sbws/softdown")) {
            String requestBody = CommonUtil.readRequestAsChars(req);
            log.info("get softDelete sbw order:{}.", requestBody);
            OrderSoftDown softDown = JSON.parseObject(requestBody, OrderSoftDown.class);
            ResponseEntity responseEntity = bssApiService.stopOrSoftDeleteSbw(softDown);

            response.setStatus(HttpStatus.SC_OK);
            response.setContentType(HsConstants.APPLICATION_JSON);
            response.getWriter().write(responseEntity.toString());
        }else {
            filterChain.doFilter(servletRequest, servletResponse);
        }

    }

    @Override
    public void destroy() {
        log.info("BillFilter destroy");

    }

}
