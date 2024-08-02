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
package org.trustdeck.benchmark.psnservice;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * A class for records
 * @author Felix Wirth
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "idType", "validFrom", "validityTime" })
public class Record {

    @JsonProperty("id")
    private String              id;
    @JsonProperty("idType")
    private String              idType;
    @JsonProperty("validFrom")
    private String              validFrom;
    @JsonProperty("validityTime")
    private String              validityTime;
    
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public Record() {
    }
    
    /**
     * Creates new instance
     * @param id
     * @param idType
     */
    public Record(String id, String idType) {
        this.id = id;
        this.idType = idType;
    }
    
    /**
     *
     * @param validityTime
     * @param idType
     * @param id
     * @param validFrom
     */
    public Record(String id, String idType, String validFrom, String validityTime) {
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

    public Record withId(String id) {
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

    public Record withIdType(String idType) {
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

    public Record withValidFrom(String validFrom) {
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

    public Record withValidityTime(String validityTime) {
        this.validityTime = validityTime;
        return this;
    }
}
