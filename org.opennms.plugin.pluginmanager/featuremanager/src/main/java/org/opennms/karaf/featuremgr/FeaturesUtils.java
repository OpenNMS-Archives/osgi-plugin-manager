package org.opennms.karaf.featuremgr;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.features.FeaturesService.Option;
import org.opennms.karaf.featuremgr.jaxb.karaf.feature.Feature;
import org.opennms.karaf.featuremgr.jaxb.karaf.feature.Features;
import org.opennms.karaf.featuremgr.jaxb.karaf.feature.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FeatureUtils {
	private static final Logger LOG = LoggerFactory.getLogger(FeatureUtils.class);
	
	
	public static synchronized String synchronizeManifestFeaturesFiles(String manifestFeaturesUri, String installedManifestFeaturesUri, FeaturesService featuresService){
		
		File manifestFeaturesFile = new File(manifestFeaturesUri);
		if(! manifestFeaturesFile.exists()) throw new RuntimeException("cannot synchronize manifestFeaturesFile does not exist "+manifestFeaturesFile.getAbsolutePath());
		Features manifestFeatures = FeatureUtils.loadFeaturesfile(manifestFeaturesFile);
		
		File installedManifestFeaturesFile = new File(installedManifestFeaturesUri);
		if(! manifestFeaturesFile.exists()){
			// save NEW installedManifestFeatures features file with no content
			Features installedManifestFeatures = new Features();
			installedManifestFeatures.setName("manifest-features");
			Feature manifestFeature = new Feature();
			manifestFeature.setName("manifest");
			manifestFeature.setVersion("0.0.1-SNAPSHOT");
			installedManifestFeatures.getRepositoryOrFeature().add(manifestFeature);
			FeatureUtils.persistFeaturesfile(installedManifestFeatures, installedManifestFeaturesFile);
		}

		Features installedManifestFeatures = FeatureUtils.loadFeaturesfile(installedManifestFeaturesFile);
		String msg = synchronizeManifestFeatures(manifestFeatures,installedManifestFeatures, featuresService);
		FeatureUtils.persistFeaturesfile(installedManifestFeatures, installedManifestFeaturesFile);
		return msg;
	}
	
	public static synchronized String synchronizeManifestFeatures(Features manifestFeatures, Features installedManifestFeatures, FeaturesService featuresService){
		StringBuffer msg = new StringBuffer();
		List<String> repoList = new ArrayList<String>();
		List<Feature> featureList = new ArrayList<Feature>();
		
		List<String> installedRepoList = new ArrayList<String>();
		List<Feature> installedFeatureList = new ArrayList<Feature>();
		
		// get repos and feature lists from installedManifestFeatures
		List<Object> installedReposOrfeatures = installedManifestFeatures.getRepositoryOrFeature();
		if(installedReposOrfeatures  !=null){
			for (Object installedRepoOrfeature  :installedReposOrfeatures ){
				if (installedRepoOrfeature instanceof Feature){
					installedFeatureList.add((Feature) installedRepoOrfeature);
				} else if (installedRepoOrfeature instanceof String){
					installedRepoList.add((String) installedRepoOrfeature);
				} else throw new RuntimeException("unknown object type "
						+ installedRepoOrfeature.getClass().getCanonicalName()
						+ " in feature definition");
			}
		}
		
		// get repos and feature lists from manifestFeatures
		List<Object> reposOrfeatures = manifestFeatures.getRepositoryOrFeature();
		if(reposOrfeatures !=null){
			for (Object repoOrfeature  :reposOrfeatures){
				if (repoOrfeature instanceof Feature){
					featureList.add((Feature) repoOrfeature);
				} else if (repoOrfeature instanceof String){
					repoList.add((String) repoOrfeature);
				} else throw new RuntimeException("unknown object type "
						+ repoOrfeature.getClass().getCanonicalName()
						+ " in feature definition");
			}
		}
		
		// remove old manifest features which are not in new manifest list
		Iterator<Feature> flIterator = installedFeatureList.iterator();
		while(flIterator.hasNext()){
			Feature f = flIterator.next();
			String fname = f.getName();
			boolean remove = true;
			for (Feature feature: featureList){
				if (feature.getName().equals(fname)) remove = false;
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
		for (Feature feature: featureList){
			try {
				LOG.debug("installing manifest feature: "+feature.getName());
				featuresService.installFeature(feature.getName());
				msg.append("installed manifest feature:"+feature.getName()+"\n");
			} catch (Exception e) {
				LOG.error("cannot install manifest feature:"+feature.getName() ,e);
				msg.append("cannot install manifest feature:"+feature.getName()+"\n");
			}
		}

		return msg.toString();
	}

	
	public static synchronized void persistFeaturesfile(Features features, File featuresFile){
		try {
			
			JAXBContext jaxbContext = JAXBContext.newInstance(Features.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// wrap basic feature in JaxbElement
			ObjectFactory of = new ObjectFactory();
			JAXBElement<Features> jbeFeature = of.createFeatures(features);
			
			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(jbeFeature, featuresFile);

			//	OutputStream out = new FileOutputStream(pluginModelFile);
			//	jaxbMarshaller.marshal(features, out);

		} catch (JAXBException e) {
			throw new RuntimeException(
					"Problem persisting feature file", e);
		}
	}

	public static synchronized Features loadFeaturesfile(File featuresFile){
		Features features=null;
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(Features.class);

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

