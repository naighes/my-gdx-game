package com.mygdx.game.controllers;

import com.mygdx.game.Guest;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.Scenario;

public class InteractingWithGuest implements PlayerStateController {
    private final MyGdxGame game;
    private final PlayerStateController next;

    public InteractingWithGuest(MyGdxGame game,
                                PlayerStateController next) {
        this.game = game;
        this.next = next;
    }

    @Override
    public PlayerStateControllerResult advance(Scenario scenario, PlayerState state) {
        if (state != PlayerState.NONE) {
            return this.next.advance(scenario, state);
        }

        Guest guest = scenario.getPlayer().checkGuestCollisions();

        if (guest != null &&
                scenario.getConversationsController()
                        .canStartConversation(guest)) {
            return new TalkWithGuestResult(PlayerState.TALKING,
                    guest,
                    scenario);
        }

        return this.next.advance(scenario, state);
    }
}
