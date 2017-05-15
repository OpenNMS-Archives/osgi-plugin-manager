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

package org.opennms.karaf.featuremgr.rest.impl;


import java.util.concurrent.atomic.AtomicReference;

import org.apache.karaf.features.FeaturesService;


/** 
 * Used to statically pass service references to Jersey ReST classes.
 * 
 * @author cgallen
 */
public class ServiceLoader {

	private static AtomicReference<FeaturesService> featuresService = new AtomicReference<>();

	public ServiceLoader(){
		super();
	}

	public ServiceLoader(FeaturesService featuresService ){
		super();
		setFeaturesService(featuresService);
	}

	/**
	 * @return the featuresService
	 */
	public static FeaturesService getFeaturesService() {
		return featuresService.get();
	}

	/**
	 * @param featuresService the featuresService to set
	 */
	public static void setFeaturesService(FeaturesService featuresService) {
		ServiceLoader.featuresService.set(featuresService);
	}

}
