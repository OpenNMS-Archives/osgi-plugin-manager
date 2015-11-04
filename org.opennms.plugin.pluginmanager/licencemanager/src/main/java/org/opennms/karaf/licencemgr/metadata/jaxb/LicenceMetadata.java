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

import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name="licenceMetadata")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType (propOrder={"productId","featureRepository","licensee","licensor","startDate","expiryDate","duration","maxSizeSystemIds","systemIds","options"})
public class LicenceMetadata {

	//NOTE IF YOU MODIFY THIS CLASS YOU MUST REGENERATE THE equals and hashCode methods
	//AND change the fromXml() method


	/**
	 * productId is expected to contain the name of the product in the form <name>/<version>
	 * The productId is used as the feature name for installing into Karaf the top level feature 
	 * definition of the licensed product such that the feature can be installed using 
	 * features:install name/version (version is optional) (Karaf 2.4.0)
	 * 
	 * Karaf features can reference dependent features with a range of versions 
	 * e.g. <feature version="[2.5.6,4)">spring</feature>
	 * so this allows us to have a single licence which can cover a range of releases of the
	 * feature which implements the product.
	 */
	String productId=null;
	
	/**
	 * featureRepository is expected to contain the url of the features repository 
	 * which describes the Karaf feature using the concatenated form
	 * <groupId>/<artifactId>/<version>/xml/features
	 * such that the repository can be installed using features:addurl (Karaf 2.4.0)
	 * e.g. features:addurl mvn:org.apache.camel/camel-example-osgi/2.10.0/xml/features
	 */
	String featureRepository=null;
	
	/**
	 * systemIds is a list of unique systemId's for which this licence is valid.
	 * a systemId is expected to contain the unique identifier of the system on which which the licensed artifact 
	 * will be installed. The systemId is terminated with a CRC32 checksum seperated by a - symbol <systemId>-<CRC32>
	 */
	Set<String> systemIds=new TreeSet<String>();
	
	/**
	 * maxSizeSystemIds is the maximum number of systemId's which can be included in the systemIds list.
	 * This is used by the shopping cart to limit the number of systems to which licences can be applied.
	 * 
	 * The actual number of systemId entries in systemId's must be <= maxSizeSystemIds for the licence to authenticate.
	 * 
	 * If maxSizeSystemIds==0 then no systemId's should be included in the list and the licence will
	 * authenticate with any and all systemIds,
	 */
	String maxSizeSystemIds=null;
	
	/**
	 * startDate - the date from which the licence will be valid
	 */
	Date startDate=new Date();
	
	/**
	 * expiryDate - the date on which the licence will expire. If Null (and duration is null) there is no expiry date.
	 */
	Date expiryDate=null;
	
	/**
	 * duration - alternative to expiry date. Duration of licence in days. If null (and expiryDate is null) there is no expiry date.
	 * If duration =0, there is no expiry date. If both defined, duration has precedence over expiryDate.
	 */
	String duration=null;

	/**
	 * (Definition licensee n. a person (organisation) given a license by 
	 * government or under private agreement)
	 * 
	 * the name / address of the person / organisation who is granted the licence
	 */
	String licensee=null;
	
	/**
	 * (Definition: licensor n. a person who gives another a license, particularly 
	 *  a private party doing so, such as a business giving someone a license to sell its product)
	 * 
	 * the name / address of the person / organisation granting the licence
	 */
	String licensor=null;
	
	/**
	 * licence options. Each option contains a name/value pair and a description field.
	 * This is intended to grant/restrict access to particular features
	 */
	Set<OptionMetadata> options=new HashSet<OptionMetadata>();
	
	/**
	 * sets LicenceMetadata values of this object from another licenceMetadata object
	 * @param licenceMetadata
	 */
	public void setLicenceMetadata(LicenceMetadata licenceMetadata){
		this.productId=licenceMetadata.productId;
		this.featureRepository=licenceMetadata.featureRepository;
		this.maxSizeSystemIds=licenceMetadata.maxSizeSystemIds;
		if(licenceMetadata.systemIds!=null) this.systemIds.addAll(licenceMetadata.systemIds);
		this.startDate=licenceMetadata.startDate;
		this.expiryDate=licenceMetadata.expiryDate;
		this.duration=licenceMetadata.duration;
		this.licensee=licenceMetadata.licensee;
		this.licensor=licenceMetadata.licensor;
		this.options.clear();
		if (licenceMetadata.options!=null) this.options.addAll(licenceMetadata.options);
	}


