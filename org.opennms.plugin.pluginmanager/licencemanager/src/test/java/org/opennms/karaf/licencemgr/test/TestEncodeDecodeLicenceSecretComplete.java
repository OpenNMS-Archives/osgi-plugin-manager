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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opennms.karaf.licencemgr.AesSymetricKeyCipher;
import org.opennms.karaf.licencemgr.RsaAsymetricKeyCipher;
import org.opennms.karaf.licencemgr.StringCrc32Checksum;
import org.opennms.karaf.licencemgr.metadata.Licence;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadata;
import org.opennms.karaf.licencemgr.metadata.jaxb.OptionMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test goes through the complete life-cycle of the algorithms for generating and decoding a licence. 
 * It does not use the Licence object except to encode the secret properties as a string.
 * This test also tests the additional secret options
 * @author cgallen
 *
 */
public class TestEncodeDecodeLicenceSecretComplete {
	private static final Logger LOG = LoggerFactory.getLogger(TestEncodeDecodeLicenceSecretComplete.class);

	public static  String aesSecretKeyStr=null;

	private static String publicKeyStr=null;
	private static String privateKeyStr=null;
	private static String privateKeyEnryptedStr=null;

	private static String licenceStrPlusCrc=null;
	
	private static String secretPropertiesStr=null;

	@BeforeClass
	public static void oneTimeSetUp() {
		LOG.debug("@Before - setting up tests");

	}

	@AfterClass
	public static void oneTimeTearDown() {
		LOG.debug("@After - tearDown");
	}

	@Test
	public void testEncodeDecodeLicenceTests(){
		A_generateKeys();
		B_encodeLicence();
		C_decodeLicence();
	}


	public void A_generateKeys(){
		LOG.debug("@Test AgenerateKeys() Start");

		//asemetric cipher key
		AesSymetricKeyCipher aesCipher = new AesSymetricKeyCipher();

		aesCipher.generateKey();

		aesSecretKeyStr = aesCipher.getEncodedSecretKeyStr();
		LOG.debug("@Test AgenerateKeys() aesSecretKeyStr="+aesSecretKeyStr);

		// generate RSA keys
		RsaAsymetricKeyCipher rsaAsymetricKeyCipher = new RsaAsymetricKeyCipher();
		rsaAsymetricKeyCipher.generateKeys();

		privateKeyStr=rsaAsymetricKeyCipher.getPrivateKeyStr();
		publicKeyStr=rsaAsymetricKeyCipher.getPublicKeyStr();

		assertNotNull(privateKeyStr);
		assertNotNull(publicKeyStr);

		LOG.debug("@Test generateKeys() privateKeyStr="+privateKeyStr);
		LOG.debug("@Test generateKeys() publicKeyStr="+publicKeyStr);

		//encrypt private key
		privateKeyEnryptedStr=null;

		privateKeyEnryptedStr = aesCipher.aesEncryptStr(privateKeyStr);

		LOG.debug("@Test generateKeys() privateKeyEnryptedStr="+privateKeyEnryptedStr);

		LOG.debug("@Test AgenerateKeys() END");
	}


