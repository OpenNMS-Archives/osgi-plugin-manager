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

import java.util.Map;
import java.util.Map.Entry;

import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.opennms.karaf.licencemgr.metadata.jaxb.ProductMetadata;
import org.opennms.karaf.productpub.ProductPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Command(scope = "product-pub", name = "list", description="Lists product specifications installed in product publisher")
public class ListProductSpecsCommand extends OsgiCommandSupport {
	private static final Logger LOG = LoggerFactory.getLogger(ListProductSpecsCommand.class);


	private ProductPublisher productPublisher=null;

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

	@Override
	protected Object doExecute() throws Exception {
		try {
			System.out.println("list of product specifications");
			LOG.info("list of product specifications");

			Map<String, ProductMetadata> productSpecMap = getProductPublisher().getProductDescriptionMap();

			for (Entry<String, ProductMetadata> entry : productSpecMap.entrySet()){

				ProductMetadata productSpecification = entry.getValue();

				String msg="***********\n"
						+ "  productId='"+entry.getKey()+"'\n"
						+ "  productMetadata='"+productSpecification.toXml()+"'\n";
				LOG.info(msg);
				System.out.println(msg);
			}
			System.out.println("***********\n");
			LOG.info("***********\n");
		} catch (Exception e) {
			System.err.println("Error getting list of installed licence specifications. Exception="+e);
			LOG.error("Error getting list of installed licence specifications. Exception=",e);
		}
		return null;
	}


}