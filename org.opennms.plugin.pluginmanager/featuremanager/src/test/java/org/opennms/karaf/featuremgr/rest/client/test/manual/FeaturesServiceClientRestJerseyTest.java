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

package org.opennms.karaf.featuremgr.rest.client.test.manual;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.Properties;

import org.junit.Test;
import org.opennms.karaf.featuremgr.jaxb.FeatureList;
import org.opennms.karaf.featuremgr.jaxb.FeatureWrapperJaxb;
import org.opennms.karaf.featuremgr.jaxb.RepositoryList;
import org.opennms.karaf.featuremgr.jaxb.RepositoryWrapperJaxb;
import org.opennms.karaf.featuremgr.jaxb.Util;
import org.opennms.karaf.featuremgr.rest.client.FeaturesServiceClient;
import org.opennms.karaf.featuremgr.rest.client.jerseyimpl.FeaturesServiceClientRestJerseyImpl;

/**
 * Test of jersey web client implementation
 * @author admin
 *
 */
public class FeaturesServiceClientRestJerseyTest {
	
	private static String TEST_PROPERTIES_FILE="/featuresServiceTest.properties";
	
	//defaults for test running on standard karaf
	private String baseUrl = "http://localhost:8181";
	private String basePath = "/featuremgr";
	private String userName="admin";
	private String password="admin";
	
	// name of test feature to be loaded
	private String uriStr="mvn:org.opennms.project/myproject.Feature/1.0-SNAPSHOT/xml/features";
	private String name="myproject.Feature";
	private String version="1.0-SNAPSHOT";
	
	// constructor loads test properties file if exists
	public FeaturesServiceClientRestJerseyTest(){
		super();
		
		System.out.println("LOADING PROPERTIES: FeaturesServiceClientRestJerseyTest() from "+TEST_PROPERTIES_FILE);
		
		Properties prop = null;
        InputStream is = null;
        try {
            prop = new Properties();
            is = this.getClass().getResourceAsStream(TEST_PROPERTIES_FILE);
            prop.load(is);
            
    		baseUrl = prop.getProperty("baseUrl");
    		basePath =  prop.getProperty("basePath");
    		userName=  prop.getProperty("userName");
    		password= prop.getProperty("password");
    		uriStr= prop.getProperty("uriStr");
    		name= prop.getProperty("name");
    		version= prop.getProperty("version");
 
        } catch (Exception e) {
        	System.out.println("     Using defailt values. Problem loading TEST_PROPERTIES_FILE:"+TEST_PROPERTIES_FILE+" Exception:"+e);
        }

        System.out.println("     baseUrl = "+baseUrl);
		System.out.println("     basePath = "+basePath);
		System.out.println("     userName= "+userName);
		System.out.println("     password= "+password);
		System.out.println("     test feature uriStr=" +uriStr);
		System.out.println("     test feature name=" +name);
		System.out.println("     test feature version=" +version);
		
	}

	// initialises tests
	private FeaturesServiceClient getFeaturesService(){
		
		FeaturesServiceClientRestJerseyImpl jerseyFeaturesService = new FeaturesServiceClientRestJerseyImpl(); 
		jerseyFeaturesService.setBasePath(basePath);
		jerseyFeaturesService.setBaseUrl(baseUrl);
		jerseyFeaturesService.setUserName(userName);
		jerseyFeaturesService.setPassword(password);
		
		return jerseyFeaturesService;
	}
	
	
	@Test
	public void testsInOrder(){
		System.out.println("@Test - LICENCE MANAGER TESTS.START");
		
		this.featuresAddRepository();
		this.getFeaturesListRepositories();
		this.getFeaturesRepositoryInfo();
		this.getFeaturesList();
		this.featuresInstall();
		this.getFeaturesInfo();
		this.featuresUninstall();
		this.featuresRemoveRepository();		
		
		System.out.println("@Test - LICENCE MANAGER TESTS.FINISH");
	}

	
	
	//@Test
	public void featuresAddRepository() {
		System.out.println("@Test - featuresAddRepository.START");

		//http://localhost:8181/featuremgr/rest/features-addrepositoryurl?uri=mvn:org.opennms.project/myproject.Feature/1.0-SNAPSHOT/xml/features

		FeaturesServiceClient featuresService = getFeaturesService(); 
		try {
			featuresService.featuresAddRepository(uriStr);
		} catch (Exception e) {
			e.printStackTrace();
			fail("@Test - featuresAddRepository() failed. See stack trace in consol");
		}
		
		System.out.println("@Test - featuresAddRepository.FINISH");
		
	}
	
