package com.inspur.eip.controller;

import com.inspur.eip.config.pool.http.clientImpl.HttpConnectionManager;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author: jiasirui
 * @date: 2018/9/27 15:34
 * @description:
 */

@RestController
public class HttpClientPoolController {

    private final static Log log = LogFactory.getLog(HttpClientPoolController.class);

    @Autowired
    HttpConnectionManager connManager;

    @GetMapping(value="/httppool")
    public void test(){
        log.info("pool executed");
        for(int i=0;i<100;i++){
            myRequestPool runnable = new myRequestPool();
            Thread thread = new Thread(runnable);
            thread.start();
        }
        log.info("wait pool executed");
        try {
            Thread.sleep(1000*30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("no pool executed");
        for(int i=0;i<100;i++){
            myRequestNoPool runnable = new myRequestNoPool();
            Thread thread = new Thread(runnable);
            thread.start();
        }
        log.info("wait no pool executed");
    }

    //has a pool test
    class myRequestPool implements  Runnable{
        @Override
        public void run() {
            log.info("PoolThread ==>"+Thread.currentThread().getName()+"==id"+Thread.currentThread().getId());
            String path="http://localhost:8088/bss/v2.0/eips?currentPage=1&limit=2";
            CloseableHttpClient httpClient=connManager.getHttpClient();
            HttpGet httpget = new HttpGet(path);
            String json=null;
            CloseableHttpResponse response=null;
            try {
                response = httpClient.execute(httpget);
                InputStream in=response.getEntity().getContent();
                json= IOUtils.toString(in);
                in.close();//将用完的连接释放，下次请求可以复用
            } catch (UnsupportedOperationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(response!=null){
                    try {
                        response.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            log.info(json);
        }
    }

    class myRequestNoPool implements  Runnable{

        @Override
        public void run() {
            log.info("NoPoolThread ==》"+Thread.currentThread().getName());
            String path="http://localhost:8088/bss/v2.0/eips?currentPage=1&limit=2";
            HttpGet httpget = new HttpGet(path);
            CloseableHttpResponse response=null;
            String json=null;
            try{
                CloseableHttpClient httpClient = HttpClients.createDefault();
                InputStream in=response.getEntity().getContent();
                json= IOUtils.toString(in);
                in.close();//将用完的连接释放，下次请求可以复用
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(response!=null){
                    try {
                        response.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


        }
    }
}
