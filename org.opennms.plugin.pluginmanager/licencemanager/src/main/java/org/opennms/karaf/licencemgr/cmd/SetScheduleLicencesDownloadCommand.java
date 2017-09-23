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

package org.opennms.karaf.licencemgr.cmd;

import jline.internal.Log;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.opennms.karaf.licencemgr.LicenceManagerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Command(scope = "licence-mgr", name = "scheduleLicencesDownload", description="Lists or sets parameters for schedulling automatic licence download and licence re-validation interval. With no options existing paramaters are listed.")
public class SetScheduleLicencesDownloadCommand extends OsgiCommandSupport {
	private static final Logger LOG = LoggerFactory.getLogger(SetScheduleLicencesDownloadCommand.class);

	private LicenceManagerController _licenceManagerController;

	public LicenceManagerController getLicenceManagerController() {
		return _licenceManagerController;
	}

	public void setLicenceManagerController( LicenceManagerController licenceManagerController) {
		_licenceManagerController = licenceManagerController;
	}
	
	@Option(name = "-e", aliases =  "--enableRemoteLicenceDownload", description = "(true or false) If true, will try to download licences from remote urls", required = false, multiValued = false)
	String enableRemoteLicenceDownloadStr;
	
	@Option(name = "-i", aliases =  "--retryInterval", description = "(integer ms) Interval before retrying unsuccessful download of licences ", required = false, multiValued = false)
	String retryIntervalStr;
	
	@Option(name = "-r", aliases =  "--retryNumber", description = "(integer) unsuccessful number of retrys -1 = forever until successful", required = false, multiValued = false)
	String retryNumberStr;
	
	@Option(name = "-u", aliases =  "--updateInterval", description = "(integer ms) long term update interval before attempting to reload licence. -1= only try on startup", required = false, multiValued = false)
	String updateIntervalStr;
	
	@Option(name = "-c", aliases =  "--checkLicenceInterval", description = "(integer ms) interval between rechecking validity of licences . -1= only try on startup", required = false, multiValued = false)
	String checkLicenceIntervalStr;

	@Override
	protected Object doExecute() throws Exception {
		try{
			Boolean useRemotePluginManagers =  (enableRemoteLicenceDownloadStr==null || "".equals(enableRemoteLicenceDownloadStr)) ? null : Boolean.parseBoolean(enableRemoteLicenceDownloadStr);
			Integer retryInterval= (retryIntervalStr==null || "".equals(retryIntervalStr)) ? null : Integer.parseInt(retryIntervalStr);
			Integer retryNumber= (retryNumberStr==null || "".equals(retryNumberStr)) ? null : Integer.parseInt(retryNumberStr);
			Integer updateInterval = (updateIntervalStr==null || "".equals(updateIntervalStr)) ? null : Integer.parseInt(updateIntervalStr);
			Integer checkLicenceInterval = (checkLicenceIntervalStr==null || "".equals(checkLicenceIntervalStr)) ? null : Integer.parseInt(checkLicenceIntervalStr);
			
			String schedule = getLicenceManagerController().updateSchedule(useRemotePluginManagers, retryInterval, retryNumber, updateInterval, checkLicenceInterval);

			String msg="licences schedule command result: "+schedule;
			LOG.info(msg);
			System.out.println(msg);
		} catch (Exception e) {
			System.err.println("error updating licences schedule. Exception="+e);
			LOG.error("error updating licences schedule. Exception=",e);
		}
		return null;
	}
}