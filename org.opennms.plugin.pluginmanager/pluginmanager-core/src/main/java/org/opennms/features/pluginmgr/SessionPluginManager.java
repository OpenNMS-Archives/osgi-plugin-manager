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

import org.opennms.features.pluginmgr.model.KarafManifestEntryJaxb;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceList;
import org.opennms.karaf.licencemgr.metadata.jaxb.ProductMetadata;
import org.opennms.karaf.licencemgr.metadata.jaxb.ProductSpecList;
import org.osgi.service.blueprint.container.BlueprintContainer;

/**
 * this model is a proxy for the PluginManagerImpl shared by all ui sessions. 
 * It allows some extra variables to be persisted and changed within a session
 * so that a session can choose which karaf instance is being operated on
 * @author admin
 *
 */
public class SessionPluginManager {

	private String karafInstance="localhost"; //default value

	private PluginManager pluginManager=null;
	
	private BlueprintContainer blueprintContainer=null;

	/** 
	 * gets the karaf instance which will be used for other commands
	 * @return
	 */
	public synchronized String getKarafInstance() {
		return karafInstance;
	}

	/**
	 * sets the karaf instance which will be used for other commands
	 * @param karafInstance
	 */
	public synchronized void setKarafInstance(String karafInstance) {
		this.karafInstance = karafInstance;
	}
	
	public String getKarafUrl(){
		return pluginManager.getKarafInstances().get(karafInstance).getKarafInstanceUrl();
	}
	
	public  Date getAvailablePluginsLastUpdated(){
		return pluginManager.getAvailablePluginsLastUpdated();
	}
	
	public  Date getKarafInstanceLastUpdated(){
		return pluginManager.getKarafInstanceLastUpdated(karafInstance);
	}

	/**
	 * @return the pluginManager
	 */
	public PluginManager getPluginManager() {
		return pluginManager;
	}

	public void setPluginManager(PluginManager pluginManager) {
		this.pluginManager=pluginManager;
		
		if(pluginManager.getKarafInstances().isEmpty()){
			setKarafInstance("");
		} else setKarafInstance(pluginManager.getKarafInstances().firstKey());

	}

	/**
	 * reloads the data from the selected karaf instance
	 */
	public void refreshKarafEntry(){
		pluginManager.refreshKarafEntry(karafInstance);
	}
	
	/**
	 * reloads the data from the selected karaf instance
	 */
	public void refreshAvailablePlugins(){
		pluginManager.refreshAvailablePlugins();
	}

	/**
	 * @return the availablePlugins
	 */
	public ProductSpecList getAvailablePlugins() {
		return pluginManager.getAvailablePlugins();
	}



	/**
	 * @return the installedPlugins
	 */
	public ProductSpecList getInstalledPlugins() {
		return pluginManager.getInstalledPlugins(karafInstance);
	}


	/**
	 * @return the installedLicenceList
	 */
	public LicenceList getInstalledLicenceList() {
		return pluginManager.getInstalledLicenceList(karafInstance);

	}

	/**
	 * @return the systemId
	 */
	public String getSystemId() {
		return pluginManager.getSystemId(karafInstance);
	}

	/**
	 * @param systemId the systemId to set
	 */
	public void setSystemId(String systemId) {
		pluginManager.setSystemId(systemId, karafInstance);
	}

	/**
	 * @return the pluginServerPassword
	 */
	public String getPluginServerPassword() {
		return pluginManager.getPluginServerPassword();
	}

	/**
	 * @return the pluginServerUsername
	 */
	public String getPluginServerUsername() {
		return pluginManager.getPluginServerUsername();
	}


	/**
	 * @return the pluginServerUrl
	 */
	public String getPluginServerUrl() {
		return pluginManager.getPluginServerUrl();

	}

	/**
	 * @return the licenceShoppingCartUrl
	 */
	public String getLicenceShoppingCartUrl(){
		return pluginManager.getLicenceShoppingCartUrl();
	}

	/**
	 * Sets basic data for PluginManager and persists all at once
	 * @param pluginServerUsername
	 * @param pluginServerPassword
	 * @param pluginServerUrl
	 * @param licenceShoppingCartUrl
	 */
	public synchronized void setPluginManagerBasicData(String pluginServerUsername, String pluginServerPassword, String pluginServerUrl, String licenceShoppingCartUrl){
		pluginManager.setPluginManagerBasicData(pluginServerUsername, pluginServerPassword, pluginServerUrl, licenceShoppingCartUrl);
	}


