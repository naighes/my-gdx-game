package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Character extends Sprite {
    private final Scenario scenario;
    private final float offsetX;
    private final float offsetY;

    Character(Scenario scenario,
              float offsetX,
              float offsetY) {
        this.scenario = scenario;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    void setMovement(float dt,
                     float speed,
                     Vector2 direction) {
        Vector2 movement =  direction.cpy().scl(speed * dt);
        this.translate(movement.x, movement.y);
    }

    Tile getCollidedTile() {
        Rectangle bounds = this.getBoundingRectangle();
        return this.scenario
                .getArea()
                .collidesWith(bounds,
                        this.offsetX,
                        this.offsetY);
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public Scenario getScenario() {
        return scenario;
    }
}
