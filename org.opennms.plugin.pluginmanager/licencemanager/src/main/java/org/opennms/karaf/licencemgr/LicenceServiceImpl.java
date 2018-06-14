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

package org.opennms.karaf.licencemgr;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;

import org.opennms.karaf.licencemgr.metadata.Licence;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceEntry;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceList;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name="LicenceServiceData")
@XmlAccessorType(XmlAccessType.NONE)
public class LicenceServiceImpl implements LicenceService {
	private static final Logger LOG = LoggerFactory.getLogger(LicenceServiceImpl.class);

	// used to store location of persistence file
	private String fileUri=null;

	// used to store list of productId's with authenticated licences
	// this is not persisted but rebuilt as products are installed and authenticated
	private Set<String> authenticatedLicences= new HashSet<String>();

	@XmlElementWrapper(name="licenceMap")
	private SortedMap<String, String> licenceMap = new TreeMap<String, String>();

	@XmlElement
	private String systemId="NOT_SET";

	/**
	 * @param fileUri used to store location of persistence file for licence service
	 */
	public void setFileUri(String fileUri) {
		this.fileUri = fileUri;
	}

	@Override
	public synchronized void addAuthenticatedProductId(String productId){
		if (productId==null) throw new RuntimeException("productId cannot be null");
		if (! licenceMap.containsKey(productId)) throw new RuntimeException("there is no licence installed for productId="+productId);
		authenticatedLicences.add(productId);
	}

	@Override
	public synchronized void removeAuthenticatedProductId(String productId){
		if (productId==null) throw new RuntimeException("productId cannot be null");
		authenticatedLicences.remove(productId);
	}

	@Override
	public synchronized boolean isAuthenticatedProductId(String productId){
		if (productId==null) throw new RuntimeException("productId cannot be null");
		return authenticatedLicences.contains(productId);
	}

	/**
	 * used to add single licence locally without calling persist
	 * @param licenceStrPlusCrc
	 * @return
	 */
	private synchronized LicenceMetadata localAddLicence(String licenceStrPlusCrc) {
		if (licenceStrPlusCrc==null) throw new RuntimeException("licenceStrPlusCrc cannot be null");
		LicenceMetadata unverifiedMetadata;
		try {
			unverifiedMetadata = Licence.getUnverifiedMetadata(licenceStrPlusCrc);
		} catch (Exception e) {
			throw new RuntimeException("cannot decode licence string", e);
		}
		String productId = unverifiedMetadata.getProductId();
		licenceMap.put(productId, licenceStrPlusCrc);
		return unverifiedMetadata;
	}
	
	/**
	 * installs all licences in supplied licence list
	 * @param licenceList
	 */
	@Override
	public synchronized void installLicenceList(LicenceList licenceList){
		
		List<LicenceEntry> remoteLicenceEntries = licenceList.getLicenceList();

		for(LicenceEntry le: remoteLicenceEntries){
			String licenceStrPlusCrc = le.getLicenceStr();
			localAddLicence(licenceStrPlusCrc);
			System.out.println("    Licence Manager Added licence for productId="+le.getProductId());
			LOG.info("    Licence Manager Added licence for productId="+le.getProductId());
		}
		persist();
	}

	/**
	 * adds and persists new licence string
	 */
	public synchronized LicenceMetadata addLicence(String licenceStrPlusCrc) {
		LicenceMetadata unverifiedMetadata = localAddLicence(licenceStrPlusCrc);
		persist(); // persist the licence added locally
		return unverifiedMetadata;
	}

	@Override
	public synchronized boolean removeLicence(String productId) {
		if (productId==null) throw new RuntimeException("productID cannot be null");
		if (! licenceMap.containsKey(productId)) {
			return false;
		} else{
			licenceMap.remove(productId);
			persist();
			return true;
		}
	}

	@Override
	public synchronized String getLicence(String productId) {
		if (productId==null) throw new RuntimeException("productID cannot be null");
		return licenceMap.get(productId);
	}

	@Override
	public synchronized Map<String, String> getLicenceMap() {
		// returns an instance of the map because it may change after returned.
		Map<String, String> map = new TreeMap<String, String>(licenceMap);
		return map;
	}

