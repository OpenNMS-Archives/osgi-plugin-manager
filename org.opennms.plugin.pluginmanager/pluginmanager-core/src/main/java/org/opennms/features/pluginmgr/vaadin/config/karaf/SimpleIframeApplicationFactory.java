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

import org.opennms.vaadin.extender.AbstractApplicationFactory;

import com.vaadin.ui.UI;

/**
 * A factory for creating Plugin Manager Administration Application objects.
 */
public class SimpleIframeApplicationFactory extends AbstractApplicationFactory {
	
	private String iframePageUrl;
	
	private Map<String, String> headerLinks;
	

	public String getIframePageUrl() {
		return iframePageUrl;
	}

	public void setIframePageUrl(String iframePageUrl) {
		this.iframePageUrl = iframePageUrl;
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


    /* (non-Javadoc)
     * @see org.opennms.vaadin.extender.AbstractApplicationFactory#getUI()
     */
    @Override
    public UI createUI() {
        SimpleIframeApplication simpleIframeApplication = new SimpleIframeApplication();
        simpleIframeApplication.setIframePageUrl(iframePageUrl);
        simpleIframeApplication.setHeaderLinks(headerLinks);;
        return simpleIframeApplication;
    }

    /* (non-Javadoc)
     * @see org.opennms.vaadin.extender.AbstractApplicationFactory#getUIClass()
     */
    @Override
    public Class<? extends UI> getUIClass() {
        return SimpleIframeApplication.class;
    }

}
