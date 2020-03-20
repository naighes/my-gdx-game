package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.descriptors.DialogTextBoxDescriptor;

class DialogTextBox extends Sprite {
    private final MyGdxGame game;
    private final Texture texture;
    private final BitmapFont font;
    private final DialogTextBoxDescriptor descriptor;

    private final Texture fillTexture;
    private final TextureRegion regionUp;
    private final TextureRegion regionDown;
    private final TextureRegion regionSide;

    private boolean boxFullyVisible = false;
    private boolean done = false;

    DialogTextBox(MyGdxGame game,
                  Texture texture,
                  BitmapFont font,
                  DialogTextBoxDescriptor descriptor) {
        super();

        this.game = game;
        this.texture = texture;
        this.font = font;
        this.descriptor = descriptor;

        this.regionUp = new TextureRegion(
                texture,
                0,
                0,
                texture.getWidth(),
                this.descriptor.upOffsetY
        );
        this.regionDown = new TextureRegion(
                texture,
                0,
                texture.getHeight() - this.descriptor.upOffsetY,
                texture.getWidth(),
                this.descriptor.upOffsetY
        );
        this.regionSide = new TextureRegion(
                texture,
                0,
                this.descriptor.upOffsetY,
                this.descriptor.sideOffsetX,
                texture.getHeight() - (this.descriptor.upOffsetY * 2)
        );
        Pixmap pixmap = new Pixmap(
                this.texture.getWidth() - (this.descriptor.sideOffsetX * 2),
                this.texture.getHeight() - (this.descriptor.upOffsetY * 2),
                Pixmap.Format.RGBA8888
        );
        pixmap.setColor(this.descriptor.backgroundColor);
        pixmap.fill();
        this.fillTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    private float boxAccumulator = 0f;
    private float textAccumulator = 0f;

    void render(String text, float x, float y) {
        float delta = Gdx.graphics.getDeltaTime();
        float variation = (this.boxAccumulator * this.descriptor.boxAnimationSpeed);

        float upY = y + (this.texture.getHeight() / 2) + variation;
        final float upLimitY = y + this.texture.getHeight() - this.descriptor.upOffsetY;
        this.game.getBatch().draw(
                this.regionUp,
                x,
                Math.min(upLimitY, upY)
        );

        final float downLimitY = y;
        float downY = y + (this.texture.getHeight() / 2) - this.descriptor.upOffsetY - variation;
        this.game.getBatch().draw(
                this.regionDown,
                x,
                Math.max(downLimitY, downY)
        );

        final float rightLimitY = y + this.descriptor.upOffsetY;
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
                x + this.texture.getWidth() - this.descriptor.sideOffsetX,
                Math.max(rightLimitY, rightY),
                this.regionSide.getRegionWidth(),
                Math.min(variation * 2, this.regionSide.getRegionHeight())
        );

        float centerLimitY = y + this.descriptor.upOffsetY;
        float centerY = y + (this.texture.getHeight() / 2) - variation;

        this.game.getBatch().draw(
                this.fillTexture,
                x + this.descriptor.sideOffsetX,
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
                        (int) (this.textAccumulator * this.descriptor.textSpeed),
                        text.length()
                )
        );

        font.setColor(this.descriptor.dropShadowColor);
        this.font.draw(
                this.game.getBatch(),
                drawnText,
                x + this.descriptor.sideOffsetX + this.descriptor.textPadding + this.descriptor.dropShadowSize,
                y + this.texture.getHeight() - this.descriptor.upOffsetY - this.descriptor.textPadding - this.descriptor.dropShadowSize,
                this.fillTexture.getWidth() - this.descriptor.textRightMargin,
                Align.left,
                true
        );
        font.setColor(this.descriptor.textColor);
        this.font.draw(
                this.game.getBatch(),
                drawnText,
                x + this.descriptor.sideOffsetX + this.descriptor.textPadding,
                y + this.texture.getHeight() - this.descriptor.upOffsetY - this.descriptor.textPadding,
                this.fillTexture.getWidth() - this.descriptor.textRightMargin,
                Align.left,
                true
        );
        this.textAccumulator += delta;

        if (drawnText == text) {
            this.done = true;
        }
    }

    boolean isConsumed() {
        return done;
    }

    void reset() {
        this.boxFullyVisible = false;
        this.done = false;
        this.boxAccumulator = 0f;
        this.textAccumulator = 0f;
    }
}
