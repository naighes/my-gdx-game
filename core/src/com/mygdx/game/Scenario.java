package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

class ScenarioDescriptor {
    ScenarioDescriptor(String name,
                       Vector2 playerPosition,
                       String assetPath,
                       String collisionAssetPath,
                       String overlayAssetPath,
                       String playerAssetPath,
                       Array<GuestDescriptor> guests) {
        this.name = name;
        this.playerPosition = playerPosition;
        this.assetPath = assetPath;
        this.collisionAssetPath = collisionAssetPath;
        this.overlayAssetPath = overlayAssetPath;
        this.playerAssetPath = playerAssetPath;
        this.guests = guests;
    }

    public final String name;
    public final Vector2 playerPosition;
    public final String assetPath;
    public final String collisionAssetPath;
    public final String overlayAssetPath;
    public final String playerAssetPath;
    public final Array<GuestDescriptor> guests;
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
    private final Array<GuestDescriptor> guestDescriptors;
    private final Array<Guest> guests;

    private boolean initialized = false;
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
             String playerAssetPath,
             Array<GuestDescriptor> guests) {
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
        this.guestDescriptors = guests;
        this.guests = new Array<>();
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

        init();
    }

    void init() {
        if (this.initialized) {
            return;
        }

        Texture playerTexture = this.game.getAssetManager().get(this.playerAssetPath);
        this.player = Player.New(this.game,
                this,
                playerTexture,
                this.playerPosition,
                this.playerDirection);

        for (GuestDescriptor guestDescriptor : this.guestDescriptors) {
            Texture guestTexture = this.game.getAssetManager().get(guestDescriptor.assetPath);
            Guest guest = Guest.New(this.game,
                    this,
                    guestTexture,
                    guestDescriptor.position,
                    Direction.UP);
            this.guests.add(guest);
        }

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

        this.initialized = true;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        this.camera.update(this);
        this.player.update(Gdx.input, Gdx.graphics, this.camera.getInnerCamera());

        for (Guest guest : this.guests) {
            guest.update(Gdx.input, Gdx.graphics, this.camera.getInnerCamera());
        }

        this.draw();
    }

    void draw() {
        this.game.getBatch().setProjectionMatrix(this.camera.getInnerCamera().combined);
        this.game.getBatch().begin();
        this.getArea().render();
        this.player.render();

        for (Guest guest : this.guests) {
            guest.render();
        }

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

        for (GuestDescriptor guest : this.guestDescriptors) {
            this.game.getAssetManager().load(guest.assetPath, Texture.class);
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

    boolean isInitialized() {
        return this.initialized;
    }
}
