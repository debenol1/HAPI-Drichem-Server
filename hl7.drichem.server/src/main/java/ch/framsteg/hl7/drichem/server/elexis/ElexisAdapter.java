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
package ch.framsteg.hl7.drichem.server.elexis;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;
import ch.framsteg.hl7.drichem.server.identification.IdExtractor;
import ch.framsteg.hl7.drichem.server.identification.LaborIdentifier;
import ch.framsteg.hl7.drichem.server.identification.PatientIdentifier;
import ch.framsteg.hl7.drichem.server.persistence.FileWriter;

public class ElexisAdapter implements ReceivingApplication {

	private final static String RECEIVED_MESSAGE = "Received message:\\n";
	private final static String NEWLINE = "\n\n";
	private final static String EXTRACTOR_CREATED = "IdExtractor created";
	private final static String LAB_IDENTIFIFER_CREATED = "LaborIdentifier created";
	private final static String PAT_IDENTIFIFER_CREATED = "PatientIdentifier created";
	private final static String FILE_WRITER_CREATED = "FileWriter created";
	private final static String FILE_WRITTEN = "File written to disc";
	private final static String ACK_CREATED = "ACK created";
	private final static String EXCEPTION = "Exception occured: ";

	private Properties properties;

	private static Logger logger = Logger.getLogger(ElexisAdapter.class);

	public ElexisAdapter(Properties properties) {
		this.properties = properties;
	}

	@Override
	public Message processMessage(Message message, Map<String, Object> metadata)
			throws ReceivingApplicationException, HL7Exception {

		try (DefaultHapiContext defaultHapiContext = new DefaultHapiContext()) {
			String encodedMessage = defaultHapiContext.getPipeParser().encode(message);
			logger.info(RECEIVED_MESSAGE + encodedMessage + NEWLINE);
			// patID is being extracted from the HL7 message
			IdExtractor idExtractor = new IdExtractor(encodedMessage, properties);
			logger.info(EXTRACTOR_CREATED);
			// labID is being extracted from the HL7 message
			LaborIdentifier laborIdentifier = new LaborIdentifier(properties);
			logger.info(LAB_IDENTIFIFER_CREATED);
			// Patient is being resolved
			PatientIdentifier patientIdentifier = new PatientIdentifier(properties);
			logger.info(PAT_IDENTIFIFER_CREATED);
			String id = idExtractor.extract();
			// The HL7 message is being written to the filesystem. The filename starts with the patID
			FileWriter fileWriter = new FileWriter(properties,
					patientIdentifier.identify(laborIdentifier.identify(encodedMessage), id), id);
			logger.info(FILE_WRITER_CREATED);
			fileWriter.write();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info(FILE_WRITTEN);
		Message ACK = null;
		try {
			ACK = message.generateACK();
			logger.info(ACK_CREATED);
			logger.info(ACK);
		} catch (HL7Exception | IOException e) {
			logger.error(EXCEPTION, e);
		} 
		return ACK;
	}

	@Override
	public boolean canProcess(Message theMessage) {
		return true;
	}
}
