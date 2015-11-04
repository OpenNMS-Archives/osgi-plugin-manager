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

package org.opennms.features.pluginmgr.model;


import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennms.karaf.licencemgr.metadata.jaxb.ProductSpecList;


@XmlRootElement(name="pluginManagerData")
@XmlAccessorType(XmlAccessType.NONE)
public class PluginModelJaxb {


	@XmlElement(name="pluginServerPassword")
	private String pluginServerPassword = "admin";

	@XmlElement(name="pluginServerUsername")
	private String pluginServerUsername = "admin";

	@XmlElement(name="pluginServerUrl")
	private String pluginServerUrl = "http://localhost:8980";
	
	@XmlElement(name="licenceShoppingCartUrl")
	private String licenceShoppingCartUrl = "http://opennms.org";
	
	@XmlElementWrapper(name="karafDataMap")
	private SortedMap<String, KarafEntryJaxb> karafDataMap = new TreeMap<String, KarafEntryJaxb>();
	
	@XmlElementWrapper(name="karafManifestEntryMap")
	private SortedMap<String, KarafManifestEntryJaxb > karafManifestEntryMap = new TreeMap<String, KarafManifestEntryJaxb >();
	
	//TODO REMOVE
	//@XmlElementWrapper(name="karafManifestMap")
	//private SortedMap<String, ProductSpecList> karafManifestMap = new TreeMap<String, ProductSpecList>();
	
	//@XmlElementWrapper(name="karafManifestSystemIdMap")
	//private SortedMap<String, String> karafManifestSystemIdMap = new TreeMap<String, String>();

	@XmlElement(name="availablePlugins")
	private ProductSpecList availablePlugins;
	
	@XmlElement(name="availablePluginsLastUpdated")
	private Date availablePluginsLastUpdated = null;
	
	/**
	 * returns the password of the remote plugin server
	 * @return the pluginServerPassword
	 */
	public String getPluginServerPassword() {
		return pluginServerPassword;
	}

	/**
	 * sets the password of the plugin server
	 * @param pluginServerPassword the pluginServerPassword to set
	 */
	public void setPluginServerPassword(String pluginServerPassword) {
		this.pluginServerPassword = pluginServerPassword;
	}

	/**
	 * gets the username to access the plugin server
	 * @return the pluginServerUsername
	 */
	public String getPluginServerUsername() {
		return pluginServerUsername;
	}

	/**
	 * sets the username of the plugin server
	 * @param pluginServerUsername the pluginServerUsername to set
	 */
	public void setPluginServerUsername(String pluginServerUsername) {
		this.pluginServerUsername = pluginServerUsername;
	}

	/**
	 * gets the url to access the plugin server
	 * @return the pluginServerUrl
	 */
	public String getPluginServerUrl() {
		return pluginServerUrl;
	}

	/**
	 * sets the url of the plugin server
	 * @param pluginServerUrl the pluginServerUrl to set
	 */
	public void setPluginServerUrl(String pluginServerUrl) {
		this.pluginServerUrl = pluginServerUrl;
	}

	/**
	 * @return the licenceShoppingCartUrl
	 */
	public String getLicenceShoppingCartUrl() {
		return licenceShoppingCartUrl;
	}

	/**
	 * @param licenceShoppingCartUrl the licenceShoppingCartUrl to set
	 */
	public void setLicenceShoppingCartUrl(String licenceShoppingCartUrl) {
		this.licenceShoppingCartUrl = licenceShoppingCartUrl;
	}

	/**
	 * @return the karafDataMap
	 */
	public SortedMap<String, KarafEntryJaxb> getKarafDataMap() {
		return karafDataMap;
	}

	/**
	 * @param karafDataMap the karafDataMap to set
	 */
	public void setKarafDataMap(SortedMap<String, KarafEntryJaxb> karafDataMap) {
		this.karafDataMap = karafDataMap;
	}

	/**
	 * @return the availablePlugins
	 */
	public ProductSpecList getAvailablePlugins() {
		return availablePlugins;
	}

	/**
	 * @param availablePlugins the availablePlugins to set
	 */
	public void setAvailablePlugins(ProductSpecList availablePlugins) {
		this.availablePlugins = availablePlugins;
	}

	/**
	 * @return the availablePluginsLastUpdated
	 */
	public Date getAvailablePluginsLastUpdated() {
		return availablePluginsLastUpdated;
	}

	/**
	 * @param availablePluginsLastUpdated the availablePluginsLastUpdated to set
	 */
	public void setAvailablePluginsLastUpdated(Date availablePluginsLastUpdated) {
		this.availablePluginsLastUpdated = availablePluginsLastUpdated;
	}

	//TODO REMOVE
	/**
	 * karaf manifest map contains the a map of product manifests 
	 * key = karaf instance
	 * value = list of product specs to try and install in system
	 * @return the karafManifestMap
	 */
//	public SortedMap<String, ProductSpecList> getKarafManifestMap() {
//		return karafManifestMap;
//	}

	/**
	 * @param karafManifestMap the karafManifestMap to set
	 */
//	public void setKarafManifestMap( SortedMap<String, ProductSpecList> karafManifestMap) {
//		this.karafManifestMap = karafManifestMap;
//	}

	/**
	 * karaf KarafManifestSystemIdMap contains the a map of manifestSystemId's to karaf instances 
	 * key = karaf instance
	 * value = ManifestSystemId
	 * @return the karafManifestSystemIdMap
	 */
//	public SortedMap<String, String> getKarafManifestSystemIdMap() {
//		return karafManifestSystemIdMap;
//	}

	/**
	 * @param karafManifestSystemIdMap the karafManifestSystemIdMap to set
	 */
//	public void setKarafManifestSystemIdMap(
//			SortedMap<String, String> karafManifestSystemIdMap) {
//		this.karafManifestSystemIdMap = karafManifestSystemIdMap;
//	}

	/**
	 * karaf KarafManifestEntryJaxb contains a map static data by karaf instances
	 * key = karaf instance
	 * value = KarafManifestEntryJaxb manifest entry for the karef instance
	 * @return the karafManifestEntryMap
	 */
	public SortedMap<String, KarafManifestEntryJaxb > getKarafManifestEntryMap() {
		return karafManifestEntryMap;
	}

	/**
	 * @param karafManifestEntryMap the karafManifestEntryMap to set
	 */
	public void setKarafManifestEntryMap(SortedMap<String, KarafManifestEntryJaxb > karafManifestEntryMap) {
		this.karafManifestEntryMap = karafManifestEntryMap;
	}



}
