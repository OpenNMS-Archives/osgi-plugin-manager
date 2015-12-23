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

package org.opennms.karaf.licencepub.cmd;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadata;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceSpecification;
import org.opennms.karaf.licencepub.LicencePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Command(scope = "licence-pub", name = "getlicencespec", description="Gets the licence metadata for installed licence specification for a given product id")
public class GetLicenceMetadataCommand extends OsgiCommandSupport {
	private static final Logger LOG = LoggerFactory.getLogger(GetLicenceMetadataCommand.class);

	private LicencePublisher _licencePublisher;

	public LicencePublisher getLicencePublisher() {
		return _licencePublisher;
	}

	public void setLicencePublisher(LicencePublisher _licencePublisher) {
		this._licencePublisher = _licencePublisher;
	}


	@Argument(index = 0, name = "productId", description = "productId for which to find specification metadata", required = true, multiValued = false)
	String productId = null;

	@Override
	protected Object doExecute() throws Exception {
		try{
			LicenceSpecification licenceSpecification = getLicencePublisher().getLicenceSpec(productId);
			if (licenceSpecification==null){
				System.out.println("licence specification not installed for productId='"+productId+"'");
				LOG.info("licence specification not installed for productId='"+productId+"'");
			} else {
				LicenceMetadata licenceMetadata = licenceSpecification.getLicenceMetadataSpec();
				String metadatastr = (licenceMetadata==null) ? "null" : licenceMetadata.toXml();
				System.out.println("  productId='"+productId+"'\n"
						+ "      licenceMetadataSpec='"+metadatastr+"'\n");
				LOG.info("  productId='"+productId+"'\n"
						+ "      licenceMetadataSpec='"+metadatastr+"'\n");
			}
		} catch (Exception e) {
			System.err.println("Error getting licence specification. Exception="+e);
			LOG.error("Error getting licence specification. Exception="+e);
		}
		return null;
	}
}