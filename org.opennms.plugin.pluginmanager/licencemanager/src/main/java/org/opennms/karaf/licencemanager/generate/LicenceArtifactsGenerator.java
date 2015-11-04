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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.opennms.karaf.licencemgr.GeneratedKeys;

public class LicenceArtifactsGenerator {

	// class name constants
	private static final String licenceSpecClassName="BundleLocalLicenceSpecImpl";
	private static final String licenceAuthenticatorClassName="BundleLicenceAuthenticatorImpl";
	private static final String licenceAuthenticatorInterfaceName="BundleLicenceAuthenticator";

	// input arguments
	private String basePackage=null;
	private String productId=null;
	private String outputDirectory=null;

	// licence spec keys
	private String aesSecretKeyStr=null;
	private String publicKeyStr=null;

	// licence authenticator keys
	String privateKeyStr=null;
	String privateKeyEnryptedStr=null;

	// misc global variables
	private String javaOutputDirectory=null;
	private String resourcesOutputDirectory=null;
	private String productIdHash=null;


	public LicenceArtifactsGenerator(String productId, String basePackage,  String outputDirectory){
		if (basePackage==null) throw new IllegalArgumentException("basePackage cannot be null");
		if (productId==null) throw new IllegalArgumentException("productId cannot be null");
		if (outputDirectory==null) throw new IllegalArgumentException("outputDirectory cannot be null");

		this.basePackage=basePackage;
		this.productId=productId;
		this.outputDirectory=outputDirectory;

		this.javaOutputDirectory=outputDirectory+"/java";
		this.resourcesOutputDirectory=outputDirectory+"/resources";
		this.productIdHash = "x"+Integer.toHexString(productId.hashCode());

		generateLicenceKeys();
		generateLicenceSpec();
		generateLicenceAuthenticator();
		generateProductDescriptor();

	}


	private void generateLicenceKeys(){
		GeneratedKeys generatedKeys = new GeneratedKeys();
		aesSecretKeyStr=generatedKeys.getAesSecretKeyStr();
		privateKeyEnryptedStr=generatedKeys.getPrivateKeyEnryptedStr();
		privateKeyStr=generatedKeys.getPrivateKeyStr();
		publicKeyStr=generatedKeys.getPublicKeyStr();

		System.out.println("Generated Keys  aesSecretKeyStr      ="+aesSecretKeyStr);
		System.out.println("Generated Keys  privateKeyStr        ="+privateKeyStr);
		System.out.println("Generated Keys  privateKeyEnryptedStr="+privateKeyEnryptedStr);
		System.out.println("Generated Keys  publicKeyStrr        ="+publicKeyStr);
	}

