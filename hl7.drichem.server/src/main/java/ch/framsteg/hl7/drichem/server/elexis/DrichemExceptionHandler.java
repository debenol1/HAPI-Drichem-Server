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

import java.util.Map;

import org.apache.log4j.Logger;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.protocol.ReceivingApplicationExceptionHandler;

public class DrichemExceptionHandler implements ReceivingApplicationExceptionHandler {
	
	private static Logger logger = Logger.getLogger(DrichemExceptionHandler.class);

	/**
	 * Process an exception.
	 * 
	 * @param theIncomingMessage  the incoming message. This is the raw message
	 *                            which was received from the external system
	 * @param theIncomingMetadata Any metadata that accompanies the incoming
	 *                            message. See
	 *                            {@link ca.uhn.hl7v2.protocol.Transportable#getMetadata()}
	 * @param theOutgoingMessage  the outgoing message. The response NAK message
	 *                            generated by HAPI.
	 * @param theE                the exception which was received
	 * @return The new outgoing message. This can be set to the value provided by
	 *         HAPI in <code>outgoingMessage</code>, or may be replaced with another
	 *         message. <b>This method may not return <code>null</code></b>.
	 */
	@Override
	public String processException(String incomingMessage, Map<String, Object> incomingMetadata, String outgoingMessage,
			Exception e) throws HL7Exception {

		logger.info(incomingMessage);
		logger.info(outgoingMessage);
		
		return outgoingMessage;
	}
}
