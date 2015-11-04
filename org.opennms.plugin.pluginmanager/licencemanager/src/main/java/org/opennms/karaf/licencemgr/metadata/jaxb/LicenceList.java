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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement(name="licences")
@XmlAccessorType(XmlAccessType.NONE)
public class LicenceList  {

	@XmlElementWrapper(name="licenceList")
	@XmlElement(name="licenceEntry")
	private List<LicenceEntry> licenceList = new ArrayList<LicenceEntry>();

	/**
	 * @return the licenceList
	 */
	public List<LicenceEntry> getLicenceList() {
		return licenceList;
	}

	/**
	 * @param licenceList the licenceList to set
	 */
	public void setLicenceList(List<LicenceEntry> licenceList) {
		this.licenceList = licenceList;
	}


}