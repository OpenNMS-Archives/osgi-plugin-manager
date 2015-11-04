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

import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceList;
import org.opennms.karaf.licencemgr.metadata.jaxb.ProductSpecList;

@XmlRootElement(name="remoteKarafState")
@XmlAccessorType(XmlAccessType.NONE)
public class RemoteKarafState {


	@XmlElement(name="systemId")
	private String systemId = null;
	
	@XmlElement(name="installedPlugins")
	private ProductSpecList installedPlugins = null;

	@XmlElement(name="installedLicenceList")
	private LicenceList installedLicenceList = null;
	
	/**
	 * @return the systemId
	 */
	public String getSystemId() {
		return systemId;
	}

	/**
	 * @param systemId the systemId to set
	 */
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	/**
	 * @return the installedPlugins
	 */
	public ProductSpecList getInstalledPlugins() {
		return installedPlugins;
	}

	/**
	 * @param installedPlugins the installedPlugins to set
	 */
	public void setInstalledPlugins(ProductSpecList installedPlugins) {
		this.installedPlugins = installedPlugins;
	}

	/**
	 * @return the installedLicenceList
	 */
	public LicenceList getInstalledLicenceList() {
		return installedLicenceList;
	}

	/**
	 * @param installedLicenceList the installedLicenceList to set
	 */
	public void setInstalledLicenceList(LicenceList installedLicenceList) {
		this.installedLicenceList = installedLicenceList;
	}

	
}
