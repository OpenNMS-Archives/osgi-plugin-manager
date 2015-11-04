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

import java.util.Map;
import java.util.Map.Entry;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.opennms.karaf.licencemgr.LicenceService;

@Command(scope = "licence-mgr", name = "listforsystemid", description="Lists installed licences which will authenticate for a given systemId")
public class ListLicencesForSystemIdCommand extends OsgiCommandSupport {

	private LicenceService _licenceService;

	public LicenceService getLicenceService() {
		return _licenceService;
	}

	public void setLicenceService( LicenceService licenceService) {
		_licenceService = licenceService;
	}
	
	@Argument(index = 0, name = "systemId", description = "systemId for which to find licences", required = true, multiValued = false)
	String systemId = null;

	@Override
	protected Object doExecute() throws Exception {
		try {
			System.out.println("list of licences which will authenticate systemId='"+systemId+"'");

			Map<String, String> licenceMap = getLicenceService().getLicenceMapForSystemId(systemId);
			for (Entry<String, String> entry : licenceMap.entrySet()){
				System.out.println("   productId='"+entry.getKey()+"' licence='"+entry.getValue()+"'");
			}
		} catch (Exception e) {
			System.out.println("Error getting list of installed licences. Exception="+e);
		}
		return null;
	}


}