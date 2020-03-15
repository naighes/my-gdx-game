package com.mygdx.game.junctions;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.descriptors.EndpointDescriptor;

class Junction {
    private final EndpointDescriptor source;
    private final EndpointDescriptor target;

    Junction(EndpointDescriptor source, EndpointDescriptor target) {
        this.source = source;
        this.target = target;
    }

    EndpointDescriptor contains(Rectangle r, float offsetX, float offsetY) {
        return this.source.contains(r, offsetX, offsetY) ? this.target : null;
    }

    EndpointDescriptor intersect(Rectangle r, float offsetX, float offsetY) {
        return this.source.intersect(r, offsetX, offsetY) ? this.target : null;
    }
}
