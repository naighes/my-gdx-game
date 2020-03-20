package com.mygdx.game.utils;

import com.badlogic.gdx.math.Vector2;

public class Geometry {
    private Geometry() {
    }

    public static Vector2 LEFT = new Vector2(-1f, 0f);
    public static Vector2 RIGHT = Vector2.X;
    public static Vector2 UP = Vector2.Y;
    public static Vector2 DOWN = new Vector2(0f, -1f);
}
