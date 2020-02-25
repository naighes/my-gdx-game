package com.mygdx.game;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

class Bucket implements Disposable {
    private final Rectangle rectangle;
    private final String assetPath;

    private Texture texture;

    private Bucket(Rectangle rectangle,
                   String assetPath) {
        this.rectangle = rectangle;
        this.assetPath = assetPath;
    }

    static Bucket New(Graphics graphics) {
        final int yOffset = 20;
        final int width = 64;
        final int height = 64;
        final String assetPath = "bucket.png";
        Rectangle r = new Rectangle(graphics.getWidth() / 2 - width / 2,
                yOffset,
                width,
                height);
        return new Bucket(r, assetPath);
    }

    public void create(Files files, Graphics graphics) {
        this.texture = new Texture(files.internal(this.assetPath));
    }

    public void render(Input input, Graphics graphics, Camera camera, Batch batch) {
        if (input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(input.getX(), input.getY(), 0);
            camera.unproject(touchPos);
            this.rectangle.x = touchPos.x - this.rectangle.width / 2;
        }

        if (this.rectangle.x < 0) {
            this.rectangle.x = 0;
        }

        if (this.rectangle.x > graphics.getWidth() - this.rectangle.width) {
            this.rectangle.x = graphics.getWidth() - this.rectangle.width;
        }

        batch.draw(this.texture, this.rectangle.x, this.rectangle.y);
    }

    @Override
    public void dispose() {
        this.texture.dispose();
    }
}
