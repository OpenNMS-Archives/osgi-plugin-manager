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

package org.opennms.features.pluginmgr.vaadin.pluginmanager;

import org.opennms.features.pluginmgr.SimpleStackTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.v7.ui.TextArea;

/**
 * used to manage user feedback and logging messages
 * @author admin
 *
 */
public class SystemMessages {
	private static final Logger LOG = LoggerFactory.getLogger(SystemMessages.class);

	private String message="";
	private String longMessage="";
	private TextArea messagePanel=null;

	//TODO REMOVE
//	public synchronized void setValue(String message){
//		this.message=message;
//		if (messagePanel!= null)  messagePanel.setValue(message);
//	}

	/**
	 * @return the messagePanel
	 */
	//public synchronized TextArea getMessagePanel() {
	//	return messagePanel;
	//}

	/**
	 * @param messagePanel the messagePanel to set
	 */
	public synchronized void setMessageTextArea(TextArea messagePanel) {
		this.messagePanel = messagePanel;
	}
	
	/**
	 * Clears System Messages Panel
	 * Does not log any messages
	 */
	public synchronized void clear(){
		setMessage("");
		setLongMessage("");
	}
	
	/**
	 * Writes simple message to message panel
	 * logs message to LOG.info
	 * @param msg
	 */
	public synchronized void info(String msg){
		LOG.info(msg);
		setMessage(msg);
	}
	
	/** 
	 * Writes simple error message to message panel
	 * Logs message to LOG.error
	 * 
	 * @param msg
	 */
	public synchronized void error(String msg){
		LOG.error(msg);
		setMessage(msg);
	}
	
	/**
	 * Writes simple error message to message panel
	 * Writes full exception to long message
	 * Logs message to LOG.error
	 * @param msg
	 * @param e
	 */
	public synchronized void error(String msg, Exception e){
		LOG.error(msg, e);
		setMessage(msg);
		setLongMessage(msg+"\n"+SimpleStackTrace.errorToString(e));
	}

	/**
	 * longMessage goes in the full message popup panel
	 * If long message is empty, return short message content
	 * Does not log any messages
	 * @return the longMessage
	 */
	public synchronized  String getLongMessage() {
		if (longMessage==null || "".equals(longMessage)) longMessage=message;
		return longMessage;
	}

	/**
	 * longMessage goes in the full message popup panel
	 * Does not log any messages
	 * @param longMessage the longMessage to set
	 */
	public synchronized void setLongMessage(String longMessage) {
		this.longMessage = longMessage;
	}
	
	/**
	 * message goes in the small message panel
	 * Does not log any messages
	 * @return the message
	 */
	public synchronized String getMessage() {
		return message;
	}

	/**
	 * message goes in the small message panel
	 * Does not log any messages
	 * @param message the message to set
	 */
	public synchronized void setMessage(String message) {
		this.message = message;
		if (messagePanel!= null)  messagePanel.setValue(message);
	}

}
