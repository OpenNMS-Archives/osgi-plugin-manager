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

package org.opennms.features.pluginmgr.rest;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.opennms.features.pluginmgr.model.RemoteKarafState;



/**
 * REST service to access plugin manager
 */
@Path("/")
public interface PluginManagerRest {
	

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
	public Response  getManifestList(@QueryParam("systemId") String systemId) throws Exception ;


	/**
	 * Updates the karaf state known to the plugin manager using a RemoteKarafState xml message
	 * 
	 * http://localhost:8980/opennms/pluginmgr/rest/updateRemoteKaraState
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
	public Response updateRemoteKaraState(RemoteKarafState remoteKarafState) throws Exception ;

}
