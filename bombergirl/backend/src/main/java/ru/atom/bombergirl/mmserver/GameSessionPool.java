package ru.atom.bombergirl.mmserver;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ikozin on 17.04.17.
 */

public class GameSessionPool {
    private static ConcurrentHashMap<Long, GameSession> map = new ConcurrentHashMap<>();

    public static void put(GameSession session) {
        map.put(session.getId(), session);
    }

    public static Collection<GameSession> getAll() {
        return map.values();
    }

    public static int getSize() {
        return map.size();
    }
}

