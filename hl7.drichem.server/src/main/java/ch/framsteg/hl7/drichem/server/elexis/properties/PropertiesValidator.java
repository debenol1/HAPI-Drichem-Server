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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.log4j.Logger;

import ch.framsteg.hl7.drichem.server.interfaces.Validator;

public class PropertiesValidator  implements Validator<Properties> {

	private final static String PORT = "port";
	private final static String VALID = " is valid ";
	private final static String OUTPUT = "output.dir";
	private final static String PATH_EXISTS = " exists";
	private final static String DIRECTORY = " is directory";
	private final static String READABLE = " is readable";
	private final static String NOT_READABLE = " is not readable";
	private final static String NOT_DIRECTORY = " is not a directory";
	private final static String NOT_EXISTING = " does not exist";
	
	private static Logger logger = Logger.getLogger(PropertiesValidator.class);

	@Override
	public boolean validate(Properties properties) {
		boolean validPort = false;
		boolean validOutput = false;

		int port = Integer.parseInt(properties.getProperty(PORT));
		validPort = (port > 100 && port < 65535) ? true : false;
		logger.info(port + VALID + validPort);
		validOutput = validate(properties.getProperty(OUTPUT));

		return validPort && validOutput;
	}

	private boolean validate(String path) {
		boolean valid = false;
		if (Files.exists(Paths.get(path))) {
			logger.info(path + PATH_EXISTS);
			if (Files.isDirectory(Paths.get(path))) {
				logger.info(path + DIRECTORY);
				if (Files.isReadable(Paths.get(path))) {
					logger.info(path + READABLE);
					valid = true;
				} else {
					logger.error(path + NOT_READABLE);
				}
			} else {
				logger.error(path + NOT_DIRECTORY);
			}
		} else {
			logger.error(path + NOT_EXISTING);
		}
		return valid;
	}
}
