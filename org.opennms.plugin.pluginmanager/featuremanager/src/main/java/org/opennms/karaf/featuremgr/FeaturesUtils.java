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
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.features.FeaturesService.Option;
import org.opennms.karaf.featuremgr.jaxb.karaf.feature.Dependency;
import org.opennms.karaf.featuremgr.jaxb.karaf.feature.Feature;
import org.opennms.karaf.featuremgr.jaxb.karaf.feature.Features;
import org.opennms.karaf.featuremgr.jaxb.karaf.feature.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeaturesUtils {
	private static final Logger LOG = LoggerFactory.getLogger(FeaturesUtils.class);

	private static final String MANIFEST_FEATURES_REPOSITORY_NAME ="manifest-features" ;
	private static final String MANIFEST_FEATURE_NAME ="manifest" ;
	private static final String MANIFEST_FEATURE_VERSION = "1.0-SNAPSHOT";

	private static final String EMPTY_MANIFEST_FEATURES="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
			+"<features name=\"manifest-features\" xmlns=\"http://karaf.apache.org/xmlns/features/v1.2.0\">"
			+"  <feature name=\"manifest\" version=\"1.0-SNAPSHOT\" description=\"Plugin manifest to be installed at startup\">"
			+"  </feature>"
			+"</features>";

	public static synchronized String synchronizeManifestFeaturesFiles(String manifestFeaturesUri, String installedManifestFeaturesUri, FeaturesService featuresService){

		File manifestFeaturesFile = new File(manifestFeaturesUri);
		if(! manifestFeaturesFile.exists()) throw new RuntimeException("cannot synchronize manifestFeaturesFile does not exist "+manifestFeaturesFile.getAbsolutePath());
		Features manifestFeatures = FeaturesUtils.loadFeaturesFile(manifestFeaturesFile);

		File installedManifestFeaturesFile = new File(installedManifestFeaturesUri);
		if(! installedManifestFeaturesFile.exists()){
			// save NEW installedManifestFeatures features file with no content
			Features installedManifestFeatures = new Features();
			installedManifestFeatures.setName(MANIFEST_FEATURES_REPOSITORY_NAME);
			Feature manifestFeature = new Feature();
			manifestFeature.setName(MANIFEST_FEATURE_NAME);
			manifestFeature.setVersion(MANIFEST_FEATURE_VERSION);
			installedManifestFeatures.getRepositoryOrFeature().add(manifestFeature);
			FeaturesUtils.persistFeaturesFile(installedManifestFeatures, installedManifestFeaturesFile);
		}

		Features installedManifestFeatures = FeaturesUtils.loadFeaturesFile(installedManifestFeaturesFile);
		String msg = synchronizeManifestFeatures(manifestFeatures,installedManifestFeatures, featuresService);
		FeaturesUtils.persistFeaturesFile(installedManifestFeatures, installedManifestFeaturesFile);
		return msg;
	}


	public static synchronized String synchronizeManifestFeatures(Features manifestFeatures, Features installedManifestFeatures, FeaturesService featuresService){
		StringBuffer msg = new StringBuffer();

		List<String> repoList = new ArrayList<String>();
		Map<String,Feature> featureMap = new LinkedHashMap<String,Feature>();
		List<Dependency> manifestFeatureDependencyList = new ArrayList<Dependency>();

		List<String> installedRepoList = new ArrayList<String>();
		Map<String,Feature> installedFeatureMap = new LinkedHashMap<String,Feature>();
		List<Dependency> installedManifestFeatureDependencyList = new ArrayList<Dependency>();

		// get repos and feature lists from new manifestFeatures
		List<Object> reposOrfeatures = manifestFeatures.getRepositoryOrFeature();
		if(reposOrfeatures !=null){
			for (Object repoOrfeature  :reposOrfeatures){
				if (repoOrfeature instanceof Feature){
					Feature feature = (Feature) repoOrfeature;
					featureMap.put(feature.getName(),feature);
				} else if (repoOrfeature instanceof String){
					repoList.add((String) repoOrfeature);
				} else throw new RuntimeException("unknown object type "
						+ repoOrfeature.getClass().getCanonicalName()
						+ " in feature definition");
			}
		}
		if(!featureMap.containsKey(MANIFEST_FEATURE_NAME)) throw new RuntimeException("new manifest features definition does not contain feature"+MANIFEST_FEATURE_NAME);

		Feature manifestFeature = featureMap.get(MANIFEST_FEATURE_NAME);
		for(Object content : manifestFeature.getDetailsOrConfigOrConfigfile()){
			if (content instanceof Dependency) manifestFeatureDependencyList.add((Dependency) content);
		}


		// get repos and feature lists from installedManifestFeatures
		List<Object> installedReposOrfeatures = installedManifestFeatures.getRepositoryOrFeature();
		if(installedReposOrfeatures  !=null){
			for (Object installedRepoOrfeature  :installedReposOrfeatures ){
				if (installedRepoOrfeature instanceof Feature){
					Feature feature = (Feature) installedRepoOrfeature;
					installedFeatureMap.put(feature.getName(),feature);
				} else if (installedRepoOrfeature instanceof String){
					installedRepoList.add((String) installedRepoOrfeature);
				} else throw new RuntimeException("unknown object type "
						+ installedRepoOrfeature.getClass().getCanonicalName()
						+ " in feature definition");
			}
		}
		if(!installedFeatureMap.containsKey(MANIFEST_FEATURE_NAME)) throw new RuntimeException("installed manifest features definition does not contain feature"+MANIFEST_FEATURE_NAME);

		Feature installedManifestFeature = installedFeatureMap.get(MANIFEST_FEATURE_NAME);
		for(Object content : installedManifestFeature.getDetailsOrConfigOrConfigfile()){
			if (content instanceof Dependency)  installedManifestFeatureDependencyList.add((Dependency) content);
		}


		// remove old manifest features which are not in new manifest list
		Iterator<Dependency> flIterator = installedManifestFeatureDependencyList.iterator();
		while(flIterator.hasNext()){
			Dependency f = flIterator.next();
			String fname = f.getValue();
			boolean remove = true;
			for (Dependency dependency: manifestFeatureDependencyList){
				if (dependency.getValue().equals(fname)) remove = false;
			}
			if(remove){
				try {
					EnumSet<Option> options = EnumSet.of(Option.NoFailOnFeatureNotFound);
					featuresService.uninstallFeature(fname, options );
					flIterator.remove();
					msg.append("removed installed manifest feature:"+fname+"\n");
				} catch (Exception e) {
					LOG.error("cannot remove installed manifest feature:"+fname ,e);
				}
			}

		}


		// refresh manifest repositories
		for (String repo: repoList){
			URI uri;
			try {
				LOG.debug("refreshing manifest repository: "+repo);
				uri = new URI(repo);
				boolean uninstall=false;
				featuresService.removeRepository(uri, uninstall);
				boolean install=false;
				featuresService.addRepository(uri, install);
				msg.append("refreshed manifest repository uri:"+repo+"\n");
			} catch (Exception e) {
				LOG.error("cannot refresh manifest repository uri:"+repo ,e);

			}
		}

		// install manifest features
		for (Dependency dependency: manifestFeatureDependencyList){
			try {
				LOG.debug("installing manifest feature: "+dependency.getValue());
				featuresService.installFeature(dependency.getValue());
				msg.append("installed manifest feature:"+dependency.getValue()+"\n");
			} catch (Exception e) {
				LOG.error("cannot install manifest feature:"+dependency.getValue() ,e);
				msg.append("cannot install manifest feature:"+dependency.getValue()+"\n");
			}
		}

		// replace installed manifest list
		installedManifestFeatures.getRepositoryOrFeature().clear();
		installedManifestFeatures.getRepositoryOrFeature().addAll(manifestFeatures.getRepositoryOrFeature());


		return msg.toString();
	}

	public static synchronized void installManifestFeatures (String manifest, String installedManifestFeaturesUri,FeaturesService featuresService){

		try{
			Features features = FeaturesUtils.parseFeatures(manifest);

			URI uri = new URI(installedManifestFeaturesUri);

			File installedManifestFile = new File(uri.getPath());

			FeaturesUtils.persistFeaturesFile(features, installedManifestFile);

			featuresService.addRepository(uri);

			org.apache.karaf.features.Feature mfeature = featuresService.getFeature(MANIFEST_FEATURE_NAME);

			if (mfeature !=null && featuresService.isInstalled(mfeature )){
				featuresService.uninstallFeature(MANIFEST_FEATURE_NAME);
			}

			featuresService.installFeature(MANIFEST_FEATURE_NAME);


		} catch(Exception ex) {
			throw new RuntimeException("problem installing manifest features", ex);
		}
	}

	public static synchronized void uninstallManifestFeatures(String installedManifestFeaturesUri, FeaturesService featuresService) {
		try{
			installManifestFeatures (EMPTY_MANIFEST_FEATURES, installedManifestFeaturesUri,featuresService);

		} catch(Exception ex) {
			throw new RuntimeException("problem uninstalling manifest features", ex);
		}

	}

	public static  Features parseFeatures(String featuresStr){
		Features features=null;
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(Features.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			StringReader reader = new StringReader(featuresStr);

			JAXBElement<Features> jbe = (JAXBElement<Features>) jaxbUnmarshaller.unmarshal(reader);
			features = jbe.getValue();
		} catch (JAXBException e) {
			throw new RuntimeException(
					"Problem parsing features string", e);
		}
		return features;
	}


	public static String featuresToString(Features features){
		try {

			JAXBContext jaxbContext = JAXBContext.newInstance(Features.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// wrap features in JaxbElement
			ObjectFactory of = new ObjectFactory();
			JAXBElement<Features> jbeFeature = of.createFeatures(features);

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			StringWriter sw = new StringWriter();

			jaxbMarshaller.marshal(jbeFeature, sw);

			return sw.toString();

		} catch (JAXBException e) {
			throw new RuntimeException(
					"Problem persisting feature file", e);
		}
	}
	
	/**
	 * returns true if features manifests are functionaly the same (no change)
	 * @return
	 */
	public static boolean compareManifestFeatures(Features manifest1, Features  manifest2){
		if(manifest1==null) throw new RuntimeException("parameter manifest1 cannot be null");
		if(manifest2==null) throw new RuntimeException("parameter manifest2 cannot be null");
		
		// note may not work if marshallar orders lists randomly
		if(featuresToString(manifest1).equals(featuresToString(manifest2))) return true;
		return false;

	}

	public static synchronized void persistFeaturesFile(Features features, File featuresFile){
		try {
			featuresFile.getParentFile().mkdirs(); // creates directory if doesn't exist

			JAXBContext jaxbContext = JAXBContext.newInstance(Features.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// wrap basic feature in JaxbElement
			ObjectFactory of = new ObjectFactory();
			JAXBElement<Features> jbeFeature = of.createFeatures(features);

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(jbeFeature, featuresFile);

		} catch (JAXBException e) {
			throw new RuntimeException(
					"Problem persisting feature file", e);
		}
	}

	public static synchronized Features loadFeaturesFile(File featuresFile){
		Features features=null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Features.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			JAXBElement<Features> jbe = (JAXBElement<Features>) jaxbUnmarshaller.unmarshal(featuresFile);
			features = jbe.getValue();
		} catch (JAXBException e) {
			throw new RuntimeException(
					"Problem loading features file", e);
		}
		return features;

	}


}

