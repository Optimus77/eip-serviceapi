package com.inspur.eip.config.pool.httpclient;

import com.inspur.eip.config.pool.httpclient.factory.AbstractClientFactory;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 *   ����JAX-RS 2.1��׼Э�飬����Jerseyʵ��
 *   ֧��ͬ��Http�����Լ��첽http����
 *   ֧��KeepAlive�Լ����Ӹ���
 *   Api���ûص���ʽ������չ�м�ǿ����չ�ԡ�
 *
 *   http client restful api : ���ڷ������󣬲����ؽ������ͨ������������ClientFactory������Client���󣬲�ͨ��RequestHandler���û�����Clientʵ������Ľ���
 */
public class HttpClient {

    private AbstractClientFactory factory;

    public HttpClient(AbstractClientFactory factory) {
        this.factory = factory;
    }

    /**
     * api��չ�ӿ�
     * @param requestHandler
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T request(RequestHandler<T> requestHandler) throws Exception {
        return factory.request(requestHandler);
    }

    /**
     * ����Client Builder ����
     * @param client
     * @param target
     * @param path
     * @param headers
     * @param params
     * @return
     */
    public static Invocation.Builder build(Client client, String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params) {

        if(target == null)
            throw new NullPointerException("target is null");

        WebTarget webTarget = client.target(target);
        if(path != null)
            webTarget = webTarget.path(path);

        if(params != null) {
            Iterator<Map.Entry<String, List<Object>>> it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, List<Object>> entry = it.next();
                webTarget = webTarget.queryParam(entry.getKey(), entry.getValue().toArray(new Object[entry.getValue().size()]));
            }
        }

        Invocation.Builder builder = webTarget.request();
        if(headers != null)
            builder = builder.headers(headers);
        return builder;
    }

    public <T> T get(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params, Class<T> resultClass) throws Exception {

        return request((Client client) -> {
            Invocation.Builder builder = build(client, target, path, headers, params);
            return builder.get(resultClass);
        });
    }

    public String get(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params) throws Exception {
        return get(target, path, headers, params, String.class);
    }

    public <T> Future<T> getAsync(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params, Class<T> resultClass) throws Exception {
        return request((Client client) -> {
            Invocation.Builder builder = build(client, target, path, headers, params);
            return builder.async().get(resultClass);
        });
    }

    public <T> Future<T> getAsync(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params, InvocationCallback<T> callback) throws Exception {
        return request((Client client) -> {
            Invocation.Builder builder = build(client, target, path, headers, params);
            return builder.async().get(callback);
        });
    }

    public Future<String> getAsync(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params) throws Exception {
        return getAsync(target, path, headers, params, String.class);
    }

    public <T> T post(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params, Entity<?> entity, Class<T> resultClass) throws Exception {
        return request(client -> {
            Invocation.Builder builder = build(client, target, path, headers, params);
            return builder.post(entity, resultClass);
        });
    }

    public String post(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params, Entity<?> entity) throws Exception {
        return post(target, path, headers, params, entity, String.class);
    }


    public <T> Future<T> postAsync(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params, Entity<?> entity, Class<T> resultClass) throws Exception {
        return request(client -> {
            Invocation.Builder builder = build(client, target, path, headers, params);
            return builder.async().post(entity, resultClass);
        });
    }

    public <T> Future<T> postAsync(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params, Entity<?> entity, InvocationCallback<T> callback) throws Exception {
        return request(client -> {
            Invocation.Builder builder = build(client, target, path, headers, params);
            return builder.async().post(entity, callback);
        });
    }

    public Future<String> postAsync(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params, Entity<?> entity) throws Exception {
        return postAsync(target, path, headers, params, entity, String.class);
    }

    public <T> T put(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params, Entity<?> entity, Class<T> resultClass) throws Exception {
        return request(client -> {
            Invocation.Builder builder = build(client, target, path, headers, params);
            return builder.put(entity, resultClass);
        });
    }

    public String put(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params, Entity<?> entity) throws Exception {
        return put(target, path, headers, params, entity, String.class);
    }

    public <T> Future<T> putAsync(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params, Entity<?> entity, Class<T> resultClass) throws Exception {
        return request(client -> {
            Invocation.Builder builder = build(client, target, path, headers, params);
            return builder.async().put(entity, resultClass);
        });
    }

    public <T> Future<T> putAsync(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params, Entity<?> entity, InvocationCallback<T> callback) throws Exception {
        return request(client -> {
            Invocation.Builder builder = build(client, target, path, headers, params);
            return builder.async().put(entity, callback);
        });
    }

    public Future<String> putAsync(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params, Entity<?> entity) throws Exception {
        return putAsync(target, path, headers, params, entity, String.class);
    }


}
