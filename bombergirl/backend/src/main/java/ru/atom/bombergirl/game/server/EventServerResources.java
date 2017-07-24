package ru.atom.bombergirl.game.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.atom.bombergirl.dao.Database;
import ru.atom.bombergirl.dao.TokenDao;
import ru.atom.bombergirl.dbmodel.Token;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Created by dmitriy on 15.05.17.
 */
@Path("/")
public class EventServerResources {
    private static final Logger log = LogManager.getLogger(EventServerResources.class);
    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Produces("text/plain")
    @Path("/")
    public static Response gs(@QueryParam("token") String token) {
        System.out.println("I'm here! My token is " + token);
//        Transaction txn = null;
//        try (Session session = Database.session()) {
//            txn = session.beginTransaction();
//            Token regToken = TokenDao.getInstance().getByStrToken(session, token);
//        } catch (RuntimeException e) {
//            if (txn != null && txn.isActive()) {
//                txn.rollback();
//            }
//            log.error("Transaction failed.", e);
//            return Response.status(Response.Status.BAD_REQUEST).entity("Exception occured.").build();
//        }
        return Response.ok().build();
    }
}
