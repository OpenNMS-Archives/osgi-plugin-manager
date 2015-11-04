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

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link OptionMetadata }
     */
    public OptionMetadata createOptionMetadata() {
        return new OptionMetadata();
    }
    
    /**
     * Create an instance of {@link OptionMetadata }
     */
    public ProductMetadata createProductMetadata() {
        return new ProductMetadata();
    }
    
    /**
     * Create an instance of {@link OptionMetadata }
     */
    public LicenceMetadata createLicenceMetadata() {
        return new LicenceMetadata();
    }
    
    /**
     * Create an instance of {@link ProductSpecList }
     */
    public ProductSpecList createProductSpecList() {
        return new ProductSpecList();
    }
    
    /**
     * Create an instance of {@link LicenceList }
     */
    public LicenceList createLicenceList() {
        return new LicenceList();
    }
    
    /**
     * Create an instance of {@link LicenceSpecification }
     */
    public LicenceSpecification createLicenceSpecification() {
        return new LicenceSpecification();
    }
    
    /**
     * Create an instance of {@link LicenceSpecList }
     */
    public LicenceSpecList createLicenceSpecList() {
        return new LicenceSpecList();
    }
    
    /**
     * Create an instance of {@link LicenceSpecList }
     */
    public LicenceMetadataList createLicenceMetadataList() {
        return new LicenceMetadataList();
    }
    
    /**
     * Create an instance of {@link ErrorMessage }
     */
    public ErrorMessage createErrorMessage() {
        return new ErrorMessage();
    }
    
    /**
     * Create an instance of {@link ReplyMessage }
     */
    public ReplyMessage createReplyMessage() {
        return new ReplyMessage();
    }
    
    /**
     * Create an instance of {@link LicenceEntry }
     */
    public LicenceEntry createLicenceEntry() {
        return new LicenceEntry();
    }
    
}