package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class GameCamera {
    private OrthographicCamera camera;

    public GameCamera() {
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    OrthographicCamera getInnerCamera() {
        return this.camera;
    }

    void update(Scenario scenario) {
        this.camera.position.x = scenario.getPlayer().getX();
        this.camera.position.y = scenario.getPlayer().getY();

        float dx = scenario.getPlayer().getX() - this.camera.position.x;
        float lx = Gdx.graphics.getWidth() / 5f;

        if (dx > lx) {
            this.camera.position.x = scenario.getPlayer().getX() - lx;
        }

        if (dx < -1f * lx) {
            this.camera.position.x = scenario.getPlayer().getX() + lx;
        }

        float bx1 = scenario.getArea().getWidth() - Gdx.graphics.getWidth() / 2f;

        if (this.camera.position.x >= bx1) {
            this.camera.position.x = bx1;
        }

        float bx2 = Gdx.graphics.getWidth() / 2f;

        if (this.camera.position.x <= bx2) {
            this.camera.position.x = bx2;
        }

        float dy = scenario.getPlayer().getY() - this.camera.position.y;
        float ly = Gdx.graphics.getHeight() / 5f;

        if (dy > ly) {
            this.camera.position.y = scenario.getPlayer().getY() - ly;
        }

        if (dy < -1f * ly) {
            this.camera.position.y = scenario.getPlayer().getY() + ly;
        }

        float by1 = scenario.getArea().getHeight() - Gdx.graphics.getHeight() / 2f;

        if (this.camera.position.y >= by1) {
            this.camera.position.y = by1;
        }

        float by2 = Gdx.graphics.getHeight() / 2f;

        if (this.camera.position.y <= by2) {
            this.camera.position.y = by2;
        }

        this.camera.update();
    }
}
