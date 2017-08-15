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
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

public class InstalledPluginControlsPanel extends CustomComponent {

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	@AutoGenerated
	private VerticalLayout mainLayout;

	@AutoGenerated
	private Button uninstallPluginButton;

	@AutoGenerated
	private Button reInstallPluginButton;

	private static final long serialVersionUID = 1L;

	private SessionPluginManager sessionPluginManager=null;

	private ProductDescriptorTablePanel productDescriptorTablePanel=null;

	private SystemMessages systemMessages;

	private boolean remoteUpdateControlsEnabled=true;

	public void setRemoteUpdateControlsEnabled(boolean remoteUpdateControlsEnabled) {
		this.remoteUpdateControlsEnabled = remoteUpdateControlsEnabled;
		// set state of update control buttons to remoteUpdateControlsEnabled 
		uninstallPluginButton.setEnabled(remoteUpdateControlsEnabled);
		reInstallPluginButton.setEnabled(remoteUpdateControlsEnabled);
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
	public InstalledPluginControlsPanel() {
		buildMainLayout();
		setCompositionRoot(mainLayout);

		// manually add user code here

		// Handle the installPluginButton events with an anonymous class
		uninstallPluginButton.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				systemMessages.clear();
				try{
					String selectedProductId = productDescriptorTablePanel.getSelectedProductId();
					sessionPluginManager.unInstallPlugin(selectedProductId);
					systemMessages.info("uninstalled product Id "+selectedProductId);
					ProductSpecList productSpeclist = sessionPluginManager.getInstalledPlugins();
					if (productSpeclist!=null) productDescriptorTablePanel.addProductList(productSpeclist);
				} catch (Exception e){
					systemMessages.error("Problem uninstalling plugin.",e);
				}
			}
		});

		// Handle the reInstallPluginButton events with an anonymous class
		reInstallPluginButton.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				systemMessages.clear();
				String message="";
				try{
					String selectedProductId = productDescriptorTablePanel.getSelectedProductId();
					message = "Re-installing product Id "+selectedProductId;
					systemMessages.info(message);
					try {
						sessionPluginManager.unInstallPlugin(selectedProductId);
						message = message +"\nUn-installed product Id "+selectedProductId;
						systemMessages.info(message);
					} catch (Exception e){
						message = message +"\nCould not Un-install product Id "+selectedProductId;
						systemMessages.error(message,e);
					}
					productDescriptorTablePanel.markAsDirty();
					try {
						sessionPluginManager.installPlugin(selectedProductId);
						message = message +"\nRe-installed product Id "+selectedProductId;
						systemMessages.info(message);
					} catch (Exception e){
						message = message +"\nCould not Re-install product Id "+selectedProductId;
						systemMessages.error(message,e);
					}
					ProductSpecList productSpeclist = sessionPluginManager.getInstalledPlugins();
					if (productSpeclist!=null) productDescriptorTablePanel.addProductList(productSpeclist);
				} catch (Exception e){
					systemMessages.error(message,e);
				}
			}
		});

	}

	public void setSystemMessages(SystemMessages systemMessages) {
		this.systemMessages=systemMessages;

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

		// reInstallPluginButton
		reInstallPluginButton = new Button();
		reInstallPluginButton.setCaption("Reinstall / Restart Selected Plugin");
		reInstallPluginButton.setImmediate(true);
		reInstallPluginButton
		.setDescription("This command reinstalls and restarts the selected plugin. (This allows you to re-apply a new licence to a restarted plugin).");
		reInstallPluginButton.setWidth("-1px");
		reInstallPluginButton.setHeight("-1px");
		mainLayout.addComponent(reInstallPluginButton);

		// uninstallPluginButton
		uninstallPluginButton = new Button();
		uninstallPluginButton.setCaption("Uninstall Selected Plugin");
		uninstallPluginButton.setImmediate(true);
		uninstallPluginButton
		.setDescription("This command uninstalls the selected plugin.");
		uninstallPluginButton.setWidth("-1px");
		uninstallPluginButton.setHeight("-1px");
		mainLayout.addComponent(uninstallPluginButton);

		return mainLayout;
	}
}
