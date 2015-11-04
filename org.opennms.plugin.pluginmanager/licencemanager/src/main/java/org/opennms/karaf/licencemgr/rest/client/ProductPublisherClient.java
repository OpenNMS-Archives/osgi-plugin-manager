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

package org.opennms.karaf.licencemgr.rest.client;

import org.opennms.karaf.licencemgr.metadata.jaxb.ProductMetadata;
import org.opennms.karaf.licencemgr.metadata.jaxb.ProductSpecList;

public interface ProductPublisherClient {


	/**
	 * /addproductspec (POST productMetadata)
	 * 
	 * Post command to add a new product specification. 
	 * Adds a new product specification to the product publisher. 
	 * Looks for the productId in the product specification and adds 
	 * an entry in the licence table under that productId. 
	 * Replaces any previous licence entry

	 * @param productMetadata
	 * @throws Exception
	 */
	public void addProductSpec(ProductMetadata productMetadata) throws Exception;


	/**
	 * /removeproductspec (GET productId)
	 * 
	 * Checks the product publisher and removes any entry for productId
	 * e.g. http://localhost:8181/licencemgr/rest/product-pub/removeproductspec?productId=
	 * 
	 * @param productId
	 * @throws Exception
	 */
	public void removeProductSpec(String productId ) throws Exception;


	/**
	 * /getproductspec (GET productId)
	 * returns product description metadata for productId if found
	 * 
	 * e.g. http://localhost:8181/licencemgr/rest/product-pub/getproductspec?productId=
	 * 
	 * @param productId
	 * @return productMetadata contains description of product
	 * @throws Exception
	 */
	public ProductMetadata getProductSpec(String productId ) throws Exception;


	/**
	 * /list (GET )
	 * returns a map of product description entries with key=productId, value= ProductMetadata
	 * 
	 * e.g. http://localhost:8181/licencemgr/rest/product-pub/list
	 * 
	 * @return productSpecList 
	 * @throws Exception
	 */
	public ProductSpecList getList() throws Exception;


	/**
	 * /clearproductspecs (GET )
	 * 
	 * Deletes all product descriptions. 
	 * Will only delete descriptions if parameter confirm=true
	 * 
	 * e.g. http://localhost:8181/licencemgr/rest/product-pub/clearproductspecs?confirm=false
	 * 
	 * @param confirm
	 * @throws Exception
	 */
	public void clearProductSpecs(Boolean confirm ) throws Exception;


}
