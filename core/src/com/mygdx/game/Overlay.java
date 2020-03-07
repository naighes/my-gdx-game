package com.mygdx.game;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Disposable;

class Overlay implements Disposable {
    private final float x;
    private final float y;
    private final String assetPath;

    private Texture texture;

    Overlay(float x,
            float y,
            String assetPath) {
        this.x = x;
        this.y = y;
        this.assetPath = assetPath;
    }

    void create(Files files, Graphics graphics) {
        this.texture = new Texture(files.internal(this.assetPath));
    }

    void render(Input input, Graphics graphics, Camera camera, Batch batch) {
        if (this.texture != null) {
            batch.draw(this.texture,
                    this.x,
                    this.y,
                    this.texture.getWidth(),
                    this.texture.getHeight());
        }
    }

    @Override
    public void dispose() {
        this.texture.dispose();
    }
}
