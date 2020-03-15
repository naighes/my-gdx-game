package com.mygdx.game.controllers;

import com.mygdx.game.MyGdxGame;
import com.mygdx.game.Player;
import com.mygdx.game.Scenario;
import com.mygdx.game.descriptors.EndpointDescriptor;

// check if the player attempts to enter a new scenario
public class ExitingScenario implements PlayerStateController {
    private final MyGdxGame game;
    private final PlayerStateController next;

    public ExitingScenario(MyGdxGame game,
                           PlayerStateController next) {
        this.game = game;
        this.next = next;
    }

    @Override
    public PlayerStateControllerResult advance(Scenario scenario, PlayerState state) {
        if (state != PlayerState.NONE) {
            return this.next.advance(scenario, state);
        }

        Player player = scenario.getPlayer();
        EndpointDescriptor endpoint = this.game.getConnections()
                .checkConnectionHit(scenario.name,
                        player.getBoundingRectangle(),
                        player.getOffsetX(),
                        player.getOffsetY());

        if (endpoint != null) {
            return new ChangeScenarioResult(PlayerState.EXITING_SCENARIO,
                    this.game,
                    endpoint,
                    player);
        }

        return this.next.advance(scenario, state);
    }
}
