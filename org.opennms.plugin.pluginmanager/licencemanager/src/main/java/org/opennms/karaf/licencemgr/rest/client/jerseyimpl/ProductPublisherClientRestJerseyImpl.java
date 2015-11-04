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

package org.opennms.karaf.licencemgr.rest.client.jerseyimpl;

import javax.ws.rs.core.MediaType;

import org.opennms.karaf.licencemgr.metadata.jaxb.ErrorMessage;
import org.opennms.karaf.licencemgr.metadata.jaxb.ReplyMessage;
import org.opennms.karaf.licencemgr.metadata.jaxb.Util;
import org.opennms.karaf.licencemgr.metadata.jaxb.ProductMetadata;
import org.opennms.karaf.licencemgr.metadata.jaxb.ProductSpecList;
import org.opennms.karaf.licencemgr.rest.client.ProductPublisherClient;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class ProductPublisherClientRestJerseyImpl implements ProductPublisherClient {
	
	private String baseUrl = "http://localhost:8181";
	private String basePath = "/licencemgr/rest/product-pub";
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
	 * e.g http://localhost:8181/licencemgr/rest/product-pub
	 * @return basePath
	 */
	public String getBasePath() {
		return basePath;
	}

	/**
	 * base path of service starting with '/' such that service is accessed using baseUrl/basePath... 
	 * e.g http://localhost:8181/licencemgr/rest/product-pub
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
	public void addProductSpec(ProductMetadata productMetadata) throws Exception {
		if(baseUrl==null || basePath==null) throw new RuntimeException("basePath and baseUrl must both be set");
		if(productMetadata==null ) throw new RuntimeException("productMetadata must be set");
	    
		Client client = newClient();
		
		//http://localhost:8181/licencemgr/rest/product-pub/addproductspec
		
		String getStr= baseUrl+basePath+"/addproductspec";
		
		WebResource r = client.resource(getStr);
		
		// POST method
		ClientResponse response = r.accept(MediaType.APPLICATION_XML)
                .type(MediaType.APPLICATION_XML).post(ClientResponse.class, productMetadata);

        // check response status code and reply error message
        if (response.getStatus() != 200) {
        	ErrorMessage errorMessage=null;
        	try {
        		errorMessage = response.getEntity(ErrorMessage.class);
        	} catch (Exception e) {
        	}
        	String errMsg= "Failed : HTTP error code : "+ response.getStatus();
        	if (errorMessage!=null){
        		errMsg=errMsg+" message:"+ errorMessage.getMessage()
					+" code:"+ errorMessage.getCode()
					+" developer message:"+errorMessage.getDeveloperMessage();
        	}
            throw new RuntimeException(errMsg);
        }
		
        // success !!!
		
	}

	@Override
	public void removeProductSpec(String productId) throws Exception {
		if(baseUrl==null || basePath==null) throw new RuntimeException("basePath and baseUrl must both be set");
		if(productId==null ) throw new RuntimeException("productId must be set");
	    
		Client client = newClient();
		
		//http://localhost:8181/licencemgr/rest/product-pub/removeproductspec?productId=
		
		String getStr= baseUrl+basePath+"/removeproductspec?productId="+productId;
		
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
        	String errMsg= "removeProductSpec Failed : HTTP error code : "+ response.getStatus();
        	if (errorMessage!=null){
        		errMsg=errMsg+" message:"+ errorMessage.getMessage()
					+" code:"+ errorMessage.getCode()
					+" developer message:"+errorMessage.getDeveloperMessage();
        	}
            throw new RuntimeException(errMsg);
        }
		
        // success !!!

		
	}

	@Override
	public ProductMetadata getProductSpec(String productId) throws Exception {
		if(baseUrl==null || basePath==null) throw new RuntimeException("basePath and baseUrl must both be set");
		if(productId==null ) throw new RuntimeException("productId must be set");
	    
		Client client = newClient();
		
		//http://localhost:8181/licencemgr/rest/product-pub/getproductspec?productId=
		
		String getStr= baseUrl+basePath+"/getproductspec?productId="+productId;
		
		WebResource r = client.resource(getStr);

		ReplyMessage replyMessage= r
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.accept(MediaType.APPLICATION_XML).get(ReplyMessage.class);
		
		if(replyMessage.getProductMetadata()==null) {
			throw new RuntimeException("unable to get product metadata for productId="+productId
					+ " replyMessage.getReplyComment()="+replyMessage.getReplyComment());
		}
		else return replyMessage.getProductMetadata(); // success !!!
		
	}

	@Override
	public ProductSpecList getList() throws Exception {
		if(baseUrl==null || basePath==null) throw new RuntimeException("basePath and baseUrl must both be set");

		Client client = newClient();
		
		//http://localhost:8181/licencemgr/rest/product-pub/list
		
		String getStr= baseUrl+basePath+"/list";
		
		WebResource r = client.resource(getStr);

		String replyString= r
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.accept(MediaType.APPLICATION_XML).get(String.class);
		
		// unmarshalling reply
		ProductSpecList productSpecList=null;
		Object replyObject = Util.fromXml(replyString);
		if (replyObject instanceof ProductSpecList){
			productSpecList= (ProductSpecList) replyObject;
		} else {
			throw new RuntimeException("received unexpected reply object: "+replyObject.getClass().getCanonicalName());
		}
		
		// success !!!
		return productSpecList;

	}

	@Override
	public void clearProductSpecs(Boolean confirm) throws Exception {
		if(baseUrl==null || basePath==null) throw new RuntimeException("basePath and baseUrl must both be set");
		if(confirm==null) throw new RuntimeException("confirm must be set true of false");

		Client client = newClient();
		
		//http://localhost:8181/licencemgr/rest/product-pub/clearproductspecs?confirm=false
		
		String getStr= baseUrl+basePath+"/clearproductspecs?confirm="+ (confirm ? "true":"false");
		
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
        	String errMsg= "clearProductSpecs Failed : HTTP error code : "+ response.getStatus();
        	if (errorMessage!=null){
        		errMsg=errMsg+" message:"+ errorMessage.getMessage()
					+" code:"+ errorMessage.getCode()
					+" developer message:"+errorMessage.getDeveloperMessage();
        	}
            throw new RuntimeException(errMsg);
        }
		
        // success !!!

	}

}
