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

@Command(scope = "plugin-feature-mgr", name = "setRemotePluginServers", description="sets the remote plugin server urls, username and password.")
public class SetRemotePluginServersCommand extends OsgiCommandSupport {
	private static final Logger LOG = LoggerFactory.getLogger(SetRemotePluginServersCommand.class);

	private PluginFeatureManagerService _pluginFeatureManagerService;

	public PluginFeatureManagerService getPluginFeatureManagerService() {
		return _pluginFeatureManagerService;
	}

	public void setPluginFeatureManagerService( PluginFeatureManagerService pluginFeatureManager) {
		_pluginFeatureManagerService = pluginFeatureManager;
	}

	@Argument(index = 0, name = "urlList", description = "comma separated list of plugin manger urls to ask for manifest", required = true, multiValued = false)
    String urls = null;
	
	@Argument(index = 0, name = "RemoteUsername", description = "Remote Username to download manifest", required = false, multiValued = false)
    String remoteUsername = null;
	
	@Argument(index = 0, name = "RemotePassword", description = "Remote Password to download manifest", required = false, multiValued = false)
    String remotePassword = null;

	@Override
	protected Object doExecute() throws Exception {
		try{
			getPluginFeatureManagerService().updateRemotePluginServers(urls, remoteUsername, remotePassword);;
			
			String msg="set remote plugin servers urls="+urls;
			LOG.info(msg);
			System.out.println(msg);
		} catch (Exception e) {
			System.err.println("error setting remote plugin servers. Exception="+e);
			LOG.error("error setting remote plugin servers. Exception=",e);
		}
		return null;
	}
}