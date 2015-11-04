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

/**
 * Replay message used to wrap all correct replies from rest service
 * @author cgallen
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ReplyMessage {
	
	@XmlElement()
	private String replyComment=null;

	@XmlElement()
	private String licence=null;

	@XmlElement()
	private String systemId=null;
		
	@XmlElement()
	private String productId=null;
	
	@XmlElement()
	private LicenceSpecification licenceSpecification=null;
	
	@XmlElement()
	private LicenceMetadata licenceMetadata=null;
	
	@XmlElement()
	private LicenceMetadata licenceMetadataSpec=null;
	
	@XmlElement()
	private ProductMetadata productMetadata=null;
	
	@XmlElement()
	private String checksum=null;
	
	@XmlElement()
	private Boolean isAuthenticated=null;

	/**
	 * @return the replyComment
	 */
	public String getReplyComment() {
		return replyComment;
	}

	/**
	 * @param replyComment the replyComment to set
	 */
	public void setReplyComment(String replyComment) {
		this.replyComment = replyComment;
	}

	/**
	 * @return the licence
	 */
	public String getLicence() {
		return licence;
	}

	/**
	 * @param licence the licence to set
	 */
	public void setLicence(String licence) {
		this.licence = licence;
	}

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
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}

	/**
	 * @param productId the productId to set
	 */
	public void setProductId(String productId) {
		this.productId = productId;
	}

	/**
	 * @return the licenceSpecification
	 */
	public LicenceSpecification getLicenceSpecification() {
		return licenceSpecification;
	}

	/**
	 * @param licenceSpecification the licenceSpecification to set
	 */
	public void setLicenceSpecification(LicenceSpecification licenceSpecification) {
		this.licenceSpecification = licenceSpecification;
	}

	/**
	 * @return the licenceMetadata
	 */
	public LicenceMetadata getLicenceMetadata() {
		return licenceMetadata;
	}

	/**
	 * @param licenceMetadata the licenceMetadata to set
	 */
	public void setLicenceMetadata(LicenceMetadata licenceMetadata) {
		this.licenceMetadata = licenceMetadata;
	}

	/**
	 * @return the licenceMetadataSpec
	 */
	public LicenceMetadata getLicenceMetadataSpec() {
		return licenceMetadataSpec;
	}

	/**
	 * @param licenceMetadataSpec the licenceMetadataSpec to set
	 */
	public void setLicenceMetadataSpec(LicenceMetadata licenceMetadataSpec) {
		this.licenceMetadataSpec = licenceMetadataSpec;
	}

	/**
	 * @return the productMetadata
	 */
	public ProductMetadata getProductMetadata() {
		return productMetadata;
	}

	/**
	 * @param productMetadata the productMetadata to set
	 */
	public void setProductMetadata(ProductMetadata productMetadata) {
		this.productMetadata = productMetadata;
	}

	/**
	 * @return the checksum
	 */
	public String getChecksum() {
		return checksum;
	}

	/**
	 * @param checksum the checksum to set
	 */
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	/**
	 * @return the isAuthenticated
	 */
	public Boolean getIsAuthenticated() {
		return isAuthenticated;
	}

	/**
	 * @param isAuthenticated the isAuthenticated to set
	 */
	public void setIsAuthenticated(Boolean isAuthenticated) {
		this.isAuthenticated = isAuthenticated;
	}
	
	
}
