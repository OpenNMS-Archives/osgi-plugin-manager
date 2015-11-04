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


import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.opennms.features.pluginmgr.model.KarafEntryJaxb;
import org.opennms.features.pluginmgr.model.KarafManifestEntryJaxb;
import org.opennms.features.pluginmgr.model.PluginModelJaxb;
import org.opennms.karaf.featuremgr.rest.client.jerseyimpl.FeaturesServiceClientRestJerseyImpl;
import org.opennms.karaf.licencemgr.StringCrc32Checksum;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceEntry;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceList;
import org.opennms.karaf.licencemgr.metadata.jaxb.ProductMetadata;
import org.opennms.karaf.licencemgr.metadata.jaxb.ProductSpecList;
import org.opennms.karaf.licencemgr.rest.client.jerseyimpl.LicenceManagerClientRestJerseyImpl;
import org.opennms.karaf.licencemgr.rest.client.jerseyimpl.ProductRegisterClientRestJerseyImpl;


public class PluginManagerImpl implements PluginManager {

	private static String PRODUCT_PUB_BASE_PATH = "/licencemgr/rest/product-pub";
	private static String PRODUCT_REG_BASE_PATH = "/licencemgr/rest/product-reg";

	private static String LICENCE_PUB_BASE_PATH = "/licencemgr/rest/licence-pub"; // not used
	private static String LICENCE_MGR_BASE_PATH = "/licencemgr/rest/licence-mgr";

	private static String FEATURE_MGR_BASE_PATH = "/featuremgr";

	private String fileUri="./pluginmodeldata.xml";

	private PluginModelJaxb pluginModelJaxb = new PluginModelJaxb();

	/**
	 * fileUri is the location of the persisted plugin data
	 * @return
	 */
	public String getFileUri() {
		return fileUri;
	}

