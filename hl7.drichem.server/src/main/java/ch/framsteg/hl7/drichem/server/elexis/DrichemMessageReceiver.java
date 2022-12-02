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

import java.util.Properties;

import org.apache.log4j.Logger;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.HL7Service;
import ca.uhn.hl7v2.protocol.ReceivingApplication;

public class DrichemMessageReceiver {

	private Properties properties;

	private static Logger logger = Logger.getLogger(DrichemMessageReceiver.class);

	public DrichemMessageReceiver(Properties properties) {
		this.properties = properties;
	}

	public void start() {
		int port = Integer.parseInt(properties.getProperty("port"));
		boolean useTls = false;

		HapiContext context = new DefaultHapiContext();
		logger.info("Creating context");
		HL7Service server = context.newServer(port, useTls);
		logger.info("Creating server");
		ReceivingApplication handler = new ElexisAdapter(properties);
		logger.info("Creating receiving application");
		server.registerApplication(properties.getProperty("app.messageType"),
				properties.getProperty("app.triggerEvent"), handler);
		logger.info("Registering OUL R22 application");
		server.registerConnectionListener(new DrichemConnectionListener());
		logger.info("Registering connection DrichemConnectionListener()");
		server.setExceptionHandler(new DrichemExceptionHandler());
		logger.info("Setting DrichemExceptionHandler");
		try {
			server.startAndWait();
			logger.info("HAPI Drichem Server started and waiting for requests...");
		} catch (InterruptedException e) {
			logger.error("Error", e);
		}
	}
}
