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

import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadata;
import org.opennms.karaf.licencepub.LicencePublisher;
import org.osgi.framework.BundleContext;

public interface BundleLicenceSpec {

	public void unregisterSpec();

	public void registerSpec();

	public void setBundleContext(BundleContext bundleContext);

	public BundleContext getBundleContext();

	public LicenceMetadata getLicenceMetadataSpec();

	public void setLicenceMetadataSpec(LicenceMetadata licenceMetadata);

	public LicencePublisher getLicencePublisher();

	public void setLicencePublisher(LicencePublisher licencePublisher);

	public String getLicenceMetadataUri();

	public void setLicenceMetadataUri(String licenceMetadataUri);

	public String getAesSecretKeyStr();

	public void setAesSecretKeyStr(String aesSecretKeyStr);

	public String getPublicKeyStr();

	public void setPublicKeyStr(String publicKeyStr);

	public void setProductId(String productId);

	public String getProductId();
	
}
