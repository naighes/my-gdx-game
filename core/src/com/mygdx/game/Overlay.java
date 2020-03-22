package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

class Overlay {
    private final MyGdxGame game;
    private final float x;
    private final float y;
    private final Texture texture;

    Overlay(MyGdxGame game,
            float x,
            float y,
            Texture texture) {
        this.game = game;
        this.x = x;
        this.y = y;
        this.texture = texture;
    }

    void draw(Batch batch) {
        if (this.texture != null) {
            batch.draw(this.texture,
                    this.x,
                    this.y,
                    this.texture.getWidth(),
                    this.texture.getHeight());
        }
    }
}
