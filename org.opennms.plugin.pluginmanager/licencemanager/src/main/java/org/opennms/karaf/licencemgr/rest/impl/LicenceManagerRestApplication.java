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

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.opennms.karaf.licencemgr.rest.LicencePublisherRest;
import org.opennms.karaf.licencemgr.rest.LicenceServiceRest;
import org.opennms.karaf.licencemgr.rest.ProductPublisherRest;
import org.opennms.karaf.licencemgr.rest.ProductRegisterRest;

public class LicenceManagerRestApplication extends Application {

	// doing this because the com.sun.ws.rest.api.core.PackagesResourceConfig 
	// class contains OSGi unfriendly classloader code
	@Override
	public Set<Class<?>> getClasses()
	{
		Set<Class<?>> s = new HashSet<Class<?>>();
		s.add(LicenceServiceRestImpl.class);
		s.add(LicencePublisherRestImpl.class);
		s.add(ProductPublisherRestImpl.class);
		s.add(ProductRegisterRestImpl.class);
		return s;
	}

	// Enable LoggingFilter & output entity.   
	// only for java logging - no use :(
	// registerInstances(new LoggingFilter(Logger.getLogger(EventGatewayApplication.class.getName()), true));
	//	}

}
