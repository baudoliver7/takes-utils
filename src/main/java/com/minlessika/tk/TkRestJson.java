package com.minlessika.tk;

import java.net.HttpURLConnection;

import javax.json.Json;

import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsJson;
import org.takes.rs.RsWithStatus;

public class TkRestJson implements Take {

	/**
     * Original take.
     */
    private final Take origin;

    /**
     * Ctor.
     * @param take Original take
     */
    public TkRestJson(final Take take) {
    	this.origin = take;
    }

    @Override
    public Response act(final Request req) throws Exception {
    	try {
    		return this.origin.act(req); 
		} catch (IllegalArgumentException ex) {
			return new RsWithStatus(
					new RsJson(
						Json.createObjectBuilder()
							.add("message", ex.getLocalizedMessage())
							.build()
					),
					HttpURLConnection.HTTP_BAD_REQUEST
				);
		} catch (Exception ex) {
			return new RsWithStatus(
					new RsJson(
						Json.createObjectBuilder()
							.add("message", ex.getLocalizedMessage())
							.build()
					),
					HttpURLConnection.HTTP_INTERNAL_ERROR
				);
		}  	
    }

}
