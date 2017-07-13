package ru.atom.bombergirl.game.server;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Created by dmitriy on 15.05.17.
 */
@Path("/")
public class EventServerResources {
    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Produces("text/plain")
    @Path("/")
    public static Response gs(@QueryParam("id") String id) {
        return Response.ok().build();
    }
}
