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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test of jersey web client implementation
 * @author admin
 *
 */
public class FeaturesServiceClientRestJerseyTest {
	private static final Logger LOG = LoggerFactory.getLogger(FeaturesServiceClientRestJerseyTest.class);
	
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
	
	String manifestStr = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
			+"<features name=\"manifest-features\" xmlns=\"http://karaf.apache.org/xmlns/features/v1.2.0\">"
			+"  <repository>mvn:org.apache.activemq/activemq-karaf/5.14.5/xml/features</repository>"
			+"  <feature name=\"manifest\" version=\"1.0-SNAPSHOT\" description=\"Manifest\">"
			+"</feature>"
			+"</features>";
	
	// constructor loads test properties file if exists
	public FeaturesServiceClientRestJerseyTest(){
		super();
		
		LOG.debug("LOADING PROPERTIES: FeaturesServiceClientRestJerseyTest() from "+TEST_PROPERTIES_FILE);
		
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
        	LOG.error("     Using default values. Problem loading TEST_PROPERTIES_FILE:"+TEST_PROPERTIES_FILE+" Exception:",e);
        }

        LOG.debug("     baseUrl = "+baseUrl);
		LOG.debug("     basePath = "+basePath);
		LOG.debug("     userName= "+userName);
		LOG.debug("     password= "+password);
		LOG.debug("     test feature uriStr=" +uriStr);
		LOG.debug("     test feature name=" +name);
		LOG.debug("     test feature version=" +version);
		
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
		LOG.debug("@Test - LICENCE MANAGER TESTS.START");
		
		this.featuresAddRepository();
		this.getFeaturesListRepositories();
		this.getFeaturesRepositoryInfo();
		this.getFeaturesList();
		this.featuresInstall();
		this.getFeaturesInfo();
		this.featuresUninstall();
		this.featuresRemoveRepository();
		this.featuresSynchronizeManifest();
		this.featuresUninstallManifest();
		
