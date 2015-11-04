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

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="product")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType (propOrder={"productId","featureRepository","productName","productDescription","productUrl","organization","licenceType","licenceKeyRequired","licenceKeyAuthenticated"})
public class ProductMetadata {

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
	 * product name - plain text name of product- usually maven name field
	 */
	private String productName=null;
	
	/**
	 * description of product usually maven description field
	 */
	private String productDescription=null;
	
	/**
	 * URL for more product details usually matches maven url field
	 */
	private String productUrl=null;
	
	/**
	 * Organization which created the product. Usually matches the maven organization field
	 */
	private String organization=null;
	
	/**
	 * Licence under which product is published. Usually matches the maven licence field
	 */
	private String licenceType=null;
	
	/**
	 * If true this product requires a licence key to be installed
	 */
	private Boolean licenceKeyRequired=null;
	
	/**
	 * If true the licence key for this product is authenticated
	 * (this is only used by licence manager and not the product publisher)
	 */
	private Boolean licenceKeyAuthenticated=null;


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
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}

	/**
	 * @param productName the productName to set
	 */
	@XmlElement(name="productName")
	public void setProductName(String productName) {
		this.productName = productName;
	}

	/**
	 * @return the productMetadata
	 */
	public String getProductDescription() {
		return productDescription;
	}

	/**
	 * @param productMetadata the productMetadata to set
	 */
	@XmlElement(name="productDescription")
	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	/**
	 * @return the productUrl
	 */
	public String getProductUrl() {
		return productUrl;
	}

	/**
	 * @param productUrl the productUrl to set
	 */
	@XmlElement(name="productUrl")
	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}

	/**
	 * @return the organization
	 */
	public String getOrganization() {
		return organization;
	}

	/**
	 * @param organization the organization to set
	 */
	@XmlElement(name="organization")
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	

	/**
	 * @return the licence
	 */
	public String getLicenceType() {
		return licenceType;
	}

	/**
	 * @param licenceType the licenceType to set
	 */
	@XmlElement(name="licenceType")
	public void setLicenceType(String licenceType) {
		this.licenceType = licenceType;
	}

	/**
	 * @return the licenceKeyRequired
	 */
	public Boolean getLicenceKeyRequired() {
		return licenceKeyRequired;
	}

	/**
	 * @param licenceKeyRequired the licenceKeyRequired to set
	 */
	@XmlElement(name="licenceKeyRequired")
	public void setLicenceKeyRequired(Boolean licenceKeyRequired) {
		this.licenceKeyRequired = licenceKeyRequired;
	}
	
	/**
	 * If true the licence key for this product is authenticated
	 * (this is only used by licence manager and not the product publisher)
	 */
	public Boolean getLicenceKeyAuthenticated() {
		return licenceKeyAuthenticated;
	}

	/**
	 * If true the licence key for this product is authenticated
	 * (this is only used by licence manager and not the product publisher)
	 */
	@XmlElement(name="licenceKeyAuthenticated")
	public void setLicenceKeyAuthenticated(Boolean licenceKeyAuthenticated) {
		this.licenceKeyAuthenticated = licenceKeyAuthenticated;
	}
	
	/**
	 * @return XML encoded version of ProductMetadata
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
			ProductMetadata productMetadata= (ProductMetadata) jaxbUnmarshaller.unmarshal(reader);
			this.productId=productMetadata.productId;
			this.featureRepository=productMetadata.featureRepository;
			this.productDescription=productMetadata.productDescription;
			this.organization=productMetadata.organization;
			this.productName=productMetadata.productName;
			this.productUrl=productMetadata.productUrl;
			this.licenceKeyRequired=productMetadata.licenceKeyRequired;
			this.licenceKeyAuthenticated=productMetadata.licenceKeyAuthenticated;
			this.licenceType=productMetadata.licenceType;
		} catch (JAXBException e) {
			throw new RuntimeException("Problem unmarshalling ProductMetadata:",e);
		}
	}

	/**
	 * @return Hex encoded string of XML version of ProductMetadata
	 * @throws UnsupportedEncodingException
	 */
	public String toHexString(){
		try {
			String xmlStr = toXml();
			byte[] array = xmlStr.getBytes("UTF-8");
			return DatatypeConverter.printHexBinary(array);
		} catch ( Exception e) {
			throw new RuntimeException("problem converting ProductMetadata to hexString:",e);
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
			throw new RuntimeException("problem importing ProductMetadata from hexString:",e);
		}
	}

	//NOTE IF YOU MODIFY THIS CLASS YOU MUST REGENERATE THE equals and hashCode methods
	//AND change the fromXml() method
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((featureRepository == null) ? 0 : featureRepository
						.hashCode());
		result = prime
				* result
				+ ((licenceKeyAuthenticated == null) ? 0
						: licenceKeyAuthenticated.hashCode());
		result = prime
				* result
				+ ((licenceKeyRequired == null) ? 0 : licenceKeyRequired
						.hashCode());
		result = prime * result
				+ ((licenceType == null) ? 0 : licenceType.hashCode());
		result = prime * result
				+ ((organization == null) ? 0 : organization.hashCode());
		result = prime
				* result
				+ ((productDescription == null) ? 0 : productDescription
						.hashCode());
		result = prime * result
				+ ((productId == null) ? 0 : productId.hashCode());
		result = prime * result
				+ ((productName == null) ? 0 : productName.hashCode());
		result = prime * result
				+ ((productUrl == null) ? 0 : productUrl.hashCode());
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
		ProductMetadata other = (ProductMetadata) obj;
		if (featureRepository == null) {
			if (other.featureRepository != null)
				return false;
		} else if (!featureRepository.equals(other.featureRepository))
			return false;
		if (licenceKeyAuthenticated == null) {
			if (other.licenceKeyAuthenticated != null)
				return false;
		} else if (!licenceKeyAuthenticated
				.equals(other.licenceKeyAuthenticated))
			return false;
		if (licenceKeyRequired == null) {
			if (other.licenceKeyRequired != null)
				return false;
		} else if (!licenceKeyRequired.equals(other.licenceKeyRequired))
			return false;
		if (licenceType == null) {
			if (other.licenceType != null)
				return false;
		} else if (!licenceType.equals(other.licenceType))
			return false;
		if (organization == null) {
			if (other.organization != null)
				return false;
		} else if (!organization.equals(other.organization))
			return false;
		if (productDescription == null) {
			if (other.productDescription != null)
				return false;
		} else if (!productDescription.equals(other.productDescription))
			return false;
		if (productId == null) {
			if (other.productId != null)
				return false;
		} else if (!productId.equals(other.productId))
			return false;
		if (productName == null) {
			if (other.productName != null)
				return false;
		} else if (!productName.equals(other.productName))
			return false;
		if (productUrl == null) {
			if (other.productUrl != null)
				return false;
		} else if (!productUrl.equals(other.productUrl))
			return false;
		return true;
	}



}
