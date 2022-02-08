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
import org.takes.rs.RsWithHeaders;
import org.takes.rs.RsWithStatus;
import org.apache.commons.io.IOUtils;

/**
 * Take that caches static resources files.
 * <p>It's used for fast pages loading with
 * a smart refresh of resources that have been changed
 * based on ETag.
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Caching">HTTP Caching</a>
 * @since 0.2
 */
public final class TkCachedFiles implements Take {

	/**
	 * Origin.
	 */
	private final Take origin;

	/**
	 * Max age in seconds.
	 */
	private final int maxage;

	/**
	 * Extensions of files to cache.
	 */
	private final String[] extensions;

	/**
	 * Ctor.
	 * @param origin Origin
	 * @param extension List of extensions
	 */
	public TkCachedFiles(final Take origin, final String... extension) {
		this(origin, 60, extension);
	}
	
	/**
	 * Ctor.
	 * @param origin Origin
	 * @param maxage Max age in seconds
	 * @param extensions Extensions
	 */
	public TkCachedFiles(final Take origin, final int maxage, final String... extensions) {
		this.origin = origin;
		this.maxage = maxage;
		this.extensions = extensions;
	}

	@Override
	public Response act(Request req) throws Exception {
		final RqHref.Smart rqhref = new RqHref.Smart(req);
		final String location = rqhref.href().path();
		Response res;
		if(
			Arrays.stream(extensions)
				.anyMatch(
					ext -> location.toLowerCase(Locale.ENGLISH).endsWith(
						String.format(".%s", ext.toLowerCase(Locale.ENGLISH))
					)
				)
		) {
			try (InputStream input = this.getClass().getResourceAsStream(location)) {
				final String content = IOUtils.toString(input, "UTF-8");
				final String etag = DigestUtils.md5Hex(content).toUpperCase();
				final Iterator<String> ifnonematch = new RqHeaders.Base(req)
					.header("If-None-Match").iterator();
				if(ifnonematch.hasNext() && ifnonematch.next().equals(etag)) {
					res = new RsWithStatus(304);
				} else {
					res = new RsWithHeaders(
						this.origin.act(req),
						String.format("Cache-Control: public, max-age=%s", maxage),
						String.format("ETag: %s", etag)
					);
				}
			} catch(Exception ex) {
				res = this.origin.act(req);
			}
		} else {
			res = origin.act(req);
		}
		return res;
	}

}
