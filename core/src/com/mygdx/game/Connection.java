package com.mygdx.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

class Endpoint {
    public final String scenarioName;
    public final Rectangle collisionArea;
    public final Vector2 playerInitialPosition;

    public Endpoint(String scenarioName,
                    Rectangle collisionArea,
                    Vector2 playerInitialPosition) {
        this.scenarioName = scenarioName;
        this.collisionArea = collisionArea;
        this.playerInitialPosition = playerInitialPosition;
    }

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

    public Vector2 getCenterAgainst(Player player) {
        Rectangle bounds = player.getBoundingRectangle();
        return new Vector2((this.collisionArea.x + (this.collisionArea.width / 2f)) - (bounds.width / 2f) + player.getOffsetX(),
                (this.collisionArea.y + (this.collisionArea.height / 2f)) - (bounds.height / 2f) + player.getOffsetY());
    }
}

class Junction {
    public final Endpoint source;
    public final Endpoint target;

    public Junction(Endpoint source, Endpoint target) {
        this.source = source;
        this.target = target;
    }

    public Endpoint contains(Rectangle r, float offsetX, float offsetY) {
        return this.source.contains(r, offsetX, offsetY) ? this.target : null;
    }

    public Endpoint intersect(Rectangle r, float offsetX, float offsetY) {
        return this.source.intersect(r, offsetX, offsetY) ? this.target : null;
    }
}

class Connections {
    private final ObjectMap<String, Array<Junction>> connections = new ObjectMap<>();

    public void add(Connection connection) {
        if (!this.connections.containsKey(connection.endpoint1.scenarioName)) {
            this.connections.put(connection.endpoint1.scenarioName, new Array<Junction>());
        }

        Array<Junction> array1 = this.connections.get(connection.endpoint1.scenarioName);
        array1.add(new Junction(connection.endpoint1, connection.endpoint2));

        if (!this.connections.containsKey(connection.endpoint2.scenarioName)) {
            this.connections.put(connection.endpoint2.scenarioName, new Array<Junction>());
        }

        Array<Junction> array2 = this.connections.get(connection.endpoint2.scenarioName);
        array2.add(new Junction(connection.endpoint2, connection.endpoint1));
    }

    public Array<Junction> get(String scenarioName) {
        if (this.connections.containsKey(scenarioName)) {
            return this.connections.get(scenarioName);
        }

        return new Array<>();
    }

    Endpoint checkConnectionLeft(String scenarioName,
                                 Rectangle rectangle,
                                 float offsetX,
                                 float offsetY) {
        for (Junction junction : this.get(scenarioName)) {
            Endpoint endpoint = junction.intersect(rectangle, offsetX, offsetY);
            if (endpoint != null) {
                return endpoint;
            }
        }
        return null;
    }

    Endpoint checkConnectionHit(String scenarioName,
                                Rectangle rectangle,
                                float offsetX,
                                float offsetY) {
        for (Junction junction : this.connections.get(scenarioName)) {
            Endpoint endpoint = junction.contains(rectangle, offsetX, offsetY);
            if (endpoint != null) {
                return endpoint;
            }
        }
        return null;
    }
}

class Connection {
    public final Endpoint endpoint1;
    public final Endpoint endpoint2;

    public Connection(Endpoint endpoint1,
                      Endpoint endpoint2) {
        this.endpoint1 = endpoint1;
        this.endpoint2 = endpoint2;
    }
}
