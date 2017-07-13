package ru.atom.bombergirl.game.model.model;

/**
 * Created by ikozin on 01.05.17.
 */
public class PlantBomb implements Action {
    @Override
    public void act(Pawn pawn) {
        pawn.makePlantBomb();
    }
}
