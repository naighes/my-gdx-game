package com.mygdx.game.controllers;

import com.mygdx.game.Guest;
import com.mygdx.game.Scenario;

public class TalkWithGuestResult extends PlayerStateControllerResult {
    private final Guest guest;
    private final Scenario scenario;

    public TalkWithGuestResult(PlayerState state,
                               Guest guest,
                               Scenario scenario) {
        super(state);

        this.guest = guest;
        this.scenario = scenario;
    }

    public void process() {
        this.scenario
                .getConversationsController()
                .tryStartConversation(this.guest, this.scenario.getPlayer());
    }
}
