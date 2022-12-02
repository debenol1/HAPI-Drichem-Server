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

	private Properties properties;
	private static Logger logger = Logger.getLogger(ElexisAdapter.class);

	public ElexisAdapter(Properties properties) {
		this.properties = properties;
	}

	@Override
	public Message processMessage(Message message, Map<String, Object> metadata)
			throws ReceivingApplicationException, HL7Exception {

		String encodedMessage = new DefaultHapiContext().getPipeParser().encode(message);
		logger.info("Received message:\n" + encodedMessage + "\n\n");

		IdExtractor idExtractor = new IdExtractor(encodedMessage, properties);
		logger.info("IdExtractor created");
		LaborIdentifier laborIdentifier = new LaborIdentifier(properties);
		logger.info("LaborIdentifier created");
		PatientIdentifier patientIdentifier = new PatientIdentifier(properties);
		logger.info("PatientIdentifier created");
		String id = idExtractor.extract();
		FileWriter fileWriter = new FileWriter(properties,
				patientIdentifier.identify(laborIdentifier.identify(encodedMessage), id), id);
		logger.info("FileWriter created");
		fileWriter.write();
		logger.info("File written to disc");
		Message ACK = null;
		try {
			ACK = message.generateACK();
			logger.info("ACK created");
			logger.info(ACK);
		} catch (HL7Exception e) {
			logger.error("Error",e);
		} catch (IOException e) {
			logger.error("Error",e);
		}
		return ACK;
	}

	@Override
	public boolean canProcess(Message theMessage) {
		return true;
	}
}
