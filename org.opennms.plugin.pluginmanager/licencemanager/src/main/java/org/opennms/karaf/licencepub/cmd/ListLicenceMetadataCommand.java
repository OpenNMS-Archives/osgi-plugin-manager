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

import java.util.Map;
import java.util.Map.Entry;

import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.opennms.karaf.licencemgr.LicenceService;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadata;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceSpecification;
import org.opennms.karaf.licencepub.LicencePublisher;

@Command(scope = "licence-pub", name = "list", description="lists licence metadata for installed licence specifications (i.e. not the keys)")
public class ListLicenceMetadataCommand extends OsgiCommandSupport {

	private LicencePublisher _licencePublisher;

	public LicencePublisher getLicencePublisher() {
		return _licencePublisher;
	}

	public void setLicencePublisher(LicencePublisher _licencePublisher) {
		this._licencePublisher = _licencePublisher;
	}

	@Override
	protected Object doExecute() throws Exception {
		try {
			System.out.println("List of licence metadata for installed licence specifications:");

			Map<String, LicenceSpecification> licenceSpecMap = getLicencePublisher().getLicenceSpecMap();
			for (Entry<String, LicenceSpecification> entry : licenceSpecMap.entrySet()){
				
				LicenceSpecification licenceSpecification = entry.getValue();
				LicenceMetadata licenceMetadata = licenceSpecification.getLicenceMetadataSpec();
				
				System.out.println("***********\n"
						+ "  productId='"+entry.getKey()+"'\n"
						+ "  licenceMetadataSpec='"+licenceMetadata.toXml()+"'\n");
			}
			System.out.println("***********\n");
		} catch (Exception e) {
			System.out.println("Error getting list of licence metadata for installed licence specifications. Exception="+e);
		}
		return null;
	}


}