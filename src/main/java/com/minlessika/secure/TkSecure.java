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

import org.takes.Take;
import org.takes.tk.TkWrap;

/**
 * Take that secures a page and redirects by default to login page.
 *
 * <p>The class is immutable and thread-safe.</p>
 *
 * @author Olivier B. OURA (baudoliver7@gmail.com)
 */
public final class TkSecure extends TkWrap {

	/**
	 * Ctor.
	 * @param take take to secure
	 */
	public TkSecure(final Take take) {
		this(take, "login");
	}
	
	/**
	 * Ctor.
	 * @param take take to secure
	 * @param location Location to redirect
	 */
	public TkSecure(final Take take, final String location) {
		super(
			new org.takes.facets.auth.TkSecure(
				take, 
				location
			)
		);
	}

}
