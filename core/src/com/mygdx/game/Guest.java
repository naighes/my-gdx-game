package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.Random;

class GuestDescriptor {
    GuestDescriptor(String name,
                    Vector2 position,
                    String assetPath) {
        this.name = name;
        this.position = position;
        this.assetPath = assetPath;
    }

    public final String name;
    final Vector2 position;
    final String assetPath;
}

class Guest extends Character {
    private final float FRAME_DURATION = 0.25f;

    private final MyGdxGame game;
    private final Texture texture;
    private final ObjectMap<String, com.badlogic.gdx.graphics.g2d.Animation<TextureRegion>> animations;
    private final ObjectMap<Direction, com.badlogic.gdx.graphics.g2d.Animation<TextureRegion>> directionToAnimation;

    private float currentSpeed;
    private com.badlogic.gdx.graphics.g2d.Animation<TextureRegion> currentAnimation;
    private Direction currentDirection;
    private float currentAnimationElapsedTime = 0f;
    private final float speed;

    private float timeSinceLastChange;
    private final Random rnd = new Random();

    static Guest New(MyGdxGame game,
                     Scenario scenario,
                     Texture texture,
                     Vector2 position,
                     Direction direction) {
        final int width = 64;
        final int height = 64;
        final float speed = 100;
        final float offsetX = 14f;
        final float offsetY = 5f;
        Rectangle r = new Rectangle(position.x,
                position.y,
                width,
                height);
        return new Guest(game,
                scenario,
                r,
                direction,
                texture,
                speed,
                offsetX,
                offsetY);
    }

    private Guest(MyGdxGame game,
                  Scenario scenario,
                  Rectangle bounds,
                  Direction direction,
                  Texture texture,
                  float speed,
                  float offsetX,
                  float offsetY) {
        super(scenario, offsetX, offsetY);
        this.game = game;
        this.currentDirection = direction;
        this.texture = texture;
        this.speed = speed;
        this.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
        this.animations = new ObjectMap<>();
        this.animations.put("down",
                new com.badlogic.gdx.graphics.g2d.Animation<>(
                        FRAME_DURATION,
                        Animation.getTextureRegion(
                                this.texture,
                                new Rectangle(0f, 0f, 0.25f, 0.25f)
                        ),
                        Animation.getTextureRegion(
                                this.texture,
                                new Rectangle(0.25f, 0f, 0.5f, 0.25f)
                        ),
                        Animation.getTextureRegion(
                                this.texture,
                                new Rectangle(0.5f, 0f, 0.75f, 0.25f)
                        ),
                        Animation.getTextureRegion(
                                this.texture,
                                new Rectangle(0.75f, 0f, 1f, 0.25f)
                        ))
        );
        this.animations.put("up",
                new com.badlogic.gdx.graphics.g2d.Animation<>(
                        FRAME_DURATION,
                        Animation.getTextureRegion(
                                this.texture,
                                new Rectangle(0f, 0.75f, 0.25f, 1f)
                        ),
                        Animation.getTextureRegion(
                                this.texture,
                                new Rectangle(0.25f, 0.75f, 0.5f, 1f)
                        ),
                        Animation.getTextureRegion(
                                this.texture,
                                new Rectangle(0.5f, 0.75f, 0.75f, 1f)
                        ),
                        Animation.getTextureRegion(
                                this.texture,
                                new Rectangle(0.75f, 0.75f, 1f, 1f)
                        ))
        );
        this.animations.put("left",
                new com.badlogic.gdx.graphics.g2d.Animation<>(
                        FRAME_DURATION,
                        Animation.getTextureRegion(
                                this.texture,
                                new Rectangle(0f, 0.25f, 0.25f, 0.5f)
                        ),
                        Animation.getTextureRegion(
                                this.texture,
                                new Rectangle(0.25f, 0.25f, 0.5f, 0.5f)
                        ),
                        Animation.getTextureRegion(
                                this.texture,
                                new Rectangle(0.5f, 0.25f, 0.75f, 0.5f)
                        ),
                        Animation.getTextureRegion(
                                this.texture,
                                new Rectangle(0.75f, 0.25f, 1f, 0.5f)
                        ))
        );
        this.animations.put("right",
                new com.badlogic.gdx.graphics.g2d.Animation<>(
                        FRAME_DURATION,
                        Animation.getTextureRegion(
                                this.texture,
                                new Rectangle(0f, 0.5f, 0.25f, 0.75f)
                        ),
                        Animation.getTextureRegion(
                                this.texture,
                                new Rectangle(0.25f, 0.5f, 0.5f, 0.75f)
                        ),
                        Animation.getTextureRegion(
                                this.texture,
                                new Rectangle(0.5f, 0.5f, 0.75f, 0.75f)
                        ),
                        Animation.getTextureRegion(
                                this.texture,
                                new Rectangle(0.75f, 0.5f, 1f, 0.75f)
                        ))
        );

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
    }

    float getSpeed() {
        return this.speed;
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
        if (this.timeSinceLastChange > 10f) {
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
        this.game.getBatch().draw(frame,
                this.getX(),
                this.getY());
    }
}