	/**
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}

	/**
	 * @param productId the productId to set
	 */
	@XmlElement(name="productId")
	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	/**
	 * @return the featureRepository
	 */
	public String getFeatureRepository() {
		return featureRepository;
	}

	/**
	 * @param featureRepository the featureRepository to set
	 */
	@XmlElement(name="featureRepository")
	public void setFeatureRepository(String featureRepository) {
		this.featureRepository = featureRepository;
	}

	/**
	 * @return the systemIds
	 */
	public Set<String> getSystemIds() {
		return systemIds;
	}


	/**
	 * @param systemIds the systemIds to set
	 */
	@XmlElementWrapper(name="systemIds")
	@XmlElement(name="systemId")
	public void setSystemIds(Set<String> systemIds) {
		this.systemIds = systemIds;
	}


	/**
	 * @return the maxSizeSystemIds
	 */
	public String getMaxSizeSystemIds() {
		return maxSizeSystemIds;
	}


	/**
	 * @param maxSizeSystemIds the maxSizeSystemIds to set
	 */
	@XmlElement(name="maxSizeSystemIds")
	public void setMaxSizeSystemIds(String maxSizeSystemIds) {
		this.maxSizeSystemIds = maxSizeSystemIds;
	}
	

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	@XmlElement(name="startDate")
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the expiryDate
	 */
	public Date getExpiryDate() {
		return expiryDate;
	}

	/**
	 * @param expiryDate the expiryDate to set
	 */
	@XmlElement(name="expiryDate")
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	/**
	 * @return the duration
	 */
	public String getDuration() {
		return duration;
	}


	/**
	 * @param duration the duration to set
	 */
	@XmlElement(name="duration")
	public void setDuration(String duration) {
		this.duration = duration;
	}


	/**
	 * @return the licensee
	 */
	public String getLicensee() {
		return licensee;
	}

	/**
	 * @param licensee the licensee to set
	 */
	@XmlElement(name="licensee")
	public void setLicensee(String licensee) {
		this.licensee = licensee;
	}

	/**
	 * @return the licensor
	 */
	public String getLicensor() {
		return licensor;
	}

	/**
	 * @param licensor the licensor to set
	 */
	@XmlElement(name="licensor")
	public void setLicensor(String licensor) {
		this.licensor = licensor;
	}

	/**
	 * @return the options
	 */
	public Set<OptionMetadata> getOptions() {
		return options;
	}

	/**
	 * @param options the options to set
	 */
	@XmlElementWrapper(name="options")
	@XmlElement(name="option")
	public void setOptions(Set<OptionMetadata> options) {
		this.options = options;
	}

	
	/**
	 * @return XML encoded version of LicenceMetadata
	 */
	public String toXml(){

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(org.opennms.karaf.licencemgr.metadata.jaxb.ObjectFactory.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			StringWriter stringWriter = new StringWriter();
			jaxbMarshaller.marshal(this,stringWriter);
			return stringWriter.toString();

		} catch (JAXBException e) {
			throw new RuntimeException("Problem marshalling LicenceMetadata:",e);
		}
	}

	/**
	 * load this object with data from xml string
	 * @parm XML encoded version of LicenceMetadata
	 */
	public void fromXml(String xmlStr){

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(org.opennms.karaf.licencemgr.metadata.jaxb.ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			StringReader reader = new StringReader(xmlStr);
			LicenceMetadata licenceMetadata= (LicenceMetadata) jaxbUnmarshaller.unmarshal(reader);
			this.productId=licenceMetadata.productId;
			this.featureRepository=licenceMetadata.featureRepository;
			this.maxSizeSystemIds=licenceMetadata.maxSizeSystemIds;
			if(licenceMetadata.systemIds!=null) this.systemIds.addAll(licenceMetadata.systemIds);
			this.startDate=licenceMetadata.startDate;
			this.expiryDate=licenceMetadata.expiryDate;
			this.duration=licenceMetadata.duration;
			this.licensee=licenceMetadata.licensee;
			this.licensor=licenceMetadata.licensor;
			this.options.clear();
			if (licenceMetadata.options!=null) this.options.addAll(licenceMetadata.options);
		} catch (JAXBException e) {
			throw new RuntimeException("Problem unmarshalling LicenceMetadata:",e);
		}
	}

