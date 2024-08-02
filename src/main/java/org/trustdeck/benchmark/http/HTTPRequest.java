package org.trustdeck.benchmark.http;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class HTTPRequest {
    
    /**
     * Type of request
     * 
     * @author Felix Wirth
     */
    public enum HTTPRequestType {
                                 GET,
                                 POST,
                                 PUT,
                                 DELETE
    };
    
    /**
     * Type of media
     * @author Fabian Prasser
     */
    public enum HTTPMediaType {
        TEXT_PLAIN,
        APPLICATION_JSON
    };
    
    /** Parameter*/
    private final URI                 server;
    /** Parameter*/
    private final String              path;
    /** Parameter*/
    private final HTTPRequestType     requestType;
    /** Parameter*/
    private final String              authToken;
    /** Parameter*/
    private final String              body;
    /** Parameter*/
    private final HTTPMediaType       bodyMediaType;
    /** Parameter*/
    private final Map<String, String> parameters;
    
    /**
     * Creates a new instance
     * @param server
     * @param path
     * @param requestType
     * @param authToken
     * @param parameters
     */
    public HTTPRequest(URI server,
                       String path,
                       HTTPRequestType requestType,
                       String authToken,
                       Map<String, String> parameters) {
        this(server, path, requestType, authToken, null, null, parameters);
    }

    /**
     * Creates a new instance
     * @param server
     * @param path
     * @param requestType
     * @param authToken
     * @param body
     * @param bodyMediaType
     */
    public HTTPRequest(URI server,
                       String path,
                       HTTPRequestType requestType,
                       String authToken,
                       String body,
                       HTTPMediaType bodyMediaType) {
        
        this.server = server;
        this.path = path;
        this.requestType = requestType;
        this.authToken = authToken;
        this.body = body;
        this.bodyMediaType = bodyMediaType != null ? bodyMediaType : 
                              (requestType == HTTPRequestType.POST || requestType == HTTPRequestType.PUT ? HTTPMediaType.APPLICATION_JSON : HTTPMediaType.TEXT_PLAIN);
        this.parameters = null;
    }
    
    /**
     * Creates a new instance
     * @param server
     * @param path
     * @param requestType
     * @param authToken
     * @param body
     * @param bodyMediaType
     * @param parameters
     */
    public HTTPRequest(URI server,
                       String path,
                       HTTPRequestType requestType,
                       String authToken,
                       String body,
                       HTTPMediaType bodyMediaType,
                       Map<String, String> parameters) {
        
        this.server = server;
        this.path = path;
        this.requestType = requestType;
        this.authToken = authToken;
        this.body = body;
        this.bodyMediaType =  bodyMediaType != null ? bodyMediaType
                : requestType == HTTPRequestType.POST || requestType == HTTPRequestType.PUT ? 
                        HTTPMediaType.APPLICATION_JSON : HTTPMediaType.TEXT_PLAIN;
        this.parameters = parameters;
    }
    
    /**
     * Execute request
     * @return
     */
    public String execute() {
        
        // Create target
        // newClient might be expensive? Use one client?
        WebTarget target = ClientBuilder.newClient().target(server).path(path);
        if (parameters != null && !parameters.isEmpty()) {
            for (Entry<String, String> parameter : parameters.entrySet()) {
                target = target.queryParam(parameter.getKey(), parameter.getValue());
            }
        }
        
        // Build request
        Builder builder = target.request();
        builder.header("Authorization", String.format("Bearer %s", authToken));
        
        // Handle media type
        String type = null;
        switch (bodyMediaType) {
        case APPLICATION_JSON:
            type = MediaType.APPLICATION_JSON;
            break;
        case TEXT_PLAIN:
            type = MediaType.TEXT_PLAIN;
            break;
        default:
            throw new IllegalStateException("Unknown media type");
        }
        
        // Execute request
        Response response = null;
        String method = "";
        switch (requestType) {
        case GET:
        	method = "GET";
            response = builder.get(Response.class);
            break;
        case POST:
        	method = "POST";
            if (body == null || type == null) {
                throw new IllegalArgumentException("Body and media type must not be null");
            }
            response = builder.post(Entity.entity(body, type));
            break;
        case PUT:
        	method = "PUT";
            if (body == null || type == null) {
                throw new IllegalArgumentException("Body and media type must not be null");
            }
            response = builder.put(Entity.entity(body, type));
            break;
        case DELETE:
        	method = "DELETE";
            response = builder.delete(Response.class);
            break;
        default:
            throw new IllegalStateException("Unknown request type");
        }
        
        // Done
        return response.readEntity(String.class);
    }
}
