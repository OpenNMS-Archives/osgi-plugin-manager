package org.opennms.karaf.licencemgr;

import java.io.File;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.opennms.karaf.licencemgr.TaskTimer;
import org.opennms.karaf.licencemgr.TaskTimer.ScheduledTask;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceList;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadata;
import org.opennms.karaf.licencemgr.metadata.jaxb.LicenceMetadataList;
import org.opennms.karaf.licencemgr.rest.client.jerseyimpl.LicenceManagerClientRestJerseyImpl;
import org.opennms.karaf.licencemgr.rest.client.jerseyimpl.LicencePublisherClientRestJerseyImpl;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LicenceManagerController {
	private static final Logger LOG = LoggerFactory.getLogger(LicenceManagerController.class);

	public static final String PERSISTANT_ID = "org.opennms.features.licencemgr.config";
	public static final String USE_REMOTE_LICENCE_MANAGERS_KEY = "org.opennms.karaf.licencemanager.use-remote-licence-managers";
	public static final String REMOTE_LICENCE_MANAGERS_URLS_KEY = "org.opennms.karaf.licencemanager.remote-licence-managers-urls";
	public static final String REMOTE_LICENCE_MANAGERS_USERNAME_KEY = "org.opennms.karaf.licencemanager.remote-licence-managers-username";
	public static final String REMOTE_LICENCE_MANAGERS_PASSWORD_KEY = "org.opennms.karaf.licencemanager.remote-licence-managers-password";
	public static final String RETRY_INTERVAL_KEY = "org.opennms.karaf.licencemanager.retryInterval";
	public static final String RETRY_NUMBER_KEY = "org.opennms.karaf.licencemanager.retryNumber";
	public static final String UPDATE_INTERVAL_KEY = "org.opennms.karaf.licencemanager.updateInterval";
	public static final String CHECK_LICENCE_INTERVAL_KEY = "org.opennms.karaf.licencemanager.checkLicenceInterval";
	public static final String USE_LICENCE_REQUEST_METADATA_KEY = "org.opennms.karaf.licencemanager.useLicenceRequestMetadata";

	private Integer m_retryInterval=null;
	private Integer m_retryNumber=null;
	private Integer m_updateInterval=null;
	private Integer m_checkLicenceInterval=null;
	private boolean m_useLicenceRequestMetadata=false;
	private String m_licenceRequestMetadataFile=null;


	private TaskTimer m_timer=new TaskTimer();

	private LicenceService m_licenceService=null;

	// if true, will try to download licence list from remote urls
	private Boolean m_useRemoteLicenceManagers=false;

	// local list of urls to contact remote licence managers
	// in order to download licence list for this system. Urls will be tried in order
	private Set<String> m_remoteLicenceMgrs = new LinkedHashSet<String>();

	// username to access remote licence manager
	private String m_remoteLicenceManagerUserName;

	// password to access remote licence manager
	private String m_remoteLicenceManagerPassword;

	private ConfigurationAdmin m_configurationAdmin=null;

	public void setConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
		this.m_configurationAdmin = configurationAdmin;
	}

	public LicenceService getLicenceService() {
		return m_licenceService;
	}

	public void setLicenceService(LicenceService licenceService) {
		this.m_licenceService = licenceService;
	}

	/**
	 * @param m_remoteLicenceManagerUserName username to access remote licence manager
	 */
	public void setRemoteLicenceManagerUserName(String remoteLicenceManagerUserName) {
		this.m_remoteLicenceManagerUserName = remoteLicenceManagerUserName;
	}

	/**
	 * @param m_remoteLicenceManagerPassword password to access remote licence manager
	 */
	public void setRemoteLicenceManagerPassword(String remoteLicenceManagerPassword) {
		this.m_remoteLicenceManagerPassword = remoteLicenceManagerPassword;
	}

	/**
	 * @param m_useRemoteLicenceManagers if 'true', will try to download licence list from remote licence managers
	 * if 'false' will just use local values. Any other value throws an exception
	 */
	public synchronized void setUseRemoteLicenceManagers(String useRemoteLicenceManagersStr) {
		if(useRemoteLicenceManagersStr !=null && ( useRemoteLicenceManagersStr.equals("true") || useRemoteLicenceManagersStr.equals("false"))) {
			m_useRemoteLicenceManagers = Boolean.valueOf(useRemoteLicenceManagersStr);
		} else throw new RuntimeException("m_useRemoteLicenceManagers set to ("+useRemoteLicenceManagersStr+") but must be set to boolean true or false");
	}

	public synchronized void setRemoteLicenceManagersUrls(String urlList){
		if (urlList ==null) throw new RuntimeException("urlList should not be set to null.");
		if (! "".equals(urlList)) {
			String[] urls = urlList.split(",");

			Set<String> licenceMgrs = new LinkedHashSet<String>();
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
			m_remoteLicenceMgrs = licenceMgrs;
			String msg="Remote licence managers set to:";
			for (String lmUrl: m_remoteLicenceMgrs){
				msg=msg+"'"+lmUrl+"' ";
			}
			LOG.info(msg);
		}
	}

	/**
	 * @param urlList comma separated local list of urls to contact remote licence managers
	 * in order to download licence list for this system. Urls will be tried in order. -->
	 */
	public synchronized void updateRemoteLicenceManagersUrls(String urlList, String remoteUsername, String remotePassword) {
		setRemoteLicenceManagersUrls(urlList);
		setRemoteLicenceManagerPassword(remotePassword);
		setRemoteLicenceManagerUserName(remoteUsername);
		String msg = "Licence manager remoteUsername set to:"+remoteUsername+" remotePassword(obfuscated):";
		String msg2 = (remotePassword==null || "".equals(remotePassword) ) ? remotePassword : "xxxxxx";
		LOG.info(msg+msg2);
		System.out.println(msg);
	}


	public void setRetryInterval(Integer retryInterval) {
		this.m_retryInterval = retryInterval;
	}

	public void setRetryNumber(Integer retryNumber) {
		this.m_retryNumber = retryNumber;
	}

	public void setUpdateInterval(Integer updateInterval) {
		this.m_updateInterval = updateInterval;
	}

	public void setCheckLicenceInterval(Integer checkLicenceInterval) {
		this.m_checkLicenceInterval = checkLicenceInterval;
	}

	public void setUseLicenceRequestMetadata(String useLicenceRequestMetadataStr) {
		if(useLicenceRequestMetadataStr !=null && ( useLicenceRequestMetadataStr.equals("true") || useLicenceRequestMetadataStr.equals("false"))) {
			m_useLicenceRequestMetadata = Boolean.valueOf(useLicenceRequestMetadataStr);
		} else throw new RuntimeException("useLicenceRequestMetadataStr set to ("+useLicenceRequestMetadataStr+") but must be set to boolean true or false");

	}

	public void setLicenceRequestMetadataFile(String licenceRequestMetadataFile) {
		this.m_licenceRequestMetadataFile = licenceRequestMetadataFile;
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
		for (String remoteLicenceManagerUrl : m_remoteLicenceMgrs ){
			try {
				successRemoteLicenceManagerUrl =installRemoteLicencesFromSystemId(remoteLicenceManagerUrl, systemIdStr, m_remoteLicenceManagerUserName, m_remoteLicenceManagerPassword );
			} catch (Exception e){
				System.err.println("   Licence Manager could not load licences from from licence manager at "+remoteLicenceManagerUrl
						+" for systemIdStr='"+systemIdStr+"' Exception:"+e);
				LOG.error("   Licence Manager could not load licences from from licence manager at "+remoteLicenceManagerUrl
						+" for systemIdStr='"+systemIdStr+"' Exception:",e);
			}
			if (null != successRemoteLicenceManagerUrl) {
				System.out.println("Licence Manager succeeded in loading licences from licence manager at "+successRemoteLicenceManagerUrl);
				LOG.info("Licence Manager succeeded in loading licences from licence manager at "+successRemoteLicenceManagerUrl);
				break;
			}
		}
		if (null == successRemoteLicenceManagerUrl) {
			System.err.println("Licence Manager Could not load licences from any remote licence manager.");
			LOG.error("Licence Manager Could not load licences from any remote licence manager.");
		}

		return successRemoteLicenceManagerUrl;
	}


	/**
	 * tries to install remote licences from licence manager at a given url. Updates licences list and persists.
	 * This will only replace duplicate licence it will not delete licences present locally which are not
	 * in remote system
	 * NOTE - because of 'synchronised' you cannot get remote licences from local system //TODO REMOVE 
	 * @param remoteLicencesUrl URL version of remote url
	 * 
	 */
	public synchronized String installRemoteLicencesFromSystemId(String remoteLicenceManagerUrl, String systemIdStr, String remoteLicenceManagerUserName, String remoteLicenceManagerPassword ){
		if (m_licenceService==null)  throw new RuntimeException("m_licenceService must not be null");
		if (remoteLicenceManagerUrl==null) throw new RuntimeException("Licence Manager remoteLicenceManagerUrl must not be null");
		if (systemIdStr==null) throw new RuntimeException("Licence Manager systemIdStr must not be null");

		String remoteUserName = (remoteLicenceManagerUserName!=null) ? remoteLicenceManagerUserName : m_remoteLicenceManagerUserName;
		String remotePassword = (remoteLicenceManagerPassword!=null) ? remoteLicenceManagerPassword : m_remoteLicenceManagerPassword;

		try {
			LicenceManagerClientRestJerseyImpl licenceManagerClient = new LicenceManagerClientRestJerseyImpl();

			String basePath = "/licencemgr/rest/v1-0/licence-mgr";

			licenceManagerClient.setBasePath(basePath);
			licenceManagerClient.setBaseUrl(remoteLicenceManagerUrl);
			licenceManagerClient.setUserName(remoteUserName);
			licenceManagerClient.setPassword(remotePassword);

			LicenceList licenceList = licenceManagerClient.getLicenceMapForSystemId(systemIdStr);


			m_licenceService.installLicenceList(licenceList);

			return remoteLicenceManagerUrl;

		} catch (Exception e){
			throw new RuntimeException("    Licence Manager Cannot get remote licences from remoteLicenceManagerUrl="+remoteLicenceManagerUrl,e);
		}
	}

	public synchronized String installRemoteLicencesFromLicenceMetadata(String remoteLicenceManagerUrl, LicenceMetadataList licenceMetadataList, String remoteLicenceManagerUserName, String remoteLicenceManagerPassword ){
		if (m_licenceService==null)  throw new RuntimeException("m_licenceService must not be null");
		if (remoteLicenceManagerUrl==null) throw new RuntimeException("Licence Manager remoteLicenceManagerUrl must not be null");
		if (licenceMetadataList==null) throw new RuntimeException("Licence Manager licenceMetadataList must not be null");

		String remoteUserName = (remoteLicenceManagerUserName!=null) ? remoteLicenceManagerUserName : m_remoteLicenceManagerUserName;
		String remotePassword = (remoteLicenceManagerPassword!=null) ? remoteLicenceManagerPassword : m_remoteLicenceManagerPassword;

		try {
			LicencePublisherClientRestJerseyImpl licencePublisherClient = new LicencePublisherClientRestJerseyImpl();

			String basePath = "/licencemgr/rest/v1-0/licence-pub";

			licencePublisherClient.setBasePath(basePath);
			licencePublisherClient.setBaseUrl(remoteLicenceManagerUrl);
			licencePublisherClient.setUserName(remoteUserName);
			licencePublisherClient.setPassword(remotePassword);

			LicenceList licenceList = licencePublisherClient.createMultiLicenceInstance(licenceMetadataList);

			m_licenceService.installLicenceList(licenceList);

			return remoteLicenceManagerUrl;

		} catch (Exception e){
			throw new RuntimeException("    Licence Manager Cannot get remote licences from remoteLicenceManagerUrl="+remoteLicenceManagerUrl,e);
		}
	}

	public synchronized String installRemoteLicencesUsingMetadataFromUrlList(String systemIdStr){

		// fetch list of remote licence metadata to ask for and add the local systemId
		LicenceMetadataList licenceMetadataList;
		try{
			licenceMetadataList = loadLicenceMetadataListFile();
			for (LicenceMetadata licenceMetadata :licenceMetadataList.getLicenceMetadataList()){
				licenceMetadata.getSystemIds().add(systemIdStr);
			}
		} catch (Exception e){
			throw new RuntimeException("problem loading licenceMetadataListFile",e);
		}

		// try all urls to download licences
		String successRemoteLicenceManagerUrl=null;
		for (String remoteLicenceManagerUrl : m_remoteLicenceMgrs ){
			try {

				successRemoteLicenceManagerUrl=installRemoteLicencesFromLicenceMetadata(remoteLicenceManagerUrl, licenceMetadataList,  m_remoteLicenceManagerUserName, m_remoteLicenceManagerPassword );

			} catch (Exception e){
				System.err.println("   Licence Manager could not load licences from from licence manager at "+remoteLicenceManagerUrl
						+" for systemIdStr='"+systemIdStr+"' Exception:"+e);
				LOG.error("   Licence Manager could not load licences from from licence manager at "+remoteLicenceManagerUrl
						+" for systemIdStr='"+systemIdStr+"' Exception:",e);
			}
			if (null != successRemoteLicenceManagerUrl) {
				System.out.println("Licence Manager succeeded in loading licences from licence manager at "+successRemoteLicenceManagerUrl);
				LOG.info("Licence Manager succeeded in loading licences from licence manager at "+successRemoteLicenceManagerUrl);
				break;
			}
		}
		if (null == successRemoteLicenceManagerUrl) {
			System.err.println("Licence Manager Could not load licences from any remote licence manager.");
			LOG.error("Licence Manager Could not load licences from any remote licence manager.");
		}

		return successRemoteLicenceManagerUrl;
	}


	/**
	 * blueprint destroy-method
	 */
	public synchronized void close() {
		System.out.println("Licence Manager Shutting Down ");
		LOG.info("Licence Manager Shutting Down ");
	}

	/**
	 * blueprint init-method
	 */
	public synchronized void init(){
		System.out.println("Licence Manager Starting");
		LOG.info("Licence Manager Starting");

		if (m_useRemoteLicenceManagers!=null && m_useRemoteLicenceManagers==true){
			System.out.println("Licence Manager system schedulling load of remote licences");
			try{
				restartSchedule();
			}catch(Exception ex){
				LOG.error("PluginFeatureManager problem starting licence download schedule",ex);
			}
		} else {
			System.out.println("Licence Manager system set to not load remote licences");
			LOG.info("Licence Manager system set to not load remote licences");
		}

		System.out.println("Licence Manager Started");
		LOG.info("Licence Manager Started");
	}

	private class ScheduledLicenceUpdate implements ScheduledTask{
		private LicenceManagerController s_licenceManagerController;
		private AtomicInteger s_count = new AtomicInteger(0);
		private boolean s_useLicenceRequestMetadata=false;
		private String s_systemId;

		ScheduledLicenceUpdate(LicenceManagerController licenceManagerController){
			this.s_licenceManagerController=Objects.requireNonNull(licenceManagerController);
			this.s_useLicenceRequestMetadata= licenceManagerController.m_useLicenceRequestMetadata;
			this.s_systemId=licenceManagerController.m_licenceService.getSystemId();
		}

		@Override
		public boolean runScheduledTask() {
			int c = s_count.incrementAndGet();
			boolean success=false;

			LOG.info("Running scheduled licence update. Times this schedule has run: "+c);
			try{
				if(s_useLicenceRequestMetadata){
					// install licences using systemId and request metadata
					LOG.info("trying to installed remote licences using metadata and systemId");
					String installedUrl= s_licenceManagerController.installRemoteLicencesUsingMetadataFromUrlList(s_systemId);
					if(installedUrl!=null){
						success=true;
						LOG.info("installed remote licences using metadata for systemId="+s_systemId
								+ "from url="+installedUrl);
					}
				} else {
					// install licences using only systemId
					LOG.info("trying to installed remote licences using systemId");
					String installedUrl= s_licenceManagerController.installRemoteLicencesFromUrlList(s_systemId);
					if(installedUrl!=null){
						success=true;
						LOG.info("installed remote licences for systemId="+s_systemId
								+ "from url="+installedUrl);
					}
				}
			} catch(Exception e){
				LOG.error("problem running schedule updating licence from plugin managers",e);
			}
			return success;
		}
	}

	public synchronized void restartSchedule(){
		if(this.m_retryInterval==null) throw new RuntimeException("retryInterval cannot be null when starting schedule");
		if(this.m_retryNumber==null) throw new RuntimeException("retryNumber cannot be null when starting schedule");
		if(this.m_updateInterval==null) throw new RuntimeException("updateInterval cannot be null when starting schedule");

		m_timer.stopSchedule();
		if(this.m_useRemoteLicenceManagers){
			m_timer.setRetryInterval(this.m_retryInterval);
			m_timer.setRetryNumber(this.m_retryNumber);
			m_timer.setUpdateInterval(this.m_updateInterval);

			LicenceManagerController licenceManagerController=this;
			ScheduledTask task = new ScheduledLicenceUpdate(licenceManagerController);

			m_timer.setTask(task);

			m_timer.startSchedule();
		}
	}

	public synchronized void stopSchedule(){
		if (m_timer!=null) m_timer.stopSchedule();
	}

	//@Override
	public synchronized String updateSchedule(Boolean useRemoteLicenceManagers, Integer retryInterval, Integer retryNumber, Integer updateInterval, Integer checkLicenceInterval, Boolean useLicenceRequestMetadata) {
		boolean justlist=true;
		String msg="";
		if(retryInterval!=null) {
			justlist=false;
			this.m_retryInterval = retryInterval;
		}
		if(retryNumber!=null) {
			justlist=false;
			this.m_retryNumber = retryNumber;
		}
		if(updateInterval!=null) {
			justlist=false;
			this.m_updateInterval = updateInterval;
		}
		if(useRemoteLicenceManagers!=null) {
			justlist=false;
			this.m_useRemoteLicenceManagers=useRemoteLicenceManagers;
		}
		if(checkLicenceInterval!=null) {
			justlist=false;
			this.m_checkLicenceInterval=checkLicenceInterval;
		}
		if(useLicenceRequestMetadata!=null) {
			justlist=false;
			this.m_useLicenceRequestMetadata=useLicenceRequestMetadata;
		}


		if(!justlist){
			msg="Restarting schedule with new configuration.\n";
			restartSchedule();
		}

		if(m_timer.getScheduleIsRunning()){
			msg=msg+"Schedule Running\n";
		} else msg=msg+"Schedule Stopped\n";

		msg=msg+"Schedule configuration = useRemoteLicenceManagers="+ this.m_useRemoteLicenceManagers
				+", useLicenceRequestMetadata="+this.m_useLicenceRequestMetadata
				+ ", retryInterval="+this.m_retryInterval
				+", retryNumber="+this.m_retryNumber
				+", updateInterval="+this.m_updateInterval;

		return msg;

	}

	public synchronized String persistConfiguration() {
		if(m_configurationAdmin==null) throw new RuntimeException("m_configurationAdmin cannot be null");

		try {
			Configuration config = m_configurationAdmin.getConfiguration(PERSISTANT_ID);

			@SuppressWarnings("unchecked")
			Dictionary<String, Object> props = config.getProperties();

			// if null, the configuration is new
			if (props == null) {
				props = new Hashtable<String, Object>();
			}

			props.put(USE_REMOTE_LICENCE_MANAGERS_KEY,Boolean.toString(m_useRemoteLicenceManagers));
			props.put(REMOTE_LICENCE_MANAGERS_URLS_KEY,listToStringCsvProperty(m_remoteLicenceMgrs));
			props.put(REMOTE_LICENCE_MANAGERS_USERNAME_KEY,(m_remoteLicenceManagerUserName==null) ? "" : m_remoteLicenceManagerUserName);
			props.put(REMOTE_LICENCE_MANAGERS_PASSWORD_KEY,(m_remoteLicenceManagerPassword==null) ? "" : m_remoteLicenceManagerPassword);
			props.put(RETRY_INTERVAL_KEY,Integer.toString(m_retryInterval));
			props.put(RETRY_NUMBER_KEY,Integer.toString(m_retryNumber));
			props.put(UPDATE_INTERVAL_KEY,Integer.toString(m_updateInterval));
			props.put(CHECK_LICENCE_INTERVAL_KEY,Integer.toString(m_checkLicenceInterval));
			props.put(USE_LICENCE_REQUEST_METADATA_KEY,Boolean.toString(m_useLicenceRequestMetadata));

			StringBuffer msg = new StringBuffer("Persisted configuration:\n");
			Enumeration<String> e = props.keys();
			while(e.hasMoreElements()){
				String key = e.nextElement();
				String value = (String) props.get(key);
				msg.append("    "+key+"="+value+"\n");
			}

			config.update(props);

			LOG.info(msg.toString());
			return msg.toString();
		} catch (Exception e) {
			throw new RuntimeException("problem updating configuration in "+PERSISTANT_ID+".cfg",e);
		}
	}


	// helper methods

	private Set<String> stringCsvPropertyToList(String setStringStr){
		Set<String> setString= new LinkedHashSet<String>();
		if ((setStringStr!=null) & (! "".equals(setStringStr)) ) {
			String[] stringArray = setStringStr.split(",");

			for (String str: stringArray){
				str.trim();
				if (! "".equals(str)){
					setString.add(str);
				} 
			}
		}
		return setString;
	}

	private String listToStringCsvProperty(Set<String> setString){

		StringBuffer sb=new StringBuffer();

		Iterator<String> itr = setString.iterator();
		while(itr.hasNext()){
			sb.append(itr.next());
			if (itr.hasNext()) sb.append(",");
		}

		return sb.toString();
	}

	/** 
	 * loads licence metadata file.
	 * returns null if file not found  throws exception if cannot parse fule
	 */
	public synchronized LicenceMetadataList loadLicenceMetadataListFile(){

		if (m_licenceRequestMetadataFile==null) throw new RuntimeException("fileUri must be set for readLicenceMetadataFile");

		LicenceMetadataList licenceMetadata =null;
		try {

			File licenceMetadataFile = new File(m_licenceRequestMetadataFile);
			LOG.debug("reading licenceMetadataFile:"+licenceMetadataFile.getAbsolutePath());

			if (licenceMetadataFile.exists()) {
				JAXBContext jaxbContext = JAXBContext.newInstance(LicenceMetadataList.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				licenceMetadata = (LicenceMetadataList) jaxbUnmarshaller.unmarshal(licenceMetadataFile);

				System.out.println("Licence Manager successfully loaded request licence metadata from file="+licenceMetadataFile.getAbsolutePath());
				LOG.info("Licence Manager successfully loaded request licence metadata from file="+licenceMetadataFile.getAbsolutePath());
			} else {
				System.out.println("Licence Manager licence file="+licenceMetadataFile.getAbsolutePath()+" does not exist.");
				LOG.info("Licence Manager licence file="+licenceMetadataFile.getAbsolutePath()+" does not exist.");
			}
			return licenceMetadata;

		} catch (JAXBException e) {
			LOG.error("Licence Manager Problem loading licence metadata: "+ e.getMessage());
			throw new RuntimeException("Problem loading licence metadata",e);
		}
	}

	/**
	 * Saves licence metadata file.
	 * @param fileUri
	 * @param licenceMetadataList
	 */
	public synchronized void saveLicenceMetadataListFile(LicenceMetadataList licenceMetadataList){
		if (m_licenceRequestMetadataFile==null) throw new RuntimeException("m_licenceRequestMetadataFile must be set ");
		if (licenceMetadataList==null) throw new RuntimeException("licenceMetadataList must be set");

		try {

			File licenceMetadataFile = new File(m_licenceRequestMetadataFile);
			LOG.debug("writing licenceMetadataFile:"+licenceMetadataFile.getAbsolutePath());

			JAXBContext jaxbContext = JAXBContext.newInstance(LicenceMetadataList.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(licenceMetadataList, licenceMetadataFile);

		} catch (JAXBException e) {
			throw new RuntimeException("Problem persisting Licence Metadata Data",e);
		}
	}




}
