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

import java.util.concurrent.atomic.AtomicReference;

import org.opennms.karaf.licencemgr.LicenceService;
import org.opennms.karaf.licencepub.LicencePublisher;
import org.opennms.karaf.productpub.ProductPublisher;
import org.opennms.karaf.productpub.ProductRegister;

/** used to statically pass service references to Jersey ReST classes
 * 
 * @author cgallen
 *
 */
public class ServiceLoader {

	private static AtomicReference<LicenceService> licenceService = new AtomicReference<>();

	private static AtomicReference<LicencePublisher> licencePublisher= new AtomicReference<>();

	private static AtomicReference<ProductPublisher> productPublisher= new AtomicReference<>();

	private static AtomicReference<ProductRegister> productRegister= new AtomicReference<>();


	public ServiceLoader(){
		super();
	}

	public ServiceLoader(LicenceService licenceService,
			LicencePublisher licencePublisher, 
			ProductPublisher productPublisher, 
			ProductRegister productRegister){
		super();

		setLicenceService(licenceService);
		setLicencePublisher(licencePublisher);
		setProductPublisher(productPublisher);
		setProductRegister(productRegister);
	}

	/**
	 * @return the licenceService
	 */
	public static LicenceService getLicenceService() {
		return licenceService.get();
	}

	/**
	 * @param licenceService the licenceService to set
	 */
	public static void setLicenceService(LicenceService licenceService) {
		ServiceLoader.licenceService.set(licenceService);
	}

	/**
	 * @return the licencePublisher
	 */
	public static LicencePublisher getLicencePublisher() {
		return licencePublisher.get();
	}

	/**
	 * @param licencePublisher the licencePublisher to set
	 */
	public static void setLicencePublisher(LicencePublisher licencePublisher) {
		ServiceLoader.licencePublisher.set(licencePublisher);
	}

	/**
	 * @return the productPublisher
	 */
	public static ProductPublisher getProductPublisher() {
		return productPublisher.get();
	}

	/**
	 * @param productPublisher the productPublisher to set
	 */
	public static void setProductPublisher(ProductPublisher productPublisher) {
		ServiceLoader.productPublisher.set(productPublisher);
	}

	/**
	 * @return the productRegister
	 */
	public static ProductRegister getProductRegister() {
		return productRegister.get();
	}

	/**
	 * @param productRegister the productRegister to set
	 */
	public static void setProductRegister(ProductRegister productRegister) {
		ServiceLoader.productRegister.set(productRegister);
	}

}
