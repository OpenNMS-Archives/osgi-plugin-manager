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



@XmlRootElement(name="licenceMetadataSpecs")
@XmlAccessorType(XmlAccessType.NONE)
public class LicenceMetadataList  {

	@XmlElementWrapper(name="licenceMetadataSpecList")
	@XmlElement(name="licenceMetadataSpec")
	private List<LicenceMetadata> licenceMetadataList = new ArrayList<LicenceMetadata>();

	/**
	 * @return the licenceMetadataList
	 */
	public List<LicenceMetadata> getLicenceMetadataList() {
		return licenceMetadataList;
	}

	/**
	 * @param licenceMetadataList the licenceMetadataList to set
	 */
	public void setLicenceMetadataList(List<LicenceMetadata> licenceMetadataList) {
		this.licenceMetadataList = licenceMetadataList;
	}
	

	// YOU MUST UPDATE hashCode() and equals(Object obj) if you change the fields in this class
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((licenceMetadataList == null) ? 0 : licenceMetadataList
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LicenceMetadataList other = (LicenceMetadataList) obj;
		if (licenceMetadataList == null) {
			if (other.licenceMetadataList != null)
				return false;
		} else if (!licenceMetadataList.equals(other.licenceMetadataList))
			return false;
		return true;
	}
	
	

}