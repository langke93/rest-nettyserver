/*
 * Licensed to Elastic Search and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Elastic Search licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.langke.util.logging;


/**
 * A set of utilities around Logging.
 * 
 * @author kimchy (shay.banon)
 */
@SuppressWarnings("rawtypes")
public class Loggers {

	private static boolean consoleLoggingEnabled = true;

	public static void disableConsoleLogging() {
		consoleLoggingEnabled = false;
	}

	public static void enableConsoleLogging() {
		consoleLoggingEnabled = true;
	}

	public static boolean consoleLoggingEnabled() {
		return consoleLoggingEnabled;
	}

	public static ESLogger getLogger(ESLogger parentLogger, String s) {
		return getLogger(parentLogger.getName() + s, parentLogger.getPrefix());
	}

	public static ESLogger getLogger(String s) {
		return ESLoggerFactory.getLogger(s);
	}

	public static ESLogger getLogger(Class clazz) {
		return ESLoggerFactory.getLogger(getLoggerName(clazz));
	}

	public static ESLogger getLogger(Class clazz, String... prefixes) {
		return getLogger(getLoggerName(clazz), prefixes);
	}

	public static ESLogger getLogger(String name, String... prefixes) {
		String prefix = null;
		if (prefixes != null && prefixes.length > 0) {
			StringBuilder sb = new StringBuilder();
			for (String prefixX : prefixes) {
				if (prefixX != null) {
					sb.append("[").append(prefixX).append("]");
				}
			}
			if (sb.length() > 0) {
				sb.append(" ");
				prefix = sb.toString();
			}
		}
		return ESLoggerFactory.getLogger(prefix, getLoggerName(name));
	}

	private static String getLoggerName(Class clazz) {
		String name = clazz.getName();
		// if (name.startsWith("org.elasticsearch.")) {
		// name = Classes.getPackageName(clazz);
		// }
		return getLoggerName(name);
	}

	private static String getLoggerName(String name) {
		if (name.startsWith("org.elasticsearch.")) {
			return name.substring("org.elasticsearch.".length());
		}
		return name;
	}
}
