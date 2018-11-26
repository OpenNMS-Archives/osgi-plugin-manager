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

package org.opennms.karaf.featuremgr.cmd;

import jline.internal.Log;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.opennms.karaf.featuremgr.PluginFeatureManagerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Command(scope = "plugin-feature-mgr", name = "installManifestFromUrl", description="Installs a new plugin manifest from a given url.")
public class InstallNewManifestFromUrlCommand extends OsgiCommandSupport {
	private static final Logger LOG = LoggerFactory.getLogger(InstallNewManifestFromUrlCommand.class);

	private PluginFeatureManagerService _pluginFeatureManagerService;

	public PluginFeatureManagerService getPluginFeatureManagerService() {
		return _pluginFeatureManagerService;
	}

	public void setPluginFeatureManagerService( PluginFeatureManagerService pluginFeatureManager) {
		_pluginFeatureManagerService = pluginFeatureManager;
	}
	
	@Argument(index = 0, name = "karafInstance", description = "karaf instance for which to request manifest", required = true, multiValued = false)
    String karafInstance = null;

	@Argument(index = 1, name = "url", description = "url to download manifest", required = true, multiValued = false)
    String urlStr = null;
	
	@Argument(index = 2, name = "RemoteUsername", description = "Remote Username to download manifest (optional)", required = false, multiValued = false)
    String remoteUserName = null;
	
	@Argument(index = 3, name = "RemotePassword", description = "Remote Password to download manifest (optional)", required = false, multiValued = false)
    String remotePassword = null;

	@Override
	protected Object doExecute() throws Exception {
		try{
			
			String newManifestStr=null;
			String msg="Trying to install manifest from url="+urlStr+", karafInstance="+karafInstance;
			LOG.info(msg);
			System.out.println(msg);
			
			getPluginFeatureManagerService().installNewManifestFromPluginManagerUrl(karafInstance, urlStr, remoteUserName, remotePassword);
			String installedManifest = getPluginFeatureManagerService().getInstalledManifest();

            msg=msg+"\nSuccess. Currently Installed Manifest='"+installedManifest+"'";
			LOG.info(msg);
			System.out.println(msg);
		} catch (Exception e) {
			System.err.println("Error installing new manifest from url="+urlStr+", karafInstance="+karafInstance+ " Exception="+e);
			LOG.error("Error installing new manifest from url="+urlStr+", karafInstance="+karafInstance,e);
		}
		return null;
	}
}