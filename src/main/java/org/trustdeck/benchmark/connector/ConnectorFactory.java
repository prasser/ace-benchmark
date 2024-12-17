package org.trustdeck.benchmark.connector;

public interface ConnectorFactory {

    public Connector create() throws ConnectorException;

    public void shutdown();
}
