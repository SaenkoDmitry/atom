package ru.atom.bombergirl.game.server;

import com.google.common.net.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import ru.atom.bombergirl.mmserver.Connection;
import ru.atom.bombergirl.mmserver.ConnectionQueue;
import ru.atom.bombergirl.communication.network.Broker;
import ru.atom.bombergirl.communication.network.ConnectionPool;

public class EventHandler extends WebSocketAdapter {
    private static final Logger log = LogManager.getLogger(EventServer.class);

    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        String token = sess.getUpgradeRequest().getHeader(HttpHeaders.COOKIE).substring(14);
        Connection player = new Connection(token, sess);
        ConnectionPool.getInstance().add(sess, player);
        ConnectionQueue.getInstance().offer(player);
        log.info("add element : " + player.getPawn() + " into queue");
        log.info("ThreadSafeQueue size is " + ConnectionQueue.getInstance().size());
        System.out.println("Socket Connected: " + sess);
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        Broker.getInstance().receive(super.getSession(), message);
        System.out.println("Received TEXT message: " + message);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        System.out.println("Socket Closed: [" + statusCode + "] " + reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
    }
}
