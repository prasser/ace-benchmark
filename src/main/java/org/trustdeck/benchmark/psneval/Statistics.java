package org.trustdeck.benchmark.psneval;

import java.io.IOException;
import java.io.Writer;
import java.lang.StringIndexOutOfBoundsException;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicInteger;

import org.trustdeck.benchmark.http.HTTPAuthentication;
import org.trustdeck.benchmark.psnservice.PSNService;

public class Statistics {

    /** Config */
    private final Configuration config;

    /** Start time */
    private long                startTime;

    /** Stats */
    private final AtomicInteger creates = new AtomicInteger();
    /** Stats */
    private final AtomicInteger reads   = new AtomicInteger();
    /** Stats */
    private final AtomicInteger updates = new AtomicInteger();
    /** Stats */
    private final AtomicInteger deletes = new AtomicInteger();
    /** Stats */
    private final AtomicInteger pings 	= new AtomicInteger();

    /** Internal tracking */
    private long                lastTime    = 0;
    /** Internal tracking */
    private long                lastTimeDB  = 0;
    /** Internal tracking */
    private int                 lastCreates = 0;
    /** Internal tracking */
    private int                 lastReads   = 0;
    /** Internal tracking */
    private int                 lastUpdates = 0;
    /** Internal tracking */
    private int                 lastDeletes = 0;
    /** Internal tracking */
    private int                 lastPings = 0;
    /** Internal tracking */
    private int                 lastCRUDs = 0;
   
    /**
     * Creates a new instance.
     * @param configuration
     */
    public Statistics(Configuration configuration) {
        this.config = configuration;
    }
    
    /**
     * Performance tracking. Thread safe.
     */
    public void addCreate() {
        this.creates.incrementAndGet();
    }
    
    /**
     * Performance tracking. Thread safe.
     */
    public void addDelete() {
        this.deletes.incrementAndGet();
    }
    
    /**
     * Performance tracking. Thread safe.
     */
    public void addRead() {
        this.reads.incrementAndGet();
    }
    
    /**
     * Performance tracking. Thread safe.
     */
    public void addUpdate() {
        this.updates.incrementAndGet();
    }
    
    /**
     * Performance tracking. Thread safe.
     */
    public void addPing() {
        this.pings.incrementAndGet();
    }
    
    /**
     * @return the lastTime
     */
    public long getLastTime() {
        return lastTime;
    }
    
    /**
     * @return the lastTimeDB
     */
    public long getLastTimeDB() {
        return lastTimeDB;
    }
    
    /**
     * @return the startTime
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Reporting. NOT thread safe
     * @throws IOException 
     */
    public void report(Writer writer) throws IOException {
        
        // Collect
        long currentTime = System.currentTimeMillis();
        int currentCreates = creates.get();
        int currentReads = reads.get(); 
        int currentUpdates = updates.get();
        int currentDeletes = deletes.get();
        int currentPings = pings.get();
        int currentCRUDs = currentCreates + currentReads + currentUpdates + currentDeletes + currentPings;
        
        // Derive parameters
        double tpsCreate = (double)(currentCreates - lastCreates) / (double)(currentTime - lastTime) * 1000d;
        double tpsRead = (double)(currentReads - lastReads) / (double)(currentTime - lastTime) * 1000d;
        double tpsUpdate = (double)(currentUpdates - lastUpdates) / (double)(currentTime - lastTime) * 1000d;
        double tpsDelete = (double)(currentDeletes - lastDeletes) / (double)(currentTime - lastTime) * 1000d;
        double tpsPing = (double)(currentPings - lastPings) / (double)(currentTime - lastTime) * 1000d;
        double tpsOverall = (double)(currentCRUDs - lastCRUDs) / (double)(currentTime - lastTime) * 1000d;
        
        // Print header
        if (lastTime == 0) {
            
            // Print parameters
            StringBuilder builder = new StringBuilder();
            builder.append("Name").append(";");
            builder.append("Threads").append(";");
            builder.append("Initial size").append(";");
            builder.append("Time").append(";");
            builder.append("Num creates").append(";");
            builder.append("Num reads").append(";");
            builder.append("Num updates").append(";");
            builder.append("Num deletes").append(";");
            builder.append("TPS create").append(";");
            builder.append("TPS read").append(";");
            builder.append("TPS update").append(";");
            builder.append("TPS delete").append(";");
            builder.append("TPS ping").append(";");
            builder.append("TPS overall").append("\n");
            writer.write(builder.toString());
        }
        
        // Print parameters
        StringBuilder builder = new StringBuilder();
        builder.append(config.getName()).append(";");
        builder.append(config.getNumThreads()).append(";");
        builder.append(config.getInitialDBSize()).append(";");
        builder.append(String.valueOf((double)(currentTime - startTime)/1000d).replace('.', ',')).append(";");
        builder.append(currentCreates).append(";");
        builder.append(currentReads).append(";");
        builder.append(currentUpdates).append(";");
        builder.append(currentDeletes).append(";");
        builder.append((int)tpsCreate).append(";");
        builder.append((int)tpsRead).append(";");
        builder.append((int)tpsUpdate).append(";");
        builder.append((int)tpsDelete).append(";");
        builder.append((int)tpsPing).append(";");
        builder.append((int)tpsOverall).append("\n");
        writer.write(builder.toString());
        
        // Store
        this.lastTime = currentTime;
        this.lastCreates = currentCreates;
        this.lastReads = currentReads;
        this.lastUpdates = currentUpdates;
        this.lastDeletes = currentDeletes;
        this.lastPings = currentPings;
        this.lastCRUDs = currentCRUDs;
    }
    
