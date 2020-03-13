package com.mygdx.game;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

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
    public final Vector2 position;
    public final String assetPath;
}

public class Guest extends Sprite {
    private final MyGdxGame game;
    private final Scenario scenario;
    private final Rectangle rectangle;
    private final Texture texture;
    private final float speed;
    private final float offsetX;
    private final float offsetY;

    private Animation downAnimation;
    private Animation upAnimation;
    private Animation leftAnimation;
    private Animation rightAnimation;

    private Animation currentAnimation;

    private Direction currentDirection;

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
                  Rectangle rectangle,
                  Direction direction,
                  Texture texture,
                  float speed,
                  float offsetX,
                  float offsetY) {
        this.game = game;
        this.scenario = scenario;
        this.rectangle = rectangle;
        this.currentDirection = direction;
        this.texture = texture;
        this.speed = speed;
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

    void update(Input input, Graphics graphics, Camera camera) {
        float dt = graphics.getDeltaTime();
        this.timeSinceLastChange += dt;

        float prevX = this.rectangle.x;
        float prevY = this.rectangle.y;

        this.currentDirection = this.getDirection(dt);
        this.handleMovement(dt);
        this.handleCollisions(prevX, prevY, dt);
        this.currentAnimation.update(input, graphics, camera);
    }

    private void handleCollisions(float prevX, float prevY, float delta) {
        Tile tile = this.scenario
                .getArea()
                .collidesWith(this.rectangle,
                        this.offsetX,
                        this.offsetY);

        if (tile != null) {
            this.rectangle.x = prevX;
            this.rectangle.y = prevY;

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

            this.currentDirection = getDirection(delta);
        }
    }

    private void handleMovement(float dt) {
        switch (this.currentDirection) {
            case RIGHT:
                this.rectangle.x = this.rectangle.x + this.speed * dt;
                this.currentAnimation = this.rightAnimation;
                break;
            case LEFT:
                this.rectangle.x = this.rectangle.x - this.speed * dt;
                this.currentAnimation = this.leftAnimation;
                break;
            case UP:
                this.rectangle.y = this.rectangle.y + this.speed * dt;
                this.currentAnimation = this.upAnimation;
                break;
            case DOWN:
                this.rectangle.y = this.rectangle.y - this.speed * dt;
                this.currentAnimation = this.downAnimation;
                break;
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

    public Direction randDirection() {
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
        this.game.getBatch().draw(this.currentAnimation.getCurrentFrame(),
                this.rectangle.x,
                this.rectangle.y);
    }
}
