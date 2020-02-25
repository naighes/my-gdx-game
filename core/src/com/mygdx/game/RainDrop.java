package com.mygdx.game;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

class RainDrop {
    private final Rectangle rectangle;
    private final Texture texture;

    private boolean outOfBound = false;

    boolean isOutOfBound() {
        return this.outOfBound;
    }

    private RainDrop(Rectangle rectangle, Texture texture) {
        this.rectangle = rectangle;
        this.texture = texture;
    }

    static RainDrop New(Graphics graphics, Texture texture) {
        final int width = 64;
        final int height = 64;
        Rectangle r = new Rectangle(MathUtils.random(0, graphics.getWidth() - width),
                graphics.getHeight(),
                width,
                height);
        return new RainDrop(r, texture);
    }

    public void create(Files files, Graphics graphics) {
    }

    void render(Input input, Graphics graphics, Camera camera, Batch batch) {
        final int speed = 200;

        this.rectangle.y -= speed * graphics.getDeltaTime();

        if (this.rectangle.y + this.rectangle.height < 0) {
            this.outOfBound = true;
        }

        if (!this.outOfBound) {
            batch.draw(this.texture, this.rectangle.x, this.rectangle.y);
        }
    }
}
