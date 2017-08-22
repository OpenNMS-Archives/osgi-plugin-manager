package org.opennms.karaf.featuremgr.test;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;
import org.opennms.karaf.featuremgr.ManifestUtils;

public class ManifestUtilsTest {

	@Test
	public void test() {
		String csvManifestString = "featureA,featureB/1.0.0-SNAPSHOT,featureC/1.0.0";
		
		Map<String, String> manifestMap = ManifestUtils.csvStringToManifestMap(csvManifestString);
		
		String returnedCsvManifestString = ManifestUtils.manifestMapToCsvString(manifestMap);
		
		assertEquals(csvManifestString,returnedCsvManifestString);
	}
	
	

}
