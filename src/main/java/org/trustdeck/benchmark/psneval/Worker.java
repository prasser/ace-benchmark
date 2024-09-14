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

import org.trustdeck.benchmark.http.HTTPAuthentication;
import org.trustdeck.benchmark.psnservice.PSNService;

/**
 * This class represents a thread that performs dedicated work.
 * 
 * @author Armin Müller, Felix N. Wirth, and Fabian Prasser
 */
public class Worker extends Thread {
    
    /** The service object. */
    private PSNService service;
    
    /** The access token. */
    private String token;
    
    /** Authentication handler. */
    private HTTPAuthentication authentication;
    
    /** Work provider. */
    private WorkProvider provider;
    
    /** Time point when the benchmark tool last authenticated the user. */
    private long lastAuthenticated;
    
    /**
     * Creates a new instance.
     * 
     * @param authentication
     * @param service
     * @param provider
     */
    public Worker(HTTPAuthentication authentication, PSNService service, WorkProvider provider) {
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
        	if (System.currentTimeMillis() - lastAuthenticated > 290000) {
        		lastAuthenticated = System.currentTimeMillis();
        		this.token = authentication.refreshToken();
        	}
            
            // Next work package
            Work work = this.provider.getWork();
            
            // Perform work
            work.perform(service, token);
            
            // See if it's time to stop
            if (Thread.interrupted()) {
                return;
            }
        }
    }
}
