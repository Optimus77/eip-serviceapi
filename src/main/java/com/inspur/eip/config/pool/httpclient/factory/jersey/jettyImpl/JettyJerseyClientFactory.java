package com.inspur.eip.config.pool.httpclient.factory.jersey.jettyImpl;

import com.inspur.eip.config.pool.httpclient.factory.jersey.DefaultJerseyClientFactory;
import org.glassfish.jersey.client.spi.ConnectorProvider;
import org.glassfish.jersey.jetty.connector.JettyConnectorProvider;

/**
 * jetty��jersey��ʵ��
 */
public class JettyJerseyClientFactory extends DefaultJerseyClientFactory {

    public JettyJerseyClientFactory(int aSynHttpThreadCount, int connectionTimeout, int readTimeout) {
        super(aSynHttpThreadCount, connectionTimeout, readTimeout);
    }

    @Override
    protected ConnectorProvider newProvider() {
        return new JettyConnectorProvider();
    }

}