		LOG.debug("@Test - LICENCE MANAGER TESTS.FINISH");
	}

	
	
	//@Test
	public void featuresAddRepository() {
		LOG.debug("@Test - featuresAddRepository.START");

		//http://localhost:8181/featuremgr/rest/v1-0/features-addrepositoryurl?uri=mvn:org.opennms.project/myproject.Feature/1.0-SNAPSHOT/xml/features

		FeaturesServiceClient featuresService = getFeaturesService(); 
		try {
			featuresService.featuresAddRepository(uriStr);
		} catch (Exception e) {
			e.printStackTrace();
			fail("@Test - featuresAddRepository() failed. See stack trace in consol");
		}
		
		LOG.debug("@Test - featuresAddRepository.FINISH");
		
	}
	
	//@Test
	public void getFeaturesListRepositories() {
		LOG.debug("@Test - getFeaturesListRepositories.START");
		
		FeaturesServiceClient featuresService = getFeaturesService(); 
		try {
			RepositoryList repositoryList = featuresService.getFeaturesListRepositories();
			LOG.debug(Util.toXml(repositoryList));
		} catch (Exception e) {
			LOG.error("problem in test getFeaturesListRepositories(). Exception:",e);
			fail("@Test - getFeaturesListRepositories() failed. See stack trace in consol");
		}

		LOG.debug("@Test - getFeaturesListRepositories.FINISH");
		
	}

	//@Test
	public void getFeaturesRepositoryInfo() {
		LOG.debug("@Test - getFeaturesRepositoryInfo.START");
		
		String name=null;

		//http://localhost:8181/featuremgr/rest/v1-0/features-repositoryinfo?uri=mvn:org.opennms.project/myproject.Feature/1.0-SNAPSHOT/xml/features
				
		FeaturesServiceClient featuresService = getFeaturesService(); 
		try {
			RepositoryWrapperJaxb repositoryWrapper = featuresService.getFeaturesRepositoryInfo(name, uriStr);
			LOG.debug(Util.toXml(repositoryWrapper));
		} catch (Exception e) {
			LOG.error("problem in test getFeaturesRepositoryInfo. Exception:",e);
			e.printStackTrace();
			fail("@Test - getFeaturesRepositoryInfo failed. See stack trace in consol");
		}

		LOG.debug("@Test - getFeaturesRepositoryInfo.FINISH");
		
	}

	
	//@Test
	public void getFeaturesList() {

		LOG.debug("@Test - getFeaturesList. START");
		
		FeaturesServiceClient featuresService = getFeaturesService(); 
		try {
			FeatureList featuresList = featuresService.getFeaturesList();
			LOG.debug(Util.toXml(featuresList));
		} catch (Exception e) {
			LOG.error("problem in test getFeaturesList(). Exception:",e);
			fail("@Test - getFeaturesList() failed. See stack trace in consol");
		}
		
		LOG.debug("@Test - getFeaturesList.FINISH");
		
	}

	//@Test
	public void featuresInstall() {
		LOG.debug("@Test - featuresInstall.START");
				
		FeaturesServiceClient featuresService = getFeaturesService(); 
		try {
			featuresService.featuresInstall(name, version);
		} catch (Exception e) {
			LOG.error("problem in test featuresInstall(). Exception:",e);
			fail("@Test - featuresInstall() failed. See stack trace in consol");
		}
		
		LOG.debug("@Test - featuresInstall.FINISH");

	}

	//@Test
	public void getFeaturesInfo() {
		
		LOG.debug("@Test - getFeaturesInfo.START");
		
		//http://localhost:8181/featuremgr/rest/v1-0/features-info?name=myproject.Feature&version=1.0-SNAPSHOT

		FeaturesServiceClient featuresService = getFeaturesService(); 
		try {
			FeatureWrapperJaxb featureWrapper = featuresService.getFeaturesInfo(name, version);
			LOG.debug(Util.toXml(featureWrapper));
		} catch (Exception e) {
			LOG.error("problem in test getFeaturesInfo(). Exception:",e);
			fail("@Test - getFeaturesInfo() failed. See stack trace in consol");
		}
		
		LOG.debug("@Test - getFeaturesInfo.FINISH");

	}

	//@Test
	public void featuresUninstall() {
		LOG.debug("@Test - featuresUninstall.START");
				
		FeaturesServiceClient featuresService = getFeaturesService(); 
		try {
			featuresService.featuresUninstall(name, version);
		} catch (Exception e) {
			LOG.error("problem in test featuresUninstall(). Exception:",e);
			fail("@Test - featuresUninstall() failed. See stack trace in consol");
		}
		LOG.debug("@Test - featuresUninstall.FINISH");
	}


	//@Test
	public void featuresRemoveRepository() {
		LOG.debug("@Test - featuresRemoveRepository.START");
		
		//http://localhost:8181/featuremgr/rest/v1-0/features-removerepository?uri=mvn:org.opennms.project/myproject.Feature/1.0-SNAPSHOT/xml/features
				
		FeaturesServiceClient featuresService = getFeaturesService(); 
		try {
			featuresService.featuresRemoveRepository(uriStr);
		} catch (Exception e) {
			LOG.error("problem in test featuresRemoveRepository(). Exception:",e);
			fail("@Test - featuresRemoveRepository() failed. See stack trace in consol");
		}
		
		LOG.debug("@Test - featuresRemoveRepository.FINISH");
	}
	
	//@Test
	public void featuresSynchronizeManifest() {
		LOG.debug("@Test - featuresSynchronizeManifest.START");

		//http://localhost:8181/featuremgr/rest/v1-0/features-synchronizemanifest

		FeaturesServiceClient featuresService = getFeaturesService(); 
		try {
			LOG.debug("@Test - featuresSynchronizeManifest trying to deploy manifestStr="+manifestStr);
			featuresService.featuresSynchronizeManifest(manifestStr);
		} catch (Exception e) {
			LOG.error("problem in test featuresSynchronizeManifest(). Exception:",e);
			fail("@Test - featuresSynchronizeManifest() failed. See stack trace in consol");
		}
		
		LOG.debug("@Test - featuresSynchronizeManifest.FINISH");
	}
	
	//@Test
	public void featuresUninstallManifest() {
		LOG.debug("@Test - featuresUninstallManifest.START");

		//http://localhost:8181/featuremgr/rest/v1-0/features-uninstallmanifest

		FeaturesServiceClient featuresService = getFeaturesService(); 
		try {
			LOG.debug("@Test - featuresUninstallManifest trying to uninstall manifest");
			featuresService.featuresUninstallManifest();
		} catch (Exception e) {
			LOG.error("problem in test featuresUninstallManifest(). Exception:",e);
			fail("@Test - featuresUninstallManifest() failed. See stack trace in consol");
		}
		
		LOG.debug("@Test - featuresUninstallManifest.FINISH");
	}

}
