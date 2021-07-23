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
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.minlessika.exceptions.DatabaseException;

/**
 * A basic database
 * 
 * @author Olivier B. OURA (baudolivier.oura@gmail.com)
 */
public final class BasicDatabase implements Database {

	private static Logger logger = LoggerFactory.getLogger(BasicDatabase.class);
	
	/**
	 * Thread connection
	 */
	private static final ThreadLocal<Connection> connection = new ThreadLocal<>();
	
	/**
	 * Data source of database
	 */
	private final DataSource source;
	
	/**
	 * Ctor.
	 * @param source Data source
	 */
	public BasicDatabase(final DataSource source) {
		this.source = source;
	}
	
	@Override
	public Connection getConnection() {
		try {
			if(transactionStarted()) {
				return new ClosedShieldConnection(connection.get());		
			} else {
				return source.getConnection();
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}	
		
	}

	@Override
	public void commit() {	
		if(transactionStarted()) {
			try {
				connection.get().commit();				
			} catch (SQLException e) {
				throw new DatabaseException(e);
			}				
		}	
	}
	
	@Override
	public void rollback() {	
		if(transactionStarted()) {
			try {
				connection.get().rollback();
			} catch (SQLException e) {
				throw new DatabaseException(e);
			}				
		}
		
	}

	@Override
	public boolean transactionStarted() {
		return connection.get() != null;
	}

	@Override
	public void startTransaction() {		
		if(!transactionStarted()) {			
			try {
				final Connection newConnection = source.getConnection();
				newConnection.setAutoCommit(false);
				newConnection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
				connection.set(newConnection);
			} catch (SQLException e) {
				throw new DatabaseException(e);
			}			
		}
	}

	@Override
	public void terminateTransaction() {
		
		try {
			if(transactionStarted()) {
				final Connection conn = connection.get();
				connection.remove();
				conn.close();
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}	
	}

	@Override
	public void start() {
		logger.info("Database started !");
	}

	@Override
	public Connection getConnection(String username, String password) {
		throw new UnsupportedOperationException("BasicDatabase#getConnection"); 
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return source.getLogWriter();
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		source.setLogWriter(out);
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		source.setLoginTimeout(seconds);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return source.getLoginTimeout();
	}

	@Override
	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return source.getParentLogger();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return source.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return source.isWrapperFor(iface);
	}
}
