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
package ch.framsteg.hl7.drichem.server.persistence;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class FileWriter {
	
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
		String filename = id + "-" + new Long(new java.util.Date().getTime()).toString()
				+ properties.getProperty("extension");
		logger.info("Output file created " + filename);
		try {
			BufferedWriter writer = new BufferedWriter(
					new java.io.FileWriter(properties.getProperty("output.dir") + filename));
			logger.info("BufferedWriter created");
			writer.write(message.toString());
			logger.info("Message writed to file");
			writer.close();
		} catch (IOException e) {
			logger.error("Error", e);
		}
	}
}
