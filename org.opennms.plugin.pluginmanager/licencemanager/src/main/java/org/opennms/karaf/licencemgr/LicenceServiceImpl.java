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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.opennms.karaf.licencemgr.rest.client.jerseyimpl.LicenceManagerClientRestJerseyImpl;
import org.osgi.framework.ServiceException;

@XmlRootElement(name="LicenceServiceData")
@XmlAccessorType(XmlAccessType.NONE)
public class LicenceServiceImpl implements LicenceService {


	// used to store location of persistence file
	private String fileUri=null;

	// if true, will try to download licence list from remote urls
	private Boolean useRemoteLicenceManagers=false;

	// local list of urls to contact remote licence managers
	// in order to download licence list for this system. Urls will be tried in order
	private List<String> remoteLicenceMgrs= new ArrayList<String>();

	// used to store list of productId's with authenticated licences
	// this is not persisted but rebuilt as products are installed and authenticated
	private Set<String> authenticatedLicences= new HashSet<String>();

	@XmlElementWrapper(name="licenceMap")
	private SortedMap<String, String> licenceMap = new TreeMap<String, String>();

	@XmlElement
	private String systemId="NOT_SET";

	// username to access remote licence manager
	private String remoteLicenceManagerUserName;

	// password to access remote licence manager
	private String remoteLicenceManagerPassword;

	/**
	 * @param fileUri used to store location of persistence file for licence service
	 */
	public void setFileUri(String fileUri) {
		this.fileUri = fileUri;
	}


	/**
	 * @param remoteLicenceManagerUserName username to access remote licence manager
	 */
	public void setRemoteLicenceManagerUserName(String remoteLicenceManagerUserName) {
		this.remoteLicenceManagerUserName = remoteLicenceManagerUserName;
	}

	/**
	 * @param remoteLicenceManagerPassword password to access remote licence manager
	 */
	public void setRemoteLicenceManagerPassword(String remoteLicenceManagerPassword) {
		this.remoteLicenceManagerPassword = remoteLicenceManagerPassword;
	}

	/**
	 * @param useRemoteLicenceManagers if 'true', will try to download licence list from remote licence managers
	 * if 'false' will just use local values. Any other value throws an exception
	 */
	public synchronized void setUseRemoteLicenceManagers(String useRemoteLicenceManagersStr) {
		if(useRemoteLicenceManagersStr !=null && ( useRemoteLicenceManagersStr.equals("true") || useRemoteLicenceManagersStr.equals("false"))) {
			useRemoteLicenceManagers = Boolean.valueOf(useRemoteLicenceManagersStr);
		} else throw new RuntimeException("useRemoteLicenceManagers set to ("+useRemoteLicenceManagersStr+") but must be set to boolean true or false");
	}

