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

import com.minlessika.exceptions.DatabaseException;

import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

/**
 * Database decorator to apply database schema updates from liquibase files at starting
 * 
 * @author Olivier B. OURA (baudolivier.oura@gmail.com)
 *
 */
public final class DatabaseLiquibaseUpdate extends WrapDatabase implements Database {
	
	private final String changeLogRelativePath;
	
	/**
	 * Ctor.
	 * @param origin Original database
	 * @param changeLogRelativePath Path relative to resources path
	 */
	public DatabaseLiquibaseUpdate(final Database origin, final String changeLogRelativePath) {
		super(origin);
		
		this.changeLogRelativePath = changeLogRelativePath;
	}
	
	@Override
	public void start() {
		    	
		try (
        		final Connection connection = getConnection()
        ) {      	
        	       	
        	final liquibase.database.Database database = DatabaseFactory.getInstance()
																		 .findCorrectDatabaseImplementation(
																			 new JdbcConnection(connection)
																		 );       
        	
        	final Liquibase liquibase = new Liquibase(
        									changeLogRelativePath, 
											new ClassLoaderResourceAccessor(), 
											database
										);
        	
        	liquibase.update("");   
        	
        } catch (Exception e) {
			throw new DatabaseException(e);
		}	
		
		super.start();
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

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return origin.getConnection(username, password);
	}

}
