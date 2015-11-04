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

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * used to generate error response messages
 * error handling suggestion taken from http://www.codingpedia.org/ama/error-handling-in-rest-api-with-jersey/
 * @author cgallen@opennms.org
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlType (propOrder={"status","message","code","link","developerMessage"})
public class ErrorMessage {
	


	/**
	 * Helper constructor to build an error reply 
	 * (Error handling suggestion taken from http://www.codingpedia.org/ama/error-handling-in-rest-api-with-jersey/)
	 * @param status holds redundantly the HTTP error status code, so that the developer can â€œseeâ€� 
	 *        it without having to analyze the responseâ€™s header
	 * @param code this is an internal code specific to the API (should be more relevant for business exceptions)
	 * @param message short description of the error, what might have cause it and possibly a â€œfixingâ€� proposal
	 * @param link points to an online resource, where more details can be found about the error
	 * @param developerMessage detailed message, containing additional data that might be relevant to the developer. 
	 *       This should only be available when the â€œdebugâ€� mode is 
	 *       switched on and could potentially contain stack trace information or something similar
	 * @return EventGatewayErrorMessage jaxb object to include in the xml reply
	 */
	public ErrorMessage(int status,int code, String message, String link, String developerMessage)	{
		super();
		this.status=status;
		this.code=code;
		this.message=message;
		this.link=link;
		this.developerMessage=developerMessage;
	}
	
	/**
	 * Helper constructor to build an error reply 
	 * (Error handling suggestion taken from http://www.codingpedia.org/ama/error-handling-in-rest-api-with-jersey/)
	 * @param status holds redundantly the HTTP error status code, so that the developer can â€œseeâ€� 
	 *        it without having to analyze the responseâ€™s header
	 * @param code this is an internal code specific to the API (should be more relevant for business exceptions)
	 * @param message short description of the error, what might have cause it and possibly a â€œfixingâ€� proposal
	 * @param link points to an online resource, where more details can be found about the error
	 * @param exception is converted to stacktrace and appended as developer message
	 * @return EventGatewayErrorMessage jaxb object to include in the xml reply
	 */
	public ErrorMessage(int status,int code, String message, String link, Exception exception)	{
		super();
        this.status=status;
		this.code=code;
		this.message=message;
		this.link=link;
		
		// generate stacktrace for Exception
		if (exception!=null){
			StringWriter sw = new StringWriter();
			sw.append("Reported Exception:");
			exception.printStackTrace(new PrintWriter(sw));
			this.developerMessage = sw.toString();
		}
	}

	public ErrorMessage() {
		super();
	}

	/** contains the same HTTP Status code returned by the server */
	private int status;

	/** application specific error code */
	private int code;

	/** message describing the error*/
	private String message;

	/** link point to page where the error message is documented */
	private String link;

	/** extra information that might useful for developers */
	private String developerMessage;

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	
	/**
	 * @param status contains the same HTTP Status code returned by the server 
	 */
	@XmlElement(name = "status")
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}
	
	/**
	 * @param code application specific error code
	 */
	@XmlElement(name = "code")
	public void setCode(int code) {
		this.code = code;
	}
	
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * @param  message describing the error
	 */
	@XmlElement(name = "message")
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}
	
	/**
	 * @param link points to page where the error message is documented 
	 */
	@XmlElement(name = "link")
	public void setLink(String link) {
		this.link = link;
	}
	/**
	 * @return the developerMessage
	 */
	public String getDeveloperMessage() {
		return developerMessage;
	}
	
	/**
	 * @param developerMessage extra information that might useful for developers
	 */
	@XmlElement(name = "developerMessage")
	public void setDeveloperMessage(String developerMessage) {
		this.developerMessage = developerMessage;
	}

}