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

import org.opennms.karaf.licencemgr.AesSymetricKeyCipher;
import org.opennms.karaf.licencemgr.RsaAsymetricKeyCipher;

/**
 * This Class generates all public and private keys for a licence / LicenceValidator pair
 * Each new instance of GeneratedKeys generates a new key set at instance creation. 
 * @author cgallen
 *
 */
public class GeneratedKeys implements ClientKeys, PublisherKeys {

	private final String aesSecretKeyStr;
	private final String privateKeyStr;
	private final String publicKeyStr;
	private final String privateKeyEnryptedStr;

	public GeneratedKeys(){

		//Asymmetric cipher key
		AesSymetricKeyCipher aesCipher = new AesSymetricKeyCipher();

		aesCipher.generateKey();

		aesSecretKeyStr = aesCipher.getEncodedSecretKeyStr();

		// generate RSA keys
		RsaAsymetricKeyCipher rsaAsymetricKeyCipher = new RsaAsymetricKeyCipher();
		rsaAsymetricKeyCipher.generateKeys();

		privateKeyStr=rsaAsymetricKeyCipher.getPrivateKeyStr();
		publicKeyStr=rsaAsymetricKeyCipher.getPublicKeyStr();

		//encrypt private key
		privateKeyEnryptedStr = aesCipher.aesEncryptStr(privateKeyStr);

	}
	
	/**
	 * @return the privateKeyStr
	 */
	public String getPrivateKeyStr() {
		return privateKeyStr;
	}

	/**
	 * @return the publicKeyStr
	 */
	public String getPublicKeyStr() {
		return publicKeyStr;
	}

	
	/**
	 * @return the aesSecretKeyStr
	 */
	public String getAesSecretKeyStr() {
		return aesSecretKeyStr;
	}

	/**
	 * Returns the private key encrypted using the aesSecretKeyStr
	 * @return the privateKeyEnryptedStr
	 */
	public String getPrivateKeyEnryptedStr() {
		return privateKeyEnryptedStr;
	}
	
	/**
	 * Returns a new client key object based upon keys in this key generator
	 * @return
	 */
	public PublisherKeys makePublisherKeys() {
		
		final String aesSecret = aesSecretKeyStr;
		final String pubKeyStr=publicKeyStr;
		
		return new PublisherKeys(){

			private final String aesSecretKeyStr = aesSecret;
			private final String publicKeyStr = pubKeyStr;

			@Override
			public String getAesSecretKeyStr() {
				return aesSecretKeyStr;
			}

			@Override
			public String getPublicKeyStr() {
				return publicKeyStr;
			}
			
		};
		
	}
	
	/**
	 * Returns a new public key object based upon keys in this key generator
	 * @return
	 */
	public ClientKeys makeClientKeys() {
		
		final String privKeyEnryptedStr = privateKeyEnryptedStr;
		
		return new ClientKeys(){

			private final String privateKeyEnryptedStr = privKeyEnryptedStr;

			@Override
			public String getPrivateKeyEnryptedStr() {
				return privateKeyEnryptedStr;
			}
		};
	}

}
