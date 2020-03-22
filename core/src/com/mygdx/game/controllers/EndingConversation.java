package com.mygdx.game.controllers;

import com.mygdx.game.Scenario;

// TODO: to be managed by ConversationController
public class EndingConversation implements PlayerStateController {
    @Override
    public PlayerStateControllerResult advance(Scenario scenario, PlayerState state) {
        if (state == PlayerState.TALKING &&
                !scenario.getConversationsController()
                        .hasPendingConversation()) {
            return new NoOpResult(PlayerState.NONE);
        }

        return new NoOpResult(state);
    }
}
