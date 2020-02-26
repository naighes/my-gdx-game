package com.mygdx.game;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

class Player {
    private final int width = 64;
    private final int height = 64;

    private final String assetPath;
    private final Array<TextureRegion> regions;

    private int x;
    private int y;
    private Texture texture;
    private long lastDropTime;
    private int animationIndex = 0;

    private Player(String assetPath) {
        this.assetPath = assetPath;
        this.regions = new Array<>();
    }

    static Player New(Graphics graphics) {
        final String assetPath = "player_1.png";
        return new Player(assetPath);
    }

    public void create(Files files, Graphics graphics) {
        this.lastDropTime = TimeUtils.nanoTime();
        this.texture = new Texture(files.internal(this.assetPath));
        this.regions.add(new TextureRegion(this.texture,
                0,
                0,
                width,
                height));
        this.regions.add(new TextureRegion(this.texture,
                64,
                0,
                width,
                height));
        this.regions.add(new TextureRegion(this.texture,
                128,
                0,
                width,
                height));
        this.regions.add(new TextureRegion(this.texture,
                192,
                0,
                width,
                height));
    }

    public void render(Input input, Graphics graphics, Camera camera, Batch batch) {
        if (TimeUtils.nanoTime() - this.lastDropTime > TimeUtils.millisToNanos(200)) {
            this.lastDropTime = TimeUtils.nanoTime();
            this.animationIndex = this.animationIndex + 1;

            if (this.animationIndex >= this.regions.size) {
                this.animationIndex = 0;
            }
        }
        batch.draw(this.regions.get(this.animationIndex), this.x, this.y);
    }
}
