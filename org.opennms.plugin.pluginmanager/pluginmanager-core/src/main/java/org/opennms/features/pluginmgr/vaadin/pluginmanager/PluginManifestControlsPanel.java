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
import org.opennms.features.pluginmgr.SimpleStackTrace;
import org.opennms.karaf.licencemgr.metadata.jaxb.ProductSpecList;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class PluginManifestControlsPanel extends CustomComponent {

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	@AutoGenerated
	private VerticalLayout mainLayout;

	@AutoGenerated
	private Button manuallyAddManifestButton;

	@AutoGenerated
	private Button installManifestPluginButton;

	@AutoGenerated
	private Button removeManifestPluginButton;

	private static final long serialVersionUID = 1L;

	private SessionPluginManager sessionPluginManager=null;

	private ProductDescriptorTablePanel productDescriptorTablePanel=null;

	private SystemMessages systemMessages;
	
	private PluginManagerUIMainPanel pluginManagerUIMainPanel=null;
	
	private boolean remoteUpdateControlsEnabled=true;

	public void setRemoteUpdateControlsEnabled(boolean remoteUpdateControlsEnabled) {
		this.remoteUpdateControlsEnabled = remoteUpdateControlsEnabled;
		// set state of update control buttons to remoteUpdateControlsEnabled 
		installManifestPluginButton.setEnabled(remoteUpdateControlsEnabled);
	}


	public void setSessionPluginManager(SessionPluginManager sessionPluginManager) {
		this.sessionPluginManager = sessionPluginManager;
	}


	public void setProductDescriptorTablePanel(
			ProductDescriptorTablePanel productDescriptorTablePanel) {
		this.productDescriptorTablePanel = productDescriptorTablePanel;
	}

	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public PluginManifestControlsPanel() {
		buildMainLayout();
		setCompositionRoot(mainLayout);

		// manually add user code here
		
		// Handle the removeManifestPluginButton events with an anonymous class
		removeManifestPluginButton.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				systemMessages.clear();
				try{
					String selectedProductId = productDescriptorTablePanel.getSelectedProductId();
					sessionPluginManager.removePluginFromManifest(selectedProductId);
					systemMessages.info("Removed product Id "+selectedProductId+" from manifest for karaf instance: "+sessionPluginManager.getKarafInstance());
				    ProductSpecList productManifestList = sessionPluginManager.getPluginsManifest();
					if (productManifestList!=null) productDescriptorTablePanel.addProductList(productManifestList);
				} catch (Exception e){
					systemMessages.error("Problem removing manifest for karaf instance: "+sessionPluginManager.getKarafInstance(),e);
				}
			}
		});
		
		// Handle the reInstallPluginButton events with an anonymous class
		installManifestPluginButton.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				systemMessages.clear();
				String message="";
				try{
					String selectedProductId = productDescriptorTablePanel.getSelectedProductId();
					message = "Installing product Id "+selectedProductId+ " from Manifest for karaf instance: "+sessionPluginManager.getKarafInstance();
					systemMessages.info(message);
					sessionPluginManager.installPlugin(selectedProductId);
					message = message +"\nInstalled product Id "+selectedProductId;
					systemMessages.info(message);
					// forces update after manifest plugin is installed
					if (pluginManagerUIMainPanel!=null) pluginManagerUIMainPanel.updateDisplayValues();
				} catch (Exception e){
					systemMessages.error(message +"\n Problem installing manifest ",e);
				}
			}
		});
		
		// set up code to open instance editor window
		manuallyAddManifestButton.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				systemMessages.clear();
				
				ManualManifestEditorWindow manualManifestEditorWindow = new ManualManifestEditorWindow(sessionPluginManager);

				manualManifestEditorWindow.addCloseListener(new Window.CloseListener() {
					private static final long serialVersionUID = 1L;
					// inline close-listener updates main display when editor exits
					public void windowClose(CloseEvent e) {
						// forces update after new manifest plugin is added
						if (pluginManagerUIMainPanel!=null) pluginManagerUIMainPanel.updateDisplayValues();
					}
				});

				// Add it to the root component
				UI.getCurrent().addWindow(manualManifestEditorWindow);
			}
		});
		
	}

	public void setSystemMessages(SystemMessages systemMessages) {
		this.systemMessages=systemMessages;

	}


	/**
	 * @param pluginManagerUIMainPanel the pluginManagerUIMainPanel to set
	 */
	public void setPluginManagerUIMainPanel(
			PluginManagerUIMainPanel pluginManagerUIMainPanel) {
		this.pluginManagerUIMainPanel = pluginManagerUIMainPanel;
	}


	@AutoGenerated
	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// removeManifestPluginButton
		removeManifestPluginButton = new Button();
		removeManifestPluginButton
				.setCaption("Remove Selected Plugin From Manifest");
		removeManifestPluginButton.setImmediate(true);
		removeManifestPluginButton
				.setDescription("This command removes the selected plugin from the Manifest");
		removeManifestPluginButton.setWidth("-1px");
		removeManifestPluginButton.setHeight("-1px");
		mainLayout.addComponent(removeManifestPluginButton);
		
		// installManifestPluginButton
		installManifestPluginButton = new Button();
		installManifestPluginButton
				.setCaption("Install Plugin Selected From Manifest");
		installManifestPluginButton.setImmediate(true);
		installManifestPluginButton
				.setDescription("This command tries to install the selected plugin from the Manifest");
		installManifestPluginButton.setWidth("-1px");
		installManifestPluginButton.setHeight("-1px");
		mainLayout.addComponent(installManifestPluginButton);
		
		// manuallyAddManifestButton
		manuallyAddManifestButton = new Button();
		manuallyAddManifestButton.setCaption("Add Manual Manifest Entry");
		manuallyAddManifestButton.setImmediate(false);
		manuallyAddManifestButton
				.setDescription("This command allows a user to add manifest entry directly without using available plugins list");
		manuallyAddManifestButton.setWidth("-1px");
		manuallyAddManifestButton.setHeight("-1px");
		mainLayout.addComponent(manuallyAddManifestButton);
		
		return mainLayout;
	}
}
