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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.opennms.karaf.licencemgr.metadata.jaxb.ProductMetadata;
import org.opennms.karaf.licencemgr.metadata.jaxb.ProductSpecList;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.VerticalLayout;

public class ProductDescriptorTablePanel extends CustomComponent {

	/*- VaadinEditorProperties={"grid":"RegularGrid,5","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */


	@AutoGenerated
	private HorizontalLayout mainLayout;

	@AutoGenerated
	private VerticalLayout verticalLayout_1;

	@AutoGenerated
	private VerticalLayout productPanels;

	@AutoGenerated
	private VerticalLayout verticalLayout_2;

	@AutoGenerated
	private VerticalLayout controlsVerticalLayout;

	@AutoGenerated
	private ListSelect productListSelect;

	private static final long serialVersionUID = 1L;
	
	private static final int PRODUCT_LIST_SELECT_ROWS = 10; // default number of rows shown in product list

	private Map<String,ProductDescriptorPanel> panelIds = new HashMap<String,ProductDescriptorPanel>();

	private String selectedProductId=null;

	private Object selectedProductIdLock = new Object();
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */

	public ProductDescriptorTablePanel() {
		buildMainLayout();
		setCompositionRoot(mainLayout);

		// TODO add user code here

		productListSelect.setRows(PRODUCT_LIST_SELECT_ROWS); 	// Show n items and a scrollbar if there are more
		productListSelect.setNullSelectionAllowed(false);

		// Feedback on value changes
		productListSelect.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 4777915807221505438L;

			@Override
			public void valueChange(ValueChangeEvent event) {

				// set all panels invisible
				for(ProductDescriptorPanel pdp: panelIds.values()){
					pdp.setVisible(false);
				}

				//set selected panel visible
				if (productListSelect.getValue()!=null) {
					String selectedProdId = productListSelect.getValue().toString();
					ProductDescriptorPanel selectedProductDescriptorPanel = panelIds.get(selectedProdId);
					if (selectedProductDescriptorPanel!=null) {
						selectedProductDescriptorPanel.setVisible(true);
						synchronized (selectedProductIdLock){
							selectedProductId=selectedProdId;
						}
					}
				}
				mainLayout.markAsDirty();

			}
		});

	}


	public String getSelectedProductId(){
		String s =null;
		synchronized (selectedProductIdLock) {
			s = selectedProductId;
		}
		return s;
	}

	public synchronized void addProductList(ProductSpecList productSpecList){
		
		// default if empty entry
		List<ProductMetadata> speclist = new ArrayList<ProductMetadata>();
		
		if (productSpecList!=null ) speclist = productSpecList.getProductSpecList();

		Map<String,ProductMetadata> pmap = new TreeMap<String,ProductMetadata>();

		for (ProductMetadata pmeta: speclist){
			pmap.put(pmeta.getProductId(), pmeta);
		}

		productPanels.removeAllComponents();
		
		for (String productId: pmap.keySet()){
			
			// update/add product ids to list select without throwing value change event
			if (!productListSelect.containsId(productId)) productListSelect.addItem(productId);

			// add a new product descriptor panel and populate with ProductMetadata(
			ProductDescriptorPanel productDescriptorPanel= new ProductDescriptorPanel();
			productDescriptorPanel.setImmediate(true);
			productDescriptorPanel.setProductMetadata(pmap.get(productId));
			productDescriptorPanel.setVisible(false);
			
			productDescriptorPanel.setReadOnly(true);

			panelIds.put(productId, productDescriptorPanel);
			productPanels.addComponent(productDescriptorPanel);
		}
		
		// update/remove product ids from list select without throwing value change event
		List<Object> itemIds = new ArrayList<Object>(productListSelect.getItemIds());
		for (Object itemid: itemIds){
			if (! pmap.keySet().contains(itemid)) productListSelect.removeItem(itemid);
		}
		
		if (pmap.keySet().isEmpty()) {
			//if there are no panels to display display an empty panel
			selectedProductId=null;
			ProductDescriptorPanel productDescriptorPanel= new ProductDescriptorPanel();
			productDescriptorPanel.setReadOnly(true);
			productDescriptorPanel.setVisible(true);
			productPanels.addComponent(productDescriptorPanel);
		} else{
			// selects first value for display
			String selectedProdId = pmap.keySet().iterator().next();
			ProductDescriptorPanel selectedProductDescriptorPanel = panelIds.get(selectedProdId);
			if (selectedProductDescriptorPanel!=null) {
				selectedProductDescriptorPanel.setVisible(true);
				synchronized (selectedProductIdLock){
					selectedProductId=selectedProdId;
				}
			}
		}

	}

	public VerticalLayout getControlsVerticalLayout(){
		return controlsVerticalLayout;
	}


	@AutoGenerated
	private HorizontalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new HorizontalLayout();
		mainLayout.setCaption("Product Details");
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// verticalLayout_2
		verticalLayout_2 = buildVerticalLayout_2();
		mainLayout.addComponent(verticalLayout_2);
		mainLayout.setExpandRatio(verticalLayout_2, 1.0f);
		
		// verticalLayout_1
		verticalLayout_1 = buildVerticalLayout_1();
		mainLayout.addComponent(verticalLayout_1);
		mainLayout.setExpandRatio(verticalLayout_1, 1.0f);
		
		return mainLayout;
	}


	@AutoGenerated
	private VerticalLayout buildVerticalLayout_2() {
		// common part: create layout
		verticalLayout_2 = new VerticalLayout();
		verticalLayout_2.setImmediate(true);
		verticalLayout_2.setWidth("100.0%");
		verticalLayout_2.setHeight("100.0%");
		verticalLayout_2.setMargin(true);
		
		// productListSelect
		productListSelect = new ListSelect();
		productListSelect.setCaption("Product Id");
		productListSelect.setImmediate(true);
		productListSelect.setWidth("100.0%");
		productListSelect.setHeight("100.0%");
		verticalLayout_2.addComponent(productListSelect);
		
		// controlsVerticalLayout
		controlsVerticalLayout = new VerticalLayout();
		controlsVerticalLayout.setImmediate(true);
		controlsVerticalLayout.setWidth("100.0%");
		controlsVerticalLayout.setHeight("100.0%");
		controlsVerticalLayout.setMargin(false);
		verticalLayout_2.addComponent(controlsVerticalLayout);
		
		return verticalLayout_2;
	}


	@AutoGenerated
	private VerticalLayout buildVerticalLayout_1() {
		// common part: create layout
		verticalLayout_1 = new VerticalLayout();
		verticalLayout_1.setImmediate(true);
		verticalLayout_1.setWidth("100.0%");
		verticalLayout_1.setHeight("100.0%");
		verticalLayout_1.setMargin(false);
		
		// productPanels
		productPanels = new VerticalLayout();
		productPanels.setImmediate(true);
		productPanels.setWidth("100.0%");
		productPanels.setHeight("100.0%");
		productPanels.setMargin(false);
		verticalLayout_1.addComponent(productPanels);
		verticalLayout_1.setExpandRatio(productPanels, 1.0f);
		
		return verticalLayout_1;
	}

}
