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
package ch.framsteg.hl7.drichem.server.persistence;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class FileWriter {
	
	private final static String DASH = "-";
	private final static String EXTENSION = "extension";
	private final static String OUTPUT_CREATED = "Output file created ";
	private final static String OUTPUT_DIR = "output.dir";
	private final static String BW_CREATED = "BufferedWriter created";
	private final static String MSG_WRITTEN = "Message written to file"; 
	private final static String EXCEPTION = "Exception occured: ";
	
	private Properties properties;
	private String message;
	private String id;
	private static Logger logger = Logger.getLogger(FileWriter.class);

	public FileWriter(Properties properties, String message, String id) {
		this.properties = properties;
		this.message = message;
		this.id = id;
	}

	public void write() {
		String filename = id + DASH + Long.toString(new java.util.Date().getTime())
				+ properties.getProperty(EXTENSION);
		logger.info(OUTPUT_CREATED + filename);
		try {
			BufferedWriter writer = new BufferedWriter(
					new java.io.FileWriter(properties.getProperty(OUTPUT_DIR) + filename));
			logger.info(BW_CREATED);
			writer.write(message.toString());
			logger.info(MSG_WRITTEN);
			writer.close();					
		} catch (IOException e) {
			logger.error(EXCEPTION, e);
		}
	}
}
