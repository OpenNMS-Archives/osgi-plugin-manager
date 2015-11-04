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

import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadata;

public interface LicenceAuthenticator {
	
	/**
	 * if the class authenticates the licence then the metadata will be available
	 * @return the licenceMetadata
	 */
	public LicenceMetadata getLicenceMetadata() ;
	
	/**
	 * If the class authenticates the licence then the licence string will be available
	 * @return the licencewithCRC
	 */
	public String getLicencewithCRC();

}
