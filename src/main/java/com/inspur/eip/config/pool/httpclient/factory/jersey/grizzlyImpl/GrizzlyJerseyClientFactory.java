package com.inspur.eip.config.pool.httpclient.factory.jersey.grizzlyImpl;

import com.inspur.eip.config.pool.httpclient.factory.jersey.DefaultJerseyClientFactory;
import org.glassfish.jersey.client.spi.ConnectorProvider;
import org.glassfish.jersey.grizzly.connector.GrizzlyConnectorProvider;

/**
 * grizzly��jersey��ʵ��
 */
public class GrizzlyJerseyClientFactory extends DefaultJerseyClientFactory{

    public GrizzlyJerseyClientFactory(int aSynHttpThreadCount, int connectionTimeout, int readTimeout) {
        super(aSynHttpThreadCount, connectionTimeout, readTimeout);
    }
    @Override
    protected ConnectorProvider newProvider() {
        return new GrizzlyConnectorProvider();
    }
}
