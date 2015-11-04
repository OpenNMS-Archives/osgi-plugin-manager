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

import org.junit.Assert;
import org.junit.Test;
import org.opennms.karaf.licencemgr.AesSymetricKeyCipher;

public class AesSymmetricKeyCipherTest {
    
    @Test
    public void testEncryptionDecryption()
            throws Exception {
    
        AesSymetricKeyCipher aesCipher = new AesSymetricKeyCipher();
        aesCipher.generateKey();
        
        String sourceStr="Craig is very tall.";
		System.out.println("sourceStr (length="+sourceStr.length()+")="+sourceStr);
        String encrypted = aesCipher.aesEncryptStr(sourceStr);
		System.out.println("encrypted= (length="+encrypted.length()+")="+encrypted);
        
        String secretKeyStr = aesCipher.getEncodedSecretKeyStr();
        System.out.println("secretKeyStr="+secretKeyStr);
        
        aesCipher = new AesSymetricKeyCipher();
        aesCipher.setEncodedSecretKeyStr(secretKeyStr);
        
        String decrypted = aesCipher.aesDecryptStr(encrypted);
		System.out.println("decrypted= (length="+decrypted.length()+")="+decrypted);
		
        Assert.assertEquals(decrypted, sourceStr); 
    }
}