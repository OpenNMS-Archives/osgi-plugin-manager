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
import java.util.SortedMap;
import java.util.TreeMap;

import org.opennms.karaf.licencemgr.metadata.jaxb.ProductMetadata;

public class ProductPublisherImpl implements ProductPublisher, ProductRegister {

	private SortedMap<String, ProductMetadata> productSpecMap = new TreeMap<String, ProductMetadata>();
		
	@Override
	public void addProductDescription(ProductMetadata productMetadata) {
		if (productMetadata==null) throw new IllegalArgumentException("productMetadata cannot be null");
		productSpecMap.put(productMetadata.getProductId(), productMetadata);
	}

	@Override
	public boolean removeProductDescription(String productId) {
		if (productId==null) throw new IllegalArgumentException("productId cannot be null");
		if (! productSpecMap.containsKey(productId)) {
			return false;
		} else{
			productSpecMap.remove(productId);
			return true;
		}
	}

	@Override
	public ProductMetadata getProductDescription(String productId) {
		if (productId==null) throw new IllegalArgumentException("productId cannot be null");
		return productSpecMap.get(productId);
	}

	@Override
	public Map<String, ProductMetadata> getProductDescriptionMap() {
		return new TreeMap<String, ProductMetadata>(productSpecMap);
	}

	@Override
	public void deleteProductDescriptions() {
		productSpecMap.clear();		
	}

}
