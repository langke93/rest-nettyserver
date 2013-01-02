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

package org.langke.util.logging.jdk;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.langke.util.logging.support.AbstractESLogger;


/**
 * @author kimchy (shay.banon)
 */
public class JdkESLogger extends AbstractESLogger {

	private final Logger logger;

	public JdkESLogger(String prefix, Logger logger) {
		super(prefix);
		this.logger = logger;
	}

	public String getName() {
		return logger.getName();
	}

	public boolean isTraceEnabled() {
		return logger.isLoggable(Level.FINEST);
	}

	public boolean isDebugEnabled() {
		return logger.isLoggable(Level.FINE);
	}

	public boolean isInfoEnabled() {
		return logger.isLoggable(Level.INFO);
	}

	public boolean isWarnEnabled() {
		return logger.isLoggable(Level.WARNING);
	}

	public boolean isErrorEnabled() {
		return logger.isLoggable(Level.SEVERE);
	}

	protected void internalTrace(String msg) {
		logger.log(Level.FINEST, msg);
	}

	protected void internalTrace(String msg, Throwable cause) {
		logger.log(Level.FINEST, msg, cause);
	}

	protected void internalDebug(String msg) {
		logger.log(Level.FINE, msg);
	}

	protected void internalDebug(String msg, Throwable cause) {
		logger.log(Level.FINE, msg, cause);
	}

	protected void internalInfo(String msg) {
		logger.log(Level.INFO, msg);
	}

	protected void internalInfo(String msg, Throwable cause) {
		logger.log(Level.INFO, msg, cause);
	}

	protected void internalWarn(String msg) {
		logger.log(Level.WARNING, msg);
	}

	protected void internalWarn(String msg, Throwable cause) {
		logger.log(Level.WARNING, msg, cause);
	}

	protected void internalError(String msg) {
		logger.log(Level.SEVERE, msg);
	}

	protected void internalError(String msg, Throwable cause) {
		logger.log(Level.SEVERE, msg, cause);
	}
}
