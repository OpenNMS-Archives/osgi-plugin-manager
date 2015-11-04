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

package org.opennms.karaf.licencemgr;

import java.util.Set;

import org.opennms.karaf.licencemgr.LicenceAuthenticator;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadata;
import org.osgi.framework.ServiceException;

public class LicenceAuthenticatorImpl implements LicenceAuthenticator {

	private String productId;
	private String privateKeyEnryptedStr;
	private LicenceMetadata licenceMetadata=null;
	private String licencewithCRC=null;

	private LicenceService licenceService= null;

	/**
	 * Simply authenticates the supplied licencewithCRC string  against the local keys without looking up the licence manager service or
	 * authenticating the systemid
	 * @param licencewithCRC
	 * @param productId
	 * @param privateKeyEnryptedStr
	 */
	public LicenceAuthenticatorImpl(String licencewithCRC, String productId, String privateKeyEnryptedStr ){
		if (licencewithCRC==null) throw new RuntimeException("LicenceAuthenticatoImpl: licencewithCRC cannot be null");
		if (productId==null) throw new RuntimeException("LicenceAuthenticatoImpl: productId cannot be null");
		if (privateKeyEnryptedStr==null) throw new RuntimeException("LicenceAuthenticatoImpl: privateKeyEnryptedStr cannot be null");

		licenceAuthenticatorImpl(licencewithCRC, productId, privateKeyEnryptedStr );

	}


	/**
	 * Uses the licence manager service to find an installed licence for the productId and then authenticates it against the
	 * system id and the local keys
	 * @param licenceService
	 * @param productId
	 * @param privateKeyEnryptedStr
	 */
	public LicenceAuthenticatorImpl(LicenceService licenceService, String productId, String privateKeyEnryptedStr ){

		if (licenceService==null) throw new RuntimeException("LicenceAuthenticatoImpl: licenceService cannot be null");
		if (productId==null) throw new RuntimeException("LicenceAuthenticatoImpl: productId cannot be null");
		if (privateKeyEnryptedStr==null) throw new RuntimeException("LicenceAuthenticatoImpl: privateKeyEnryptedStr cannot be null");

		this.productId=productId;
		this.privateKeyEnryptedStr=privateKeyEnryptedStr;

		this.licenceService=licenceService;

		this.licencewithCRC = licenceService.getLicence(productId);

		if (licencewithCRC==null) {
			System.out.println("No licence installed for productId:'"+productId+"'");
			throw new ServiceException("No licence installed for productId:'"+productId+"'");
		}

		String systemId =licenceService.getSystemId();
		if (systemId==null) throw new ServiceException("systemId cannot be null");

		try {

			// check the licence is encoded correctly
			licenceAuthenticatorImpl(licencewithCRC, productId, privateKeyEnryptedStr );

			// check if licenceMetadata contains the local systemId
			Integer maxSizeSystemIds = 0;
			if (licenceMetadata.getMaxSizeSystemIds()== null || "".equals(licenceMetadata.getMaxSizeSystemIds())) {
				throw new ServiceException("the maxSizeSystemIds value must be set as integer for productId='"+productId+"'");
			} else {
				try {
					maxSizeSystemIds = Integer.parseInt(licenceMetadata.getMaxSizeSystemIds());
				} catch (Exception e){
					throw new ServiceException("the maxSizeSystemIds '"+licenceMetadata.getMaxSizeSystemIds()
							+ "' cannot be parsed as int in licence for productId='"+productId+"'", e);
				}
			}
			Set<String> systemIds = licenceMetadata.getSystemIds();
			if (maxSizeSystemIds==null) throw new ServiceException("maxSizeSystemIds must not be null in licence for productId='"+productId+"'");
			if (systemIds.size()>maxSizeSystemIds)  throw new ServiceException("the systemIds list in licence for productId='"+productId
					+"' contains "+systemIds.size()
					+ " entries which is more than maxSizeSystemIds ("+maxSizeSystemIds+ ")");

			// If maxSizeSystemIds==0 then this test is passed because any systemId is allowed
			if (maxSizeSystemIds!= 0) {
				// check if local systemId is referenced in the licence. 
				if (! systemIds.contains(systemId)) throw new ServiceException("installed licence for productId='"+productId+"'"
						+"does not contain local systemId = '"+systemId + "'");
			}

			// if licence authenticated then add the productId to the authenticatedProductId list
			licenceService.addAuthenticatedProductId(productId);

			System.out.println("BundleLicenceAuthenticator authenticated licence for productId="+productId);
			System.out.println("Licence Metadata xml="+licenceMetadata.toXml());

		} catch (Exception e){
			// if licence not authenticated then remove the productId from the authenticatedProductId list
			licenceService.removeAuthenticatedProductId(productId);
			throw e;
		}

	}

