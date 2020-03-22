package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.controllers.GuestDirectionController;
import com.mygdx.game.descriptors.GuestDescriptor;
import com.mygdx.game.utils.Geometry;

import java.util.HashMap;
import java.util.Map;

public class Guest extends Character {
    private final MyGdxGame game;
    private final Map<String, Animation<TextureRegion>> animations;
    private final Map<Vector2, Animation<TextureRegion>> directionToAnimation;
    private final GuestDescriptor descriptor;
    private final GuestDirectionController directionController;

    private float currentSpeed;
    private Animation<TextureRegion> currentAnimation;
    private Vector2 currentDirection;
    private float currentAnimationElapsedTime = 0f;

    public Guest(MyGdxGame game,
                 Scenario scenario,
                 Vector2 position,
                 Vector2 direction,
                 GuestDescriptor descriptor) {
        super(scenario, descriptor.offsetX, descriptor.offsetY);
        this.game = game;
        this.currentDirection = direction;
        this.descriptor = descriptor;
        this.animations = descriptor.animations.getAnimations(game.getAssetManager());
        this.setBounds(position.x, position.y, descriptor.width, descriptor.height);
        this.directionController = new GuestDirectionController(this);

        this.directionToAnimation = new HashMap<>();
        this.directionToAnimation.put(Geometry.DOWN, this.animations.get("down"));
        this.directionToAnimation.put(Geometry.UP, this.animations.get("up"));
        this.directionToAnimation.put(Geometry.LEFT, this.animations.get("left"));
        this.directionToAnimation.put(Geometry.RIGHT, this.animations.get("right"));

        this.currentAnimation = this.directionToAnimation.get(this.currentDirection);
    }

    void update(float delta) {
        float prevX = this.getX();
        float prevY = this.getY();

        this.setMovement(delta, this.currentSpeed, this.currentDirection);
        Tile collidedTile = this.getCollidedTile();
        this.currentDirection = this.directionController.update(delta,
                collidedTile,
                this.currentDirection);
        this.currentSpeed = this.getSpeed();
        this.currentAnimation = this.directionToAnimation.get(this.currentDirection);
        this.handleCollisions(prevX, prevY, collidedTile);
    }

    public String[] getAvailableConversations() {
        return this.descriptor.conversations;
    }

    float getSpeed() {
        return this.getScenario()
                .getConversationsController()
                .hasPendingConversationWith(this)
                ? 0f
                : this.descriptor.speed;
    }

    private void handleCollisions(float prevX, float prevY, Tile collidedTile) {
        if (collidedTile != null) {
            this.setX(prevX);
            this.setY(prevY);
        }
    }

    @Override
    public void draw(Batch batch) {
        this.currentAnimationElapsedTime += Gdx.graphics.getDeltaTime();
        TextureRegion frame = this.currentSpeed != 0f
                ? this.currentAnimation.getKeyFrame(this.currentAnimationElapsedTime, true)
                : this.currentAnimation.getKeyFrames()[0];
        this.setRegion(frame);

        super.draw(batch);
    }

    public boolean wannaTalk() {
        return this.descriptor.conversations.length > 0;
    }

    public GuestDescriptor getDescriptor() {
        return descriptor;
    }
}
