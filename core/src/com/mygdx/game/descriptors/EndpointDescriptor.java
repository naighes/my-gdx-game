package com.mygdx.game.descriptors;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class EndpointDescriptor {
    public String scenarioName;
    public Rectangle collisionArea;

    public boolean contains(Rectangle r, float offsetX, float offsetY) {
        float pl = r.x + offsetX;
        float el = this.collisionArea.x;
        float pr = r.x + r.width - offsetX;
        float er = this.collisionArea.x + this.collisionArea.width;

        float pd = r.y + offsetY;
        float ed = this.collisionArea.y;
        float pu = r.y + r.height - offsetY;
        float eu = this.collisionArea.y + this.collisionArea.height;

        return pl >= el && pr <= er && pd >= ed && pu <= eu;
    }

    public boolean intersect(Rectangle r, float offsetX, float offsetY) {
        float pl = r.x + offsetX;
        float el = this.collisionArea.x;
        float pr = r.x + r.width - offsetX;
        float er = this.collisionArea.x + this.collisionArea.width;

        float pd = r.y + offsetY;
        float ed = this.collisionArea.y;
        float pu = r.y + r.height - offsetY;
        float eu = this.collisionArea.y + this.collisionArea.height;

        return pl < er && pr > el && pd < eu && pu > ed;
    }

    public Vector2 getCenterAgainst(Rectangle bounds, float offsetX, float offsetY) {
        return new Vector2((this.collisionArea.x + (this.collisionArea.width / 2f)) - (bounds.width / 2f) + offsetX,
                (this.collisionArea.y + (this.collisionArea.height / 2f)) - (bounds.height / 2f) + offsetY);
    }
}
