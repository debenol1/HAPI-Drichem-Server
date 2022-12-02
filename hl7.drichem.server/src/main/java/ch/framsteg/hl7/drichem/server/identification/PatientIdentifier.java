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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

public class PatientIdentifier {
	private final char CARRIAGE_RETURN = 13;
	private final String MESSAGE_FIELD_DELIMITER = Character.toString(CARRIAGE_RETURN);

	private Properties properties;
	private String name;
	private String birthdate;
	private static Logger logger = Logger.getLogger(PatientIdentifier.class);

	public PatientIdentifier(Properties properties) {
		this.properties = properties;
	}

	public String identify(String message, String id) {
		logger.info("Load JDBC properties");
		String url = properties.getProperty("url");
		String user = properties.getProperty("user");
		String password = properties.getProperty("password");
		StringBuilder modifiedMessage = null;

		try {
			logger.info("Establish connection");
			Connection conn = DriverManager.getConnection(url, user, password);
			logger.info("Create SQL statement");
			Statement st = conn.createStatement();
			logger.info("SELECT bezeichnung2,bezeichnung1,geburtsdatum FROM kontakt WHERE patientnr ='" + id + "'");
			ResultSet rs = st.executeQuery(
					"SELECT bezeichnung2,bezeichnung1,geburtsdatum FROM kontakt WHERE patientnr ='" + id + "'");
			while (rs.next()) {
				// Setting verbose mode on/off
				if (properties.getProperty("verbose").equalsIgnoreCase("true")) {
					name = rs.getString(2) + "," + rs.getString(1);
				} else {
					name = rs.getString(1) + "," + rs.getString(2);
				}
				birthdate = rs.getString(3);
			}

			rs.close();
			st.close();
			logger.info("Create PID segment");
			// Creating the PID segment to be inserted
			String pidSegment = MessageFormat.format(properties.getProperty("pid"), id, name, birthdate);
			logger.info(pidSegment);
			StringTokenizer messageTokenizer = new StringTokenizer(message, MESSAGE_FIELD_DELIMITER);
			modifiedMessage = new StringBuilder();
			logger.info("Insert PID segment into message");
			// Find the position after the MSH segment to insert the PID segmen
			while (messageTokenizer.hasMoreElements()) {
				String token = messageTokenizer.nextToken();
				modifiedMessage.append(token);
				modifiedMessage.append(MESSAGE_FIELD_DELIMITER);
				if (token.startsWith(properties.getProperty("first.segment.id"))) {
					modifiedMessage.append(pidSegment);
					modifiedMessage.append(MESSAGE_FIELD_DELIMITER);
				}
			}

		} catch (SQLException e) {
			logger.error("Error", e);
		}
		logger.info(modifiedMessage.toString());
		return modifiedMessage.toString();
	}
}
