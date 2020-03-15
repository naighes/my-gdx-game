package com.mygdx.game.descriptors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.utils.Graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnimationsDescriptor {
    public float frameDuration;
    public HashMap<String, Rectangle[]> animations;
    public String assetPath;

    public Map<String, Animation<TextureRegion>> getAnimations(AssetManager assetManager) {
        Texture texture = assetManager.get(this.assetPath);
        Map<String, Animation<TextureRegion>> animations = new HashMap<>();

        for (Map.Entry<String, Rectangle[]> entry : this.animations.entrySet()) {
            ArrayList<TextureRegion> regions = new ArrayList<>();

            for (Rectangle rectangle : entry.getValue()) {
                regions.add(Graphics.getTextureRegion(texture, rectangle));
            }
            Animation<TextureRegion> animation = new Animation<>(this.frameDuration,
                    regions.toArray(new TextureRegion[0]));
            animations.put(entry.getKey(), animation);
        }

        return animations;
    }
}

