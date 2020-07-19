package com.minlessika.rq;

import java.io.IOException;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonStructure;

import org.takes.Request;
import org.takes.rq.RqWrap;

public final class RqJsonBase extends RqWrap implements RqJson {
	
	/**
     * Request.
     */
    private final Request req;
    
    /**
     * Ctor.
     * @param request Original request
     */
    public RqJsonBase(final Request request) {
        super(request);
        this.req = request;
    }

	@Override
	public JsonStructure payload() throws IOException {	
		
		JsonReader reader = null;
		
		try {
			reader = Json.createReader(req.body());
			return reader.read();
		} finally {
			if(reader != null)
				reader.close();
		}
	}
}
