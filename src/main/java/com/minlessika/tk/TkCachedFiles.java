package com.minlessika.tk;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.codec.digest.DigestUtils;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqHref;
import org.takes.rs.RsPrint;
import org.takes.rs.RsWithHeaders;
import org.takes.rs.RsWithStatus;
import org.apache.commons.io.IOUtils;
import org.takes.tk.TkWrap;

/**
 * Take that caches static resources files.
 * <p>It's used for fast pages loading with
 * a smart refresh of resources that have been changed
 * based on ETag.
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Caching">HTTP Caching</a>
 * @since 0.2
 */
public final class TkCachedFiles extends TkWrap {
	
	/**
	 * Ctor.
	 * @param origin Origin
	 * @param maxage Max age in seconds
	 * @param extensions Extensions
	 */
	public TkCachedFiles(final Take origin, final int maxage, final String... extensions) {
		super(
			req -> {
				final String location = new RqHref.Smart(req).href().path();
				final Response resp;
				if(
					Arrays.stream(extensions)
						.anyMatch(
							ext -> location.toLowerCase(Locale.ENGLISH).endsWith(
								String.format(".%s", ext.toLowerCase(Locale.ENGLISH))
							)
						)
				) {
					final Response raw = origin.act(req);;
					final String content = new RsPrint(raw).printBody();
					final String etag = DigestUtils.md5Hex(content).toUpperCase();
					final Iterator<String> ifnonematch = new RqHeaders.Base(req)
						.header("If-None-Match").iterator();
					if(ifnonematch.hasNext() && ifnonematch.next().equals(etag)) {
						resp = new RsWithStatus(304);
					} else {
						resp = new RsWithHeaders(
							origin.act(req),
							String.format("Cache-Control: public, max-age=%s", maxage),
							String.format("ETag: %s", etag)
						);
					}
				} else {
					resp = origin.act(req);
				}
				return resp;
			}
		);
	}

}
