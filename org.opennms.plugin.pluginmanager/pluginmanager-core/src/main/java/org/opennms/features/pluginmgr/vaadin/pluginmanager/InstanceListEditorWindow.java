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

package org.opennms.features.pluginmgr.vaadin.pluginmanager;

import org.opennms.features.pluginmgr.SessionPluginManager;
import org.osgi.service.blueprint.container.BlueprintContainer;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class InstanceListEditorWindow extends Window {
	


	private static final long serialVersionUID = 1L;
	
	private SessionPluginManager sessionPluginManager=null;
	

	public InstanceListEditorWindow(SessionPluginManager sessionPluginManager) {
        super("Karaf List Editor"); // Set window caption
        
		this.sessionPluginManager=sessionPluginManager;
		
        center();
        // Disable the close button
        setClosable(false);

        // get editor from blueprint or use SimpleInstanceListEditor
        InstanceListEditor instanceListEditor= new SimpleInstanceListEditor();
        try {
        	BlueprintContainer container = sessionPluginManager.getBlueprintContainer();
        	InstanceListEditor factoryInstanceListEditor = (InstanceListEditor) container.getComponentInstance("instanceListEditor");
        	if (factoryInstanceListEditor!=null ) {
        		instanceListEditor = factoryInstanceListEditor;
        	} else {
        		//TODO LOG MESSAGE
        		System.out.println("instanceListEditor not defined in blueprintContainer. Using SimpleInstanceListEditor");
        	}
        } catch (Exception e) {
            throw new RuntimeException("problem loading InstanceListEditor from blueprintContainer: ", e);
        }
        
        instanceListEditor.setSessionPluginManager(sessionPluginManager);
        instanceListEditor.setParentWindow(this);
        
        VerticalLayout verticalLayout1 = new VerticalLayout();
        verticalLayout1.addComponent(instanceListEditor);
        setContent(verticalLayout1);

    }


}