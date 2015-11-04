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

package org.opennms.karaf.productreg.cmd;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.opennms.karaf.licencemgr.metadata.jaxb.ProductMetadata;
import org.opennms.karaf.productpub.ProductRegister;

@Command(scope = "product-reg", name = "getproductspec", description="gets product spec from  product registry for productId")
public class GetRegProductSpecCommand extends OsgiCommandSupport {

	private ProductRegister productRegister;

	/**
	 * @return the productRegister
	 */
	public ProductRegister getProductRegister() {
		return productRegister;
	}

	/**
	 * @param productRegister the productRegister to set
	 */
	public void setProductRegister(ProductRegister productRegister) {
		this.productRegister = productRegister;
	}
	
	@Argument(index = 0, name = "productId", description = "Product Id for which to get product spec", required = true, multiValued = false)
	String productId = null;

	@Override
	protected Object doExecute() throws Exception {
		try{
			if (productRegister==null) throw new RuntimeException("productPublisher cannot be null");
			ProductMetadata pmeta = productRegister.getProductDescription(productId);
			if (pmeta==null) {
				System.out.println("No product metadata installed for productId='" + productId+"'");
			} else {
				System.out.println("productId='" + productId+"' Product metadata XML='"+pmeta.toXml()+"'");
			}
		} catch (Exception e) {
			System.out.println("Error getting product info. Exception="+e);
		}
		return null;
	}
}