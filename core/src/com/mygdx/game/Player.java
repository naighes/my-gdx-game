package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.game.controllers.PlayerState;
import com.mygdx.game.descriptors.PlayerDescriptor;

import java.util.Map;

enum Direction {
    LEFT, RIGHT, UP, DOWN
}

public class Player extends Character {
    private final MyGdxGame game;
    private final Map<String, Animation<TextureRegion>> animations;
    private final ObjectMap<Direction, Animation<TextureRegion>> directionToAnimation;
    private final PlayerDescriptor descriptor;

    private float currentSpeed;
    private Animation<TextureRegion> currentAnimation;
    private Direction currentDirection;
    private float currentAnimationElapsedTime = 0f;

    Player(MyGdxGame game,
           Scenario scenario,
           Vector2 position,
           Direction direction,
           PlayerDescriptor descriptor) {
        super(scenario, descriptor.offsetX, descriptor.offsetY);
        this.game = game;
        this.currentDirection = direction;
        this.currentSpeed = descriptor.speed;
        this.animations = descriptor.animations.getAnimations(game.getAssetManager());
        this.descriptor = descriptor;

        this.directionToAnimation = new ObjectMap<>();
        this.directionToAnimation.put(Direction.DOWN, this.animations.get("down"));
        this.directionToAnimation.put(Direction.UP, this.animations.get("up"));
        this.directionToAnimation.put(Direction.LEFT, this.animations.get("left"));
        this.directionToAnimation.put(Direction.RIGHT, this.animations.get("right"));

        this.currentAnimation = this.directionToAnimation.get(this.currentDirection);
        this.setBounds(position.x, position.y, descriptor.width, descriptor.height);
    }

    void update(float delta, PlayerState state) {
        float prevX = this.getX();
        float prevY = this.getY();

        this.currentDirection = this.getDirection(state, delta);
        this.currentSpeed = this.getSpeed(state);
        this.setMovement(delta, this.currentSpeed, this.currentDirection);
        this.currentAnimation = this.directionToAnimation.get(this.currentDirection);
        this.handleCollisions(state, prevX, prevY);
    }

    private void handleCollisions(PlayerState state,
                                  float prevX,
                                  float prevY) {
        if (state != PlayerState.NONE) {
            return;
        }

        if (this.getCollidedTile() != null) {
            this.setX(prevX);
            this.setY(prevY);
        }
    }

    private float getSpeed(PlayerState state) {
        switch (state) {
            case EXITING_SCENARIO:
                return 0f;
            case ENTERING_SCENARIO:
                return this.descriptor.transitionSpeed;
            case TALKING:
                return 0f;
            default:
                if (Gdx.input.isTouched()) {
                    return this.descriptor.speed;
                } else {
                    return 0f;
                }
        }
    }

    private Direction getDirection(PlayerState state, float delta) {
        if (state != PlayerState.NONE || !Gdx.input.isTouched()) {
            return this.currentDirection;
        }

        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        this.getScenario().getCamera().getInnerCamera().unproject(touchPos);

        float newX = touchPos.x;
        float currentX = this.getX() + this.getWidth() / 2;
        float deltaX = newX - currentX;

        float newY = touchPos.y;
        float currentY = this.getY() + this.getHeight() / 2;
        float deltaY = newY - currentY;

        Direction direction;

        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            if (deltaX > 0) {
                direction = Direction.RIGHT;
            } else {
                direction = Direction.LEFT;
            }
        } else {
            if (deltaY > 0) {
                direction = Direction.UP;
            } else {
                direction = Direction.DOWN;
            }
        }

        return direction;
    }

    void render() {
        this.currentAnimationElapsedTime += Gdx.graphics.getDeltaTime();
        TextureRegion frame = this.currentSpeed != 0f
                ? this.currentAnimation.getKeyFrame(this.currentAnimationElapsedTime, true)
                : this.currentAnimation.getKeyFrames()[0];
        this.setRegion(frame);
        this.draw(this.game.getBatch());
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }
}
