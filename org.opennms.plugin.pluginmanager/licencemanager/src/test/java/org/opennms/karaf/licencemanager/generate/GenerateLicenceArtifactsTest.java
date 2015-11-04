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

package org.opennms.karaf.licencemanager.generate;

import org.junit.Test;

public class GenerateLicenceArtifactsTest {

	private String basePackage="org.opennms.karaf.licencemanager.testbundle";
	private String productId="org.opennms/org.opennms.karaf.licencemanager.testbundle/1.0-SNAPSHOT";
	private String outputDirectory="target/test-output/generated-licence";
	
	@Test
	public void generateLicenceArtifacts(){
		System.out.println("@Test LicenceArtifacts Start");
		
		LicenceArtifactsGenerator licenceArtifactsGenerator = new LicenceArtifactsGenerator(productId, basePackage, outputDirectory);

		System.out.println("@Test LicenceArtifacts End");
	}
}
