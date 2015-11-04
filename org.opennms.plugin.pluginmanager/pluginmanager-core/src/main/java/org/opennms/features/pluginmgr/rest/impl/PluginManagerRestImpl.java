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

package org.opennms.features.pluginmgr.rest.impl;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.opennms.features.pluginmgr.PluginManager;
import org.opennms.features.pluginmgr.model.RemoteKarafState;
import org.opennms.features.pluginmgr.rest.PluginManagerRest;
import org.opennms.features.pluginmgr.rest.impl.ServiceLoader;
import org.opennms.karaf.featuremgr.jaxb.ErrorMessage;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceList;
import org.opennms.karaf.licencemgr.metadata.jaxb.ProductSpecList;

/**
 * REST service to access plugin manager
 */
@Path("/")
public class PluginManagerRestImpl implements PluginManagerRest {


	/**
	 * Returns manifest list in XML format in response to HTTP GET request.
	 * e.g. http://localhost:8980/opennms/pluginmgr/rest/manifest-list?systemId=
	 * @param systemId the system id for which to get the manifest
	 * @return response containing manifest list or an error message if not found
	 * @throws Exception 
	 */
	@GET
	@Path("/manifest-list")
	@Produces(MediaType.APPLICATION_XML)
	@Override
	public Response  getManifestList(@QueryParam("systemId") String systemId) throws Exception {
		
		PluginManager pluginManager = ServiceLoader.getPluginManager();
		if (pluginManager == null) throw new RuntimeException("ServiceLoader.getPluginManager() cannot be null.");

		try{
			if(systemId==null || "".equals(systemId)){
				throw new RuntimeException("systemId cannot be null or empty string");
			}
			
			// find the karaf instance with a matching systemId
			String karafInstance=null;
			Set<String> karafInstances = pluginManager.getKarafInstances().keySet();
			for (String karafIns : karafInstances){
				String sysId = pluginManager.getManifestSystemId(karafIns);
				if(systemId.equals(sysId)){
					karafInstance=karafIns;
					break;
				}
			}
			if (karafInstance==null) throw new RuntimeException("cannot find karaf instance manifest for systemId="+systemId);
			
			// find the plugins manifest for the karafInstance
			ProductSpecList productSpecList = pluginManager.getPluginsManifest(karafInstance);
			
            return Response.status(200).entity(productSpecList).build();
			
		} catch (Exception exception){
			//return status 400 Error
			return Response.status(400).entity(new ErrorMessage(400, 0, "Unable to get plugin manifest list", null, exception)).build();
		}

	}
	
	/**
	 * Updates the karaf state known to the plugin manager using a RemoteKarafState xml message
	 * 
	 * http://localhost:8980/opennms/pluginmgr/rest/updateremotekarafstate
	 * 
	 * @param remoteKarafState contains the following xml elements
	 *    String systemId - the systemId of the remote karaf instance updating its state 
	 *    installedPlugins - a list of product specs of installed plugins including licenceAuthenticated state
	 *    (if null it should be ignored - i.e. a null entry indicates do not change the list in the plugin manager)
	 *    installedLicenceList - list of licences installed in remote karaf. If null, it should be ignored 
	 *    (if null it should be ignored - i.e. a null entry indicates do not change the installedLicenceList in the plugin manager)
	 * 
	 * @return success or error message
	 */
	@POST
	@Path("/updateremotekarafstate")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Response updateRemoteKaraState(RemoteKarafState remoteKarafState) throws Exception {
		
		PluginManager pluginManager = ServiceLoader.getPluginManager();
		if (pluginManager == null) throw new RuntimeException("ServiceLoader.getPluginManager() cannot be null.");
		try{
			if(remoteKarafState==null){
				throw new RuntimeException("remoteKarafState cannot be empty string");
			}
			String systemId=remoteKarafState.getSystemId();
			if(systemId==null || "".equals(systemId)){
				throw new RuntimeException("remoteKarafState systemId cannot be null or empty string");
			}
			
			// find the karaf instance with a matching systemId
			String karafInstance=null;
			Set<String> karafInstances = pluginManager.getKarafInstances().keySet();
			for (String karafIns : karafInstances){
				String sysId = pluginManager.getManifestSystemId(karafIns);
				if(systemId.equals(sysId)){
					karafInstance=karafIns;
					break;
				}
			}
			if (karafInstance==null) throw new RuntimeException("cannot find karaf instance for systemId="+systemId);

			LicenceList installedLicenceList= remoteKarafState.getInstalledLicenceList();
			if (installedLicenceList!=null ) pluginManager.updateInstalledLicenceList(installedLicenceList, karafInstance);
			
			ProductSpecList installedPlugins = remoteKarafState.getInstalledPlugins();
			if (installedPlugins!=null ) pluginManager.updateInstalledPlugins(installedPlugins, karafInstance);
			
            return Response.status(200).build();
			
		} catch (Exception exception){
			//return status 400 Error
			return Response.status(400).entity(new ErrorMessage(400, 0, "Unable to update remote karaf state", null, exception)).build();
		}


	}

}
