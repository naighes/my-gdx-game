package com.mygdx.game.descriptors;

import com.badlogic.gdx.math.Vector2;

public class ScenarioDescriptor {
    public String name;
    public Vector2 playerInitialPosition;
    public String assetPath;
    public String collisionAssetPath;
    public String overlayAssetPath;
    public GuestDescriptor[] guests;
    public PlayerDescriptor player;
}
