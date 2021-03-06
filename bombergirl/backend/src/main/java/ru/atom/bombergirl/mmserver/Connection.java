package ru.atom.bombergirl.mmserver;

import org.eclipse.jetty.websocket.api.Session;
import ru.atom.bombergirl.game.model.model.Action;
import ru.atom.bombergirl.game.model.model.Pawn;

/**
 * Created by ikozin on 17.04.17.
 */
public class Connection {
    //private final String name;
    private final Session session;
    private Pawn pawn;
    private String token;
    private static int counter = 0;
    private final int id = counter++;

    public Connection(String token, Session session) {
        this.token = token;
        this.session = session;
    }

    public void start(Action action) {
        pawn.addAction(action);
    }

    public void setGirl(Pawn pawn) {
        this.pawn = pawn;
    }

    public Pawn getPawn() {
        return pawn;
    }

    public Session getSession() {
        return session;
    }

    /*
    public String getName() {
        return name;
    }
*/

    @Override
    public String toString() {
        return "Connection{" +
                ", id='" + id + '\'' +
                '}';
    }

    public String getToken() {
        return token;
    }
}
