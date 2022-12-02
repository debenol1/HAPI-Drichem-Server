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

	private final static String LOAD_JDBC_PROPERTIES = "Load JDBC properties";
	private final static String JDBC_URL = "url";
	private final static String JDBC_USER = "user";
	private final static String JDBC_PW = "password";
	private final static String CONN_ESTABLISHED = "Establish connection";
	private final static String SQL_STMT_CREATED = "Create SQL statement";
	private final static String SQL_STMT = "SELECT bezeichnung2,bezeichnung1,geburtsdatum FROM kontakt WHERE patientnr ='";
	private final static String QUOTE = "'";
	private final static String VERBOSE = "verbose";
	private final static String TRUE = "true";
	private final static String DELIM = ",";
	private final static String CREATE_PID = "Create PID segment";
	private final static String PID = "pid";
	private final static String INSERT_PID = "Insert PID segment into message";
	private final static String FIRST_SEGMENT_ID = "first.segment.id";
	private final static String EXCEPTION = "Exception occured: ";

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
		logger.info(LOAD_JDBC_PROPERTIES);
		String url = properties.getProperty(JDBC_URL);
		String user = properties.getProperty(JDBC_USER);
		String password = properties.getProperty(JDBC_PW);
		StringBuilder modifiedMessage = null;

		try {
			logger.info(CONN_ESTABLISHED);
			Connection conn = DriverManager.getConnection(url, user, password);
			logger.info(SQL_STMT_CREATED);
			Statement st = conn.createStatement();
			logger.info(SQL_STMT + id + QUOTE);
			ResultSet rs = st.executeQuery(SQL_STMT + id + QUOTE);
			while (rs.next()) {
				// Setting verbose mode on/off
				if (properties.getProperty(VERBOSE).equalsIgnoreCase(TRUE)) {
					name = rs.getString(2) + DELIM + rs.getString(1);
				} else {
					name = rs.getString(1) + DELIM + rs.getString(2);
				}
				birthdate = rs.getString(3);
			}

			rs.close();
			st.close();
			logger.info(CREATE_PID);
			// Creating the PID segment to be inserted
			String pidSegment = MessageFormat.format(properties.getProperty(PID), id, name, birthdate);
			logger.info(pidSegment);
			StringTokenizer messageTokenizer = new StringTokenizer(message, MESSAGE_FIELD_DELIMITER);
			modifiedMessage = new StringBuilder();
			logger.info(INSERT_PID);
			// Find the position after the MSH segment to insert the PID segmen
			while (messageTokenizer.hasMoreElements()) {
				String token = messageTokenizer.nextToken();
				modifiedMessage.append(token);
				modifiedMessage.append(MESSAGE_FIELD_DELIMITER);
				if (token.startsWith(properties.getProperty(FIRST_SEGMENT_ID))) {
					modifiedMessage.append(pidSegment);
					modifiedMessage.append(MESSAGE_FIELD_DELIMITER);
				}
			}

		} catch (SQLException e) {
			logger.error(EXCEPTION, e);
		}
		logger.info(modifiedMessage.toString());
		return modifiedMessage.toString();
	}
}
