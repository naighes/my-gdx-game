package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

class ScenarioDescriptor {
    ScenarioDescriptor(String name,
                       Vector2 playerPosition,
                       String assetPath,
                       String collisionAssetPath,
                       String overlayAssetPath,
                       String playerAssetPath) {
        this.name = name;
        this.playerPosition = playerPosition;
        this.assetPath = assetPath;
        this.collisionAssetPath = collisionAssetPath;
        this.overlayAssetPath = overlayAssetPath;
        this.playerAssetPath = playerAssetPath;
    }

    public final String name;
    public final Vector2 playerPosition;
    public final String assetPath;
    public final String collisionAssetPath;
    public final String overlayAssetPath;
    public final String playerAssetPath;
}

public class Scenario extends ScreenAdapter {
    private final MyGdxGame game;
    private final String name;
    private final float x;
    private final float y;
    private final Vector2 playerPosition;
    private final Direction playerDirection;
    private final String assetPath;
    private final String collisionAssetPath;
    private final String overlayAssetPath;
    private final String playerAssetPath;
    private final GameCamera camera;

    private Player player;
    private Area area;
    private Overlay overlay;

    Scenario(MyGdxGame game,
             String name,
             float x,
             float y,
             Vector2 playerPosition,
             Direction playerDirection,
             String assetPath,
             String collisionAssetPath,
             String overlayAssetPath,
             String playerAssetPath) {
        this.game = game;
        this.name = name;
        this.x = x;
        this.y = y;
        this.playerPosition = playerPosition;
        this.playerDirection = playerDirection;
        this.assetPath = assetPath;
        this.collisionAssetPath = collisionAssetPath;
        this.overlayAssetPath = overlayAssetPath;
        this.playerAssetPath = playerAssetPath;
        this.camera = new GameCamera();
    }

    Area getArea() {
        return this.area;
    }

    Player getPlayer() {
        return this.player;
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
        super.show();

        Texture playerTexture = this.game.getAssetManager().get(this.playerAssetPath);
        this.player = Player.New(this.game,
                playerTexture,
                this.playerPosition,
                this.playerDirection);
        Texture texture = this.game.getAssetManager().get(this.assetPath);
        Texture collisionTexture = this.game.getAssetManager().get(this.collisionAssetPath);
        Texture overlayTexture = null;

        if (this.overlayAssetPath != null) {
            overlayTexture = this.game.getAssetManager().get(this.overlayAssetPath);
        }

        this.area = new Area(this.game,
                this.x,
                this.y,
                texture,
                collisionTexture);
        this.area.init();

        if (overlayTexture != null) {
            this.overlay = new Overlay(this.game, this.x, this.y, overlayTexture);
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        this.camera.update(this);
        this.player.update(Gdx.input, Gdx.graphics, this.camera.getInnerCamera());

        this.game.getBatch().setProjectionMatrix(this.camera.getInnerCamera().combined);
        this.game.getBatch().begin();
        this.getArea().render();
        this.player.render();

        if (this.overlay != null) {
            overlay.render();
        }

        this.game.getBatch().end();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void hide() {
        super.hide();
    }

    void loadAssets() {
        this.game.getAssetManager().load(this.assetPath, Texture.class);
        this.game.getAssetManager().load(this.collisionAssetPath, Texture.class);
        this.game.getAssetManager().load(this.playerAssetPath, Texture.class);

        if (this.overlayAssetPath != null) {
            this.game.getAssetManager().load(this.overlayAssetPath, Texture.class);
        }
    }

    void unloadAssets() {
        this.game.getAssetManager().unload(this.assetPath);
        this.game.getAssetManager().unload(this.collisionAssetPath);
        this.game.getAssetManager().unload(this.playerAssetPath);

        if (this.overlayAssetPath != null) {
            this.game.getAssetManager().unload(this.overlayAssetPath);
        }
    }

    @Override
    public void dispose() {
        this.unloadAssets();
    }
}
