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

package org.opennms.karaf.featuremgr.rest.impl;

import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.features.Repository;
import org.opennms.karaf.featuremgr.jaxb.FeatureList;
import org.opennms.karaf.featuremgr.jaxb.FeatureWrapperJaxb;
import org.opennms.karaf.featuremgr.jaxb.ErrorMessage;
import org.opennms.karaf.featuremgr.jaxb.RepositoryList;
import org.opennms.karaf.featuremgr.jaxb.RepositoryWrapperJaxb;
import org.opennms.karaf.featuremgr.jaxb.ReplyMessage;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * REST service to manipulate Karaf features
 */
@Path("/")
public class FeaturesServiceRestImpl {

	/* ************************************
	 * feature management rest interface
	 * ************************************
	 */
	
	/**
	 * Returns an explicit collection of all features in XML format in response to HTTP GET requests.
	 * @return a response containing a list of Features
	 */
	@GET
	@Path("/features-list")
	@Produces(MediaType.APPLICATION_XML)
	public Response  getFeaturesList() throws Exception {
		
		FeaturesService featuresService = ServiceLoader.getFeaturesService();
		if (featuresService == null) throw new RuntimeException("ServiceLoader.getLicencePublisher() cannot be null.");

		FeatureList featuresList = new FeatureList();
		Feature[] features = featuresService.listFeatures();
		for (int i = 0; i < features.length; i++) {
            Boolean isInstalled = featuresService.isInstalled(features[i]);
			FeatureWrapperJaxb wrapper = new FeatureWrapperJaxb(features[i].getName(), features[i].getVersion(), features[i].getDescription(), features[i].getDetails(),isInstalled);
			featuresList.getFeatureList().add(wrapper);     
		}     
		return Response.status(200).entity(featuresList).build();  
	}
	
	/** 
	 * Returns feature in XML format in response to HTTP GET requests.
	 * @return a response containing a feature or an ErrorMessage
	 */
	@GET
	@Path("/features-info")
	@Produces(MediaType.APPLICATION_XML)
	public Response  getFeaturesInfo(@QueryParam("name") String name, @QueryParam("version") String version) throws Exception {
		
		FeaturesService featuresService = ServiceLoader.getFeaturesService();
		if (featuresService == null) throw new RuntimeException("ServiceLoader.getLicencePublisher() cannot be null.");

		Feature feature=null;
        Boolean isInstalled=null;
		try{
			if (name== null) throw new RuntimeException("feature name cannot be null.");
			if (version !=null) {
				feature = featuresService.getFeature(name,version); 
			} else feature = featuresService.getFeature(name); 
			if (feature== null) throw new RuntimeException("feature not found.");
			isInstalled = featuresService.isInstalled(feature);
		} catch (Exception exception){
			//return status 400 Error
			return Response.status(400).entity(new ErrorMessage(400, 0, "unable to get feature name="+name+ " version="+version, null, exception)).build();
		}
		FeatureWrapperJaxb featurewrapper = new FeatureWrapperJaxb(feature.getName(), feature.getVersion(), feature.getDescription(), feature.getDetails(), isInstalled);
		return Response.status(200).entity(featurewrapper).build();  
	}

	/** 
	 * Installs a feature with the specified name and version.
	 * @param name  name of the feature
	 * @param version version of the feature (optional - if not supplied will use the latest found)
	 * @return a 200 response or an ErrorMessage
	 */
	@GET
	@Path("/features-install")
	@Produces(MediaType.APPLICATION_XML)
	public Response  featuresInstall(@QueryParam("name") String name, @QueryParam("version") String version) throws Exception {
		
		FeaturesService featuresService = ServiceLoader.getFeaturesService();
		if (featuresService == null) throw new RuntimeException("ServiceLoader.getLicencePublisher() cannot be null.");

		try{
			if (name== null) throw new RuntimeException("feature name cannot be null.");
			if (version !=null) {
				featuresService.installFeature(name, version);
			} else featuresService.installFeature(name); 
		
		} catch (Exception exception){
			//return status 400 Error
			return Response.status(400).entity(new ErrorMessage(400, 0, "unable to install feature name="+name+ " version="+version, null, exception)).build();
		}

		return Response.status(200).entity(new ReplyMessage(200, 0, "Success. Installed feature name="+name+ " version="+version, null,null)).build();

	}
	

