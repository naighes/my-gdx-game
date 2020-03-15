package com.mygdx.game.controllers;

import com.mygdx.game.Scenario;

public interface PlayerStateController {
    PlayerStateControllerResult advance(Scenario scenario, PlayerState state);
}
