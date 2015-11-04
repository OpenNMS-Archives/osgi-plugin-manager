/*
 * Copyright 2014 OpenNMS Group Inc., Entimoss ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opennms.karaf.licencemgr;

import java.util.Map;

import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadata;

public interface LicenceService {

	/**
	 * adds a licence to the licence service. Throws RuntimeError if licence string incorrectly formatted
	 * licence string must have correct checksum and readable LicenceMetadata
	 * the licence is added for productId corresponding to the productId in the LicenceMetadata
	 * previous entries for that productId are overwritten
	 * @param licence
	 * @return the licence metadata contained in the added licence
	 */
	public LicenceMetadata addLicence(String licence);

	/**
	 * removes any licence corresponding to productId. 
	 * @param productId
	 * @return true if entry corresponding to productId was found and removed. false if entry not found.
	 */
	public boolean removeLicence(String productId);

	/**
	 * gets the licence corresponding to the productId
	 * @param productId
	 * @return licence string if found or null if no entry for productId found
	 */
	public String getLicence(String productId);

	/**
	 * returns a map of all installed licences with 
	 * key=productId 
	 * and value = licence string
	 * @return
	 */
	public Map<String, String> getLicenceMap();
	
	/**
	 * returns a map of all installed licences which have systemId in systemId's list
	 * or where maxSizeSystemIds=0 (i.e. authenticates for any systemId)
	 * returns an empty map if no licences match.
	 * key=productId 
	 * and value = licence string
	 * @return
	 */
	public Map<String, String> getLicenceMapForSystemId(String systemId);
	
	/**
	 * deletes all licence entries
	 */
	public void deleteLicences();

	/**
	 * 
	 * @return the systemId for this system
	 */
	public String getSystemId();

	/**
	 * sets the systemId. Note that the checksum for the systemId must be correct
	 * throws RuntimeError if systemId not correct
	 * @param systemId
	 */
	public void setSystemId(String systemId);
	
	/**
	 * Makes a new systemId with a random identifier and checksum
	 * sets the systemId to the newly generated value
	 * @return new systemId
	 */
	public String makeSystemInstance();
	
	/**
	 * Generates a checksum for the supplied string
	 * Adds a CRC32 encoded string to supplied string separated by '-' resulting in string of form 
	 * form 'valueString'-'CRC32 in Hex'
	 * @param string - string to have checkum added
	 * @return original string plus checksum in form 'valueString'-'CRC32 in Hex'
	 */
	public String checksumForString(String string);
	
	
	/**
	 * Used by the Licence Authenticator to add a productId to the authenticatedProductId list.
	 * Throws an exception if the productId does not have a corresponding licence installed.
	 * (this is an error as it can't have been authenticated without a licence)
	 * @param productId
	 */
	public void addAuthenticatedProductId(String productId);
	
	/**
	 * Used by the Licence Authenticator to remove a productId from the authenticatedProductId list.
	 * If the productId is not in the list, nothing happens.
	 * @param productId
	 */
	public void removeAuthenticatedProductId(String productId);
	
	/**
	 * Checks if the productId is in the authenticatedProductId list.
	 * NOTE this service is used for the UI not for licence verification.
	 * This service should NOT be used to enable a product to start up but only as an indication that
	 * a licence authenticator has started up. A licence Authenticator failing should be used to stop a product from starting.
	 * 
	 * @param productId
	 * @return true if the productId is in the list, false if not in the list
	 */
	public boolean isAuthenticatedProductId(String productId);

}
