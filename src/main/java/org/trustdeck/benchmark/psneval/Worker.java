package org.trustdeck.benchmark.psneval;

import org.trustdeck.benchmark.http.HTTPAuthentication;
import org.trustdeck.benchmark.psnservice.PSNService;

public class Worker extends Thread {
    
    /** Parameter*/
    private PSNService         service;
    /** Parameter*/
    private String             token;
    /** Parameter*/
    private HTTPAuthentication authentication;
    /** Parameter*/
    private WorkProvider       provider;
    /** Parameter*/
    private long			   lastAuthenticated;
    
    /**
     * Creates a new instance
     * @param authentication
     * @param service
     * @param provider
     */
    public Worker(HTTPAuthentication authentication,
                  PSNService service,
                  WorkProvider provider) {
        this.authentication = authentication;
        this.token = this.authentication.authenticate();
        this.lastAuthenticated = System.currentTimeMillis();
        this.provider = provider;
        this.service = service;
        this.setDaemon(true);
    }
    
    @Override
    public void run() {
        
        // Do forever
        while (true) {
        	// Periodically renew the access token (standard validity time is 300 seconds)
        	if (System.currentTimeMillis() - lastAuthenticated > 285000) {
        		lastAuthenticated = System.currentTimeMillis();
        		this.token = authentication.refreshToken();
        	}
            
            // Next work package
            Work work = this.provider.getWork();
            
            // Perform work
            work.perform(service, token);
            
            // See if time to stop
            if (Thread.interrupted()) {
                return;
            }
        }
    }
}
