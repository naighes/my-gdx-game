package com.mygdx.game;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.game.controllers.ConversationsController;
import com.mygdx.game.controllers.EndingConversation;
import com.mygdx.game.controllers.EnteringScenario;
import com.mygdx.game.controllers.ExitingScenario;
import com.mygdx.game.controllers.InteractingWithGuest;
import com.mygdx.game.controllers.PlayerState;
import com.mygdx.game.controllers.PlayerStateController;
import com.mygdx.game.controllers.PlayerStateControllerResult;
import com.mygdx.game.descriptors.DialogTextBoxDescriptor;
import com.mygdx.game.descriptors.GuestDescriptor;
import com.mygdx.game.descriptors.ScenarioDescriptor;
import com.mygdx.game.utils.Geometry;

public class Scenario extends ScreenAdapter {
    public final String name;
    private final MyGdxGame game;
    private final float x;
    private final float y;
    private final Vector2 playerPosition;
    private final Vector2 playerDirection;
    private final GameCamera camera;
    private final ObjectMap<String, Guest> guests;
    private final PlayerStateController playerStateController;
    private final ScenarioDescriptor descriptor;
    private final DialogTextBoxDescriptor textBoxDescriptor;

    private boolean initialized = false;
    private Player player;
    private Area area;
    private Overlay overlay;
    private ConversationsController conversationsController;
    private PlayerState playerState;

    Scenario(MyGdxGame game,
             String name,
             float x,
             float y,
             Vector2 playerPosition,
             Vector2 playerDirection,
             ScenarioDescriptor descriptor,
             DialogTextBoxDescriptor textBoxDescriptor) {
        this.game = game;
        this.name = name;
        this.x = x;
        this.y = y;
        this.playerPosition = playerPosition;
        this.playerDirection = playerDirection;
        this.descriptor = descriptor;
        this.textBoxDescriptor = textBoxDescriptor;
        this.camera = new GameCamera();
        this.guests = new ObjectMap<>();
        this.playerState = PlayerState.ENTERING_SCENARIO;
        this.playerStateController = new ExitingScenario(game,
                new EnteringScenario(game,
                        new InteractingWithGuest(game,
                                new EndingConversation())));
    }

    public Area getArea() {
        return this.area;
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public GameCamera getCamera() {
        return this.camera;
    }

    public ConversationsController getConversationsController() {
        return this.conversationsController;
    }

    public ObjectMap<String, Guest> getGuests() {
        return this.guests;
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

        this.player = new Player(this.game,
                this,
                this.playerPosition,
                this.playerDirection,
                this.descriptor.player);

        for (GuestDescriptor guestDescriptor : this.descriptor.guests) {
            Guest guest = new Guest(this.game,
                    this,
                    guestDescriptor.position,
                    Geometry.DOWN, // TODO: needs to be parametrized
                    guestDescriptor);
            this.guests.put(guest.getDescriptor().name, guest);
        }

        Texture texture = this.game.getAssetManager().get(this.descriptor.assetPath);
        Texture collisionTexture = this.game.getAssetManager().get(this.descriptor.collisionAssetPath);
        Texture overlayTexture = null;

        if (this.descriptor.overlayAssetPath != null) {
            overlayTexture = this.game.getAssetManager().get(this.descriptor.overlayAssetPath);
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

        Texture textBoxTexture = this.game.getAssetManager().get(this.textBoxDescriptor.assetPath);
        BitmapFont font = this.game.getAssetManager().get(this.textBoxDescriptor.fontName);

        this.conversationsController = new ConversationsController(new DialogTextBox(this.game,
                textBoxTexture,
                font,
                this.textBoxDescriptor));

        this.initialized = true;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        this.update(delta);
        this.draw(this.game.getBatch());
    }

    public void update(float delta) {
        this.camera.update(this);

        PlayerStateControllerResult result = this.playerStateController.advance(this,
                this.playerState);
        this.playerState = result.getState();
        this.player.update(delta, this.playerState);
        result.process();

        for (Guest guest : this.guests.values()) {
            guest.update(delta);
        }

        this.conversationsController.update(delta);
    }

    private void draw(Batch batch) {
        batch.setProjectionMatrix(this.camera.getInnerCamera().combined);
        batch.begin();
        this.getArea().draw(batch);
        this.player.draw(batch);

        for (Guest guest : this.guests.values()) {
            guest.draw(batch);
        }

        if (this.overlay != null) {
            overlay.draw(batch);
        }

        this.conversationsController.draw(batch);
        batch.end();
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
        this.game.getAssetManager().load(this.descriptor.assetPath, Texture.class);
        this.game.getAssetManager().load(this.descriptor.collisionAssetPath, Texture.class);
        this.game.getAssetManager().load(this.descriptor.player.animations.assetPath, Texture.class);

        if (this.descriptor.overlayAssetPath != null) {
            this.game.getAssetManager().load(this.descriptor.overlayAssetPath, Texture.class);
        }

        for (GuestDescriptor guest : this.descriptor.guests) {
            this.game.getAssetManager().load(guest.animations.assetPath, Texture.class);
        }

        this.game.getAssetManager().load(this.textBoxDescriptor.assetPath, Texture.class);
        this.game.getAssetManager().load(this.textBoxDescriptor.fontName, BitmapFont.class);
    }

    private void unloadAssets() {
        this.game.getAssetManager().unload(this.descriptor.assetPath);
        this.game.getAssetManager().unload(this.descriptor.collisionAssetPath);
        this.game.getAssetManager().unload(this.descriptor.player.animations.assetPath);

        if (this.descriptor.overlayAssetPath != null) {
            this.game.getAssetManager().unload(this.descriptor.overlayAssetPath);
        }

        for (GuestDescriptor guest : this.descriptor.guests) {
            this.game.getAssetManager().unload(guest.animations.assetPath);
        }

        this.game.getAssetManager().unload(this.textBoxDescriptor.assetPath);
        this.game.getAssetManager().unload(this.textBoxDescriptor.fontName);
    }

    @Override
    public void dispose() {
        this.unloadAssets();
    }
}

