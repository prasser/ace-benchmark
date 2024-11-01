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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.trustdeck.benchmark.http.HTTPAuthentication;
import org.trustdeck.benchmark.http.HTTPException;
import org.trustdeck.benchmark.psnservice.PSNService;
import org.yaml.snakeyaml.Yaml;

/**
 * Main class of the benchmark driver.
 * 
 * @author Armin Müller, Felix N. Wirth, and Fabian Prasser
 */
public class Main {

    public static void main(String[] args) throws URISyntaxException, HTTPException, IOException {
    	
    	// Load configuration from file
        Yaml yaml = new Yaml();
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("config.yaml");
        Map<String, Object> yamlConfig = yaml.load(inputStream);

        // Extract the tool configuration from the loaded configuration file
        @SuppressWarnings("unchecked")
		Map<String, String> toolConfig = (Map<String, String>) yamlConfig.get("tool");
        final String URI = toolConfig.get("uri");
        final String CLIENT_ID = toolConfig.get("clientId");
        final String CLIENT_SECRET = toolConfig.get("clientSecret");
        final String KEYCLOAK_AUTH_URI = toolConfig.get("keycloakAuthUri");
        final String KEYCLOAK_REALM_NAME = toolConfig.get("keycloakRealmName");
        final String USERNAME = toolConfig.get("username");
        final String PASSWORD = toolConfig.get("password");

        // Extract the benchmark configuration from the loaded configuration file
        @SuppressWarnings("unchecked")
		Map<String, Object> benchmarkConfig = (Map<String, Object>) yamlConfig.get("benchmark");
        final int INITIAL_DB_SIZE = (int) benchmarkConfig.get("initialDbSize");
        final int MAX_TIME = (int) benchmarkConfig.get("maxTime");
        final String DOMAIN_NAME = (String) benchmarkConfig.get("domainName");
        final int REPORTING_INTERVAL = (int) benchmarkConfig.get("reportingInterval");
        final boolean REPORT_DB_SPACE = (boolean) benchmarkConfig.get("reportDbSpace");
        final int REPORTING_INTERVAL_DB_SPACE = (int) benchmarkConfig.get("reportingIntervalDbSpace");
        final int NUM_THREADS = (int) benchmarkConfig.get("numThreads");
        final int NUMBER_OF_REPETITIONS = (int) benchmarkConfig.get("numberOfRepetitions");

        // Extract the scenario configurations from the loaded configuration file
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> scenarios = (List<Map<String, Object>>) benchmarkConfig.get("scenarios");

        // Create configs
        List<Configuration> configs = new ArrayList<>();
        for (Map<String, Object> scenario : scenarios) {
            String name = (String) scenario.get("name");
            int createRate = scenario.containsKey("createRate") ? (int) scenario.get("createRate") : 0;
            int readRate = scenario.containsKey("readRate") ? (int) scenario.get("readRate") : 0;
            int updateRate = scenario.containsKey("updateRate") ? (int) scenario.get("updateRate") : 0;
            int deleteRate = scenario.containsKey("deleteRate") ? (int) scenario.get("deleteRate") : 0;
            int pingRate = scenario.containsKey("pingRate") ? (int) scenario.get("pingRate") : 0;

            for (int i = 0; i < NUMBER_OF_REPETITIONS; i++) {
                configs.add(Configuration.builder()
                        .setCreateRate(createRate)
                        .setReadRate(readRate)
                        .setUpdateRate(updateRate)
                        .setDeleteRate(deleteRate)
                        .setPingRate(pingRate)
                        .setInitialDBSize(INITIAL_DB_SIZE)
                        .setMaxTime(MAX_TIME)
                        .setName(name + "-" + NUM_THREADS + "-threads")
                        .setNumThreads(NUM_THREADS)
                        .setDomainName(DOMAIN_NAME)
                        .setReportingInterval(REPORTING_INTERVAL)
                        .setReportingIntervalDBSpace(REPORTING_INTERVAL_DB_SPACE)
                        .build());
            }
        }

        // Execute
        for (Configuration config : configs) {
            execute(config, CLIENT_ID, CLIENT_SECRET, KEYCLOAK_AUTH_URI, KEYCLOAK_REALM_NAME, USERNAME, PASSWORD, URI, REPORT_DB_SPACE);
        }
    }
    
