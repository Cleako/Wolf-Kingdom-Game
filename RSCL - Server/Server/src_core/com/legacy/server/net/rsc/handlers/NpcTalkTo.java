package com.legacy.server.net.rsc.handlers;

import com.legacy.server.model.Point;
import com.legacy.server.model.action.WalkToMobAction;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.states.Action;
import com.legacy.server.model.world.World;
import com.legacy.server.model.world.region.TileValue;
import com.legacy.server.net.Packet;
import com.legacy.server.net.rsc.PacketHandler;
import com.legacy.server.plugins.PluginHandler;

public final class NpcTalkTo implements PacketHandler {

	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Player player) throws Exception {

		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		player.resetAll();
		final Npc n = world.getNpc(p.readShort());
		
		if (n == null) {
			return;
		}
		player.setFollowing(n);
		player.setStatus(Action.TALKING_MOB);
		player.setWalkToAction(new WalkToMobAction(player, n, 1) {
			public void execute() {
				player.resetFollowing();
				player.resetPath();
				if (player.isBusy() || player.isRanging() || !player.canReach(n)
						|| player.getStatus() != Action.TALKING_MOB) {
					return;
				}
				player.resetAll();

				if (n.isBusy()) {
					player.message(n.getDef().getName() + " is busy at the moment");
					return;
				}

				n.resetPath();

				if (player.getLocation().equals(n.getLocation())) {
					for (int x = -1; x <= 1; ++x) {
						for (int y = -1; y <= 1; ++y) {
							if(x == 0 || y == 0) 
								continue;
							Point destination = canWalk(player.getX() - x, player.getY() - y);
							if (destination != null && destination.inBounds(n.getLoc().minX, n.getLoc().minY, n.getLoc().maxY, n.getLoc().maxY)) {
								n.teleport(destination.getX(), destination.getY());
								break;
							}
						}
					}
				}
				player.face(n);
				n.face(player);
				player.setInteractingNpc(n);
				PluginHandler.getPluginHandler().blockDefaultAction("TalkToNpc", new Object[] { player, n });
			}

			private Point canWalk(int x, int y) {
				int myX = n.getX();
				int myY = n.getY();
				int newX = x;
				int newY = y;
				boolean myXBlocked = false, myYBlocked = false, newXBlocked = false, newYBlocked = false;
				if (myX > x) {
					myXBlocked = isBlocking(myX - 1, myY, 8); // Check right
																// tiles
					newX = myX - 1;
				} else if (myX < x) {
					myXBlocked = isBlocking(myX + 1, myY, 2); // Check left
																// tiles
					newX = myX + 1;
				}
				if (myY > y) {
					myYBlocked = isBlocking(myX, myY - 1, 4); // Check top tiles
					newY = myY - 1;
				} else if (myY < y) {
					myYBlocked = isBlocking(myX, myY + 1, 1); // Check bottom
																// tiles
					newY = myY + 1;
				}

				if ((myXBlocked && myYBlocked) || (myXBlocked && myY == newY) || (myYBlocked && myX == newX)) {
					return null;
				}

				if (newX > myX) {
					newXBlocked = isBlocking(newX, newY, 2);
				} else if (newX < myX) {
					newXBlocked = isBlocking(newX, newY, 8);
				}

				if (newY > myY) {
					newYBlocked = isBlocking(newX, newY, 1);
				} else if (newY < myY) {
					newYBlocked = isBlocking(newX, newY, 4);
				}
				if ((newXBlocked && newYBlocked) || (newXBlocked && myY == newY) || (myYBlocked && myX == newX)) {
					return null;
				}
				if ((myXBlocked && newXBlocked) || (myYBlocked && newYBlocked)) {
					return null;
				}
				return new Point(newX, newY);
			}

			private boolean isBlocking(int x, int y, int bit) {
				TileValue t = World.getWorld().getTile(x, y);
				Point p = new Point(x, y);
				for(Npc n : n.getViewArea().getNpcsInView()) {
					if(n.getLocation().equals(p)) {
						return true;
					}
				}
				for(Player areaPlayer : n.getViewArea().getPlayersInView()) {
					if(areaPlayer.getLocation().equals(p)) {
						return true;
					}
				}
				return isBlocking(t.traversalMask, (byte) bit);
			}

			private boolean isBlocking(int objectValue, byte bit) {
				if ((objectValue & bit) != 0) { // There is a wall in the way
					return true;
				}
				if ((objectValue & 16) != 0) { // There is a diagonal wall here:
												// \
					return true;
				}
				if ((objectValue & 32) != 0) { // There is a diagonal wall here:
												// /
					return true;
				}
				if ((objectValue & 64) != 0) { // This tile is unwalkable
					return true;
				}
				return false;
			}
		});
	}
}