	@Override
	public synchronized Map<String, String> getLicenceMapForSystemId(String systemId) {

		Map<String, String> returnMap = new TreeMap<String, String>();

		for(Entry<String, String> licenceEntry : licenceMap.entrySet()){
			String licenceStrPlusCrc = licenceEntry.getValue();
			LicenceMetadata licenceMetadata=null; 
			try {
				licenceMetadata = Licence.getUnverifiedMetadata(licenceStrPlusCrc);
			} catch (Exception e) {
				throw new RuntimeException("cannot decode licenceMetadata for internal licence map entry "+licenceEntry.getKey());
			}
			
			Integer maxSizeSystemIds=null;
			try {
				maxSizeSystemIds = Integer.parseInt(licenceMetadata.getMaxSizeSystemIds());
			} catch (Exception e){
				throw new RuntimeException("the maxSizeSystemIds '"+licenceMetadata.getMaxSizeSystemIds()
						+ "' cannot be parsed as int in licence for productId='"+licenceMetadata.getProductId()+"'", e);
			}
			// checks if any systemId is OK or if systemId matches list of systemId's in licence
			if (maxSizeSystemIds == 0 || licenceMetadata.getSystemIds().contains(systemId)){
				returnMap.put(licenceEntry.getKey(), licenceEntry.getValue());
			}
		}
		return returnMap;
	}

	@Override
	public synchronized String getSystemId() {
		return systemId;
	}

	@Override
	public synchronized void setSystemId(String systemId) {
		StringCrc32Checksum stringCrc32Checksum = new StringCrc32Checksum();
		if (! stringCrc32Checksum.checkCRC(systemId)){
			throw new RuntimeException("Incorrect checksum or format for systemId="+systemId);
		};

		this.systemId=systemId;
		persist();
	}

	/**
	 * Makes a random system instance value
	 */
	@Override
	public synchronized String makeSystemInstance() {

		// create random object
		Random randomgen = new Random();

		// get next long value 
		long systemIdValue = randomgen.nextLong();

		// make hex string
		String hexSystemIdString=Long.toHexString(systemIdValue);
		StringCrc32Checksum stringCrc32Checksum = new StringCrc32Checksum();
		String systemId = stringCrc32Checksum.addCRC(hexSystemIdString);
		this.setSystemId(systemId);
		return systemId;
	}

	/**
	 * adds checksum onto end of given string separated by - character
	 */
	@Override
	public String checksumForString(String valueString) {
		if (valueString==null) throw new RuntimeException("valueString cannot be null");
		StringCrc32Checksum stringCrc32Checksum = new StringCrc32Checksum();
		return stringCrc32Checksum.addCRC(valueString);
	}

	@Override
	public synchronized void deleteLicences() {
		licenceMap.clear();
	}

	/**
	 * Persists licence manager file. Creates a backup first
	 */
	public synchronized void persist(){
		if (fileUri==null) throw new RuntimeException("fileUri must be set for licence manager");

		try {
			
			// write licence data to a new temp file
			File tmpLicenceManagerFile = new File(fileUri+".tmp"); // temporary file
			File backupLicenceManagerFile = new File(fileUri+".back"); //backup file

			JAXBContext jaxbContext = JAXBContext.newInstance(LicenceServiceImpl.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			//jaxbMarshaller.marshal(this, file);
			jaxbMarshaller.marshal(this, tmpLicenceManagerFile);
			
			// if successfull written remove old backup file
			backupLicenceManagerFile.delete();
			
			// rename current licence file to .back
			File licenceManagerFile = new File(fileUri);
			licenceManagerFile.renameTo(backupLicenceManagerFile);

			// rename new temp file to file uri of current licence file
			tmpLicenceManagerFile.renameTo(licenceManagerFile);

		} catch (JAXBException e) {
			throw new RuntimeException("Problem persisting Licence Manager Data",e);
		}
	}


	public synchronized void load(){
		if (fileUri==null) throw new RuntimeException("fileUri must be set for licence manager");

		//TODO CREATE ROLLING FILE TO AVOID CORRUPTED LICENCES
		try {

			File licenceManagerFile = new File(fileUri);

			if (licenceManagerFile.exists()) {
				JAXBContext jaxbContext = JAXBContext.newInstance(LicenceServiceImpl.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

				LicenceServiceImpl licenceServiceImpl = (LicenceServiceImpl) jaxbUnmarshaller.unmarshal(licenceManagerFile);

				this.licenceMap.clear();
				this.licenceMap.putAll(licenceServiceImpl.getLicenceMap());
				this.systemId= licenceServiceImpl.getSystemId();
				System.out.println("Licence Manager successfully loaded licences from file="+licenceManagerFile.getAbsolutePath());
				LOG.info("Licence Manager successfully loaded licences from file="+licenceManagerFile.getAbsolutePath());
			} else {
				System.out.println("Licence Manager licence file="+licenceManagerFile.getAbsolutePath()+" does not exist. A new one will be created.");
				LOG.info("Licence Manager licence file="+licenceManagerFile.getAbsolutePath()+" does not exist. A new one will be created.");
			}

		} catch (JAXBException e) {
			LOG.error("Licence Manager Problem Starting: "+ e.getMessage());
			throw new RuntimeException("Problem loading Licence Manager Data",e);
		}
	}

	/**
	 * blueprint destroy-method
	 */
	public void close() {
	}

	/**
	 * blueprint init-method
	 */
	public void init(){
		load(); // first load the existing persistence file
	}

}
