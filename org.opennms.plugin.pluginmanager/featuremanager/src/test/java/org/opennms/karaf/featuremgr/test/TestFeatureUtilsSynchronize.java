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
import org.opennms.karaf.featuremgr.FeatureUtils;
import org.opennms.karaf.featuremgr.jaxb.karaf.feature.Features;
import org.opennms.karaf.featuremgr.jaxb.karaf.feature.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestFeatureUtilsSynchronize {
	private static final Logger LOG = LoggerFactory.getLogger(TestFeatureUtilsSynchronize.class);
	String installedManifestUri = "./src/test/resources/jaxb/installed-manifest-features.xml";
	String manifestUri = "./src/test/resources/jaxb/manifest-features.xml";
	String emptyManifestUri = "./src/test/resources/jaxb/empty-manifest-features.xml";
	String targetInstalledManifestUri="./target/testoutput/installed-manifest-features.xml";

	
	@Test 
	public void noInstalledManifest(){
		FeaturesService featuresService = new MockFeaturesService();
		
		File targetInstalledManifestFile = new File(targetInstalledManifestUri);
		targetInstalledManifestFile.delete(); 
		
		String msg = FeatureUtils.synchronizeManifestFeaturesFiles(emptyManifestUri, targetInstalledManifestUri, featuresService);
		
	}
	

	@Test
	public void orderedTests(){
		setUpInstalledManifestUri();
	}

	public void setUpInstalledManifestUri(){
		try{
			File testFeaturesFile = new File(installedManifestUri);
			LOG.debug("reading test file at:"+testFeaturesFile.getAbsolutePath());
			Features features = FeatureUtils.loadFeaturesfile(testFeaturesFile);

			File tempFeaturesFile = new File(targetInstalledManifestUri);
			LOG.debug("writing test output file at:"+tempFeaturesFile.getAbsolutePath());

			tempFeaturesFile.getParentFile().mkdirs(); // creates directory

			FeatureUtils.persistFeaturesfile(features, tempFeaturesFile);

		} catch (Exception e){
			e.printStackTrace();
		}

	}




}

