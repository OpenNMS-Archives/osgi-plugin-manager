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

package org.opennms.karaf.featuremgr;

import java.io.File;
import java.net.URI;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.karaf.features.FeaturesService;
import org.opennms.karaf.featuremgr.jaxb.karaf.feature.Features;
import org.opennms.karaf.featuremgr.manifest.client.jerseyimpl.ManifestServiceClientRestJerseyImpl;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PluginFeatureManagerImpl implements PluginFeatureManagerService {
	private static final Logger LOG = LoggerFactory.getLogger(PluginFeatureManagerImpl.class);

	// name of config  file <persistentId>.cfg
	private static final String PERSISTANT_ID="org.opennms.features.featuremgr.config";
	private static final String USE_REMOTE_PLUGIN_MANAGER_KEY = "org.opennms.karaf.featuremgr.useRemotePluginManagers";
	private static final String REMOTE_PLUGIN_MANAGER_URLS_KEY = "org.opennms.karaf.featuremgr.remotePluginManagersUrls";
	private static final String REMOTE_PLUGIN_MANAGER_USERNAME_KEY = "org.opennms.karaf.featuremgr.remoteUsername";
	private static final String REMOTE_PLUGIN_MANAGER_PASSWORD_KEY = "org.opennms.karaf.featuremgr.remotePassword";
	private static final String KARAF_INSTANCE_KEY="org.opennms.karaf.featuremgr.karafInstance";
	private static final String RETRY_INTERVAL_KEY="org.opennms.karaf.featuremgr.retryInterval";
	private static final String RETRY_NUMBER_KEY="org.opennms.karaf.featuremgr.retryNumber";
	private static final String UPDATE_INTERVAL_KEY="org.opennms.karaf.featuremgr.updateInterval";

	private FeaturesService featuresService=null;
	private ConfigurationAdmin configurationAdmin=null;

	private boolean useRemotePluginManagers = false;
	private Set<String> remotePluginManagersUrls = new LinkedHashSet<String>();
	private String remoteUsername=null;
	private String remotePassword=null;
	private String installedManifestUri=null;
	private String karafInstance=null;
	private Integer retryInterval=null;
	private Integer retryNumber=null;
	private Integer updateInterval=null;

	// blueprint wiring methods
	public void setConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
		this.configurationAdmin = configurationAdmin;
	}

	public void setUseRemotePluginManagers(String useRemotePluginManagers) {
		this.useRemotePluginManagers = Boolean.parseBoolean(useRemotePluginManagers);
	}

	public void setRemotePluginManagersUrls(String remotePluginManagersUrls) {
		this.remotePluginManagersUrls = stringCsvPropertyToList(remotePluginManagersUrls);
	}

	public void setRemoteUsername(String remoteUsername) {
		this.remoteUsername = remoteUsername;
	}

	public void setRemotePassword(String remotePassword) {
		this.remotePassword = remotePassword;
	}

	public void setFeaturesService(FeaturesService featuresService) {
		this.featuresService = featuresService;
	}

	public void setInstalledManifestUri(String installedManifestUri) {
		this.installedManifestUri = installedManifestUri;
	}

	public String getKarafInstance() {
		return karafInstance;
	}

	public void setKarafInstance(String karafInstance) {
		this.karafInstance = karafInstance;
	}

	public void setRetryInterval(Integer retryInterval) {
		this.retryInterval = retryInterval;
	}

	public void setRetryNumber(Integer retryNumber) {
		this.retryNumber = retryNumber;
	}

	public void setUpdateInterval(Integer updateInterval) {
		this.updateInterval = updateInterval;
	}

	// business methods

	@Override
	public synchronized String installNewManifest(String newManifestStr) {
		FeaturesUtils.installManifestFeatures(newManifestStr, installedManifestUri, featuresService);
		return "installed manifest";
	}

	@Override
	public synchronized String uninstallManifest() {
		FeaturesUtils.uninstallManifestFeatures(installedManifestUri, featuresService);
		return "uninstalled manifest";
	}


	@Override
	public synchronized void installNewManifestFromPluginManagerUrl(String karafInstance, String url, String userName, String password) {
		ManifestServiceClientRestJerseyImpl manifestServiceClient = new ManifestServiceClientRestJerseyImpl(); 
		manifestServiceClient.setBaseUrl(url);
		manifestServiceClient.setBasePath(""); // include based path in url e.g. http://localhost:8181/pluginmgr
		manifestServiceClient.setUserName(userName);
		manifestServiceClient.setPassword(password);

		String manifest=null;
		try {
			manifest=manifestServiceClient.getFeatureManifest(karafInstance);
		} catch (Exception e) {
			throw new RuntimeException("problem fetching manifest from remote pluging manager",e);
		}
		try {
			FeaturesUtils.installManifestFeatures(manifest, installedManifestUri, featuresService);
		}catch (Exception e) {
			throw new RuntimeException("problem installing manifest="+manifest,e);
		}
	}

	@Override
	public synchronized String updateManifestFromPluginManagers() {
		StringBuffer msg = new StringBuffer("updateManifest: ");
		for (String url :remotePluginManagersUrls){
			try{
				installNewManifestFromPluginManagerUrl(karafInstance, url, remoteUsername, remotePassword);
				msg.append(" successfully updated manifest from url="+url);
				LOG.debug(" successfully updated manifest from url="+url);
				break; // if success do not try other urls
			}catch (Exception ex){
				msg.append(" failed to update manifest from url="+url+"\n");
				LOG.error(" failed to update manifest from url="+url,ex);
			}
		}
		return msg.toString();
	}

	@Override
	public synchronized String getInstalledManifest() {
		if (installedManifestUri == null) throw new RuntimeException("ServiceLoader.getInstalledManifestUri() cannot be null.");
		try {
			URI uri = new URI(installedManifestUri);
			File installedManifestFile = new File(uri.getPath());
			Features features = FeaturesUtils.loadFeaturesFile(installedManifestFile);
			return FeaturesUtils.featuresToString(features);
		} catch(Exception ex) {
			throw new RuntimeException("problem loading installed manifest from installedManifestUri="+installedManifestUri,ex);
		}
	}


	@Override
	public synchronized void updateKarafInstance(String karafInstance) {
		this.karafInstance=karafInstance;
	}

	@Override
	public synchronized void updateRemotePluginServers(String remotePluginManagersUrls, String remoteUserName, String remotePassword) {
		this.remotePluginManagersUrls=stringCsvPropertyToList(remotePluginManagersUrls);
		this.remoteUsername=remoteUserName;
		this.remotePassword=remotePassword;
	}


	@Override
	public synchronized String updateSchedule(Boolean useRemotePluginManagers, Integer retryInterval, Integer retryNumber, Integer updateInterval) {

		if(useRemotePluginManagers!=null) this.useRemotePluginManagers = useRemotePluginManagers;
		if(retryInterval!=null) this.retryInterval = retryInterval;
		if(retryNumber!=null) this.retryNumber = retryNumber;
		if(updateInterval!=null) this.updateInterval = updateInterval;

		return ("useRemotePluginManagers="+ this.useRemotePluginManagers
				+", retryInterval="+this.retryInterval
				+", retryNumber="+this.retryNumber
				+", updateInterval="+this.updateInterval);

	}


	@Override
	public synchronized String persistConfiguration() {
		try {
			Configuration config = configurationAdmin.getConfiguration(PERSISTANT_ID);

			@SuppressWarnings("unchecked")
			Dictionary<String, Object> props = config.getProperties();

			// if null, the configuration is new
			if (props == null) {
				props = new Hashtable<String, Object>();
			}

			props.put(USE_REMOTE_PLUGIN_MANAGER_KEY,Boolean.toString(useRemotePluginManagers));
			props.put(REMOTE_PLUGIN_MANAGER_URLS_KEY,listToStringCsvProperty(remotePluginManagersUrls));
			props.put(REMOTE_PLUGIN_MANAGER_USERNAME_KEY,remoteUsername);
			props.put(REMOTE_PLUGIN_MANAGER_PASSWORD_KEY,remotePassword);
			props.put(KARAF_INSTANCE_KEY,karafInstance);
			props.put(RETRY_INTERVAL_KEY,Integer.toString(retryInterval));
			props.put(RETRY_NUMBER_KEY,Integer.toString(retryNumber));
			props.put(UPDATE_INTERVAL_KEY,Integer.toString(updateInterval));

			StringBuffer msg = new StringBuffer("Persisted configuration:\n");
			Enumeration<String> e = props.keys();
			while(e.hasMoreElements()){
				String key = e.nextElement();
				String value = (String) props.get(key);
				msg.append("    "+key+"="+value+"\n");
			}
			
			config.update(props);
			
			LOG.info(msg.toString());
			return msg.toString();
		} catch (Exception e) {
			throw new RuntimeException("problem updating configuration in "+PERSISTANT_ID+".cfg",e);
		}
	}


	// helper methods

	private Set<String> stringCsvPropertyToList(String setStringStr){
		Set<String> setString= new LinkedHashSet<String>();
		if ((setStringStr!=null) & (! "".equals(setStringStr)) ) {
			String[] stringArray = setStringStr.split(",");

			for (String str: stringArray){
				str.trim();
				if (! "".equals(str)){
					setString.add(str);
				} 
			}
		}
		return setString;
	}

	private String listToStringCsvProperty(Set<String> setString){

		StringBuffer sb=new StringBuffer();

		Iterator<String> itr = setString.iterator();
		while(itr.hasNext()){
			sb.append(itr.next());
			if (itr.hasNext()) sb.append(",");
		}

		return sb.toString();


	}

}
