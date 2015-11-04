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

/**
 * This interface defines the keys to be made available to the LicencePublisher in order to create 
 * new licences
 * @author cgallen
 *
 */
public interface PublisherKeys {
	
	/**
	 * @return the aesSecretKeyStr
	 */
	public String getAesSecretKeyStr() ;
	
	/**
	 * @return the publicKeyStr
	 */
	public String getPublicKeyStr();

}
