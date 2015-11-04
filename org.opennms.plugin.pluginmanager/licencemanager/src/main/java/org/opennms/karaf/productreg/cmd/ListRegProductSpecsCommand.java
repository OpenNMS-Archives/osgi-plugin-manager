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

import java.util.Map;
import java.util.Map.Entry;

import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.opennms.karaf.licencemgr.metadata.jaxb.ProductMetadata;
import org.opennms.karaf.productpub.ProductRegister;

@Command(scope = "product-reg", name = "list", description="lists product specifications installed in product registry")
public class ListRegProductSpecsCommand extends OsgiCommandSupport {

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
	
	@Override
	protected Object doExecute() throws Exception {
		try {
			System.out.println("list of product specifications");

			Map<String, ProductMetadata> productSpecMap = getProductRegister().getProductDescriptionMap();
			for (Entry<String, ProductMetadata> entry : productSpecMap.entrySet()){
				
				ProductMetadata productSpecification = entry.getValue();
				
				System.out.println("***********\n"
						+ "  productId='"+entry.getKey()+"'\n"
						+ "  productMetadata='"+productSpecification.toXml()+"'\n");
				}
			System.out.println("***********\n");
		} catch (Exception e) {
			System.out.println("Error getting list of installed licence specifications. Exception="+e);
		}
		return null;
	}


}