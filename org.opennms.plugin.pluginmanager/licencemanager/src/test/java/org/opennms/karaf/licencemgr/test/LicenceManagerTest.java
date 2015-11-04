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

package org.opennms.karaf.licencemgr.test;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.*;
import org.opennms.karaf.licencemgr.LicenceService;
import org.opennms.karaf.licencemgr.LicenceServiceImpl;
import org.opennms.karaf.licencemgr.StringCrc32Checksum;

import static org.junit.Assert.*;


/**
 * @author cgallen
 *
 */
public class LicenceManagerTest {
	
	// first correct licence string (same productId1, different dates)
	static final String licence1a="3C3F786D6C2076657273696F6E3D22312E302220656E636F64696E673D225554462D3822207374616E64616C6F6E653D22796573223F3E3C6C6963656E63654D657461646174613E3C70726F6475637449643E6F72672E6F70656E6E6D732F6F72672E6F70656E6E6D732E6B617261662E6C6963656E63656D616E616765722E7465737462756E646C652F312E302D534E415053484F543C2F70726F6475637449643E3C6C6963656E7365653E4D722043726169672047616C6C656E3C2F6C6963656E7365653E3C6C6963656E736F723E4F70656E4E4D5320554B3C2F6C6963656E736F723E3C73797374656D49643E346164373261333465333633356331622D39396461333332333C2F73797374656D49643E3C7374617274446174653E323031352D30322D30335431363A34343A31392E3535305A3C2F7374617274446174653E3C657870697279446174653E323031352D30322D30335431363A34343A31392E3535305A3C2F657870697279446174653E3C6F7074696F6E733E3C6F7074696F6E3E3C6465736372697074696F6E3E7468697320697320746865206465736372697074696F6E3C2F6465736372697074696F6E3E3C6E616D653E6E65776E616D653C2F6E616D653E3C76616C75653E6E657776616C75653C2F76616C75653E3C2F6F7074696F6E3E3C2F6F7074696F6E733E3C2F6C6963656E63654D657461646174613E:0C08BC7C52B50C4BA9BC571017A04FCD48AA25A4E643497F5ACA7AD85D589734EEFB5061605E3B8D1F48D014A80167D1B62F7465EC8875D0AC7B338AB2E37782C6FFFC6CABC89FD55B72F72A76A8BB50D9644D3B35005066EE9035C4E60900EECA585F2C546CDB498A5A5F82D65F3D93272049C71F5BECB042F5F2D0019194D90224676042B3C080DE140BCFB5FA554EE1B20A8A004BC795D424A117D4AD5DCC1C054CA14ADD6C83D89B179D3028265CD678364C0093915845703BE27810B9623106E0C1A174E0C58C75BE85181B4102BFBC90DB3EC7DC517CCC223369833BCB149ACE242678E04216295D2A4FF9D9480F4042C8BAA43C517A7AF7AF19C9F12B:7C094F1CCD5DF991947D5B44F728BC47-e0b39642";
	
	// second correct licence string (same productId1, different dates)
	static final String licence1b="3C3F786D6C2076657273696F6E3D22312E302220656E636F64696E673D225554462D3822207374616E64616C6F6E653D22796573223F3E3C6C6963656E63654D657461646174613E3C70726F6475637449643E6F72672E6F70656E6E6D732F6F72672E6F70656E6E6D732E6B617261662E6C6963656E63656D616E616765722E7465737462756E646C652F312E302D534E415053484F543C2F70726F6475637449643E3C6C6963656E7365653E4D722043726169672047616C6C656E3C2F6C6963656E7365653E3C6C6963656E736F723E4F70656E4E4D5320554B3C2F6C6963656E736F723E3C73797374656D49643E346164373261333465333633356331622D39396461333332333C2F73797374656D49643E3C7374617274446174653E323031352D30322D30335431363A34363A35302E3537345A3C2F7374617274446174653E3C657870697279446174653E323031352D30322D30335431363A34363A35302E3537345A3C2F657870697279446174653E3C6F7074696F6E733E3C6F7074696F6E3E3C6465736372697074696F6E3E7468697320697320746865206465736372697074696F6E3C2F6465736372697074696F6E3E3C6E616D653E6E65776E616D653C2F6E616D653E3C76616C75653E6E657776616C75653C2F76616C75653E3C2F6F7074696F6E3E3C2F6F7074696F6E733E3C2F6C6963656E63654D657461646174613E:88359093E3B8B665021AA4FED3956640E44AAA278DE059DC53E1F1CD0AAB6B268F6CD0542AF6017FE4FFEDCFC78931AE6F559FB5AB01EFBE3B8FADA899B255779798DAF32651B8DB8D463AECCB197BC2D70CD8A098884B19A2B92ABCA972BDF498ED6483FD38A365D0DFF5A2879E105FF6DACEC5D0FE60F96CE452397D39582EAFE72B609377FD8E2FE36D3D9224E091F01FC811150F4741237AF17FD867750394B5298322C5BC22DC1B882FA0465BAE8D37D4F794D8D10F6448735CBDD6A179B51708852369E7B8E36FA7749935B288F23F6B06046A928FCE89FEA76A725D03DC576785E3703126973766CCF3002AEE845AD08DC96B438283B1ED882FF0E955:D80928AD010B7693CEA7A1F6DAFF5084-e559bc0b";