	/** 
	 * Uninstalls a feature with the specified name and version.
	 * @param name  name of the feature
	 * @param version version of the feature (optional - if not supplied will use the latest found)
	 * @return a 200 response or an ErrorMessage
	 */
	@GET
	@Path("/features-uninstall")
	@Produces(MediaType.APPLICATION_XML)
	public Response  featuresUninstall(@QueryParam("name") String name, @QueryParam("version") String version) throws Exception {
		
		FeaturesService featuresService = ServiceLoader.getFeaturesService();
		if (featuresService == null) throw new RuntimeException("ServiceLoader.getLicencePublisher() cannot be null.");

		try{
			if (name== null) throw new RuntimeException("feature name cannot be null.");
			if (version !=null) {
				featuresService.uninstallFeature(name, version);
			} else featuresService.uninstallFeature(name); 
		
		} catch (Exception exception){
			//return status 400 Error
			return Response.status(400).entity(new ErrorMessage(400, 0, "Unable to uninstall feature name="+name+ " version="+version, null, exception)).build();
		}

		return Response.status(200).entity(new ReplyMessage(200, 0, "Success. Uninstalled feature name="+name+ " version="+version, null,null)).build();
	}
	
/* ************************************
 * repository management rest interface
 * ************************************
 */
	
	/** 
	 * Returns an explicit collection of all defined repositories in XML format in response to HTTP GET requests.
	 * @return a response containing a feature or an ErrorMessage
	 */
	@GET
	@Path("/features-listrepositories")
	@Produces(MediaType.APPLICATION_XML)
	public Response  getFeaturesListRepositories() throws Exception {

		FeaturesService featuresService = ServiceLoader.getFeaturesService();
		if (featuresService == null) throw new RuntimeException("ServiceLoader.getLicencePublisher() cannot be null.");

		Repository[] repositories = featuresService.listRepositories();
		RepositoryList repositoryList= new RepositoryList();
		for (int i = 0; i < repositories.length; i++) {
			RepositoryWrapperJaxb wrapper = new RepositoryWrapperJaxb(repositories[i].getName(), repositories[i].getURI());
			repositoryList.getRepositoryList().add(wrapper);     
		}
		return Response.status(200).entity(repositoryList).build();
	}
	
	/** 
	 * Returns repository in XML format in response to HTTP GET requests.
	 * name or URI can be used to select the repository
	 * @return a response containing a feature or an ErrorMessage
	 */
	@GET
	@Path("/features-repositoryinfo")
	@Produces(MediaType.APPLICATION_XML)
	public Response  getFeaturesRepositoryInfo(@QueryParam("name") String name, @QueryParam("uri") String uriStr) throws Exception {
		
		FeaturesService featuresService = ServiceLoader.getFeaturesService();
		if (featuresService == null) throw new RuntimeException("ServiceLoader.getLicencePublisher() cannot be null.");

		Repository repository=null;
		RepositoryWrapperJaxb repositoryWrapper=null;
		try{
			// check parameters
			if (name== null && uriStr==null) throw new RuntimeException("you must specify either a ?uri= or ?name= parameter.");
			if (name!=null && uriStr!=null) throw new RuntimeException("you can only specify ONE of either a ?uri= or ?name= parameter.");
			
			URI repoUri=null;
			if(uriStr!=null) repoUri = new URI(uriStr); // will throw exception if uriStr cannot be parsed
			
			//find repository
			Repository repositories[]=featuresService.listRepositories();
			for (int i = 0; i < repositories.length; i++){
				if (name!=null){ //testing name 
					if(repositories[i].getName().equals(name)) repository=repositories[i];
				}else { // or testing uri
					if(repositories[i].getURI().equals(repoUri)) repository=repositories[i];
				}
			}	
			if (repository== null) throw new RuntimeException("repository not found.");
			
			// wrap repository details as Jaxb objects
			FeatureList featuresList = new FeatureList();
			Feature[] features = repository.getFeatures();
			for (int i = 0; i < features.length; i++) {
	            Boolean isInstalled = featuresService.isInstalled(features[i]);
				FeatureWrapperJaxb wrapper = new FeatureWrapperJaxb(features[i].getName(), features[i].getVersion(), features[i].getDescription(), features[i].getDetails(),isInstalled);
				featuresList.getFeatureList().add(wrapper);     
			}
			
			List<URI> repositoriesURI = Arrays.asList(repository.getRepositories());
			
			repositoryWrapper= new RepositoryWrapperJaxb(
					repository.getName(), 
					repository.getURI(), 
					featuresList, 
					repositoriesURI);
			
		} catch (URISyntaxException uriException){
			//return status 400 Error
			return Response.status(400).entity(new ErrorMessage(400, 0, "unable to parse URI for feature uri="+uriStr, null, uriException)).build();
		} catch (Exception exception){
			//return status 400 Error
			return Response.status(400).entity(new ErrorMessage(400, 0, "problem finding repository="+name+ " "+uriStr, null, exception)).build();
		}

		return Response.status(200).entity(repositoryWrapper).build();  
	}
	