	//@Test
	public void getFeaturesListRepositories() {
		System.out.println("@Test - getFeaturesListRepositories.START");
		
		FeaturesServiceClient featuresService = getFeaturesService(); 
		try {
			RepositoryList repositoryList = featuresService.getFeaturesListRepositories();
			System.out.println(Util.toXml(repositoryList));
		} catch (Exception e) {
			e.printStackTrace();
			fail("@Test - getFeaturesListRepositories() failed. See stack trace in consol");
		}

		System.out.println("@Test - getFeaturesListRepositories.FINISH");
		
	}

	//@Test
	public void getFeaturesRepositoryInfo() {
		System.out.println("@Test - getFeaturesRepositoryInfo.START");
		
		String name=null;

		//http://localhost:8181/featuremgr/rest/features-repositoryinfo?uri=mvn:org.opennms.project/myproject.Feature/1.0-SNAPSHOT/xml/features
				
		FeaturesServiceClient featuresService = getFeaturesService(); 
		try {
			RepositoryWrapperJaxb repositoryWrapper = featuresService.getFeaturesRepositoryInfo(name, uriStr);
			System.out.println(Util.toXml(repositoryWrapper));
		} catch (Exception e) {
			e.printStackTrace();
			fail("@Test - getFeaturesRepositoryInfo failed. See stack trace in consol");
		}

		System.out.println("@Test - getFeaturesRepositoryInfo.FINISH");
		
	}

	
	//@Test
	public void getFeaturesList() {

		System.out.println("@Test - getFeaturesList. START");
		
		FeaturesServiceClient featuresService = getFeaturesService(); 
		try {
			FeatureList featuresList = featuresService.getFeaturesList();
			System.out.println(Util.toXml(featuresList));
		} catch (Exception e) {
			e.printStackTrace();
			fail("@Test - getFeaturesList() failed. See stack trace in consol");
		}
		
		System.out.println("@Test - getFeaturesList.FINISH");
		
	}

	//@Test
	public void featuresInstall() {
		System.out.println("@Test - featuresInstall.START");
				
		FeaturesServiceClient featuresService = getFeaturesService(); 
		try {
			featuresService.featuresInstall(name, version);
		} catch (Exception e) {
			e.printStackTrace();
			fail("@Test - featuresInstall() failed. See stack trace in consol");
		}
		
		System.out.println("@Test - featuresInstall.FINISH");

	}

	//@Test
	public void getFeaturesInfo() {
		
		System.out.println("@Test - getFeaturesInfo.START");
		
		//http://localhost:8181/featuremgr/rest/features-info?name=myproject.Feature&version=1.0-SNAPSHOT

		FeaturesServiceClient featuresService = getFeaturesService(); 
		try {
			FeatureWrapperJaxb featureWrapper = featuresService.getFeaturesInfo(name, version);
			System.out.println(Util.toXml(featureWrapper));
		} catch (Exception e) {
			e.printStackTrace();
			fail("@Test - getFeaturesInfo() failed. See stack trace in consol");
		}
		
		System.out.println("@Test - getFeaturesInfo.FINISH");

	}

	//@Test
	public void featuresUninstall() {
		System.out.println("@Test - featuresUninstall.START");
				
		FeaturesServiceClient featuresService = getFeaturesService(); 
		try {
			featuresService.featuresUninstall(name, version);
		} catch (Exception e) {
			e.printStackTrace();
			fail("@Test - featuresUninstall() failed. See stack trace in consol");
		}
		System.out.println("@Test - featuresUninstall.FINISH");
	}


	//@Test
	public void featuresRemoveRepository() {
		System.out.println("@Test - featuresRemoveRepository.START");
		
		//http://localhost:8181/featuremgr/rest/features-removerepository?uri=mvn:org.opennms.project/myproject.Feature/1.0-SNAPSHOT/xml/features
				
		FeaturesServiceClient featuresService = getFeaturesService(); 
		try {
			featuresService.featuresRemoveRepository(uriStr);
		} catch (Exception e) {
			e.printStackTrace();
			fail("@Test - featuresRemoveRepository() failed. See stack trace in consol");
		}
		
		System.out.println("@Test - featuresRemoveRepository.FINISH");
	}


}
