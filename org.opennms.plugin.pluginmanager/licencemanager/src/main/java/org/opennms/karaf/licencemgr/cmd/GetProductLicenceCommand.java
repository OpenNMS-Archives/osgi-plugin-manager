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

@Command(scope = "licence-mgr", name = "getlicence", description="returns licence string installed for productId")
public class GetProductLicenceCommand extends OsgiCommandSupport {

	private LicenceService _licenceService;

	public LicenceService getLicenceService() {
		return _licenceService;
	}

	public void setLicenceService( LicenceService licenceService) {
		_licenceService = licenceService;
	}

	@Argument(index = 0, name = "productId", description = "Product Id to which licence-mgr applies", required = true, multiValued = false)
	String productId = null;

	@Override
	protected Object doExecute() throws Exception {
		try{
			String licence = getLicenceService().getLicence(productId);
			if(licence==null){
				System.out.println("no licence installed for productId='" + productId+"'");
			} else {
				String metadatastr = Licence.getUnverifiedMetadata(licence).toXml();
				System.out.println("Found licence ProductId='"+productId + "'");
				System.out.println("              licence=  '" + licence+"'");
				System.out.println("              licenceMetadata='"+metadatastr+"'");
			}
		} catch (Exception e) {
			System.out.println("Error getting licence for productId. Exception="+e);
		}
		return null;
	}
}