	/**
	 * Generates the file artifacts for the Bundle Licence Specification
	 */
	private void generateLicenceSpec(){

		String licenceSpecClassFileName=licenceSpecClassName+".java";

		// generates package of form spec.base.package.licence.FFAA2233AA
		String licenceSpecPackage=basePackage+".licence."+productIdHash;
		String licenceSpecFileDirectory=licenceSpecPackage.replace('.', File.separatorChar);

		// Generate licence specification artifacts
		String licenceSpecClassStr=""
				+"\n"
				+"package "+licenceSpecPackage+";\n"
				+"\n"
				+"import org.opennms.karaf.licencemgr.BundleLicenceSpec;\n"
				+"import org.opennms.karaf.licencemgr.BundleLicenceSpecImpl;\n"
				+"import org.opennms.karaf.licencepub.LicencePublisher;\n"
				+"import org.osgi.framework.BundleContext;\n"
				+"\n"
				+"/**\n"
				+" *  Generated Licence Specification Class\n"
				+" *  for productId="+productId+"\n"
				+" */\n"
				+"public class "+licenceSpecClassName+" {\n"
				+"\n"
				+"    private static final String productId=\""+productId+"\";\n"
				+"\n"
				+"    private static final String aesSecretKeyStr=\""+aesSecretKeyStr+"\";\n"
				+"    private static final String publicKeyStr=\""+publicKeyStr+"\";\n"
				+"\n"
				+"    private BundleLicenceSpec bundleLicenceSpec=null;\n"
				+"    \n"
				+"    public BundleLocalLicenceSpecImpl( LicencePublisher licencePublisher, BundleContext bundleContext, String licenceMetadataUri){\n"
				+"        bundleLicenceSpec = new BundleLicenceSpecImpl(licencePublisher, bundleContext, productId, licenceMetadataUri,  aesSecretKeyStr, publicKeyStr);\n"
				+"    }\n"
				+"    \n"
				+"\n"
				+"    //use as blueprint destroy-method\n"
				+"    public void unregisterSpec(){\n"
				+"        if (bundleLicenceSpec!=null){\n"
				+"            try{\n"
				+"                bundleLicenceSpec.unregisterSpec();\n"
				+"                System.out.println(BundleLocalLicenceSpecImpl.class +\": Unregistered Licence Specification for productId=\"+productId);\n"
				+"            } catch (Exception e){\n"
				+"                System.out.println(BundleLocalLicenceSpecImpl.class +\": Problem Unregistering Licence Specification for productId=\"+productId+\"  \"+ e);\n"
				+"            }  finally {\n"
				+"                bundleLicenceSpec=null; //release resources\n"
				+"            }\n"
				+"        }\n"
				+"    }\n"
				+"}\n"
				+ "";

		writeFile(javaOutputDirectory, licenceSpecFileDirectory, licenceSpecClassFileName, licenceSpecClassStr);

		String licenceSpecBlueprintStr=""
				+"<blueprint xmlns=\"http://www.osgi.org/xmlns/blueprint/v1.0.0\">\n"
				+"\n"
				+"    <!-- Generated Licence Specification Blueprint-->\n"
				+"    <!-- for productId='"+productId+"' -->\n"
				+"\n"
				+"    <reference id=\"productPublisher\" interface=\"org.opennms.karaf.productpub.ProductPublisher\"\n"
				+"        timeout=\"10000\" />\n"
				+"\n"
				+"    <bean id=\"localBundleProductSpec\" class=\"org.opennms.karaf.productpub.BundleProductSpecImpl\"\n"
				+"        init-method=\"registerSpec\" destroy-method=\"unregisterSpec\">\n"
				+"        <property name=\"bundleContext\" ref=\"blueprintBundleContext\"></property>\n"
				+"        <property name=\"productPublisher\" ref=\"productPublisher\"></property>\n"
				+"        <property name=\"productMetadataUri\" value=\"/productSpec.xml\"></property>\n"
				+"    </bean>\n"
				+"\n"
				+"    <reference id=\"licencePublisher\" interface=\"org.opennms.karaf.licencepub.LicencePublisher\"\n"
				+"        timeout=\"10000\" />\n"
				+"\n"
				+"    <!-- BundleLocalLicenceSpecImpl(LicencePublisher licencePublisher, BundleContext bundleContext, String licenceMetadataUri -->\n"
				+"    <bean id=\"localBundleLicenceSpec\"\n"
				+"        class=\""+licenceSpecPackage+"."+licenceSpecClassName+"\"\n"
				+"        destroy-method=\"unregisterSpec\">\n"
				+"        <argument index=\"0\" ref=\"licencePublisher\" />\n"
				+"        <argument index=\"1\" ref=\"blueprintBundleContext\" />\n"
				+"        <argument index=\"2\" value=\"/licenceMetadataSpec.xml\" />\n"
				+"    </bean>\n"
				+"\n"
				+"</blueprint>\n";



		writeFile(resourcesOutputDirectory, "OSGI-INF/blueprint", "licenceSpecBlueprint.xml", licenceSpecBlueprintStr);

	}

	/**
	 * Generates the file artifacts for the Bundle Licence Authenticator
	 */
	private void generateLicenceAuthenticator(){

		String licenceAuthenticatorClassFileName=licenceAuthenticatorClassName+".java";
		String licenceAuthenticatorInterfaceFileName=licenceAuthenticatorInterfaceName+".java";

		// generates package of form spec.base.package.licence.FFAA2233AA
		String licenceAuthenticatorPackage=basePackage+".authenticator."+productIdHash;
		String licenceAuthenticatorFileDirectory=licenceAuthenticatorPackage.replace('.', File.separatorChar);

		// Generate licence authenticator artifacts
		String licenceAuthenticatorClassStr= ""
				+"package "+licenceAuthenticatorPackage+";  \n"
				+"  \n"
				+"import org.opennms.karaf.licencemgr.LicenceAuthenticatorImpl;  \n"
				+"import org.opennms.karaf.licencemgr.LicenceService;  \n"
				+"import "+licenceAuthenticatorPackage+"."+licenceAuthenticatorInterfaceName+";  \n"
				+"  \n"
				+"/**\n"
				+" *   Generated Licence Authenticator Class\n"
				+" *   for productId="+productId+"\n"
				+" */\n"
				+"public class "+licenceAuthenticatorClassName+" extends LicenceAuthenticatorImpl implements "+licenceAuthenticatorInterfaceName+" {  \n"
				+"  \n"
				+"    private static final String productId=\""+productId+"\";\n"
				+"    final static String privateKeyEnryptedStr=\""+privateKeyEnryptedStr+"\";  \n"
				+"  \n"
				+"    // constructor to authenticate against the licence stored in the manager\n"
				+"    public BundleLicenceAuthenticatorImpl(LicenceService licenceService) {  \n"
				+"        super(licenceService, productId, privateKeyEnryptedStr);  \n"
				+"    }  \n"
				+"  \n"
				+"    // constructor to authenticate an externally supplied licence string\n"
				+"    public BundleLicenceAuthenticatorImpl(String licencewithCRC) {  \n"
				+"        super (licencewithCRC, productId, privateKeyEnryptedStr );  \n"
				+"    }  \n"
				+"  \n"
				+"}  \n";

		writeFile(javaOutputDirectory, licenceAuthenticatorFileDirectory, licenceAuthenticatorClassFileName, licenceAuthenticatorClassStr);

		String licenceAuthenticatorInterfaceStr= ""
				+"package "+licenceAuthenticatorPackage+";  \n"
				+"  \n"
				+"/**\n"
				+" *   Generated Licence Authenticator Interface\n"
				+" *   for productId="+productId+"\n"
				+" */\n"
				+"public interface "+licenceAuthenticatorInterfaceName+" extends org.opennms.karaf.licencemgr.BundleLicenceAuthenticator {  \n"
				+"  \n"
				+"}  \n";

		writeFile(javaOutputDirectory, licenceAuthenticatorFileDirectory, licenceAuthenticatorInterfaceFileName, licenceAuthenticatorInterfaceStr);

		String licenceAuthenticatorBlueprintStr=""
				+"<blueprint xmlns=\"http://www.osgi.org/xmlns/blueprint/v1.0.0\">\n"
				+"\n"
				+"  <!-- Generated Licence Authenticator Blueprint-->\n"
				+"  <!-- for productId='"+productId+"' -->\n"
				+"\n"
				+"  <!-- check licence with licence service -->\n"
				+"  <reference id=\"licenceService\" interface=\"org.opennms.karaf.licencemgr.LicenceService\" timeout=\"10000\" />\n"
				+"\n"
				+"  <bean id=\"licenceAuthenticator\" class=\""+licenceAuthenticatorPackage+"."+licenceAuthenticatorClassName+"\" destroy-method=\"destroyMethod\">\n"
				+"    <argument ref=\"licenceService\" />\n"
				+"  </bean>\n"
				+"\n"
				+"</blueprint>\n"
				+ "";

		writeFile(resourcesOutputDirectory, "OSGI-INF/blueprint", "licenceAuthenticatorBlueprint.xml", licenceAuthenticatorBlueprintStr);

	}

