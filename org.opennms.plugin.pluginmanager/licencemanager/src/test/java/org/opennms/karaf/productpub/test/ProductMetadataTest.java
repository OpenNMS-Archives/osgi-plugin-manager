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

package org.opennms.karaf.productpub.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opennms.karaf.licencemgr.metadata.jaxb.ProductMetadata;

public class ProductMetadataTest {

	@Test
	public void testProductMetadata() {
		System.out.println("@Test - testProductMetadata. START");
		ProductMetadata pmeta= new ProductMetadata();
		pmeta.setOrganization("OpenNMS Project");
		pmeta.setProductDescription("Test product description");
		pmeta.setFeatureRepository("mvn:org.opennms.licencemgr/licence.manager.example/2.10.0/xml/features");
		pmeta.setProductId("org.opennms/org.opennms.karaf.licencemanager.testbundle/1.0-SNAPSHOT");
		pmeta.setProductName("test Bundle");
		pmeta.setProductUrl("http:\\\\opennms.co.uk");
		pmeta.setLicenceKeyRequired(true);
		pmeta.setLicenceType("OpenNMS EULA See http:\\\\opennms.co.uk\\EULA");
		
		String productMetadataXml=pmeta.toXml();
		String productMetadataHex=pmeta.toHexString();
		
		System.out.println("@Test - testProductMetadata. productMetadataXml="+productMetadataXml);
		System.out.println("@Test - testProductMetadata. productMetadataHex="+productMetadataHex);
		
		ProductMetadata pmeta2= new ProductMetadata();
		pmeta2.fromXml(productMetadataXml);
		System.out.println("@Test - testProductMetadata. pmeta2.toXml()="+pmeta2.toXml());
		
		//assertEquals(pmeta,pmeta2);
		assertEquals(pmeta.toXml(),pmeta2.toXml());
		
		ProductMetadata pmeta3= new ProductMetadata();
		pmeta3.fromHexString(productMetadataHex);
		assertEquals(pmeta,pmeta3);
		assertEquals(pmeta.toXml(),pmeta3.toXml());
		
		System.out.println("@Test - testProductMetadata. END");
	}

}
