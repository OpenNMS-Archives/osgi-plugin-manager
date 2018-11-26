package org.opennms.karaf.licencemgr.test;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import org.opennms.karaf.licencemgr.LicenceManagerController;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadata;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadataList;
import org.opennms.karaf.licencemgr.metadata.jaxb.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LicenceManagerControllerTest {
	private static final Logger LOG = LoggerFactory.getLogger(LicenceManagerControllerTest.class);
	
	private static final String TARGET_TEST_METADATA_FILE="target/test-output/testLicenceMetadataList.xml";
	
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

	@Test
	public void testSaveLoadLicenceMetadataFile() {
		
		String licenceRequestMetadataFile = TARGET_TEST_METADATA_FILE;
		LicenceManagerController controller =new LicenceManagerController();
		controller.setLicenceRequestMetadataFile(licenceRequestMetadataFile);
		
		// create metadate and write out
		try {
			File testfile = new File(licenceRequestMetadataFile);
			LOG.debug("writing test licence metadata list to:"+testfile.getAbsolutePath());
			testfile.getParentFile().mkdirs(); // create parent output directory if doesn't exist
			testfile.createNewFile();
			
			// load test product specification
			LicenceMetadata licenceMetadataSpec = (LicenceMetadata) Util.fromXml(test_create_licence_metadata);
			LicenceMetadataList savedLicenceMetadataList = new LicenceMetadataList();
			savedLicenceMetadataList.getLicenceMetadataList().add(licenceMetadataSpec);
			controller.saveLicenceMetadataListFile(savedLicenceMetadataList);
			
			LOG.debug("reading test metadata list from:"+testfile.getAbsolutePath());
			LicenceMetadataList loadedLicenceMetadataList = controller.loadLicenceMetadataListFile();
			
			assertEquals(savedLicenceMetadataList,loadedLicenceMetadataList);
				
		} catch (Exception e) {
			LOG.error("@Test - testSaveLoadLicenceMetadataFile() failed. Exception:",e);
			fail("@Test - testSaveLoadLicenceMetadataFile() failed. See stack trace in error log");
		}
		

	}

}
