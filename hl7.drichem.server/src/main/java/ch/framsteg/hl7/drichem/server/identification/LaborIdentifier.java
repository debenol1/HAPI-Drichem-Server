/*******************************************************************************
 * Copyright (c) 2020-2022,  Olivier Debenath
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Olivier Debenath <olivier@debenath.ch> - initial implementation
 *    
 *******************************************************************************/
package ch.framsteg.hl7.drichem.server.identification;

import java.util.Properties;

import org.apache.log4j.Logger;


public class LaborIdentifier {
	
	private final static String ID_POSITION = "Identify position to insert lab ID";
	private final static String POSITION_FOUND = "Found position at ";
	private final static String LAB_ID = "labor.id";
	private final static String LAB_ID_INSERTED = "Inserted lab ID ";
	
	private final String SEGMENT_FIELD_DELIMITER = "|";

	private Properties properties;
	private static Logger logger = Logger.getLogger(LaborIdentifier.class);

	public LaborIdentifier(Properties properties) {
		this.properties = properties;
	}

	// Add the lab ID
	public String identify(String message) {
		logger.info(ID_POSITION);
		int position = ordinalIndexOf(message, SEGMENT_FIELD_DELIMITER, 3);
		logger.info(POSITION_FOUND + position);
		StringBuilder stringBuilder = new StringBuilder(message);
		stringBuilder.insert(position, properties.getProperty(LAB_ID));
		logger.info(LAB_ID_INSERTED + properties.getProperty(LAB_ID));
		return stringBuilder.toString();
	}

	// Identify position after the 3. |
	private int ordinalIndexOf(String str, String substr, int n) {
		int pos = -1;
		do {
			pos = str.indexOf(substr, pos + 1);
		} while (n-- > 0 && pos != -1);
		return pos;
	}
}
