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

package org.opennms.karaf.licencemgr;

import java.io.IOException;
import java.io.InputStream;

import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadata;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceSpecification;
import org.opennms.karaf.licencepub.LicencePublisher;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class BundleLicenceSpecImpl implements BundleLicenceSpec {

	private BundleContext bundleContext;

	private LicenceMetadata licenceMetadataSpec=null;

	private LicencePublisher licencePublisher=null;
	
	private String productId=null;

	private String licenceMetadataUri=null;
	
	private String aesSecretKeyStr=null;
	
	private String publicKeyStr=null;

	private String readFile(InputStream is ) throws IOException {
		java.util.Scanner s = new java.util.Scanner(is, "UTF-8").useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}
	
	/**
	 * constructor
	 */
	public BundleLicenceSpecImpl(){
		super();
	}

	/**
	 * constructor will also register spec
	 */
	public BundleLicenceSpecImpl(LicencePublisher licencePublisher, BundleContext bundleContext, String productId, String licenceMetadataUri,  String aesSecretKeyStr, String publicKeyStr){
		super();
		if (licencePublisher==null) throw new IllegalArgumentException("licencePublisher cannot be null");
		if (bundleContext==null) throw new IllegalArgumentException("bundleContext cannot be null");
		if (productId==null) throw new IllegalArgumentException("productId cannot be null");
		if (licenceMetadataUri==null) throw new IllegalArgumentException("licenceMetadataUri cannot be null");
		if (aesSecretKeyStr==null) throw new IllegalArgumentException("aesSecretKeyStr cannot be null");
		if (publicKeyStr==null) throw new IllegalArgumentException("publicKeyStr cannot be null");
		
		setLicencePublisher(licencePublisher);
		setBundleContext(bundleContext);
		setProductId(productId);
		setLicenceMetadataUri(licenceMetadataUri);
		setPublicKeyStr(publicKeyStr);
		setAesSecretKeyStr(aesSecretKeyStr);

		registerSpec();
	}
	
	/**
	 * use as blueprint init-method
	 */
	@Override
	public void registerSpec(){
		if (licencePublisher==null) throw new RuntimeException("licencePublisher cannot be null");
		if (bundleContext==null) throw new RuntimeException("bundleContext cannot be null");

		// only loads licenceMetadataSpec from URI if not already set by dependency injection
		if (licenceMetadataSpec==null){
			if (licenceMetadataUri==null) throw new IllegalArgumentException("licenceMetadataUri cannot be null if no licenceMetadataSpec defined");
			InputStream is=null;
			try {
				Bundle bundle = bundleContext.getBundle();
				is = bundle.getEntry(licenceMetadataUri).openStream();
				String licenceMetadataXml =  readFile(is);
				licenceMetadataSpec=new LicenceMetadata();
				licenceMetadataSpec.fromXml(licenceMetadataXml);
			} catch (Exception e) {
				throw new IllegalArgumentException("cannot load licenceMetadataSpec file from bundle licenceMetadataUri="+licenceMetadataUri+ "  "+e);
			} finally {
				if (is!=null)
					try {
						is.close();
					} catch (IOException e) {}
				is=null;
			}
		}
		
		if (! productId.equals(licenceMetadataSpec.getProductId())){
			throw new IllegalArgumentException("productId in licenceMetadataSpec ="+licenceMetadataSpec.getProductId()
					+" is different to productId for licence keys ="+productId+ "  ");
		}
		LicenceSpecification licenceSpec= new LicenceSpecification(productId, licenceMetadataSpec, aesSecretKeyStr, publicKeyStr);
		licencePublisher.addLicenceSpec(licenceSpec);;
		System.out.println("Registered Licence Specification for productId="+licenceMetadataSpec.getProductId());
	}

	/**
	 * use as blueprint destroy-method
	 */
	@Override
	public void unregisterSpec(){
		if (licencePublisher!=null){
			try{
				licencePublisher.removeLicenceSpec(licenceMetadataSpec.getProductId());
				System.out.println("Unregistered Licence Specification for productId="+licenceMetadataSpec.getProductId());
			} catch (Exception e){
				System.out.println("Problem Unregistering Licence Specification for productId="+licenceMetadataSpec.getProductId()+"  "+ e);
			}  finally {
				licencePublisher=null; //release resources
			}
		}
	}



	/**
	 * @return the bundleContext
	 */
	@Override
	public BundleContext getBundleContext() {
		return bundleContext;
	}

	/**
	 * @param bundleContext the bundleContext to set
	 */
	@Override
	public void setBundleContext(BundleContext bundleContext) {
		if (bundleContext==null) throw new IllegalArgumentException("bundleContext cannot be null");
		this.bundleContext = bundleContext;
	}

	/**
	 * @return the licenceMetadataSpec
	 */
	@Override
	public LicenceMetadata getLicenceMetadataSpec() {
		return licenceMetadataSpec;
	}

	/**
	 * @param licenceMetadataSpec the licenceMetadataSpec to set
	 */
	@Override
	public void setLicenceMetadataSpec(LicenceMetadata licenceMetadataSpec) {
		if (licenceMetadataSpec==null) throw new RuntimeException("licenceMetadataSpec cannot be null");
		this.licenceMetadataSpec = licenceMetadataSpec;
	}

	/**
	 * @return the licencePublisher
	 */
	@Override
	public LicencePublisher getLicencePublisher() {
		return licencePublisher;
	}

	/**
	 * @param licencePublisher the licencePublisher to set
	 */
	@Override
	public void setLicencePublisher(LicencePublisher licencePublisher) {
		if (licencePublisher==null) throw new RuntimeException("licencePublisher cannot be null");
		this.licencePublisher = licencePublisher;
	}

	/**
	 * 
	 * @return productId
	 */
	@Override
	public String getProductId() {
		return productId;
	}

	/**
	 * 
	 * @param productId
	 */
	@Override
	public void setProductId(String productId) {
		this.productId = productId;
	}

	/**
	 * @return the licenceMetadataUri
	 */
	@Override
	public String getLicenceMetadataUri() {
		return licenceMetadataUri;
	}

	/**
	 * @param licenceMetadataUri the licenceMetadataUri to set
	 */
	@Override
	public void setLicenceMetadataUri(String licenceMetadataUri) {
		if (licenceMetadataUri==null) throw new RuntimeException("licenceMetadataUri cannot be null");
		this.licenceMetadataUri = licenceMetadataUri;
	}

	/**
	 * @return the aesSecretKeyStr
	 */
	@Override
	public String getAesSecretKeyStr() {
		return aesSecretKeyStr;
	}

	/**
	 * @param aesSecretKeyStr the aesSecretKeyStr to set
	 */
	@Override
	public void setAesSecretKeyStr(String aesSecretKeyStr) {
		if (aesSecretKeyStr==null) throw new RuntimeException("aesSecretKeyStr cannot be null");
		this.aesSecretKeyStr = aesSecretKeyStr;
	}

	/**
	 * @return the publicKeyStr
	 */
	@Override
	public String getPublicKeyStr() {
		return publicKeyStr;
	}

	/**
	 * @param publicKeyStr the publicKeyStr to set
	 */
	@Override
	public void setPublicKeyStr(String publicKeyStr) {
		if (publicKeyStr==null) throw new RuntimeException("publicKeyStr cannot be null");
		this.publicKeyStr = publicKeyStr;
	}
}
