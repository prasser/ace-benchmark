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

package org.trustdeck.benchmark.psnservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Represents a domain.
 * 
 * @author Felix Wirth
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "name", "prefix", "validFrom", "validityTime", "description" })
public class Domain {
	
	/** The domain's . */
    @JsonProperty("name")
    private String name;
    
    /** The domain's prefix. */
    @JsonProperty("prefix")
    private String prefix;
    
    /** The domain's valid from date-time. */
    @JsonProperty("validFrom")
    private String validFrom;
    
    /** The domain's validity period. */
    @JsonProperty("validityTime")
    private String validityTime;
    
    /** The domain's description. */
    @JsonProperty("description")
    private String description;

    /**
     * Creates a new instance.
     */
    public Domain() {
    	// Empty by design
    }

    /**
     * Creates a new domain object.
     *
     * @param validityTime
     * @param prefix
     * @param name
     * @param description
     * @param validFrom
     */
    public Domain(String name,
                  String prefix,
                  String validFrom,
                  String validityTime,
                  String description) {
        super();
        this.name = name;
        this.prefix = prefix;
        this.validFrom = validFrom;
        this.validityTime = validityTime;
        this.description = description;
    }

    /**
     * Creates a new instance.
     * 
     * @param name
     * @param prefix
     */
    public Domain(String name, String prefix) {
        this.name = name;
        this.prefix = prefix;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public Domain withName(String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("prefix")
    public String getPrefix() {
        return prefix;
    }

    @JsonProperty("prefix")
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Domain withPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    @JsonProperty("validFrom")
    public String getValidFrom() {
        return validFrom;
    }

    @JsonProperty("validFrom")
    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public Domain withValidFrom(String validFrom) {
        this.validFrom = validFrom;
        return this;
    }

    @JsonProperty("validityTime")
    public String getValidityTime() {
        return validityTime;
    }

    @JsonProperty("validityTime")
    public void setValidityTime(String validityTime) {
        this.validityTime = validityTime;
    }

    public Domain withValidityTime(String validityTime) {
        this.validityTime = validityTime;
        return this;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    public Domain withDescription(String description) {
        this.description = description;
        return this;
    }
}
