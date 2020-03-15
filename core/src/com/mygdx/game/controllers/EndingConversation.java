package com.mygdx.game.controllers;

import com.mygdx.game.Scenario;

public class EndingConversation implements PlayerStateController {
    @Override
    public PlayerStateControllerResult advance(Scenario scenario, PlayerState state) {
        if (state == PlayerState.TALKING && !scenario.hasPendingConversation()) {
            return new NoOpResult(PlayerState.NONE);
        }

        return new NoOpResult(state);
    }
}
