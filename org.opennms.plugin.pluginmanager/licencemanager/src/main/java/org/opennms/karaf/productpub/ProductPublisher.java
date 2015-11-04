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

import java.util.Map;

import org.opennms.karaf.licencemgr.metadata.jaxb.ProductMetadata;


public interface ProductPublisher {

	/**
	 * Checks the productId in the productMetadata
	 * and then adds a product description entry in the Product Description map corresponding to 
	 * the productId. Previous entries for productId are overwritten.
	 * @param productMetadata
	 */
	public void addProductDescription(ProductMetadata productMetadata);
	
	/**
	 * Checks the productId removes any entry for productId.
	 * @return true if entry for productId is found and removed. false if productId is not found
	 * @param productMetadata
	 */
	public boolean removeProductDescription(String productId);

	/**
	 * @return returns product description for productId if found.
	 * null if no entry found
	 * @param productId
	 */
	public ProductMetadata getProductDescription(String productId);

	/**
	 * @return returns a map of product description entries with key=productId
	 * value= ProductMetadata
	 */
	public Map<String, ProductMetadata> getProductDescriptionMap();
	
	/**
	 * Deletes all product descriptions
	 */
	public void deleteProductDescriptions();
}