	/**
	 * @param remoteLicenceMgrsStr comma separated local list of urls to contact remote licence managers
	 * in order to download licence list for this system. Urls will be tried in order. -->
	 */
	public synchronized void setRemoteLicenceMgrs(String remoteLicenceMgrsStr) {
		if (remoteLicenceMgrsStr ==null) throw new RuntimeException("remoteLicenceMgrsStr should not be set to null.");
		if (! "".equals(remoteLicenceMgrsStr)) {
			String[] urls = remoteLicenceMgrsStr.split(",");
			List<String> licenceMgrs= new ArrayList<String>();
			for (String urlstr: urls){
				urlstr.trim();
				if (! "".equals(urlstr)) try {
					URL url = new URL(urlstr); // if cannot be parsed then reject string
					URI uri = url.toURI(); // checks uri can be parsed
					licenceMgrs.add(urlstr);
				} catch (MalformedURLException | URISyntaxException e){
					throw new RuntimeException("unparsable URL in remote Licence Managers configuration ",e);
				}
			}
			remoteLicenceMgrs = licenceMgrs;
			System.out.println("Licence manager remote licence managers set to:");
			for (String lmUrl: remoteLicenceMgrs){
				System.out.println("'"+lmUrl+"' ");
			}
		}
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
	 * tries to install remote licences from given list of urls for remote managers
	 * tries each manager in turn and returns for the first one to succeed
	 * returns url of remote licence manager which was used or null if none succeeded
	 * @ param systemIdStr the system id to use to select remote values
	 * 
	 */
	public synchronized String installRemoteLicencesFromUrlList(String systemIdStr){

		String successRemoteLicenceManagerUrl=null;
		for (String remoteLicenceManagerUrl : remoteLicenceMgrs ){
			try {
				successRemoteLicenceManagerUrl =installRemoteLicences(remoteLicenceManagerUrl, systemIdStr );
			} catch (Exception e){
				System.out.println("   Licence Manager could not load licences from from licence manager at "+remoteLicenceManagerUrl
						+" for systemIdStr='"+systemIdStr+"' Exception:"+e);
			}
			if (null != successRemoteLicenceManagerUrl) {
				System.out.println("Licence Manager succeeded in loading licences from licence manager at "+successRemoteLicenceManagerUrl);
				break;
			}
		}
		if (null == successRemoteLicenceManagerUrl) System.out.println("Licence Manager Could not load licences from any remote licence manager.");

		return successRemoteLicenceManagerUrl;
	}

	/**
	 * tries to install remote licences from licence manager at a given url. Updates licences list and persists.
	 * This will only replace duplicate licence it will not delete licences present locally which are not
	 * in remote system
	 * NOTE - because of 'synchronised' you cannot get remote licences from local system
	 * @param remoteLicencesUrl URL version of remote url
	 * 
	 */
	public synchronized String installRemoteLicences(String remoteLicenceManagerUrl, String systemIdStr ){
		if (remoteLicenceManagerUrl==null) throw new RuntimeException("Licence Manager remoteLicenceManagerUrl must not be null");
		if (systemIdStr==null) throw new RuntimeException("Licence Manager systemIdStr must not be null");
		try {
			LicenceManagerClientRestJerseyImpl licenceManagerClient = new LicenceManagerClientRestJerseyImpl();

			String basePath = "/licencemgr/rest/licence-mgr";

			licenceManagerClient.setBasePath(basePath);
			licenceManagerClient.setBaseUrl(remoteLicenceManagerUrl);
			licenceManagerClient.setUserName(remoteLicenceManagerUserName);
			licenceManagerClient.setPassword(remoteLicenceManagerPassword);

			LicenceList licenceList = licenceManagerClient.getLicenceMapForSystemId(systemIdStr);
			List<LicenceEntry> remoteLicenceEntries = licenceList.getLicenceList();

			for(LicenceEntry le: remoteLicenceEntries){
				String licenceStrPlusCrc = le.getLicenceStr();
				localAddLicence(licenceStrPlusCrc);
				System.out.println("    Licence Manager Added remote licence from "+remoteLicenceManagerUrl+" for productId="+le.getProductId());
			}
			persist();
			return remoteLicenceManagerUrl;

		} catch (Exception e){
			throw new RuntimeException("    Licence Manager Cannot get remote licences from remoteLicenceManagerUrl="+remoteLicenceManagerUrl,e);
		}
	}


	public synchronized void persist(){
		if (fileUri==null) throw new RuntimeException("fileUri must be set for licence manager");

		try {

			File licenceManagerFile = new File(fileUri);
			JAXBContext jaxbContext = JAXBContext.newInstance(LicenceServiceImpl.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			//jaxbMarshaller.marshal(this, file);
			jaxbMarshaller.marshal(this, licenceManagerFile);

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
			} else {
				System.out.println("Licence Manager licence file="+licenceManagerFile.getAbsolutePath()+" does not exist. A new one will be created.");
			}

		} catch (JAXBException e) {
			System.out.println("Licence Manager Problem Starting: "+ e.getMessage());
			throw new RuntimeException("Problem loading Licence Manager Data",e);
		}
	}

	/**
	 * blueprint destroy-method
	 */
	public synchronized void close() {
		System.out.println("Licence Manager Shutting Down ");
	}

	/**
	 * blueprint init-method
	 */
	public synchronized void init(){

		System.out.println("Licence Manager Starting");

		load(); // first load the existing persistence file

		String installedFromLicenceMgr=null;
		if (useRemoteLicenceManagers!=null && useRemoteLicenceManagers==true){
			System.out.println("Licence Manager system attempting to load remote licences");
			if(remoteLicenceMgrs !=null && ! remoteLicenceMgrs.isEmpty()){
				installedFromLicenceMgr=installRemoteLicencesFromUrlList(systemId);
			} else {
				System.out.println("WARNING: list of remote licence managers is empty");
			}
			if (installedFromLicenceMgr!=null) {
				System.out.println("Licence Manager loaded remote licences from url="+installedFromLicenceMgr);
			} else System.out.println("WARNING Licence Manager unabled to load remote licences from any supplied url");
		} else System.out.println("Licence Manager system set to not load remote licences");
		System.out.println("Licence Manager Started");

	}

}
