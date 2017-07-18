package ru.atom.bombergirl.mmserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.atom.bombergirl.game.server.EventServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ikozin on 17.04.17.
 */
public class MatchMaker implements Runnable {
    private static final Logger log = LogManager.getLogger(MatchMakerServer.class);
    //private static AtomicLong idGame = new AtomicLong();

    /*public static Long getIdGame() {
        return idGame.get();
    }*/

    @Override
    public void run() {
        log.info("MM Started");
        List<Connection> candidates = new ArrayList<>(GameSession.PLAYERS_IN_GAME);
        EventServer server = new EventServer();
        Thread eventServer = new Thread(server);
        eventServer.setName("eventServer");
        eventServer.start();
        log.info(eventServer);
        while (!Thread.currentThread().isInterrupted()) {
            try {
                log.info("ThreadSafeQueue size is " + ThreadSafeQueue.getInstance().size());
//                while ((connection = ThreadSafeQueue.getInstance()
//                        .poll(10_000, TimeUnit.MILLISECONDS) ) != null ) {
//                    candidates.add(connection);
//                }
                Connection connection = ThreadSafeQueue.getInstance().poll(10_000, TimeUnit.SECONDS);if (connection != null) {
                    candidates.add(connection);
                }
                log.info("Finally I'm here");
                log.info("ThreadSafeQueue size is " + ThreadSafeQueue.getInstance().size());
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
//                server = new EventServer(idGame);
//                eventServer = new Thread(server);
//                eventServer.setName("eventServer " + idGame);
//                eventServer.start();
//                log.info(eventServer);
//                log.info("idGame : " + idGame);
            }
        }
    }
}
