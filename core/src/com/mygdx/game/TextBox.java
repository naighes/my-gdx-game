package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;

class TextBox extends Sprite {
    private final MyGdxGame game;
    private final Texture texture;
    private final BitmapFont font;
    private final Color backgroundColor;
    private final int upOffsetY;
    private final int sideOffsetX;
    private final float textPadding;
    private final int textSpeed;
    private final float boxAnimationSpeed;
    private final float dropShadowSize;
    private final Color textColor;
    private final Color dropShadowColor;

    private final Texture fillTexture;
    private final TextureRegion regionUp;
    private final TextureRegion regionDown;
    private final TextureRegion regionSide;

    private boolean boxFullyVisible = false;
    private int textRightMargin;
    private boolean done = false;

    TextBox(MyGdxGame game,
            Texture texture,
            BitmapFont font,
            Color backgroundColor,
            int upOffsetY,
            int sideOffsetX,
            float textPadding,
            int textSpeed,
            float boxAnimationSpeed,
            float dropShadowSize,
            int textRightMargin,
            Color textColor,
            Color dropShadowColor) {
        super();

        this.game = game;
        this.texture = texture;
        this.font = font;
        this.backgroundColor = backgroundColor;
        this.upOffsetY = upOffsetY;
        this.sideOffsetX = sideOffsetX;
        this.textPadding = textPadding;
        this.textSpeed = textSpeed;
        this.boxAnimationSpeed = boxAnimationSpeed;
        this.dropShadowSize = dropShadowSize;
        this.textRightMargin = textRightMargin;
        this.textColor = textColor;
        this.dropShadowColor = dropShadowColor;

        this.regionUp = new TextureRegion(
                texture,
                0,
                0,
                texture.getWidth(),
                this.upOffsetY
        );
        this.regionDown = new TextureRegion(
                texture,
                0,
                texture.getHeight() - this.upOffsetY,
                texture.getWidth(),
                this.upOffsetY
        );
        this.regionSide = new TextureRegion(
                texture,
                0,
                this.upOffsetY,
                this.sideOffsetX,
                texture.getHeight() - (upOffsetY * 2)
        );
        Pixmap pixmap = new Pixmap(
                this.texture.getWidth() - (this.sideOffsetX * 2),
                this.texture.getHeight() - (this.upOffsetY * 2),
                Pixmap.Format.RGBA8888
        );
        pixmap.setColor(this.backgroundColor);
        pixmap.fill();
        this.fillTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    private float boxAccumulator = 0f;
    private float textAccumulator = 0f;

    void render(String text, float x, float y) {
        float delta = Gdx.graphics.getDeltaTime();
        float variation = (this.boxAccumulator * this.boxAnimationSpeed);

        float upY = y + (this.texture.getHeight() / 2) + variation;
        final float upLimitY = y + this.texture.getHeight() - this.upOffsetY;
        this.game.getBatch().draw(
                this.regionUp,
                x,
                Math.min(upLimitY, upY)
        );

        final float downLimitY = y;
        float downY = y + (this.texture.getHeight() / 2) - this.upOffsetY - variation;
        this.game.getBatch().draw(
                this.regionDown,
                x,
                Math.max(downLimitY, downY)
        );

        final float rightLimitY = y + this.upOffsetY;
        float rightY = y + (this.texture.getHeight() / 2) - variation;
        this.game.getBatch().draw(
                this.regionSide,
                x,
                Math.max(rightLimitY, rightY),
                this.regionSide.getRegionWidth(),
                Math.min(variation * 2, this.regionSide.getRegionHeight())
        );
        this.game.getBatch().draw(
                this.regionSide,
                x + this.texture.getWidth() - this.sideOffsetX,
                Math.max(rightLimitY, rightY),
                this.regionSide.getRegionWidth(),
                Math.min(variation * 2, this.regionSide.getRegionHeight())
        );

        float centerLimitY = y + this.upOffsetY;
        float centerY = y + (this.texture.getHeight() / 2) - variation;

        this.game.getBatch().draw(
                this.fillTexture,
                x + this.sideOffsetX,
                Math.max(centerLimitY, centerY),
                this.fillTexture.getWidth(),
                Math.min(variation * 2, this.fillTexture.getHeight())
        );

        if (centerY <= centerLimitY) {
            this.boxFullyVisible = true;
        }

        this.boxAccumulator += delta;
        this.drawText(text, x, y, delta);
    }

    private void drawText(String text, float x, float y, float delta) {
        if (!this.boxFullyVisible) {
            return;
        }

        String drawnText = text.substring(
                0,
                Math.min(
                        (int)(this.textAccumulator * this.textSpeed),
                        text.length()
                )
        );

        font.setColor(this.dropShadowColor);
        this.font.draw(
                this.game.getBatch(),
                drawnText,
                x + this.sideOffsetX + this.textPadding + this.dropShadowSize,
                y + this.texture.getHeight() - this.upOffsetY - this.textPadding - this.dropShadowSize,
                this.fillTexture.getWidth() - this.textRightMargin,
                Align.left,
                true
        );
        font.setColor(this.textColor);
        this.font.draw(
                this.game.getBatch(),
                drawnText,
                x + this.sideOffsetX + this.textPadding,
                y + this.texture.getHeight() - this.upOffsetY - this.textPadding,
                this.fillTexture.getWidth() - this.textRightMargin,
                Align.left,
                true
        );
        this.textAccumulator += delta;

        if (drawnText == text) {
            this.done = true;
        }
    }

    public boolean isConsumed() {
        return done;
    }

    public void reset() {
        this.boxFullyVisible = false;
        this.done = false;
        this.boxAccumulator = 0f;
        this.textAccumulator = 0f;
    }
}
