package com.legacy.server.event.rsc.impl;

import com.legacy.server.event.rsc.GameTickEvent;
import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.world.World;


public class ObjectRemover extends GameTickEvent {
	
    public static final World world = World.getWorld();
    private GameObject object;

    public ObjectRemover(GameObject object, int ticks) {
        super(null, ticks);
        this.object = object;
    }

    public boolean equals(Object o) {
        if (o instanceof ObjectRemover) {
            return ((ObjectRemover) o).getObject().equals(getObject());
        }
        return false;
    }

    public GameObject getObject() {
        return object;
    }

    public void run() {
        world.unregisterGameObject(object);
      	stop();
    }

}