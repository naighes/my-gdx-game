package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.descriptors.DialogTextBoxDescriptor;

public class DialogTextBox extends Sprite {
    private final Texture texture;
    private final BitmapFont font;
    private final DialogTextBoxDescriptor descriptor;

    private final Texture fillTexture;
    private final TextureRegion regionUp;
    private final TextureRegion regionDown;
    private final TextureRegion regionSide;

    private boolean boxFullyVisible = false;
    private boolean done = false;

    DialogTextBox(Texture texture,
                  BitmapFont font,
                  DialogTextBoxDescriptor descriptor) {
        super();

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
        this.setBounds(0f, 0f, this.texture.getWidth(), this.texture.getHeight());
    }

    private float boxAccumulator = 0f;
    private float textAccumulator = 0f;
    private String text = "";

    public void setText(String text) {
        this.text = text;
        this.resetText();
    }

    @Override
    public void draw(Batch batch) {
        this.drawBox(batch);
        this.drawText(batch);
    }

    private void drawBox(Batch batch) {
        float delta = Gdx.graphics.getDeltaTime();
        float variation = (this.boxAccumulator * this.descriptor.boxAnimationSpeed);
        float x = this.getX();
        float y = this.getY();

        float upY = y + (this.texture.getHeight() / 2f) + variation;
        final float upLimitY = y + this.texture.getHeight() - this.descriptor.upOffsetY;
        batch.draw(
                this.regionUp,
                x,
                Math.min(upLimitY, upY)
        );

        final float downLimitY = y;
        float downY = y + (this.texture.getHeight() / 2f) - this.descriptor.upOffsetY - variation;
        batch.draw(
                this.regionDown,
                x,
                Math.max(downLimitY, downY)
        );

        final float rightLimitY = y + this.descriptor.upOffsetY;
        float rightY = y + (this.texture.getHeight() / 2f) - variation;
        batch.draw(
                this.regionSide,
                x,
                Math.max(rightLimitY, rightY),
                this.regionSide.getRegionWidth(),
                Math.min(variation * 2, this.regionSide.getRegionHeight())
        );
        batch.draw(
                this.regionSide,
                x + this.texture.getWidth() - this.descriptor.sideOffsetX,
                Math.max(rightLimitY, rightY),
                this.regionSide.getRegionWidth(),
                Math.min(variation * 2, this.regionSide.getRegionHeight())
        );

        float centerLimitY = y + this.descriptor.upOffsetY;
        float centerY = y + (this.texture.getHeight() / 2f) - variation;

        batch.draw(
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
    }

    private void drawText(Batch batch) {
        float delta = Gdx.graphics.getDeltaTime();
        float x = this.getX();
        float y = this.getY();

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
                batch,
                drawnText,
                x + this.descriptor.sideOffsetX + this.descriptor.textPadding + this.descriptor.dropShadowSize,
                y + this.texture.getHeight() - this.descriptor.upOffsetY - this.descriptor.textPadding - this.descriptor.dropShadowSize,
                this.fillTexture.getWidth() - this.descriptor.textRightMargin,
                Align.left,
                true
        );
        font.setColor(this.descriptor.textColor);
        this.font.draw(
                batch,
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

    public boolean isConsumed() {
        return this.done;
    }

    public void resetAll() {
        this.resetBox();
        this.resetText();
    }

    private void resetBox() {
        this.boxFullyVisible = false;
        this.boxAccumulator = 0f;
    }

    private void resetText() {
        this.textAccumulator = 0f;
        this.done = false;
    }
}
