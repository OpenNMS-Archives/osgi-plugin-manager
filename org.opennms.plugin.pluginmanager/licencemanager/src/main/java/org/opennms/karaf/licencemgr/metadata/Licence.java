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

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.opennms.karaf.licencemgr.AesSymetricKeyCipher;
import org.opennms.karaf.licencemgr.ClientKeys;
import org.opennms.karaf.licencemgr.PublisherKeys;
import org.opennms.karaf.licencemgr.RsaAsymetricKeyCipher;
import org.opennms.karaf.licencemgr.StringCrc32Checksum;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadata;


public class Licence {

	private final LicenceMetadata licenceMetadata;
	private final String licenceStrPlusCrc;
	private final Map<String,String> secretProperties = new LinkedHashMap<String,String>();

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
	 * gets secret properties if populated
	 * @return
	 */
	public Map<String, String> getSecretProperties() {
		return secretProperties;
	}

	/*
	 * Constructor methods for creating a licence from LicenceMetadata
	 */

	/**
	 * Creates a new licence object from LicenceMetadata and PublisherKeys object
	 * @param secretPropertiesMap if not null will create secret properties and encode and append to licence
	 */
	public Licence(LicenceMetadata licenceMetadata, PublisherKeys publisherKeys, Map<String,String> secretPropertiesMap){
		this(licenceMetadata, publisherKeys.getPublicKeyStr(), publisherKeys.getAesSecretKeyStr(),secretPropertiesMap);
	}

