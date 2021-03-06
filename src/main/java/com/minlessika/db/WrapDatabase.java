/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 Olivier B. OURA
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.minlessika.db;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * Wrapper for database
 * 
 * @author Olivier B. OURA
 *
 */
public abstract class WrapDatabase implements Database {

	/**
	 * Database wrapped
	 */
	protected final Database origin;
	
	/**
	 * Ctor.
	 * @param origin Database wrapped
	 */
	public WrapDatabase(final Database origin) {
		this.origin = origin;
	}
	
	@Override
	public void start() {
		origin.start();
	}

	@Override
	public void startTransaction() {
		origin.startTransaction();
	}

	@Override
	public void commit() {
		origin.commit();
	}

	@Override
	public void rollback() {
		origin.rollback();
	}

	@Override
	public void terminateTransaction() {
		origin.terminateTransaction();
	}

	@Override
	public boolean transactionStarted() {
		return origin.transactionStarted();
	}

	@Override
	public Connection getConnection() throws SQLException {
		return origin.getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return origin.getConnection(username, password);
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return origin.getLogWriter();
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		origin.setLogWriter(out);
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		origin.setLoginTimeout(seconds);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return origin.getLoginTimeout();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return origin.getParentLogger();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return origin.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return origin.isWrapperFor(iface);
	}

}
