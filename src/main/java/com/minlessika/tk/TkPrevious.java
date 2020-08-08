/*
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
package com.minlessika.tk;

import java.net.URLDecoder;
import java.util.Iterator;

import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.cookies.RqCookies;
import org.takes.facets.cookies.RsWithCookie;
import org.takes.rs.RsRedirect;

/**
 * Take that redirects to previous URL if it's different of current
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
public final class TkPrevious implements Take {
	/**
     * Original take.
     */
    private final Take origin;

    /**
     * Ctor.
     * @param take Original take
     */
    public TkPrevious(final Take take) {
    	this.origin = take;
    }

    @Override
    public Response act(final Request req) throws Exception {
        final Iterator<String> cookies = new RqCookies.Base(req)
            .cookie(TkPrevious.class.getSimpleName())
            .iterator();
        final Response response;
        if (cookies.hasNext()) {
        	
        	response = new RsWithCookie(
    				new RsRedirect(URLDecoder.decode(cookies.next(), "UTF-8")),
    				TkPrevious.class.getSimpleName(),
                    "",
                    "Path=/",
                    "Expires=Thu, 01 Jan 1970 00:00:00 GMT"
                );
        	  
        } else {
        	// do nothing
            response = this.origin.act(req);
        }
        
        return response;
    }
}
