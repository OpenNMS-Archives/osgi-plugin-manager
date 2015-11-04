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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opennms.karaf.licencemgr.AesSymetricKeyCipher;
import org.opennms.karaf.licencemgr.RsaAsymetricKeyCipher;
import org.opennms.karaf.licencemgr.StringCrc32Checksum;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadata;
import org.opennms.karaf.licencemgr.metadata.jaxb.OptionMetadata;

/**
 * This test goes through the complete life-cycle of the algorithms for generating and decoding a licence. 
 * It does not use the Licence object. 
 * @author cgallen
 *
 */
public class TestEncodeDecodeLicenceComplete {

	public static  String aesSecretKeyStr=null;

	private static String publicKeyStr=null;
	private static String privateKeyStr=null;
	private static String privateKeyEnryptedStr=null;

	private static String licenceStrPlusCrc=null;

	@BeforeClass
	public static void oneTimeSetUp() {
		System.out.println("@Before - setting up tests");

	}

	@AfterClass
	public static void oneTimeTearDown() {
		System.out.println("@After - tearDown");
	}

	@Test
	public void testEncodeDecodeLicenceTests(){
		A_generateKeys();
		B_encodeLicence();
		C_decodeLicence();
	}


	public void A_generateKeys(){
		System.out.println("@Test AgenerateKeys() Start");

		//asemetric cipher key
		AesSymetricKeyCipher aesCipher = new AesSymetricKeyCipher();

		aesCipher.generateKey();

		aesSecretKeyStr = aesCipher.getEncodedSecretKeyStr();
		System.out.println("@Test AgenerateKeys() aesSecretKeyStr="+aesSecretKeyStr);

		// generate RSA keys
		RsaAsymetricKeyCipher rsaAsymetricKeyCipher = new RsaAsymetricKeyCipher();
		rsaAsymetricKeyCipher.generateKeys();

		privateKeyStr=rsaAsymetricKeyCipher.getPrivateKeyStr();
		publicKeyStr=rsaAsymetricKeyCipher.getPublicKeyStr();

		assertNotNull(privateKeyStr);
		assertNotNull(publicKeyStr);

		System.out.println("@Test generateKeys() privateKeyStr="+privateKeyStr);
		System.out.println("@Test generateKeys() publicKeyStr="+publicKeyStr);

		//encrypt private key
		privateKeyEnryptedStr=null;

		privateKeyEnryptedStr = aesCipher.aesEncryptStr(privateKeyStr);

		System.out.println("@Test generateKeys() privateKeyEnryptedStr="+privateKeyEnryptedStr);

		System.out.println("@Test AgenerateKeys() END");
	}


	public void B_encodeLicence(){
		System.out.println("@Test BencodeLicence() Start");
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
		System.out.println("@Test BencodeLicence licenceMetadataHashStr="+licenceMetadataHashStr);
		System.out.println("@Test BencodeLicence encryptedHashStr="+encryptedHashStr);

		String licenceStr= licenceMetadataHexStr+":"+encryptedHashStr+":"+aesSecretKeyStr;
		System.out.println("@Test BencodeLicence licenceStr="+licenceStr);

		// add checksum
		StringCrc32Checksum stringCrc32Checksum = new StringCrc32Checksum();
		licenceStrPlusCrc=stringCrc32Checksum.addCRC(licenceStr);

		System.out.println("@Test BencodeLicence() licenceStringPlusCrc="+licenceStrPlusCrc);

		assertTrue(stringCrc32Checksum.checkCRC(licenceStrPlusCrc));

		System.out.println("@Test BencodeLicence() END");

	}


	public void C_decodeLicence(){
		System.out.println("@Test CdecodeLicence() Start");

		// check checksum
		StringCrc32Checksum stringCrc32Checksum = new StringCrc32Checksum();
		assertTrue(stringCrc32Checksum.checkCRC(licenceStrPlusCrc));

		String licenceStr= stringCrc32Checksum.removeCRC(licenceStrPlusCrc);

		// split components of licence string
		String[] components = licenceStr.split(":");
		assertEquals(components.length, 3);

		String receivedLicenceMetadataHexStr=components[0];
		String receivedEncryptedHashStr=components[1];
		String receivedAesSecretKeyStr=components[2];

		// decode licence private key before using
		AesSymetricKeyCipher aesCipher = new AesSymetricKeyCipher();

		aesCipher.setEncodedSecretKeyStr(receivedAesSecretKeyStr);

		String decryptedPrivateKeyStr=null;

		decryptedPrivateKeyStr = aesCipher.aesDecryptStr(privateKeyEnryptedStr);

		System.out.println("CdecodeLicence() decryptedPrivateKeyStr="+decryptedPrivateKeyStr);

		assertEquals(privateKeyStr, decryptedPrivateKeyStr);

		//verify hashprivateKeyStr
		RsaAsymetricKeyCipher rsaAsymetricKeyCipher = new RsaAsymetricKeyCipher();
		rsaAsymetricKeyCipher.setPrivateKeyStr(decryptedPrivateKeyStr);

		String decriptedHashStr= rsaAsymetricKeyCipher.rsaDecryptString(receivedEncryptedHashStr);

		System.out.println("@Test testDecrypt decriptedHashStr="+decriptedHashStr);

		LicenceMetadata licenceMetadata= new LicenceMetadata();
		licenceMetadata.fromHexString(receivedLicenceMetadataHexStr);
		String sha256Hash = licenceMetadata.sha256Hash();

		String metadataxml= licenceMetadata.toXml();
		System.out.println("@Test testDecrypt licenceMetadata.toxml="+metadataxml);
		System.out.println("@Test testDecrypt licenceMetadata.sha256Hash="+sha256Hash);

		assertEquals(sha256Hash,decriptedHashStr);

		System.out.println("@Test CdecodeLicence() End");

	}
}
