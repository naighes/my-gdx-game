package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
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
    private final Array<Guest> guests;
    private final PlayerStateController stateController;
    private final ScenarioDescriptor descriptor;
    private final DialogTextBoxDescriptor textBoxDescriptor;

    private boolean initialized = false;
    private Player player;
    private Area area;
    private Overlay overlay;
    private DialogTextBox tb;
    private Pair<String, Guest> pendingConversation;
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
        this.guests = new Array<>();
        this.pendingConversation = null;
        this.playerState = PlayerState.ENTERING_SCENARIO;
        this.stateController = new ExitingScenario(game,
                new EnteringScenario(game,
                        new InteractingWithGuest(game,
                                new EndingConversation())));
    }

    Area getArea() {
        return this.area;
    }

    public Player getPlayer() {
        return this.player;
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
                    Geometry.UP, // TODO: needs to be parametrized
                    guestDescriptor);
            this.guests.add(guest);
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

        this.tb = new DialogTextBox(this.game,
                textBoxTexture,
                font,
                this.textBoxDescriptor);

        this.initialized = true;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        this.camera.update(this);
        PlayerStateControllerResult result = this.stateController.advance(this,
                this.playerState);
        this.playerState = result.getState();
        this.player.update(delta, result.getState());
        result.process();

        for (Guest guest : this.guests) {
            guest.update(delta);
        }

        this.draw();
    }

    public Guest checkGuestCollisions() {
        for (Guest guest : this.guests) {
            if (this.intersect(this.player, guest)) {
                return guest;
            }
        }

        return null;
    }

    private boolean intersect(Player player, Guest guest) {
        Rectangle pRect = player.getBoundingRectangle();
        Rectangle gRect = guest.getBoundingRectangle();
        float pl = pRect.x + player.getOffsetX();
        float el = gRect.x + guest.getOffsetX();
        float pr = pRect.x + pRect.width - player.getOffsetY();
        float er = gRect.x + gRect.width - guest.getOffsetY();

        float pd = pRect.y + player.getOffsetY();
        float ed = gRect.y + guest.getOffsetY();
        float pu = pRect.y + pRect.height - player.getOffsetY();
        float eu = gRect.y + gRect.height - guest.getOffsetY();

        return pl < er && pr > el && pd < eu && pu > ed;
    }

    private void draw() {
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

        if (this.hasPendingConversation()) {
            if (this.tb.isConsumed() && Gdx.input.isTouched()) {
                this.pendingConversation.y.leaveConversation();
                this.pendingConversation = null;
                this.tb.reset();
            } else {
                this.tb.render(this.pendingConversation.x, 300f, 200f);
            }
        }

        this.game.getBatch().end();
    }

    public boolean hasPendingConversation() {
        return this.pendingConversation != null;
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

    boolean isInitialized() {
        return this.initialized;
    }

    GameCamera getCamera() {
        return camera;
    }

    void startConversation(String[] conversations, Guest guest) {
        this.pendingConversation = new Pair<>(conversations[0], guest);
    }
}
