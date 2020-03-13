package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;

enum Direction {
    LEFT, RIGHT, UP, DOWN
}

enum PlayerState {
    EXITING_SCENARIO,
    ENTERING_SCENARIO,
    NONE
}

class PlayerStateController {
    private final MyGdxGame game;
    private final Player player;

    private PlayerState state;

    PlayerStateController(MyGdxGame game,
                          Player player,
                          PlayerState initialState) {
        this.game = game;
        this.player = player;
        this.state = initialState;
    }

    void update() {
        Rectangle bounds = this.player.getBoundingRectangle();

        if (this.state == PlayerState.NONE) {
            // check if the player attempts to enter a new scenario
            Endpoint endpoint = this.game.getConnections()
                    .checkConnectionHit(this.player.getScenario().name,
                            bounds,
                            this.player.getOffsetX(),
                            this.player.getOffsetY());

            if (endpoint != null) {
                this.state = PlayerState.EXITING_SCENARIO;
                this.game.setCurrentScenario(
                        endpoint.scenarioName,
                        endpoint.getCenterAgainst(this.player),
                        this.player.getCurrentDirection());
            }

            return;
        }

        if (this.state == PlayerState.ENTERING_SCENARIO) {
            // check if the player is into the new scenario and
            // out of the connection
            Endpoint endpoint = this.game.getConnections()
                    .checkConnectionLeft(this.player.getScenario().name,
                            bounds,
                            this.player.getOffsetX(),
                            this.player.getOffsetY());

            if (endpoint == null) {
                this.state = PlayerState.NONE;
            }

            return;
        }
    }

    public PlayerState getState() {
        return this.state;
    }
}

class Player extends Character {
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

    private final float transitionSpeed;
    private final PlayerStateController stateController;

    private Player(MyGdxGame game,
                   Scenario scenario,
                   Rectangle bounds,
                   Direction direction,
                   Texture texture,
                   float speed,
                   float offsetX,
                   float offsetY,
                   PlayerState state) {
        super(scenario, offsetX, offsetY);
        this.game = game;
        this.currentDirection = direction;
        this.texture = texture;
        this.speed = speed;
        this.currentSpeed = speed;
        this.transitionSpeed = speed / 4f;
        this.stateController = new PlayerStateController(game, this, state);
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
        this.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    static Player New(MyGdxGame game,
                      Scenario scenario,
                      Texture texture,
                      Vector2 position,
                      Direction direction,
                      PlayerState state) {
        final int width = 64;
        final int height = 64;
        final float speed = 240;
        final float offsetX = 14f;
        final float offsetY = 5f;
        Rectangle r = new Rectangle(position.x,
                position.y,
                width,
                height);
        return new Player(game,
                scenario,
                r,
                direction,
                texture,
                speed,
                offsetX,
                offsetY,
                state);
    }

    void update(float delta) {
        this.stateController.update();

        float prevX = this.getX();
        float prevY = this.getY();

        this.currentDirection = this.getDirection(delta);
        this.currentSpeed = this.getSpeed();
        this.setMovement(delta, this.currentSpeed, this.currentDirection);
        this.currentAnimation = this.directionToAnimation.get(this.currentDirection);
        this.handleCollisions(prevX, prevY);
    }

    private void handleCollisions(float prevX, float prevY) {
        if (this.stateController.getState() != PlayerState.NONE) {
            return;
        }

        if (this.getCollidedTile() != null) {
            this.setX(prevX);
            this.setY(prevY);
        }
    }

    private float getSpeed() {
        switch (this.stateController.getState()) {
            case EXITING_SCENARIO:
                return 0f;
            case ENTERING_SCENARIO:
                return this.transitionSpeed;
            default:
                if (Gdx.input.isTouched()) {
                    return this.speed;
                } else {
                    return 0f;
                }
        }
    }

    private Direction getDirection(float delta) {
        if (this.stateController.getState() != PlayerState.NONE || !Gdx.input.isTouched()) {
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
        this.game.getBatch().draw(frame,
                this.getX(),
                this.getY());
    }

    Direction getCurrentDirection() {
        return currentDirection;
    }
}
