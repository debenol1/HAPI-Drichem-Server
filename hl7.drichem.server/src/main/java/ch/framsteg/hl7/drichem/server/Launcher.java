/*-
 * =====LICENSE-START=====
 * Java 11 Application
 * ------
 * Copyright (C) 2020 - 2022 Framsteg GmbH
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
package ch.framsteg.hl7.drichem.server;

import java.util.Properties;

import ch.framsteg.hl7.drichem.server.elexis.DrichemMessageReceiver;
import ch.framsteg.hl7.drichem.server.elexis.properties.PropertiesLoader;
import ch.framsteg.hl7.drichem.server.elexis.properties.PropertiesValidator;
import ch.framsteg.hl7.drichem.server.interfaces.Loader;
import ch.framsteg.hl7.drichem.server.interfaces.Validator;

public class Launcher {

	private final static String ERR_PROPERTIES_INVALID = "Properties invalid";
	private final static String ERR_PARAMETERS_MISSING = "Missing parameter";

	public static void main(String[] args) {

		try {
			if (args.length > 0) {
				Loader<Properties> loader = new PropertiesLoader();
				Properties properties = loader.load(args[0]);
				Validator<Properties> validator = new PropertiesValidator();
				if (validator.validate(properties)) {
					DrichemMessageReceiver drichemMessageReceiver = new DrichemMessageReceiver(properties);
					drichemMessageReceiver.start();
				} else {
					// Properties invalid
					System.out.println(ERR_PROPERTIES_INVALID);
				}
			} else {
				// Parameter not entered
				System.out.println(ERR_PARAMETERS_MISSING);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
