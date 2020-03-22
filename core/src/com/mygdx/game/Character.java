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
        return this.offsetX;
    }

    public float getOffsetY() {
        return this.offsetY;
    }

    public Scenario getScenario() {
        return this.scenario;
    }

    public boolean intersect(Character character) {
        Rectangle pRect = this.getBoundingRectangle();
        Rectangle gRect = character.getBoundingRectangle();
        float pl = pRect.x + this.getOffsetX();
        float el = gRect.x + character.getOffsetX();
        float pr = pRect.x + pRect.width - this.getOffsetY();
        float er = gRect.x + gRect.width - character.getOffsetY();

        float pd = pRect.y + this.getOffsetY();
        float ed = gRect.y + character.getOffsetY();
        float pu = pRect.y + pRect.height - this.getOffsetY();
        float eu = gRect.y + gRect.height - this.getOffsetY();

        return pl < er && pr > el && pd < eu && pu > ed;
    }
}