	/**
	 * checks the encoding of the licence and that the licence productId matches the local product id
	 * @param licencewithCRC
	 * @param productId
	 * @param privateKeyEnryptedStr
	 */
	private void licenceAuthenticatorImpl(String licencewithCRC, String productId, String privateKeyEnryptedStr ){
		if (licencewithCRC==null) throw new RuntimeException("LicenceAuthenticatoImpl: licencewithCRC cannot be null");
		if (productId==null) throw new RuntimeException("LicenceAuthenticatoImpl: productId cannot be null");
		if (privateKeyEnryptedStr==null) throw new RuntimeException("LicenceAuthenticatoImpl: privateKeyEnryptedStr cannot be null");

		// check and remove checksum
		StringCrc32Checksum stringCrc32Checksum = new StringCrc32Checksum();
		String licenceStr= stringCrc32Checksum.removeCRC(licencewithCRC);
		if (licenceStr==null) {
			System.out.println("licence checksum incorrect for productId:'"+productId+"'");
			throw new ServiceException("licence checksum incorrect for productId:'"+productId+"'");
		}

		// split components of licence string
		String[] components = licenceStr.split(":");
		if (components.length!=3) {
			System.out.println("incorrectly formatted licence string for productId:'"+productId+"'");
			throw new ServiceException("incorrectly formatted licence string for productId:'"+productId+"'");
		}

		String receivedLicenceMetadataHexStr=components[0];
		String receivedEncryptedHashStr=components[1];
		String receivedAesSecretKeyStr=components[2];

		// decode licence private key before using
		AesSymetricKeyCipher aesCipher = new AesSymetricKeyCipher();
		aesCipher.setEncodedSecretKeyStr(receivedAesSecretKeyStr);

		String decryptedPrivateKeyStr= aesCipher.aesDecryptStr(privateKeyEnryptedStr);

		//decrypt encrypted hash of metadata 
		RsaAsymetricKeyCipher rsaAsymetricKeyCipher = new RsaAsymetricKeyCipher();
		rsaAsymetricKeyCipher.setPrivateKeyStr(decryptedPrivateKeyStr);

		String decriptedHashStr= rsaAsymetricKeyCipher.rsaDecryptString(receivedEncryptedHashStr);

		// verify hash of licence metadata matches decrypted hash
		this.licenceMetadata= new LicenceMetadata();
		licenceMetadata.fromHexString(receivedLicenceMetadataHexStr);
		String sha256Hash = licenceMetadata.sha256Hash();

		if (! sha256Hash.equals(decriptedHashStr)) {
			System.out.println("licence hash not verified  for productId:'"+productId+"'");
			throw new ServiceException("licence hash not verified  for productId:'"+productId+"'");
		}

		// check metadata matches expected values
		if (! productId.equals(licenceMetadata.getProductId())){
			System.out.println("licence productId='"+licenceMetadata.getProductId()+"' does not match expected productId:'"+productId+"'");
			throw new ServiceException("licence productId='"+licenceMetadata.getProductId()+"' does not match expected productId:'"+productId+"'");
		}

	}

	/**
	 * this method should be called from blueprint to ensure the licence is 
	 * indicated as unauthenticated when the authenticator shuts down
	 */
	public void destroyMethod(){
		if (licenceService!=null && productId!=null){
			try {
				licenceService.removeAuthenticatedProductId(productId);
			}catch ( Exception e){
				System.out.println("BundleLicenceAuthenticator cannot remove authenticatedProductId="+productId);
			}
		}
		System.out.println("BundleLicenceAuthenticator shutdown for productId="+productId);

	}


	/**
	 * if the class authenticates the licence then the metadata will be available
	 * @return the licenceMetadata
	 */
	public LicenceMetadata getLicenceMetadata() {
		return licenceMetadata;
	}

	/**
	 * If the class authenticates the licence then the licence string will be available
	 * @return the licencewithCRC
	 */
	public String getLicencewithCRC() {
		return licencewithCRC;
	}


}
