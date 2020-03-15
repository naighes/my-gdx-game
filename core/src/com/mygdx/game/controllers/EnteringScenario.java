package com.mygdx.game.controllers;

import com.mygdx.game.MyGdxGame;
import com.mygdx.game.Player;
import com.mygdx.game.Scenario;
import com.mygdx.game.descriptors.EndpointDescriptor;

// check if the player is into the new scenario and
// out of the connection
public class EnteringScenario implements PlayerStateController {
    private final MyGdxGame game;
    private final PlayerStateController next;

    public EnteringScenario(MyGdxGame game,
                            PlayerStateController next) {
        this.game = game;
        this.next = next;
    }

    @Override
    public PlayerStateControllerResult advance(Scenario scenario, PlayerState state) {
        if (state != PlayerState.ENTERING_SCENARIO) {
            return this.next.advance(scenario, state);
        }

        Player player = scenario.getPlayer();
        EndpointDescriptor endpoint = this.game.getConnections()
                .checkConnectionLeft(player.getScenario().name,
                        player.getBoundingRectangle(),
                        player.getOffsetX(),
                        player.getOffsetY());

        if (endpoint == null) {
            return new NoOpResult(PlayerState.NONE);
        }

        return this.next.advance(scenario, state);
    }
}
