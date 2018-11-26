package org.opennms.karaf.featuremgr.manifest.client.test.jerseyimpl.manual;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opennms.karaf.featuremgr.manifest.client.jerseyimpl.ManifestServiceClientRestJerseyImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManifestServiceClientRestJerseyTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(ManifestServiceClientRestJerseyTest.class);

	
	//defaults for test running on standard karaf
	private String baseUrl = "http://localhost:8181"; 
	private String basePath = "/pluginmgr";
	private String userName="admin";
	private String password="admin";
	private String karafInstance="localhost";
	
	// initialises tests
	private ManifestServiceClientRestJerseyImpl getManifestService(){
		
		ManifestServiceClientRestJerseyImpl manifestServiceClient = new ManifestServiceClientRestJerseyImpl(); 
		manifestServiceClient.setBasePath(basePath);
		manifestServiceClient.setBaseUrl(baseUrl);
		if(userName!=null && ! "".equals(userName)) manifestServiceClient.setUserName(userName);
		if(password!=null) manifestServiceClient.setPassword(password);
		
		return manifestServiceClient;
	}

	@Test
	public void test() {
		ManifestServiceClientRestJerseyImpl manifestServiceClient =getManifestService();
		try {
			String manifest = manifestServiceClient.getFeatureManifest(karafInstance);
			LOG.debug("manifest="+manifest);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
