package org.opennms.karaf.featuremgr;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Utilities to convert manifest maps to/from property strings 
 * 
 * A manifest Map contains the features in a manifest: Map<String,String> key= featureName, value=featureId
 * A featureId Set contains a Set<String> of featureId's: featureName/featureVersion or featureName (no version)
 * a csv string representing a manifest contains comma separated featureId's e.g. featureA,featureB/1.0.0-SNAPSHOT,featureC/1.0.0
 * @author admin
 *
 */
public class ManifestUtils {

	/**
	 * converts property definition of a manifest into a manifestMap
	 * e.g. featureA,featureB/1.0.0-SNAPSHOT becomes Map<String,String> ["featureA",""]["featureB","1.0.0-SNAPSHOT"]
	 * @param csvManifestString
	 * @return
	 */
	public static Map<String,String> csvStringToManifestMap(String csvManifestString){
		Set<String> manifestSet = csvStringToStringSet(csvManifestString);
		return stringSetToManifestMap(manifestSet);
	}
	
	/**
	 * converts manifestMap into a property definition of a manifest
	 * e.g. Map<String,String> ["featureA",""]["featureB","1.0.0-SNAPSHOT"] becomes featureA,featureB/1.0.0-SNAPSHOT
	 * @param manifestMap
	 * @return
	 */
	public static String manifestMapToCsvString(Map<String,String> manifestMap){
		Set<String> setString = manifestMapToStringSet(manifestMap );
		return stringSetToCsvString(setString);
	}

	/**
	 * turns a set of featureId's into a map of feature names and feature versions 
	 * Set<featureName/featureVersion> becomes Map<featureName,featureVersion>
	 * featureName can only be used once and will have only one version. 
	 * if featureId has no version  resulting featureName has a "" featureVersion
	 * @param manifestSet
	 * @return
	 */
	public static Map<String,String> stringSetToManifestMap(Set<String> manifestSet){
		Map<String,String> manifestMap = new LinkedHashMap<String,String>();

		// update manifest map
		for(String featureId: manifestSet){
			String[] split = featureId.split("/");
			String featureName=split[0];
			String featureVersion="";
			if (split.length>1)	featureVersion=split[1];
			manifestMap.put(featureName, featureVersion);
		}
		return manifestMap;

	}

	/**
	 * turns map of feature names and feature versions into a set of featureId's 
	 * Map<featureName,featureVersion> becomes Set<featureName/featureVersion>
	 * featurename can only be used once and will have only one version. 
	 * if version is "" or null the resulting featureId only contains featureName and no /
	 * @param manifestMap
	 * @return
	 */
	public static Set<String> manifestMapToStringSet(Map<String,String> manifestMap ){
		Set<String> setString = new LinkedHashSet<String>();
		for(Entry<String, String> e : manifestMap.entrySet()){
			String valueStr =  e.getValue()==null || "".equals(e.getValue()) ? "" : "/"+e.getValue();
			setString.add(e.getKey()+valueStr);
		}
		return setString;
	}

	/**
	 * turns set of strings into a single csv string
	 * e.g. e.g.  Set<String> [aaa,yyy,zzz] becomes aaa,yyy,zzz
	 * @param setString
	 * @return
	 */
	public static String stringSetToCsvString(Set<String> setString){
		StringBuffer sb=new StringBuffer();

		Iterator<String> itr = setString.iterator();
		while(itr.hasNext()){
			sb.append(itr.next());
			if (itr.hasNext()) sb.append(",");
		}
		return sb.toString();
	}


	/**
	 * turns csv string into set of strings separated at commas
	 * note csv values must be unique or will be overwritten
	 * e.g. aaa,yyy,zzz  becomes Set<String> [aaa,yyy,zzz]
	 * @param setStringStr 
	 * @return
	 */
	public static Set<String>  csvStringToStringSet(String setStringStr){
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

}
