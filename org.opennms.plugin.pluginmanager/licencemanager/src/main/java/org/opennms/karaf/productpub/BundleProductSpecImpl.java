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

package org.opennms.karaf.productpub;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.opennms.karaf.licencemgr.metadata.jaxb.ProductMetadata;
import org.opennms.karaf.licencemgr.metadata.jaxb.ProductSpecList;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BundleProductSpecImpl implements BundleProductSpec {
	private static final Logger LOG = LoggerFactory.getLogger(BundleProductSpecImpl.class);

	private BundleContext bundleContext;

	private ProductMetadata productMetadata=null;

	private ProductPublisher productPublisher=null;

	private String productMetadataUri=null;

	private String productSpecListUri=null;

	private ProductSpecList productSpecList=null;

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
	 * use as blueprint init-method
	 */
	@Override
	public void registerSpec(){
		if (productPublisher==null) throw new RuntimeException("productPublisher cannot be null");
		if (bundleContext==null) throw new RuntimeException("bundleContext cannot be null");

		// only loads product metadata from URI if not already set by dependency injection
		if (productMetadata==null){
			if (productMetadataUri==null) throw new RuntimeException("productMetadataUri cannot be null if no productMetadata defined");
			InputStream is=null;
			try {
				Bundle bundle = bundleContext.getBundle();
				is = bundle.getEntry(productMetadataUri).openStream();
				String productMetadataXml =  readFile(is);
				productMetadata=new ProductMetadata();
				productMetadata.fromXml(productMetadataXml);
			} catch (Exception e) {
				throw new RuntimeException("cannot load productMetadata file from bundle productMetadataUri="+productMetadataUri+ "  "+e);
			} finally {
				if (is!=null)
					try {
						is.close();
					} catch (IOException e) {}
				is=null;
			}
		}
		productPublisher.addProductDescription(productMetadata);
		System.out.println("Registered Product Specification for productId="+productMetadata.getProductId());
		LOG.info("Registered Product Specification for productId="+productMetadata.getProductId());
	}

	/**
	 * use as blueprint destroy-method
	 */
	@Override
	public void unregisterSpec(){
		if (productPublisher!=null){
			try{
				productPublisher.removeProductDescription(productMetadata.getProductId());
				System.out.println("Unregistered Product Specification for productId="+productMetadata.getProductId());
				LOG.info("Unregistered Product Specification for productId="+productMetadata.getProductId());
			} catch (Exception e){
				System.err.println("Problem Unregistering Product Specification for productId="+productMetadata.getProductId()+"  "+ e);
				LOG.error("Problem Unregistering Product Specification for productId="+productMetadata.getProductId()+"  ", e);
			}  finally {
				productPublisher=null; //release resources
			}
		}
	}

	/**
	 * use as blueprint init-method for adding a specification list
	 * This is used to register a specification for plugins in a kar file
	 */
	@Override
	public void registerSpecificationList(){
		if (productPublisher==null) throw new RuntimeException("productPublisher cannot be null");
		if (bundleContext==null) throw new RuntimeException("bundleContext cannot be null");

		// only loads product metadata from URI if not already set by dependency injection
		if (productSpecList==null){
			if (productSpecListUri==null) throw new RuntimeException("registerSpecificationList: productSpecListUri cannot be null if no productSpecList defined");
			InputStream is=null;
			try {
				Bundle bundle = bundleContext.getBundle();
				is = bundle.getEntry(productSpecListUri).openStream();
				String productSpecListXml =  readFile(is);
				productSpecList=new ProductSpecList();
				productSpecList.fromXml(productSpecListXml);
			} catch (Exception e) {
				throw new RuntimeException("registerSpecificationList: cannot load productSpecList file from bundle productSpecListUri="+productSpecListUri+ "  "+e);
			} finally {
				if (is!=null)
					try {
						is.close();
					} catch (IOException e) {}
				is=null;
			}
		}

		String msg="Registering Product Specifications from productListSource="+productSpecList.getProductListSource();
		List<ProductMetadata> prodSpecList = productSpecList.getProductSpecList();
		for(ProductMetadata pmeta: prodSpecList){
			productPublisher.addProductDescription(pmeta);
			msg=msg+"\n"+"Specification List Registered Product Specification for productId="+pmeta.getProductId();
		}
		System.out.println(msg);
		LOG.info(msg);
	}

	/**
	 * use as blueprint destroy-method for removing a specification list
	 * This is used to un-register a specification for plugins in a kar file
	 */
	@Override
	public void unregisterSpecificationList(){
		if (productPublisher!=null){
			try{
				System.out.println("Unregistering Product Specifications from productListSource="+productSpecList.getProductListSource());
				LOG.info("Specification List Unregistering Product Specifications from productListSource="+productSpecList.getProductListSource());
				List<ProductMetadata> prodSpecList = productSpecList.getProductSpecList();
				for(ProductMetadata pmeta: prodSpecList){
					boolean unregistered =productPublisher.removeProductDescription(pmeta.getProductId());
					if (unregistered){
						System.out.println("Specification List Unregistered Product Specification for productId="+pmeta.getProductId());
						LOG.info("Specification List Unregistered Product Specification for productId="+pmeta.getProductId());
					}else {
						System.out.println("Specification List Already Unregistered Product Specification for productId="+pmeta.getProductId());
						LOG.info("Specification List Already Unregistered Product Specification for productId="+pmeta.getProductId());
					}
				}
			} catch (Exception e){
				System.err.println("Specification List Problem Unregistering Product Specifications from productListSource="+productSpecList.getProductListSource()+"  "+e);
				LOG.error("Specification List Problem Unregistering Product Specifications from productListSource="+productSpecList.getProductListSource()+"  ",e);
			}  finally {
				productPublisher=null; //release resources
			}
		}
	}

	/**
	 * @return the productMetadata
	 */
	@Override
	public ProductMetadata getProductMetadata() {
		return productMetadata;
	}

	/**
	 * @param productMetadata the productMetadata to set
	 */
	@Override
	public void setProductMetadata(ProductMetadata productMetadata) {
		if (productMetadata==null) throw new IllegalArgumentException("productMetadata cannot be null");

		this.productMetadata = productMetadata;
	}

	/**
	 * @return the productPublisher
	 */
	@Override
	public ProductPublisher getProductPublisher() {
		return productPublisher;
	}

	/**
	 * @param productPublisher the productPublisher to set
	 */
	@Override
	public void setProductPublisher(ProductPublisher productPublisher) {
		if (productPublisher==null) throw new IllegalArgumentException("productPublisher cannot be null");
		this.productPublisher = productPublisher;
	}

	/**
	 * @return the productMetadataUri
	 */
	@Override
	public String getProductMetadataUri() {
		return productMetadataUri;
	}

	/**
	 * @param productMetadataUri the productMetadataUri to set
	 */
	@Override
	public void setProductMetadataUri(String productMetadataUri) {
		if (productMetadataUri==null) throw new IllegalArgumentException("productMetadataUri cannot be null");
		this.productMetadataUri = productMetadataUri;
	}

	/**
	 * @return the bundleContext
	 */
	@Override
	public BundleContext getBundleContext() {
		return bundleContext;
	}

	/**
	 * @param bundleContext the bundleContext to set
	 */
	@Override
	public void setBundleContext(BundleContext bundleContext) {
		if (bundleContext==null) throw new IllegalArgumentException("bundleContext cannot be null");
		this.bundleContext = bundleContext;
	}

	public String getProductSpecListUri() {
		return productSpecListUri;
	}

	public void setProductSpecListUri(String productSpecListUri) {
		this.productSpecListUri = productSpecListUri;
	}

	public ProductSpecList getProductSpecList() {
		return productSpecList;
	}

	public void setProductSpecList(ProductSpecList productSpecList) {
		this.productSpecList = productSpecList;
	}


}
