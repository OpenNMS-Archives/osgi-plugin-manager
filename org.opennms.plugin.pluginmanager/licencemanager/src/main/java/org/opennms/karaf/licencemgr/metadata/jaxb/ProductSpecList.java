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

package org.opennms.karaf.licencemgr.metadata.jaxb;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement(name="productSpecifications")
@XmlAccessorType(XmlAccessType.NONE)
public class ProductSpecList  {

	
	@XmlElementWrapper(name="productSpecList")
	@XmlElement(name="productMetadata")
	private List<ProductMetadata> productSpecList = new ArrayList<ProductMetadata>();
	
	@XmlElement(name="productListSource")
	private String productListSource=null;

	/**
	 * @return the productSpecList
	 */
	public List<ProductMetadata> getProductSpecList() {
		return productSpecList;
	}

	/**
	 * @param productSpecList the productSpecList to set
	 */
	public void setProductSpecList(List<ProductMetadata> productSpecList) {
		this.productSpecList = productSpecList;
	}
	
	public String getProductListSource() {
		return productListSource;
	}

	public void setProductListSource(String productListSource) {
		this.productListSource = productListSource;
	}
	
	//NOTE IF YOU MODIFY THIS CLASS YOU MUST change the fromXml() method
	/**
	 * load this object with data from xml string
	 * @parm XML encoded version of ProductSpecList
	 */
	public void fromXml(String xmlStr){

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(org.opennms.karaf.licencemgr.metadata.jaxb.ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			StringReader reader = new StringReader(xmlStr);
			ProductSpecList prodSpecList= (ProductSpecList) jaxbUnmarshaller.unmarshal(reader);
			this.productSpecList=prodSpecList.getProductSpecList();
			this.productListSource=prodSpecList.getProductListSource();

		} catch (JAXBException e) {
			throw new RuntimeException("Problem unmarshalling ProductSpecList:",e);
		}
	}
	
	/**
	 * @return XML encoded version of ProductSpecList
	 */
	public String toXml(){
		return toXml(false);
	}
	
	/**
	 * @param prettyPrint if true returns formatted xml output
	 * @return XML encoded version of ProductSpecList
	 */
	public String toXml(boolean prettyPrint){
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(org.opennms.karaf.licencemgr.metadata.jaxb.ObjectFactory.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, prettyPrint);
			StringWriter stringWriter = new StringWriter();
			jaxbMarshaller.marshal(this,stringWriter);
			return stringWriter.toString();
		} catch (JAXBException e) {
			throw new RuntimeException("Problem marshalling ProductSpecList:",e);
		}
	}




}