	/**
	 * @return Hex encoded string of XML version of Metadata
	 * @throws UnsupportedEncodingException
	 */
	public String toHexString(){
		try {
			String xmlStr = toXml();
			byte[] array = xmlStr.getBytes("UTF-8");
			return DatatypeConverter.printHexBinary(array);
		} catch ( Exception e) {
			throw new RuntimeException("problem converting LicenceMetadata to hexString:",e);
		}
	}

	/**
	 * imports licence metadata from hex encoded version of XML metadata
	 * @param hexString
	 * 
	 */
	public void fromHexString(String hexString){
		try {
			byte[] array = DatatypeConverter.parseHexBinary(hexString);
			String xmlStr = new String(array, "UTF-8");
			this.fromXml(xmlStr);
		} catch ( Exception e) {
			throw new RuntimeException("problem importing LicenceMetadata from hexString:",e);
		}
	}

	/**
	 * Returns sha256 hash of XML encoded LicenceMetadata
	 * @return digest sha256 hash of XML encoded LicenceMetadata
	 */
	public String sha256Hash() {

		try {
			String xmlStr = toXml();
			byte[] array = xmlStr.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(array);
			byte[] digest = md.digest();
			return DatatypeConverter.printHexBinary(digest);
		} catch (Exception e) {
			throw new RuntimeException("problem calculating sha-256 hash for LicenceMetadata:",e);
		}
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((duration == null) ? 0 : duration.hashCode());
		result = prime * result
				+ ((expiryDate == null) ? 0 : expiryDate.hashCode());
		result = prime
				* result
				+ ((featureRepository == null) ? 0 : featureRepository
						.hashCode());
		result = prime * result
				+ ((licensee == null) ? 0 : licensee.hashCode());
		result = prime * result
				+ ((licensor == null) ? 0 : licensor.hashCode());
		result = prime * result
				+ ((maxSizeSystemIds == null) ? 0 : maxSizeSystemIds.hashCode());
		result = prime * result + ((options == null) ? 0 : options.hashCode());
		result = prime * result
				+ ((productId == null) ? 0 : productId.hashCode());
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result
				+ ((systemIds == null) ? 0 : systemIds.hashCode());
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
		LicenceMetadata other = (LicenceMetadata) obj;
		if (duration == null) {
			if (other.duration != null)
				return false;
		} else if (!duration.equals(other.duration))
			return false;
		if (expiryDate == null) {
			if (other.expiryDate != null)
				return false;
		} else if (!expiryDate.equals(other.expiryDate))
			return false;
		if (featureRepository == null) {
			if (other.featureRepository != null)
				return false;
		} else if (!featureRepository.equals(other.featureRepository))
			return false;
		if (licensee == null) {
			if (other.licensee != null)
				return false;
		} else if (!licensee.equals(other.licensee))
			return false;
		if (licensor == null) {
			if (other.licensor != null)
				return false;
		} else if (!licensor.equals(other.licensor))
			return false;
		if (maxSizeSystemIds == null) {
			if (other.maxSizeSystemIds != null)
				return false;
		} else if (!maxSizeSystemIds.equals(other.maxSizeSystemIds))
			return false;
		if (options == null) {
			if (other.options != null)
				return false;
		} else if (!options.equals(other.options))
			return false;
		if (productId == null) {
			if (other.productId != null)
				return false;
		} else if (!productId.equals(other.productId))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (systemIds == null) {
			if (other.systemIds != null)
				return false;
		} else if (!systemIds.equals(other.systemIds))
			return false;
		return true;
	}




}
