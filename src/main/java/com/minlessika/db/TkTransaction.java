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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import com.minlessika.exceptions.DatabaseException;

/**
 * Take that keeps all database operations made in another Take within a transaction.
 *
 * <p>The class is immutable and thread-safe.</p>
 *
 * @author Olivier B. OURA (baudoliver7@gmail.com)
 */
public class TkTransaction implements Take {

	private static Logger logger = LoggerFactory.getLogger(TkTransaction.class);
	
	/**
	 * Database
	 */
	protected final Database database;
	
	/**
	 * Take to put within transaction
	 */
	private final Take origin;
	
	/**
	 * 
	 * @param origin
	 * @param database
	 */
	public TkTransaction(final Take origin, final Database database) {
		this.origin = origin;
		this.database = database;
	}
	
	@Override
	public Response act(Request req) throws Exception {
		
		database.startTransaction();
		
		try {
			final Response response = origin.act(req);			
			database.commit();
			return response;
		} catch (DatabaseException e) {
			database.rollback();
			logger.error(e.getLocalizedMessage(), e); 
			throw e;
		} finally {
			database.terminateTransaction();
		}		
	}

}
