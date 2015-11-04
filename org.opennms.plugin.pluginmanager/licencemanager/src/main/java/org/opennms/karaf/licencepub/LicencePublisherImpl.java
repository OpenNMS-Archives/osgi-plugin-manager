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

package org.opennms.karaf.licencepub;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.opennms.karaf.licencemgr.metadata.Licence;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadata;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceSpecification;
import org.opennms.karaf.licencemgr.metadata.jaxb.OptionMetadata;

public class LicencePublisherImpl implements LicencePublisher {

	private SortedMap<String, LicenceSpecification> licenceSpecMap = new TreeMap<String, LicenceSpecification>();

	/**
	 * @param licenceSpec LicenceSpecification to be added to licence publisher
	 */
	@Override
	public synchronized void addLicenceSpec(LicenceSpecification licenceSpec) {
		if (licenceSpec==null) throw new IllegalArgumentException("licenceSpec cannot be null");
		licenceSpecMap.put(licenceSpec.getProductId(), licenceSpec);
	}

	/**
	 * removes the entry for productId from the licenceSpecMap
	 * @param productId
	 */
	@Override
	public synchronized boolean removeLicenceSpec(String productId) {
		if (productId==null) throw new IllegalArgumentException("productId cannot be null");
		if (! licenceSpecMap.containsKey(productId)) {
			return false;
		} else{
			licenceSpecMap.remove(productId);
			return true;
		}
	}

	/**
	 * @param productId
	 * @return  the LicenceSpecification stored for productId 
	 * returns null if no LicenceSpecification found for productId 
	 */
	@Override
	public synchronized LicenceSpecification getLicenceSpec(String productId) {
		if (productId==null) throw new IllegalArgumentException("productId cannot be null");
		return licenceSpecMap.get(productId);
	}

	/**
	 * @return a copy of the map of the LicenceSpecifications ordered by productId
	 */
	@Override
	public synchronized Map<String, LicenceSpecification> getLicenceSpecMap() {
		return new TreeMap<String, LicenceSpecification>(licenceSpecMap);
	}

	/**
	 * deletes all values of the licenceSpecMap
	 */
	@Override
	public synchronized void deleteLicenceSpecifications() {
		licenceSpecMap.clear();
	}

	/**
	 * Creates an encoded String instance of a licence from the LicenceSecification 
	 * corresponding to the productId in the supplied createLicenceMetadata
	 * throws IllegalArgumentException if the correspondingLicenceSecification cannot be found
	 * or the names of options or licencee are different from the specification
	 * @Param createLicenceMetadata this should be created from a copy of the LicenceMetadata in the LicenceSpecfication
	 * i.e. it must contain the productId and the options must correspond to the
	 * options in the LicenceSpecification
	 */
	@Override
	public synchronized  String createLicenceInstanceStr(LicenceMetadata createLicenceMetadata) throws IllegalArgumentException {
		if (createLicenceMetadata==null) throw new IllegalArgumentException("licenceMetadata cannot be null");
		
		String productId = createLicenceMetadata.getProductId();
		
		if (! licenceSpecMap.containsKey(productId))
			throw new IllegalArgumentException("no Licence Specification exists for productId="+productId);
		
		LicenceSpecification licenceSpec = licenceSpecMap.get(productId);
		LicenceMetadata metadataSpec = licenceSpec.getLicenceMetadataSpec();
		
		// check that the licensor is the same as in the licence specification
		if (! metadataSpec.getLicensor().equals(createLicenceMetadata.getLicensor())) 
			throw new IllegalArgumentException("createLicenceMetadata licensor='"+createLicenceMetadata.getLicensor()
					+"' is different from Licence Specification licensor='"+metadataSpec.getLicensor()+"' for productId="+productId);

		// check that when maxSizeSystemIds= 0 there are no systemId's defined in specification
		Integer maxSizeSystemIds=null;
		try {
			maxSizeSystemIds = Integer.parseInt(createLicenceMetadata.getMaxSizeSystemIds());
		} catch (Exception e){
			throw new RuntimeException("the maxSizeSystemIds '"+createLicenceMetadata.getMaxSizeSystemIds()
					+ "' cannot be parsed as int in licence for productId='"+createLicenceMetadata.getProductId()+"'", e);
		}
		
		if ( maxSizeSystemIds==0 && createLicenceMetadata.getSystemIds().size()>0){
			throw new IllegalArgumentException("createLicenceMetadata maxSizeSystemIds= 0 but number of systemIds defined is greater than 0 in licence for productId="+productId);
		}
		
		// check that the actual number of systemIds in the licence specification is not greater than the max number in
		// the supplied licenceMetadata
		if ( maxSizeSystemIds< createLicenceMetadata.getSystemIds().size()){
			throw new IllegalArgumentException("createLicenceMetadata maxSizeSystemIds is less than the number of systemIds defined in licence for productId="+productId);
		}

		// check that the options in the licence specification match the options in
		// the supplied licenceMetadata
		// note value for options are set in the licence specification but the number
		// and names of options must be the same as the specification
		Set<OptionMetadata> specOptions = metadataSpec.getOptions();
		Set<OptionMetadata> licenceOptions = createLicenceMetadata.getOptions();
		if (specOptions.size()!=licenceOptions.size()) 
			throw new IllegalArgumentException("licenceMetadata options do not match specification for productId="+productId);
		
		HashSet<String> specOptionNames=new HashSet<String>();
		for ( OptionMetadata option: specOptions){
			specOptionNames.add(option.getName());
		}
		for ( OptionMetadata option: licenceOptions){
			if(! specOptionNames.contains(option.getName()))
					throw new IllegalArgumentException("licenceMetadata option name="+option.getName()+" is not in specification for productId="+productId);
		}
		
		// create and return a new licence with the supplied Metadata
		Licence licence= new Licence(createLicenceMetadata, licenceSpec.getPublicKeyStr(), licenceSpec.getAesSecretKeyStr());
		return licence.getLicenceStrPlusCrc();
	}

	/**
	 * Creates an encoded String instance of a licence from the supplied licenceMetadata in xml form
	 * using same criteria as in createLicenceInstance(licenceMetadata)
	 * 
	 */
	@Override
	public synchronized String createLicenceInstanceStr(String licenceMetadataXml) {
		if (licenceMetadataXml==null) throw new IllegalArgumentException("licenceMetadataXml cannot be null");
        LicenceMetadata licenceMetadata= new LicenceMetadata();
        licenceMetadata.fromXml(licenceMetadataXml);
        return createLicenceInstanceStr(licenceMetadata);
	}


}
