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


import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceList;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadata;

public interface LicenceManagerClient {


	/**
	 * /addlicence (GET licence)
	 * 
	 * Adds a licence to the licence service. 
	 * 
	 * e.g. http://localhost:8181/licencemgr/rest/licence-mgr/addlicence?licence=
	 * 
	 * The licence is added for productId corresponding to the productId in 
	 * the LicenceMetadata. Previous entries for that productId are overwritten
	 * The licence string must have correct checksum and readable LicenceMetadata 
	 * 
	 * @param licence
	 * @return licenceMetadata contained in the licence just added
	 * @throws Exception Throws error if licence string incorrectly formatted. 
	 */
	public LicenceMetadata addLicence(String licence) throws Exception;

	/**
	 * /removelicence (GET productId)
	 * 
	 * e.g. http://localhost:8181/licencemgr/rest/licence-mgr/removelicence?productId=
	 * 
	 * removes any licence corresponding to productId.
	 * 
	 * @param productId
	 * @throws Exception
	 */
	public void removeLicence(String productId) throws Exception;
	
	/**
	 * /getlicence (GET productId)
	 * 
	 * e.g. http://localhost:8181/licencemgr/rest/licence-mgr/getlicence?productId=
	 * 
	 * Gets the licence corresponding to the productId
	 * 
	 * @param productId
	 * @return
	 * @throws Exception
	 */
	public String getLicence(String productId) throws Exception;
	
	
	/**
	 * /isauthenticated (GET productId)
	 * 
	 * e.g. http://localhost:8181/licencemgr/rest/licence-mgr/isauthenticated?productId=
	 * 
	 * checks if the licence corresponding to the productId is authenticated
	 * 
	 * 
	 * @param productId
	 * @return true if authenticated / false if not
	 * @throws Exception
	 */
	public Boolean isAuthenticated(String productId) throws Exception;

	/**
	 * /list (GET )
	 * e.g http://localhost:8181/licencemgr/rest/licence-mgr/list
	 * 
	 * returns a map of all installed licences 
	 * with key=productId and value = licence string
	 * @return
	 * @throws Exception
	 */
	public LicenceList getLicenceMap() throws Exception;
	
	/**
	 * /list (GET )
	 * e.g http://localhost:8980/opennms/licencemgr/rest/licence-mgr/listforsystemid?systemId=32e396e36b28ef5d-a48ef1cb
	 * 
	 * returns a map of all installed licences for given systemid
	 * with key=productId and value = licence string
	 * @return
	 * @throws Exception
	 */
	public LicenceList getLicenceMapForSystemId(String systemId) throws Exception;



	/**
	 * /clearlicences (GET )
	 * 
	 * e.g. http://localhost:8181/licencemgr/rest/licence-mgr/clearlicences?confirm=false
	 * 
	 * deletes all licence entries. Will only delete licences if paramater confirm=true 
	 * 
	 * @param confirm
	 * @throws Exception
	 */
	public void deleteLicences(Boolean confirm) throws Exception;

	/**
	 * /getsystemid (GET ) 
	 * 
	 * e.g. http://localhost:8181/licencemgr/rest/licence-mgr/getsystemid
	 * 
	 * gets the systemId for this system
	 * @return
	 * @throws Exception
	 */
	public String getSystemId() throws Exception;

	/**
	 * /setsystemid (GET systemId) 
	 * 
	 * e.g. http://localhost:8181/licencemgr/rest/licence-mgr/setsystemid?systemId=
	 * 
	 * sets the systemId. 
	 * Note that the checksum for the systemId must be correct
	 * @param systemId
	 * @throws Exception
	 */
	public void setSystemId(String systemId) throws Exception;
	
	/**
	 * /makesystemid (GET )
	 * 
	 * e.g. http://localhost:8181/licencemgr/rest/licence-mgr/makesystemid
	 * 
	 * Makes a new systemId with a random identifier and checksum.
	 * Sets the systemId to the newly generated value.
	 * 
	 * @return
	 * @throws Exception
	 */
	public String makeSystemInstance() throws Exception;

	/**
	 * /checksumforstring (GET string)
	 * 
	 * e.g. http://localhost:8181/licencemgr/rest/licence-mgr/checksumforstring?string=
	 * 
	 * Generates a checksum for the supplied string
	 * Adds a CRC32 encoded string to supplied string separated by '-' 
	 * resulting in string of form 'valueString'-'CRC32 in Hex'. 
	 * returns original string plus checksum in form 'valueString'-'CRC32 in Hex' 
	 * 
	 * @param string Paramater string - string to have checkum added. 
	 * @return returns original string plus checksum in form 'valueString'-'CRC32 in Hex' 
	 * @throws Exception
	 */
	public String checksumForString(String string) throws Exception;
}
