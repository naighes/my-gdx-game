package com.mygdx.game.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Graphics {
    public static TextureRegion getTextureRegion(Texture texture, Rectangle r) {
        return new TextureRegion(texture, r.x, r.y, r.width, r.height);
    }
}
