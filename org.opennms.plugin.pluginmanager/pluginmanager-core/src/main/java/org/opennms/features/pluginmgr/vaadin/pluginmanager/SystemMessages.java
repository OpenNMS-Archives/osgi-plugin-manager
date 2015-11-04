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

import com.vaadin.ui.TextArea;

/**
 * used to manage user feedback and logging messages
 * @author admin
 *
 */
public class SystemMessages {

	private String message="";
	private TextArea messagePanel=null;
	
	public synchronized void setValue(String message){
		this.message=message;
		if (messagePanel!= null)  messagePanel.setValue(message);
	}

	/**
	 * @return the message
	 */
	public synchronized String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public synchronized void setMessage(String message) {
		this.message = message;
		if (messagePanel!= null)  messagePanel.setValue(message);
	}

	/**
	 * @return the messagePanel
	 */
	public synchronized TextArea getMessagePanel() {
		return messagePanel;
	}

	/**
	 * @param messagePanel the messagePanel to set
	 */
	public synchronized void setMessageTextArea(TextArea messagePanel) {
		this.messagePanel = messagePanel;
	}
}
