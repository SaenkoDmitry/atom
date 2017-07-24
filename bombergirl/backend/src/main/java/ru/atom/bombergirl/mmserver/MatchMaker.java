package ru.atom.bombergirl.mmserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.util.ConcurrentHashSet;
import ru.atom.bombergirl.game.server.EventServer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ikozin on 17.04.17.
 */
public class MatchMaker implements Runnable {
    private static final Logger log = LogManager.getLogger(MatchMakerServer.class);
    private static List<Connection> candidates = new ArrayList<>(GameSession.PLAYERS_IN_GAME);
    private static ConcurrentHashSet<String> sessionManager = new ConcurrentHashSet<>();

    @Override
    public void run() {
        log.info("MM Started");
        EventServer server = new EventServer();
        Thread eventServer = new Thread(server);
        eventServer.setName("eventServer");
        eventServer.start();
        log.info(eventServer);
        while (!Thread.currentThread().isInterrupted()) {
            try {
                log.info("ThreadSafeQueue size is " + ConnectionQueue.getInstance().size());
                Connection connection = ConnectionQueue.getInstance().poll(10_000, TimeUnit.SECONDS);
                if (connection != null) {
                    candidates.add(connection);
                }
                log.info("ThreadSafeQueue size is " + ConnectionQueue.getInstance().size());
                log.info("candidates size is " + candidates.size());
            } catch (InterruptedException e) {
                log.warn("Timeout reached");
            }

            if (candidates.size() == GameSession.PLAYERS_IN_GAME) {
                GameSession session = new GameSession(candidates.toArray(new Connection[0]));
                Thread gameSession = new Thread(session);
                gameSession.setName("gameSession " + session.getIdGame());
                gameSession.start();
                log.info(session);
                GameSessionPool.put(session);
                candidates.clear();
            }
        }
    }

    public static void addGameToken(String strToken) {
        if (!sessionManager.contains(strToken))
            sessionManager.add(strToken);
        if (sessionManager.size() == 4) {
            sessionManager.clear();
        }
    }

    public static void waitIfNeeded(String strToken) {
        while (sessionManager.contains(strToken)) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
