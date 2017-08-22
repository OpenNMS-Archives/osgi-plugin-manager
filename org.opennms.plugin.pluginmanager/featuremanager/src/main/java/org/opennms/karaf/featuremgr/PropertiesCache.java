package org.opennms.karaf.featuremgr;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.karaf.features.Feature;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * manages persistant properties in karaf config backend store
 * @author admin
 *
 */
public class PropertiesCache {
	private static final Logger LOG = LoggerFactory.getLogger(PluginFeatureManagerImpl.class);


	public static final String INSTALLED_MANIFEST_KEY = "org.opennms.karaf.featuremgr.installedmanifest";
	public static final String REQUIRED_MANIFEST_KEY = "org.opennms.karaf.featuremgr.requiredmanifest";
	public static final String USE_REMOTE_PLUGIN_MANAGER_KEY = "org.opennms.karaf.featuremgr.useRemotePluginManagers";
	public static final String REMOTE_PLUGIN_MANAGER_URLS_KEY = "org.opennms.karaf.featuremgr.remotePluginManagersUrls";
	public static final String REMOTE_PLUGIN_MANAGER_USERNAME_KEY = "org.opennms.karaf.featuremgr.remoteUsername";
	public static final String REMOTE_PLUGIN_MANAGER_PASSWORD_KEY = "org.opennms.karaf.featuremgr.remotePassword";

	//<reference id="configurationAdmin" interface="org.osgi.service.cm.ConfigurationAdmin"/> 
	private ConfigurationAdmin configurationAdmin=null;

	// name of config  file <persistentId>.cfg
	private String persistentId="org.opennms.features.featuremgr.config";

	public ConfigurationAdmin getConfigurationAdmin() {
		return configurationAdmin;
	}

	public void setConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
		this.configurationAdmin = configurationAdmin;
	}

	public String getPersistentId() {
		return persistentId;
	}

	public void setPersistentId(String persistentId) {
		this.persistentId = persistentId;
	}

	public Boolean getUseRemotePluginManager() {
		String bool = loadStringProperty(USE_REMOTE_PLUGIN_MANAGER_KEY);
		return (bool==null) ? false : Boolean.parseBoolean(bool);
	}

	public void setUseRemotePluginManager(boolean useRemotePluginManager) {
		saveStringProperty(REMOTE_PLUGIN_MANAGER_USERNAME_KEY, Boolean.toString(useRemotePluginManager));
	}

	public String getRemotePluginManagerUsername() {
		return loadStringProperty(REMOTE_PLUGIN_MANAGER_USERNAME_KEY);
	}

	public void setRemotePluginManagerUsername(String remotePluginManagerUsername) {
		saveStringProperty(REMOTE_PLUGIN_MANAGER_USERNAME_KEY, remotePluginManagerUsername);
	}

	public String getRemotePluginManagerPassword() {
		return loadStringProperty(REMOTE_PLUGIN_MANAGER_PASSWORD_KEY);
	}

	public void setRemotePluginManagerPassword(String remotePluginManagerPassword) {
		saveStringProperty(REMOTE_PLUGIN_MANAGER_PASSWORD_KEY, remotePluginManagerPassword);
	}

	public Set<String> getRemotePluginManagerUrls() {
		return  loadStringCsvProperty(REMOTE_PLUGIN_MANAGER_URLS_KEY);
	}

	public void setRemotePluginManagerUrls(Set<String> remotePluginManagerUrls) {
		saveStringCsvProperty(REMOTE_PLUGIN_MANAGER_URLS_KEY, remotePluginManagerUrls);
	}

	public Map<String,String> getInstalledManifest(){
		return loadManifestMap(INSTALLED_MANIFEST_KEY);
	}

	public void setInstalledManifest(Map<String,String> installedManifest) {
		saveManifestMap(INSTALLED_MANIFEST_KEY, installedManifest);
	}

	public Map<String,String> getRequiredManifest(){
		return loadManifestMap(REQUIRED_MANIFEST_KEY);
	}

	public void setRequiredManifest(Map<String,String> requiredManifest) {
		saveManifestMap(REQUIRED_MANIFEST_KEY, requiredManifest);
	}
	

	private Map<String,String> loadManifestMap(String key){
		Set<String> manifestSet = loadStringCsvProperty(key);
		Map<String,String> manifestMap = ManifestUtils.stringSetToManifestMap(manifestSet);
		return manifestMap;

	}
	
	private void saveManifestMap(String key, Map<String,String> manifestMap ){
		Set<String> setString = ManifestUtils.manifestMapToStringSet(manifestMap);
		saveStringCsvProperty(key, setString);
	}

	/**
	 * saves a  Set of strings split at each comma into a comma separated string property
	 * @param setString
	 * @param key key for property in config file
	 */
	private void saveStringCsvProperty(String key, Set<String> setString){
		try {
			StringBuffer sb=new StringBuffer();

			Iterator<String> itr = setString.iterator();
			while(itr.hasNext()){
				sb.append(itr.next());
				if (itr.hasNext()) sb.append(",");
			}
			saveStringProperty(sb.toString(), key);

		} catch (Exception e) {
			throw new RuntimeException("problem updating key definition "+key+ " in "+persistentId+".cfg",e);
		}
	}

	/**
	 * loads a comma separated string into a Set of strings split at each comma
	 * @param key
	 * @return
	 */
	private Set<String> loadStringCsvProperty(String key){
		Set<String> setString= new LinkedHashSet<String>();
		try {
			String setStringStr= loadStringProperty(key);

			if ((setStringStr!=null) & (! "".equals(setStringStr)) ) {
				String[] stringArray = setStringStr.split(",");

				for (String str: stringArray){
					str.trim();
					if (! "".equals(str)){
						setString.add(str);
					} 
				}
			}

		} catch (Exception e) {
			throw new RuntimeException("problem getting key definition "+key+ " from "+persistentId+".cfg",e);
		}
		return setString;
	}

	/**
	 * saves String property corresponding to key or
	 * @param key
	 * @param value
	 */
	private void saveStringProperty(String key,String value){
		try {
			Configuration config = configurationAdmin.getConfiguration(persistentId);

			@SuppressWarnings("unchecked")
			Dictionary<String, Object> props = config.getProperties();

			// if null, the configuration is new
			if (props == null) {
				props = new Hashtable<String, Object>();
			}

			props.put(key,value);
			config.update(props);

		} catch (Exception e) {
			throw new RuntimeException("problem updating key definition "+key+ " with value '"+value
					+"' in "+persistentId+".cfg",e);
		}
	}

	/**
	 * loads String property corresponding to key or null if doesn't exist
	 * @param key
	 * @return
	 */
	private String loadStringProperty(String key){
		Configuration config;
		String valueStr=null;

		try {
			config = configurationAdmin.getConfiguration(persistentId);

			@SuppressWarnings("unchecked")
			Dictionary<String, Object> props = config.getProperties();

			if (props == null) return null;

			valueStr = (String) props.get(key);

		} catch (Exception e) {
			throw new RuntimeException("problem getting key definition "+key+ " from "+persistentId+".cfg",e);
		}
		return valueStr;
	}

}


