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

package org.opennms.karaf.featuremgr.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Wrapper for a Karaf feature including JAXB annotations.
 */
@XmlRootElement(name = "Feature")
public class FeatureWrapperJaxb {

	private String name;
	private String version;
	private String description;
	private String details;
	private Boolean isInstalled;

	public FeatureWrapperJaxb() { }

	public FeatureWrapperJaxb(String name, String version, String description, String details, Boolean isInstalled) {
		this.name = name;
		this.version = version;
		this.description=description;
		this.details=details;
		this.isInstalled=isInstalled;
	}

	public String getName() {
		return this.name;
	}

	@XmlElement(name="name")
	public void setName(String name) {
		this.name = name;
	}


	public String getVersion() {
		return this.version;
	}

	@XmlElement(name="version")
	public void setVersion(String version) {
		this.version = version;
	}


	public String getDescription() {
		return description;
	}

	@XmlElement(name="description")
	public void setDescription(String description) {
		this.description = description;
	}

	public String getDetails() {
		return details;
	}

	@XmlElement(name="details")
	public void setDetails(String details) {
		this.details = details;
	}

	public Boolean getIsInstalled() {
		return isInstalled;
	}

	@XmlElement(name="isinstalled")
	public void setIsInstalled(Boolean isInstalled) {
		this.isInstalled = isInstalled;
	}

}
