package com.mygdx.game;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

class Background implements Disposable {
    private final Rectangle rectangle;
    private final String assetPath;

    private Texture texture;

    static Background New(Graphics graphics) {
        final String assetPath = "background.jpg";
        Rectangle r = new Rectangle(0f,
                0f,
                graphics.getWidth(),
                graphics.getHeight());
        return new Background(r, assetPath);
    }

    private Background(Rectangle rectangle,
                       String assetPath) {
        this.rectangle = rectangle;
        this.assetPath = assetPath;
    }

    public void create(Files files, Graphics graphics) {
        this.texture = new Texture(files.internal(this.assetPath));
    }

    public void render(Input input, Graphics graphics, Camera camera, Batch batch) {
        batch.draw(this.texture,
                this.rectangle.x,
                this.rectangle.y,
                this.texture.getWidth(),
                this.texture.getHeight());
    }

    @Override
    public void dispose() {
        this.texture.dispose();
    }
}
