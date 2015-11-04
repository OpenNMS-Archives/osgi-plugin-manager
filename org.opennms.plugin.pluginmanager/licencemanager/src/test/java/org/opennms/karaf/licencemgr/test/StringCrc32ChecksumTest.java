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

import static org.junit.Assert.*;


/**
 * @author cgallen
 *
 */
public class StringCrc32ChecksumTest {

    @BeforeClass
	public static void oneTimeSetUp() {
		System.out.println("@Before - setting up tests");

	}

	@AfterClass
	public static void oneTimeTearDown() {
		System.out.println("@After - tearDown");
	}

	@Test
	public void testaStringChecksum() {
		System.out.println("@Test - testStringChecksum ");
		String testString="testString";
		checksumTest(testString);
	}
	
	@Test
	public void testbStringChecksum() {
		System.out.println("@Test - testStringChecksum ");
		String testString="test-String";
		checksumTest(testString);
	}
	
	@Test
	public void testcStringChecksum() {
		System.out.println("@Test - testStringChecksum ");
		String testString="testString-";
		checksumTest(testString);
	}
	
	@Test
	public void testdStringChecksum() {
		System.out.println("@Test - testStringChecksum ");
		String testString="-testString";
		checksumTest(testString);
	}

    public void checksumTest(String valueString){
		StringCrc32Checksum stringCrc32Checksum = new StringCrc32Checksum();
		String testStringPlusCrc=stringCrc32Checksum.addCRC(valueString);
		
		System.out.println("     testStringChecksum testStringPlusCrc="+testStringPlusCrc);
		
		assertTrue(stringCrc32Checksum.checkCRC(testStringPlusCrc));
		
		String stringwithoutChecksum = stringCrc32Checksum.removeCRC(testStringPlusCrc);
		assertEquals(valueString, stringwithoutChecksum );
		System.out.println("     stringwithoutChecksum="+stringwithoutChecksum);
    }

	@Test
    public void checksum1FailTest(){
		System.out.println("@Test - checksum1FailTest ");
		String valueString="test-String";
		StringCrc32Checksum stringCrc32Checksum = new StringCrc32Checksum();
		String testStringPlusCrc=stringCrc32Checksum.addCRC(valueString)+"b"; // incorrect checksum
		
		System.out.println("     testStringChecksum Wrong testStringPlusCrc="+testStringPlusCrc);
		
		assertFalse(stringCrc32Checksum.checkCRC(testStringPlusCrc));
		
		String stringwithoutChecksum = stringCrc32Checksum.removeCRC(testStringPlusCrc);
		assertNull(stringwithoutChecksum);
		System.out.println("     null  stringwithoutChecksum="+stringwithoutChecksum);
    }
	
    public void checksum2FailTest(){
		System.out.println("@Test - checksum2FailTest ");
		String valueString="test-String";
		StringCrc32Checksum stringCrc32Checksum = new StringCrc32Checksum();
		String testStringPlusCrc=stringCrc32Checksum.addCRC(valueString)+"z"; // incorrect checksum not a number
		
		System.out.println("     testStringChecksum Wrong testStringPlusCrc="+testStringPlusCrc);
		
		assertFalse(stringCrc32Checksum.checkCRC(testStringPlusCrc));
		
		String stringwithoutChecksum = stringCrc32Checksum.removeCRC(testStringPlusCrc);
		assertNull(stringwithoutChecksum);
		System.out.println("     null  stringwithoutChecksum="+stringwithoutChecksum);
    }
	
	@Test
    public void checksum3FailTest(){
		System.out.println("@Test - checksum2FailTest ");
		String valueString="test-String";
		StringCrc32Checksum stringCrc32Checksum = new StringCrc32Checksum();
		String testStringPlusCrc="EXTRA"+stringCrc32Checksum.addCRC(valueString); // incorrect checksum
		
		System.out.println("     testString3Checksum Wrong testStringPlusCrc="+testStringPlusCrc);
		
		assertFalse(stringCrc32Checksum.checkCRC(testStringPlusCrc));
		
		String stringwithoutChecksum = stringCrc32Checksum.removeCRC(testStringPlusCrc);
		assertNull(stringwithoutChecksum);
		System.out.println("     null  stringwithoutChecksum="+stringwithoutChecksum);
    }
}