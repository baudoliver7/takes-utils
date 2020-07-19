package com.minlessika.rs;

import java.util.Map;

import org.takes.facets.fork.FkTypes;
import org.takes.facets.fork.RsFork;
import org.takes.rq.RqFake;
import org.takes.rs.RsWithType;
import org.takes.rs.RsWrap;
import org.takes.rs.RsXslt;
import org.takes.rs.xe.RsXembly;
import org.takes.rs.xe.XeAppend;
import org.takes.rs.xe.XeStylesheet;

import com.minlessika.rs.xe.XeForm;

public final class RsRedirectPost extends RsWrap {

	public RsRedirectPost(final String url, final Map<String, String> body) {
		super(
			new RsFork(
	            new RqFake(),
	            new FkTypes(
	                "*/*",
	                new RsXslt(
	                	new RsWithType(
		                	new RsXembly(
		    					new XeStylesheet("/xsl/form.xsl"),
								new XeAppend(
									"page",
							        new XeForm(url, body)
								)
		                	), 
		                	"text/html"
		                )
	                )
	            )
	        )
		);
	}
}
