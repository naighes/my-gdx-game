package com.mygdx.game.controllers;

import com.mygdx.game.MyGdxGame;
import com.mygdx.game.Player;
import com.mygdx.game.descriptors.EndpointDescriptor;

public class ChangeScenarioResult extends PlayerStateControllerResult {
    private final MyGdxGame game;
    private final EndpointDescriptor endpoint;
    private final Player player;

    ChangeScenarioResult(PlayerState state,
                         MyGdxGame game,
                         EndpointDescriptor endpoint,
                         Player player) {
        super(state);
        this.game = game;
        this.endpoint = endpoint;
        this.player = player;
    }

    public void process() {
        this.game.setCurrentScenario(
                endpoint.scenarioName,
                endpoint.getCenterAgainst(player.getBoundingRectangle(),
                        player.getOffsetX(),
                        player.getOffsetY()),
                player.getCurrentDirection());
    }
}
