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
package ch.framsteg.hl7.drichem.server.elexis.properties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.log4j.Logger;

import ch.framsteg.hl7.drichem.server.interfaces.Loader;

public class PropertiesLoader implements Loader<Properties> {

	private final static String PATH_EXISTS = " exists";
	private final static String REGULAR_FILE = " ist regular file";
	private final static String READABLE_FILE = " is readable file";
	private final static String LOAD_PROPERTIES_FILE = " load properties file";
	private final static String NOT_READABLE_FILE = " is not readable";
	private final static String NOT_REGULAR_FILE = " is not regular file";
	private final static String NOT_EXISTING_FILE = " does not exist";

	private static Logger logger = Logger.getLogger(PropertiesLoader.class);

	@Override
	public Properties load(String path) throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		if (Files.exists(Paths.get(path))) {
			logger.info(path + PATH_EXISTS);
			if (Files.isRegularFile(Paths.get(path))) {
				logger.info(path + REGULAR_FILE);
				if (Files.isReadable(Paths.get(path))) {
					logger.info(path + READABLE_FILE);
					logger.info(LOAD_PROPERTIES_FILE + path);
					properties.load(new FileInputStream(path));
				} else {
					logger.error(path + NOT_READABLE_FILE);
				}
			} else {
				logger.error(path + NOT_REGULAR_FILE);
			}
		} else {
			logger.error(path + NOT_EXISTING_FILE);
		}
		return properties;
	}
}
