package com.mygdx.game.junctions;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.game.descriptors.ConnectionDescriptor;
import com.mygdx.game.descriptors.EndpointDescriptor;

public class Connections {
    private final ObjectMap<String, Array<Junction>> connections = new ObjectMap<>();

    public void addRange(ConnectionDescriptor...connections) {
        for (ConnectionDescriptor connection : connections) {
            this.add(connection);
        }
    }

    private void add(ConnectionDescriptor connection) {
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

    private Array<Junction> get(String scenarioName) {
        if (this.connections.containsKey(scenarioName)) {
            return this.connections.get(scenarioName);
        }

        return new Array<>();
    }

    public EndpointDescriptor checkConnectionLeft(String scenarioName,
                                                  Rectangle rectangle,
                                                  float offsetX,
                                                  float offsetY) {
        for (Junction junction : this.get(scenarioName)) {
            EndpointDescriptor endpoint = junction.intersect(rectangle, offsetX, offsetY);

            if (endpoint != null) {
                return endpoint;
            }
        }

        return null;
    }

    public EndpointDescriptor checkConnectionHit(String scenarioName,
                                                 Rectangle rectangle,
                                                 float offsetX,
                                                 float offsetY) {
        for (Junction junction : this.connections.get(scenarioName)) {
            EndpointDescriptor endpoint = junction.contains(rectangle, offsetX, offsetY);

            if (endpoint != null) {
                return endpoint;
            }
        }

        return null;
    }
}
