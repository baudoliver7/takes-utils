package com.minlessika.rs.xe;

import java.io.IOException;
import java.util.Map;

import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Mapped;
import org.takes.rs.xe.XeWrap;
import org.xembly.Directive;
import org.xembly.Directives;
import org.takes.rs.xe.XeSource;

public final class XeForm extends XeWrap {

	public XeForm(final String url, final Map<String, String> body) {
		
		super(new XeSource() {
            @Override
            public Iterable<Directive> toXembly() throws IOException {
                return new Directives()
                		.add("url").set(url).up()
                		.add("parameters")
    					.append(
    	                    new Joined<>(
	                    		new Mapped<java.util.Map.Entry<String, String>, Iterable<Directive>>(
            			            item -> transform("parameter", item),
            			            body.entrySet()
    	            		    )
                            )
    	                 ).up();
            }
        });
	}
	
	private static Iterable<Directive> transform(final String root, final java.util.Map.Entry<String, String> item) throws IOException {
		return new Directives()
                .add(root)
                .add("name").set(item.getKey()).up()
                .add("value").set(item.getValue()).up()      
                .up();
	}

}
