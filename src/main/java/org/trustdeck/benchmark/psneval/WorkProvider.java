/*
 * ACE-Benchmark Driver
 * Copyright 2024 Armin Müller and contributors.
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

package org.trustdeck.benchmark.psneval;

import java.net.URISyntaxException;

import org.trustdeck.benchmark.http.HTTPAuthentication;
import org.trustdeck.benchmark.http.HTTPException;
import org.trustdeck.benchmark.psnservice.Domain;
import org.trustdeck.benchmark.psnservice.PSNService;
import org.trustdeck.benchmark.psnservice.Pseudonym;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Class that provides the work for the worker threads.
 * 
 * @author Armin Müller, Felix N. Wirth, and Fabian Prasser
 */
public class WorkProvider {
    
    /** Default domain prefix. */
    private static final String DEFAULT_DOMAIN_PREFIX = "TST";
    
    /** Default idType. */
    private static final String DEFAULT_ID_TYPE = "ID";
    
    /** Default start time for the domain's validity period. */
    private static final String DEFAULT_DOMAIN_VALID_FROM = "2000-01-01T18:00:00";
    
    /** Default start time for the pseudonym's validity period. */
    protected static final String DEFAULT_PSEUDONYM_VALID_FROM = "2001-01-01T18:00:00";

    /** The benchmark driver's configuration object. */
    private Configuration config;
    
    /** The work distribution according to the scenario. */
    private WorkDistribution distribution;
    
    /** The set of identifiers used to create/access pseudonym-objects. */
    private Identifiers identifiers;
    
    /** The statistics object. */
    private Statistics statistics;
    
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
     * @param authentication
     * @param service
     * @throws URISyntaxException 
     * @throws HTTPException 
     * @throws JsonProcessingException 
     */
    public void prepare(HTTPAuthentication authentication, PSNService service) throws HTTPException, URISyntaxException, JsonProcessingException {
        // Create a new domain object
        Domain domain = new Domain(config.getDomainName(), DEFAULT_DOMAIN_PREFIX);
        domain.setValidFrom(DEFAULT_DOMAIN_VALID_FROM);
        
        // Retrieve an access token
        String token = authentication.authenticate();

        // Remove old data from ACE
        try {
            service.clearTables(token);
        } catch(HTTPException e) {
            // Ignore
        }
        
        // Refresh access token (since the old-data-removal can take a while) and create the domain
        long lastAuthenticated = System.currentTimeMillis();
        token = authentication.refreshToken();
        service.createDomain(token, domain);
        
        // Create initial pseudonyms
        for (int i = 0; i < config.getInitialDBSize(); i++) {
        	// Creating a lot of data points can take a while. Re-authenticate if necessary.
        	if (System.currentTimeMillis() - lastAuthenticated > 290000) {
        		// Standard token validity time is 300 seconds
        		lastAuthenticated = System.currentTimeMillis();
        		token = authentication.refreshToken();
        	}
            
            // Create pseudonym for next identifier
            Pseudonym record = new Pseudonym(identifiers.create(), DEFAULT_ID_TYPE);
            service.createPseudonym(token, domain, record);
        }
    }
    
    /**
     * GET database storage metrics.
     * 
     * @param authentication
     * @param service
     * @param tableName
     * @throws URISyntaxException 
     * @throws HTTPException 
     * @throws JsonProcessingException 
     */
    public String getDBStorageMetrics(HTTPAuthentication authentication, PSNService service, String tableName) throws HTTPException, URISyntaxException, JsonProcessingException {
        // Retrieve access token
        String token = authentication.authenticate();
        
        // Gather storage information
        String response = "";
        try {
            response = service.getStorage(token, tableName);
        } catch(HTTPException e) {
            // Ignore
        }
        
        return response;
    }
    
    /**
     * Returns the next work item.
     * 
     * @return the work
     */
    public Work getWork() {
        
        // Get the template according to the defined distribution
        switch(distribution.sample()) {
	        case CREATE:
	            return new Work() {
	                @Override
	                public void perform(PSNService service, String token) {
	                    try {
	                        service.createPseudonym(token,
	                                                new Domain(config.getDomainName(), DEFAULT_DOMAIN_PREFIX),
	                                                new Pseudonym(identifiers.create(), DEFAULT_ID_TYPE));
	                        statistics.addCreate();
	                    } catch (Exception e) {
	                        throw new RuntimeException(e);
	                    }
	                }
	            };
	        case READ:
	            return new Work() {
	                @Override
	                public void perform(PSNService service, String token) {
	                    try {
	                        service.readPseudonym(token,
	                                              new Domain(config.getDomainName(), DEFAULT_DOMAIN_PREFIX),
	                                              new Pseudonym(identifiers.read(), DEFAULT_ID_TYPE));
	                        statistics.addRead();
	                    } catch (Exception e) {
	                        // It is ok if the pseudonym does not exist
	                        if (!(e instanceof HTTPException && ((HTTPException) e).getStatusCode() == 404)) {
	                            throw new RuntimeException(e);
	                        } else {
	                            statistics.addRead();
	                        }
	                    }
	                }
	            };
	        case UPDATE:
	            return new Work() {
	                @Override
	                public void perform(PSNService service, String token) {
	                    try {
	                        service.updatePseudonym(token,
	                                                new Domain(config.getDomainName(), DEFAULT_DOMAIN_PREFIX),
	                                                new Pseudonym(identifiers.read(), DEFAULT_ID_TYPE).withValidFrom(DEFAULT_PSEUDONYM_VALID_FROM));
	                        statistics.addUpdate();
	                    } catch (Exception e) {
	                        // It is ok if the pseudonym does not exist
	                        if (!(e instanceof HTTPException && ((HTTPException) e).getStatusCode() == 404)) {
	                            throw new RuntimeException(e);
	                        } else {
	                            statistics.addUpdate();
	                        }
	                    }
	                }
	            };
	        case DELETE:
	            return new Work() {
	                @Override
	                public void perform(PSNService service, String token) {
	                    try {
	                        service.deletePseudonym(token,
	                                                new Domain(config.getDomainName(), DEFAULT_DOMAIN_PREFIX),
	                                                new Pseudonym(identifiers.read(), DEFAULT_ID_TYPE));
	                        statistics.addDelete();
	                    } catch (Exception e) {
	                        // It is ok if the pseudonym does not exist
	                        if (!(e instanceof HTTPException && ((HTTPException) e).getStatusCode() == 404)) {
	                            throw new RuntimeException(e);
	                        } else {
	                            statistics.addDelete();
	                        }
	                    }
	                }
	            };
	        case PING:
	            return new Work() {
	                @Override
	                public void perform(PSNService service, String token) {
	                    try {
	                        service.ping(token);
	                        statistics.addPing();
	                    } catch (Exception e) {
	                        // It is ok if it does not exist
	                        if (!(e instanceof HTTPException && ((HTTPException) e).getStatusCode() == 404)) {
	                            throw new RuntimeException(e);
	                        } else {
	                            statistics.addPing();
	                        }
	                    }
	                }
	            };
	    };
        
        // Sanity check
        throw new IllegalStateException("No work can be provided.");
    }
}
