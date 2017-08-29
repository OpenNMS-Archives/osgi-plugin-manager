package org.opennms.karaf.featuremgr.test;


import static org.junit.Assert.*;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.opennms.karaf.featuremgr.FeatureUtils;
import org.opennms.karaf.featuremgr.jaxb.karaf.feature.Features;
import org.opennms.karaf.featuremgr.jaxb.karaf.feature.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestFeatureUtilsPersistance {
	private static final Logger LOG = LoggerFactory.getLogger(TestFeatureUtilsPersistance.class);
	String testfileUri = "./src/test/resources/jaxb/test-manifest-features.xml";
	String testOutputFileUri="./target/testoutput/test-manifest-features.xml";

	@Test
	public void orderedTests(){
		testLoadAndSaveFeatureFile();
	}
	
	public void testLoadAndSaveFeatureFile(){
		try{
		File testFeaturesFile = new File(testfileUri);
		LOG.debug("reading test file at:"+testFeaturesFile.getAbsolutePath());
		Features features = FeatureUtils.loadFeaturesfile(testFeaturesFile);
		
		File tempFeaturesFile = new File(testOutputFileUri);
		LOG.debug("writing test output file at:"+tempFeaturesFile.getAbsolutePath());
		
		tempFeaturesFile.getParentFile().mkdirs(); // creates directory
		
		FeatureUtils.persistFeaturesfile(features, tempFeaturesFile);
		
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}


}

