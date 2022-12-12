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
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/* Extracts the patID. Fujuitsu Drichem stores the patID within the SPM segment */
public class IdExtractor {
	
	private final static String PID_IDENTIFICATION = "Identify position to insert PID segment just after ";
	private final static String SEGMENT_ID = "spm.segment.id";
	private final static String TOKENIZE_MSG = "Tokenize message";
	private final static String TOKENIZE_MSG_SEG = "Tokenize message segments to get the patient ID";
	private final static String COL = "column";
	private final static String TOKEN = "Token: ";
	private final static String RETURN_ID = "Return id=";
	
	private final char CARRIAGE_RETURN = 13;
	private final String SEGMENT_FIELD_DELIMITER = "|";
	private final String MESSAGE_FIELD_DELIMITER = Character.toString(CARRIAGE_RETURN);
	private String message;
	private Properties properties;
	private static Logger logger = Logger.getLogger(IdExtractor.class);

	public IdExtractor(String message, Properties properties) {
		this.message = message;
		this.properties = properties;
	}

	/* Extracts the patientID from the HL7 message to resolve the name and firstname */
	public String extract() {
		logger.info(PID_IDENTIFICATION + properties.getProperty(SEGMENT_ID));
		StringTokenizer messageTokenizer = new StringTokenizer(message, MESSAGE_FIELD_DELIMITER);
		String segment = new String();
		logger.info(TOKENIZE_MSG);
		
		// Iterates over the message segments until the SPM segment is found
		while (messageTokenizer.hasMoreElements()) {
			String token = messageTokenizer.nextToken();						
			if (token.startsWith(properties.getProperty(SEGMENT_ID))) {
				segment = token;
				break;
			}
		}
		StringTokenizer segmentTokenizer = new StringTokenizer(segment, SEGMENT_FIELD_DELIMITER);
		String id = new String();

		int counter = 1;
		logger.info(TOKENIZE_MSG_SEG);
		// Iterates over the segment element until the third column is found
		while (segmentTokenizer.hasMoreElements()) {
			String token = segmentTokenizer.nextToken();
			if (counter == Integer.parseInt(properties.getProperty(COL))) {
				logger.info(TOKEN + token);
				id = token;
			}
			counter++;
		}
		logger.info(RETURN_ID + id);
		return id;
	}
}
