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
import java.util.Properties;

import org.apache.log4j.Logger;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.HL7Service;
import ca.uhn.hl7v2.protocol.ReceivingApplication;

public class DrichemMessageReceiver {

	private final static String PORT = "port";
	private final static String CREATING_CTX = "Creating context";
	private final static String CREATING_SERVER = "Creating server";
	private final static String CREATING_APPLICATION = "Creating receiving application";
	private final static String MESSAGE_TYPE = "app.messageType";
	private final static String TRIGGER_EVENT = "app.triggerEvent";
	private final static String OUL_22 = "Registering OUL R22 application";
	private final static String REGISTERING_LISTENER = "Registering connection DrichemConnectionListener()";
	private final static String SETTING_EXCEPTION_HANDLER = "Setting DrichemExceptionHandler";
	private final static String SERVER_STARTED = "HAPI Drichem Server started and waiting for requests...";
	private final static String EXCEPTION = "Exception occured: ";

	private Properties properties;

	private static Logger logger = Logger.getLogger(DrichemMessageReceiver.class);

	public DrichemMessageReceiver(Properties properties) {
		this.properties = properties;
	}

	public void start() {
		int port = Integer.parseInt(properties.getProperty(PORT));
		boolean useTls = false;

		try (HapiContext context = new DefaultHapiContext()) {
			logger.info(CREATING_CTX);
			HL7Service server = context.newServer(port, useTls);
			logger.info(CREATING_SERVER);
			ReceivingApplication handler = new ElexisAdapter(properties);
			logger.info(CREATING_APPLICATION);
			server.registerApplication(properties.getProperty(MESSAGE_TYPE), properties.getProperty(TRIGGER_EVENT),
					handler);
			logger.info(OUL_22);
			server.registerConnectionListener(new DrichemConnectionListener());
			logger.info(REGISTERING_LISTENER);
			server.setExceptionHandler(new DrichemExceptionHandler());
			logger.info(SETTING_EXCEPTION_HANDLER);
			server.startAndWait();
			logger.info(SERVER_STARTED);
		} catch (InterruptedException | IOException e) {
			logger.error(EXCEPTION, e);
			e.printStackTrace();
		}
	}
}
