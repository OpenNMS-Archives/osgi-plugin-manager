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

package org.opennms.karaf.licencemgr.rest.client;


import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadata;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadataList;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceSpecList;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceSpecification;

public interface LicencePublisherClient {

	/**
	 * /addlicencespec (POST LicenceSpecification)
	 * 
	 * e.g. http://localhost:8181/licencemgr/rest/licence-pub/addlicencespec
	 * 
	 * Adds a new licence specification to the licence publisher. 
	 * Looks for the productId in the licence specification and adds an 
	 * entry in the licence table under that productId. 
	 * Replaces any previous licence entry

	 * @param licence Specification
	 * @throws Exception Throws error if licence spec incorrectly formatted. 
	 * 
	 */
	public void addLicenceSpec(LicenceSpecification licenceSpec) throws Exception;
	
	
	/**
	 * /removelicencespec (GET productId)
	 * 
	 * e.g. http://localhost:8181/licencemgr/rest/licence-pub/removelicencespec?productId=
	 * 
	 * removes the entry for productId from the licenceSpecMap
	 * 
	 * @param productId
	 * @throws Exception
	 */
	public void removeLicenceSpec( String productId) throws Exception;
	
	
	
	/**
	 * /getlicencespec (GET productId)
	 * 
	 * e.g. http://localhost:8181/licencemgr/rest/licence-pub/getlicencespec?productId=
	 * 
	 * gets the LicenceSpecification stored for productId. 
	 * Returns error message if no LicenceSpecification found for productId

	 * @param productId
	 * @return licence specification
	 * @throws exception if licence for productId not found
	 */
	public LicenceSpecification getLicenceSpec(String productId) throws Exception;
	
	/**
	 * /getlicencemetadataspec (GET productId)
	 * 
	 * e.g. http://localhost:8181/licencemgr/rest/licence-pub/getlicencemetadataspec?productId=
	 * 
	 * gets the LicenceSpecification stored for productId.
	 * Returns error message if no LicenceSpecification found for productId
	 * 
	 * @param productId
	 * @return licence metadata spec (not the full license spec)
	 */
	public LicenceMetadata getLicenceMetadata(String productId) throws Exception;
	
	

	/**
	 * /listspecs (GET )
	 * 
	 * e.g. http://localhost:8181/licencemgr/rest/licence-pub/listspecs
	 * 
	 * returns a list of the LicenceSpecifications (including the keys)
	 * @return
	 * @throws Exception
	 */
	public LicenceSpecList getLicenceSpecList() throws Exception;

	
	/**
	 * /list (GET )
	 * 
	 * e.g. http://localhost:8181/licencemgr/rest/licence-pub/list
	 * 
	 * returns a list of the LicenceMetadata specifications 
	 * (not the full licence specs with keys)
	 * @return list of licence metadata specs (not the full license spec)
	 */
	public LicenceMetadataList getLicenceMetadataList() throws Exception;
	
	/**
	 * /clearlicencespecs (GET ) deletes all values of the licenceSpecMap. 
	 * 
	 * e.g. http://localhost:8181/licencemgr/rest/licence-pub/clearlicencespecs?confirm=false
	 * 
	 * Will only delete specs if paramater confirm=true
	 * @param confirm
	 * @throws Exception
	 */
	public void deleteLicenceSpecifications(Boolean confirm) throws Exception;
	
	/**
	 * /createlicence (POST licenceMetadata) 
	 * 
	 * Creates an encoded String instance of a licence from the supplied licenceMetadata in xml form. 
	 * Creates an encoded String instance of a licence from the LicenceSecification corresponding to the productId 
	 * in the supplied createLicenceMetadata 
	 * throws an exception if the corresponding LicenceSecification cannot be found 
	 * or the names of options or licencee are different from the specification
	 * 
	 * Parameter createLicenceMetadata should be created from a copy of the LicenceMetadata in the LicenceSpecfication 
	 * i.e. it must contain the productId and the options must correspond to the options in the LicenceSpecification 
	 * 
	 * @param licenceMetadata
	 * @return
	 */
	public String createLicenceInstanceStr(LicenceMetadata licenceMetadata);
	

	
}

