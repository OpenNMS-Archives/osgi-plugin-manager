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

package org.opennms.karaf.licencepub.test;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.opennms.karaf.licencemgr.ClientKeys;
import org.opennms.karaf.licencemgr.GeneratedKeys;
import org.opennms.karaf.licencemgr.PublisherKeys;
import org.opennms.karaf.licencemgr.metadata.Licence;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadata;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceSpecification;
import org.opennms.karaf.licencemgr.metadata.jaxb.OptionMetadata;
import org.opennms.karaf.licencepub.LicencePublisher;
import org.opennms.karaf.licencepub.LicencePublisherImpl;

public class LicencePublisherTest {

	@Test
	public void test1LicencePublisher(){
		System.out.println("@test test1LicencePublisher() Start");
		
		// generate keys
		GeneratedKeys generatedKeys= new GeneratedKeys();
		String aesSecretKeyStr=generatedKeys.getAesSecretKeyStr();
		String publicKeyStr=generatedKeys.getPublicKeyStr();

		//generate licence metadata
		String productId="org.opennms/org.opennms.karaf.licencemanager.testbundle/1.0-SNAPSHOT";
		String featureRepository="mvn:org.opennms.licencemgr/licence.manager.example/2.10.0/xml/features";
		
		LicenceMetadata licenceMetadataSpec= new LicenceMetadata();
		licenceMetadataSpec.setLicensor("OpenNMS UK");
		licenceMetadataSpec.setProductId(productId);
		licenceMetadataSpec.setFeatureRepository(featureRepository);
		OptionMetadata option1 = new OptionMetadata("newname", "false", "this is the description");
		licenceMetadataSpec.getOptions().add(option1);
		
		System.out.println("@test test1LicencePublisher() licenceMetadataSpec().toXml()"+licenceMetadataSpec.toXml());
		
		// generate licence specification
		LicenceSpecification licenceSpec= new LicenceSpecification(productId, 
				licenceMetadataSpec, aesSecretKeyStr, publicKeyStr);
		
		LicencePublisher licencePubService= new LicencePublisherImpl();
		licencePubService.addLicenceSpec(licenceSpec);
		
		// test retrieve licence specification
		LicenceSpecification retrievedLicenceSpec = licencePubService.getLicenceSpec(productId);
		assertEquals(licenceSpec.getLicenceMetadataSpec().toXml(), retrievedLicenceSpec.getLicenceMetadataSpec().toXml());
		
		// generate licence metadata using the licence metadata spec
		LicenceMetadata retrievedLicenceMetadataSpec = retrievedLicenceSpec.getLicenceMetadataSpec();
		LicenceMetadata licencemetadata = new LicenceMetadata();
		licencemetadata.setLicenceMetadata(retrievedLicenceMetadataSpec);
		assertEquals(licencemetadata.toXml(), retrievedLicenceMetadataSpec.toXml());
		
		licencemetadata.setStartDate(new Date());
		licencemetadata.setLicensee("Mr Craig Gallen");
		licencemetadata.setProductId("org.opennms/org.opennms.karaf.licencemanager.testbundle/1.0-SNAPSHOT");
		
		licencemetadata.setMaxSizeSystemIds("3");
		licencemetadata.getSystemIds().add("4ad72a34e3635c1b-99da3323");
		licencemetadata.getSystemIds().add("32e396e36b28ef5d-a48ef1cb");

		String licenceMetadataXml=licencemetadata.toXml();
		
		// licenceMetadataXml is sent to licence publisher to publish licence
		System.out.println("@test test1LicencePublisher() licenceMetadataXml="+licenceMetadataXml);
		String licenceInstanceStr = licencePubService.createLicenceInstanceStr(licenceMetadataXml);
		System.out.println("@test test1LicencePublisher() licenceInstanceStr="+licenceInstanceStr);
		
		System.out.println("@test test1LicencePublisher() End");
		
		// check licenceInstanceStr
		new Licence(licenceInstanceStr, generatedKeys.getPrivateKeyEnryptedStr());
		
	}
	
	/**
	 * same test but using ClientKeys and PublisherKeys
	 */
	@Test
	public void test2LicencePublisher(){
		System.out.println("@test test2LicencePublisher() Start");
		
		// generate keys
		GeneratedKeys generatedKeys= new GeneratedKeys();
		ClientKeys clientkeys = generatedKeys.makeClientKeys();
		PublisherKeys publisherkeys = generatedKeys.makePublisherKeys();

		//generate licence metadata
		String productId="org.opennms/org.opennms.karaf.licencemanager.testbundle/1.0-SNAPSHOT";
		
		LicenceMetadata licenceMetadataSpec= new LicenceMetadata();
		licenceMetadataSpec.setLicensor("OpenNMS UK");
		licenceMetadataSpec.setProductId(productId);
		OptionMetadata option1 = new OptionMetadata("newname", "false", "this is the description");
		licenceMetadataSpec.getOptions().add(option1);
		
		System.out.println("@test test2LicencePublisher() licenceMetadataSpec().toXml()"+licenceMetadataSpec.toXml());
		
		// generate licence specification
		LicenceSpecification licenceSpec= new LicenceSpecification(productId, 
				licenceMetadataSpec, publisherkeys);
		
		// test add licence specification
		LicencePublisher licencePubService= new LicencePublisherImpl();
		licencePubService.addLicenceSpec(licenceSpec);
		
		// test retrieve licence specification
		LicenceSpecification retrievedLicenceSpec = licencePubService.getLicenceSpec(productId);
		assertEquals(licenceSpec.getLicenceMetadataSpec().toXml(), retrievedLicenceSpec.getLicenceMetadataSpec().toXml());
		
		// generate licence metadata using the licence metadata spec
		LicenceMetadata retrievedLicenceMetadataSpec = retrievedLicenceSpec.getLicenceMetadataSpec();
		LicenceMetadata licencemetadata = new LicenceMetadata();
		licencemetadata.setLicenceMetadata(retrievedLicenceMetadataSpec);
		assertEquals(licencemetadata.toXml(), retrievedLicenceMetadataSpec.toXml());
		
		licencemetadata.setStartDate(new Date());
		licencemetadata.setLicensee("Mr Craig Gallen");
		licencemetadata.setProductId("org.opennms/org.opennms.karaf.licencemanager.testbundle/1.0-SNAPSHOT");

		licencemetadata.setMaxSizeSystemIds("3");
		licencemetadata.getSystemIds().add("4ad72a34e3635c1b-99da3323");
		licencemetadata.getSystemIds().add("32e396e36b28ef5d-a48ef1cb");

		String licenceMetadataXml=licencemetadata.toXml();
		
		// licenceMetadataXml is sent to licence publisher to publish licence
		System.out.println("@test test2LicencePublisher() licenceMetadataXml="+licenceMetadataXml);
		String licenceInstanceStr = licencePubService.createLicenceInstanceStr(licenceMetadataXml);
		System.out.println("@test test2LicencePublisher() licenceInstanceStr="+licenceInstanceStr);
		
		// check licenceInstanceStr contains valid licence
		new Licence(licenceInstanceStr, clientkeys);
		
		System.out.println("@test test2LicencePublisher() End");
		
	}
}
