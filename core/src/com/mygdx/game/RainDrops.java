package com.mygdx.game;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

class RainDrops implements Disposable {
    private final String assetPath;
    private final Array<RainDrop> rainDrops;

    private Texture texture;
    private long lastDropTime;

    private RainDrops(String assetPath) {
        this.assetPath = assetPath;
        this.rainDrops = new Array<>();
    }

    static RainDrops New(Graphics graphics) {
        final String assetPath = "droplet.png";
        return new RainDrops(assetPath);
    }

    public void create(Files files, Graphics graphics) {
        this.texture = new Texture(files.internal(this.assetPath));
        addRainDrop(graphics);
    }

    private void addRainDrop(Graphics graphics) {
        RainDrop raindrop = RainDrop.New(graphics, this.texture);
        this.rainDrops.add(raindrop);
        this.lastDropTime = TimeUtils.nanoTime();
    }

    public void render(Input input, Graphics graphics, Camera camera, Batch batch) {
        if (TimeUtils.nanoTime() - this.lastDropTime > TimeUtils.millisToNanos(1000)) {
            addRainDrop(graphics);
        }

        for (Iterator<RainDrop> iter = this.rainDrops.iterator(); iter.hasNext(); ) {
            RainDrop raindrop = iter.next();
            if (raindrop.isOutOfBound()) {
                iter.remove();
            }
        }

        for (RainDrop rainDrop : this.rainDrops) {
            rainDrop.render(input, graphics, camera, batch);
        }
    }

    @Override
    public void dispose() {
        this.texture.dispose();
    }
}
