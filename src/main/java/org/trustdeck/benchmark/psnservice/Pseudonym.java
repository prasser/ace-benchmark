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

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Represents a pseudonym record.
 * 
 * @author Felix Wirth
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "idType", "validFrom", "validityTime" })
public class Pseudonym {

	/** The pseudonym's identifier. */
    @JsonProperty("id")
    private String id;

	/** The pseudonym's identifier's type. */
    @JsonProperty("idType")
    private String idType;

	/** The pseudonym's start date of validity. */
    @JsonProperty("validFrom")
    private String validFrom;

	/** The pseudonym's validity time period. */
    @JsonProperty("validityTime")
    private String validityTime;

	/** Additional properties. */
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization.
     *
     */
    public Pseudonym() {
    	// Emtpy by design
    }
    
    /**
     * Creates a new instance.
     * 
     * @param id
     * @param idType
     */
    public Pseudonym(String id, String idType) {
        this.id = id;
        this.idType = idType;
    }
    
    /**
     * Creates a new instance.
     *
     * @param validityTime
     * @param idType
     * @param id
     * @param validFrom
     */
    public Pseudonym(String id, String idType, String validFrom, String validityTime) {
        super();
        this.id = id;
        this.idType = idType;
        this.validFrom = validFrom;
        this.validityTime = validityTime;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    public Pseudonym withId(String id) {
        this.id = id;
        return this;
    }

    @JsonProperty("idType")
    public String getIdType() {
        return idType;
    }

    @JsonProperty("idType")
    public void setIdType(String idType) {
        this.idType = idType;
    }

    public Pseudonym withIdType(String idType) {
        this.idType = idType;
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

    public Pseudonym withValidFrom(String validFrom) {
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

    public Pseudonym withValidityTime(String validityTime) {
        this.validityTime = validityTime;
        return this;
    }
}