	// product id for both licence strings
	static final String productId1="org.opennms/org.opennms.karaf.licencemanager.testbundle/1.0-SNAPSHOT";

	static LicenceService licenceService=null;

    @BeforeClass
	public static void oneTimeSetUp() {
		System.out.println("@Before - oneTimeSetUp().START");
		LicenceServiceImpl licenceServiceImpl = new LicenceServiceImpl();

		try {
			// added to solve problems with JAXB not unmarshalling relative paths
			// this gives JAXB an absolute path to work with
			String uri="target"+File.separator+"licenceService.xml";
			File licenceManagerFile = new File(uri);
			String absolutePath=licenceManagerFile.getAbsolutePath();
			System.out.println("oneTimeSetUp() absolute file fileUri:"+absolutePath);
						
			licenceServiceImpl.setFileUri(absolutePath);

			licenceServiceImpl.load();
			licenceServiceImpl.persist();
		} catch (Exception e){
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			System.out.println("oneTimeSetUp() Exception:"+errors);
		}

		licenceService=licenceServiceImpl;
		System.out.println("@Before - oneTimeSetUp().FINISH");

	}

	@AfterClass
	public static void oneTimeTearDown() {
		System.out.println("@After - tearDown");
	}

	@Test
	public void testMakeSystemInstance() {
		System.out.println("@Test - testMakeSystemInstance().START");
		String instanceId = licenceService.makeSystemInstance();
		System.out.println("@Test - testMakeSystemInstance(). instanceId="+instanceId);

		StringCrc32Checksum stringCrc32Checksum = new StringCrc32Checksum();
		assertTrue(stringCrc32Checksum.checkCRC(instanceId));
		System.out.println("@Test - testMakeSystemInstance().FINISH");
	}


	@Test
	public void testSetWrongSystemInstance() {
		System.out.println("@Test testSetWrongSystemInstance().START");

		boolean thrown = false;
		try {
			licenceService.setSystemId("ddfgdfhdhjd");
		} catch (RuntimeException e) {
			System.out.println("@Test testSetWrongSystemInstance() 1 Expected Exception:"+e);
			thrown = true;
		}
		assertTrue(thrown);
		
		try {
			licenceService.setSystemId("bc032e68b0defeb5-e7dde04b");
		} catch (RuntimeException e) {
			System.out.println("@Test testSetWrongSystemInstance() 2 Expected Exception:"+e);
			thrown = true;
		}
		assertTrue(thrown);
		
		try {
			licenceService.setSystemId("bc032e68b0de-feb4-e7dde04b");
		} catch (RuntimeException e) {
			System.out.println("@Test testSetWrongSystemInstance() 3 Expected Exception:"+e);
			thrown = true;
		}
		assertTrue(thrown);
		System.out.println("@Test testSetWrongSystemInstance().FINISH");

	}

	@Test
	public void testSetCorrectSystemInstance() {
		System.out.println("@Test testSetCorrectSystemInstance().START");
		Boolean thrown=false;
		try {
			licenceService.setSystemId("bc032e68b0defeb4-e7dde04b");
		} catch (RuntimeException e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			System.out.println("@Test testSetCorrectSystemInstance() Unexpected Exception:"+errors);
			thrown = true;
		}
		assertFalse(thrown);
		System.out.println("@Test testSetCorrectSystemInstance().FINISH");
	}
	
	@Test
	public void testAddCorrectLicences() {
		System.out.println("@Test testAddCorrectLicences().START");
		// adds same productId licence twice - should only be one licence stored
		boolean thrown=false;
		try {
			licenceService.addLicence(licence1a);
		} catch (RuntimeException e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			System.out.println("@Test testAddCorrectLicences() Unexpected Exception:"+errors);
			thrown = true;
		}
		
		try {
			licenceService.addLicence(licence1b);
		} catch (RuntimeException e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			System.out.println("@Test testAddCorrectLicences() Unexpected Exception:"+errors);
			thrown = true;
		}
		
		assertFalse(thrown);
		System.out.println("@Test testAddCorrectLicences().FINISH");

	}
	
	@Test
	public void testAddInCorrectLicence() {
		System.out.println("@Test testAddInCorrectLicence().START");
		boolean thrown=false;
		try {
			String licence="bc032e68b0defeb5-e7dde04b"; //incorrect checksum
			licenceService.addLicence(licence);
		} catch (RuntimeException e) {
			System.out.println("@Test testAddInCorrectLicence() expected Exception:"+e);
			thrown = true;
		}
		assertTrue(thrown);
		System.out.println("@Test testAddInCorrectLicence().FINISH");

	}
	
	
	@Test
	public void testGetLicence() {
		System.out.println("@Test testGetLicence().START");
		String licence = licenceService.getLicence(productId1);
		assertNotNull(licence);
		assertEquals(licence1b,licence);
		System.out.println("@Test testGetLicence().FINISH");
	}

}