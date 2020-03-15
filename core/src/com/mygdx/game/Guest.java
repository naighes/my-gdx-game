package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.game.descriptors.GuestDescriptor;

import java.util.Map;
import java.util.Random;

public class Guest extends Character {
    private final MyGdxGame game;
    private final Map<String, Animation<TextureRegion>> animations;
    private final ObjectMap<Direction, Animation<TextureRegion>> directionToAnimation;
    private final GuestDescriptor descriptor;
    private final Random rnd = new Random();

    private float currentSpeed;
    private Animation<TextureRegion> currentAnimation;
    private Direction currentDirection;
    private float currentAnimationElapsedTime = 0f;

    private float timeSinceLastChange;
    private int nextDirectionChangeAt = 0;
    private boolean conversationInPlace = false;
    private float timeSinceLastConversation = 2f;

    public Guest(MyGdxGame game,
                 Scenario scenario,
                 Vector2 position,
                 Direction direction,
                 GuestDescriptor descriptor) {
        super(scenario, descriptor.offsetX, descriptor.offsetY);
        this.game = game;
        this.currentDirection = direction;
        this.descriptor = descriptor;
        this.animations = descriptor.animations.getAnimations(game.getAssetManager());
        this.setBounds(position.x, position.y, descriptor.width, descriptor.height);

        this.directionToAnimation = new ObjectMap<>();
        this.directionToAnimation.put(Direction.DOWN, this.animations.get("down"));
        this.directionToAnimation.put(Direction.UP, this.animations.get("up"));
        this.directionToAnimation.put(Direction.LEFT, this.animations.get("left"));
        this.directionToAnimation.put(Direction.RIGHT, this.animations.get("right"));

        this.currentAnimation = this.directionToAnimation.get(this.currentDirection);
    }

    void update(float delta) {
        this.timeSinceLastChange += delta;

        float prevX = this.getX();
        float prevY = this.getY();

        this.currentDirection = this.getDirection(delta);
        this.currentSpeed = this.getSpeed();
        this.setMovement(delta, this.currentSpeed, this.currentDirection);
        this.currentAnimation = this.directionToAnimation.get(this.currentDirection);
        this.handleCollisions(prevX, prevY);

        if (!this.conversationInPlace) {
            this.timeSinceLastConversation += delta;
        }
    }

    float getSpeed() {
        return this.conversationInPlace
                ? 0f
                : this.descriptor.speed;
    }

    private void handleCollisions(float prevX, float prevY) {
        if (getCollidedTile() != null) {
            this.setX(prevX);
            this.setY(prevY);

            switch (this.currentDirection) {
                case DOWN:
                    this.currentDirection = Direction.UP;
                    break;
                case UP:
                    this.currentDirection = Direction.DOWN;
                    break;
                case LEFT:
                    this.currentDirection = Direction.RIGHT;
                    break;
                case RIGHT:
                    this.currentDirection = Direction.LEFT;
                    break;
            }
        }
    }

    private Direction getDirection(float delta) {
        if (this.timeSinceLastChange > this.nextDirectionChangeAt && !this.conversationInPlace) {
            // it'll automatically change direction in a range
            // between 2 and 10 seconds
            final int max = 10;
            final int min = 2;
            this.nextDirectionChangeAt = this.rnd.nextInt((max - min) + 1) + min;
            this.timeSinceLastChange = 0f;
            return randDirection();
        } else {
            this.timeSinceLastChange += delta;
            return this.currentDirection;
        }
    }

    private Direction randDirection() {
        int i = this.rnd.nextInt((4 - 1) + 1) + 1;
        switch (i) {
            case 1:
                return Direction.UP;
            case 2:
                return Direction.DOWN;
            case 3:
                return Direction.LEFT;
        }

        return Direction.RIGHT;
    }

    void render() {
        this.currentAnimationElapsedTime += Gdx.graphics.getDeltaTime();
        TextureRegion frame = this.currentSpeed != 0f
                ? this.currentAnimation.getKeyFrame(this.currentAnimationElapsedTime, true)
                : this.currentAnimation.getKeyFrames()[0];
        this.setRegion(frame);
        this.draw(this.game.getBatch());
    }

    public boolean wannaTalk() {
        return this.timeSinceLastConversation > 2f && this.descriptor.conversations.length > 0;
    }

    public void talk() {
        this.conversationInPlace = true;
        this.getScenario().startConversation(this.descriptor.conversations,
                this);
    }

    void leaveConversation() {
        this.timeSinceLastConversation = 0f;
        this.conversationInPlace = false;
    }
}
