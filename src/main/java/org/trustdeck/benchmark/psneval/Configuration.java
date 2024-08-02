/* 
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

/**
 * @author Felix Wirth and Armin Müller
 *
 */
public class Configuration {
    
    /** Create rate */
    private final int                      createRate;
    /** Read rate */
    private final int                      readRate;
    /** Update rate */
    private final int                      updateRate;
    /** Delete rate */
    private final int                      deleteRate;
    /** Ping rate */
    private final int					   pingRate;
    /** Number of threads */
    private final int                      numThreads;
    /** Maximal time until evaluation stops in millis */
    private final int                      maxTime;
    /** Name */
    private final String                   name;
    /** Domain name */
    private final String                   domainName;
    /** Number of records created already at preparation stage */
    private final int                      initialDBSize;
    /** Interval of performance recording in millis */
    private final int                      reportingInterval;
    /** Interval of database storage check recording in millis */
    private final int                      reportingIntervalDBSpace;
    
    /**
     * Creates a new instance
     * @param createRate
     * @param readRate
     * @param upateRate
     * @param deleteRate
     * @param pingRate
     * @param numThreads
     * @param maxTime
     * @param name
     * @param domainName
     * @param initialDBSize
     * @param reportingInterval
     * @param reportingIntervalDBSpace
     */
    private Configuration(int createRate,
                          int readRate,
                          int upateRate,
                          int deleteRate,
                          int pingRate,
                          int numThreads,
                          int maxTime,
                          String name,
                          String domainName,
                          int initialDBSize,
                          int reportingInterval,
                          int reportingIntervalDBSpace) {
        this.readRate = readRate;
        this.createRate = createRate;
        this.updateRate = upateRate;
        this.deleteRate = deleteRate;
        this.pingRate = pingRate;
        this.numThreads = numThreads;
        this.maxTime = maxTime;
        this.name = name;
        this.domainName = domainName;
        this.initialDBSize = initialDBSize;
        this.reportingInterval = reportingInterval;
        this.reportingIntervalDBSpace = reportingIntervalDBSpace;
    }
    
    /**
     * @return the readRate
     */
    public int getReadRate() {
        return readRate;
    }

    /**
     * @return the createRate
     */
    public int getCreateRate() {
        return createRate;
    }

    /**
     * @return the updateRate
     */
    public int getUpdateRate() {
        return updateRate;
    }

    /**
     * @return the deleteRate
     */
    public int getDeleteRate() {
        return deleteRate;
    }

    /**
     * @return the pingRate
     */
    public int getPingRate() {
        return pingRate;
    }

    /**
     * @return the numThreads
     */
    public int getNumThreads() {
        return numThreads;
    }
    
