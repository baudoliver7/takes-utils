/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Yegor Bugayenko
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
package com.minlessika.rs;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import org.takes.Response;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsWrap;

/**
 * Response decorator that remove a header.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author OURA Olivier Baudouin (baudolivier.oura@gmail.com)
 */
public final class RsRemoveHeader extends RsWrap {

    public RsRemoveHeader(final CharSequence header) {
        this(new RsEmpty(), header);
    }
    
    public RsRemoveHeader(final Response res, final CharSequence header) {
        super(
            new Response() {
                @Override
                public Iterable<String> head() throws IOException {
                    return RsRemoveHeader.remove(res.head(), header.toString());
                }
                @Override
                public InputStream body() throws IOException {
                    return res.body();
                }
            }
        );
    }

    /**
     * Remove in head header.
     * @param head Original head
     * @param header Value to replace
     * @param replacement Value of replacement
     * @return Head with header updated
     */
    private static Iterable<String> remove(final Iterable<String> head, final String header) {
        List<String> updatedHead = new LinkedList<>();
        for (String h : head) {
			if(!h.equals(header)) {
				updatedHead.add(h);
			}
		}
        return updatedHead;
    }
}
