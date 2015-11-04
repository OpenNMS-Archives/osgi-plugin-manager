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
import org.opennms.karaf.licencemgr.rest.client.ProductRegisterClient;
import org.opennms.karaf.licencemgr.rest.client.jerseyimpl.ProductRegisterClientRestJerseyImpl;


public class ProductRegisterClientRestJerseyTest {
	
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
	
	//TODO THIS WAS wrong /licencemgr/rest/product-pub
	private String basePath = "/licencemgr/rest/product-reg";
	
	//defaults for test running on standard karaf
	private String baseUrl = "http://localhost:8181";
	private String userName="admin";
	private String password="admin";
	
	// constructor loads test properties file if exists
	public ProductRegisterClientRestJerseyTest(){
		super();
		
		System.out.println("LOADING PROPERTIES: LicenceManagerClientRestJerseyTest() from "+TEST_PROPERTIES_FILE);
		
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
        	System.out.println("     Using defailt values. Problem loading TEST_PROPERTIES_FILE:"+TEST_PROPERTIES_FILE+" Exception:"+e);
        }
        
		System.out.println("     basePath = "+basePath);
		
        System.out.println("     baseUrl = "+baseUrl);
		System.out.println("     userName= "+userName);
		System.out.println("     password= "+password);
		
	}
	
	
	// initialises tests
	private ProductRegisterClient getProductRegisterClient() {

		ProductRegisterClientRestJerseyImpl productRegisterClient = new ProductRegisterClientRestJerseyImpl();
		productRegisterClient.setBasePath(basePath);
		productRegisterClient.setBaseUrl(baseUrl);
		productRegisterClient.setUserName(userName);
		productRegisterClient.setPassword(password);

		return productRegisterClient;
	}

	@Test
	public void testsInOrder(){
		System.out.println("@Test - PRODUCT REGISTER TESTS.START");
		addProductSpecTest();
		getListTest();
		getProductSpecTest();
		removeProductSpecTest();
		clearProductSpecsTest();
		getListTest(); //  just to confirm product gone
		System.out.println("@Test - PRODUCT REGISTER TESTS.FINISH");
	}
	
	//@Test
	public void addProductSpecTest() {
		System.out.println("@Test - addProductSpecTest.START");

		ProductRegisterClient productRegisterClient = getProductRegisterClient();

		try {
			// load test product specification
			ProductMetadata productMetadata = (ProductMetadata) Util.fromXml(test_productMetadata);
			
			productRegisterClient.addProductSpec(productMetadata);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("@Test - addProductSpecTest() failed. See stack trace in consol");
		}

		System.out.println("@Test - addProductSpecTest.FINISH");
	}
	

	//@Test
	public void getListTest() {
		System.out.println("@Test - getListTest.START");

		ProductRegisterClient productRegisterClient = getProductRegisterClient();

		try {
			ProductSpecList repositoryList = productRegisterClient.getList();
			System.out.println(Util.toXml(repositoryList));
		} catch (Exception e) {
			e.printStackTrace();
			fail("@Test - getListTest() failed. See stack trace in consol");
		}

		System.out.println("@Test - getListTest.FINISH");
	}
	
	
	//@Test
	public void getProductSpecTest() {
		System.out.println("@Test - getProductSpecTest.START");
		
		String productId=test_productId;

		ProductRegisterClient productRegisterClient = getProductRegisterClient();

		try {
			ProductMetadata productMetadata = productRegisterClient.getProductSpec(productId);
			System.out.println(Util.toXml(productMetadata));

			// productRegisterClient
		} catch (Exception e) {
			e.printStackTrace();
			fail("@Test - getProductSpecTest() failed. See stack trace in consol");
		}

		System.out.println("@Test - getProductSpecTest.FINISH");
	}

	
	//@Test
	public void removeProductSpecTest() {
		System.out.println("@Test - removeProductSpecTest.START");

		ProductRegisterClient productRegisterClient = getProductRegisterClient();

		try {
			String productId=test_productId;
			productRegisterClient.removeProductSpec(productId);
		} catch (Exception e) {
			e.printStackTrace();
			fail("@Test - removeProductSpecTest() failed. See stack trace in consol");
		}

		System.out.println("@Test - removeProductSpecTest.FINISH");
	}
	

	//@Test
	public void clearProductSpecsTest() {
		System.out.println("@Test - clearProductSpecTest.START");

		ProductRegisterClient productRegisterClient = getProductRegisterClient();

		try {
			 productRegisterClient.clearProductSpecs(true);
		} catch (Exception e) {
			e.printStackTrace();
			fail("@Test - clearProductSpecTest() failed. See stack trace in consol");
		}

		System.out.println("@Test - clearProductSpecTest.FINISH");
	}

}
