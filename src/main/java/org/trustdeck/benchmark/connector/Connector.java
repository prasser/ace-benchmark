/*
 * ACE-Benchmark Driver
 * Copyright 2024 Armin M�ller and contributors.
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
package org.trustdeck.benchmark.connector;

/**
 * Connector interface
 * @author Fabian Prasser
 */
public interface Connector {

    /** Prepare benchmark*/
    public void prepare() throws ConnectorException;
    
    /** Create pseudonym*/
    public void createPseudonym(String id) throws ConnectorException;
    
    /** Retrieve storage consumption*/
    public String getStorageConsumption(String storageID) throws ConnectorException;
    
    /** Read pseudonym*/
    public void readPseudonym(String string) throws ConnectorException;
    
    /** Update pseudonym*/
    public void updatePseudonym(String string) throws ConnectorException;
    
    /** Delete pseudonym*/
    public void deletePseudonym(String string) throws ConnectorException;
    
    /** Ping the service*/
    public void ping() throws ConnectorException;
}
