package com.mygdx.game;

import java.util.Locale;

public class Tile {
    private final int x;
    private final int y;
    private final int size;
    private final boolean solid;

    Tile(int x,
         int y,
         int size,
         boolean solid) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.solid = solid;
    }

    boolean isSolid() {
        return this.solid;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "x:%d; y:%d; size:%d; solid: %b",
                this.x,
                this.y,
                this.size,
                this.solid);
    }
}
