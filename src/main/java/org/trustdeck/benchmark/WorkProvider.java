/*
 * ACE-Benchmark Driver
 * Copyright 2024 Armin M�ller and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trustdeck.benchmark;

import org.trustdeck.benchmark.connector.Connector;
import org.trustdeck.benchmark.connector.ConnectorException;

/**
 * Class that provides the work for the worker threads.
 * 
 * @author Armin M�ller, Felix N. Wirth, and Fabian Prasser
 */
public class WorkProvider {
    
    /** The benchmark driver's configuration object. */
    private Configuration config;
    
    /** The work distribution according to the scenario. */
    private WorkDistribution distribution;
    
    /** The set of identifiers used to create/access pseudonym-objects. */
    private Identifiers identifiers;
    
    /** The statistics object. */
    private Statistics statistics;
    
    /** The connector. */
    private Connector connector;
    
    /**
     * Creates a new instance.
     * 
     * @param config
     * @param identifiers
     * @param statistics
     */
    public WorkProvider(Configuration config, Identifiers identifiers, Statistics statistics) {
    	
        // Store config
        this.config = config;
        this.identifiers = identifiers;
        this.statistics = statistics;
        
        // Distribution of work
        this.distribution = new WorkDistribution(config.getCreateRate(),
                                                 config.getReadRate(),
                                                 config.getUpdateRate(),
                                                 config.getDeleteRate(),
                                                 config.getPingRate());
        
    }
    
    /**
     * Prepare the benchmark run.
     * 
     * @param connector
     * @throws ConnectorException
     */
    public void prepare(Connector connector) throws ConnectorException {
        this.connector = connector;
        this.connector.prepare();
        for (int i = 0; i < config.getInitialDBSize(); i++) {
            this.connector.createPseudonym(identifiers.create());
        }
    }
    
    /**
     * Get storage metrics.
     * 
     * @param storageIdentifier
     * @throws ConnectorException
     */
    public String getDBStorageMetrics(String storageIdentifier) throws ConnectorException {
        return this.connector.getStorageConsumption(storageIdentifier);
        
    }
    
    /**
     * Returns the next work item.
     * 
     * @return the work
     */
    public Runnable getWork() {
        
        // Get the template according to the defined distribution
        switch(distribution.sample()) {
	        case CREATE:
	            return new Runnable() {
	                @Override
	                public void run() {
	                    try {
                            connector.createPseudonym(identifiers.create());
                        } catch (ConnectorException e) {
                            throw new RuntimeException(e);
                        }
	                    statistics.addCreate();
	                }
	            };
	        case READ:
	            return new Runnable() {
	                @Override
	                public void run() {
	                    try {
                            connector.readPseudonym(identifiers.read());
                        } catch (ConnectorException e) {
                            throw new RuntimeException(e);
                        }
	                    statistics.addRead();
	                }
	            };
	        case UPDATE:
	            return new Runnable() {
	                @Override
	                public void run() {
	                    try {
                            connector.updatePseudonym(identifiers.read());
                        } catch (ConnectorException e) {
                            throw new RuntimeException(e);
                        }
	                    statistics.addUpdate();
	                }
	            };
	        case DELETE:
	            return new Runnable() {
	                @Override
	                public void run() {
	                    try {
                            connector.deletePseudonym(identifiers.read());
                        } catch (ConnectorException e) {
                            throw new RuntimeException(e);
                        }
	                    statistics.addDelete();
	                }
	            };
	        case PING:
	            return new Runnable() {
	                @Override
	                public void run() {
	                    try {
                            connector.ping();
                        } catch (ConnectorException e) {
                            throw new RuntimeException(e);
                        }
                        statistics.addPing();
	                }
	            };
	    };
        
        // Sanity check
        throw new IllegalStateException("No work can be provided.");
    }
}
