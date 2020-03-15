package com.mygdx.game.controllers;

public abstract class PlayerStateControllerResult {
    private final PlayerState state;

    public PlayerStateControllerResult(PlayerState state) {
        this.state = state;
    }

    public PlayerState getState() {
        return state;
    }

    public abstract void process();
}
