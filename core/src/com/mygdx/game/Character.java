package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

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
                     Direction direction) {
        switch (direction) {
            case RIGHT:
                this.translateX(speed * dt);
                break;
            case LEFT:
                this.translateX(-1f * speed * dt);
                break;
            case UP:
                this.translateY(speed * dt);
                break;
            case DOWN:
                this.translateY(-1f * speed * dt);
                break;
        }
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
