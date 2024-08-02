package org.trustdeck.benchmark.psneval;

import java.net.URISyntaxException;

import org.trustdeck.benchmark.http.HTTPAuthentication;
import org.trustdeck.benchmark.http.HTTPException;
import org.trustdeck.benchmark.psnservice.Domain;
import org.trustdeck.benchmark.psnservice.PSNService;
import org.trustdeck.benchmark.psnservice.Record;

import com.fasterxml.jackson.core.JsonProcessingException;

public class WorkProvider {
    
    /** Default object properties */
    private static final String   DEFAULT_DOMAIN_PREFIX        = "TST";
    /** Default object properties */
    private static final String   DEFAULT_ID_TYPE              = "ID";
    /** Default valid time for domain */
    private static final String   DEFAULT_DOMAIN_VALID_FROM    = "2000-01-01T18:00:00";
    /** Default valid time for pseudonym */
    protected static final String DEFAULT_PSEUDONYM_VALID_FROM = "2001-01-01T18:00:00";

    /** Config */
    private Configuration         config;
    /** Distribution */
    private WorkDistribution      distribution;
    /** Identifiers */
    private Identifiers           identifiers;
    /** Statistics */
    private Statistics            statistics;
    
    /**
     * Creates a new instance
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
     * Perform preparation
     * @param authentication
     * @param service
     * @throws URISyntaxException 
     * @throws HTTPException 
     * @throws JsonProcessingException 
     */
    public void prepare(HTTPAuthentication authentication, PSNService service) throws HTTPException, URISyntaxException, JsonProcessingException {
        // Domain
        Domain domain = new Domain(config.getDomainName(), DEFAULT_DOMAIN_PREFIX);
        domain.setValidFrom(DEFAULT_DOMAIN_VALID_FROM);
        
        // Authenticate
        String token = authentication.authenticate();

        // Clear tables
        try {
            service.clearTables(token);
        } catch(HTTPException e) {
            // Ignore
        }
        
        // Refresh access token (since the deletion can take a while) and create the domain
        token = authentication.refreshToken();
        service.createDomain(token, domain);
        
        // Refresh token and create initial pseudonyms
        token = authentication.refreshToken();
        for (int i = 0; i < config.getInitialDBSize(); i++) {
            
            // Create pseudonym for next identifier
            Record record = new Record(identifiers.create(), DEFAULT_ID_TYPE);
            service.createPseudonym(token, domain, record);
        }
    }
    
    /**
     * GET DB storage metrics
     * @param authentication
     * @param service
     * @param tableName
     * @throws URISyntaxException 
     * @throws HTTPException 
     * @throws JsonProcessingException 
     */
    public String getDBStorageMetrics(HTTPAuthentication authentication, PSNService service, String tableName) throws HTTPException, URISyntaxException, JsonProcessingException {
        // Authenticate
        String token = authentication.authenticate();
        
        // Gather storage
        String response = "";
        try {
            response = service.getStorage(token, tableName);
        } catch(HTTPException e) {
            // Ignore
        }
        
        return response;
    }
    
    /**
     * Returns the next work item
     * @return
     */
    public Work getWork() {
        
        // Get the template according to defined distribution
        switch(distribution.sample()) {
        case CREATE:
            return new Work() {
                @Override
                public void perform(PSNService service, String token) {
                    try {
                        service.createPseudonym(token,
                                                new Domain(config.getDomainName(), DEFAULT_DOMAIN_PREFIX),
                                                new Record(identifiers.create(), DEFAULT_ID_TYPE));
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
                                              new Record(identifiers.read(), DEFAULT_ID_TYPE));
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
                                                new Record(identifiers.read(), DEFAULT_ID_TYPE).withValidFrom(DEFAULT_PSEUDONYM_VALID_FROM));
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
                                                new Record(identifiers.read(), DEFAULT_ID_TYPE));
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
        throw new IllegalStateException("No work can be provided");
    }
}
