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

@Command(scope = "licence-mgr", name = "isauthenticated", description="checks if licence has been authenticated when starting productId")
public class IsAuthenticatedProductIdCommand extends OsgiCommandSupport {

	private LicenceService _licenceService;

	public LicenceService getLicenceService() {
		return _licenceService;
	}

	public void setLicenceService( LicenceService licenceService) {
		_licenceService = licenceService;
	}

	@Argument(index = 0, name = "productId", description = "productId to be checked if licence authenticated", required = true, multiValued = false)
	String productId = null;

	@Override
	protected Object doExecute() throws Exception {
		try{

			if (getLicenceService().isAuthenticatedProductId(productId)){
				System.out.println("Licence Authenticated for ProductId='"+productId + "'");
			} else {
				System.out.println("Licence Not Authenticated for ProductId='"+productId + "'");
				if (getLicenceService().getLicence(productId)==null){
					System.out.println("No Licence installed for ProductId='"+productId + "'");
				}
			}
		} catch (Exception e) {
			System.out.println("Error Checking Licence for productId. Exception="+e);
		}
		return null;
	}
}