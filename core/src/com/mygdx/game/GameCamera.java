package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class GameCamera extends OrthographicCamera {
    public GameCamera() {
        super();

        this.setToOrtho(false,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());
    }

    void update(Scenario scenario) {
        this.position.x = scenario.getPlayer().getX();
        this.position.y = scenario.getPlayer().getY();

        float dx = scenario.getPlayer().getX() - this.position.x;
        float lx = Gdx.graphics.getWidth() / 5f;

        if (dx > lx) {
            this.position.x = scenario.getPlayer().getX() - lx;
        }

        if (dx < -1f * lx) {
            this.position.x = scenario.getPlayer().getX() + lx;
        }

        float bx1 = scenario.getArea().getWidth() - Gdx.graphics.getWidth() / 2f;

        if (this.position.x >= bx1) {
            this.position.x = bx1;
        }

        float bx2 = Gdx.graphics.getWidth() / 2f;

        if (this.position.x <= bx2) {
            this.position.x = bx2;
        }

        float dy = scenario.getPlayer().getY() - this.position.y;
        float ly = Gdx.graphics.getHeight() / 5f;

        if (dy > ly) {
            this.position.y = scenario.getPlayer().getY() - ly;
        }

        if (dy < -1f * ly) {
            this.position.y = scenario.getPlayer().getY() + ly;
        }

        float by1 = scenario.getArea().getHeight() - Gdx.graphics.getHeight() / 2f;

        if (this.position.y >= by1) {
            this.position.y = by1;
        }

        float by2 = Gdx.graphics.getHeight() / 2f;

        if (this.position.y <= by2) {
            this.position.y = by2;
        }

        this.update();
    }
}