    /**
     * Executes a configuration.
     * 
     * @param config the configuration object that should be used to run the benchmark
     * @throws IOException
     * @throws URISyntaxException
     */
    private static final void execute(Configuration config, String CLIENT_ID, String CLIENT_SECRET, 
    		String KEYCLOAK_AUTH_URI, String KEYCLOAK_REALM_NAME, String USERNAME, String PASSWORD, 
    		String URI, boolean REPORT_DB_SPACE) throws IOException, URISyntaxException {
        // Some logging
        System.out.println("Executing configuration: " + config.getName());
        System.out.println(" - Preparing service: ");
        
        // Authenticate
        System.out.print("\r - Preparing service: creating authentication object");
        HTTPAuthentication authentication = new HTTPAuthentication()
                .setClientId(CLIENT_ID)
                .setClientSecret(CLIENT_SECRET)
                .setKeycloakAuthenticationURI(KEYCLOAK_AUTH_URI)
                .setKeycloakRealmName(KEYCLOAK_REALM_NAME)
                .setUsername(USERNAME)
                .setPassword(PASSWORD);
        System.out.println("\r - Preparing service: creating authentication object\t[DONE]");
        
        // Service
        System.out.print("\r - Preparing service: creating service object                      ");
        PSNService service = new PSNService(new URI(URI));
        System.out.println("\r - Preparing service: creating service object\t\t[DONE]");
        
        // Identifiers
        System.out.print("\r - Preparing service: creating identifiers                      ");
        Identifiers identifiers = new Identifiers();
        System.out.println("\r - Preparing service: creating identifiers\t\t[DONE]");

        // Statistics
        System.out.print("\r - Preparing service: creating statistics                      ");
        Statistics statistics = new Statistics(config);
        System.out.println("\r - Preparing service: creating statistics\t\t[DONE]");
        
        // Provider
        System.out.print("\r - Preparing service: creating work provider                      ");
        WorkProvider provider = new WorkProvider(config, identifiers, statistics);
        System.out.println("\r - Preparing service: creating work provider\t\t[DONE]");
        
        // Prepare
        System.out.print("\r - Preparing service: purge database and re-initialize            ");
        provider.prepare(authentication, service);
        System.out.println("\r - Preparing service: purge database and re-initialize\t[DONE]");
        
        // Some logging
        System.out.println("\r - Preparing service: Done                                              ");
        
        // Start workers
        statistics.start();
        for (int i = 0; i < config.getNumThreads(); i++) {
            new Worker(authentication, service, provider).start();
        }
        
        // Some logging
        System.out.println(" - Number of workers launched: " + config.getNumThreads());
        
        // Files to write to
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(config.getName() + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss")) + ".csv")));
        BufferedWriter dbWriter = REPORT_DB_SPACE ? new BufferedWriter(new FileWriter(new File(config.getName() + "_DB_STORAGE-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss")) + ".csv"))) : null;
        
        // Event and logging loop
        while (true) {
            
            // Reporting
            if (System.currentTimeMillis() - statistics.getLastTime() >= config.getReportingInterval()) {
                statistics.report(writer);
                writer.flush();
                
                // Print progress
                System.out.print("\r - Progress: " + (double)((int)(((double)(System.currentTimeMillis() - statistics.getStartTime())/(double)config.getMaxTime()) * 1000d))/10d + " %");
            }
            
            // Reporting DB storage size
            if (REPORT_DB_SPACE && System.currentTimeMillis() - statistics.getLastTimeDB() >= config.getReportingIntervalDBSpace()) {
                statistics.reportDBStorage(dbWriter, provider, authentication, service);
                dbWriter.flush();
            }
            
            // End of experiment
            if (System.currentTimeMillis() - statistics.getStartTime() >= config.getMaxTime()) {
            	System.out.println("\r - Progress: 100 % ");
                break;
            }
            
            // Sleep
            try {
                Thread.sleep(100); // 0.1 second
            } catch (InterruptedException e) {
                break;
            }
            
            // Interrupted?
            if (Thread.interrupted()) {
                break;
            }
        }
        
        // Close writer
        writer.close();
        if (REPORT_DB_SPACE) {
        	dbWriter.close();
        }
        
        // Some logging
        System.out.println(" - Done");
    }
}
