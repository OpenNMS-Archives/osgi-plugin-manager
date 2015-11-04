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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="option")
@XmlAccessorType(XmlAccessType.NONE)
public class OptionMetadata {

	private String name=null;
	private String value=null;
	private String description=null;
	
	public OptionMetadata(String name, String value, String description){
		if (name==null) throw new IllegalArgumentException("OptionMetadata name cannot be null");
		if (value==null) throw new IllegalArgumentException("OptionMetadata value cannot be null");
		if (description==null) throw new IllegalArgumentException("OptionMetadata description cannot be null");
		this.name=name;
		this.value=value;
		this.description = description;
	}
	
	public OptionMetadata(){
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name name of the licence option
	 */
	@XmlElement(name="name")
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the value of the licence option
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value of the licence option to set
	 */
	@XmlElement(name="value")
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return textual description of the value option
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param textual description of the value option
	 */
	@XmlElement(name="description")
	public void setDescription(String description) {
		this.description = description;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OptionMetadata other = (OptionMetadata) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	
	
	
	
}
