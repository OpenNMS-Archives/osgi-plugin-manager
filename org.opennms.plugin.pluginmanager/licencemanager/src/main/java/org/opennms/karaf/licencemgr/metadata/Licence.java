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

package org.opennms.karaf.licencemgr.metadata;

import org.opennms.karaf.licencemgr.AesSymetricKeyCipher;
import org.opennms.karaf.licencemgr.ClientKeys;
import org.opennms.karaf.licencemgr.PublisherKeys;
import org.opennms.karaf.licencemgr.RsaAsymetricKeyCipher;
import org.opennms.karaf.licencemgr.StringCrc32Checksum;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadata;


public class Licence {

	private final LicenceMetadata licenceMetadata;
	private final String licenceStrPlusCrc;

	/**
	 * @return the licenceMetadata
	 */
	public LicenceMetadata getLicenceMetadata() {
		return licenceMetadata;
	}

	/**
	 * @return the licenceStrPlusCrc
	 */
	public String getLicenceStrPlusCrc() {
		return licenceStrPlusCrc;
	}

	
	/**
	 * Creates a new licence object from LicenceMetadata and PublisherKeys object
	 */
	public Licence(LicenceMetadata licenceMetadata, PublisherKeys publisherKeys ){
		this(licenceMetadata, publisherKeys.getPublicKeyStr(), publisherKeys.getAesSecretKeyStr());
	}
	
	/**
	 * Creates a new licence object from LicenceMetadata and cipher key strings
	 * @param licenceMetadata metadata object to be encoded as a licence
	 * @param publicKeyStr the public key to encode the hash of the licence metadata
	 * @param aesSecretKeyStr the Symmetric secret key to allow the licence to be decoded
	 */
	public Licence(LicenceMetadata licenceMetadata, String publicKeyStr, String aesSecretKeyStr){
		if (licenceMetadata==null) throw new RuntimeException("licenceMetadata cannot be null");
		if (publicKeyStr==null) throw new RuntimeException("publicKeyStr cannot be null");
		if (aesSecretKeyStr==null) throw new RuntimeException("aesSecretKeyStr cannot be null");

		try{
			this.licenceMetadata=licenceMetadata;

			// generate hex string and hash of metadata
			String licenceMetadataHexStr = licenceMetadata.toHexString();
			String licenceMetadataHashStr= licenceMetadata.sha256Hash();

			// encrypt metadata hash string
			RsaAsymetricKeyCipher rsaAsymetricKeyCipher = new RsaAsymetricKeyCipher();
			rsaAsymetricKeyCipher.setPublicKeyStr(publicKeyStr);
			String encryptedHashStr = rsaAsymetricKeyCipher.rsaEncryptString(licenceMetadataHashStr);

			//create licence string
			String licenceStr= licenceMetadataHexStr+":"+encryptedHashStr+":"+aesSecretKeyStr;

			// add checksum
			StringCrc32Checksum stringCrc32Checksum = new StringCrc32Checksum();
			licenceStrPlusCrc=stringCrc32Checksum.addCRC(licenceStr);
			
		} catch (Exception e){
			throw new RuntimeException("could not instantiate new licence with supplied paramaters:",e);
		}

	}

	/**
	 * Creates a new licence object from a licence string and ClientKeys object
	 * This method will ONLY CREATE A VALID LICENCE AND SHOULD BE USED TO VALIDATE received licenceStrPlusCrc
	 */
	public Licence(String licenceStrPlusCrc, ClientKeys clientKeys){
		this(licenceStrPlusCrc, clientKeys.getPrivateKeyEnryptedStr());
	}
	
