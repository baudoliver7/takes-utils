package com.minlessika.tk;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.tk.TkWrap;

import com.minlessika.rs.RsReplaceHeader;

/**
 * Set-Cookie domain can be changed.
 *
 * <p>This may be useful for some browsers. When you change a cookie domain,
 * the RFC-6265 (in section 5.2.3) requires it to be set with attribute "Domain" with value in lowercase. </p>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Olivier B. OURA (baudoliver7@gmail.com)
 */
public final class TkWithCookieDomain extends TkWrap {
    
	/**
     * Pattern to find them.
     */
    private static final Pattern PTN = Pattern.compile(
        "set-cookie: (.+)", Pattern.CASE_INSENSITIVE
    );
    
    /**
     * Pattern to find domain setting.
     */
    private static final Pattern DOMAIN_ATTR = Pattern.compile(
        "domain=(.+);", Pattern.CASE_INSENSITIVE
    );
    
	public TkWithCookieDomain(final Take take, final CharSequence domain) {
		super(
				new Take() {
	                @Override
	                public Response act(final Request req) throws Exception {
	                    return TkWithCookieDomain.changeDomain(take.act(req), domain);
	                }
	            }
		);
	}

	private static Response changeDomain(final Response res, final CharSequence domain) throws IOException {

		Response out = res;	
		Iterable<String> headers = res.head();
		for (final String header : headers) {
            final Matcher matcher = TkWithCookieDomain.PTN.matcher(header);
            if (!matcher.matches()) {
                continue;
            }
            
            String cookie = matcher.group(1);
            final Matcher domainMatcher = TkWithCookieDomain.DOMAIN_ATTR.matcher(cookie);
            
            final String domainAttr = String.format("Domain=%s;", domain);
            if(domainMatcher.matches()) {
            	cookie = domainMatcher.replaceAll(domainAttr);
            } else {
            	cookie += domainAttr;
            }
                      
            out = new RsReplaceHeader(
            		out, 
            		header, 
            		String.format("Set-Cookie: %s", cookie)
            );   
        }

		return out;
	}
}
