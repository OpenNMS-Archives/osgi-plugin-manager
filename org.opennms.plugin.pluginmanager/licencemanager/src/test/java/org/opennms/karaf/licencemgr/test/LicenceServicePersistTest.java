package org.opennms.karaf.licencemgr.test;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import org.opennms.karaf.licencemgr.LicenceServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LicenceServicePersistTest {
	private static final Logger LOG = LoggerFactory.getLogger(LicenceServicePersistTest .class);
	
	// licenceString for myproject/1.0-SNAPSHOT
	String licenceStrPlusCrc= "3C3F786D6C2076657273696F6E3D22312E302220656E636F64696E673D225554462D3822207374616E64616C6F6E653D22796573223F3E3C6C6963656E63654D657461646174613E3C70726F6475637449643E6D7970726F6A6563742F312E302D534E415053484F543C2F70726F6475637449643E3C666561747572655265706F7369746F72793E6D766E3A6F72672E6F70656E6E6D732E706C7567696E732F6D7970726F6A6563742F312E302D534E415053484F542F786D6C2F66656174757265733C2F666561747572655265706F7369746F72793E3C6C6963656E7365653E3C2F6C6963656E7365653E3C6C6963656E736F723E4F70656E4E4D5320554B3C2F6C6963656E736F723E3C7374617274446174653E323031382D30362D31345432323A33373A32322E3232352B30313A30303C2F7374617274446174653E3C6475726174696F6E3E3C2F6475726174696F6E3E3C6D617853697A6553797374656D4964733E353C2F6D617853697A6553797374656D4964733E3C73797374656D4964733E3C73797374656D49643E4E4F545F5345543C2F73797374656D49643E3C2F73797374656D4964733E3C6F7074696F6E733E3C6F7074696F6E3E3C6465736372697074696F6E3E7468697320697320746865206465736372697074696F6E206F66206F7074696F6E20313C2F6465736372697074696F6E3E3C6E616D653E6F7074696F6E313C2F6E616D653E3C76616C75653E3C2F76616C75653E3C2F6F7074696F6E3E3C2F6F7074696F6E733E3C2F6C6963656E63654D657461646174613E:919D32FE4C5E74805B16754F2D98F35AE5E3B0424EADDB1622F87E2764ABA40E1D51BAB28A0888D8A7FE81D8EF91BFE2BBA72B6610F88DC572E12A94E307623C122CA545F08062ABFAE25E115BCE010F07A548C6F6FD69AF9C121068918ACA2A1BD3EB78741016007981F478F82352D0ED8F6F51B27BD6EC2CF710E9A61589620C313DD8EC40CBC22BE520270711262F7F3FA1FC610260210C60FEEE841B08C25F2E2A06C4A04FFCD7F824E7333ED6BDD57A3738A6853BBDF2227041EBF3A85B01D37673AAA9F190425F18D276FF210A0AA7413C2A2065B457EE898CBBBD5D7F8DAD14771BB1DE569CD6BBB0DAC5721405A827747DACE8CAECD0FE364710B5EE:24BF967383E0479232EDE2C5F8FB4139-3fc5ccea";

	String fileUri="./target/test-output/tempTestLicenceFile.xml";
	@Test
	public void test() {
		LOG.debug("LicenceServicePersistTest start");
		
		//delete test files if exist

		File licenceManagerFile = new File(fileUri);
		LOG.debug("persisting test file to "+ licenceManagerFile.getAbsolutePath());
		File tmpLicenceManagerFile = new File(fileUri+".tmp"); // temporary file
		File backupLicenceManagerFile = new File(fileUri+".back"); //backup file
		
		// delete files if exist
		tmpLicenceManagerFile.delete();
		backupLicenceManagerFile.delete();
		licenceManagerFile.delete();
		
		LicenceServiceImpl licenceService = new LicenceServiceImpl();
		licenceService.setFileUri(fileUri);
		
		//add licence data
		licenceService.addLicence(licenceStrPlusCrc);
		assertTrue(licenceManagerFile.exists());
		
		//add licence data again - test that backup created
		licenceService.addLicence(licenceStrPlusCrc);
		assertTrue(backupLicenceManagerFile.exists());

		LOG.debug("LicenceServicePersistTest end");
	}

}