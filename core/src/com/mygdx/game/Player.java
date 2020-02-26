package com.mygdx.game;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

class Player {
    private final Rectangle rectangle;
    private final String assetPath;

    private Animation animation;

    private Player(Rectangle rectangle, String assetPath) {
        this.rectangle = rectangle;
        this.assetPath = assetPath;
    }

    static Player New(Graphics graphics) {
        final int width = 64;
        final int height = 64;
        final String assetPath = "player_1.png";
        Rectangle r = new Rectangle(MathUtils.random(0, graphics.getWidth() - width),
                MathUtils.random(0, graphics.getHeight() - height),
                width,
                height);
        return new Player(r, assetPath);
    }

    public void create(Files files, Graphics graphics) {
        Texture texture = new Texture(files.internal(this.assetPath));
        this.animation = Animation.New(texture,
                200,
                new Rectangle(0f, 0f, 0.25f, 0.25f),
                new Rectangle(0.25f, 0f, 0.5f, 0.25f),
                new Rectangle(0.5f, 0f, 0.75f, 0.25f),
                new Rectangle(0.75f, 0f, 1f, 0.25f));
    }

    public void render(Input input, Graphics graphics, Camera camera, Batch batch) {
        animation.render(input, graphics, camera, batch);
        batch.draw(this.animation.getCurrentFrame(), this.rectangle.x, this.rectangle.y);
    }
}
