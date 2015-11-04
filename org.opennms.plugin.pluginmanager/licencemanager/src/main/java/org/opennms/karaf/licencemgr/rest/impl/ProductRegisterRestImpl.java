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

package org.opennms.karaf.licencemgr.rest.impl;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.opennms.karaf.licencemgr.metadata.jaxb.ErrorMessage;
import org.opennms.karaf.licencemgr.metadata.jaxb.ProductMetadata;
import org.opennms.karaf.licencemgr.metadata.jaxb.ProductSpecList;
import org.opennms.karaf.licencemgr.metadata.jaxb.ReplyMessage;
import org.opennms.karaf.licencemgr.rest.ProductRegisterRest;
import org.opennms.karaf.productpub.ProductRegister;

@Path("/product-reg")
public class ProductRegisterRestImpl implements ProductRegisterRest {

	@POST
	@Path("/addproductspec")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	@Override
	public Response addProductDescription(ProductMetadata productMetadata){

		ProductRegister productRegister= ServiceLoader.getProductRegister();
		if (productRegister == null) throw new RuntimeException("ServiceLoader.getProductRegister cannot be null.");

		try{
			if (productMetadata == null) throw new RuntimeException("productMetadata cannot be null.");
			productRegister.addProductDescription(productMetadata);
		} catch (Exception exception){
			//return status 400 Error
			return Response.status(400).entity(new ErrorMessage(400, 0, "Unable to add product description", null, exception)).build();
		}

		ReplyMessage reply= new ReplyMessage();
        reply.setReplyComment("Product Description added for productId="+productMetadata.getProductId());
        reply.setProductId(productMetadata.getProductId());
		
		return Response
				.status(200).entity(reply).build();

	}



	@GET
	@Path("/removeproductspec")
	@Produces(MediaType.APPLICATION_XML)
	@Override
	public Response removeProductDescription(@QueryParam("productId") String productId){

		ProductRegister productRegister= ServiceLoader.getProductRegister();
		if (productRegister == null) throw new RuntimeException("ServiceLoader.getProductRegister cannot be null.");

		Boolean removed=null;
		try{
			if (productId == null) throw new RuntimeException("productId cannot be null.");
			removed = productRegister.removeProductDescription(productId);
			String devMessage=null;
			if (!removed) return Response.status(400).entity(new ErrorMessage(400, 0, "Product Description not found to remove for productId="+productId, null, devMessage)).build();
		} catch (Exception exception){
			//return status 400 Error
			return Response.status(400).entity(new ErrorMessage(400, 0, "Unable to remove product description", null, exception)).build();
		}

		ReplyMessage reply= new ReplyMessage();
		reply.setReplyComment("Product Description removed for productId="+productId);
		
		return Response.status(200).entity(reply).build();

	}


	@GET
	@Path("/getproductspec")
	@Produces(MediaType.APPLICATION_XML)
	@Override
	public Response getProductDescription(@QueryParam("productId") String productId){

		ProductRegister productRegister= ServiceLoader.getProductRegister();
		if (productRegister == null) throw new RuntimeException("ServiceLoader.getProductRegister cannot be null.");

		ProductMetadata productDescription=null;
		try{
			if (productId == null) throw new RuntimeException("productId cannot be null.");
			productDescription = productRegister.getProductDescription(productId);
			String devMessage=null;
			if (productDescription==null) return Response.status(400).entity(new ErrorMessage(400, 0, "productDescription not found for productId="+productId, null, devMessage)).build();
		} catch (Exception exception){
			//return status 400 Error
			return Response.status(400).entity(new ErrorMessage(400, 0, "Unable to get product description", null, exception)).build();
		}

		ReplyMessage reply= new ReplyMessage();
		reply.setReplyComment("Product Description found for productId="+productId);
		reply.setProductMetadata(productDescription);
		
		return Response.status(200).entity(reply).build();

	}


	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_XML)
	@Override
	public Response getProductDescriptionList(){

		ProductRegister productRegister= ServiceLoader.getProductRegister();
		if (productRegister == null) throw new RuntimeException("ServiceLoader.getProductRegister cannot be null.");

		Map<String, ProductMetadata> productDescrMap=null;
		try{
			productDescrMap = productRegister.getProductDescriptionMap();
		} catch (Exception exception){
			//return status 400 Error
			return Response.status(400).entity(new ErrorMessage(400, 0, "Unable to get product description map", null, exception)).build();
		}

		ProductSpecList productSpecList= new ProductSpecList();
		productSpecList.getProductSpecList().addAll(productDescrMap.values());

		return Response
				.status(200).entity(productSpecList).build();

	}

	@GET
	@Path("/clearproductspecs")
	@Produces(MediaType.APPLICATION_XML)
	@Override
	public Response deleteProductDescriptions(@QueryParam("confirm") String confirm){

		ProductRegister productRegister= ServiceLoader.getProductRegister();
		if (productRegister == null) throw new RuntimeException("ServiceLoader.getProductRegister cannot be null.");

		try{
			if (!"true".equals(confirm)) throw new IllegalArgumentException("Will only delete specs if paramater confirm=true");
			productRegister.deleteProductDescriptions();
		} catch (Exception exception){
			//return status 400 Error
			return Response.status(400).entity(new ErrorMessage(400, 0, "Unable to delete product descriptions", null, exception)).build();
		}

		ReplyMessage reply= new ReplyMessage();
        reply.setReplyComment("All Product Specifications removed");
		
		return Response
				.status(200).entity(reply).build();

	}

}