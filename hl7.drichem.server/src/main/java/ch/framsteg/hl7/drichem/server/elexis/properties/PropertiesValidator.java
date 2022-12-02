/*-
 * =====LICENSE-START=====
 * Java 11 Application
 * ------
 * Copyright (C) 2020 - 2022 Organization Name
 * ------
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * =====LICENSE-END=====
 */
package ch.framsteg.hl7.drichem.server.elexis.properties;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.log4j.Logger;

import ch.framsteg.hl7.drichem.server.interfaces.Validator;

public class PropertiesValidator  implements Validator<Properties> {

	private static Logger logger = Logger.getLogger(PropertiesValidator.class);

	@Override
	public boolean validate(Properties properties) {
		boolean validPort = false;
		boolean validOutput = false;

		int port = Integer.parseInt(properties.getProperty("port"));
		validPort = (port > 100 && port < 65535) ? true : false;
		logger.info(port + " is valid " + validPort);
		validOutput = validate(properties.getProperty("output.dir"));

		return validPort && validOutput;
	}

	private boolean validate(String path) {
		boolean valid = false;
		if (Files.exists(Paths.get(path))) {
			logger.info(path + " exists");
			if (Files.isDirectory(Paths.get(path))) {
				logger.info(path + " is directory");
				if (Files.isReadable(Paths.get(path))) {
					logger.info(path + " is readable");
					valid = true;
				} else {
					logger.error(path + " is not readable");
				}
			} else {
				logger.error(path + " is not a directory");
			}
		} else {
			logger.error(path + " does not exist");
		}
		return valid;
	}
}