	public String generateRandomManifestSystemId(){
		return pluginManager.generateRandomManifestSystemId(karafInstance);

	}

	public void addLicence(String licenceStr){
		pluginManager.addLicence(licenceStr, karafInstance);
	}

	public void removeLicence(String selectedLicenceId) {
		pluginManager.removeLicence(selectedLicenceId, karafInstance);
	}

	public void installPlugin(String selectedProductId) {
		pluginManager.installPlugin(selectedProductId, karafInstance);

	}

	public void unInstallPlugin(String selectedProductId) {
		pluginManager.unInstallPlugin(selectedProductId, karafInstance);

	}
	
	public ProductSpecList getPluginsManifest() {
		return pluginManager.getPluginsManifest(karafInstance);
	}
	
	public void addPluginToManifest(String selectedProductId) {
		pluginManager.addPluginToManifest(selectedProductId, karafInstance);
		
	}
	
	public void addUserDefinedPluginToManifest(ProductMetadata productMetadata) {
		pluginManager.addUserDefinedPluginToManifest(productMetadata, karafInstance);
		
	}
	
	public void removePluginFromManifest(String selectedProductId) {
		pluginManager.removePluginFromManifest( selectedProductId,karafInstance);
	}
	
	
	public void setManifestSystemId(String manifestSystemId){
		pluginManager.setManifestSystemId(manifestSystemId,karafInstance);
	}
	
	public String getManifestSystemId(){
		return pluginManager.getManifestSystemId(karafInstance);
	}
	
	public Boolean getRemoteIsAccessible(){
		return pluginManager.getRemoteIsAccessible(karafInstance);
	}
	

	public void setRemoteIsAccessible(Boolean remoteIsAccessible){
		pluginManager.setRemoteIsAccessible(remoteIsAccessible, karafInstance);
	}

	
	public Boolean getAllowUpdateMessages(){
		return pluginManager.getAllowUpdateMessages(karafInstance);
	}
	

	public void setAllowUpdateMessages(Boolean allowUpdateMessages){
		pluginManager.setAllowUpdateMessages(allowUpdateMessages, karafInstance);
	}

	
	/**
	 * returns list of karaf instances which can be addressed by ui
	 * @return Map of key = karafInstanceName, value = KarafManifestEntryJaxb
	 */
	public SortedMap<String, KarafManifestEntryJaxb> getKarafInstances(){
		return pluginManager.getKarafInstances();
	}
	
	/**
	 * adds new karaf instance based on karafManifestEntryJaxb
	 * changes default 
	 * throws exception entry already exists or name not set
	 * @param karafManifestEntryJaxb
	 */
	public void addNewKarafInstance(KarafManifestEntryJaxb karafManifestEntryJaxb){
		
		String kInstance=karafManifestEntryJaxb.getKarafInstanceName();
		if(kInstance==null || "".equals(kInstance))  throw new RuntimeException("cannot add new karaf instance - karafInstanceName in karafManifestEntryJaxb cannot be null or empty");
		
		if("localhost".equals(kInstance)) throw new RuntimeException("cannot add localhost karaf instance to plugin manager");

		pluginManager.addNewKarafInstance(karafManifestEntryJaxb);
		
		setKarafInstance(kInstance);
	}
	

	public void deleteKarafInstance(String kInstance){
		if(kInstance==null || "".equals(kInstance))  throw new RuntimeException("cannot add new karaf instance - karafInstanceName in karafManifestEntryJaxb cannot be null or empty");
		if("localhost".equals(kInstance)) throw new RuntimeException("cannot delete localhost karaf instance from plugin manager");
		pluginManager.deleteKarafInstance(karafInstance);
		
		if (kInstance.equals(getKarafInstance())) {
			if(pluginManager.getKarafInstances().isEmpty()){
				setKarafInstance("");
			} else setKarafInstance(pluginManager.getKarafInstances().firstKey());
		}
	}

	public void updateAccessData(String karafInstanceUrl, String karafInstanceUserName, String karafInstancePassword, Boolean remoteIsAccessible, Boolean allowUpdateMessages ){
		pluginManager.updateAccessData(karafInstanceUrl, karafInstanceUserName, karafInstancePassword, remoteIsAccessible, allowUpdateMessages,	karafInstance);
	}

	
	public void setBlueprintContainer(BlueprintContainer blueprintContainer) {
	    this.blueprintContainer=blueprintContainer;
	}

	/**
	 * @return the blueprintContainer
	 */
	public BlueprintContainer getBlueprintContainer() {
		return blueprintContainer;
	}


}
