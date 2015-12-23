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

import org.junit.*;
import org.opennms.karaf.licencemgr.StringCrc32Checksum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;


/**
 * @author cgallen
 *
 */
public class StringCrc32ChecksumTest {
	private static final Logger LOG = LoggerFactory.getLogger(StringCrc32ChecksumTest.class);

    @BeforeClass
	public static void oneTimeSetUp() {
		LOG.debug("@Before - setting up tests");

	}

	@AfterClass
	public static void oneTimeTearDown() {
		LOG.debug("@After - tearDown");
	}

	@Test
	public void testaStringChecksum() {
		LOG.debug("@Test - testStringChecksum ");
		String testString="testString";
		checksumTest(testString);
	}
	
	@Test
	public void testbStringChecksum() {
		LOG.debug("@Test - testStringChecksum ");
		String testString="test-String";
		checksumTest(testString);
	}
	
	@Test
	public void testcStringChecksum() {
		LOG.debug("@Test - testStringChecksum ");
		String testString="testString-";
		checksumTest(testString);
	}
	
	@Test
	public void testdStringChecksum() {
		LOG.debug("@Test - testStringChecksum ");
		String testString="-testString";
		checksumTest(testString);
	}

    public void checksumTest(String valueString){
		StringCrc32Checksum stringCrc32Checksum = new StringCrc32Checksum();
		String testStringPlusCrc=stringCrc32Checksum.addCRC(valueString);
		
		LOG.debug("     testStringChecksum testStringPlusCrc="+testStringPlusCrc);
		
		assertTrue(stringCrc32Checksum.checkCRC(testStringPlusCrc));
		
		String stringwithoutChecksum = stringCrc32Checksum.removeCRC(testStringPlusCrc);
		assertEquals(valueString, stringwithoutChecksum );
		LOG.debug("     stringwithoutChecksum="+stringwithoutChecksum);
    }

	@Test
    public void checksum1FailTest(){
		LOG.debug("@Test - checksum1FailTest ");
		String valueString="test-String";
		StringCrc32Checksum stringCrc32Checksum = new StringCrc32Checksum();
		String testStringPlusCrc=stringCrc32Checksum.addCRC(valueString)+"b"; // incorrect checksum
		
		LOG.debug("     testStringChecksum Wrong testStringPlusCrc="+testStringPlusCrc);
		
		assertFalse(stringCrc32Checksum.checkCRC(testStringPlusCrc));
		
		String stringwithoutChecksum = stringCrc32Checksum.removeCRC(testStringPlusCrc);
		assertNull(stringwithoutChecksum);
		LOG.debug("     null  stringwithoutChecksum="+stringwithoutChecksum);
    }
	
    public void checksum2FailTest(){
		LOG.debug("@Test - checksum2FailTest ");
		String valueString="test-String";
		StringCrc32Checksum stringCrc32Checksum = new StringCrc32Checksum();
		String testStringPlusCrc=stringCrc32Checksum.addCRC(valueString)+"z"; // incorrect checksum not a number
		
		LOG.debug("     testStringChecksum Wrong testStringPlusCrc="+testStringPlusCrc);
		
		assertFalse(stringCrc32Checksum.checkCRC(testStringPlusCrc));
		
		String stringwithoutChecksum = stringCrc32Checksum.removeCRC(testStringPlusCrc);
		assertNull(stringwithoutChecksum);
		LOG.debug("     null  stringwithoutChecksum="+stringwithoutChecksum);
    }
	
	@Test
    public void checksum3FailTest(){
		LOG.debug("@Test - checksum2FailTest ");
		String valueString="test-String";
		StringCrc32Checksum stringCrc32Checksum = new StringCrc32Checksum();
		String testStringPlusCrc="EXTRA"+stringCrc32Checksum.addCRC(valueString); // incorrect checksum
		
		LOG.debug("     testString3Checksum Wrong testStringPlusCrc="+testStringPlusCrc);
		
		assertFalse(stringCrc32Checksum.checkCRC(testStringPlusCrc));
		
		String stringwithoutChecksum = stringCrc32Checksum.removeCRC(testStringPlusCrc);
		assertNull(stringwithoutChecksum);
		LOG.debug("     null  stringwithoutChecksum="+stringwithoutChecksum);
    }
}