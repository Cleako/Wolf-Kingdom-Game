package com.legacy.server.model.entity;

import com.legacy.server.Constants;
import com.legacy.server.Server;
import com.legacy.server.event.rsc.GameTickEvent;
import com.legacy.server.external.EntityHandler;
import com.legacy.server.external.ItemDefinition;
import com.legacy.server.external.ItemLoc;
import com.legacy.server.model.Point;
import com.legacy.server.model.entity.player.Player;

public class GroundItem extends Entity {
	/**
	 * Amount (for stackables)
	 */
	private int amount;

	/**
	 * Location definition of the item
	 */
	private ItemLoc loc = null;

	/**
	 * Contains the player that the item belongs to, if any
	 */
	private long ownerUsernameHash;
	/**
	 * The time that the item was spawned
	 */
	private long spawnedTime = 0L;
	
	public GroundItem(int id, Point location) { // used for ::masks
		super.id = id;
		super.location.set(location);
		amount = 1;
	}

	public GroundItem(int id, int x, int y, int amount, Player owner) {
		setID(id);
		setAmount(amount);
		this.ownerUsernameHash = owner == null ? 0 : owner.getUsernameHash();
		spawnedTime = System.currentTimeMillis();
		setLocation(Point.location(x, y));
	}

	public GroundItem(int id, int x, int y, int amount, Player owner, long spawntime) {
		setID(id);
		setAmount(amount);
		this.ownerUsernameHash = owner == null ? 0 : owner.getUsernameHash();
		spawnedTime = spawntime;
		setLocation(Point.location(x, y));
	}

	public GroundItem(ItemLoc loc) {
		this.loc = loc;
		setID(loc.id);
		setAmount(loc.amount);
		spawnedTime = System.currentTimeMillis();
		setLocation(Point.location(loc.x, loc.y));
	}

	public boolean belongsTo(Player p) {
		return p.getUsernameHash() == ownerUsernameHash || ownerUsernameHash == 0;
	}

	public long getOwnerUsernameHash() {
		return ownerUsernameHash;
	}

	public boolean is(Object o) {
		if (o instanceof GroundItem) {
			GroundItem item = (GroundItem) o;
			return item.getID() == getID() && item.getAmount() == getAmount()
					&& item.getSpawnedTime() == getSpawnedTime()
					&& (item.getOwnerUsernameHash() == getOwnerUsernameHash())
					&& item.getLocation().equals(getLocation());
		}
		return false;
	}

	public int getAmount() {
		return amount;
	}

	public ItemDefinition getDef() {
		return EntityHandler.getItemDef(id);
	}

	public ItemLoc getLoc() {
		return loc;
	}

	public long getSpawnedTime() {
		return spawnedTime;
	}

	public boolean isOn(int x, int y) {
		return x == getX() && y == getY();
	}

	public void remove() {
		if (!removed && loc != null && loc.getRespawnTime() > 0) {
			Server.getServer().getGameEventHandler().add(new GameTickEvent(null, loc.getRespawnTime()) {
				public void run() {
					world.registerItem(new GroundItem(loc));
					stop();
				}
			});
		}
		super.remove();
	}

	public void setAmount(int amount) {
		if (getDef() != null) {
			if (getDef().isStackable()) {
				this.amount = amount;
			} else {
				this.amount = 1;
			}
		}
	}

	public boolean visibleTo(Player p) {
		if (belongsTo(p)) {
			return true;
		}
		if (getDef().isMembersOnly() && !Constants.GameServer.MEMBER_WORLD) {
			return false;
		}
		if (getDef().isUntradable())
			return false;
		if(!belongsTo(p) && p.getIronMan() >= 1 && p.getIronMan() <= 3)
			return false;
		return System.currentTimeMillis() - spawnedTime > 60000;
	}

	@Override
	public String toString() {
		return "Item(" + this.id + ", " + this.amount + ") location = " + location.toString();
	}
}
