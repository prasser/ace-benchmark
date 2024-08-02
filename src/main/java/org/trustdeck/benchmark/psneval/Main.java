package org.trustdeck.benchmark.psneval;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.trustdeck.benchmark.http.HTTPAuthentication;
import org.trustdeck.benchmark.http.HTTPException;
import org.trustdeck.benchmark.psnservice.PSNService;

public class Main {
	
    static String URI = "http://ace.server.com/api/pseudonymization"; // TODO: Change to appropriate URL
    static String CLIENT_ID = "ace"; // TODO: Change if necessary
    static String CLIENT_SECRET = ""; // TODO: Insert client secret provided by keycloak
    static String USERNAME = "user"; // TODO: Change to the benchmark user name
    static String PASSWORD = "password"; // TODO: Change to the user's password

    public static void main(String[] args) throws URISyntaxException, HTTPException, IOException {
        
    	// Config for benchmarking
        int INITIAL_DB_SIZE = 2000;
        int MAX_TIME = 3600 * 1000; // in ms
        String DOMAIN_NAME = "TestStudy"; // Change to a domain name that the benchmark tool can access 
        int REPORTING_INTERVAL = 1000;
        int REPORTING_INTERVAL_DB_SPACE = 30000;
        int NUM_THREADS = 16;
        int NUMBER_OF_REPETITIONS = 5;
        
        // Create configs
        List<Configuration> configs = new ArrayList<Configuration>();
        // Example: Ping
		for (int i = 0; i < NUMBER_OF_REPETITIONS; i++) {
			configs.add(Configuration.builder()
		             .setCreateRate(0)
		             .setReadRate(0)
		             .setUpdateRate(0)
		             .setDeleteRate(0)
		             .setPingRate(100)
		             .setInitialDBSize(0)
		             .setMaxTime(MAX_TIME)
		             .setName("ping-" + NUM_THREADS + "-threads")
		             .setNumThreads(NUM_THREADS)
		             .setDomainName(DOMAIN_NAME)
		             .setReportingInterval(REPORTING_INTERVAL)
		             .setReportingIntervalDBSpace(REPORTING_INTERVAL_DB_SPACE)
		             .build());
		}
	
        // Example: Mostly Write
		for (int i = 0; i < NUMBER_OF_REPETITIONS; i++) {
			configs.add(Configuration.builder()
                     .setCreateRate(75)
                     .setReadRate(23)
                     .setUpdateRate(1)
                     .setDeleteRate(1)
                     .setInitialDBSize(INITIAL_DB_SIZE)
                     .setMaxTime(MAX_TIME)
                     .setName("mostly-write-" + NUM_THREADS + "-threads")
                     .setNumThreads(NUM_THREADS)
                     .setDomainName(DOMAIN_NAME)
                     .setReportingInterval(REPORTING_INTERVAL)
		             .setReportingIntervalDBSpace(REPORTING_INTERVAL_DB_SPACE)
                     .build());
		}

        // Example: Mostly Read
		for (int i = 0; i < NUMBER_OF_REPETITIONS; i++) {
			configs.add(Configuration.builder()
					.setCreateRate(23)
					.setReadRate(75)
					.setUpdateRate(1)
					.setDeleteRate(1)
					.setInitialDBSize(INITIAL_DB_SIZE)
					.setMaxTime(MAX_TIME)
					.setName("mostly-read-" + NUM_THREADS + "-threads")
					.setNumThreads(NUM_THREADS)
					.setDomainName(DOMAIN_NAME)
					.setReportingInterval(REPORTING_INTERVAL)
		            .setReportingIntervalDBSpace(REPORTING_INTERVAL_DB_SPACE)
					.build());
		}

        // Example: Read Write
		for (int i = 0; i < NUMBER_OF_REPETITIONS; i++) {
			configs.add(Configuration.builder()
                    .setCreateRate(49)
                    .setReadRate(49)
                    .setUpdateRate(1)
                    .setDeleteRate(1)
                    .setInitialDBSize(INITIAL_DB_SIZE)
                    .setMaxTime(MAX_TIME)
                    .setName("read-write-" + NUM_THREADS + "-threads")
                    .setNumThreads(NUM_THREADS)
                    .setDomainName(DOMAIN_NAME)
                    .setReportingInterval(REPORTING_INTERVAL)
		            .setReportingIntervalDBSpace(REPORTING_INTERVAL_DB_SPACE)
                    .build());
		}

        // Execute
        for (Configuration config : configs) {
            execute(config);
        }
    }
    
    /**
     * Executes a config
     * @param config
     * @throws IOException
     * @throws URISyntaxException
     */
    private static final void execute(Configuration config) throws IOException, URISyntaxException {
        // Some logging
        System.out.println("Executing configuration: " + config.getName());
        System.out.println(" - Preparing service: ");
        
        // Authenticate
        System.out.print("\r - Preparing service: creating authentication object");
        HTTPAuthentication authentication = new HTTPAuthentication()
                .setClientId(CLIENT_ID)
                .setClientSecret(CLIENT_SECRET)
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
        BufferedWriter dbWriter = new BufferedWriter(new FileWriter(new File(config.getName() + "_DB_STORAGE-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss")) + ".csv")));
        
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
            if (System.currentTimeMillis() - statistics.getLastTimeDB() >= config.getReportingIntervalDBSpace()) {
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
        dbWriter.close();

        // Some logging
        System.out.println(" - Done");
    }
}
