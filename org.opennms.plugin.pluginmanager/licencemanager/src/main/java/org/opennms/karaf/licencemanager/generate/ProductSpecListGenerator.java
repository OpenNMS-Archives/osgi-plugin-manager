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

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.opennms.karaf.licencemanager.generate.LicenceArtifactsGenerator;
import org.opennms.karaf.licencemgr.metadata.jaxb.ProductMetadata;
import org.opennms.karaf.licencemgr.metadata.jaxb.ProductSpecList;


/**
 * Generates a product spec list based upon any product specification files found contained in 
 * jars in a given search directory
 * @author admin
 *
 */
public class ProductSpecListGenerator {

	private static final String PRODUCT_SPEC_FILENAME="productSpec.xml";

	private String outputFile=null;

	private String searchDirectory=null;
	
	private String packagingDescriptor=null;

	public ProductSpecListGenerator(String searchDirectory, String outputFile, String packagingDescriptor){
		super();
		this.outputFile=outputFile;
		this.searchDirectory=searchDirectory;
		this.packagingDescriptor=packagingDescriptor;
		generateProductSpecList();
	}


	public void generateProductSpecList() {
		System.out.println("Generating product specifications list for "+PRODUCT_SPEC_FILENAME
				+ " product definitions contained in jars"
				+ "\n  search directory="+searchDirectory
				+ "\n  output file="+outputFile);

		ArrayList<File> files = new ArrayList<File>();

		findFiles(searchDirectory, ".jar", files);

		ProductSpecList productSpecList = new ProductSpecList();

		System.out.println("Discovering product spec in jar files");
		for (File f:files){
			System.out.println("   searching "+f.getAbsolutePath());
			ZipFile jarFile=null;
			InputStream is=null;
			try {
				jarFile = new ZipFile(f);
				ZipEntry arg = jarFile.getEntry(PRODUCT_SPEC_FILENAME);
				if(arg !=null){
					System.out.println("   found "+arg.getName());
					is = jarFile.getInputStream(arg);
					String productMetadataXml =  readFile(is);
					ProductMetadata productMetadata = new ProductMetadata();
					// supplied packaging descriptor will replace pre-existing one in product descriptor
					if (packagingDescriptor!=null) productMetadata.setPackageingDescriptor(packagingDescriptor);
					productMetadata.fromXml(productMetadataXml);
					System.out.println("       productName="+productMetadata.getProductName());
					System.out.println("       productId="+productMetadata.getProductId());
					System.out.println("       featureRepository="+productMetadata.getFeatureRepository());
					System.out.println("       packagingDescriptor="+productMetadata.getPackageingDescriptor());
					productSpecList.getProductSpecList().add(productMetadata);
				} else {
					System.out.println("   no "+PRODUCT_SPEC_FILENAME+ " in:"+f.getAbsolutePath());
				}
				if (packagingDescriptor!=null) productSpecList.setProductListSource(packagingDescriptor);
				System.out.println("   productListSource="+productSpecList.getProductListSource());

			} catch (Exception e) {
				throw new RuntimeException("   problem reading "+PRODUCT_SPEC_FILENAME+" in:"+f.getAbsolutePath(),e);
			} finally {
				try {
					if (is!=null) is.close();
					if (jarFile!=null) jarFile.close();
				} catch (Exception e) {	}
			}
		}

		PrintWriter out=null;
		try{
			String productSpecStr = productSpecList.toXml(true);
			System.out.println("final product specification :\n"+productSpecStr);
			
			File outfile = new File(outputFile);
			File absoutfile = outfile.getAbsoluteFile();
			System.out.println("writing product specification to file :\n"+absoutfile.getAbsolutePath());
			absoutfile.getParentFile().mkdirs();
			out = new PrintWriter(absoutfile);
			out.println(productSpecStr);
		} catch(Exception e){
			throw new RuntimeException("problem creating product spec list in outputFile="+outputFile, e);
		} finally{
			if (out!=null) out.close();
		}
		
		System.out.println("completed writing product spec list to "+outputFile);
	}

	
	/**
	 * reads all of a file referenced by an InputStream into a String
	 * @param is
	 * @return
	 */
	private String readFile(InputStream is )  {
		String str=null;
		java.util.Scanner s=null;
		try{
			s = new java.util.Scanner(is, "UTF-8").useDelimiter("\\A");
			str=s.hasNext() ? s.next() : "";
		} catch (Exception e){
			throw e;
		} finally{
			if (s!=null) s.close();
		}
		return str;
	}


	/**
	 * recursively finds all files within a given directory which 
	 * have the given file name suffix (e.g. <filename>.jar 
	 * @param directoryName
	 * @param fileSuffix
	 * @param files
	 */
	private void findFiles(String directoryName, String fileSuffix, ArrayList<File> files) {
		File directory = new File(directoryName);

		if(! directory.getAbsoluteFile().exists()) throw new IllegalArgumentException("directoryName "+directoryName+ " does not exist. File path="+directory.getAbsolutePath());
		if(! directory.getAbsoluteFile().isDirectory()) throw new IllegalArgumentException("directoryName "+directoryName+ " is not a directory. File path="+directory.getAbsolutePath());

		// get all the files from a directory
		File[] fList = directory.getAbsoluteFile().listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				if (file.getName().endsWith(fileSuffix))
					files.add(file);
			} else if (file.isDirectory()) {
				findFiles(file.getAbsolutePath(), fileSuffix, files);
			}
		}
	}


	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public String getSearchDirectory() {
		return searchDirectory;
	}

	public void setSearchDirectory(String searchDirectory) {
		this.searchDirectory = searchDirectory;
	}

	/**
	 * Generates a product spec list based upon any product specification files found contained in 
	 * jars in a given search directory
	 * @param args args[0] = searchDirectory; args[1]=outputFile
	 */
	public static void main(String [] args) {

		System.out.println(ProductSpecListGenerator.class.getName()+ " Starting to generate Available Plugins List for Kar ");

		try{
			if (args.length !=3 && args.length!=2) throw new IllegalArgumentException(ProductSpecListGenerator.class.getName()+" Has wrong number of arguments");

			String searchDirectory=args[0];
			String outputFile=args[1];
			String packagingDescriptor=args[2];

			ProductSpecListGenerator test=new ProductSpecListGenerator(searchDirectory, outputFile, packagingDescriptor);

			System.out.println(LicenceArtifactsGenerator.class.getName()+ " Available Plugins List Generated");
		} catch (Exception e){
			System.err.println(LicenceArtifactsGenerator.class.getName()+ " Problem Generating Available Plugins List: ");
			System.err.println("Correct usage: java args[0] = searchDirectory; args[1]=outputFile; args[3]=packagingDescriptor (optional)\nError: "+e.getMessage());
			e.printStackTrace();
		}

	}


}
