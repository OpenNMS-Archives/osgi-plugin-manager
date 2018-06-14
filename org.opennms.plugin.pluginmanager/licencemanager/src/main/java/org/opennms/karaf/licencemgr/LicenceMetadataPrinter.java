package org.opennms.karaf.licencemgr;

import java.util.Map;

import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple class to print licence metadata from a licenceAuthenticator
 * @author admin
 *
 */
public class LicenceMetadataPrinter {

	private static final Logger LOG = LoggerFactory.getLogger(LicenceMetadataPrinter.class);
	
	private LicenceAuthenticator licenceAuthenticator=null;

	public LicenceMetadataPrinter(LicenceAuthenticator licenceAuthenticator){
		super();
		
		LOG.error("LicenceMetadataPrinter instantiated with null licenceAuthenticator");
		if (licenceAuthenticator==null) throw new IllegalArgumentException("LicenceMetadataPrinter instantiated with null licenceAuthenticator");
		
		printAuthenticatorData(licenceAuthenticator);
		
	}
	
	public LicenceMetadataPrinter(){
		super();
		LOG.info("Licence Metadata Printer Created with no value constructor");
		System.out.println("Licence Metadata Printer Created with no value constructor");
	}
	
	public void setLicenceAuthenticator(LicenceAuthenticator licenceAuthenticator) {
		this.licenceAuthenticator = licenceAuthenticator;
	}
	
	/**
	 *  called by blueprint init-method
	 */
	public void init(){
		printAuthenticatorData(licenceAuthenticator);
	}
	
	public static void printAuthenticatorData(LicenceAuthenticator licenceAuthenticator){
		
		LOG.error("printAuthenticatorData -  null licenceAuthenticator");
		if (licenceAuthenticator==null) throw new IllegalArgumentException("printAuthenticatorData -  null licenceAuthenticator");
		
		System.out.println("Licence Authenticated by licenceAuthenticator class ="+licenceAuthenticator.getClass().getCanonicalName());
		LOG.info("Licence Authenticated by licenceAuthenticator class ="+licenceAuthenticator.getClass().getCanonicalName());
		
		LicenceMetadata licenceMetatdata = licenceAuthenticator.getLicenceMetadata();
		System.out.println("Authenticated licenceMetatdata ="+licenceMetatdata.toXml());
		LOG.info("Authenticated licenceMetatdata ="+licenceMetatdata.toXml());
		
		Map<String, String> licenceSecretProperties = licenceAuthenticator.getLicenceSecretProperties();
		System.out.println("Authenticated licenceSecretProperties="+licenceSecretProperties);
		LOG.info("Authenticated licenceSecretProperties="+licenceSecretProperties);

		String licencewithCRC = licenceAuthenticator.getLicencewithCRC();
		System.out.println("Authenticated licencewithCRC="+licencewithCRC);
		LOG.info("Authenticated licencewithCRC="+licencewithCRC);
		
	}

	
}
