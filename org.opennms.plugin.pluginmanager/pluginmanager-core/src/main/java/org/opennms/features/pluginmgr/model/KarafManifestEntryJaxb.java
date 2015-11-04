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

package org.opennms.features.pluginmgr.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennms.karaf.licencemgr.metadata.jaxb.ProductSpecList;

@XmlRootElement(name="karafManifestData")
@XmlAccessorType(XmlAccessType.NONE)
public class KarafManifestEntryJaxb {
	

	@XmlElement(name="karafInstanceName")
	private String karafInstanceName = null;
	
	@XmlElement(name="karafInstanceUrl")
	private String karafInstanceUrl = null;
	
	@XmlElement(name="karafInstanceUserName")
	private String karafInstanceUserName = "admin"; //default
	
	@XmlElement(name="karafInstancePassword")
	private String karafInstancePassword = "admin"; //default
	
	@XmlElement(name="remoteIsAccessible")
	private Boolean remoteIsAccessible=true;
	
	@XmlElement(name="allowUpdateMessages")
	private Boolean allowUpdateMessages=false;
	
	@XmlElement(name="pluginManifest")
	private ProductSpecList pluginManifest=new ProductSpecList();
	
	@XmlElement(name="manifestSystemId")
	private String manifestSystemId=null;
	
	/**
	 * @return the karafInstanceName
	 */
	public String getKarafInstanceName() {
		return karafInstanceName;
	}

	/**
	 * @param karafInstanceName the karafInstanceName to set
	 */
	public void setKarafInstanceName(String karafInstanceName) {
		this.karafInstanceName = karafInstanceName;
	}

	/**
	 * @return the karafInstanceUrl
	 */
	public String getKarafInstanceUrl() {
		return karafInstanceUrl;
	}

	/**
	 * @param karafInstanceUrl the karafInstanceUrl to set
	 */
	public void setKarafInstanceUrl(String karafInstanceUrl) {
		this.karafInstanceUrl = karafInstanceUrl;
	}
	
	/**
	 * @return the karafInstanceUserName
	 */
	public String getKarafInstanceUserName() {
		return karafInstanceUserName;
	}

	/**
	 * @param karafInstanceUserName the karafInstanceUserName to set
	 */
	public void setKarafInstanceUserName(String karafInstanceUserName) {
		this.karafInstanceUserName = karafInstanceUserName;
	}

	/**
	 * @return the karafInstancePassword
	 */
	public String getKarafInstancePassword() {
		return karafInstancePassword;
	}

	/**
	 * @param karafInstancePassword the karafInstancePassword to set
	 */
	public void setKarafInstancePassword(String karafInstancePassword) {
		this.karafInstancePassword = karafInstancePassword;
	}


	/**
	 * @return the remoteIsAccessible
	 */
	public Boolean getRemoteIsAccessible() {
		return remoteIsAccessible;
	}

	/**
	 * @param remoteIsAccessible the remoteIsAccessible to set
	 */
	public void setRemoteIsAccessible(Boolean remoteIsAccessible) {
		this.remoteIsAccessible = remoteIsAccessible;
	}

	/**
	 * @return the allowUpdateMessages
	 */
	public Boolean getAllowUpdateMessages() {
		return allowUpdateMessages;
	}

	/**
	 * @param allowUpdateMessages the allowUpdateMessages to set
	 */
	public void setAllowUpdateMessages(Boolean allowUpdateMessages) {
		this.allowUpdateMessages = allowUpdateMessages;
	}

	/**
	 * @return the pluginManifest
	 */
	public ProductSpecList getPluginManifest() {
		return pluginManifest;
	}

	/**
	 * @param pluginManifest the pluginManifest to set
	 */
	public void setPluginManifest(ProductSpecList pluginManifest) {
		this.pluginManifest = pluginManifest;
	}

	/**
	 * @return the manifestSystemId
	 */
	public String getManifestSystemId() {
		return manifestSystemId;
	}

	/**
	 * @param manifestSystemId the manifestSystemId to set
	 */
	public void setManifestSystemId(String manifestSystemId) {
		this.manifestSystemId = manifestSystemId;
	}
}
