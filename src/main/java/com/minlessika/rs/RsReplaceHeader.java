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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.takes.Response;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsWrap;

/**
 * Response decorator, with an additional header.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author OURA Olivier Baudouin (baudolivier.oura@gmail.com)
 */
public final class RsReplaceHeader extends RsWrap {

    private static final Pattern HEADER = Pattern.compile(
        "[a-zA-Z0-9\\-]+:\\p{Print}+"
    );

    public RsReplaceHeader(final CharSequence header, final CharSequence replacement) {
        this(new RsEmpty(), header, replacement);
    }
    
    public RsReplaceHeader(final Response res, final CharSequence header, final CharSequence replacement) {
        super(
            new Response() {
                @Override
                public Iterable<String> head() throws IOException {
                    return RsReplaceHeader.replace(res.head(), header.toString(), replacement.toString());
                }
                @Override
                public InputStream body() throws IOException {
                    return res.body();
                }
            }
        );
    }

    /**
     * Replace in head header by replacement.
     * @param head Original head
     * @param header Value to replace
     * @param replacement Value of replacement
     * @return Head with header updated
     */
    private static Iterable<String> replace(final Iterable<String> head, final String header, final String replacement) {
        if (!RsReplaceHeader.HEADER.matcher(replacement).matches()) {
            throw new IllegalArgumentException(
                String.format(
                    // @checkstyle LineLength (1 line)
                    "replacement header \"%s\" doesn't match \"%s\" regular expression, but it should, according to RFC 7230",
                    replacement, RsReplaceHeader.HEADER
                )
            );
        }
       
        List<String> updatedHead = new ArrayList<>();
        for (String myHeader : head) {
			if(myHeader.equals(header)) {
				updatedHead.add(replacement);
			} else {
				updatedHead.add(myHeader);
			}
		}
        
        return updatedHead;
    }
}
