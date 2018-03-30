package com.legacy.server.model.entity;

import com.legacy.server.external.DoorDef;
import com.legacy.server.external.EntityHandler;
import com.legacy.server.external.GameObjectDef;
import com.legacy.server.external.GameObjectLoc;
import com.legacy.server.model.Point;
import com.legacy.server.model.entity.player.Player;

public class GameObject extends Entity {
	/**
	 * Returns the ID of an item contained in the object.
	 *
	 * @author Konijn
	 */
	private int containsItem = -1;
	/**
	 * The direction the object points in
	 */
	private int direction;
	/**
	 * Location definition of the object
	 */
	private GameObjectLoc loc = null;
	/**
	 * The type of object
	 */
	private int type;

	private VisibleCondition statement;

	public GameObject(GameObjectLoc loc, VisibleCondition statement) {
		direction = loc.direction;
		type = loc.type;
		this.loc = loc;
		super.setID(loc.id);
		this.statement = statement;
	}

	public GameObject(GameObjectLoc loc) {
		direction = loc.direction;
		type = loc.type;
		this.loc = loc;
		super.setID(loc.id);
	}

	public GameObject(Point location, int id, int direction, int type) {
		this(new GameObjectLoc(id, location.getX(), location.getY(), direction,
				type));
	}

	public GameObject(Point location, int id, int direction, int type,
			String owner) {
		this(new GameObjectLoc(id, location.getX(), location.getY(), direction,
				type, owner));
	}

	public String getOwner() {
		return loc.owner;
	}

	public int containsItem() {
		return containsItem;
	}

	public void containsItem(int item) {
		containsItem = item;
	}

	public boolean equals(Object o) {
		if (o instanceof GameObject) {
			GameObject go = (GameObject) o;
			return go.getLocation().equals(getLocation())
					&& go.getID() == getID()
					&& go.getDirection() == getDirection()
					&& go.getType() == getType();
		}
		return false;
	}

	public int getDirection() {
		return direction;
	}

	public DoorDef getDoorDef() {
		return EntityHandler.getDoorDef(super.getID());
	}

	public GameObjectDef getGameObjectDef() {
		return EntityHandler.getGameObjectDef(super.getID());
	}

	public GameObjectLoc getLoc() {
		return loc;
	}

	public int getType() {
		return type;
	}

	public boolean isOn(int x, int y) {
		int width, height;
		if (type == 1) {
			width = height = 1;
		} else if (direction == 0 || direction == 4) {
			width = getGameObjectDef().getWidth();
			height = getGameObjectDef().getHeight();
		} else {
			height = getGameObjectDef().getWidth();
			width = getGameObjectDef().getHeight();
		}
		if (type == 0) { // Object
			return x >= getX() && x <= (getX() + width) && y >= getY()
					&& y <= (getY() + height);
		} else { // Door
			return x == getX() && y == getY();
		}
	}

	public boolean isRemoved() {
		return removed;
	}

	public boolean isTelePoint() {
		return EntityHandler.getObjectTelePoint(getLocation(), null) != null;
	}

	public void remove() {
		removed = true;
		super.remove();
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String toString() {
		return (type == 0 ? "GameObject" : "WallObject") + ":id = " + id
				+ "; dir = " + direction + "; location = "
				+ location.toString() + ";";
	}

	public boolean isVisibleTo(Player p) {
		if(statement == null)
			return true;
		
		return statement != null && statement.isVisibleTo(this, p);
	}
	
	public final Point[] getObjectBoundary() {
		int dir = getDirection();
		int minX = getX();
		int minY = getY();
		int maxX = minX;
		int maxY = minY;
		if (getType() == 0) {
			int worldWidth;
			int worldHeight;
			if (dir != 0 && dir != 4) {
				worldWidth = getGameObjectDef().getHeight();
				worldHeight = getGameObjectDef().getWidth();
			} else {
				worldHeight = getGameObjectDef().getHeight();
				worldWidth = getGameObjectDef().getWidth();
			}
			maxX = worldWidth + getX() - 1;
			maxY = worldHeight + getY() - 1;

			if (getGameObjectDef().getType() == 2 || getGameObjectDef().getType() == 3) {
				if (dir == 0) {
					++worldWidth;
					--minX;
				}
				if (dir == 2) {
					++worldHeight;
				}
				if (dir == 6) {
					--minY;
					++worldHeight;
				}
				if (dir == 4) {// here.
					++worldWidth;
				}
				maxX = worldWidth + getX() - 1;
				maxY = worldHeight + getY() - 1;
			}
		} else if (getType() == 1) {
			
			if (dir == 0) {
				minX = getX();
				minY = getY() - 1;
				maxX = getX();
				maxY = getY();
			} else if (dir != 1) {
				
				minX = getX();
				minY = getY();
				maxX = getX();
				maxY = getY();
				if(dir == 3 || dir == 2) {
					minX = getX() - 1;
					minY = getY() - 1;
					maxX = getX() + 1;
					maxY = getY() + 1;
				}
			} else {
				minX = getX() - 1;
				minY = getY();
				maxX = getX();
				maxY = getY();
			}
		}
		return new Point[] { Point.location(minX, minY), Point.location(maxX, maxY)};
	}
}
