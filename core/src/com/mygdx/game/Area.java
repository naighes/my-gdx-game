package com.mygdx.game;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

class Area {
    private final int TILE_SIZE = 32;
    private final MyGdxGame game;
    private final float x;
    private final float y;
    private final Texture texture;
    private final Texture collisionTexture;

    private Pixmap collisionPixmap;
    private Array<Tile> tiles;

    Area(MyGdxGame game,
         float x,
         float y,
         Texture texture,
         Texture collisionTexture) {
        this.game = game;
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.collisionTexture = collisionTexture;
    }

    int getWidth() {
        return this.texture.getWidth();
    }

    int getHeight() {
        return this.texture.getHeight();
    }

    Tile collidesWith(Rectangle r, float offsetX, float offsetY) {
        Tile tile;

        for (int i = 0; i < 4; i++) {
            int m = i % 2;
            int n = i / 2;
            float osx = m == 0 ? offsetX : -1 * offsetX;
            float osy = n == 1 ? (r.height - (2 * offsetY)) / 2 : -1 * offsetY;
            float pixX = r.x + (r.width * m) + osx;
            float pixY = this.collisionTexture.getHeight() - r.y - (r.height * n) + osy;
            int px = (int) Math.ceil(pixX / TILE_SIZE) - 1;
            int py = (int) Math.ceil(pixY / TILE_SIZE) - 1;
            int tileIndex = ((this.collisionTexture.getHeight() / TILE_SIZE) * py) + px;
            tile = this.tiles.get(tileIndex);

            if (tile.isSolid()) {
                // per pixel collision.
                int leftX = (int) (r.x + offsetX);
                int rightX = (int) (r.x + r.width - offsetX);
                int leftY = (int) (this.collisionTexture.getHeight() - r.y - r.height + (r.height - (2 * offsetY)));
                int rightY = (int) (this.collisionTexture.getHeight() - r.y - offsetY);

                for (int y = leftY; y < rightY; y++) {
                    for (int x = leftX; x < rightX; x++) {
                        int pixel = this.collisionPixmap.getPixel(x, y);

                        if (pixel == 255) {
                            return tile;
                        }
                    }
                }
            }
        }

        return null;
    }

    void init() {
        tiles = new Array<>();
        TextureData data = this.collisionTexture.getTextureData();

        if (!data.isPrepared()) {
            data.prepare();
        }

        this.collisionPixmap = data.consumePixmap();

        for (int y = 0; y <= data.getHeight() - TILE_SIZE; y += TILE_SIZE) {
            for (int x = 0; x <= data.getWidth() - TILE_SIZE; x += TILE_SIZE) {
                boolean collides = false;

                for (int sy = y; sy < y + TILE_SIZE; sy++) {
                    for (int sx = x; sx < x + TILE_SIZE; sx++) {
                        int px = collisionPixmap.getPixel(sx, sy);

                        if (px == 255) {
                            collides = true;
                            break;
                        }
                    }
                    if (collides) {
                        break;
                    }
                }

                tiles.add(new Tile(x / TILE_SIZE, y / TILE_SIZE, TILE_SIZE, collides));
            }
        }
    }

    void draw(Batch batch) {
        batch.draw(this.texture,
                this.x,
                this.y,
                this.texture.getWidth(),
                this.texture.getHeight());
    }
}
