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

package org.opennms.karaf.productpub.cmd;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.opennms.karaf.licencemgr.metadata.jaxb.ProductMetadata;
import org.opennms.karaf.productpub.ProductPublisher;

@Command(scope = "product-pub", name = "addproductspec", description="Adds product spec to product publisher for productId")
public class AddProductSpecCommand extends OsgiCommandSupport {

	private ProductPublisher productPublisher;

	/**
	 * @return the productPublisher
	 */
	public ProductPublisher getProductPublisher() {
		return productPublisher;
	}

	/**
	 * @param productPublisher the productPublisher to set
	 */
	public void setProductPublisher(ProductPublisher productPublisher) {
		this.productPublisher = productPublisher;
	}

	@Argument(index = 0, name = "productMetadata", description = "product metadata as xml (surround xml in 'quotes') ", required = true, multiValued = false)
	String productMetadataXml = null;

	@Override
	protected Object doExecute() throws Exception {
		try{
			ProductMetadata pmeta= new ProductMetadata();
			pmeta.fromXml(productMetadataXml);
			
			getProductPublisher().addProductDescription(pmeta);
			System.out.println("Added Product Metadata for productId='"+pmeta.getProductId()+ "' productMetadata='" + pmeta.toXml()+"'");
		} catch (Exception e) {
			System.out.println("Error adding product spec for productId. Exception="+e);
		}
		return null;
	}


}