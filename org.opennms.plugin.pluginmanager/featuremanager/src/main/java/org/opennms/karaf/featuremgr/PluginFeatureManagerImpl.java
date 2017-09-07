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
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.karaf.features.FeaturesService;
import org.opennms.karaf.featuremgr.TaskTimer.ScheduledTask;
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

	private FeaturesService m_featuresService=null;
	private ConfigurationAdmin m_configurationAdmin=null;

	private TaskTimer m_timer=new TaskTimer();

	private boolean m_useLocalManifestAtStartup=true;
	private boolean m_useRemotePluginManagers = false;	
	private Set<String> m_remotePluginManagersUrls = new LinkedHashSet<String>();
	private String m_remoteUsername=null;
	private String m_remotePassword=null;
	private String m_installedManifestUri=null;
	private String m_karafInstance=null;
	private Integer m_retryInterval=null;
	private Integer m_retryNumber=null;
	private Integer m_updateInterval=null;

	// blueprint wiring methods

	public void setFeaturesService(FeaturesService featuresService) {
		this.m_featuresService = featuresService;
	}

	public void setConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
		this.m_configurationAdmin = configurationAdmin;
	}

	public void setUseLocalManifestAtStartup(String useLocalManifestAtStartup) {
		this.m_useLocalManifestAtStartup = Boolean.parseBoolean(useLocalManifestAtStartup);
	}

	public void setUseRemotePluginManagers(String useRemotePluginManagers) {
		this.m_useRemotePluginManagers = Boolean.parseBoolean(useRemotePluginManagers);
	}

	public void setRemotePluginManagersUrls(String remotePluginManagersUrls) {
		this.m_remotePluginManagersUrls = stringCsvPropertyToList(remotePluginManagersUrls);
	}

	public void setRemoteUsername(String remoteUsername) {
		this.m_remoteUsername = remoteUsername;
	}

	public void setRemotePassword(String remotePassword) {
		this.m_remotePassword = remotePassword;
	}

	public void setInstalledManifestUri(String installedManifestUri) {
		this.m_installedManifestUri = installedManifestUri;
	}

	public String getKarafInstance() {
		return m_karafInstance;
	}

	public void setKarafInstance(String karafInstance) {
		this.m_karafInstance = karafInstance;
	}

	public void setRetryInterval(Integer retryInterval) {
		this.m_retryInterval = retryInterval;
	}

	public void setRetryNumber(Integer retryNumber) {
		this.m_retryNumber = retryNumber;
	}

	public void setUpdateInterval(Integer updateInterval) {
		this.m_updateInterval = updateInterval;
	}

	// business methods



	/**
	 * init method at startup
	 */
	public synchronized void init(){
		System.out.println("PluginFeatureManager starting up. Use local manifest at startup="+m_useLocalManifestAtStartup
				+ " Use remote plugin managers="+m_useRemotePluginManagers);
		LOG.info("PluginFeatureManager starting up Use local manifest at startup="+m_useLocalManifestAtStartup
				+ " Use remote plugin managers="+m_useRemotePluginManagers);

		// run in separate thread so that init thread completes
		Thread manifestStartup = new Thread(new Runnable() {
			public void run() {
				if(m_useLocalManifestAtStartup) {
					LOG.info("PluginFeatureManager starting up without using local manifest (useLocalManifestAtStartup=true)");
					try{
						String manifest = getInstalledManifest();
						if (manifest==null){
							LOG.info("PluginFeatureManager trying to startup using local manifest but no local manifest file present.");
						} else{
							LOG.info("PluginFeatureManager trying to startup using local manifest="+manifest);
							installNewManifest(manifest);
							LOG.info("PluginFeatureManager installed local manifest");
						}
					}catch(Exception ex){
						LOG.error("PluginFeatureManager problem installing local manifest",ex);
					}
				} LOG.info("PluginFeatureManager starting up without using local manifest (useLocalManifestAtStartup=false)");
				// start manifest schedule if enabled after installing local manifest
				if(m_useRemotePluginManagers){
					LOG.info("PluginFeatureManager schedulling download of manifests from remote plugin manager (useRemotePluginManagers=true)");
					try{
						restartSchedule();
					}catch(Exception ex){
						LOG.error("PluginFeatureManager problem starting manifest download schedule",ex);
					}
				} else {
					LOG.info("PluginFeatureManager download of manifests from remote plugin manager not scheduled. (useRemotePluginManagers=false)");
				}
			}
		});
		manifestStartup.start();


		LOG.info("PluginFeatureManager started");
		System.out.println("PluginFeatureManager started");
	}

	/**
	 * destroy method
	 */
	public synchronized void destroy(){
		LOG.info("PluginFeatureManager shutting down");
		System.out.println("PluginFeatureManager shutting down");
		try{
			if (m_timer!=null) m_timer.stopSchedule();
		} catch ( Exception ex){
			LOG.error("problem stopping schedule when shutting down", ex);
		}finally {
			m_timer=null;
		}

	}

	@Override
	public synchronized String installNewManifest(String newManifestStr) {
		FeaturesUtils.installManifestFeatures(newManifestStr, m_installedManifestUri, m_featuresService);
		return "installed manifest";
	}

	@Override
	public synchronized String uninstallManifest() {
		FeaturesUtils.uninstallManifestFeatures(m_installedManifestUri, m_featuresService);
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
			FeaturesUtils.installManifestFeatures(manifest, m_installedManifestUri, m_featuresService);
		}catch (Exception e) {
			throw new RuntimeException("problem installing manifest="+manifest,e);
		}
	}

	@Override
	public synchronized boolean updateManifestFromPluginManagers() {
		boolean success=false;
		for (String url :m_remotePluginManagersUrls){
			try{
				installNewManifestFromPluginManagerUrl(m_karafInstance, url, m_remoteUsername, m_remotePassword);
				success=true;
				LOG.info(" successfully updated manifest from url="+url);
				break; // if success do not try other urls
			}catch (Exception ex){
				LOG.error(" failed to update manifest from url="+url,ex);
			}
		}
		return success;
	}

	@Override
	public synchronized String getInstalledManifest() {
		if (m_installedManifestUri == null) throw new RuntimeException("ServiceLoader.getInstalledManifestUri() cannot be null.");
		try {
			URI uri = new URI(m_installedManifestUri);
			File installedManifestFile = new File(uri.getPath());

			if(!installedManifestFile.exists()) return null;

			Features features = FeaturesUtils.loadFeaturesFile(installedManifestFile);
			return FeaturesUtils.featuresToString(features);
		} catch(Exception ex) {
			throw new RuntimeException("problem loading installed manifest from installedManifestUri="+m_installedManifestUri,ex);
		}
	}


	@Override
	public synchronized void updateKarafInstance(String karafInstance) {
		this.m_karafInstance=karafInstance;
	}

	@Override
	public synchronized void updateRemotePluginServers(String remotePluginManagersUrls, String remoteUserName, String remotePassword) {
		this.m_remotePluginManagersUrls=stringCsvPropertyToList(remotePluginManagersUrls);
		this.m_remoteUsername=remoteUserName;
		this.m_remotePassword=remotePassword;
	}

	private class ScheduledManifestUpdate implements ScheduledTask{
		private PluginFeatureManagerImpl pluginFeatureManager;
		private AtomicInteger count = new AtomicInteger(0);

		ScheduledManifestUpdate(PluginFeatureManagerImpl pluginFeatureManager){
			this.pluginFeatureManager=Objects.requireNonNull(pluginFeatureManager);
		}

		@Override
		public boolean runScheduledTask() {
			int c = count.incrementAndGet();
			boolean success=false;

			LOG.info("Running scheduled manifest update. Times this schedule has run: "+c);
			try{
				success=pluginFeatureManager.updateManifestFromPluginManagers();
			} catch(Exception e){
				LOG.error("problem running schedule updating manifest from plugin managers",e);
			}
			return success;
		}

	}

	public synchronized void restartSchedule(){
		if(this.m_retryInterval==null) throw new RuntimeException("retryInterval cannot be null when starting schedule");
		if(this.m_retryNumber==null) throw new RuntimeException("retryNumber cannot be null when starting schedule");
		if(this.m_updateInterval==null) throw new RuntimeException("updateInterval cannot be null when starting schedule");

		m_timer.stopSchedule();
		if(this.m_useRemotePluginManagers){
			m_timer.setRetryInterval(this.m_retryInterval);
			m_timer.setRetryNumber(this.m_retryNumber);
			m_timer.setUpdateInterval(this.m_updateInterval);

			PluginFeatureManagerImpl pluginFeatureManager=this;
			ScheduledTask task = new ScheduledManifestUpdate(pluginFeatureManager);

			m_timer.setTask(task);

			m_timer.startSchedule();
		}
	}

	public synchronized void stopSchedule(){
		if (m_timer!=null) m_timer.stopSchedule();
	}

	@Override
	public synchronized String updateSchedule(Boolean useRemotePluginManagers, Integer retryInterval, Integer retryNumber, Integer updateInterval) {
		boolean justlist=true;
		String msg="";
		if(retryInterval!=null) {
			justlist=false;
			this.m_retryInterval = retryInterval;
		}
		if(retryNumber!=null) {
			justlist=false;
			this.m_retryNumber = retryNumber;
		}
		if(updateInterval!=null) {
			justlist=false;
			this.m_updateInterval = updateInterval;
		}
		if(useRemotePluginManagers!=null) {
			justlist=false;
			this.m_useRemotePluginManagers=useRemotePluginManagers;
		}

		if(!justlist){
			msg="Restarting schedule with new configuration.\n";
			restartSchedule();
		}

		if(m_timer.getScheduleIsRunning()){
			msg=msg+"Schedule Running\n";
		} else msg=msg+"Schedule Stopped\n";

		msg=msg+"Schedule configuration = useRemotePluginManagers="+ this.m_useRemotePluginManagers
				+", retryInterval="+this.m_retryInterval
				+", retryNumber="+this.m_retryNumber
				+", updateInterval="+this.m_updateInterval;

		return msg;

	}



	@Override
	public synchronized String persistConfiguration() {
		try {
			Configuration config = m_configurationAdmin.getConfiguration(PERSISTANT_ID);

			@SuppressWarnings("unchecked")
			Dictionary<String, Object> props = config.getProperties();

			// if null, the configuration is new
			if (props == null) {
				props = new Hashtable<String, Object>();
			}

			props.put(USE_REMOTE_PLUGIN_MANAGER_KEY,Boolean.toString(m_useRemotePluginManagers));
			props.put(REMOTE_PLUGIN_MANAGER_URLS_KEY,listToStringCsvProperty(m_remotePluginManagersUrls));
			props.put(REMOTE_PLUGIN_MANAGER_USERNAME_KEY,m_remoteUsername);
			props.put(REMOTE_PLUGIN_MANAGER_PASSWORD_KEY,m_remotePassword);
			props.put(KARAF_INSTANCE_KEY,m_karafInstance);
			props.put(RETRY_INTERVAL_KEY,Integer.toString(m_retryInterval));
			props.put(RETRY_NUMBER_KEY,Integer.toString(m_retryNumber));
			props.put(UPDATE_INTERVAL_KEY,Integer.toString(m_updateInterval));

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