	/**
	 * Creates a new licence object from LicenceMetadata and cipher key strings
	 * @param licenceMetadata metadata object to be encoded as a licence
	 * @param publicKeyStr the public key to encode the hash of the licence metadata
	 * @param aesSecretKeyStr the Symmetric secret key to allow the licence to be decoded
	 * @param secretPropertiesMap if not null will create secret properties and encode and append to licence
	 */
	public Licence(LicenceMetadata licenceMetadata, String publicKeyStr, String aesSecretKeyStr, Map<String,String> secretPropertiesMap){
		if (licenceMetadata==null) throw new RuntimeException("licenceMetadata cannot be null");
		if (publicKeyStr==null) throw new RuntimeException("publicKeyStr cannot be null");
		if (aesSecretKeyStr==null) throw new RuntimeException("aesSecretKeyStr cannot be null");

		if (secretPropertiesMap!=null) this.secretProperties.putAll(secretPropertiesMap);

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

			// if secret properties are populated create properties string
			if(! secretProperties.isEmpty()){
				String secretPropertiesStr = toProperties(secretProperties);
				// encrypt secret properties
				String encryptedSecretPropertiesStr = rsaAsymetricKeyCipher.rsaEncryptString(secretPropertiesStr);
				licenceStr=licenceStr+":"+encryptedSecretPropertiesStr;
			}

			// add checksum
			StringCrc32Checksum stringCrc32Checksum = new StringCrc32Checksum();
			licenceStrPlusCrc=stringCrc32Checksum.addCRC(licenceStr);

		} catch (Exception e){
			throw new RuntimeException("could not instantiate new licence with supplied paramaters:",e);
		}

	}

	/*
	 * Constructor Methods for decoding a licence string
	 */

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
			if (components.length < 3 || components.length > 4) throw new RuntimeException("incorrectly formatted licence string. Incorrect number ("
					+ components.length + ") of strings split around ':' ");

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

			// if encryptedSecretPropertiesStr exists, decode
			if(components.length==4){
				String receivedEncryptedSecretPropertiesStr = components[3];
				String decryptedSecretPropertiesStr= rsaAsymetricKeyCipher.rsaDecryptString(receivedEncryptedSecretPropertiesStr);
				secretProperties.putAll(fromProperties(decryptedSecretPropertiesStr));
			}


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
			if (components.length < 3 || components.length > 4) throw new RuntimeException("incorrectly formatted licence string. Incorrect number ("
					+ components.length + ") of strings split around ':' ");

			String receivedLicenceMetadataHexStr=components[0];

			LicenceMetadata licenceMetadata= new LicenceMetadata();
			licenceMetadata.fromHexString(receivedLicenceMetadataHexStr);

			return licenceMetadata;

		} catch (Exception e){
			throw new RuntimeException("could not instantiate LicenceMetadata from supplied licencewithCRC parameter:",e);
		}

	}

	/**
	 * calculates expiry date for a licence. Returns null if no expiry set or can be calculated
	 * 	duration - alternative to expiry date. Duration of licence in days. If null (and expiryDate is null) there is no expiry date.
	 *  If duration =0, there is no expiry date. If both defined, duration has precedence over expiryDate.
	 * @param licenceStrPlusCrc
	 * @return
	 * @throws Exception
	 */
	public static Date calculateExpiryDate(String licenceStrPlusCrc) throws Exception {
		LicenceMetadata meta = Licence.getUnverifiedMetadata(licenceStrPlusCrc);

		Date expiryDate = meta.getExpiryDate();
		Date startDate = meta.getStartDate();
		String productId = meta.getProductId();

		Integer duration = null;
		String durationStr = meta.getDuration();

		if (durationStr!=null && ! durationStr.trim().isEmpty()) try {
			duration = Integer.parseInt(durationStr);
		} catch(Exception ex){
			throw new Exception("cannot parse duration "+durationStr+" from licence for productId="+productId, ex);
		}

		if(duration!=null && duration==0) return null; // duration == 0 no expiry date

		if(expiryDate!=null){
			return expiryDate;

		} else {
			if(duration==null || startDate==null) return null; // expiryDate = null duration ==null or startDate== null and  no expiry date

			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			cal.add(Calendar.DATE, duration); 
			expiryDate = cal.getTime();
			return expiryDate;
		}

	}

	/**
	 * 
	 * @param licenceStrPlusCrc
	 * @return timeToExpiry in days null if no expiration time set or cannot be calculated
	 * @throws Exception
	 */
	public static Long daysToExpiry(String licenceStrPlusCrc, Date currentDate) throws Exception {
		// duration - alternative to expiry date. Duration of licence in days. If null (and expiryDate is null) there is no expiry date.
		// If duration =0, there is no expiry date. If both defined, duration has precedence over expiryDate.

		Long timeToExpiry=null;

		Date expiryDate = calculateExpiryDate(licenceStrPlusCrc);

		if (expiryDate==null) return null;

		// this is quick and dirty way to calculate days to expiry	
		timeToExpiry =  (expiryDate.getTime()-currentDate.getTime())/86400000;

		return timeToExpiry;
	}

	// utilities methods

	/**
	 * Converts propertiesMap<key,value> of name value pairs to properties string key=value separated by comma
	 * Throws an exception if resulting string is longer then 245 bytes as cannot be encrypted as part of licence
	 * @param propertiesMap
	 * @return
	 */
	public static String toProperties(Map<String,String> propertiesMap ){
		Iterator<String> itr = propertiesMap.keySet().iterator();

		StringBuffer properties =new StringBuffer();

		while (itr.hasNext()) { 
			String key = itr.next();
			String value =propertiesMap.get(key);
			properties.append(key+"="+value);
			if(itr.hasNext())properties.append(",");
		}
		String propStr = properties.toString();
		try {
			byte[] bytes = propStr.getBytes("UTF-8");
			if (bytes.length>245) 
				throw new IllegalArgumentException("cannot encode propertiesMap as block size greater than 245 bytes. Reduce number or size of your name value pairs.");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("cannot get bytes for string",e);
		}

		return propStr;
	}

	/**
	 * Converts properties string of key=value pairs separated by CR to Map<String key, String value> 
	 * @param properties
	 * @return
	 */
	public static Map<String,String> fromProperties(String properties){

		Map<String,String> propertiesMap = new LinkedHashMap<String,String>();
		String[] kvpair = properties.split(",");
		for(String kvStr: kvpair){
			String[] kv =kvStr.split("=");
			propertiesMap.put(kv[0].trim(), kv[1].trim());
		}
		return propertiesMap;
	}


}
