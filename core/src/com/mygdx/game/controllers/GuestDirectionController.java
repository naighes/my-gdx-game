package com.mygdx.game.controllers;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Guest;
import com.mygdx.game.Scenario;
import com.mygdx.game.Tile;
import com.mygdx.game.utils.Geometry;

import java.util.Random;

public class GuestDirectionController {
    private final Random rnd = new Random();

    private float timeSinceLastChange;
    private float nextDirectionChangeAt;
    private final Guest guest;
    private final Scenario scenario;
    private Vector2 previoudDirection;

    public GuestDirectionController(Guest guest, Scenario scenario) {
        this.guest = guest;
        this.scenario = scenario;
    }

    public Vector2 update(float delta,
                          Tile collidedTile,
                          Vector2 currentDirection) {
        if (this.guest
                .getScenario()
                .getConversationsController()
                .hasPendingConversationWith(this.guest)) {
            if (this.previoudDirection == null) {
                this.previoudDirection = currentDirection;

                return this.scenario.getPlayer().getCurrentDirection().cpy().scl(-1f).add(Vector2.Zero);
            }

            return currentDirection;
        }

        if (this.previoudDirection != null) {
            Vector2 direction = this.previoudDirection.cpy();
            this.previoudDirection = null;
            return direction;
        }

        if (collidedTile != null)
            return currentDirection.cpy().scl(-1f).add(Vector2.Zero);

        if (this.timeSinceLastChange > this.nextDirectionChangeAt) {
            // it'll automatically change direction in a range
            // between 2 and 10 seconds
            final int max = 10;
            final int min = 2;
            this.nextDirectionChangeAt = this.rnd.nextInt((max - min) + 1) + min;
            this.timeSinceLastChange = 0f;
            return this.randDirection();
        } else {
            this.timeSinceLastChange += delta;
            return currentDirection;
        }
    }

    private Vector2 randDirection() {
        int i = this.rnd.nextInt((4 - 1) + 1) + 1;
        switch (i) {
            case 1:
                return Geometry.UP;
            case 2:
                return Geometry.DOWN;
            case 3:
                return Geometry.LEFT;
        }

        return Geometry.RIGHT;
    }
}
