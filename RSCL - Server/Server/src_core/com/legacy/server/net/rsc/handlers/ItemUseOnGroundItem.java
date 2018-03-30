package com.legacy.server.net.rsc.handlers;

import com.legacy.server.Constants;
import com.legacy.server.model.Point;
import com.legacy.server.model.action.WalkToPointAction;
import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.GroundItem;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.entity.update.Bubble;
import com.legacy.server.model.states.Action;
import com.legacy.server.model.world.World;
import com.legacy.server.net.Packet;
import com.legacy.server.net.rsc.PacketHandler;
import com.legacy.server.plugins.PluginHandler;

public class ItemUseOnGroundItem implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	private GroundItem getItem(int id, Point location, Player player) {
		int x = location.getX();
		int y = location.getY();
		for(GroundItem i : player.getViewArea().getItemsInView()) {
			if (i.getID() == id && i.visibleTo(player) && i.getX() == x && i.getY() == y) {
				return i;
			}
		} 
		return null;
	}

	public void handlePacket(Packet p, final Player player) throws Exception {
		if (player.isBusy()) {
			player.resetPath();
			return;
		}

		player.resetAll();
		Point location = Point.location(p.readShort(), p.readShort());
		final int id = p.readShort();
		final Item myItem = player.getInventory().get(p.readShort());
		if (myItem == null)
			return;

		final GroundItem item = getItem(id, location, player);

		if (item == null || myItem == null) {
			player.setSuspiciousPlayer(true);
			player.resetPath();
			return;
		}
		player.setStatus(Action.USING_Item_ON_GITEM);
		player.setWalkToAction(new WalkToPointAction(player,
				item.getLocation(), 1) {
			public void execute() {
				if (player.isBusy()
						|| player.isRanging()
						|| getItem(id, location, player) == null
						|| !player.canReach(item)
						|| player.getStatus() != Action.USING_Item_ON_GITEM) {
					return;
				}
				if (myItem == null || item == null)
					return;

				if ((myItem.getDef().isMembersOnly() || item.getDef()
						.isMembersOnly())
						&& !Constants.GameServer.MEMBER_WORLD) {
					player.message(player.MEMBER_MESSAGE);
					return;
				}

				if (PluginHandler.getPluginHandler()
						.blockDefaultAction("InvUseOnGroundItem",
								new Object[] { myItem, item, player })) {
					return;
				}

				switch (item.getID()) {
				case 23:
					if (myItem.getID() == 135) {
						if (player.getInventory().remove(myItem) < 0)
							return;
						player.message("You put the flour in the pot");
						Bubble bubble = new Bubble(player, 135);
						player.getUpdateFlags().setActionBubble(bubble);
						world.unregisterItem(item);
						player.getInventory().add(new Item(136));
						return;
					}
					break;
				default:
					player.message("Nothing interesting happens");
					return;
				}
			}
		});

	}

}
