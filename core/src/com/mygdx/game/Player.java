package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.game.controllers.PlayerState;
import com.mygdx.game.descriptors.PlayerDescriptor;
import com.mygdx.game.utils.Geometry;

import java.util.Map;

public class Player extends Character {
    private final MyGdxGame game;
    private final Map<String, Animation<TextureRegion>> animations;
    private final ObjectMap<Vector2, Animation<TextureRegion>> directionToAnimation;
    private final PlayerDescriptor descriptor;

    private float currentSpeed;
    private Animation<TextureRegion> currentAnimation;
    private Vector2 currentDirection;
    private float currentAnimationElapsedTime = 0f;

    Player(MyGdxGame game,
           Scenario scenario,
           Vector2 position,
           Vector2 direction,
           PlayerDescriptor descriptor) {
        super(scenario, descriptor.offsetX, descriptor.offsetY);

        this.game = game;
        this.currentDirection = direction;
        this.currentSpeed = descriptor.speed;
        this.animations = descriptor.animations.getAnimations(game.getAssetManager());
        this.descriptor = descriptor;

        this.directionToAnimation = new ObjectMap<>();
        this.directionToAnimation.put(Geometry.DOWN, this.animations.get("down"));
        this.directionToAnimation.put(Geometry.UP, this.animations.get("up"));
        this.directionToAnimation.put(Geometry.LEFT, this.animations.get("left"));
        this.directionToAnimation.put(Geometry.RIGHT, this.animations.get("right"));

        this.currentAnimation = this.directionToAnimation.get(this.currentDirection);
        this.setBounds(position.x, position.y, descriptor.width, descriptor.height);
    }

    void update(float delta, PlayerState state) {
        float prevX = this.getX();
        float prevY = this.getY();

        this.setMovement(delta, this.currentSpeed, this.currentDirection);
        this.currentDirection = this.getDirection(state, delta);
        this.currentSpeed = this.getSpeed(state);
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

    private Vector2 getDirection(PlayerState state, float delta) {
        if (state != PlayerState.NONE || !Gdx.input.isTouched()) {
            return this.currentDirection;
        }

        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        this.getScenario().getCamera().unproject(touchPos);

        float newX = touchPos.x;
        float currentX = this.getX() + this.getWidth() / 2;
        float deltaX = newX - currentX;

        float newY = touchPos.y;
        float currentY = this.getY() + this.getHeight() / 2;
        float deltaY = newY - currentY;

        Vector2 direction;

        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            if (deltaX > 0) {
                direction = Geometry.RIGHT;
            } else {
                direction = Geometry.LEFT;
            }
        } else {
            if (deltaY > 0) {
                direction = Geometry.UP;
            } else {
                direction = Geometry.DOWN;
            }
        }

        return direction;
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

    public Vector2 getCurrentDirection() {
        return this.currentDirection;
    }

    public Guest checkGuestCollisions() {
        for (Guest guest : this.getScenario().getGuests().values()) {
            if (this.intersect(guest)) {
                return guest;
            }
        }

        return null;
    }
}