	/**
	 * Creates a new licence object from a licence string and an encrypted public key
	 * This method will ONLY CREATE A VALID LICENCE AND SHOULD BE USED TO VALIDATE received licenceStrPlusCrc
	 * The licence will only be created if the hash of the metadata matches the encoded hash in the licence
	 * NOTE this only verifies the licence metadata is encoded within a valid licence. You now need to check the systemId, productID, startime etc in the metadata
	 * before the licence is verified for use on this system.
	 * @param licenceStrPlusCrc full licence string with CRC appended
	 * @param privateKeyEnryptedStr encrypted private key (This is decrypted using the aesSecretKeyStr in the licence)
	 */
	public Licence(String licenceStrPlusCrc, String privateKeyEnryptedStr){
		try{ 
			if (licenceStrPlusCrc==null) throw new RuntimeException("licencewithCRC cannot be null");
			if (privateKeyEnryptedStr==null) throw new RuntimeException("privateKeyEnryptedStr cannot be null");
			
			this.licenceStrPlusCrc=licenceStrPlusCrc;

			// check and remove checksum
			StringCrc32Checksum stringCrc32Checksum = new StringCrc32Checksum();
			String licenceStr= stringCrc32Checksum.removeCRC(licenceStrPlusCrc);
			if (licenceStr==null) throw new RuntimeException("licence checksum incorrect");

			// split components of licence string
			String[] components = licenceStr.split(":");
			if (components.length!=3) throw new RuntimeException("incorrectly formatted licence string");

			String receivedLicenceMetadataHexStr=components[0];
			String receivedEncryptedHashStr=components[1];
			String receivedAesSecretKeyStr=components[2];

			licenceMetadata= new LicenceMetadata();
			licenceMetadata.fromHexString(receivedLicenceMetadataHexStr);
			String sha256Hash = licenceMetadata.sha256Hash();
			
			// decode licence private key before using
			AesSymetricKeyCipher aesCipher = new AesSymetricKeyCipher();
			aesCipher.setEncodedSecretKeyStr(receivedAesSecretKeyStr);
			String decryptedPrivateKeyStr = aesCipher.aesDecryptStr(privateKeyEnryptedStr);
			
			//verify hashprivateKeyStr
			RsaAsymetricKeyCipher rsaAsymetricKeyCipher = new RsaAsymetricKeyCipher();
			rsaAsymetricKeyCipher.setPrivateKeyStr(decryptedPrivateKeyStr);
			String decriptedHashStr= rsaAsymetricKeyCipher.rsaDecryptString(receivedEncryptedHashStr);
			if (!sha256Hash.equals(decriptedHashStr)) throw new RuntimeException("Invalid licence. MetadataHash keys do not match");

		} catch (Exception e){
			throw new RuntimeException("could not instantiate new licence from supplied licencewithCRC parameter:",e);
		}

	}
	
	/**
	 * Static Helper method to return only the LicenceMetadata from a licenceStrPlusCrc. 
	 * The CRC is checked but the licence is NOT VALIDATED by this method. This only allows easy access to the metadata
	 * to help the licence manager to manage a licence
	 * @param licenceStrPlusCrc
	 * @return
	 */
	public static LicenceMetadata getUnverifiedMetadata(String licenceStrPlusCrc) throws Exception {
		try{ 
			if (licenceStrPlusCrc==null) throw new RuntimeException("licencewithCRC cannot be null");
			// check and remove checksum
			StringCrc32Checksum stringCrc32Checksum = new StringCrc32Checksum();
			String licenceStr= stringCrc32Checksum.removeCRC(licenceStrPlusCrc);
			if (licenceStr==null) throw new RuntimeException("licence checksum incorrect");

			// split components of licence string
			String[] components = licenceStr.split(":");
			if (components.length!=3) throw new RuntimeException("incorrectly formatted licence string");

			String receivedLicenceMetadataHexStr=components[0];

			LicenceMetadata licenceMetadata= new LicenceMetadata();
			licenceMetadata.fromHexString(receivedLicenceMetadataHexStr);
			
			return licenceMetadata;
			
		} catch (Exception e){
			throw new RuntimeException("could not instantiate LicenceMetadata from supplied licencewithCRC parameter:",e);
		}
		
	}
}
