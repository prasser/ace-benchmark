package org.trustdeck.benchmark.psneval;

import java.util.Random;

public class WorkDistribution {
    
    /** Random */
    private static final Random RANDOM  = new Random();
    
    /**
     * Types of work
     * @author Fabian Prasser and Armin Müller
     */
    public static enum WorkType {
        CREATE,
        READ,
        UPDATE,
        DELETE,
        PING
    }
    
    /** Number of ops */
    private final int c;
    /** Number of ops */
    private final int cr;
    /** Number of ops */
    private final int cru;
    /** Number of ops */
    private final int crud;
    /** Number of ops */
    private final int crudp;
    
    /**
     * Returns a new instance
     * @param creates
     * @param reads
     * @param updates
     * @param deletes
     * @param pings
     */
    public WorkDistribution(int creates, int reads, int updates, int deletes, int pings) {
        this.c = creates;
        this.cr = creates + reads;
        this.cru = creates + reads + updates;
        this.crud = creates + reads + updates + deletes;
        this.crudp = creates + reads + updates + deletes + pings;
    }
    
    /**
     * Returns a type of work sampled according to the given distribution
     * @return
     */
    public WorkType sample() {
        int number = RANDOM.nextInt(crudp);
        if (number < c) {
            return WorkType.CREATE;
        } else if (number < cr) {
            return WorkType.READ;
        } else if (number < cru) {
            return WorkType.UPDATE;
        } else if (number < crud) {
            return WorkType.DELETE;
        } else {
        	return WorkType.PING;
        }
    }
}
