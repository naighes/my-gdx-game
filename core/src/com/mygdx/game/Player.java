package com.mygdx.game;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

enum Direction {
    NONE, LEFT, RIGHT, UP, DOWN
}

class Player extends Sprite {
    private final MyGdxGame game;
    private final Rectangle rectangle;
    private final Texture texture;
    private final float speed;
    private final float transitionSpeed;
    private final float offsetX;
    private final float offsetY;

    private Animation downAnimation;
    private Animation upAnimation;
    private Animation leftAnimation;
    private Animation rightAnimation;

    private Animation currentAnimation;

    private Direction currentDirection;
    private float currentSpeed;

    private Player(MyGdxGame game,
                   Rectangle rectangle,
                   Direction direction,
                   Texture texture,
                   float speed,
                   float offsetX,
                   float offsetY) {
        this.game = game;
        this.rectangle = rectangle;
        this.currentDirection = direction;
        this.texture = texture;
        this.speed = speed;
        this.currentSpeed = speed;
        this.transitionSpeed = speed / 4f;
        this.offsetX = offsetX;
        this.offsetY = offsetY;

        this.downAnimation = Animation.New(this.texture,
                200,
                new Rectangle(0f, 0f, 0.25f, 0.25f),
                new Rectangle(0.25f, 0f, 0.5f, 0.25f),
                new Rectangle(0.5f, 0f, 0.75f, 0.25f),
                new Rectangle(0.75f, 0f, 1f, 0.25f));
        this.upAnimation = Animation.New(this.texture,
                200,
                new Rectangle(0f, 0.75f, 0.25f, 1f),
                new Rectangle(0.25f, 0.75f, 0.5f, 1f),
                new Rectangle(0.5f, 0.75f, 0.75f, 1f),
                new Rectangle(0.75f, 0.75f, 1f, 1f));
        this.leftAnimation = Animation.New(this.texture,
                200,
                new Rectangle(0f, 0.25f, 0.25f, 0.5f),
                new Rectangle(0.25f, 0.25f, 0.5f, 0.5f),
                new Rectangle(0.5f, 0.25f, 0.75f, 0.5f),
                new Rectangle(0.75f, 0.25f, 1f, 0.5f));
        this.rightAnimation = Animation.New(this.texture,
                200,
                new Rectangle(0f, 0.5f, 0.25f, 0.75f),
                new Rectangle(0.25f, 0.5f, 0.5f, 0.75f),
                new Rectangle(0.5f, 0.5f, 0.75f, 0.75f),
                new Rectangle(0.75f, 0.5f, 1f, 0.75f));

        switch (direction) {
            case UP:
                this.currentAnimation = this.upAnimation;
                break;
            case DOWN:
                this.currentAnimation = this.downAnimation;
                break;
            case LEFT:
                this.currentAnimation = this.leftAnimation;
                break;
            case RIGHT:
                this.currentAnimation = this.rightAnimation;
                break;
            default:
                this.currentAnimation = this.upAnimation;
                break;
        }
    }

    static Player New(MyGdxGame game,
                      Texture texture,
                      Vector2 position,
                      Direction direction) {
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
                r,
                direction,
                texture,
                speed,
                offsetX,
                offsetY);
    }

    public float getX() {
        return this.rectangle.x;
    }

    public void setX(float x) {
        this.rectangle.x = x;
    }

    public float getY() {
        return this.rectangle.y;
    }

    public void setY(float y) {
        this.rectangle.y = y;
    }

    void update(Input input, Graphics graphics, Camera camera) {
        float prevX = this.rectangle.x;
        float prevY = this.rectangle.y;
        float dt = graphics.getDeltaTime();

        if (input.isTouched()) {
            if (!this.game.isChangingScenario()) {
                this.currentDirection = this.getDirection(input, camera);
                this.currentSpeed = speed;
            } else {
                this.currentSpeed = transitionSpeed;
            }
        } else {
            if (!this.game.isChangingScenario()) {
                this.currentDirection = Direction.NONE;
            } else {
                this.currentSpeed = transitionSpeed;
            }
        }

        this.handleScenario();
        this.handleMovement(dt);
        this.handleCollisions(prevX, prevY);

        if (this.currentDirection != Direction.NONE) {
            this.currentAnimation.update(input, graphics, camera);
        }
    }

    private void handleScenario() {
        Scenario currentScenario = this.game.getCurrentScenario();

        if (!this.game.isChangingScenario()) {
            Endpoint endpoint = currentScenario
                    .checkConnectionHit(this.rectangle, this.offsetX, this.offsetY);

            if (endpoint != null) {
                this.game.setCurrentScenario(
                        endpoint.scenarioName,
                        new Vector2((endpoint.collisionArea.x + (endpoint.collisionArea.width / 2f)) - (this.rectangle.width / 2f) + this.offsetX,
                                (endpoint.collisionArea.y + (endpoint.collisionArea.height / 2f)) - (this.rectangle.height / 2f) + this.offsetY),
                        this.currentDirection);
            }
        } else {
            Endpoint endpoint = currentScenario
                    .checkConnectionLeft(this.rectangle, this.offsetX, this.offsetY);

            if (endpoint == null) {
                this.game.scenarioChanged();
            }
        }
    }

    private void handleCollisions(float prevX, float prevY) {
        if (this.game.isChangingScenario()) {
            return;
        }

        Tile tile = this.game.getCurrentScenario()
                .getArea()
                .collidesWith(this.rectangle,
                        this.offsetX,
                        this.offsetY);

        if (tile != null) {
            this.rectangle.x = prevX;
            this.rectangle.y = prevY;
        }
    }

    private void handleMovement(float dt) {
        switch (this.currentDirection) {
            case RIGHT:
                this.rectangle.x = this.rectangle.x + this.currentSpeed * dt;
                this.currentAnimation = this.rightAnimation;
                break;
            case LEFT:
                this.rectangle.x = this.rectangle.x - this.currentSpeed * dt;
                this.currentAnimation = this.leftAnimation;
                break;
            case UP:
                this.rectangle.y = this.rectangle.y + this.currentSpeed * dt;
                this.currentAnimation = this.upAnimation;
                break;
            case DOWN:
                this.rectangle.y = this.rectangle.y - this.currentSpeed * dt;
                this.currentAnimation = this.downAnimation;
                break;
        }
    }

    private Direction getDirection(Input input, Camera camera) {
        Vector3 touchPos = new Vector3();
        touchPos.set(input.getX(), input.getY(), 0);
        camera.unproject(touchPos);

        float newX = touchPos.x;
        float currentX = this.rectangle.x + this.rectangle.width / 2;
        float deltaX = newX - currentX;

        float newY = touchPos.y;
        float currentY = this.rectangle.y + this.rectangle.height / 2;
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
        this.game.getBatch().draw(this.currentAnimation.getCurrentFrame(),
                this.rectangle.x,
                this.rectangle.y);
    }
}
