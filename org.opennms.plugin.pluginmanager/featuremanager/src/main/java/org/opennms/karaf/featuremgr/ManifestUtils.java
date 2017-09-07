package org.opennms.karaf.featuremgr;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.opennms.karaf.featuremgr.jaxb.karaf.feature.Feature;
import org.opennms.karaf.featuremgr.jaxb.karaf.feature.Features;

//TODO REMOVE NOT USED?
public class ManifestUtils {
	private static final String MANIFEST_FEATURE_NAME ="manifest" ;
	/**
	 * returns true if features manifests are functionaly the same (no change)
	 * @return
	 */
	public static boolean compareManifestFeatures(Features manifest1, Features  manifest2){
		if(manifest1==null) throw new RuntimeException("parameter manifest1 cannot be null");
		if(manifest2==null) throw new RuntimeException("parameter manifest2 cannot be null");

		if (manifest1.getName() == null) {
			if (manifest2.getName() != null) return false;
		} else if (!manifest1.getName().equals(manifest2.getName())) return false;
		if(manifest1.getRepositoryOrFeature().size()!=manifest2.getRepositoryOrFeature().size()) return false;

		ManifestContents manifest1Contents= new ManifestContents(manifest1);
		ManifestContents manifest2Contents= new ManifestContents(manifest2);

		// compare repository lists
		ArrayList<String> manifest1Repos = manifest1Contents.getRepositoryList();
		ArrayList<String> manifest2Repos = manifest2Contents.getRepositoryList();
		if(manifest1Repos.size() != manifest2Repos.size()) return false; 
		if(!manifest1Repos.containsAll(manifest2Repos)) return false;

		// compare manifest features
		Map<String, Feature> manifest1FeatureMap = manifest1Contents.getFeatureMap();
		Map<String, Feature> manifest2FeatureMap = manifest2Contents.getFeatureMap();
		if(manifest1FeatureMap.size()!=manifest2FeatureMap.size()) return false;

		for(String featureName:manifest2FeatureMap.keySet()){
			Feature manifest1ManifestFeature = manifest1FeatureMap.get(featureName);
			Feature manifest2ManifestFeature = manifest2FeatureMap.get(featureName);	
			List<Object> f1 = manifest1ManifestFeature.getDetailsOrConfigOrConfigfile();
			List<Object> f2 = manifest1ManifestFeature.getDetailsOrConfigOrConfigfile();
			if(f1.size()!=f2.size()) return false;
			if(! f1.containsAll(f2)) return false;
		}
		return true;
	}

	private static class ManifestContents{
		String name;
		ArrayList<String> repositoryList = new ArrayList<String>();
		Map<String,Feature> featureMap = new LinkedHashMap<String,Feature>();

		ManifestContents(Features manifestFeatures){
			name = manifestFeatures.getName();
			List<Object> manifest1ObjectList = manifestFeatures.getRepositoryOrFeature();

			for(Object manifestobject:manifest1ObjectList){
				if(manifestobject instanceof String){
					repositoryList.add((String)manifestobject);
				}else if(manifestobject instanceof Feature){
					Feature feature = (Feature) manifestobject;
					featureMap.put(feature.getName(),feature);
				}else throw new RuntimeException("unknown type in Features object="+manifestobject.toString());
			}
		}

		public String getName() {
			return name;
		}
		public ArrayList<String> getRepositoryList() {
			return repositoryList;
		}
		public Map<String, Feature> getFeatureMap() {
			return featureMap;
		}

	}

}