	/** 
	 * Removes the specified repository features service..
	 * @param String uri locating the repository
	 * @return a 200 response or an ErrorMessage
	 */
	@GET
	@Path("/features-removerepository")
	@Produces(MediaType.APPLICATION_XML)
	public Response  featuresRemoveRepository(@QueryParam("uri") String uriStr) throws Exception {
		
		FeaturesService featuresService = ServiceLoader.getFeaturesService();
		if (featuresService == null) throw new RuntimeException("ServiceLoader.getLicencePublisher() cannot be null.");

		try{
			if ( uriStr == null) throw new RuntimeException("you must supply ?uri= paramater.");
			URI url= new URI(uriStr);
			
			//find repository
			Repository repositories[]=featuresService.listRepositories();
			Repository repository=null;
			for (int i = 0; i < repositories.length; i++){
				 // or testing uri
					if(repositories[i].getURI().equals(url)) repository=repositories[i];
				}	
			if (repository== null) throw new RuntimeException("repository not found.");
			
			featuresService.removeRepository(url); // remove repository if found
		} catch (URISyntaxException uriException){
			//return status 400 Error
			return Response.status(400).entity(new ErrorMessage(400, 0, "unable to parse URI for feature uri="+uriStr, null, uriException)).build();
		} catch (Exception exception){
			//return status 400 Error
			return Response.status(400).entity(new ErrorMessage(400, 0, "problem removing repository uri="+uriStr, null, exception)).build();
		}
		
		return Response.status(200).entity(new ReplyMessage(200, 0, "Success. Removed repository uri="+uriStr, null,null)).build();

	}
	
	/** 
	 * adds a repository url.
	 * @param String uri locating the repository
	 * @return a 200 response or an ErrorMessage
	 */
	@GET
	@Path("/features-addrepositoryurl")
	@Produces(MediaType.APPLICATION_XML)
	public Response  featuresAddRepository(@QueryParam("uri") String uriStr) throws Exception {
		
		FeaturesService featuresService = ServiceLoader.getFeaturesService();
		if (featuresService == null) throw new RuntimeException("ServiceLoader.getLicencePublisher() cannot be null.");

		try{
			if ( uriStr == null) throw new RuntimeException("you must supply ?uri= paramater.");
			URI url= new URI(uriStr);
			featuresService.validateRepository(url); // will throw exception if not a valid repository
			featuresService.addRepository(url);
		} catch (URISyntaxException uriException){
			//return status 400 Error
			return Response.status(400).entity(new ErrorMessage(400, 0, "unable to parse URI for feature uri="+uriStr, null, uriException)).build();
		} catch (Exception exception){
			//return status 400 Error
			return Response.status(400).entity(new ErrorMessage(400, 0, "problem adding repository uri="+uriStr, null, exception)).build();
		}
		
		return Response.status(200).entity(new ReplyMessage(200, 0, "Success. Added repository uri="+uriStr, null,null)).build();
	}
	

} 