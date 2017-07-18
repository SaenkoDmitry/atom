package ru.atom.bombergirl.game.server;

/**
 * Created by dmitriy on 01.05.17.
 */
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.atom.bombergirl.server.CrossBrowserFilter;

import java.util.concurrent.atomic.AtomicLong;

public class EventServer implements Runnable {

    //private static AtomicLong idforurl = new AtomicLong(0);

    public static void main(String[] args) {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8085);
        server.addConnector(connector);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[]{
            //createGsContext(),
                createServerContext(),
            createResourceContext(),
            //context
        });
        server.setHandler(contexts);

        // Add a websocket to a specific path spec
        ServletHolder holderEvents = new ServletHolder("ws-events", EventServlet.class);
        //context.addServlet(holderEvents, "/");

        try {
            server.start();
            server.dump(System.err);
            server.join();
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }

    private static ServletContextHandler createServerContext() {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        ServletHolder jerseyServlet = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        ServletHolder holderEvents = new ServletHolder("ws-events", EventServlet.class);
        context.addServlet(holderEvents, "/events/*");
        jerseyServlet.setInitParameter(
                "jersey.config.server.provider.packages",
                "ru.atom.bombergirl.communication.network"
        );

        return context;
    }

//    private static ServletContextHandler createGsContext() {
//        ServletContextHandler context = new ServletContextHandler();
//        context.setContextPath("/");
//        ServletHolder jerseyServlet = context.addServlet(
//                org.glassfish.jersey.servlet.ServletContainer.class, "/*");
//        jerseyServlet.setInitOrder(0);
//
//        jerseyServlet.setInitParameter(
//                "jersey.config.server.provider.packages",
//                "ru.atom.bombergirl.gameserver"
//        );
//
//        jerseyServlet.setInitParameter(
//                "com.sun.jersey.spi.container.ContainerResponseFilters",
//                CrossBrowserFilter.class.getCanonicalName()
//        );
//
//        return context;
//    }

    private static ContextHandler createResourceContext() {
        ContextHandler context = new ContextHandler();
        context.setContextPath("/gs/0");
        ResourceHandler handler = new ResourceHandler();
        String eventRoot = EventServer.class.getResource("/static").toString();
        String serverRoot = eventRoot.substring(0, eventRoot.length() - 35) + "frontend/src/main/webapp";
        handler.setWelcomeFiles(new String[]{"index.html"});
        handler.setResourceBase(serverRoot);
        context.setHandler(handler);
        return context;
    }

//    public EventServer (AtomicLong id) {
//        idforurl = id;
//    }
//
    @Override
    public void run() {
        String[] args = new String[0];
        EventServer.main(args);
    }
}
