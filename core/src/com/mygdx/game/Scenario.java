package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public class Scenario implements Screen, Disposable {
    private final MyGdxGame game;
    private final String name;
    private final float x;
    private final float y;
    private final Vector2 playerInitialPosition;
    private final String assetPath;
    private final String collisionAssetPath;
    private final String overlayAssetPath;

    private Area area;
    private Overlay overlay;

    Scenario(MyGdxGame game,
             String name,
             float x,
             float y,
             Vector2 playerInitialPosition,
             String assetPath,
             String collisionAssetPath,
             String overlayAssetPath) {
        this.game = game;
        this.name = name;
        this.x = x;
        this.y = y;
        this.playerInitialPosition = playerInitialPosition;
        this.assetPath = assetPath;
        this.collisionAssetPath = collisionAssetPath;
        this.overlayAssetPath = overlayAssetPath;

        this.area = new Area(this.x,
                this.y,
                this.assetPath,
                this.collisionAssetPath);
        this.area.create(Gdx.files);

        if (this.overlayAssetPath != null) {
            this.overlay = new Overlay(this.x, this.y, this.overlayAssetPath);
            this.overlay.create(Gdx.files, Gdx.graphics);
        }
    }

    Vector2 getPlayerInitialPosition() {
        return this.playerInitialPosition;
    }

    Area getArea() {
        return this.area;
    }

    Endpoint checkConnectionHit(Rectangle rectangle, float offsetX, float offsetY) {
        for (Junction junction : this.game.getConnections(this.name)) {
            Endpoint endpoint = junction.contains(rectangle, offsetX, offsetY);
            if (endpoint != null) {
                return endpoint;
            }
        }
        return null;
    }

    Endpoint checkConnectionLeft(Rectangle rectangle, float offsetX, float offsetY) {
        for (Junction junction : this.game.getConnections(this.name)) {
            Endpoint endpoint = junction.intersect(rectangle, offsetX, offsetY);
            if (endpoint != null) {
                return endpoint;
            }
        }
        return null;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        this.game.getBatch().begin();

        this.getArea().render(this.game.getBatch());
        this.game.getPlayer().render();

        if (this.overlay != null) {
            overlay.render(Gdx.input, Gdx.graphics, this.game.getCamera(), this.game.getBatch());
        }

        this.game.getBatch().end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    public void dispose() {
        this.area.dispose();
    }
}
