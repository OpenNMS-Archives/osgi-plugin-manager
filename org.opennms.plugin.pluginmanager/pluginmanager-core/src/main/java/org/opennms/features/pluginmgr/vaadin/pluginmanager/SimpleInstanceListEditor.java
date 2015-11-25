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


import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.opennms.features.pluginmgr.SessionPluginManager;
import org.opennms.features.pluginmgr.model.KarafManifestEntryJaxb;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;


public class SimpleInstanceListEditor extends InstanceListEditor  {


	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	@AutoGenerated
	private VerticalLayout mainLayout;

	@AutoGenerated
	private HorizontalLayout horizontalLayout_1;

	@AutoGenerated
	private VerticalLayout verticalLayout_3;

	@AutoGenerated
	private TextArea messageTextArea;

	@AutoGenerated
	private Button saveKarafInstanceManifestButton;

	@AutoGenerated
	private CheckBox allowUpdateMessagesCheckBox;

	@AutoGenerated
	private CheckBox remoteIsAccessibleCheckBox;

	@AutoGenerated
	private TextField instancePasswordTextField;

	@AutoGenerated
	private TextField instanceUsernameTextField;

	@AutoGenerated
	private TextField currentKarafUrlTextField;

	@AutoGenerated
	private TextField karafInstanceSelectedTextField;

	@AutoGenerated
	private Label instructionLabel;

	@AutoGenerated
	private VerticalLayout verticalLayout_2;

	@AutoGenerated
	private VerticalLayout deleteConfirmVerticalLayout;

	@AutoGenerated
	private HorizontalLayout horizontalLayout_2;

	@AutoGenerated
	private Button deleteKarafInstanceManifestButton;

	@AutoGenerated
	private Button deleteNoButton;

	@AutoGenerated
	private Label toBeDeletedLabel;

	@AutoGenerated
	private Button askDeleteButton;

	@AutoGenerated
	private Button addKarafInstanceManifestButton;

	@AutoGenerated
	private ListSelect karafListSelect;

	@AutoGenerated
	private Button exitButton;

	private static final long serialVersionUID = 1L;

	// default length of karaf instance list
	private static final int KARAF_LIST_SELECT_ROWS = 10;


	//used to instantiate / update a local entry
	KarafManifestEntryJaxb localKarafManifest= new KarafManifestEntryJaxb();

	private boolean newManifestEntry = false;

	private String currentKarafInstance=null;
	
	private String deleteCandidate=null;
	
