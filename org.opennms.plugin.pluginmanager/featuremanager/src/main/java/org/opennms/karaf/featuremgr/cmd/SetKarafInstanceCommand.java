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

@Command(scope = "plugin-feature-mgr", name = "setKarafInstance", description="sets the karaf instance name to use when requesting manifest.")
public class SetKarafInstanceCommand extends OsgiCommandSupport {
	private static final Logger LOG = LoggerFactory.getLogger(SetKarafInstanceCommand.class);

	private PluginFeatureManagerService _pluginFeatureManagerService;

	public PluginFeatureManagerService getPluginFeatureManagerService() {
		return _pluginFeatureManagerService;
	}

	public void setPluginFeatureManagerService( PluginFeatureManagerService pluginFeatureManager) {
		_pluginFeatureManagerService = pluginFeatureManager;
	}

	@Argument(index = 0, name = "karafInstance", description = "karaf instance name to use when requesting manifest", required = true, multiValued = false)
    String karafInstance = null;
	

	@Override
	protected Object doExecute() throws Exception {
		try{
			getPluginFeatureManagerService().updateKarafInstance(karafInstance);;
			
			String msg="set remote karafInstance="+karafInstance;
			LOG.info(msg);
			System.out.println(msg);
		} catch (Exception e) {
			System.err.println("error setting karaf instance. Exception="+e);
			LOG.error("error setting karaf instance. Exception=",e);
		}
		return null;
	}
}