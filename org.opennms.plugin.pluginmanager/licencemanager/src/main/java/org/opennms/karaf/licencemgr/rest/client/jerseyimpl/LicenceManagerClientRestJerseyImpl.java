package org.opennms.karaf.licencemgr.rest.client.jerseyimpl;
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

import javax.ws.rs.core.MediaType;

import org.opennms.karaf.licencemgr.metadata.jaxb.ErrorMessage;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceList;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadata;
import org.opennms.karaf.licencemgr.metadata.jaxb.ReplyMessage;
import org.opennms.karaf.licencemgr.rest.client.LicenceManagerClient;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class LicenceManagerClientRestJerseyImpl implements LicenceManagerClient {
	
	private String baseUrl = "http://localhost:8181";
	private String basePath = "/licencemgr/rest/licence-mgr";
	private String userName = null; // If userName is null no basic authentication is generated
	private String password = "";
	
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * User name to use in basic authentication
	 * If userName is null then no basic authentication is generated
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
     * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * password to use in basic authentication.
	 * password must not be set to null but if not set, password will default to empty string "".
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		if (password==null) throw new RuntimeException("password must not be set to null");
		this.password = password;
	}
	
	/**
	 * base URL of service as http://HOSTNAME:PORT e.g http://localhost:8181
	 * @return baseUrl
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * base URL of service as http://HOSTNAME:PORT/ e.g http://localhost:8181
	 * @param baseUrl
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	/**
	 * base path of service starting with '/' such that service is accessed using baseUrl/basePath... 
	 * e.g http://localhost:8181/featuremgr
	 * @return basePath
	 */
	public String getBasePath() {
		return basePath;
	}

	/**
	 * base path of service starting with '/' such that service is accessed using baseUrl/basePath... 
	 * e.g http://localhost:8181/featuremgr
	 * @return basePath
	 */
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	
	private Client newClient(){
		Client client = Client.create();
		if (userName!=null) client.addFilter(new HTTPBasicAuthFilter(userName, password));
		return  client;
	}

	@Override
	public LicenceMetadata addLicence(String licence) throws Exception {
		if(baseUrl==null || basePath==null) throw new RuntimeException("basePath and baseUrl must both be set");
		if(licence==null ) throw new RuntimeException("licence must be set");
	    
		Client client = newClient();
		
		//http://localhost:8181/licencemgr/rest/licence-mgr/addlicence?licence=
		
		String getStr= baseUrl+basePath+"/addlicence?licence="+licence;
		
		WebResource r = client.resource(getStr);

		// GET method
		ClientResponse response = r.accept(MediaType.APPLICATION_XML)
                .type(MediaType.APPLICATION_FORM_URLENCODED).get(ClientResponse.class);

        // check response status code and reply error message
        if (response.getStatus() != 200) {
        	ErrorMessage errorMessage=null;
        	try {
        		errorMessage = response.getEntity(ErrorMessage.class);
        	} catch (Exception e) {
        	}
        	String errMsg= "getlicencespec Failed : HTTP error code : "+ response.getStatus();
        	if (errorMessage!=null){
        		errMsg=errMsg+" message:"+ errorMessage.getMessage()
					+" code:"+ errorMessage.getCode()
					+" developer message:"+errorMessage.getDeveloperMessage();
        	}
            throw new RuntimeException(errMsg);
        }
        //success !
        ReplyMessage replyMessage = response.getEntity(ReplyMessage.class);
        
        LicenceMetadata  licenceMetadata =  replyMessage.getLicenceMetadata();

        return  licenceMetadata;
	}

	@Override
	public void removeLicence(String productId) throws Exception {
		if(baseUrl==null || basePath==null) throw new RuntimeException("basePath and baseUrl must both be set");
		if(productId==null ) throw new RuntimeException("productId must be set");

		Client client = newClient();
		
		//http://localhost:8181/licencemgr/rest/licence-mgr/removelicence?productId=
		
		String getStr= baseUrl+basePath+"/removelicence?productId="+productId;
		
		WebResource r = client.resource(getStr);

		// GET method
		ClientResponse response = r.accept(MediaType.APPLICATION_XML)
                .type(MediaType.APPLICATION_FORM_URLENCODED).get(ClientResponse.class);

        // check response status code and reply error message
        if (response.getStatus() != 200) {
        	ErrorMessage errorMessage=null;
        	try {
        		errorMessage = response.getEntity(ErrorMessage.class);
        	} catch (Exception e) {
        	}
        	String errMsg= "removeLicence Failed : HTTP error code : "+ response.getStatus();
        	if (errorMessage!=null){
        		errMsg=errMsg+" message:"+ errorMessage.getMessage()
					+" code:"+ errorMessage.getCode()
					+" developer message:"+errorMessage.getDeveloperMessage();
        	}
            throw new RuntimeException(errMsg);
        }
        // success !
	}

	@Override
	public String getLicence(String productId) throws Exception {
		if(baseUrl==null || basePath==null) throw new RuntimeException("basePath and baseUrl must both be set");
		if(productId==null ) throw new RuntimeException("productId must be set");
	    
		Client client = newClient();
		
		//http://localhost:8181/licencemgr/rest/licence-mgr/getlicence?productId=
		
		String getStr= baseUrl+basePath+"/getlicence?productId="+productId;
		
		WebResource r = client.resource(getStr);

		// GET method
		ClientResponse response = r.accept(MediaType.APPLICATION_XML)
                .type(MediaType.APPLICATION_FORM_URLENCODED).get(ClientResponse.class);

        // check response status code and reply error message
        if (response.getStatus() != 200) {
        	ErrorMessage errorMessage=null;
        	try {
        		errorMessage = response.getEntity(ErrorMessage.class);
        	} catch (Exception e) {
        	}
        	String errMsg= "getLicence Failed : HTTP error code : "+ response.getStatus();
        	if (errorMessage!=null){
        		errMsg=errMsg+" message:"+ errorMessage.getMessage()
					+" code:"+ errorMessage.getCode()
					+" developer message:"+errorMessage.getDeveloperMessage();
        	}
            throw new RuntimeException(errMsg);
        }
		
        ReplyMessage replyMessage = response.getEntity(ReplyMessage.class);
        
        String  licence =  replyMessage.getLicence();

        return licence;
	}
	
	
	@Override
	public Boolean isAuthenticated(String productId) throws Exception {
		if(baseUrl==null || basePath==null) throw new RuntimeException("basePath and baseUrl must both be set");
		if(productId==null ) throw new RuntimeException("productId must be set");
	    
		Client client = newClient();
		
		//http://localhost:8181/licencemgr/rest/licence-mgr/isauthenticated?productId=
		
		String getStr= baseUrl+basePath+"/isauthenticated?productId="+productId;
		
		WebResource r = client.resource(getStr);

		// GET method
		ClientResponse response = r.accept(MediaType.APPLICATION_XML)
                .type(MediaType.APPLICATION_FORM_URLENCODED).get(ClientResponse.class);

        // check response status code and reply error message
        if (response.getStatus() != 200) {
        	ErrorMessage errorMessage=null;
        	try {
        		errorMessage = response.getEntity(ErrorMessage.class);
        	} catch (Exception e) {
        	}
        	String errMsg= "getLicence Failed : HTTP error code : "+ response.getStatus();
        	if (errorMessage!=null){
        		errMsg=errMsg+" message:"+ errorMessage.getMessage()
					+" code:"+ errorMessage.getCode()
					+" developer message:"+errorMessage.getDeveloperMessage();
        	}
            throw new RuntimeException(errMsg);
        }
		
        ReplyMessage replyMessage = response.getEntity(ReplyMessage.class);
        
        Boolean  isAuthenticated =  replyMessage.getIsAuthenticated();

        return isAuthenticated;
	}
	

	@Override
	public LicenceList getLicenceMap() throws Exception {
		if(baseUrl==null || basePath==null) throw new RuntimeException("basePath and baseUrl must both be set");
	    
		Client client = newClient();
		
		//http://localhost:8181/licencemgr/rest/licence-mgr/list
		
		String getStr= baseUrl+basePath+"/list";
		
		WebResource r = client.resource(getStr);

		// GET method
		ClientResponse response = r.accept(MediaType.APPLICATION_XML)
                .type(MediaType.APPLICATION_FORM_URLENCODED).get(ClientResponse.class);

        // check response status code and reply error message
        if (response.getStatus() != 200) {
        	ErrorMessage errorMessage=null;
        	try {
        		errorMessage = response.getEntity(ErrorMessage.class);
        	} catch (Exception e) {
        	}
        	String errMsg= "getLicenceMap Failed : HTTP error code : "+ response.getStatus();
        	if (errorMessage!=null){
        		errMsg=errMsg+" message:"+ errorMessage.getMessage()
					+" code:"+ errorMessage.getCode()
					+" developer message:"+errorMessage.getDeveloperMessage();
        	}
            throw new RuntimeException(errMsg);
        }
		// success !
        LicenceList licenceListResponse = response.getEntity(LicenceList.class);

        return licenceListResponse;
	}
	
	@Override
	public LicenceList getLicenceMapForSystemId(String systemId) throws Exception {
		if(baseUrl==null || basePath==null) throw new RuntimeException("basePath and baseUrl must both be set");
		if(systemId==null ) throw new RuntimeException("systemId must not be null");
	    
		Client client = newClient();
		
		//http://localhost:8980/opennms/licencemgr/rest/licence-mgr/listforsystemid?systemId=32e396e36b28ef5d-a48ef1cb
		
		String getStr= baseUrl+basePath+"/listforsystemid?systemId="+systemId;
		
		WebResource r = client.resource(getStr);

		// GET method
		ClientResponse response = r.accept(MediaType.APPLICATION_XML)
                .type(MediaType.APPLICATION_FORM_URLENCODED).get(ClientResponse.class);

        // check response status code and reply error message
        if (response.getStatus() != 200) {
        	ErrorMessage errorMessage=null;
        	try {
        		errorMessage = response.getEntity(ErrorMessage.class);
        	} catch (Exception e) {
        	}
        	String errMsg= "getLicenceMapForSystemId Failed : HTTP error code : "+ response.getStatus();
        	if (errorMessage!=null){
        		errMsg=errMsg+" message:"+ errorMessage.getMessage()
					+" code:"+ errorMessage.getCode()
					+" developer message:"+errorMessage.getDeveloperMessage();
        	}
            throw new RuntimeException(errMsg);
        }
		// success !
        LicenceList licenceListResponse = response.getEntity(LicenceList.class);

        return licenceListResponse;
	}


	@Override
	public void deleteLicences(Boolean confirm) throws Exception {
		if(baseUrl==null || basePath==null) throw new RuntimeException("basePath and baseUrl must both be set");
		if(confirm==null) throw new RuntimeException("confirm must be set true of false");

		Client client = newClient();

		//http://localhost:8181/licencemgr/rest/licence-mgr/clearlicences?confirm=false
		
		String getStr= baseUrl+basePath+"/clearlicences?confirm="+ (confirm ? "true":"false");
		
		WebResource r = client.resource(getStr);

		// GET method
		ClientResponse response = r.accept(MediaType.APPLICATION_XML)
                .type(MediaType.APPLICATION_FORM_URLENCODED).get(ClientResponse.class);

        // check response status code and reply error message
        if (response.getStatus() != 200) {
        	ErrorMessage errorMessage=null;
        	try {
        		errorMessage = response.getEntity(ErrorMessage.class);
        	} catch (Exception e) {
        	}
        	String errMsg= "removeLicence Failed : HTTP error code : "+ response.getStatus();
        	if (errorMessage!=null){
        		errMsg=errMsg+" message:"+ errorMessage.getMessage()
					+" code:"+ errorMessage.getCode()
					+" developer message:"+errorMessage.getDeveloperMessage();
        	}
            throw new RuntimeException(errMsg);
        }
        // success !
	}

	@Override
	public String getSystemId() throws Exception {
		if(baseUrl==null || basePath==null) throw new RuntimeException("basePath and baseUrl must both be set");
	    
		Client client = newClient();
		
		//http://localhost:8181/licencemgr/rest/licence-mgr/getsystemid
		
		String getStr= baseUrl+basePath+"/getsystemid";
		
		WebResource r = client.resource(getStr);

		// GET method
		ClientResponse response = r.accept(MediaType.APPLICATION_XML)
                .type(MediaType.APPLICATION_FORM_URLENCODED).get(ClientResponse.class);

        // check response status code and reply error message
        if (response.getStatus() != 200) {
        	ErrorMessage errorMessage=null;
        	try {
        		errorMessage = response.getEntity(ErrorMessage.class);
        	} catch (Exception e) {
        	}
        	String errMsg= "getLicence Failed : HTTP error code : "+ response.getStatus();
        	if (errorMessage!=null){
        		errMsg=errMsg+" message:"+ errorMessage.getMessage()
					+" code:"+ errorMessage.getCode()
					+" developer message:"+errorMessage.getDeveloperMessage();
        	}
            throw new RuntimeException(errMsg);
        }
		
        ReplyMessage replyMessage = response.getEntity(ReplyMessage.class);
        
        String  systemid = replyMessage.getSystemId();

        return systemid;
	}

	@Override
	public void setSystemId(String systemId) throws Exception {
		if(baseUrl==null || basePath==null) throw new RuntimeException("basePath and baseUrl must both be set");
		if(systemId==null ) throw new RuntimeException("systemId must be set");
	    
		Client client = newClient();
		
		//http://localhost:8181/licencemgr/rest/licence-mgr/setsystemid?systemId=
		
		String getStr= baseUrl+basePath+"/setsystemid?systemId="+systemId;
		
		WebResource r = client.resource(getStr);

		// GET method
		ClientResponse response = r.accept(MediaType.APPLICATION_XML)
                .type(MediaType.APPLICATION_FORM_URLENCODED).get(ClientResponse.class);

        // check response status code and reply error message
        if (response.getStatus() != 200) {
        	ErrorMessage errorMessage=null;
        	try {
        		errorMessage = response.getEntity(ErrorMessage.class);
        	} catch (Exception e) {
        	}
        	String errMsg= "setSystemId Failed : HTTP error code : "+ response.getStatus();
        	if (errorMessage!=null){
        		errMsg=errMsg+" message:"+ errorMessage.getMessage()
					+" code:"+ errorMessage.getCode()
					+" developer message:"+errorMessage.getDeveloperMessage();
        	}
            throw new RuntimeException(errMsg);
        }
        // success !
	}

	@Override
	public String makeSystemInstance() throws Exception {
		if(baseUrl==null || basePath==null) throw new RuntimeException("basePath and baseUrl must both be set");
	    
		Client client = newClient();
		
		//http://localhost:8181/licencemgr/rest/licence-mgr/makesystemid
		
		String getStr= baseUrl+basePath+"/makesystemid";
		
		WebResource r = client.resource(getStr);

		// GET method
		ClientResponse response = r.accept(MediaType.APPLICATION_XML)
                .type(MediaType.APPLICATION_FORM_URLENCODED).get(ClientResponse.class);

        // check response status code and reply error message
        if (response.getStatus() != 200) {
        	ErrorMessage errorMessage=null;
        	try {
        		errorMessage = response.getEntity(ErrorMessage.class);
        	} catch (Exception e) {
        	}
        	String errMsg= "makeSystemInstance Failed : HTTP error code : "+ response.getStatus();
        	if (errorMessage!=null){
        		errMsg=errMsg+" message:"+ errorMessage.getMessage()
					+" code:"+ errorMessage.getCode()
					+" developer message:"+errorMessage.getDeveloperMessage();
        	}
            throw new RuntimeException(errMsg);
        }
		
        ReplyMessage replyMessage = response.getEntity(ReplyMessage.class);
        
        String  systemid = replyMessage.getSystemId();

        return systemid;
	}

	@Override
	public String checksumForString(String string) throws Exception {
		if(baseUrl==null || basePath==null) throw new RuntimeException("basePath and baseUrl must both be set");
		if(string==null ) throw new RuntimeException("string must be set");
	    
		Client client = newClient();
		
		//http://localhost:8181/licencemgr/rest/licence-mgr/checksumforstring?string=
		
		String getStr= baseUrl+basePath+"/checksumforstring?string="+string;
		
		WebResource r = client.resource(getStr);

		// GET method
		ClientResponse response = r.accept(MediaType.APPLICATION_XML)
                .type(MediaType.APPLICATION_FORM_URLENCODED).get(ClientResponse.class);

        // check response status code and reply error message
        if (response.getStatus() != 200) {
        	ErrorMessage errorMessage=null;
        	try {
        		errorMessage = response.getEntity(ErrorMessage.class);
        	} catch (Exception e) {
        	}
        	String errMsg= "checksumForString Failed : HTTP error code : "+ response.getStatus();
        	if (errorMessage!=null){
        		errMsg=errMsg+" message:"+ errorMessage.getMessage()
					+" code:"+ errorMessage.getCode()
					+" developer message:"+errorMessage.getDeveloperMessage();
        	}
            throw new RuntimeException(errMsg);
        }
		//success !
        ReplyMessage replyMessage = response.getEntity(ReplyMessage.class);
        
        String  checksum =  replyMessage.getChecksum();

        return checksum;
	}


}
