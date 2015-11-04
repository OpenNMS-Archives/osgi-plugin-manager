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

package org.opennms.karaf.licencemgr.test;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadata;
import org.opennms.karaf.licencemgr.metadata.jaxb.OptionMetadata;

public class LicenceMetadataTest {

	public static LicenceMetadata metadata=null;
	public static String licenceMetadataHexStr=null;
	public static String licenceMetadataHashStr=null;
	
    @Test
    public void LicenceMetadataTestTests(){
    	AmetadataTest();
    	BmetadataTest();
    	CmetadataTest();
    }
    
    public void AmetadataTest(){
    	System.out.println("@Test AmetadataTest() Start");
    	metadata=new LicenceMetadata();
    	printMetadata(metadata);
    	System.out.println("@Test AmetadataTest() End");
    }
    

    public void BmetadataTest(){
    	System.out.println("@Test BmetadataTest() Start");
    	metadata=new LicenceMetadata();
    	
    	metadata.setExpiryDate(new Date());
    	metadata.setStartDate(new Date());
    	metadata.setLicensee("Mr Craig Gallen");
    	metadata.setLicensor("OpenNMS UK");
    	metadata.setProductId("product id");
    	metadata.setMaxSizeSystemIds("2");
    	metadata.getSystemIds().add("system id");
    	metadata.setFeatureRepository("mvn:org.opennms.licencemgr/licence.manager.example/2.10.0/xml/features");
    	
    	
    	OptionMetadata option1 = new OptionMetadata("newname1", "newvalue1", "this is the description1");
		metadata.getOptions().add(option1);
		
    	OptionMetadata option2 = new OptionMetadata("newname2", "newvalue2", "this is the description2");
		metadata.getOptions().add(option2);
		
		// should add only one instance of each unique object
		metadata.getOptions().add(option1);
		assertEquals (metadata.getOptions().size(),2);
		
		
    	
    	licenceMetadataHexStr=metadata.toHexString();
    	licenceMetadataHashStr=metadata.sha256Hash();
    	
    	System.out.println("@Test BmetadataTest() Original Metadata Object");
    	printMetadata(metadata);
    	System.out.println("@Test BmetadataTest() End");
    }
    

    public void CmetadataTest(){
    	System.out.println("@Test CmetadataTest() Start");
    	
    	LicenceMetadata newMetadata = new LicenceMetadata();
    	newMetadata.fromHexString(licenceMetadataHexStr);
    	String newHash=newMetadata.sha256Hash();
    	
    	System.out.println("@Test CmetadataTest() Original Metadata Object");
    	printMetadata(metadata);
    	System.out.println("@Test CmetadataTest() New Metadata Object");
    	printMetadata(newMetadata);
    	
    	assertEquals(licenceMetadataHashStr,newHash);
    	
    	
    	System.out.println("@Test CmetadataTest() End");
    }
    
    
    public void printMetadata(LicenceMetadata metadata){
    	
    	String xml = metadata.toXml();
    	System.out.println("@Test metadataTest1() MetadataXML="+xml);
    	String MetadataHex = metadata.toHexString();
    	System.out.println("@Test metadataTest1() MetadataHex="+MetadataHex);
    	String hash = metadata.sha256Hash();
    	System.out.println("@Test metadataTest1() Metadatasha256Hash="+hash);
    	
    }
    
    
    
    
    
    
}
