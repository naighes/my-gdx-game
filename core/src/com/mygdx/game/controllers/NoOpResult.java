package com.mygdx.game.controllers;

public class NoOpResult extends PlayerStateControllerResult {
    public NoOpResult(PlayerState state) {
        super(state);
    }

    public void process() {
    }
}
