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

package org.opennms.karaf.licencepub;

import java.util.Map;

import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadata;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceSpecification;

public interface LicencePublisher {

	/**
	 * Adds a new licence specification to the licence publisher. 
	 * Looks for the productId in the licence specification and adds an entry
	 * in the licence table under that productId. Replaces any previous licence entry
	 * @param licenceSpec LicenceSpecification to be added to licence publisher
	 */
	public void addLicenceSpec(LicenceSpecification licenceSpec);
	
	/**
	 * removes the entry for productId from the licenceSpecMap
	 * @param productId
	 */
	public boolean removeLicenceSpec(String productId );

	/**
	 * @param productId
	 * @return  the LicenceSpecification stored for productId 
	 * returns null if no LicenceSpecification found for productId 
	 */
	public LicenceSpecification getLicenceSpec(String productId);

	/**
	 * @return a copy of the map of the LicenceSpecifications ordered by productId
	 */
	public Map<String, LicenceSpecification> getLicenceSpecMap();
	
	/**
	 * deletes all values of the licenceSpecMap
	 */
	public void deleteLicenceSpecifications();
	
	/**
	 * Creates an encoded String instance of a licence from the LicenceSecification 
	 * corresponding to the productId in the supplied createLicenceMetadata
	 * throws IllegalArgumentException if the correspondingLicenceSecification cannot be found
	 * or the names of options or licencee are different from the specification
	 * @Param createLicenceMetadata this should be created from a copy of the LicenceMetadata in the LicenceSpecfication
	 * i.e. it must contain the productId and the options must correspond to the
	 * options in the LicenceSpecification
	 */
	public String createLicenceInstanceStr(LicenceMetadata licenceMetadata);
	
	/**
	 * Creates an encoded String instance of a licence from the supplied licenceMetadata in xml form
	 * using same criteria as in createLicenceInstance(licenceMetadata)
	 * 
	 */
	public String createLicenceInstanceStr(String licenceMetadataXml);

	
}
