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

package org.opennms.karaf.licencemgr.rest.client.test.manual;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceEntry;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceList;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadata;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadataList;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceSpecList;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceSpecification;
import org.opennms.karaf.licencemgr.metadata.jaxb.Util;
import org.opennms.karaf.licencemgr.rest.client.LicencePublisherClient;
import org.opennms.karaf.licencemgr.rest.client.jerseyimpl.LicencePublisherClientRestJerseyImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LicencePublisherClientRestJerseyTest {
	private static final Logger LOG = LoggerFactory.getLogger(LicencePublisherClientRestJerseyTest.class);
	
	// constants for tests
	private static String test_productId="myproject/1.0-SNAPSHOT";
	
	private static String test_system_id="32e396e36b28ef5d-a48ef1cb";

	private static String test_create_licence_metadata="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
			+ "<licenceMetadata>"
			+ "  <productId>myproject/1.0-SNAPSHOT</productId>"
			+ "  <featureRepository>org.opennms.project/myproject/1.0-SNAPSHOT/xml/features</featureRepository>"
			+ "  <licensee></licensee>"
			+ "  <licensor>OpenNMS UK</licensor>"
			+ "  <systemId>32e396e36b28ef5d-a48ef1cb</systemId>"
			+ "  <startDate>2015-08-18T16:50:08.541+01:00</startDate>"
			+ "  <duration>0</duration>"
			+ "  <options>"
			+ "    <option>"
			+ "      <description>this is the description of option 1</description>"
			+ "       <name>option1</name>"
			+ "      <value></value>"
			+ "    </option>"
			+ "   </options>"
			+ " </licenceMetadata>";
	
	private static String test_licence_specification="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
			
			+ "<licenceSpecification>"
			+ "<productId>myproject/1.0-SNAPSHOT</productId>"
			+ "<aesSecretKeyStr>CB0059D481BD446A49FBCBF21298A3CD</aesSecretKeyStr>"
			+ "<publicKeyStr>81f3de27fc2d4d7905b65d13d4e6137dffd11fb25570eee794ffb83df6e137ee0697550aa0eedbdec9173d35649e25cd55ce7dc9353506dfa8ba1af4ad992cfbddf119c4115f31ff1655f07c267365610e31c48fc69c6020149e2694d2606a9c8b9cda42ed245812fe45f35826b81d2803f117604ddb8ad147a245fc965b866e99cb2bfd6e4dc5c2fa57b322c30298ece77e18b93adc9c3279e506a49b0523bc6af943d7dd99f05c42176c617285321e73b84578d9bbdb96ba5d6e91b0d5ed1ee32547b9aab0d57408d4d84a9cdc8da181a2fe173781666b3432a2f9cebb3d49f968e383022923c12e9eb3d40501c879303f106f8a20eadd5366adb799ec9da7-10001"
			+ "</publicKeyStr>"
			+ "<licenceMetadataSpec>"
			+ "  <productId>myproject/1.0-SNAPSHOT</productId>"
			+ "  <featureRepository>org.opennms.project/myproject/1.0-SNAPSHOT/xml/features</featureRepository>"
			+ "  <licensee></licensee>"
			+ "  <licensor>OpenNMS UK</licensor>"
			+ "  <systemId></systemId>"
			+ "  <startDate>2015-08-18T16:50:08.541+01:00</startDate>"
			+ "  <duration>0</duration>"
			+ "  <options>"
			+ "    <option>"
			+ "      <description>this is the description of option 1</description>"
			+ "       <name>option1</name>"
			+ "      <value></value>"
			+ "    </option>"
			+ "   </options>"
			+ " </licenceMetadataSpec>"
			+ "</licenceSpecification>";

	private static String TEST_PROPERTIES_FILE="/licenceServiceTest.properties";
	
	private String basePath = "/licencemgr/rest/v1-0/licence-pub";
	
	//defaults for test running on standard karaf
	private String baseUrl = "http://localhost:8181";
	private String userName="admin";
	private String password="admin";
	
	// constructor loads test properties file if exists
	public LicencePublisherClientRestJerseyTest(){
		super();
		
		LOG.debug("LOADING PROPERTIES: LicenceManagerClientRestJerseyTest() from "+TEST_PROPERTIES_FILE);
		
		Properties prop = null;
        InputStream is = null;
        try {
            prop = new Properties();
            is = this.getClass().getResourceAsStream(TEST_PROPERTIES_FILE);
            prop.load(is);
            
    		baseUrl = prop.getProperty("baseUrl");
    		userName=  prop.getProperty("userName");
    		password= prop.getProperty("password");
 
        } catch (Exception e) {
        	LOG.error("     Using default values. Problem loading TEST_PROPERTIES_FILE:"+TEST_PROPERTIES_FILE+" Exception:",e);
        }
        
		LOG.debug("     basePath = "+basePath);
		
        LOG.debug("     baseUrl = "+baseUrl);
		LOG.debug("     userName= "+userName);
		LOG.debug("     password= "+password);
		
	}

	
	// initialises tests
	private LicencePublisherClient getLicencePublisherClient() {

		LicencePublisherClientRestJerseyImpl licencePublisherClient = new LicencePublisherClientRestJerseyImpl();
		licencePublisherClient.setBasePath(basePath);
		licencePublisherClient.setBaseUrl(baseUrl);
		licencePublisherClient.setUserName(userName);
		licencePublisherClient.setPassword(password);

		return licencePublisherClient;
	}

	@Test
	public void testsInOrder(){
		LOG.debug("@Test - LICENCE PUBLISHER TESTS.START");
		
		deleteLicenceSpecificationsTest();
        addLicenceSpecTest();
		getLicenceSpecListTest();
		getLicenceSpecTest();
		getLicenceMetadataTest();
        createLicenceInstanceStrTest();
        createMultiLicenceTest();
		removeLicenceSpecTest();
		deleteLicenceSpecificationsTest(); // final clean up
		getLicenceMetadataListTest(); //  just to confirm licences gone
		
		LOG.debug("@Test - LICENCE PUBLISHER TESTS.FINISH");
		
	}

	//@Test
	public void addLicenceSpecTest(){
		LOG.debug("@Test - addLicenceSpecTest().START");

		LicencePublisherClient productPublisherClient = getLicencePublisherClient();

		try {
			// load test product specification
			LicenceSpecification licenceSpec = (LicenceSpecification) Util.fromXml(test_licence_specification);
			
			productPublisherClient.addLicenceSpec(licenceSpec);
			
		} catch (Exception e) {
			LOG.error("@Test - addLicenceSpecTest() failed. Exception:",e);
			fail("@Test - addLicenceSpecTest() failed. See stack trace in error log");
		}

		LOG.debug("@Test - addLicenceSpecTest().FINISH");

	}

	//@Test
	public void removeLicenceSpecTest(){
		LOG.debug("@Test - removeLicenceSpecTest().START");

		LicencePublisherClient licencePublisherClient = getLicencePublisherClient();

		try {
			String productId=test_productId;
			licencePublisherClient.removeLicenceSpec(productId);
		} catch (Exception e) {
			LOG.error("@Test - removeLicenceSpecTest() failed. Exception:",e);
			fail("@Test - removeLicenceSpecTest() failed. See stack trace in error log");
		}

		LOG.debug("@Test - removeLicenceSpecTest().FINISH");

	}

	//@Test
	public void getLicenceSpecTest(){
		LOG.debug("@Test - getLicenceSpecTest().START");
		
		String productId=test_productId;

		LicencePublisherClient licencePublisherClient = getLicencePublisherClient();

		try {
			LicenceSpecification licenceSpecification = licencePublisherClient.getLicenceSpec(productId);
			LOG.debug(Util.toXml(licenceSpecification));

			// productPublisherClient
		} catch (Exception e) {
			LOG.error("@Test - getLicenceSpecTest() failed. Exception:",e);
			fail("@Test - getLicenceSpecTest() failed. See stack trace in error log");
		}

		LOG.debug("@Test - getLicenceSpecTest().FINISH");

	}

	//@Test
	public void getLicenceMetadataTest(){
		LOG.debug("@Test - getLicenceMetadataTest().START");
		
		String productId=test_productId;

		LicencePublisherClient licencePublisherClient = getLicencePublisherClient();

		try {
			LicenceMetadata licenceMetadata = licencePublisherClient.getLicenceMetadata(productId);
			LOG.debug(Util.toXml(licenceMetadata));
		} catch (Exception e) {
			LOG.error("@Test - getLicenceMetadataTest() failed. Exception:",e);
			fail("@Test - getLicenceMetadataTest() failed. See stack trace in error log");
		}

		LOG.debug("@Test - getLicenceMetadataTest().FINISH");

	}

	//@Test
	public void getLicenceSpecListTest(){
		LOG.debug("@Test - getLicenceSpecListTest().START");

		LicencePublisherClient licencePublisherClient = getLicencePublisherClient();

		try {
			LicenceSpecList licenceSpecList = licencePublisherClient.getLicenceSpecList();
			LOG.debug(Util.toXml(licenceSpecList));
		} catch (Exception e) {
			LOG.error("@Test - getListTest() failed. Exception:",e);
			fail("@Test - getListTest() failed. See stack trace in error log");
		}

		LOG.debug("@Test - getLicenceSpecListTest().FINISH");

	}

	//@Test
	public void getLicenceMetadataListTest(){
		LOG.debug("@Test - getLicenceMetadataListTest().START");

		LicencePublisherClient licencePublisherClient = getLicencePublisherClient();

		try {
			LicenceMetadataList licenceMetadataList = licencePublisherClient.getLicenceMetadataList();
			LOG.debug(Util.toXml(licenceMetadataList));
		} catch (Exception e) {
			LOG.error("@Test - getListTest() failed. Exception:",e);
			fail("@Test - getListTest() failed. See stack trace in error log");
		}

		LOG.debug("@Test - getLicenceMetadataListTest().FINISH");

	}

	//@Test
	public void deleteLicenceSpecificationsTest(){
		LOG.debug("@Test - deleteLicenceSpecificationsTest().START");

		LicencePublisherClient licencePublisherClient = getLicencePublisherClient();

		try {
			licencePublisherClient.deleteLicenceSpecifications(true);
		} catch (Exception e) {
			LOG.error("@Test - deleteLicenceSpecificationsTest() failed. Exception:",e);
			fail("@Test - deleteLicenceSpecificationsTest() failed. See stack trace in error log");
		}

		LOG.debug("@Test - cdeleteLicenceSpecificationsTest().FINISH");
	}


	//@Test
	public void createLicenceInstanceStrTest(){
		LOG.debug("@Test - createLicenceInstanceStrTest().START");

		LicencePublisherClient licencePublisherClient = getLicencePublisherClient();
		try {
			// load test product specification
			LicenceMetadata licenceMetadataSpec = (LicenceMetadata) Util.fromXml(test_create_licence_metadata);
			
			String licenceInstanceStr = licencePublisherClient.createLicenceInstanceStr(licenceMetadataSpec);
			
			LOG.debug("Licence instance="+licenceInstanceStr);
			
		} catch (Exception e) {
			LOG.error("@Test - createLicenceInstanceStrTest() failed. Exception:",e);
			fail("@Test - createLicenceInstanceStrTest() failed. See stack trace in error log");
		}
		
		LOG.debug("@Test - createLicenceInstanceStrTest().FINISH");

	}
	
	//@Test 
	public void createMultiLicenceTest(){
		LOG.debug("@Test - createMultiLicenceTest().START");

		LicencePublisherClient licencePublisherClient = getLicencePublisherClient();
		try {
			// load test product specification
			LicenceMetadata licenceMetadataSpec = (LicenceMetadata) Util.fromXml(test_create_licence_metadata);

			LicenceMetadataList licenceMetadataList = new LicenceMetadataList();
			licenceMetadataList.getLicenceMetadataList().add(licenceMetadataSpec);
			
			LicenceList licenceInstances = licencePublisherClient.createMultiLicenceInstance(licenceMetadataList);
			
			StringBuffer sb = new StringBuffer("Licence list received=");
			for (LicenceEntry licenceEntry:licenceInstances.getLicenceList()){
				sb.append("  productId:"+licenceEntry.getProductId()
						+ "  licence: \""+licenceEntry.getLicenceStr()+"\"\n");
			}
			LOG.debug(sb.toString());
			
		} catch (Exception e) {
			LOG.error("@Test - createMultiLicenceTest() failed. Exception:",e);
			fail("@Test - createMultiLicenceTest() failed. See stack trace in error log");
		}
		
		LOG.debug("@Test - createMultiLicenceTest().FINISH");

	}

}
