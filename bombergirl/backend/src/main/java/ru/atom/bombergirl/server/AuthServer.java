package ru.atom.bombergirl.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.atom.bombergirl.dao.Database;
import ru.atom.bombergirl.dao.TokenDao;


public class AuthServer {
    public static void main(String[] args) throws Exception {
        Database.setUp();
        Session session = Database.session();
        Transaction transaction = session.beginTransaction();
        TokenDao.getInstance().clean(session);
        transaction.commit();

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[] {
                createAuthContext(),
                createResourceContext()
        });

        Server jettyServer = new Server(8080);
        jettyServer.setHandler(contexts);

        jettyServer.start();
    }

    private static ServletContextHandler createAuthContext() {
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/auth/*");
        ServletHolder jerseyServlet = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        jerseyServlet.setInitParameter(
                "jersey.config.server.provider.packages",
                "ru.atom.bombergirl.server"
        );

        jerseyServlet.setInitParameter(
                "com.sun.jersey.spi.container.ContainerResponseFilters",
                CrossBrowserFilter.class.getCanonicalName()
        );

        return context;
    }

    private static ContextHandler createResourceContext() {
        ContextHandler context = new ContextHandler();
        context.setContextPath("/");
        ResourceHandler handler = new ResourceHandler();
        handler.setWelcomeFiles(new String[]{"index.html"});

        String serverRoot = AuthServer.class.getResource("/static").toString();
        handler.setResourceBase(serverRoot);
        context.setHandler(handler);
        return context;
    }

}