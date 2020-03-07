package com.mygdx.game;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

class Animation {
    private final long frameDurationInMilliseconds;
    private final Array<TextureRegion> frames;

    private long lastUpdate;
    private int currentFrameIndex = 0;

    public TextureRegion getCurrentFrame() {
        return this.frames.get(this.currentFrameIndex);
    }

    public Animation(long frameDurationInMilliseconds, Array<TextureRegion> frames) {
        this.frameDurationInMilliseconds = frameDurationInMilliseconds;
        this.frames = frames;
        this.lastUpdate = TimeUtils.nanoTime();
    }

    static Animation New(Texture texture, long frameDurationInMilliseconds, Rectangle... frames) {
        Array<TextureRegion> regions = new Array<>();

        for (Rectangle r : frames) {
            regions.add(new TextureRegion(texture, r.x, r.y, r.width, r.height));
        }

        return new Animation(frameDurationInMilliseconds, regions);
    }

    public void update(Input input, Graphics graphics, Camera camera) {
        if (TimeUtils.nanoTime() - this.lastUpdate > TimeUtils.millisToNanos(this.frameDurationInMilliseconds)) {
            this.lastUpdate = TimeUtils.nanoTime();
            this.currentFrameIndex = (this.currentFrameIndex + 1) % this.frames.size;
        }
    }
}
