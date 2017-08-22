package org.opennms.karaf.featuremgr;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PluginFeatureManagerImpl implements PluginFeatureManagerService {
	private static final Logger LOG = LoggerFactory.getLogger(PluginFeatureManagerImpl.class);

	private PropertiesCache propertiesCache=null;

	private FeaturesService featuresService=null;
	
	// blueprint wiring methods

	public PropertiesCache getPropertiesCache() {
		return propertiesCache;
	}

	public void setPropertiesCache(PropertiesCache propertiesCache) {
		this.propertiesCache = propertiesCache;
	}

	public FeaturesService getFeaturesService() {
		return featuresService;
	}

	public void setFeaturesService(FeaturesService featuresService) {
		this.featuresService = featuresService;
	}

	// business methods
	
	@Override
	public synchronized String installNewManifest(String newManifestStr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized String getInstalledManifest() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public synchronized String installNewManifestFromUrl(String url, String userName,
			String password) {
		// TODO Auto-generated method stub
		return null;
	}



	/**
	 * Map<Name,Version>
	 * @return
	 */
	private Map<String,String> refreshInstalledManifest(){
		if (propertiesCache==null) throw new RuntimeException("propertiesCache not set for pluginmanager");
		Map<String,String> installedManifest = propertiesCache.getInstalledManifest();
		Map<String,String> newInstalledManifest = new LinkedHashMap<String,String>();

		// update installedManifest
		for(String featureName: installedManifest.keySet()){
			String featureVersion = installedManifest.get(featureName);
			String installedVersion="";
			Feature installedFeature=null;
			try {
				if(! "".equals(featureVersion)) {
					installedFeature = featuresService.getFeature(featureName, featureVersion);
				} else {
					installedFeature = featuresService.getFeature(featureName);
				}
				if(installedFeature!=null) {
					installedVersion = installedFeature.getVersion();
					if (featuresService.isInstalled(installedFeature)){
						newInstalledManifest.put(featureName,installedVersion);
					};
				}
			} catch (Exception e) {
				LOG.error("problem while refreshing installed manifest for featureName="+featureName,e);
			}

		}
		// propertiesCache.setInstalledManifest(newInstalledManifest);
		return newInstalledManifest;

	}

	/**
	 * 1. refreshes the previously installed features in installedManifest
	 * 2. compares requiredManifest with the previously installed features in installedManifest
	 * 3. removes features in installedManifest but not in requiredManifest
	 * 4. removes features with different version to requiredManifest
	 * 5. installs features in requiredManifest not already installed
	 * 
	 * @param requiredManifest manifest of features to install
	 * @return
	 */
	private Map<String,String> synchronizeRequiredManifest(Map<String,String> requiredManifest){
		if (requiredManifest==null) throw new RuntimeException("requiredManifest not set for synchronizeRequiredManifest");

		Map<String,String> installedManifest = refreshInstalledManifest();

		LOG.info("modifying installedManifest :"+ManifestUtils.manifestMapToCsvString(installedManifest)+
				"\n to match requiredManifast :"+ManifestUtils.manifestMapToCsvString(requiredManifest));

		// remove features in installedManifest but not in required manifest
		Iterator<String> it = installedManifest.keySet().iterator();
		while(it.hasNext()){
			String featureName=it.next();
			if(! requiredManifest.containsKey(featureName)){
				// remove feature
				String featureVersion = requiredManifest.get(featureName);
				Feature feature=null;
				try {
					featuresService.uninstallFeature(featureName);
				} catch (Exception e) {
					LOG.error("could not uninstall installedManifest feature="+featureName,e);
				}
				LOG.info("removed from installedManifest feature="+featureName);
				it.remove();
			}
		}

		// install features in required manifest
		for(String featureName: requiredManifest.keySet()){

			String featureVersion = requiredManifest.get(featureName);
			String installedVersion="";
			if(! "".equals(featureVersion)) {
				Feature feature=null;
				try{
					feature = featuresService.getFeature(featureName,featureVersion);
				} catch (Exception e) {
					LOG.error("problem finding featureName="+featureName+" version="+featureVersion,e);
				}
				if(feature==null){
					try{
						feature = featuresService.getFeature(featureName);
					} catch (Exception e) {
						LOG.error("problem finding featureName="+featureName,e);
					}
					if(feature!=null){
						LOG.info("found featureName="+feature.getName()+" version="+feature.getVersion());
						if( featuresService.isInstalled(feature)){
							LOG.info("uninstalling featureName="+feature.getName()+" version="+feature.getVersion());
							try {
								featuresService.uninstallFeature(featureName);
							} catch (Exception e) {
								LOG.info("could not uninstall installedManifest feature="+featureName,e);
							}
						}
					}
					try{
						LOG.info("installing featureName="+feature.getName()+" version="+feature.getVersion());
						featuresService.installFeature(featureName,featureVersion);
						installedManifest.put(featureName, featureVersion);
					} catch (Exception e) {
						LOG.error("problem installing featureName="+featureName+" version="+featureVersion,e);
					}

				}

			}
		}
		LOG.info("saving new InstalledManifest="+ManifestUtils.manifestMapToCsvString(installedManifest));
		propertiesCache.setInstalledManifest(installedManifest);
		return installedManifest;
	}






	/**
	 * On startup
	 * 1. load remote manifests
	 * 2. compare local manifest with installed features
	 * 3. try to install new features in local manifest
	 * 
	 * command - install manifest (manifest)
	 * compare local manifest with installed features
	 * 
	 * required manifest - local required manifest
	 * installed manifest - local actually installed manifest
	 * remote manifest - remote required manifest
	 * 
	 * on startup
	 *   refreshInstalledManifest(){
	 *      instaledManifest forEach feature
	 *         if feature not installed remove from installed manifest
	 *         
	 *   setRequiedManifest
	 *   getRequiredManifest
	 *   
	 *   installManifest(){
	 *      installManifest(requiredManifest)
	 *      }
	 *      
	 *   installManifest(requiredManifest)
	 *      setRequiredManifest(requiredManifest)
	 *      synchronizeRequiredManifest(requiredManifest)
	 *   
	 * 
	 *   synchronizeRequiredManifest(requiredManifest){
	 *      instaledManifest forEach feature
	 *        if feature not in requiredManifest 
	 *     	      try { 
	 *                uninstall feature
	 *             }
	 *            remove from installedManifest
	 * 
	 *      requiredManifest forEach feature
	 *         is feature installed?
	 *         yes - add to installedManifest
	 *         no - try {
	 *              install feature 
	 *              add to installedManifest
	 *             }
	 *         return installedManifest

	 * 
	 */

	/**
	 * 	 * 
	 *   synchronizeRequiredManifest(requiredManifest){
	 *      instaledManifest forEach feature
	 *        if feature not in requiredManifest 
	 *     	      try { 
	 *                uninstall feature
	 *             }
	 *            remove from installedManifest
	 * 
	 *      requiredManifest forEach feature
	 *         is feature installed?
	 *         yes - add to installedManifest
	 *         no - try {
	 *              install feature 
	 *              add to installedManifest
	 *             }
	 *         return installedManifest
	 */

}
