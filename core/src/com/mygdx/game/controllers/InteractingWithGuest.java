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

        Guest guest = scenario.checkGuestCollisions();

        if (guest != null && guest.wannaTalk()) {
            return new TalkWithGuestResult(PlayerState.TALKING, guest);
        }

        return this.next.advance(scenario, state);
    }
}
