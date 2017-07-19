package ru.atom.bombergirl.communication.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.atom.bombergirl.game.model.geometry.Point;

/**
 * Created by dmitriy on 18.07.17.
 */
public class EndMessage {
    private final int gameEnd;

    @JsonCreator
    public EndMessage(@JsonProperty("gameEnd") int gameEnd) {
        this.gameEnd = gameEnd;
    }

    public int getGameEnd() {
        return gameEnd;
    }

    @Override
    public String toString() {
        return "EndMessage{" +
                "gameEnd='" + gameEnd + '\'' +
                '}';
    }

}