    /**
     * @return the maxTime in millis until evaluation stops
     */
    public int getMaxTime() {
        return maxTime;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return the domainName
     */
    public String getDomainName() {
        return domainName;
    }
    
    /**
     * @return The number of records created before the evaluation starts
     */
    public int getInitialDBSize() {
        return initialDBSize;
    }

    /**
     * @return the reportingInterval
     */
    public int getReportingInterval() {
        return reportingInterval;
    }

    /**
     * @return the reportingIntervalDBSpace
     */
    public int getReportingIntervalDBSpace() {
        return reportingIntervalDBSpace;
    }
    
    /**
     * Return builder
     * @return
     */
    public static ConfigurationBuilder builder() {
        return new ConfigurationBuilder();
    }
    
    /** Builder for a configuration object
     * @author Felix Wirth
     *
     */
    public static class ConfigurationBuilder {
        
        /** Create rate */
        private int                      createRate;
        /** Read rate */
        private int                      readRate;
        /** Update rate */
        private int                      updateRate;
        /** Delete rate */
        private int                      deleteRate;
        /** Ping rate */
        private int						 pingRate;
        /** Number of threads */
        private int                      numThreads;
        /** Maximal time until evaluation stops in millis */
        private int                      maxTime;
        /** Name */
        private String                   name;
        /** Domain name */
        private String                   domainName;
        /** Number of records created already at preparation stage */
        private int                      initialDBSize;
        /** Interval of performance recording in millis */
        private int                      reportingInterval;
        /** Interval of database storage check recording in millis */
        private int                      reportingIntervalDBSpace;
        
        /**
         * Build the configuration
         * @return
         */
        public Configuration build() {
            // Checks
            if(createRate < 0 || readRate < 0 || updateRate < 0 || deleteRate < 0 || pingRate < 0 || numThreads < 0 || maxTime < 0 || initialDBSize < 0) {
                throw new IllegalStateException("All number values must be zero or positive!");
            }
            if(readRate + createRate + updateRate + deleteRate + pingRate != 100) {
                throw new IllegalStateException("All rates combined must add up to exactly one hundred!");
            }
            if(!((maxTime > 0) || (maxTime == 0))) {
                throw new IllegalStateException("Max time must be 0 and the other must be positive ");
            }
            if(name == null || domainName == null) {
                throw new IllegalStateException("Domain name and name must not be null!");
            }
            if(initialDBSize == 0 && (readRate > 0 || updateRate > 0 || deleteRate > 0)) {
                throw new IllegalStateException("If read, update or delete is set, the number of pre-created records must not be null.");
            }
            if(this.reportingInterval <= 0) {
                throw new IllegalStateException("Recorder interval must be greater than zero.");
            }
            if(this.reportingIntervalDBSpace <= 0) {
                throw new IllegalStateException("Database storage recorder interval must be greater than zero.");
            }
            
            // Create object
            return new Configuration(createRate, readRate, updateRate, deleteRate, pingRate, numThreads, maxTime, name, domainName, initialDBSize, reportingInterval, reportingIntervalDBSpace);
        }
        
        /**
         * @return the readRate
         */
        public int getReadRate() {
            return readRate;
        }
        /**
         * @param readRate the readRate to set - if provided please also configure a number of records to be created while preparing
         */
        public ConfigurationBuilder setReadRate(int readRate) {
            this.readRate = readRate;
            return this;
        }
        /**
         * @return the createRate
         */
        public int getCreateRate() {
            return createRate;
        }
        /**
         * @param createRate the createRate to set
         */
        public ConfigurationBuilder setCreateRate(int createRate) {
            this.createRate = createRate;
            return this;
        }
        /**
         * @return the updateRate
         */
        public int getUpdateRate() {
            return updateRate;
        }
        /**
         * @param updateRate the updateRate to set
         */
        public ConfigurationBuilder setUpdateRate(int updateRate) {
            this.updateRate = updateRate;
            return this;
        }
        /**
         * @return the deleteRate
         */
        public int getDeleteRate() {
            return deleteRate;
        }
        /**
         * @param deleteRate the deleteRate to set
         */
        public ConfigurationBuilder setDeleteRate(int deleteRate) {
            this.deleteRate = deleteRate;
            return this;
        }
        /**
         * @return the pingRate
         */
        public int getPingRate() {
            return deleteRate;
        }
        /**
         * @param pingRate the pingRate to set
         */
        public ConfigurationBuilder setPingRate(int pingRate) {
            this.pingRate = pingRate;
            return this;
        }
        /**
         * @return the numThreads
         */
        public int getNumThreads() {
            return numThreads;
        }
        /**
         * @param numThreads the numThreads to set
         */
        public ConfigurationBuilder setNumThreads(int numThreads) {
            this.numThreads = numThreads;
            return this;
        }
        
        /**
         * @return the maxTime until evaluation stops
         */
        public int getMaxTime() {
            return maxTime;
        }
    
        /**
         * @param maxTime the maxTime to set
         */
        public ConfigurationBuilder setMaxTime(int maxTime) {
            this.maxTime = maxTime;
            return this;
        }
    
        /**
         * @return the name
         */
        public String getName() {
            return name;
        }
    
        /**
         * @param name the name to set
         */
        public ConfigurationBuilder setName(String name) {
            this.name = name;
            return this;
        }
        
        /**
         * @return the domainName
         */
        public String getDomainName() {
            return domainName;
        }
    
        /**
         * @param domainName the domainName to set
         */
        public ConfigurationBuilder setDomainName(String domainName) {
            this.domainName = domainName;
            return this;
        }
        
        /**
         * @return The number of records created before the evaluation starts
         */
        public int getInitialDBSize() {
            return initialDBSize;
        }
        
        /**
         * @param initialDBSize The number of records created before the evaluation starts
         */
        public ConfigurationBuilder setInitialDBSize(int initialDBSize) {
            this.initialDBSize = initialDBSize;
            return this;
        }
        
        /**
         * @return the reportingInterval
         */
        public int getReportingInterval() {
            return reportingInterval;
        }
        
        /**
         * @param reportingInterval the recorderInterval to set
         * @return 
         */
        public ConfigurationBuilder setReportingInterval(int reportingInterval) {
            this.reportingInterval = reportingInterval;
            return this;
        }
        
        /**
         * @return the reportingIntervalDBSpace
         */
        public int getReportingIntervalDBSpace() {
            return reportingIntervalDBSpace;
        }
        
        /**
         * @param reportingIntervalDBSpace the recorderIntervalDBSpace to set
         * @return 
         */
        public ConfigurationBuilder setReportingIntervalDBSpace(int reportingIntervalDBSpace) {
            this.reportingIntervalDBSpace = reportingIntervalDBSpace;
            return this;
        }
    }
}