    /**
    * Reporting DB storage. NOT thread safe
    * @throws IOException 
    */
   public void reportDBStorage(Writer writer, WorkProvider provider, HTTPAuthentication authentication, PSNService service) throws IOException, URISyntaxException {
	   
       // Collect
       long currentTime = System.currentTimeMillis();
       
       String d = provider.getDBStorageMetrics(authentication, service, "domain");
       String p = provider.getDBStorageMetrics(authentication, service, "pseudonym");
       String a = provider.getDBStorageMetrics(authentication, service, "auditevent");
       
       // Derive parameters
       long domainSize, domainRecordCount, domainDBSize, pseudonymSize, pseudonymRecordCount, pseudonymDBSize, auditeventSize, auditeventRecordCount, auditeventDBSize;
       double domainBytesPerRecord, pseudonymBytesPerRecord, auditeventBytesPerRecord;
       try {
    	   domainSize = Long.parseLong(d.substring(d.indexOf("tableSize: ") + "tableSize: ".length(), d.indexOf(", recordCount:")).trim());
	       domainRecordCount = Long.parseLong(d.substring(d.indexOf("recordCount: ") + "recordCount: ".length(), d.indexOf(", totalSize:")).trim());
	       domainDBSize = Long.parseLong(d.substring(d.indexOf("totalSize: ") + "totalSize: ".length(), d.length()).trim());
	       domainBytesPerRecord = (double) domainSize / (double) domainRecordCount;
	       
	       pseudonymSize = Long.parseLong(p.substring(p.indexOf("tableSize: ") + "tableSize: ".length(), p.indexOf(", recordCount:")).trim());
	       pseudonymRecordCount = Long.parseLong(p.substring(p.indexOf("recordCount: ") + "recordCount: ".length(), p.indexOf(", totalSize:")).trim());
	       pseudonymDBSize = Long.parseLong(p.substring(p.indexOf("totalSize: ") + "totalSize: ".length(), p.length()).trim());
	       pseudonymBytesPerRecord = (double) pseudonymSize / (double) pseudonymRecordCount;
	       
	       auditeventSize = Long.parseLong(a.substring(a.indexOf("tableSize: ") + "tableSize: ".length(), a.indexOf(", recordCount:")).trim());
	       auditeventRecordCount = Long.parseLong(a.substring(a.indexOf("recordCount: ") + "recordCount: ".length(), a.indexOf(", totalSize:")).trim());
	       auditeventDBSize = Long.parseLong(a.substring(a.indexOf("totalSize: ") + "totalSize: ".length(), a.length()).trim());
	       auditeventBytesPerRecord = (double) auditeventSize / (double) auditeventRecordCount;
       } catch (StringIndexOutOfBoundsException e) {
    	   // Abort
    	   return;
       }
       
       // Print header
       if (lastTimeDB == 0) {
           
           // Print parameters
           StringBuilder builder = new StringBuilder();
           builder.append("Time").append(";");
           builder.append("Table name").append(";");
           builder.append("Table size").append(";");
           builder.append("Number of records").append(";");
           builder.append("Bytes per record").append(";");
           builder.append("Database size").append("\n");
           writer.write(builder.toString());
       }
       
       // Print parameters
       StringBuilder builder = new StringBuilder();
       builder.append(String.valueOf((double)(currentTime - startTime)/1000d).replace('.', ',')).append(";");
       builder.append("domain").append(";");
       builder.append(domainSize).append(";");
       builder.append(domainRecordCount).append(";");
       builder.append(domainBytesPerRecord).append(";");
       builder.append(domainDBSize).append("\n");
       
       builder.append(String.valueOf((double)(currentTime - startTime)/1000d).replace('.', ',')).append(";");
       builder.append("pseudonym").append(";");
       builder.append(pseudonymSize).append(";");
       builder.append(pseudonymRecordCount).append(";");
       builder.append(pseudonymBytesPerRecord).append(";");
       builder.append(pseudonymDBSize).append("\n");
       
       builder.append(String.valueOf((double)(currentTime - startTime)/1000d).replace('.', ',')).append(";");
       builder.append("auditevent").append(";");
       builder.append(auditeventSize).append(";");
       builder.append(auditeventRecordCount).append(";");
       builder.append(auditeventBytesPerRecord).append(";");
       builder.append(auditeventDBSize).append("\n");
       
       writer.write(builder.toString());
       
       // Store
       this.lastTimeDB = currentTime;
   }

    /**
     * Start
     */
    public void start() {
        this.startTime = System.currentTimeMillis();
    }
}
