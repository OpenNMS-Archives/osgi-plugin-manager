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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;


/**
 * @author cgallen
 * See http://www.javamex.com/tutorials/cryptography/asymmetric.shtml 
 * This example based on tutorial
 * Note tests are run by surefire in alphabetical order - not order in class
 *
 */

public class RSAEncryptionTest {
	private static final Logger LOG = LoggerFactory.getLogger(RSAEncryptionTest.class);

	static String publicKeyStr=null;
	static String privateKeyStr=null;
	static String encryptedStr=null;
	static String encryptedStrPlusCrc=null;
	
	// system instance
	static final String testString="4ad72a34e3635c1b-99da3323";

	//static LicenceService licenceService=null;

	@BeforeClass
	public static void oneTimeSetUp() {
		LOG.debug("@Before - setting up tests");

	}

	@AfterClass
	public static void oneTimeTearDown() {
		LOG.debug("@After - tearDown");
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
		
		LOG.debug("@Test generateKeys() privateKeyStr="+privateKeyStr);
		LOG.debug("@Test generateKeys() publicKeyStr="+publicKeyStr);
	}


	public void B_testEncrypt() {

		RsaAsymetricKeyCipher rsaAsymetricKeyCipher = new RsaAsymetricKeyCipher();
		rsaAsymetricKeyCipher.setPublicKeyStr(publicKeyStr);
		
		encryptedStr = rsaAsymetricKeyCipher.rsaEncryptString(testString);
		LOG.debug("@Test testEncrypt testString="+testString);
		LOG.debug("@Test testEncrypt encryptedStr="+encryptedStr);
		
		// test string plus crc
		encryptedStrPlusCrc=rsaAsymetricKeyCipher.rsaEncryptStringAddChecksum(testString);
		LOG.debug("@Test testEncrypt encryptedStrPlusCrc="+encryptedStrPlusCrc);
		
		
	}


	public void C_testDecrypt() {
		LOG.debug("@Test testDecrypt encryptedStr="+encryptedStr);
		
		RsaAsymetricKeyCipher rsaAsymetricKeyCipher = new RsaAsymetricKeyCipher();
		rsaAsymetricKeyCipher.setPrivateKeyStr(privateKeyStr);
		
		String decriptedStr= rsaAsymetricKeyCipher.rsaDecryptString(encryptedStr);
		
		LOG.debug("@Test testDecrypt decryptedStr="+decriptedStr);
		
		assertEquals(testString,decriptedStr);
		
		// test string plus crc
		decriptedStr=rsaAsymetricKeyCipher.rsaDecryptStringRemoveChecksum(encryptedStrPlusCrc);
		LOG.debug("@Test testDecrypt  encryptedStrPlusCrc="+encryptedStrPlusCrc);
		LOG.debug("@Test testDecrypt  decryptedstring="+decriptedStr);
		
		assertEquals(testString,decriptedStr);
		
	}





}