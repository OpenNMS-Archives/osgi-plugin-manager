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
import java.util.Properties;

import org.junit.Test;
import org.opennms.karaf.licencemgr.metadata.jaxb.ProductMetadata;
import org.opennms.karaf.licencemgr.metadata.jaxb.Util;
import org.opennms.karaf.licencemgr.metadata.jaxb.ProductSpecList;
import org.opennms.karaf.licencemgr.rest.client.ProductPublisherClient;
import org.opennms.karaf.licencemgr.rest.client.jerseyimpl.ProductPublisherClientRestJerseyImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProductPublisherClientRestJerseyTest {
	private static final Logger LOG = LoggerFactory.getLogger(ProductPublisherClientRestJerseyTest.class);
	
	private static String test_productId="myproject/1.0-SNAPSHOT";
	
	private static String test_productMetadata="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
					+ "<product>\n"
					+ "	<productId>myproject/1.0-SNAPSHOT</productId>\n"
					+ "	<featureRepository>org.opennms.project/myproject/1.0-SNAPSHOT/xml/features</featureRepository>\n"
					+ "	<productName>test Bundle</productName>\n"
					+ "	<productDescription>Test product description</productDescription>\n"
					+ "	<productUrl>http:\\opennms.co.uk</productUrl>\n"
					+ "	<organization>OpenNMS Project</organization>\n"
					+ "	<licenceType>OpenNMS EULA See http:\\\\opennms.co.uk\\EULA</licenceType>\n"
					+ "	<licenceKeyRequired>true</licenceKeyRequired>\n"
					+ "</product>\n";

	private static String TEST_PROPERTIES_FILE="/licenceServiceTest.properties";
	
	private String basePath = "/licencemgr/rest/v1-0/product-pub";
	
	//defaults for test running on standard karaf
	private String baseUrl = "http://localhost:8181";
	private String userName="admin";
	private String password="admin";
	
	// constructor loads test properties file if exists
	public ProductPublisherClientRestJerseyTest(){
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
	private ProductPublisherClient getProductPublisherClient() {

		ProductPublisherClientRestJerseyImpl productPublisherClient = new ProductPublisherClientRestJerseyImpl();
		productPublisherClient.setBasePath(basePath);
		productPublisherClient.setBaseUrl(baseUrl);
		productPublisherClient.setUserName(userName);
		productPublisherClient.setPassword(password);

		return productPublisherClient;
	}

	@Test
	public void testsInOrder(){
		LOG.debug("@Test - PRODUCT PUBLISHER TESTS.START");
		addProductSpecTest();
		getListTest();
		getProductSpecTest();
		removeProductSpecTest();
		clearProductSpecsTest();
		getListTest(); //  just to confirm product gone
		LOG.debug("@Test - PRODUCT PUBLISHER TESTS.FINISH");
		
	}
	
	//@Test
	public void addProductSpecTest() {
		LOG.debug("@Test - addProductSpecTest.START");

		ProductPublisherClient productPublisherClient = getProductPublisherClient();

		try {
			// load test product specification
			ProductMetadata productMetadata = (ProductMetadata) Util.fromXml(test_productMetadata);
			
			productPublisherClient.addProductSpec(productMetadata);
			
		} catch (Exception e) {
			LOG.error("@Test - addProductSpecTest() failed. Exception:",e);
			fail("@Test - addProductSpecTest() failed. See stack trace in error log");
		}

		LOG.debug("@Test - addProductSpecTest.FINISH");
	}
	

	//@Test
	public void getListTest() {
		LOG.debug("@Test - getListTest.START");

		ProductPublisherClient productPublisherClient = getProductPublisherClient();

		try {
			ProductSpecList productSpecList = productPublisherClient.getList();
			LOG.debug(Util.toXml(productSpecList));
		} catch (Exception e) {
			LOG.error("@Test - getListTest()failed. Exception:",e);
			fail("@Test - getListTest() failed. See stack trace in error log");
		}

		LOG.debug("@Test - getListTest.FINISH");
	}
	
	
	//@Test
	public void getProductSpecTest() {
		LOG.debug("@Test - getProductSpecTest.START");
		
		String productId=test_productId;

		ProductPublisherClient productPublisherClient = getProductPublisherClient();

		try {
			ProductMetadata productMetadata = productPublisherClient.getProductSpec(productId);
			LOG.debug(Util.toXml(productMetadata));

			// productPublisherClient
		} catch (Exception e) {
			LOG.error("@Test - getProductSpecTest() failed. Exception:",e);
			fail("@Test - getProductSpecTest() failed. See stack trace in error log");
		}

		LOG.debug("@Test - getProductSpecTest.FINISH");
	}

	
	//@Test
	public void removeProductSpecTest() {
		LOG.debug("@Test - removeProductSpecTest.START");

		ProductPublisherClient productPublisherClient = getProductPublisherClient();

		try {
			String productId=test_productId;
			productPublisherClient.removeProductSpec(productId);
		} catch (Exception e) {
			LOG.error("@Test - removeProductSpecTest() failed. Exception:",e);
			fail("@Test - removeProductSpecTest() failed. See stack trace in error log");
		}

		LOG.debug("@Test - removeProductSpecTest.FINISH");
	}
	

	//@Test
	public void clearProductSpecsTest() {
		LOG.debug("@Test - clearProductSpecTest.START");

		ProductPublisherClient productPublisherClient = getProductPublisherClient();

		try {
			 productPublisherClient.clearProductSpecs(true);
		} catch (Exception e) {
			LOG.error("@Test - clearProductSpecTest() failed. Exception:",e);
			fail("@Test - clearProductSpecTest() failed. See stack trace in error log");
		}

		LOG.debug("@Test - clearProductSpecTest.FINISH");
	}

}
