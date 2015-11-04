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

import java.io.UnsupportedEncodingException;
import java.util.zip.CRC32;

public class StringCrc32Checksum {

	/**
	 * Adds a CRC32 encoded string to supplied string separated by '-'
	 * resulting in string of form 'valueString'-'CRC32 in hex'
	 * @param valueString
	 * @return original string plus checksum in form 'valueString'-'CRC32 in Hex'
	 */
	public String addCRC(String valueString){
		// make checksum
		CRC32 crc=new CRC32();
		try {
			crc.update(valueString.getBytes("UTF-8"));
		}
		catch (  UnsupportedEncodingException e) {
			throw new RuntimeException("UTF-8 encoding is not supported");
		}
		String hexcrc= Long.toHexString(crc.getValue());

		String stringPlusCrc=valueString+"-"+hexcrc;

		return stringPlusCrc;
	}

	/**
	 * Expects stringPlusCrc to be a value string separated from CRC by a '-' character.
	 * <valueString>-<CRC32 in hex>
	 * Splits the string and test that the CRC is correct for value string 
	 * @param stringPlusCrc
	 * @return true if CRC is correct
	 */
	public boolean checkCRC(String stringPlusCrc){

		//TODO remove
		//		String[] parts = stringPlusCrc.split("-");
		//		if (parts.length!=2) return false;
		//		String hexSystemIdString=parts[0];
		//		String hexcrc=parts[1];

		// allows string with dashes but last dash separates crc
		int lastdashindex = stringPlusCrc.lastIndexOf("-");
		if (lastdashindex==-1          // no dash in string or no crc <xxx>
				|| lastdashindex==0    // dash is first character in string so no string with attached crc <-xxx>
				|| lastdashindex==stringPlusCrc.length()) return false; // dash is last character so no crc <xxx->

		String valueString=stringPlusCrc.substring(0, lastdashindex);
		String hexcrc=stringPlusCrc.substring(lastdashindex+1);

		// make checksum
		CRC32 crc=new CRC32();
		try {
			crc.update(valueString.getBytes("UTF-8"));
		}
		catch (  UnsupportedEncodingException e) {
			throw new RuntimeException("UTF-8 encoding is not supported");
		}

		String checkHexCrc= Long.toHexString(crc.getValue());

		if (!checkHexCrc.equals(hexcrc)) return false;

		return true;

	}

	/**
	 * Expects stringPlusCrc to be a value string separated from CRC by a '-' character.
	 * <valueString>-<CRC32 in hex>
	 * Splits the string and test that the CRC is correct for value string 
	 * @param stringPlusCrc
	 * @return String minus crc or null if no valid crc applied
	 */
	public String removeCRC(String stringPlusCrc){

		if (checkCRC(stringPlusCrc)==false) return null;
		int lastdashindex = stringPlusCrc.lastIndexOf("-");
		return stringPlusCrc.substring(0, lastdashindex);
	}
}
