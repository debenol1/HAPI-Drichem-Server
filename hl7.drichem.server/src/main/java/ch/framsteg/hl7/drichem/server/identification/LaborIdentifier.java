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

import org.apache.log4j.Logger;

public class LaborIdentifier {
	private final String SEGMENT_FIELD_DELIMITER = "|";

	private Properties properties;
	private static Logger logger = Logger.getLogger(LaborIdentifier.class);

	public LaborIdentifier(Properties properties) {
		this.properties = properties;
	}

	// Add the lab ID
	public String identify(String message) {
		logger.info("Identify position to insert lab ID");
		int position = ordinalIndexOf(message, SEGMENT_FIELD_DELIMITER, 3);
		logger.info("Found position at " + position);
		StringBuilder stringBuilder = new StringBuilder(message);
		stringBuilder.insert(position, properties.getProperty("labor.id"));
		logger.info("Inserted lab ID " + properties.getProperty("labor.id"));
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
