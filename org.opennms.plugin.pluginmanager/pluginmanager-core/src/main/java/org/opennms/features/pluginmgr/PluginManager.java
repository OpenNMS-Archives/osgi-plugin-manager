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

package org.opennms.features.pluginmgr;

import java.util.Date;
import java.util.SortedMap;

import javax.xml.bind.annotation.XmlElement;

import org.opennms.features.pluginmgr.model.KarafEntryJaxb;
import org.opennms.features.pluginmgr.model.KarafManifestEntryJaxb;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceList;
import org.opennms.karaf.licencemgr.metadata.jaxb.ProductMetadata;
import org.opennms.karaf.licencemgr.metadata.jaxb.ProductSpecList;

public interface PluginManager {

	/**
	 * returns the password of the remote plugin server
	 * @return the pluginServerPassword
	 */
	public String getPluginServerPassword();

	/**
	 * 
	 * @param pluginServerPassword
	 */
	public void setPluginServerPassword(String pluginServerPassword);

	/**
	 * gets the username to access the plugin server
	 * @return the pluginServerUsername
	 */
	public String getPluginServerUsername();

	/**
	 * 
	 * @param pluginServerUsername
	 */
	public void setPluginServerUsername(String pluginServerUsername);

	/**
	 * gets the url to access the plugin server
	 * @return the pluginServerUrl
	 */
	public String getPluginServerUrl();

	/**
	 * 
	 * @param pluginServerUrl
	 */
	public void setPluginServerUrl(String pluginServerUrl);

	/**
	 * @return the licenceShoppingCartUrl
	 */
	public String getLicenceShoppingCartUrl();

	/**
	 * 
	 * @param licenceShoppingCartUrl
	 */
	public void setLicenceShoppingCartUrl(String licenceShoppingCartUrl);

	/**
	 * Sets basic data for PluginManager and persists all at once
	 * @param pluginServerUsername
	 * @param pluginServerPassword
	 * @param pluginServerUrl
	 * @param licenceShoppingCartUrl
	 */
	public void setPluginManagerBasicData(String pluginServerUsername,
			String pluginServerPassword, String pluginServerUrl,
			String licenceShoppingCartUrl);

	/**
	 * returns list of karaf instances which can be addressed by ui
	 * @return Map of key = karafInstanceName, value = karafInstanceUrl
	 */
	public SortedMap<String, KarafManifestEntryJaxb> getKarafInstances();

	/**
	 * 
	 */
	public void refreshAvailablePlugins();

	/**
	 * returns list of available plugins obtained from the plugin server
	 * @return the availablePlugins
	 */
	public ProductSpecList getAvailablePlugins();

	/**
	 * refreshes complete KarafEntryJaxb from remote karaf server
	 * throws exception if cannot refresh entry
	 */
	public KarafEntryJaxb refreshKarafEntry(String karafInstance);

	/**
	 * returns the time the karaf instance date was last updated from the instance
	 * returns null if never updated
	 * @param karafInstance
	 * @return
	 */
	public Date getKarafInstanceLastUpdated(String karafInstance);

	/**
	 * returns the time when the available plugins were last updated.
	 * null if never updated
	 * @return
	 */
	public Date getAvailablePluginsLastUpdated();

	/**
	 * returns the plugins installed in the given karaf instance
	 * @return the installedPlugins
	 */
	public ProductSpecList getInstalledPlugins(String karafInstance);

	/**
	 * returns the licenses installed in the karaf instance
	 * @return the installedLicenceList
	 */
	public LicenceList getInstalledLicenceList(String karafInstance);
	
	/**
	 * updates the installed licence list in for the karaf instance 
	 * if the karaf instance is known to the system
	 * @param licenceList
	 * @param karafInstance
	 */
	public void updateInstalledLicenceList(LicenceList licenceList, String karafInstance);
	
	/**
	 * updates the installed plugins list in for the karaf instance 
	 * if the karaf instance is known to the system
	 * @param installedPlugins
	 * @param karafInstance
	 */
	public void updateInstalledPlugins(ProductSpecList installedPlugins, String karafInstance);

	/**
	 * returns the system id of the karaf instance
	 * @return the systemId
	 */
	public String getSystemId(String karafInstance);

	/**
	 * sets the system id of the karaf instance
	 * @param systemId the systemId to set
	 */
	public void setSystemId(String systemId, String karafInstance);

