package org.trustdeck.benchmark.psnservice;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.trustdeck.benchmark.http.HTTPException;
import org.trustdeck.benchmark.http.HTTPRequest;
import org.trustdeck.benchmark.http.HTTPRequest.HTTPMediaType;
import org.trustdeck.benchmark.http.HTTPRequest.HTTPRequestType;
import org.trustdeck.benchmark.psnservice.PSNService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PSNService {
    
    /** Mapper */
    private static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    
    /** Service */
    private final URI service;
    
    /**
     * Creates a new instance
     * @param service
     */
    public PSNService(URI service) {
        this.service = service;
    }
    
    /**
     * Create domain
     * @param token
     * @param domain
     * @throws URISyntaxException, HTTPException 
     * @throws JsonProcessingException 
     */
    public void createDomain(String token, Domain domain) throws URISyntaxException, HTTPException, JsonProcessingException {
        
        HTTPRequest request = new HTTPRequest(service,
                                              "/domain",
                                              HTTPRequestType.POST,
                                              token,
                                              MAPPER.writer().writeValueAsString(domain),
                                              HTTPMediaType.APPLICATION_JSON);
        
        // Execute
        request.execute();
    }
    
    /**
     * Read domain
     * 
     * @param token
     * @param domain
     * @throws URISyntaxException
     * @throws HTTPException
     */
    public void readDomain(String token, Domain domain) throws URISyntaxException, HTTPException {
        
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("name", domain.getName());
        
        HTTPRequest request = new HTTPRequest(service,
                                              "/domain",
                                              HTTPRequestType.GET,
                                              token,
                                              parameters);
        
        // Execute
        request.execute();
    }
    
    /**
     * Update domain
     * 
     * @param token
     * @param domain
     * @throws URISyntaxException
     * @throws HTTPException
     * @throws JsonProcessingException
     */
    public void updateDomain(String token, Domain domain) throws URISyntaxException, HTTPException, JsonProcessingException {
        
        HTTPRequest request = new HTTPRequest(service,
                                              "/domain",
                                              HTTPRequestType.PUT,
                                              token,
                                              MAPPER.writer().writeValueAsString(domain),
                                              HTTPMediaType.APPLICATION_JSON);
        
        // Execute
        request.execute();
    }
    
    /**
     * Delete domain
     * @param token
     * @throws URISyntaxException, HTTPException 
     */
    public void deleteDomain(String token, Domain domain) throws URISyntaxException, HTTPException {
        
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("name", domain.getName());
        
        HTTPRequest request = new HTTPRequest(service,
                                              "/domain",
                                              HTTPRequestType.DELETE,
                                              token,
                                              parameters);
        
        // Execute
        request.execute();
    }
    
    /**
     * Clear all tables and vacuum them
     * @param token
     * @throws URISyntaxException, HTTPException 
     */
    public void clearTables(String token) throws URISyntaxException, HTTPException {
        HTTPRequest request = new HTTPRequest(service, "/table/pseudonym", HTTPRequestType.DELETE, token, null);
        request.execute();
        request = new HTTPRequest(service, "/table/domain", HTTPRequestType.DELETE, token, null);
        request.execute();
        request = new HTTPRequest(service, "/table/auditevent", HTTPRequestType.DELETE, token, null);
        request.execute();
    }
    
    /**
     * Get table storage usage
     * @param token
     * @throws URISyntaxException, HTTPException 
     */
    public String getStorage(String token, String tableName) throws URISyntaxException, HTTPException {
        HTTPRequest request = new HTTPRequest(service, "/table/"+tableName+"/storage", HTTPRequestType.GET, token, null);
        return request.execute();
    }

    /**
     * Create pseudonym
     * @param token
     * @param domain
     * @param pseudonym
     * @throws URISyntaxException, HTTPException, JsonProcessingException
     */
    public void createPseudonym(String token, Domain domain, Record pseudonym) throws URISyntaxException, HTTPException, JsonProcessingException {
    	HTTPRequest request = new HTTPRequest(service, 
    			"/domains/" + domain.getName() + "/pseudonym", 
    			HTTPRequestType.POST, 
    			token, 
    			MAPPER.writer().writeValueAsString(pseudonym), 
    			HTTPMediaType.APPLICATION_JSON);
    	
    	// Execute
    	request.execute();
    }
    
    /**
     * Read pseudonym
     * @param token
     * @param domain
     * @param pseudonym
     * @throws URISyntaxException
     * @throws HTTPException
     * @throws JsonProcessingException
     */
    public void readPseudonym(String token, Domain domain, Record pseudonym) throws URISyntaxException, HTTPException, JsonProcessingException {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("id", pseudonym.getId());
        parameters.put("idType", pseudonym.getIdType());
        
        HTTPRequest request = new HTTPRequest(service, 
                "/domains/" + domain.getName() + "/pseudonym", 
                HTTPRequestType.GET, 
                token,
                parameters);
        
        // Execute
        request.execute();
    }

    /**
     * Update pseudonym
     * @param token
     * @param domain
     * @param pseudonym
     * @throws URISyntaxException
     * @throws HTTPException
     * @throws JsonProcessingException
     */
    public void updatePseudonym(String token, Domain domain, Record pseudonym) throws URISyntaxException, HTTPException, JsonProcessingException {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("id", pseudonym.getId());
        parameters.put("idType", pseudonym.getIdType());
        
        HTTPRequest request = new HTTPRequest(service, 
                "/domains/" + domain.getName() + "/pseudonym", 
                HTTPRequestType.PUT, 
                token, 
                MAPPER.writer().writeValueAsString(pseudonym), 
                HTTPMediaType.APPLICATION_JSON,
                parameters);
        
        // Execute
        request.execute();
    }
    
    /**
     * Delete pseudonym
     * 
     * @param token
     * @param domain
     * @param pseudonym
     * @throws URISyntaxException
     * @throws HTTPException
     * @throws JsonProcessingException
     */
    public void deletePseudonym(String token, Domain domain, Record pseudonym) throws URISyntaxException, HTTPException, JsonProcessingException {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("id", pseudonym.getId());
        parameters.put("idType", pseudonym.getIdType());
        
        HTTPRequest request = new HTTPRequest(service, 
                "/domains/" + domain.getName() + "/pseudonym", 
                HTTPRequestType.DELETE, 
                token,
                parameters);
        
        // Execute
        request.execute();
    }
    
    /**
     * Ping
     * 
     * @param token
     * @throws URISyntaxException
     * @throws HTTPException
     * @throws JsonProcessingException
     */
    public void ping(String token) throws URISyntaxException, HTTPException, JsonProcessingException {
    	HTTPRequest request = new HTTPRequest(service, "/ping", HTTPRequestType.GET, token, null);
        
        // Execute
        request.execute();
    }
}
