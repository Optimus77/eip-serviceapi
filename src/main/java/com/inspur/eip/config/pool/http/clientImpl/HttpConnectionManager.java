package com.inspur.eip.config.pool.http.clientImpl;

import com.inspur.eip.config.pool.httpclient.HttpClient;
import com.inspur.eip.config.pool.httpclient.factory.AbstractClientFactory;
import com.inspur.eip.config.pool.httpclient.factory.jersey.grizzlyImpl.GrizzlyJerseyClientFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author: jiasirui
 * @date: 2018/9/27 09:42
 * @description:
 */
@Component
public class HttpConnectionManager {

    private final static Log log = LogFactory.getLog(HttpConnectionManager.class);

    PoolingHttpClientConnectionManager cm=null;

    @PostConstruct
    public void init() {
        log.info("****************HttpConnectionManager init**************");
        LayeredConnectionSocketFactory sslsf = null;
        try {
            sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory())
                .build();
        cm =new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(20);


        try{
            //test();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @PreDestroy
    public void destroy(){
        log.info("****************HttpConnectionManager destroy**************");
    }


    public CloseableHttpClient getHttpClient() {
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
        /*CloseableHttpClient httpClient = HttpClients.createDefault();//如果不采用连接池就是这种方式获取连接*/
        return httpClient;
    }

    private void test() throws InterruptedException {

        //AbstractClientFactory factory = new DefaultJerseyClientFactory(10, 1000, 1000);
        AbstractClientFactory factory = new GrizzlyJerseyClientFactory(10, 1000, 1000);

        HttpClient client = new HttpClient(factory);
        String stringEnitiy = new String("string entity");
        MultivaluedMap<String, Object> param = new MultivaluedHashMap<>();
        param.add("name", "testtest");
        while(true){
            List<Future<String>> list = new ArrayList(20);
            for(int i = 0 ; i <= 20 ; i++){
                try {
                    if(i%2==0){
                        list.add(client.postAsync("http://localhost:8080", "/hello", null, param, Entity.entity(stringEnitiy, MediaType.TEXT_PLAIN_TYPE)));
                    }else{
                        list.add(client.postAsync("http://localhost:8088", "/hello", null, param, Entity.entity(stringEnitiy, MediaType.TEXT_PLAIN_TYPE)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            list.forEach(future ->{
                try {
                    future.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
            list.clear();
            try {
                Thread.sleep(1000 * 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }
}