	/**
	 * fileUri is the location of the persisted plugin data
	 * @param fileUri
	 */
	public void setFileUri(String fileUri) {
		this.fileUri = fileUri;
	}


	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#getPluginServerPassword()
	 */
	@Override
	public synchronized  String getPluginServerPassword() {
		return pluginModelJaxb.getPluginServerPassword();
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#setPluginServerPassword(java.lang.String)
	 */
	@Override
	public synchronized void setPluginServerPassword(String pluginServerPassword){
		pluginModelJaxb.setPluginServerPassword(pluginServerPassword);
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#getPluginServerUsername()
	 */
	@Override
	public synchronized String getPluginServerUsername() {
		return pluginModelJaxb.getPluginServerUsername();
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#setPluginServerUsername(java.lang.String)
	 */
	@Override
	public synchronized void setPluginServerUsername(String pluginServerUsername){
		pluginModelJaxb.setPluginServerUsername(pluginServerUsername);
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#getPluginServerUrl()
	 */
	@Override
	public synchronized String getPluginServerUrl() {
		return pluginModelJaxb.getPluginServerUrl();
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#setPluginServerUrl(java.lang.String)
	 */
	@Override
	public synchronized void setPluginServerUrl(String pluginServerUrl){
		pluginModelJaxb.setPluginServerUrl(pluginServerUrl);
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#getLicenceShoppingCartUrl()
	 */
	@Override
	public synchronized String getLicenceShoppingCartUrl() {
		return pluginModelJaxb.getLicenceShoppingCartUrl();
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#setLicenceShoppingCartUrl(java.lang.String)
	 */
	@Override
	public synchronized void setLicenceShoppingCartUrl(String licenceShoppingCartUrl){
		pluginModelJaxb.setLicenceShoppingCartUrl(licenceShoppingCartUrl);
	}


	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#setPluginManagerBasicData(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized void setPluginManagerBasicData(String pluginServerUsername, String pluginServerPassword, String pluginServerUrl, String licenceShoppingCartUrl){
		pluginModelJaxb.setPluginServerUsername(pluginServerUsername);
		pluginModelJaxb.setPluginServerPassword(pluginServerPassword);
		pluginModelJaxb.setPluginServerUrl(pluginServerUrl);
		pluginModelJaxb.setLicenceShoppingCartUrl(licenceShoppingCartUrl);
		persist();
	}


	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#getKarafInstances()
	 */
	@Override
	public synchronized SortedMap<String, KarafManifestEntryJaxb> getKarafInstances(){

		SortedMap<String, KarafManifestEntryJaxb> karafInstances = pluginModelJaxb.getKarafManifestEntryMap();

		// creates a localhost entry if doesn't exist in karafInstances
		// TODO SET DEFAULTS IN BLUEPRINT
		if (! karafInstances.containsKey("localhost")){
			KarafManifestEntryJaxb localhostManifest= new KarafManifestEntryJaxb();
			localhostManifest.setKarafInstanceName("localhost");
			localhostManifest.setKarafInstanceUserName("admin");
			localhostManifest.setKarafInstancePassword("admin");
			localhostManifest.setKarafInstanceUrl("http://localhost:8980/opennms");
			localhostManifest.setRemoteIsAccessible(true);
			localhostManifest.setAllowUpdateMessages(false);

			karafInstances.put("localhost",localhostManifest);
			persist();
		}

		return karafInstances;

	}

	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#refreshAvailablePlugins()
	 */
	@Override
	public synchronized void refreshAvailablePlugins() {

		ProductRegisterClientRestJerseyImpl productRegisterClient = new ProductRegisterClientRestJerseyImpl();
		productRegisterClient.setBaseUrl(this.getPluginServerUrl());
		productRegisterClient.setUserName(this.getPluginServerUsername());
		productRegisterClient.setPassword(this.getPluginServerPassword());

		productRegisterClient.setBasePath(PRODUCT_PUB_BASE_PATH);

		ProductSpecList availablePlugins;
		try {
			availablePlugins = productRegisterClient.getList();
			pluginModelJaxb.setAvailablePlugins(availablePlugins);
			pluginModelJaxb.setAvailablePluginsLastUpdated(new Date());
			persist();
		} catch (Exception e) {
			throw new RuntimeException("problem refreshing available plugins for"
					+ " plugin server Url="+this.getPluginServerUrl()
					+ ": ", e);
		}

	}



	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#getAvailablePlugins()
	 */
	@Override
	public synchronized ProductSpecList getAvailablePlugins() {

		if (pluginModelJaxb.getAvailablePlugins()==null 
				|| pluginModelJaxb.getAvailablePlugins().getProductSpecList()==null 
				|| pluginModelJaxb.getAvailablePlugins().getProductSpecList().size()==0) refreshAvailablePlugins();

		return pluginModelJaxb.getAvailablePlugins();
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#refreshKarafEntry(java.lang.String)
	 */
	@Override
	public synchronized  KarafEntryJaxb refreshKarafEntry(String karafInstance){
		if(karafInstance==null) throw new RuntimeException("karafInstance cannot be null");

		SortedMap<String, KarafManifestEntryJaxb> karafInstances = getKarafInstances();
		if (! karafInstances.containsKey(karafInstance)) throw new RuntimeException("system does not know karafInstance="+karafInstance);

		KarafManifestEntryJaxb karafManifest = karafInstances.get(karafInstance);
		String karafInstanceUrl=karafManifest.getKarafInstanceUrl();

		// only update remote if accessible
		// else just return current value
		if (karafManifest.getRemoteIsAccessible()==null || ! karafManifest.getRemoteIsAccessible()){
			if (! pluginModelJaxb.getKarafDataMap().containsKey(karafInstance)){
				// if there is no entry, create an empty one
				KarafEntryJaxb karafEntryJaxb= new KarafEntryJaxb();
				pluginModelJaxb.getKarafDataMap().put(karafInstance, karafEntryJaxb);
			}
			return pluginModelJaxb.getKarafDataMap().get(karafInstance);

		} else {

			KarafEntryJaxb karafEntryJaxb= new KarafEntryJaxb();

			try{

				// getting karaf installed licences and system id
				LicenceManagerClientRestJerseyImpl licenceManagerClient = new LicenceManagerClientRestJerseyImpl();
				licenceManagerClient.setBaseUrl(karafInstanceUrl);
				licenceManagerClient.setUserName(karafManifest.getKarafInstanceUserName());
				licenceManagerClient.setPassword(karafManifest.getKarafInstancePassword());

				licenceManagerClient.setBasePath(LICENCE_MGR_BASE_PATH);
				LicenceList installedLicenceList;
				try {
					installedLicenceList = licenceManagerClient.getLicenceMap();
					karafEntryJaxb.setInstalledLicenceList(installedLicenceList);
					String systemId = licenceManagerClient.getSystemId();
					karafEntryJaxb.setSystemId(systemId);
					// if the remote has a system id and manifest does not, set manifest to remote value
					if(karafManifest.getManifestSystemId()==null) karafManifest.setManifestSystemId(systemId);
				} catch (Exception e) {
					throw new RuntimeException("problem refreshing installed licences for "
							+ "karafInstance="+karafInstance
							+ " karafInstanceUrl="+karafInstanceUrl
							+ ": ", e);
				}

				// getting installed plugins
				ProductRegisterClientRestJerseyImpl productRegisterClient = new ProductRegisterClientRestJerseyImpl();
				productRegisterClient.setBaseUrl(karafInstanceUrl);
				productRegisterClient.setUserName(karafManifest.getKarafInstanceUserName());
				productRegisterClient.setPassword(karafManifest.getKarafInstancePassword());

				// getting karaf installed plugins
				productRegisterClient.setBasePath(PRODUCT_REG_BASE_PATH);

				ProductSpecList installedPlugins;
				try {
					installedPlugins = productRegisterClient.getList();

					List<LicenceEntry> licenceList = karafEntryJaxb.getInstalledLicenceList().getLicenceList();

					// tests if plugins need a licence and if the licence is authenticated
					for (ProductMetadata installedPlugin: installedPlugins.getProductSpecList()){
						if (installedPlugin.getLicenceKeyRequired()!=null && installedPlugin.getLicenceKeyRequired() == true){
							// if plugin needs a licence then check if licence is already verified
							// ignores exception if no licence is installed
							Boolean licenceKeyAuthenticated = false;
							for (LicenceEntry licenceEntry :licenceList){
								if (licenceEntry.getProductId().equals(installedPlugin.getProductId())){
									// only check where licence is installed
									// note will throw exception if licence is not installed when checked
									licenceKeyAuthenticated = licenceManagerClient.isAuthenticated(installedPlugin.getProductId());
								}
							}
							installedPlugin.setLicenceKeyAuthenticated(licenceKeyAuthenticated);
						}
					}

					karafEntryJaxb.setInstalledPlugins(installedPlugins);
				} catch (Exception e) {
					throw new RuntimeException("problem refreshing installed plugins for "
							+ "karafInstance="+karafInstance
							+ " karafInstanceUrl="+karafInstanceUrl
							+ ": ", e);
				}

				karafEntryJaxb.setKarafInstanceLastUpdated(new Date());

				pluginModelJaxb.getKarafDataMap().put(karafInstance, karafEntryJaxb);

				persist();

			} catch (Exception e){
				throw new RuntimeException("problem updating data from karaf Instance '"+karafInstance+"'" , e);
			}

			return karafEntryJaxb;

		} 
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#getKarafInstanceLastUpdated(java.lang.String)
	 */
	@Override
	public synchronized Date getKarafInstanceLastUpdated(String karafInstance){
		if(karafInstance==null) throw new RuntimeException("karafInstance cannot be null");
		if (! pluginModelJaxb.getKarafDataMap().containsKey(karafInstance)){
			return null;
		} 
		KarafEntryJaxb karafEntry = pluginModelJaxb.getKarafDataMap().get(karafInstance);
		if (karafEntry==null) return null;
		return karafEntry.getKarafInstanceLastUpdated();
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#getAvailablePluginsLastUpdated()
	 */
	@Override
	public synchronized Date getAvailablePluginsLastUpdated() {
		return pluginModelJaxb.getAvailablePluginsLastUpdated();
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#getInstalledPlugins(java.lang.String)
	 */
	@Override
	public synchronized ProductSpecList getInstalledPlugins(String karafInstance) {
		if(karafInstance==null) throw new RuntimeException("karafInstance cannot be null");
		if (! pluginModelJaxb.getKarafDataMap().containsKey(karafInstance)){
			return null;
		} 
		KarafEntryJaxb karafEntry = pluginModelJaxb.getKarafDataMap().get(karafInstance);
		if (karafEntry==null ) return null;
		return karafEntry.getInstalledPlugins();
	}


	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#getInstalledLicenceList(java.lang.String)
	 */
	@Override
	public synchronized LicenceList getInstalledLicenceList(String karafInstance) {
		if(karafInstance==null) throw new RuntimeException("karafInstance cannot be null");
		if (! pluginModelJaxb.getKarafDataMap().containsKey(karafInstance)){
			return null;
		} 
		KarafEntryJaxb karafEntry = pluginModelJaxb.getKarafDataMap().get(karafInstance);
		if (karafEntry==null ) return null;
		return karafEntry.getInstalledLicenceList();
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#getInstalledLicenceList(java.lang.String)
	 */
	@Override
	public synchronized void updateInstalledLicenceList(LicenceList licenceList, String karafInstance) {
		if(karafInstance==null) throw new RuntimeException("karafInstance cannot be null");
		if(licenceList==null) throw new RuntimeException("licenceList cannot be null");

		SortedMap<String, KarafManifestEntryJaxb> karafInstances = getKarafInstances();
		if (! karafInstances.containsKey(karafInstance)) throw new RuntimeException("system does not know karafInstance="+karafInstance);

		if (! pluginModelJaxb.getKarafDataMap().containsKey(karafInstance)){
			throw new RuntimeException("no karaf entry entry exists for Karaf Instance="+karafInstance);
		} 
		KarafEntryJaxb karafEntry = pluginModelJaxb.getKarafDataMap().get(karafInstance);
		karafEntry.getInstalledLicenceList().getLicenceList().clear();
		karafEntry.getInstalledLicenceList().getLicenceList().addAll(licenceList.getLicenceList());
		persist();
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#getInstalledLicenceList(java.lang.String)
	 */
	@Override
	public synchronized void updateInstalledPlugins(ProductSpecList installedPlugins, String karafInstance) {
		if(karafInstance==null) throw new RuntimeException("karafInstance cannot be null");
		if(installedPlugins==null) throw new RuntimeException("installedPlugins cannot be null");

		SortedMap<String, KarafManifestEntryJaxb> karafInstances = getKarafInstances();
		if (! karafInstances.containsKey(karafInstance)) throw new RuntimeException("system does not know karafInstance="+karafInstance);

		if (! pluginModelJaxb.getKarafDataMap().containsKey(karafInstance)){
			throw new RuntimeException("no karaf entry entry exists for Karaf Instance="+karafInstance);
		} 
		KarafEntryJaxb karafEntry = pluginModelJaxb.getKarafDataMap().get(karafInstance);
		karafEntry.getInstalledPlugins().getProductSpecList().clear();
		karafEntry.getInstalledPlugins().getProductSpecList().addAll(installedPlugins.getProductSpecList());
		persist();
	}


	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#getSystemId(java.lang.String)
	 */
	@Override
	public synchronized String getSystemId(String karafInstance) {
		if(karafInstance==null) throw new RuntimeException("karafInstance cannot be null");
		if (! pluginModelJaxb.getKarafDataMap().containsKey(karafInstance)){
			return null;
		} 
		KarafEntryJaxb karafEntry = pluginModelJaxb.getKarafDataMap().get(karafInstance);
		if (karafEntry==null ) return null;
		return karafEntry.getSystemId();
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#setSystemId(java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized void setSystemId(String systemId, String karafInstance) {
		if(karafInstance==null) throw new RuntimeException("karafInstance cannot be null");
		if(systemId==null) throw new RuntimeException("systemId cannot be null");

		SortedMap<String, KarafManifestEntryJaxb> karafInstances = getKarafInstances();
		if (! karafInstances.containsKey(karafInstance)) throw new RuntimeException("system does not know karafInstance="+karafInstance);
		KarafManifestEntryJaxb karafManifest = karafInstances.get(karafInstance);
		String karafInstanceUrl=karafManifest.getKarafInstanceUrl();

		// only update remote if accessible
		if (karafManifest.getRemoteIsAccessible()==null || ! karafManifest.getRemoteIsAccessible()){
			throw new RuntimeException("karafInstance="+karafInstance+" is not accessable remotely");
		}

		// setting systemId for karaf instance
		LicenceManagerClientRestJerseyImpl licenceManagerClient = new LicenceManagerClientRestJerseyImpl();
		licenceManagerClient.setBaseUrl(karafInstanceUrl);
		licenceManagerClient.setUserName(karafManifest.getKarafInstanceUserName());
		licenceManagerClient.setPassword(karafManifest.getKarafInstancePassword());
		licenceManagerClient.setBasePath(LICENCE_MGR_BASE_PATH);
		try {
			licenceManagerClient.setSystemId(systemId);
			refreshKarafEntry(karafInstance);
		} catch (Exception e) {
			throw new RuntimeException("problem updating systemId for "
					+ "karafInstance="+karafInstance
					+ " karafInstanceUrl="+karafInstanceUrl
					+ ": ", e);
		}

	}

	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#generateRandomManifestSystemId(java.lang.String)
	 */
	@Override
	public synchronized String generateRandomManifestSystemId(String karafInstance){
		if(karafInstance==null) throw new RuntimeException("karafInstance cannot be null");

		SortedMap<String, KarafManifestEntryJaxb> karafInstances = getKarafInstances();
		if (! karafInstances.containsKey(karafInstance)) throw new RuntimeException("system does not know karafInstance="+karafInstance);

		// create random object
		Random randomgen = new Random();

		// get next long value 
		long systemIdValue = randomgen.nextLong();

		// make hex string
		String hexSystemIdString=Long.toHexString(systemIdValue);
		StringCrc32Checksum stringCrc32Checksum = new StringCrc32Checksum();
		String manifestSystemId = stringCrc32Checksum.addCRC(hexSystemIdString);

		this.setManifestSystemId(manifestSystemId, karafInstance);

		return manifestSystemId;

	}

	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#addLicence(java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized void addLicence(String licenceStr,String karafInstance){
		if(karafInstance==null) throw new RuntimeException("karafInstance cannot be null");
		if(licenceStr==null) throw new RuntimeException("licenceStr cannot be null");

		SortedMap<String, KarafManifestEntryJaxb> karafInstances = getKarafInstances();
		if (! karafInstances.containsKey(karafInstance)) throw new RuntimeException("system does not know karafInstance="+karafInstance);
		KarafManifestEntryJaxb karafManifest = karafInstances.get(karafInstance);
		String karafInstanceUrl=karafManifest.getKarafInstanceUrl();

		// only update remote if accessible
		if (karafManifest.getRemoteIsAccessible()==null || ! karafManifest.getRemoteIsAccessible()){
			throw new RuntimeException("karafInstance="+karafInstance+" is not accessable remotely");
		}

		// setting systemId for karaf instance
		LicenceManagerClientRestJerseyImpl licenceManagerClient = new LicenceManagerClientRestJerseyImpl();
		licenceManagerClient.setBaseUrl(karafInstanceUrl);
		licenceManagerClient.setUserName(karafManifest.getKarafInstanceUserName());
		licenceManagerClient.setPassword(karafManifest.getKarafInstancePassword());
		licenceManagerClient.setBasePath(LICENCE_MGR_BASE_PATH);

		try {
			licenceManagerClient.addLicence(licenceStr);
			refreshKarafEntry(karafInstance);
		} catch (Exception e) {
			throw new RuntimeException("problem updating licence String for "
					+ "karafInstance="+karafInstance
					+ " karafInstanceUrl="+karafInstanceUrl
					+ ": ", e);
		}

	}

	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#removeLicence(java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized void removeLicence(String selectedLicenceId, String karafInstance) {
		if(karafInstance==null) throw new RuntimeException("karafInstance cannot be null");
		if(selectedLicenceId==null) throw new RuntimeException("selectedLicenceId cannot be null");

		SortedMap<String, KarafManifestEntryJaxb> karafInstances = getKarafInstances();
		if (! karafInstances.containsKey(karafInstance)) throw new RuntimeException("system does not know karafInstance="+karafInstance);
		KarafManifestEntryJaxb karafManifest = karafInstances.get(karafInstance);
		String karafInstanceUrl=karafManifest.getKarafInstanceUrl();

		// only update remote if accessible
		if (karafManifest.getRemoteIsAccessible()==null || ! karafManifest.getRemoteIsAccessible()){
			throw new RuntimeException("karafInstance="+karafInstance+" is not accessable remotely");
		}

		// setting systemId for karaf instance
		LicenceManagerClientRestJerseyImpl licenceManagerClient = new LicenceManagerClientRestJerseyImpl();
		licenceManagerClient.setBaseUrl(karafInstanceUrl);
		licenceManagerClient.setUserName(karafManifest.getKarafInstanceUserName());
		licenceManagerClient.setPassword(karafManifest.getKarafInstancePassword());
		licenceManagerClient.setBasePath(LICENCE_MGR_BASE_PATH);

		try {
			licenceManagerClient.removeLicence(selectedLicenceId);
			refreshKarafEntry(karafInstance);
		} catch (Exception e) {
			throw new RuntimeException("problem updating licence String for "
					+ "karafInstance="+karafInstance
					+ " karafInstanceUrl="+karafInstanceUrl
					+ ": ", e);
		}

	}

	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#installPlugin(java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized void installPlugin(String selectedProductId,String karafInstance) {
		if(karafInstance==null) throw new RuntimeException("karafInstance cannot be null");
		if(selectedProductId==null) throw new RuntimeException("selectedProductId cannot be null");

		SortedMap<String, KarafManifestEntryJaxb> karafInstances = getKarafInstances();
		if (! karafInstances.containsKey(karafInstance)) throw new RuntimeException("system does not know karafInstance="+karafInstance);
		KarafManifestEntryJaxb karafManifest = karafInstances.get(karafInstance);
		String karafInstanceUrl=karafManifest.getKarafInstanceUrl();

		// only update remote if accessible
		if (karafManifest.getRemoteIsAccessible()==null || ! karafManifest.getRemoteIsAccessible()){
			throw new RuntimeException("karafInstance="+karafInstance+" is not accessable remotely");
		}

		ProductMetadata productMetadata=null;

		// try manifest plugins list first
		if (pluginModelJaxb.getKarafManifestEntryMap().get(karafInstance)!=null){
			ProductSpecList karafInstancePluginManifest = pluginModelJaxb.getKarafManifestEntryMap().get(karafInstance).getPluginManifest();
			if (karafInstancePluginManifest!=null){
				for (ProductMetadata pMetadata : karafInstancePluginManifest.getProductSpecList()){
					if (selectedProductId.equals(pMetadata.getProductId())) {
						productMetadata=pMetadata;
						break;
					}
				}
			}
		}

		// then try available plugins
		if (productMetadata==null) {
			for (ProductMetadata pMetadata : pluginModelJaxb.getAvailablePlugins().getProductSpecList()){
				if (selectedProductId.equals(pMetadata.getProductId())) {
					productMetadata=pMetadata;
					break;
				}
			}
		}

		if(productMetadata==null) throw new RuntimeException("cannot install unknown available productId="+selectedProductId);
		if(productMetadata.getFeatureRepository()==null) throw new RuntimeException("feature repository cannot be null for productId="+selectedProductId);

		FeaturesServiceClientRestJerseyImpl featuresServiceClient = new FeaturesServiceClientRestJerseyImpl(); 
		featuresServiceClient.setBaseUrl(karafInstanceUrl);
		featuresServiceClient.setUserName(karafManifest.getKarafInstanceUserName());
		featuresServiceClient.setPassword(karafManifest.getKarafInstancePassword());
		featuresServiceClient.setBasePath(FEATURE_MGR_BASE_PATH);

		try {
			// add feature repository
			featuresServiceClient.featuresAddRepository(productMetadata.getFeatureRepository());
			// a feature id looks like name/version.
			String version=null;
			String name=null;
			if(selectedProductId.contains("/")) {
				int i = selectedProductId.indexOf('/');
				version = selectedProductId.substring(i+1);
				name =  selectedProductId.substring(0, selectedProductId.indexOf('/'));
			}
			//add feature
			featuresServiceClient.featuresInstall(name, version);
			refreshKarafEntry(karafInstance);
		} catch (Exception e) {
			throw new RuntimeException("problem installing product "+selectedProductId
					+ " for Karaf Instance="+karafInstance
					+ " karafInstanceUrl="+karafInstanceUrl
					+ ": ", e);
		}

	}

	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#unInstallPlugin(java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized void unInstallPlugin(String selectedProductId,String karafInstance) {
		if(karafInstance==null) throw new RuntimeException("karafInstance cannot be null");
		if(selectedProductId==null) throw new RuntimeException("selectedProductId cannot be null");

		SortedMap<String, KarafManifestEntryJaxb> karafInstances = getKarafInstances();
		if (! karafInstances.containsKey(karafInstance)) throw new RuntimeException("system does not know karafInstance="+karafInstance);
		KarafManifestEntryJaxb karafManifest = karafInstances.get(karafInstance);
		String karafInstanceUrl=karafManifest.getKarafInstanceUrl();

		// only update remote if accessible
		if (karafManifest.getRemoteIsAccessible()==null || ! karafManifest.getRemoteIsAccessible()){
			throw new RuntimeException("karafInstance="+karafInstance+" is not accessable remotely");
		}

		FeaturesServiceClientRestJerseyImpl featuresServiceClient = new FeaturesServiceClientRestJerseyImpl(); 
		featuresServiceClient.setBaseUrl(karafInstanceUrl);
		featuresServiceClient.setUserName(karafManifest.getKarafInstanceUserName());
		featuresServiceClient.setPassword(karafManifest.getKarafInstancePassword());
		featuresServiceClient.setBasePath(FEATURE_MGR_BASE_PATH);

		try {
			String version=null;
			String name=null;
			if(selectedProductId.contains("/")) {
				int i = selectedProductId.indexOf('/');
				version = selectedProductId.substring(i+1);
				name =  selectedProductId.substring(0, selectedProductId.indexOf('/'));
			}
			//remove feature
			featuresServiceClient.featuresUninstall(name, version);
			refreshKarafEntry(karafInstance);
		} catch (Exception e) {
			throw new RuntimeException("problem un-installing product "+selectedProductId
					+ " for Karaf Instance="+karafInstance
					+ " karafInstanceUrl="+karafInstanceUrl
					+ ": ", e);
		}

	}


	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#getPluginsManifest(java.lang.String)
	 */
	@Override
	public synchronized ProductSpecList getPluginsManifest(String karafInstance) {
		if(karafInstance==null) throw new RuntimeException("karafInstance cannot be null");
		if (! pluginModelJaxb.getKarafManifestEntryMap().containsKey(karafInstance)){
			return new ProductSpecList(); // return empty list if no entry found
		} 
		ProductSpecList pluginManifest = pluginModelJaxb.getKarafManifestEntryMap().get(karafInstance).getPluginManifest();
		if (pluginManifest==null) return new ProductSpecList(); // return empty list if no entry found
		return pluginManifest;
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#addPluginToManifest(java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized void addPluginToManifest(String selectedProductId,String karafInstance) {
		if(karafInstance==null) throw new RuntimeException("karafInstance cannot be null");
		if(selectedProductId==null) throw new RuntimeException("selectedProductId cannot be null");

		SortedMap<String, KarafManifestEntryJaxb> karafInstances = getKarafInstances();
		if (! karafInstances.containsKey(karafInstance)) throw new RuntimeException("system does not know karafInstance="+karafInstance);

		ProductMetadata productMetadata=null;
		for (ProductMetadata pMetadata : pluginModelJaxb.getAvailablePlugins().getProductSpecList()){
			if (selectedProductId.equals(pMetadata.getProductId())) {
				productMetadata=pMetadata;
				break;
			}
		}
		if(productMetadata==null) throw new RuntimeException("cannot install unknown available productId="+selectedProductId);
		if(productMetadata.getFeatureRepository()==null) throw new RuntimeException("feature repository cannot be null for productId="+selectedProductId);

		if (! pluginModelJaxb.getKarafManifestEntryMap().containsKey(karafInstance)) {
			pluginModelJaxb.getKarafManifestEntryMap().put(karafInstance, new KarafManifestEntryJaxb());
		}

		ProductSpecList karafInstancePluginManifest = pluginModelJaxb.getKarafManifestEntryMap().get(karafInstance).getPluginManifest();

		// remove any product metadata with duplicate productId's from the list
		String productMetadataId=productMetadata.getProductId();
		ArrayList<ProductMetadata> searchlist = new ArrayList<ProductMetadata>(karafInstancePluginManifest.getProductSpecList());
		for (ProductMetadata searchProductMetadata : searchlist){
			if (productMetadataId.equals(searchProductMetadata.getProductId())) {
				karafInstancePluginManifest.getProductSpecList().remove(searchProductMetadata);
			}
		}

		karafInstancePluginManifest.getProductSpecList().add(productMetadata);

		persist();
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#addUserDefinedPluginToManifest(org.opennms.karaf.licencemgr.metadata.jaxb.ProductMetadata, java.lang.String)
	 */
	@Override
	public synchronized void addUserDefinedPluginToManifest(ProductMetadata newProductMetadata, String karafInstance) {
		if(karafInstance==null) throw new RuntimeException("karafInstance cannot be null");
		if(newProductMetadata==null) throw new RuntimeException("newProductMetadata cannot be null");
		if(newProductMetadata.getProductId()==null || "".equals(newProductMetadata.getProductId())) 
			throw new RuntimeException("newProductMetadata productId cannot be null or empty string");
		if(newProductMetadata.getFeatureRepository()==null || "".equals(newProductMetadata.getFeatureRepository())) 
			throw new RuntimeException("newProductMetadata featureRepository cannot be null or empty string");

		SortedMap<String, KarafManifestEntryJaxb> karafInstances = getKarafInstances();
		if (! karafInstances.containsKey(karafInstance)) throw new RuntimeException("system does not know karafInstance="+karafInstance);

		if (! pluginModelJaxb.getKarafManifestEntryMap().containsKey(karafInstance)) {
			pluginModelJaxb.getKarafManifestEntryMap().put(karafInstance, new KarafManifestEntryJaxb());
		}

		ProductSpecList karafInstancePluginManifest = pluginModelJaxb.getKarafManifestEntryMap().get(karafInstance).getPluginManifest();

		// remove any product metadata with duplicate productId's from the list
		String newProductMetadataId=newProductMetadata.getProductId();
		ArrayList<ProductMetadata> searchlist = new ArrayList<ProductMetadata>(karafInstancePluginManifest.getProductSpecList());
		for (ProductMetadata searchProductMetadata : searchlist){
			if (newProductMetadataId.equals(searchProductMetadata.getProductId())) {
				karafInstancePluginManifest.getProductSpecList().remove(searchProductMetadata);
			}
		}

		karafInstancePluginManifest.getProductSpecList().add(newProductMetadata);
		persist();

	}

	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#removePluginFromManifest(java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized void removePluginFromManifest(String selectedProductId,String karafInstance) {
		if(karafInstance==null) throw new RuntimeException("karafInstance cannot be null");
		if(selectedProductId==null) throw new RuntimeException("selectedProductId cannot be null");

		if (pluginModelJaxb.getKarafManifestEntryMap().containsKey(karafInstance)) {
			List<ProductMetadata> productSpecList = pluginModelJaxb.getKarafManifestEntryMap().get(karafInstance).getPluginManifest().getProductSpecList();
			ProductMetadata productMetadata=null;
			for (ProductMetadata pMetadata : productSpecList){
				if (selectedProductId.equals(pMetadata.getProductId())) {
					productMetadata=pMetadata;
					break;
				}
			}
			if (productMetadata!=null) productSpecList.remove(productMetadata);
			persist();
		}
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#setManifestSystemId(java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized void setManifestSystemId(String manifestSystemId,String karafInstance){
		if(karafInstance==null) throw new RuntimeException("karafInstance cannot be null");
		if(manifestSystemId==null) throw new RuntimeException("manifestSystemId cannot be null");

		SortedMap<String, KarafManifestEntryJaxb> karafInstances = getKarafInstances();
		if (! karafInstances.containsKey(karafInstance)) throw new RuntimeException("system does not know karafInstance="+karafInstance);

		if (! pluginModelJaxb.getKarafManifestEntryMap().containsKey(karafInstance)){
			pluginModelJaxb.getKarafManifestEntryMap().put(karafInstance, new KarafManifestEntryJaxb());
		}

		KarafManifestEntryJaxb karafManifestEntryJaxb = pluginModelJaxb.getKarafManifestEntryMap().get(karafInstance);

		karafManifestEntryJaxb.setManifestSystemId(manifestSystemId);

		persist();

	}

	/* (non-Javadoc)
	 * @see org.opennms.features.pluginmgr.PluginManager#getManifestSystemId(java.lang.String)
	 */
	@Override
	public synchronized String getManifestSystemId(String karafInstance){
		if(karafInstance==null) throw new RuntimeException("karafInstance cannot be null");

		KarafManifestEntryJaxb karafManifestEntry = pluginModelJaxb.getKarafManifestEntryMap().get(karafInstance);

		if (karafManifestEntry==null) return null;

		return karafManifestEntry.getManifestSystemId();

	}

	@Override
	public synchronized Boolean getRemoteIsAccessible(String karafInstance) {
		if(karafInstance==null) throw new RuntimeException("karafInstance cannot be null");

		KarafManifestEntryJaxb karafManifestEntry = pluginModelJaxb.getKarafManifestEntryMap().get(karafInstance);

		if (karafManifestEntry==null) return true;

		return karafManifestEntry.getRemoteIsAccessible();
	}

	@Override
	public synchronized void setRemoteIsAccessible(Boolean remoteIsAccessible, String karafInstance) {
		if(karafInstance==null) throw new RuntimeException("karafInstance cannot be null");

		SortedMap<String, KarafManifestEntryJaxb> karafInstances = getKarafInstances();
		if (! karafInstances.containsKey(karafInstance)) throw new RuntimeException("system does not know karafInstance="+karafInstance);

		if (! pluginModelJaxb.getKarafManifestEntryMap().containsKey(karafInstance)){
			pluginModelJaxb.getKarafManifestEntryMap().put(karafInstance, new KarafManifestEntryJaxb());
		}
		KarafManifestEntryJaxb karafManifestEntryJaxb = pluginModelJaxb.getKarafManifestEntryMap().get(karafInstance);

		karafManifestEntryJaxb.setRemoteIsAccessible(remoteIsAccessible);

		persist();
	}

	@Override
	public synchronized Boolean getAllowUpdateMessages(String karafInstance) {
		if(karafInstance==null) throw new RuntimeException("karafInstance cannot be null");

		KarafManifestEntryJaxb karafManifestEntry = pluginModelJaxb.getKarafManifestEntryMap().get(karafInstance);

		if (karafManifestEntry==null) return true;

		return karafManifestEntry.getAllowUpdateMessages();
	}

	@Override
	public synchronized void setAllowUpdateMessages(Boolean allowUpdateMessages, 	String karafInstance) {
		if(karafInstance==null) throw new RuntimeException("karafInstance cannot be null");

		SortedMap<String, KarafManifestEntryJaxb> karafInstances = getKarafInstances();
		if (! karafInstances.containsKey(karafInstance)) throw new RuntimeException("system does not know karafInstance="+karafInstance);

		if (! pluginModelJaxb.getKarafManifestEntryMap().containsKey(karafInstance)){
			pluginModelJaxb.getKarafManifestEntryMap().put(karafInstance, new KarafManifestEntryJaxb());
		}

		KarafManifestEntryJaxb karafManifestEntryJaxb = pluginModelJaxb.getKarafManifestEntryMap().get(karafInstance);

		karafManifestEntryJaxb.setAllowUpdateMessages(allowUpdateMessages);

		persist();
	}

	@Override
	public synchronized void addNewKarafInstance( KarafManifestEntryJaxb karafManifestEntryJaxb) {
		if(karafManifestEntryJaxb==null) throw new RuntimeException("cannot add new karaf instance -  karafManifestEntryJaxb cannot be null");

		String karafInstance=karafManifestEntryJaxb.getKarafInstanceName();
		if(karafInstance==null || "".equals(karafInstance))  throw new RuntimeException("cannot add new karaf instance - karafInstanceName in karafManifestEntryJaxb cannot be null or empty");

		if("localhost".equals(karafInstance)) throw new RuntimeException("cannot add localhost karaf instance to plugin manager");

		if (pluginModelJaxb.getKarafManifestEntryMap().containsKey(karafInstance)){
			throw new RuntimeException("cannot add new karaf instance - '"+karafInstance
					+ "' is already defined in plugin manager");
		}

		pluginModelJaxb.getKarafManifestEntryMap().put(karafInstance, karafManifestEntryJaxb);
		persist();

	}

	@Override
	public synchronized void deleteKarafInstance(String karafInstance) {
		if(karafInstance==null  || "".equals(karafInstance)) throw new RuntimeException("karafInstance cannot be null or empty");

		if("localhost".equals(karafInstance)) throw new RuntimeException("cannot delete localhost karaf instance from plugin manager");

		pluginModelJaxb.getKarafManifestEntryMap().remove(karafInstance);
		pluginModelJaxb.getKarafDataMap().remove(karafInstance);

		persist();
	}

	@Override
	public synchronized void updateAccessData(String karafInstanceUrl, String karafInstanceUserName, String karafInstancePassword, Boolean remoteIsAccessible, Boolean allowUpdateMessages, String karafInstance){

		if(karafInstance==null) throw new RuntimeException("karafInstance cannot be null");
		if(karafInstanceUserName==null) throw new RuntimeException("username cannot be null");
		if(karafInstancePassword==null) throw new RuntimeException("password cannot be null");
		if(remoteIsAccessible==null) throw new RuntimeException("remoteIsAccessible cannot be null");
		if(allowUpdateMessages==null) throw new RuntimeException("allowUpdateMessages cannot be null");

		SortedMap<String, KarafManifestEntryJaxb> karafInstances = getKarafInstances();
		if (! karafInstances.containsKey(karafInstance)) throw new RuntimeException("system does not know karafInstance="+karafInstance);

		KarafManifestEntryJaxb ki = karafInstances.get(karafInstance);
		ki.setKarafInstancePassword(karafInstancePassword);
		ki.setKarafInstanceUserName(karafInstanceUserName);
		ki.setKarafInstanceUrl(karafInstanceUrl);
		ki.setRemoteIsAccessible(remoteIsAccessible);
		ki.setAllowUpdateMessages(allowUpdateMessages);	

		persist();

	}

	/**
	 * persists the plugin data to the file indicated by fileUri
	 */
	public synchronized void persist(){
		if (fileUri==null) throw new RuntimeException("persist failed - fileUri must be set for plugin manager");

		try {
			//TODO CREATE ROLLING FILE TO AVOID CORRUPTED FILE
			File pluginModelFile = new File(fileUri);
			JAXBContext jaxbContext = JAXBContext.newInstance(PluginModelJaxb.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(pluginModelJaxb, pluginModelFile);

		} catch (JAXBException e) {
			throw new RuntimeException("Problem persisting Plugin Manager Data",e);
		}
	}

	/**
	 * blueprint init-method
	 * loads the persisted plugin data to the file indicated by fileUri
	 */
	public synchronized void load(){
		if (fileUri==null) throw new RuntimeException("load failed - fileUri must be set for plugin manager");
		System.out.println("Plugin Manager Starting");

		//TODO CREATE ROLLING FILE TO AVOID CORRUPTED FILE
		try {

			File pluginManagerFile = new File(fileUri);

			if (pluginManagerFile.exists()) {
				JAXBContext jaxbContext = JAXBContext.newInstance(PluginModelJaxb.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

				PluginModelJaxb loadedPluginModelJaxb = (PluginModelJaxb) jaxbUnmarshaller.unmarshal(pluginManagerFile);

				pluginModelJaxb = loadedPluginModelJaxb;

				System.out.println("Plugin Manager Successfully loaded historic data from file="+pluginManagerFile.getAbsolutePath());
			} else {
				System.out.println("Plugin Manager data file="+pluginManagerFile.getAbsolutePath()+" does not exist. A new one will be created.");
				persist(); // persists first version of plugin model
			}
			System.out.println("Plugin Manager Started");
		} catch (JAXBException e) {
			System.out.println("Plugin Manager Problem Starting: "+ SimpleStackTrace.errorToString(e));
			throw new RuntimeException("Problem loading Plugin Manager Data",e);
		}
	}

	/**
	 * blueprint destroy-method
	 */
	public synchronized void close() {
		System.out.println("Plugin Manager Shutting Down ");
	}



}
