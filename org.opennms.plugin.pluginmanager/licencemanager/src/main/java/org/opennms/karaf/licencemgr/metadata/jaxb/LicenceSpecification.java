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
import javax.xml.bind.annotation.XmlType;

import org.opennms.karaf.licencemgr.PublisherKeys;

@XmlRootElement(name="licenceSpecification")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType (propOrder={"productId","aesSecretKeyStr","publicKeyStr","licenceMetadataSpec"})
public class LicenceSpecification {

	@XmlElement(name = "aesSecretKeyStr")
	private String aesSecretKeyStr;
	
	@XmlElement(name = "publicKeyStr")
	private String publicKeyStr;
	
	@XmlElement(name = "licenceMetadataSpec")
	private LicenceMetadata licenceMetadataSpec;
	
	@XmlElement(name = "productId")
    private String productId;
	
	/**
	 * Constructor for use by jaxb only
	 * LicenceSpecification has no setters as an immutable object.
	 * (you should use the constructors with arguments for all other purposes)
	 */
	public LicenceSpecification(){
		super();
	}
    
	/**
	 * Constructor
	 * @param productId
	 * @param licenceMetadataSpec
	 * @param publisherKeys
	 */
    public LicenceSpecification(String productId, LicenceMetadata licenceMetadataSpec, PublisherKeys publisherKeys){
    	this(productId, licenceMetadataSpec, publisherKeys.getAesSecretKeyStr(), publisherKeys.getPublicKeyStr());
    }
	
    /**
     * Constructor
     * @param productId
     * @param licenceMetadataSpec
     * @param aesSecretKeyStr
     * @param publicKeyStr
     */
	public LicenceSpecification(String productId, LicenceMetadata licenceMetadataSpec, String aesSecretKeyStr, 
			String publicKeyStr) {
		super();
		if (productId==null) throw new IllegalArgumentException("productId cannot be null in LicenceSpecification");
		if (licenceMetadataSpec==null) throw new IllegalArgumentException("licenceMetadataSpec cannot be null in LicenceSpecification");
		if (aesSecretKeyStr==null) throw new IllegalArgumentException("aesSecretKeyStr cannot be null in LicenceSpecification");
		if (publicKeyStr==null) throw new IllegalArgumentException("publicKeyStr cannot be null in LicenceSpecification");
		if (! productId.equals(licenceMetadataSpec.getProductId())) 
			throw new IllegalArgumentException("productId in LicenceSpecification constructor (="+productId
					+ ") does not match productId in licenceMetadataSpec(="+licenceMetadataSpec.getProductId()+")");
		
		this.productId = productId;
		this.aesSecretKeyStr = aesSecretKeyStr;
		this.publicKeyStr = publicKeyStr;
		this.licenceMetadataSpec = licenceMetadataSpec;
	}
	
	/**
	 * @return the aesSecretKeyStr
	 */
	public String getAesSecretKeyStr() {
		return aesSecretKeyStr;
	}

	/**
	 * @return the publicKeyStr
	 */
	public String getPublicKeyStr() {
		return publicKeyStr;
	}

	/**
	 * @return the licenceMetadataSpec
	 */
	public LicenceMetadata getLicenceMetadataSpec() {
		return licenceMetadataSpec;
	}

	/**
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}
}