	@Override
	public void setSessionPluginManager(SessionPluginManager sessionPluginManager) {
		super.setSessionPluginManager(sessionPluginManager);
		// get the initial display values
		updateDisplayValues();
	}

	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public SimpleInstanceListEditor() {
		super();

		buildMainLayout();
		setCompositionRoot(mainLayout);

		// manually add user code here

		// karaf select panel
		karafListSelect.setRows(KARAF_LIST_SELECT_ROWS); 	// Show n items and a scrollbar if there are more
		karafListSelect.setNullSelectionAllowed(false);
		karafListSelect.setImmediate(true);

		// Feedback on list value changes
		karafListSelect.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				deleteConfirmVerticalLayout.setVisible(false);
				messageTextArea.setValue("");
				String karafInstanceSelected=null;
				try {
					if (karafListSelect.getValue()!=null) {
						karafInstanceSelected = karafListSelect.getValue().toString();
						getSessionPluginManager().setKarafInstance(karafInstanceSelected);
						messageTextArea.setValue("karaf instance changed to "+karafInstanceSelected);
					}
				} catch (Exception e){
					messageTextArea.setValue("problem changing karaf instance : "+e.getMessage());
				}
				newManifestEntry=false;
				updateDisplayValues();
			}
		});
		
		// handle allowUpdateMessagesCheckBox
		allowUpdateMessagesCheckBox.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			public void valueChange(ValueChangeEvent event) {
				deleteConfirmVerticalLayout.setVisible(false);
				boolean allowUpdateMessages = (Boolean) event.getProperty().getValue();
				localKarafManifest.setAllowUpdateMessages(allowUpdateMessages);
			}
		});
		
		// handle remoteIsAccessibleCheckBox
		remoteIsAccessibleCheckBox.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			public void valueChange(ValueChangeEvent event) {
				deleteConfirmVerticalLayout.setVisible(false);
				boolean allowUpdateMessages = (Boolean) event.getProperty().getValue();
				localKarafManifest.setAllowUpdateMessages(allowUpdateMessages);
			}
		});

		// add karaf button
		addKarafInstanceManifestButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				deleteConfirmVerticalLayout.setVisible(false);
				messageTextArea.setValue("fill in new karaf instance values and click save");
				localKarafManifest= new KarafManifestEntryJaxb();
				newManifestEntry=true;
				refreshKarafDisplayValues();
			}
		});

		//delete karaf button 
		saveKarafInstanceManifestButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			public void buttonClick(ClickEvent event) {
				deleteConfirmVerticalLayout.setVisible(false);
				messageTextArea.setValue("");
				localKarafManifest.setKarafInstanceName(karafInstanceSelectedTextField.getValue());
				localKarafManifest.setKarafInstanceUrl(currentKarafUrlTextField.getValue());
				localKarafManifest.setKarafInstanceUserName(instanceUsernameTextField.getValue());
				localKarafManifest.setKarafInstancePassword(instancePasswordTextField.getValue());
				localKarafManifest.setRemoteIsAccessible(remoteIsAccessibleCheckBox.getValue());
				localKarafManifest.setAllowUpdateMessages(allowUpdateMessagesCheckBox.getValue());

				// check that URL is formatted OK
				boolean urlFormattedOk=true;
				if (localKarafManifest.getKarafInstanceUrl()==null || "".equals(localKarafManifest.getKarafInstanceUrl())) {
					urlFormattedOk=false;
				} else try {
					URL u = new URL(localKarafManifest.getKarafInstanceUrl());
					// this would check for the protocol
					u.toURI(); // does the extra checking required for validation of URI 
				} catch (MalformedURLException e1) {
					urlFormattedOk=false;
				} catch (URISyntaxException e) {
					urlFormattedOk=false;
				}
				if (!urlFormattedOk){
					messageTextArea.setValue("URL field is not correctly formatted as url");
				} else 	if (newManifestEntry==true){
					String instanceName=localKarafManifest.getKarafInstanceName();
					if (instanceName==null || "".equals(instanceName) || instanceName.contains(" ")) {
						messageTextArea.setValue("karaf instance name must be set and must not contain spaces");
					} else if ("localhost".equals(instanceName)){
						messageTextArea.setValue("localhost cannot be used for new instance name");
					} else if (getSessionPluginManager().getKarafInstances().containsKey(instanceName)){
						messageTextArea.setValue("instance '"+instanceName+ "' is already defined");
					} else { // add new instance
						try{
							getSessionPluginManager().addNewKarafInstance(localKarafManifest);
							messageTextArea.setValue("added new karaf instance '"+instanceName+ "'");
							newManifestEntry=false;
							updateDisplayValues();
						} catch (Exception e){
							messageTextArea.setValue("problem adding karaf instance '"+instanceName+ "' : "+e.getMessage());
						}
					} 
				} else { // just update values
					getSessionPluginManager().updateAccessData(localKarafManifest.getKarafInstanceUrl(), 
							localKarafManifest.getKarafInstanceUserName(), 
							localKarafManifest.getKarafInstancePassword(), 
							localKarafManifest.getRemoteIsAccessible(),
							localKarafManifest.getAllowUpdateMessages());

					messageTextArea.setValue("saved karaf instance '"+ getSessionPluginManager().getKarafInstance()+ "'");
					updateDisplayValues();
				}
				refreshKarafDisplayValues();

			}
		});
		
		// delete safety panel checks 
		deleteConfirmVerticalLayout.setVisible(false);
		
		// delete no button - just hide yes option
		deleteNoButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			public void buttonClick(ClickEvent event) {
				deleteConfirmVerticalLayout.setVisible(false);
			}
		});
		
		// ask delete button show yes 
		askDeleteButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			public void buttonClick(ClickEvent event) {
				deleteCandidate=currentKarafInstance;
				toBeDeletedLabel.setValue(deleteCandidate);
				deleteConfirmVerticalLayout.setVisible(true);
			}
		});
		

		//delete karaf button 
		deleteKarafInstanceManifestButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			public void buttonClick(ClickEvent event) {
				deleteConfirmVerticalLayout.setVisible(false);
				messageTextArea.setValue("");
				
				if (deleteCandidate==null || "".equals(deleteCandidate)) {
					messageTextArea.setValue("karaf instance deleteCandidate must be chosen");
				} else if ("localhost".equals(deleteCandidate)){
					messageTextArea.setValue("cannot delete localhost karaf instance");
				} else {
					try{
						getSessionPluginManager().deleteKarafInstance(deleteCandidate);
						messageTextArea.setValue("karaf instance '"+deleteCandidate+ "' deleted");
					} catch (Exception e){
						messageTextArea.setValue("error deleting '"+deleteCandidate+ "' :"+e.getMessage());
					} finally {
						deleteCandidate=null;
					}
				}
				
				updateDisplayValues();
			}
		});

		// exit button
		exitButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			public void buttonClick(ClickEvent event) {
				if(getParentWindow()!=null) getParentWindow().close(); // Close the sub-window
			}
		});


	}

	public void refreshKarafDisplayValues(){
		karafInstanceSelectedTextField.setReadOnly(false);
		karafInstanceSelectedTextField.setValue( (localKarafManifest.getKarafInstanceName()==null) ? "" : localKarafManifest.getKarafInstanceName());
		currentKarafUrlTextField.setValue( (localKarafManifest.getKarafInstanceUrl()==null) ? "" : localKarafManifest.getKarafInstanceUrl());
		instanceUsernameTextField.setValue( (localKarafManifest.getKarafInstanceUserName()==null) ? "" : localKarafManifest.getKarafInstanceUserName());
		instancePasswordTextField.setValue( (localKarafManifest.getKarafInstancePassword()==null) ? "" : localKarafManifest.getKarafInstancePassword());
		remoteIsAccessibleCheckBox.setValue(localKarafManifest.getRemoteIsAccessible());
		allowUpdateMessagesCheckBox.setValue(localKarafManifest.getAllowUpdateMessages());
		
		if (newManifestEntry) {
			instructionLabel.setValue("Enter and save new instance");
			karafInstanceSelectedTextField.setReadOnly(false);
		} else{
			instructionLabel.setValue("Update values and save");
			karafInstanceSelectedTextField.setReadOnly(true);
		}

		mainLayout.markAsDirty();
	}


	public void updateDisplayValues(){
		deleteConfirmVerticalLayout.setVisible(false);

		// update/add available instances without throwing value change event
		Set<String> instanceNames = getSessionPluginManager().getKarafInstances().keySet();
		for (String instanceName: instanceNames ){
			if (!karafListSelect.containsId(instanceName)) karafListSelect.addItem(instanceName);
		};
		List<Object> itemIds = new ArrayList<Object>(karafListSelect.getItemIds());
		for (Object itemid: itemIds){
			if (! instanceNames.contains(itemid)) karafListSelect.removeItem(itemid);
		}

		currentKarafInstance =getSessionPluginManager().getKarafInstance();

		localKarafManifest = new KarafManifestEntryJaxb();

		KarafManifestEntryJaxb currentKarafManifest = getSessionPluginManager().getKarafInstances().get(currentKarafInstance);
		if (currentKarafManifest!=null){
			localKarafManifest.setKarafInstanceName(currentKarafManifest.getKarafInstanceName());
			localKarafManifest.setKarafInstanceUrl(currentKarafManifest.getKarafInstanceUrl());
			localKarafManifest.setKarafInstanceUserName(currentKarafManifest.getKarafInstanceUserName());
			localKarafManifest.setKarafInstancePassword(currentKarafManifest.getKarafInstancePassword());
			localKarafManifest.setRemoteIsAccessible(currentKarafManifest.getRemoteIsAccessible());
			localKarafManifest.setAllowUpdateMessages(currentKarafManifest.getAllowUpdateMessages());
		}

		refreshKarafDisplayValues();

	}

	@AutoGenerated
	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// exitButton
		exitButton = new Button();
		exitButton.setCaption("Exit");
		exitButton.setImmediate(true);
		exitButton.setWidth("-1px");
		exitButton.setHeight("-1px");
		mainLayout.addComponent(exitButton);
		mainLayout.setComponentAlignment(exitButton, new Alignment(6));
		
		// horizontalLayout_1
		horizontalLayout_1 = buildHorizontalLayout_1();
		mainLayout.addComponent(horizontalLayout_1);
		
		return mainLayout;
	}

	@AutoGenerated
	private HorizontalLayout buildHorizontalLayout_1() {
		// common part: create layout
		horizontalLayout_1 = new HorizontalLayout();
		horizontalLayout_1.setImmediate(false);
		horizontalLayout_1.setWidth("-1px");
		horizontalLayout_1.setHeight("-1px");
		horizontalLayout_1.setMargin(true);
		horizontalLayout_1.setSpacing(true);
		
		// verticalLayout_2
		verticalLayout_2 = buildVerticalLayout_2();
		horizontalLayout_1.addComponent(verticalLayout_2);
		
		// verticalLayout_3
		verticalLayout_3 = buildVerticalLayout_3();
		horizontalLayout_1.addComponent(verticalLayout_3);
		horizontalLayout_1.setExpandRatio(verticalLayout_3, 1.0f);
		
		return horizontalLayout_1;
	}

	@AutoGenerated
	private VerticalLayout buildVerticalLayout_2() {
		// common part: create layout
		verticalLayout_2 = new VerticalLayout();
		verticalLayout_2.setImmediate(false);
		verticalLayout_2.setWidth("-1px");
		verticalLayout_2.setHeight("-1px");
		verticalLayout_2.setMargin(true);
		verticalLayout_2.setSpacing(true);
		
		// karafListSelect
		karafListSelect = new ListSelect();
		karafListSelect.setCaption("Karaf Manifest Instances");
		karafListSelect.setImmediate(false);
		karafListSelect.setWidth("-1px");
		karafListSelect.setHeight("-1px");
		verticalLayout_2.addComponent(karafListSelect);
		
		// addKarafInstanceManifestButton
		addKarafInstanceManifestButton = new Button();
		addKarafInstanceManifestButton.setCaption("Add Karaf Instance");
		addKarafInstanceManifestButton.setImmediate(true);
		addKarafInstanceManifestButton.setWidth("-1px");
		addKarafInstanceManifestButton.setHeight("-1px");
		verticalLayout_2.addComponent(addKarafInstanceManifestButton);
		
		// askDeleteButton
		askDeleteButton = new Button();
		askDeleteButton.setCaption("Delete Karaf Instance");
		askDeleteButton.setImmediate(true);
		askDeleteButton.setWidth("-1px");
		askDeleteButton.setHeight("-1px");
		verticalLayout_2.addComponent(askDeleteButton);
		
		// deleteConfirmVerticalLayout
		deleteConfirmVerticalLayout = buildDeleteConfirmVerticalLayout();
		verticalLayout_2.addComponent(deleteConfirmVerticalLayout);
		
		return verticalLayout_2;
	}

	@AutoGenerated
	private VerticalLayout buildDeleteConfirmVerticalLayout() {
		// common part: create layout
		deleteConfirmVerticalLayout = new VerticalLayout();
		deleteConfirmVerticalLayout.setCaption("delete - are you sure?");
		deleteConfirmVerticalLayout.setImmediate(false);
		deleteConfirmVerticalLayout.setWidth("100.0%");
		deleteConfirmVerticalLayout.setHeight("-1px");
		deleteConfirmVerticalLayout.setMargin(true);
		deleteConfirmVerticalLayout.setSpacing(true);
		
		// toBeDeletedLabel
		toBeDeletedLabel = new Label();
		toBeDeletedLabel.setImmediate(false);
		toBeDeletedLabel.setWidth("-1px");
		toBeDeletedLabel.setHeight("-1px");
		toBeDeletedLabel.setValue("Label");
		deleteConfirmVerticalLayout.addComponent(toBeDeletedLabel);
		
		// horizontalLayout_2
		horizontalLayout_2 = buildHorizontalLayout_2();
		deleteConfirmVerticalLayout.addComponent(horizontalLayout_2);
		
		return deleteConfirmVerticalLayout;
	}

	@AutoGenerated
	private HorizontalLayout buildHorizontalLayout_2() {
		// common part: create layout
		horizontalLayout_2 = new HorizontalLayout();
		horizontalLayout_2.setImmediate(false);
		horizontalLayout_2.setWidth("-1px");
		horizontalLayout_2.setHeight("-1px");
		horizontalLayout_2.setMargin(false);
		horizontalLayout_2.setSpacing(true);
		
		// deleteNoButton
		deleteNoButton = new Button();
		deleteNoButton.setCaption("No");
		deleteNoButton.setImmediate(true);
		deleteNoButton.setWidth("-1px");
		deleteNoButton.setHeight("-1px");
		horizontalLayout_2.addComponent(deleteNoButton);
		
		// deleteKarafInstanceManifestButton
		deleteKarafInstanceManifestButton = new Button();
		deleteKarafInstanceManifestButton.setCaption("Yes Delete");
		deleteKarafInstanceManifestButton.setImmediate(true);
		deleteKarafInstanceManifestButton
				.setDescription("This will delete the karaf instance and all associated data");
		deleteKarafInstanceManifestButton.setWidth("-1px");
		deleteKarafInstanceManifestButton.setHeight("-1px");
		horizontalLayout_2.addComponent(deleteKarafInstanceManifestButton);
		
		return horizontalLayout_2;
	}

	@AutoGenerated
	private VerticalLayout buildVerticalLayout_3() {
		// common part: create layout
		verticalLayout_3 = new VerticalLayout();
		verticalLayout_3.setImmediate(false);
		verticalLayout_3.setWidth("-1px");
		verticalLayout_3.setHeight("-1px");
		verticalLayout_3.setMargin(true);
		verticalLayout_3.setSpacing(true);
		
		// instructionLabel
		instructionLabel = new Label();
		instructionLabel.setImmediate(false);
		instructionLabel.setWidth("100.0%");
		instructionLabel.setHeight("-1px");
		instructionLabel.setValue("Label");
		verticalLayout_3.addComponent(instructionLabel);
		
		// karafInstanceSelectedTextField
		karafInstanceSelectedTextField = new TextField();
		karafInstanceSelectedTextField.setCaption("Instance Name");
		karafInstanceSelectedTextField.setImmediate(false);
		karafInstanceSelectedTextField.setWidth("100.0%");
		karafInstanceSelectedTextField.setHeight("-1px");
		verticalLayout_3.addComponent(karafInstanceSelectedTextField);
		
		// currentKarafUrlTextField
		currentKarafUrlTextField = new TextField();
		currentKarafUrlTextField.setCaption("Instance Url");
		currentKarafUrlTextField.setImmediate(false);
		currentKarafUrlTextField.setWidth("100.0%");
		currentKarafUrlTextField.setHeight("-1px");
		verticalLayout_3.addComponent(currentKarafUrlTextField);
		
		// instanceUsernameTextField
		instanceUsernameTextField = new TextField();
		instanceUsernameTextField.setCaption("Instance User Name");
		instanceUsernameTextField.setImmediate(false);
		instanceUsernameTextField.setWidth("100.0%");
		instanceUsernameTextField.setHeight("-1px");
		verticalLayout_3.addComponent(instanceUsernameTextField);
		
		// instancePasswordTextField
		instancePasswordTextField = new TextField();
		instancePasswordTextField.setCaption("Instance Password");
		instancePasswordTextField.setImmediate(false);
		instancePasswordTextField.setWidth("100.0%");
		instancePasswordTextField.setHeight("-1px");
		verticalLayout_3.addComponent(instancePasswordTextField);
		
		// remoteIsAccessibleCheckBox
		remoteIsAccessibleCheckBox = new CheckBox();
		remoteIsAccessibleCheckBox.setCaption("  Remote is Accessible");
		remoteIsAccessibleCheckBox.setImmediate(false);
		remoteIsAccessibleCheckBox
				.setDescription("Check if Remote cannot be direcly updated using ReST commands. (i.e behind firewall)");
		remoteIsAccessibleCheckBox.setWidth("-1px");
		remoteIsAccessibleCheckBox.setHeight("-1px");
		verticalLayout_3.addComponent(remoteIsAccessibleCheckBox);
		
		// allowUpdateMessagesCheckBox
		allowUpdateMessagesCheckBox = new CheckBox();
		allowUpdateMessagesCheckBox
				.setCaption("  Allow Status Update from Remote");
		allowUpdateMessagesCheckBox.setImmediate(false);
		allowUpdateMessagesCheckBox
				.setDescription("Status update messages are allowed from remote");
		allowUpdateMessagesCheckBox.setWidth("-1px");
		allowUpdateMessagesCheckBox.setHeight("-1px");
		verticalLayout_3.addComponent(allowUpdateMessagesCheckBox);
		
		// saveKarafInstanceManifestButton
		saveKarafInstanceManifestButton = new Button();
		saveKarafInstanceManifestButton
				.setCaption("Update / Save Karaf Instance");
		saveKarafInstanceManifestButton.setImmediate(true);
		saveKarafInstanceManifestButton.setWidth("-1px");
		saveKarafInstanceManifestButton.setHeight("-1px");
		verticalLayout_3.addComponent(saveKarafInstanceManifestButton);
		
		// messageTextArea
		messageTextArea = new TextArea();
		messageTextArea.setImmediate(true);
		messageTextArea.setWidth("-1px");
		messageTextArea.setHeight("-1px");
		verticalLayout_3.addComponent(messageTextArea);
		
		return verticalLayout_3;
	}

}
