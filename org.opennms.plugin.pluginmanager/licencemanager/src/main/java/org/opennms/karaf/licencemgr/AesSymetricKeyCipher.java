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

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
 
//import org.apache.commons.codec.binary.Base64;

/**
 * see http://www.reindel.com/symmetric-encryption-using-aes-and-java/
 * @author cgallen
 *
 */
public class AesSymetricKeyCipher {

    private int passwordLength=16;
    private int saltLength=16;
    private int initializationVectorSeedLength=16;
    private int hashIterations=10000;
    private int keyLength=128; // note key length cannot be longer without cryptographic extensions

    private SecretKey secretKey=null;

    /**
     * 
     * @return A string containing a xsd:hexBinary lexical representation of the SecretKey
     * returns null if generateKey() has not been called or the key has not been set with setEncodedSecretKeyStr

     */
    public String getEncodedSecretKeyStr() {
    	return DatatypeConverter.printHexBinary(secretKey.getEncoded());

    }

    /**
     * Sets the Secretkey using  a xsd:hexBinary lexical representation of the SecretKey as obtained using getEncodedSecretKeyStr()
     * @param secretKeyStr
     */
    public void setEncodedSecretKeyStr(String secretKeyStr) {
    	secretKey = new SecretKeySpec(DatatypeConverter.parseHexBinary(secretKeyStr), "AES");
    }
    
	/** 
	 * Generates AES based private key string.
	 * This key string can be accessed by getEncodedSecretKeyStr() after it is generated
	 * generateKey() overwites any previous value for the secret key
	 */
    public void generateKey() {
        SecretKeyFactory secretKeyFactory;
		try {
			secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        SecureRandom secureRandom = new SecureRandom();

        KeySpec keySpec = new PBEKeySpec(getRandomPassword(), secureRandom.generateSeed(saltLength), hashIterations, keyLength);
        
        secretKey = new SecretKeySpec(secretKeyFactory.generateSecret(keySpec).getEncoded(), "AES");
        
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("cannot generate AesSymmetricKey:",e);
		} catch (InvalidKeySpecException e) {
			throw new RuntimeException("cannot generate AesSymmetricKey:",e);
		}
    }

	/**
	 * Returns an EAS encrypted Byte[] array of  src byte[] array
	 * @param src
	 * @return AES encoded lexical representation of xsd:hexBinary
	 */
    public String aesEncryptStr(String src ) {

        Cipher cipher;
        String aesEncryptStr=null;
        
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        SecureRandom secureRandom = new SecureRandom();
	        
	        byte[] seed = secureRandom.generateSeed(initializationVectorSeedLength);
	        AlgorithmParameterSpec algorithmParameterSpec = new IvParameterSpec(seed);

	        cipher.init(Cipher.ENCRYPT_MODE, secretKey, algorithmParameterSpec);
	        byte[] encryptedMessageBytes = cipher.doFinal(src.getBytes("UTF-8"));
	        
	        byte[] bytesToEncode = new byte[seed.length + encryptedMessageBytes.length];
	        System.arraycopy(seed, 0, bytesToEncode, 0, seed.length);
	        System.arraycopy(encryptedMessageBytes, 0, bytesToEncode, seed.length, encryptedMessageBytes.length);

	        aesEncryptStr = DatatypeConverter.printHexBinary(bytesToEncode);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("problem encrypting AesSymetricKey",e);
		} catch (NoSuchPaddingException e) {
			throw new RuntimeException("problem encrypting AesSymetricKey",e);
		} catch (InvalidKeyException e) {
			throw new RuntimeException("problem encrypting AesSymetricKey",e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new RuntimeException("problem encrypting AesSymetricKey",e);
		} catch (IllegalBlockSizeException e) {
			throw new RuntimeException("problem encrypting AesSymetricKey",e);
		} catch (BadPaddingException e) {
			throw new RuntimeException("problem encrypting AesSymetricKey",e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("problem encrypting AesSymetricKey",e);
		}
		
		return aesEncryptStr;

    }

	/**
	 * expects a string containing a lexical representation of xsd:hexBinary 
	 * of AES encoded source string.
	 * @param encryptedStr string to decode
	 * @return decryptedStr decrypted string
	 */
    public String aesDecryptStr(String encryptedStr) {

        byte[] bytesToDecode = DatatypeConverter.parseHexBinary(encryptedStr);

        byte[] emptySeed = new byte[initializationVectorSeedLength];
        System.arraycopy(bytesToDecode, 0, emptySeed, 0, initializationVectorSeedLength);
        
        Cipher cipher;
        
        String aesDecryptStr =null;
        
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		    cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(emptySeed));

	        int messageDecryptedBytesLength = bytesToDecode.length - initializationVectorSeedLength;
	        byte[] messageDecryptedBytes = new byte[messageDecryptedBytesLength];
	        System.arraycopy(bytesToDecode, initializationVectorSeedLength, messageDecryptedBytes, 0, messageDecryptedBytesLength);

	        byte[] decodedBytes = cipher.doFinal(messageDecryptedBytes);
	        aesDecryptStr = new String(decodedBytes, "UTF-8" );
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("problem decrypting AesSymetricKey",e);
		} catch (NoSuchPaddingException e) {
			throw new RuntimeException("problem decrypting AesSymetricKey",e);
		} catch (InvalidKeyException e) {
			throw new RuntimeException("problem decrypting AesSymetricKey",e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new RuntimeException("problem decrypting AesSymetricKey",e);
		} catch (IllegalBlockSizeException e) {
			throw new RuntimeException("problem decrypting AesSymetricKey",e);
		} catch (BadPaddingException e) {
			throw new RuntimeException("problem decrypting AesSymetricKey",e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("problem decrypting AesSymetricKey",e);
		}

		return aesDecryptStr;

    }


    
    private char[] getRandomPassword() {
        
        char[] randomPassword = new char[passwordLength];
        
        Random random = new Random();
        for(int i = 0; i < passwordLength; i++) {
            randomPassword[i] = (char)(random.nextInt('~' - '!' + 1) + '!');
        }
        
        return randomPassword;
    }
    
    
}