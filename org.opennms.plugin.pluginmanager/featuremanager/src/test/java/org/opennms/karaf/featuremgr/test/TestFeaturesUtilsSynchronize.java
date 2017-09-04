package org.opennms.karaf.featuremgr.test;


import static org.junit.Assert.*;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.karaf.features.FeaturesService;
import org.junit.Test;
import org.opennms.karaf.featuremgr.FeaturesUtils;
import org.opennms.karaf.featuremgr.jaxb.karaf.feature.Features;
import org.opennms.karaf.featuremgr.jaxb.karaf.feature.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestFeaturesUtilsSynchronize {
	private static final Logger LOG = LoggerFactory.getLogger(TestFeaturesUtilsSynchronize.class);
	String installedManifestUri = "./src/test/resources/jaxb/installed-manifest-features.xml";
	String manifestUri = "./src/test/resources/jaxb/manifest-features.xml";
	String emptyManifestUri = "./src/test/resources/jaxb/empty-manifest-features.xml";
	String targetInstalledManifestUri="./target/testoutput/installed-manifest-features.xml";

	@Test
	public void marshalUnmarshalFeaturesStringTest() {
		LOG.debug("start of  marshalUnmarshalFeaturesStringTest()" );
		
		String featuresStr = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
		+"<features name=\"manifest-features\" xmlns=\"http://karaf.apache.org/xmlns/features/v1.2.0\">"
		+"  <repository>mvn:org.opennms.karaf/opennms/21.0.0-SNAPSHOT/xml/features</repository>"
		+"  <feature name=\"manifest\" version=\"0.0.1-SNAPSHOT\" description=\"Manifest\">"
		+"  <feature>alarm-change-notifier</feature> "
		+"</feature>"
		+"</features>";
		
		Features features = FeaturesUtils.parseFeatures(featuresStr);
		
		String newFeaturesStr  = FeaturesUtils.featuresToString(features);
		
		LOG.debug("features string = "+newFeaturesStr);
		
		LOG.debug("end of  marshalUnmarshalFeaturesStringTest()" );
		
	}

	@Test 
	public void noInstalledAndEmptyManifestTest(){
		LOG.debug("start of noInstalledAndEmptyManifestTest()" );
		try{
			FeaturesService featuresService = new MockFeaturesService();

			File targetInstalledManifestFile = new File(targetInstalledManifestUri);
			targetInstalledManifestFile.delete(); 

			String msg = FeaturesUtils.synchronizeManifestFeaturesFiles(emptyManifestUri, targetInstalledManifestUri, featuresService);

			LOG.debug("message:\n"+ msg);
		} catch (Exception e){
			e.printStackTrace();
		}			
		LOG.debug("end of noInstalledAndEmptyManifestTest()" );

	}
	
	@Test 
	public void noInstalledNewManifestTest(){
		LOG.debug("start of noInstalledNewManifestTest()" );
		try{
			FeaturesService featuresService = new MockFeaturesService();

			File targetInstalledManifestFile = new File(targetInstalledManifestUri);
			targetInstalledManifestFile.delete(); 

			String msg = FeaturesUtils.synchronizeManifestFeaturesFiles(manifestUri, targetInstalledManifestUri, featuresService);

			LOG.debug("message:\n"+ msg);
		} catch (Exception e){
			e.printStackTrace();
		}
		LOG.debug("end of noInstalledNewManifestTest()" );

	}


	@Test
	public void replaceInstalledManifestFeaturesTests(){
		LOG.debug("start of replaceInstalledManifestFeaturesTests()" );
		
		setUpInstalledManifestUri();

		try{
			FeaturesService featuresService = new MockFeaturesService();

			String msg = FeaturesUtils.synchronizeManifestFeaturesFiles(manifestUri, targetInstalledManifestUri, featuresService);

			LOG.debug("message:\n"+ msg);
		} catch (Exception e){
			e.printStackTrace();
		}
		LOG.debug("end of replaceInstalledManifestFeaturesTests()" );
		
	}

	
	
	public void setUpInstalledManifestUri(){
		try{
			File testFeaturesFile = new File(installedManifestUri);
			LOG.debug("reading test file at:"+testFeaturesFile.getAbsolutePath());
			Features features = FeaturesUtils.loadFeaturesFile(testFeaturesFile);

			File tempFeaturesFile = new File(targetInstalledManifestUri);
			LOG.debug("writing test output file at:"+tempFeaturesFile.getAbsolutePath());

			tempFeaturesFile.getParentFile().mkdirs(); // creates directory

			FeaturesUtils.persistFeaturesFile(features, tempFeaturesFile);

		} catch (Exception e){
			e.printStackTrace();
		}

	}




}

