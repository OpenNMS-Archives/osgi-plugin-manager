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
import org.opennms.karaf.licencemgr.RsaAsymetricKeyCipher;

import static org.junit.Assert.*;


/**
 * @author cgallen
 * See http://www.javamex.com/tutorials/cryptography/asymmetric.shtml 
 * This example based on tutorial
 * Note tests are run by surefire in alphabetical order - not order in class
 *
 */

public class RSAEncryptionTest {

	static String publicKeyStr=null;
	static String privateKeyStr=null;
	static String encryptedStr=null;
	static String encryptedStrPlusCrc=null;
	
	// system instance
	static final String testString="4ad72a34e3635c1b-99da3323";

	//static LicenceService licenceService=null;

	@BeforeClass
	public static void oneTimeSetUp() {
		System.out.println("@Before - setting up tests");

	}

	@AfterClass
	public static void oneTimeTearDown() {
		System.out.println("@After - tearDown");
	}


	@Test 
	public void tests() {
		A_testGenerateKeys();
		B_testEncrypt();
		C_testDecrypt();
	}
	
	
	
	
	
	public void A_testGenerateKeys() {
		RsaAsymetricKeyCipher rsaAsymetricKeyCipher = new RsaAsymetricKeyCipher();
		rsaAsymetricKeyCipher.generateKeys();
		
		privateKeyStr=rsaAsymetricKeyCipher.getPrivateKeyStr();
		publicKeyStr=rsaAsymetricKeyCipher.getPublicKeyStr();
		
		assertNotNull(privateKeyStr);
		assertNotNull(publicKeyStr);
		
		System.out.println("@Test generateKeys() privateKeyStr="+privateKeyStr);
		System.out.println("@Test generateKeys() publicKeyStr="+publicKeyStr);
	}


	public void B_testEncrypt() {

		RsaAsymetricKeyCipher rsaAsymetricKeyCipher = new RsaAsymetricKeyCipher();
		rsaAsymetricKeyCipher.setPublicKeyStr(publicKeyStr);
		
		encryptedStr = rsaAsymetricKeyCipher.rsaEncryptString(testString);
		System.out.println("@Test testEncrypt testString="+testString);
		System.out.println("@Test testEncrypt encryptedStr="+encryptedStr);
		
		// test string plus crc
		encryptedStrPlusCrc=rsaAsymetricKeyCipher.rsaEncryptStringAddChecksum(testString);
		System.out.println("@Test testEncrypt encryptedStrPlusCrc="+encryptedStrPlusCrc);
		
		
	}


	public void C_testDecrypt() {
		System.out.println("@Test testDecrypt encryptedStr="+encryptedStr);
		
		RsaAsymetricKeyCipher rsaAsymetricKeyCipher = new RsaAsymetricKeyCipher();
		rsaAsymetricKeyCipher.setPrivateKeyStr(privateKeyStr);
		
		String decriptedStr= rsaAsymetricKeyCipher.rsaDecryptString(encryptedStr);
		
		System.out.println("@Test testDecrypt decryptedStr="+decriptedStr);
		
		assertEquals(testString,decriptedStr);
		
		// test string plus crc
		decriptedStr=rsaAsymetricKeyCipher.rsaDecryptStringRemoveChecksum(encryptedStrPlusCrc);
		System.out.println("@Test testDecrypt  encryptedStrPlusCrc="+encryptedStrPlusCrc);
		System.out.println("@Test testDecrypt  decryptedstring="+decriptedStr);
		
		assertEquals(testString,decriptedStr);
		
	}





}