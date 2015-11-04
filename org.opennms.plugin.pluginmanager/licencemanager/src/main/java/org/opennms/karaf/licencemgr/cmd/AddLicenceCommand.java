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

package org.opennms.karaf.licencemgr.cmd;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.opennms.karaf.licencemgr.LicenceService;
import org.opennms.karaf.licencemgr.metadata.Licence;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadata;

@Command(scope = "licence-mgr", name = "addlicence", description="adds licence for productId")
public class AddLicenceCommand extends OsgiCommandSupport {

	private LicenceService _licenceService;

	public LicenceService getLicenceService() {
		return _licenceService;
	}

	public void setLicenceService( LicenceService licenceService) {
		_licenceService = licenceService;
	}

	@Argument(index = 0, name = "licence", description = "encoded licence string including productId", required = true, multiValued = false)
	String licence = null;

	@Override
	protected Object doExecute() throws Exception {
		try{
			String productId = Licence.getUnverifiedMetadata(licence).getProductId();
			LicenceMetadata licenceMetadata = getLicenceService().addLicence(licence);
			String metadatastr = (licenceMetadata==null) ? "null" : licenceMetadata.toXml();
			System.out.println("Added licence ProductId='"+productId + "'");
			System.out.println("              licence=  '" + licence+"'");
			System.out.println("              licenceMetadata='"+metadatastr+"'\n");
		} catch (Exception e) {
			System.out.println("Error Adding licence. Exception="+e);
		}
		return null;
	}
}