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
package com.minlessika.secure;

import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.RqAuth;
import org.takes.facets.forward.RsForward;
import org.takes.rs.RsEmpty;

/**
 * Take available for anonymous users.
 *
 * <p>The class is immutable and thread-safe.</p>
 *
 * @author Olivier B. OURA (baudoliver7@gmail.com)
 */

public final class TkAnonymous implements Take {
	
    /**
     * Original take.
     */
    private final Take origin;

    /**
     * Location where to forward.
     */
    private final String loc;

    /**
     * Ctor.
     * Redirects by default to home page
     * @param take Original
     */
    public TkAnonymous(final Take take) {
        this(take, "/home");
    }

    /**
     * Ctor.
     * @param take Original
     * @param location Where to forward
     */
    public TkAnonymous(final Take take, final String location) {
        this.origin = take;
        this.loc = location;
    }

    @Override
    public Response act(final Request request) throws Exception {
        if (!new RqAuth(request).identity().equals(Identity.ANONYMOUS)) {
            throw new RsForward(
                new RsEmpty(),
                this.loc
            );
        }
        return this.origin.act(request);
    }

}