	public void B_encodeLicence(){
		LOG.debug("@Test BencodeLicence() Start");
		LicenceMetadata metadata = new LicenceMetadata();

		metadata.setExpiryDate(new Date());
		metadata.setStartDate(new Date());
		metadata.setLicensee("Mr Craig Gallen");
		metadata.setLicensor("OpenNMS UK");
		metadata.setProductId("org.opennms/org.opennms.karaf.licencemanager.testbundle/1.0-SNAPSHOT");
		metadata.setFeatureRepository("mvn:org.opennms.licencemgr/licence.manager.example/2.10.0/xml/features");
		
		metadata.setMaxSizeSystemIds("3");
		metadata.getSystemIds().add("4ad72a34e3635c1b-99da3323");
		metadata.getSystemIds().add("32e396e36b28ef5d-a48ef1cb");

		OptionMetadata option1 = new OptionMetadata("newname", "newvalue", "this is the description");
		metadata.getOptions().add(option1);

		String licenceMetadataHexStr = metadata.toHexString();
		String licenceMetadataHashStr=metadata.sha256Hash();

		RsaAsymetricKeyCipher rsaAsymetricKeyCipher = new RsaAsymetricKeyCipher();
		rsaAsymetricKeyCipher.setPublicKeyStr(publicKeyStr);

		String encryptedHashStr = rsaAsymetricKeyCipher.rsaEncryptString(licenceMetadataHashStr);
		LOG.debug("@Test BencodeLicence licenceMetadataHashStr="+licenceMetadataHashStr);
		LOG.debug("@Test BencodeLicence encryptedHashStr="+encryptedHashStr);
		
		// create secret properties
		Map<String,String> secretProperties = new LinkedHashMap<String,String>();
		secretProperties .put("secret.a.a.a.a", "xxxyyy");
		secretProperties .put("secret.a.a.a.b", "xxxzzz");
		secretProperties .put("secret.a.a.a.c", "xxxooo");
		secretProperties .put("secret.a.a.a.d", "xxxxxx");

		secretPropertiesStr = Licence.toProperties(secretProperties);

		System.out.println("secretPropertiesStr length="+secretPropertiesStr.length()
				+ " secretProperties="+secretProperties);
		// encrypt secret properties
		String encryptedSecretPropertiesStr = rsaAsymetricKeyCipher.rsaEncryptString(secretPropertiesStr);

		
		String licenceStr= licenceMetadataHexStr+":"+encryptedHashStr+":"+aesSecretKeyStr;
		LOG.debug("@Test BencodeLicence licenceStr="+licenceStr);
		
		if (! encryptedSecretPropertiesStr.isEmpty()) licenceStr=licenceStr+":"+encryptedSecretPropertiesStr ;
		
		LOG.debug("@Test BencodeLicence with secret properties licenceStr="+licenceStr);

		// add checksum
		StringCrc32Checksum stringCrc32Checksum = new StringCrc32Checksum();
		licenceStrPlusCrc=stringCrc32Checksum.addCRC(licenceStr);

		LOG.debug("@Test BencodeLicence() licenceStringPlusCrc="+licenceStrPlusCrc);

		assertTrue(stringCrc32Checksum.checkCRC(licenceStrPlusCrc));

		LOG.debug("@Test BencodeLicence() END");

	}


	public void C_decodeLicence(){
		LOG.debug("@Test CdecodeLicence() Start");

		// check checksum
		StringCrc32Checksum stringCrc32Checksum = new StringCrc32Checksum();
		assertTrue(stringCrc32Checksum.checkCRC(licenceStrPlusCrc));

		String licenceStr= stringCrc32Checksum.removeCRC(licenceStrPlusCrc);

		// split components of licence string
		String[] components = licenceStr.split(":");
		
		assertEquals(components.length, 4); // will have 4 components if secret properties

		String receivedLicenceMetadataHexStr=components[0];
		String receivedEncryptedHashStr=components[1];
		String receivedAesSecretKeyStr=components[2];
		String receivedEncryptedSecretPropertiesStr=components[3];

		// decode licence private key before using
		AesSymetricKeyCipher aesCipher = new AesSymetricKeyCipher();

		aesCipher.setEncodedSecretKeyStr(receivedAesSecretKeyStr);

		String decryptedPrivateKeyStr=null;

		decryptedPrivateKeyStr = aesCipher.aesDecryptStr(privateKeyEnryptedStr);

		LOG.debug("CdecodeLicence() decryptedPrivateKeyStr="+decryptedPrivateKeyStr);

		assertEquals(privateKeyStr, decryptedPrivateKeyStr);

		//decrypt and verify hashprivateKeyStr
		RsaAsymetricKeyCipher rsaAsymetricKeyCipher = new RsaAsymetricKeyCipher();
		rsaAsymetricKeyCipher.setPrivateKeyStr(decryptedPrivateKeyStr);

		String decriptedHashStr= rsaAsymetricKeyCipher.rsaDecryptString(receivedEncryptedHashStr);

		LOG.debug("@Test testDecrypt decriptedHashStr="+decriptedHashStr);
		
		//decrypt and verify secret properties
		
		String decryptedSecretPropertiesStr= rsaAsymetricKeyCipher.rsaDecryptString(receivedEncryptedSecretPropertiesStr);

		LOG.debug("@Test testDecrypt decryptedPropertiesStr="+decryptedSecretPropertiesStr);
		assertEquals(secretPropertiesStr,decryptedSecretPropertiesStr);
		
		Map<String, String> secretProperties = Licence.fromProperties(decryptedSecretPropertiesStr);
		assertEquals(4,secretProperties.size());

		//check licence metadata
		LicenceMetadata licenceMetadata= new LicenceMetadata();
		licenceMetadata.fromHexString(receivedLicenceMetadataHexStr);
		String sha256Hash = licenceMetadata.sha256Hash();

		String metadataxml= licenceMetadata.toXml();
		LOG.debug("@Test testDecrypt licenceMetadata.toxml="+metadataxml);
		LOG.debug("@Test testDecrypt licenceMetadata.sha256Hash="+sha256Hash);

		assertEquals(sha256Hash,decriptedHashStr);

		LOG.debug("@Test CdecodeLicence() End");

	}

	
}
