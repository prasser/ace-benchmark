package org.trustdeck.benchmark.http;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.token.TokenManager;

/**
 * Information needed for authentication
 * @author Fabian Prasser and Armin Müller
 */
public class HTTPAuthentication {
    
    /** Parameter*/
    protected String username;
    /** Parameter*/
    protected String password;
    /** Parameter*/
    protected String clientId;
    /** Parameter*/
    protected String clientSecret;
    /** Parameter*/
    private TokenManager tokenmanager;
    
    /**
     * Creates a new instance
     */
    public HTTPAuthentication() {
        // Empty by design
    }

    /**
     * @param username the username to set
     */
    public HTTPAuthentication setUsername(String username) {
        this.username = username;
        return this;
    }
    /**
     * @param password the password to set
     */
    public HTTPAuthentication setPassword(String password) {
        this.password = password;
        return this;
    }
    /**
     * @param clientId the clientId to set
     */
    public HTTPAuthentication setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }
    /**
     * @param clientSecret the clientSecret to set
     */
    public HTTPAuthentication setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }
    
    /**
     * Returns an authentication token
     * @return
     */
    public String authenticate() throws HTTPException {
        // Check
        if (username == null || password == null || clientId == null || clientSecret == null) {
            throw new NullPointerException("All parameters must not be null!");
        }
        
        String kcURI = "http://keycloak.server.com"; // TODO: Change to your keycloak servers address
        String kcRealmName = "development"; // TODO: Change to the appropriate keycloak realm
        
        Keycloak instance = Keycloak.getInstance(kcURI, kcRealmName, username, password, clientId, clientSecret);
        tokenmanager = instance.tokenManager();
        String accessToken = tokenmanager.getAccessTokenString();

    	return accessToken;
    }
    
    /**
     * Refreshes an authentication token
     * 
     * @return
     */
    public String refreshToken() {
    	return tokenmanager.refreshToken().getToken();
    }
}
