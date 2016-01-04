package org.opennms.karaf.licencemanager.generate;

import static org.junit.Assert.*;

import org.junit.Test;

public class ProductSpecListGeneratorTest {


	@Test
	public void test1() {
		System.out.println("@ProductSpecListGeneratorTest - test1. START");
		
		String searchDirectory = "target";
		
		String outputFile="target/test-gen-resources/testProductSpecList.xml";
		
		String packagingDescriptor="testPackageDescriptor/1.0.0";
		
		ProductSpecListGenerator test=new ProductSpecListGenerator( searchDirectory, outputFile, packagingDescriptor );
		
		System.out.println("@ProductSpecListGeneratorTest - test1. END");
		
	}

}
