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

package org.opennms.karaf.licencemgr.metadata.jaxb;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="licenceSpecifications")
@XmlAccessorType(XmlAccessType.NONE)
public class LicenceSpecList  {

	@XmlElementWrapper(name="licenceSpecificationList")
	@XmlElement(name="licenceSpecification")
	private List<LicenceSpecification> licenceSpecList = new ArrayList<LicenceSpecification>();

	/**
	 * @return the licenceSpecList
	 */
	public List<LicenceSpecification> getLicenceSpecList() {
		return licenceSpecList;
	}

	/**
	 * @param licenceSpecList the licenceSpecList to set
	 */
	public void setLicenceSpecList(List<LicenceSpecification> licenceSpecMap) {
		this.licenceSpecList = licenceSpecMap;
	}
	
}