	/**
	 * Generates the file artifacts for the Product Description Bundle
	 */
	private void generateProductDescriptor(){

		String productDescriptorBlueprintStr=""
				+"<blueprint xmlns=\"http://www.osgi.org/xmlns/blueprint/v1.0.0\">\n"
				+"\n"
				+"  <!-- register product information with product registry -->\n"
				+"  <reference id=\"productRegister\" interface=\"org.opennms.karaf.productpub.ProductRegister\" timeout=\"10000\" />\n"
				+"\n"
				+"  <bean id=\"localBundleProductDescription\" class=\"org.opennms.karaf.productpub.BundleProductSpecImpl\" init-method=\"registerSpec\" destroy-method=\"unregisterSpec\">\n"
				+"    <property name=\"bundleContext\" ref=\"blueprintBundleContext\"></property>\n"
				+"    <property name=\"productPublisher\" ref=\"productRegister\"></property>\n"
				+"    <property name=\"productMetadataUri\" value=\"/productSpec.xml\"></property>\n"
				+"  </bean>\n"
				+"\n"
				+"</blueprint>\n"
				+ "";

		writeFile(resourcesOutputDirectory, "OSGI-INF/blueprint", "productDescriptorBlueprint.xml", productDescriptorBlueprintStr);

	}
	
	/**
	 * 
	 * @param outputDirectory path to parent directory for generated file
	 * @param relativePathDirectory path of directories to create relative to outputDirectory
	 * @param fileName name of file to write
	 * @param fileContents content of file to write
	 */
	private void writeFile(String outputDirectory, String relativePathDirectory, String fileName, String fileContents){
		Writer writer = null;

		try {

			File outDir=new File(outputDirectory);
			outDir.mkdirs();

			File fileDir= new File(outDir,relativePathDirectory);
			fileDir.mkdirs();

			File file = new File(fileDir,fileName);

			OutputStream fileOutputStream =  new FileOutputStream(file.getAbsoluteFile());
			writer = new BufferedWriter(new OutputStreamWriter( fileOutputStream , "utf-8"));
			writer.write(fileContents);
			writer.close();

			System.out.println("file '"+fileName+"' written to "+file.getAbsolutePath());
		} catch (IOException ex) {
			throw new RuntimeException("Unable to write java file. ",ex);
		} finally {
			try {writer.close();} catch (Exception ex) {}
		}
	}

	public static void main(String [] args) {

		System.out.println(LicenceArtifactsGenerator.class.getName()+ " Starting to geneate Licence Artifacts");

		if (args.length !=3) throw new IllegalArgumentException(LicenceArtifactsGenerator.class.getName()+" Has wrong number of arguments");

		String productId=args[0];
		String basePackage=args[1];
		String outputDirectory=args[2];

		LicenceArtifactsGenerator licenceArtifactsGenerator = new LicenceArtifactsGenerator(productId, basePackage, outputDirectory);

		System.out.println(LicenceArtifactsGenerator.class.getName()+ " Licence Artifacts Generated");

	}
}
