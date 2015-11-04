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

package org.opennms.karaf.productpub;

import org.opennms.karaf.licencemgr.metadata.jaxb.ProductMetadata;
import org.osgi.framework.BundleContext;

public interface BundleProductSpec {
	
	public void registerSpec();

	public void unregisterSpec();

	public ProductMetadata getProductMetadata();

	public void setProductMetadata(ProductMetadata productMetadata);

	public ProductPublisher getProductPublisher();

	public void setProductPublisher(ProductPublisher productPublisher);

	public String getProductMetadataUri();

	public void setProductMetadataUri(String productMetadataUri);
	
	public void setBundleContext(BundleContext bundleContext);

	public BundleContext getBundleContext();
}
