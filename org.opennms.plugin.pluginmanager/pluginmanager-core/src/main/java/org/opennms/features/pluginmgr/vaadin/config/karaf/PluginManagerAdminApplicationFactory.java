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

package org.opennms.features.pluginmgr.vaadin.config.karaf;


import java.util.Map;

import org.opennms.features.pluginmgr.PluginManager;
import org.opennms.features.pluginmgr.SessionPluginManager;
import org.opennms.vaadin.extender.AbstractApplicationFactory;
import org.osgi.service.blueprint.container.BlueprintContainer;

import com.vaadin.ui.UI;

/**
 * A factory for creating Plugin Manager Administration Application objects.
 */
public class PluginManagerAdminApplicationFactory extends AbstractApplicationFactory {
	
    
    private PluginManager pluginManager;
    
    private BlueprintContainer blueprintContainer;
    
    // headerLinks map of key= name and value=url for links to be placed in header of page
    private Map<String, String> headerLinks;

	
	public PluginManager getPluginManager() {
		return pluginManager;
	}

	public void setPluginManager(PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}
	
	/**
	 * headerLinks map of key= name and value=url for links to be placed in header of page
	 * @return
	 */
	public Map<String,String> getHeaderLinks() {
		return headerLinks;
	}


	/**
	 * @param headerLinks map of key= name and value=url for links to be placed in header of page
	 */
	public void setHeaderLinks(Map<String,String> headerLinks) {
		this.headerLinks = headerLinks;
	}

    /**
	 * @return the blueprintContainer
	 */
	public BlueprintContainer getBlueprintContainer() {
		return blueprintContainer;
	}

	/**
	 * @param blueprintContainer the blueprintContainer to set
	 */
	public void setBlueprintContainer(BlueprintContainer blueprintContainer) {
		this.blueprintContainer = blueprintContainer;
	}

	
    /* (non-Javadoc)
     * @see org.opennms.vaadin.extender.AbstractApplicationFactory#getUI()
     */
    @Override
    public UI createUI() {
        PluginManagerAdminApplication pluginManagerAdminApplication = new PluginManagerAdminApplication();
        pluginManagerAdminApplication.setHeaderLinks(headerLinks);
        
        //local plugin model persists data for session instance
        SessionPluginManager sessionPluginManager=new SessionPluginManager();
        sessionPluginManager.setPluginManager(pluginManager);
        sessionPluginManager.setBlueprintContainer(blueprintContainer);
        pluginManagerAdminApplication.setSessionPluginManager(sessionPluginManager);
        return pluginManagerAdminApplication;
    }

    /* (non-Javadoc)
     * @see org.opennms.vaadin.extender.AbstractApplicationFactory#getUIClass()
     */
    @Override
    public Class<? extends UI> getUIClass() {
        return PluginManagerAdminApplication.class;
    }

}
