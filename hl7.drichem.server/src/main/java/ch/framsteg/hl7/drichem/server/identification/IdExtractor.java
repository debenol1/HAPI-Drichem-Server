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
package ch.framsteg.hl7.drichem.server.identification;

import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

public class IdExtractor {
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

	public String extract() {
		logger.info("Identify position to insert PID segment just after " + properties.getProperty("segment.id"));
		StringTokenizer messageTokenizer = new StringTokenizer(message, MESSAGE_FIELD_DELIMITER);
		String segment = new String();
		logger.info("Tokenize message");
		while (messageTokenizer.hasMoreElements()) {
			String token = messageTokenizer.nextToken();
			if (token.startsWith(properties.getProperty("segment.id"))) {
				segment = token;
				break;
			}
		}

		StringTokenizer segmentTokenizer = new StringTokenizer(segment, SEGMENT_FIELD_DELIMITER);
		String id = new String();

		int counter = 1;
		logger.info("Tokenize message segments to get the patient ID");
		while (segmentTokenizer.hasMoreElements()) {
			String token = segmentTokenizer.nextToken();
			if (counter == Integer.parseInt(properties.getProperty("column"))) {
				logger.info("Token: " + token);
				id = token;
			}
			counter++;
		}
		logger.info("Return id=" + id);
		return id;
	}
}
