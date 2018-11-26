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

import jline.internal.Log;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.opennms.karaf.licencemgr.LicenceManagerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Command(scope = "licence-mgr", name = "installLicenceFromUrl", description="Installs a new plugin licence from a licence manager at a given url.")
public class InstallNewLicenceFromUrlCommand extends OsgiCommandSupport {
	private static final Logger LOG = LoggerFactory.getLogger(InstallNewLicenceFromUrlCommand.class);

	private LicenceManagerController _licenceManagerController;

	public LicenceManagerController getLicenceManagerController() {
		return _licenceManagerController;
	}

	public void setLicenceManagerController( LicenceManagerController licenceManagerController) {
		_licenceManagerController = licenceManagerController;
	}
	
	@Option(name = "-i", aliases =  "--systemId", description = "systemId for which to request licence. Will use default config if not set", required = false, multiValued = false)
    String systemIdStr = null;
	
	@Option(name = "-m", aliases =  "--useRequestMetadata", description = "use request metadata.", required = false, multiValued = false)
    String useRequestMetadata = null;

	@Option(name = "-u", aliases =  "--url", description = "url to download licence", required = true, multiValued = false)
    String urlStr = null;
	
	@Option(name = "-n", aliases =  "--remoteUsername", description = "Remote Username to download licence (Optional. If not provided will use default config)", required = true, multiValued = false)
    String remoteUserName = null;

	@Option(name = "-p", aliases =  "--remotePassword", description = "Remote Password to download licence (Optional. If not provided will use default config)", required = true, multiValued = false)
    String remotePassword = null;

	@Override
	protected Object doExecute() throws Exception {
		try{
			//String newManifestStr=null;
			String msg="Trying to install licence from url="+urlStr+", remoteUserName="+remoteUserName+ ", systemId="+systemIdStr;
			LOG.info(msg);
			System.out.println(msg);
			
			getLicenceManagerController().installRemoteLicencesFromSystemId(systemIdStr, urlStr, remoteUserName, remotePassword);
			//String installedManifest = getLicenceManagerController().getInstalledManifest();
            // msg=msg+"\nSuccess. Currently Installed Manifest='"+installedManifest+"'";
			
			msg=msg+"\nSuccess.";
			LOG.info(msg);
			System.out.println(msg);
		} catch (Exception e) {
			System.err.println("Error installing new licence from url="+urlStr+", systemIdStr="+systemIdStr+ " Exception="+e);
			LOG.error("Error installing new licence from url="+urlStr+", systemIdStr="+systemIdStr,e);
		}
		return null;
	}
}