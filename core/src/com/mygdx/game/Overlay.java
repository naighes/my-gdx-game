package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;

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

    void render() {
        if (this.texture != null) {
            this.game.getBatch().draw(this.texture,
                    this.x,
                    this.y,
                    this.texture.getWidth(),
                    this.texture.getHeight());
        }
    }
}
