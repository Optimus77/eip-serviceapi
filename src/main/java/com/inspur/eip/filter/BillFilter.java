package com.inspur.eip.filter;


import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebFilter
public class BillFilter implements Filter {


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
        if(method.equals("POST")  && req.getPathInfo().equals("/v2.0/eips")){
            log.info("get create eip order. ");
            //req.get
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        log.info("BillFilter destroy");

    }
}
