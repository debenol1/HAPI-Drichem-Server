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

import org.apache.log4j.Logger;

import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.ConnectionListener;

public class DrichemConnectionListener implements ConnectionListener {

	private final static String CONNECTION_RECEIVED = "New connection received: ";
	private final static String CONNECTION_LOST = "Lost connection from: ";

	private static Logger logger = Logger.getLogger(DrichemConnectionListener.class);

	@Override
	public void connectionReceived(Connection connection) {
		logger.info(CONNECTION_RECEIVED + connection.getRemoteAddress().toString());
	}

	@Override
	public void connectionDiscarded(Connection connection) {
		logger.info(CONNECTION_LOST + connection.getRemoteAddress().toString());
	}
}
