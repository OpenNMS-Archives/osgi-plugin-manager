package org.opennms.karaf.licencemanager.generate;

import static org.junit.Assert.*;

import org.junit.Test;

public class ProductSpecListGeneratorTest {


	@Test
	public void test1() {
		System.out.println("@ProductSpecListGeneratorTest - test1. START");
		
		String searchDirectory = "target";
		
		String outputFile="target/testProductSpecList.xml";
		
		ProductSpecListGenerator test=new ProductSpecListGenerator( searchDirectory, outputFile);
		
		System.out.println("@ProductSpecListGeneratorTest - test1. END");
		
	}

}
