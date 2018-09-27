package com.inspur.eip.config.pool.httpclient.factory;

import javax.ws.rs.client.Client;

/**
 * ����������Client�ĳ��󹤳�
 * Client������JAX-RS 2.1�ı�׼���󣨽ӿڣ����������̶���ʵ��Client�ķ�ʽ����ͬ,
 * Ϊ�������滻����Ҫ�������ϲ���˵��ʹ��ClientFactory��Client���ʺϡ�
 */
public interface ClientFactory {
    /**
     * Client ����
     * @return
     */
    Client createClient();

    /**
     * Client ����
     * @param client
     */
    void destory(Client client);

}