	/**
	 * generates a random system id for the karaf instance
	 * @param karafInstance
	 * @return new system id
	 */
	public String generateRandomManifestSystemId(String karafInstance);

	/**
	 * adds a licence to the karaf instance
	 * @param licenceStr
	 * @param karafInstance
	 */
	public void addLicence(String licenceStr, String karafInstance);

	/**
	 * 
	 * @param selectedLicenceId
	 * @param karafInstance
	 */
	public void removeLicence(String selectedLicenceId,
			String karafInstance);

	/**
	 * installs a plugin for the product id in the selected karaf instance
	 * @param selectedProductId
	 * @param karafInstance
	 */
	public void installPlugin(String selectedProductId,
			String karafInstance);

	/**
	 * uninstalls a plugin for the product id in the selected karaf instance
	 * @param selectedProductId
	 * @param karafInstance
	 */
	public void unInstallPlugin(String selectedProductId,
			String karafInstance);

	/**
	 * returns the manifest of plugins scheduled to be installed in the given karaf instance
	 * returns an empty list if no entries are found
	 * @return the installedPlugins
	 */
	public ProductSpecList getPluginsManifest(String karafInstance);

	/**
	 * adds a plugin to the manifest of plugins scheduled to be installed in the given karaf instance
	 * @param selectedProductId
	 * @param karafInstance
	 */
	public void addPluginToManifest(String selectedProductId,
			String karafInstance);

	/**
	 * removes a plugin to the manifest of plugins scheduled to be installed in the given karaf instance
	 * @param selectedProductId
	 * @param karafInstance
	 */
	public void removePluginFromManifest(String selectedProductId,
			String karafInstance);

	/**
	 * sets the manifestSystemId for a given karafInstance
	 * @param manifestSystemId
	 * @param karafInstance
	 */
	public void setManifestSystemId(String manifestSystemId,
			String karafInstance);

	/**
	 * gets the manifestSystemId for a given karafInstance or null if no entry for karafInstance
	 * @param manifestSystemId
	 * @param karafInstance
	 */
	public String getManifestSystemId(String karafInstance);
	
	/**
	 * remoteIsAccessible tells the plugin manager that this karaf instance is updatable using ReST
	 * @param karafInstance
	 * @return Boolen true this karaf instance is updatable using ReST
	 */
	public Boolean getRemoteIsAccessible(String karafInstance);
	
	/**
	 * remoteIsAccessible tells the plugin manager that this karaf instance is updatable using ReST
	 * @param remoteIsAccessible
	 * @param karafInstance
	 */
	public void setRemoteIsAccessible(Boolean remoteIsAccessible, String karafInstance);
	
	/**
	 * allowUpdateMessages tells the plugin manager that this karaf instance is a parent and should not have licences and installed plugins
	 * updated from an external ReST push
	 * @param karafInstance
	 * @return
	 */
	public Boolean getAllowUpdateMessages(String karafInstance);
	
	/**
	 * allowUpdateMessages tells the plugin manager that this karaf instance is a parent and should not have licences and installed plugins
	 * @param allowUpdateMessages
	 * @param karafInstance
	 * @return
	 */
	public void setAllowUpdateMessages(Boolean allowUpdateMessages, String karafInstance);
	
	/**
	 * adds new karaf instance based on karafManifestEntryJaxb
	 * throws exception entry already exists or name not set
	 * @param karafManifestEntryJaxb
	 */
	public void addNewKarafInstance(KarafManifestEntryJaxb karafManifestEntryJaxb);
	
	/**
	 * deletes karaf instance from plugin manager. Throws error and will not delete localhost instance.
	 * @param karafInstance
	 */
	public void deleteKarafInstance(String karafInstance);

	/** 
	 * atomically updates password, username and instance url and whether the remote can be accessed behind a firewall
	 * @param karafInstanceUrl
	 * @param karafInstanceUserName
	 * @param karafInstancePassword
	 * @param remoteIsAccessible
	 * @param allowUpdateMessages
	 * @param karafInstance
	 */
	void updateAccessData(String karafInstanceUrl, String karafInstanceUserName, String karafInstancePassword, Boolean remoteIsAccessible, Boolean allowUpdateMessages, String karafInstance);

	/**
	 * adds a user defined plugin directly to the manifest for the given karaf instance
	 * the plugin is defined in the productMetadata
	 * @param productMetadata
	 * @param karafInstance
	 */
	public void addUserDefinedPluginToManifest(ProductMetadata productMetadata,
			String karafInstance);


}