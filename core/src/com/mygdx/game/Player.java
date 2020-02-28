package com.mygdx.game;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

class Player {
    private final Rectangle rectangle;
    private final String assetPath;
    private final float speed;

    private Animation downAnimation;
    private Animation upAnimation;
    private Animation leftAnimation;
    private Animation rightAnimation;

    private Animation currentAnimation;

    private Player(Rectangle rectangle,
                   String assetPath,
                   float speed) {
        this.rectangle = rectangle;
        this.assetPath = assetPath;
        this.speed = speed;
    }

    static Player New(Graphics graphics) {
        final int width = 64;
        final int height = 64;
        final float speed = 240;
        final String assetPath = "player_1.png";
        Rectangle r = new Rectangle(graphics.getWidth() / 2 + width / 2,
                graphics.getHeight() / 2 + height / 2,
                width,
                height);
        return new Player(r, assetPath, speed);
    }

    public float getX() {
        return this.rectangle.x;
    }

    public float getY() {
        return this.rectangle.y;
    }

    public void create(Files files, Graphics graphics) {
        Texture texture = new Texture(files.internal(this.assetPath));
        this.downAnimation = Animation.New(texture,
                200,
                new Rectangle(0f, 0f, 0.25f, 0.25f),
                new Rectangle(0.25f, 0f, 0.5f, 0.25f),
                new Rectangle(0.5f, 0f, 0.75f, 0.25f),
                new Rectangle(0.75f, 0f, 1f, 0.25f));
        this.upAnimation = Animation.New(texture,
                200,
                new Rectangle(0f, 0.75f, 0.25f, 1f),
                new Rectangle(0.25f, 0.75f, 0.5f, 1f),
                new Rectangle(0.5f, 0.75f, 0.75f, 1f),
                new Rectangle(0.75f, 0.75f, 1f, 1f));
        this.leftAnimation = Animation.New(texture,
                200,
                new Rectangle(0f, 0.25f, 0.25f, 0.5f),
                new Rectangle(0.25f, 0.25f, 0.5f, 0.5f),
                new Rectangle(0.5f, 0.25f, 0.75f, 0.5f),
                new Rectangle(0.75f, 0.25f, 1f, 0.5f));
        this.rightAnimation = Animation.New(texture,
                200,
                new Rectangle(0f, 0.5f, 0.25f, 0.75f),
                new Rectangle(0.25f, 0.5f, 0.5f, 0.75f),
                new Rectangle(0.5f, 0.5f, 0.75f, 0.75f),
                new Rectangle(0.75f, 0.5f, 1f, 0.75f));
        this.currentAnimation = this.upAnimation;
    }

    public void render(Input input, Graphics graphics, Camera camera, Batch batch) {
        if (input.isTouched()) {
            float dt = graphics.getDeltaTime();

            Vector3 touchPos = new Vector3();
            touchPos.set(input.getX(), input.getY(), 0);
            camera.unproject(touchPos);

            float newX = touchPos.x;
            float currentX = this.rectangle.x + this.rectangle.width / 2;
            float deltaX = newX - currentX;

            float newY = touchPos.y;
            float currentY = this.rectangle.y + this.rectangle.height / 2;
            float deltaY = newY - currentY;

            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                if (deltaX > 0) {
                    this.rectangle.x = this.rectangle.x + this.speed * dt;
                    this.currentAnimation = this.rightAnimation;
                } else {
                    this.rectangle.x = this.rectangle.x - this.speed * dt;
                    this.currentAnimation = this.leftAnimation;
                }
            } else {
                if (deltaY > 0) {
                    this.rectangle.y = this.rectangle.y + this.speed * dt;
                    this.currentAnimation = this.upAnimation;
                } else {
                    this.rectangle.y = this.rectangle.y - this.speed * dt;
                    this.currentAnimation = this.downAnimation;
                }
            }

            this.currentAnimation.render(input, graphics, camera, batch);
        }

        batch.draw(this.currentAnimation.getCurrentFrame(), this.rectangle.x, this.rectangle.y);
    }
}
