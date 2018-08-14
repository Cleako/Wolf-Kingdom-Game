package com.wk.server.net.rsc.handlers;

import com.wk.server.Constants;
import com.wk.server.model.action.WalkToMobAction;
import com.wk.server.model.container.Item;
import com.wk.server.model.entity.player.Player;
import com.wk.server.model.states.Action;
import com.wk.server.model.world.World;
import com.wk.server.net.Packet;
import com.wk.server.net.rsc.PacketHandler;
import com.wk.server.plugins.PluginHandler;

public class ItemUseOnPlayer implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Player player) throws Exception {

		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		player.resetAll();
		final Player affectedPlayer = world.getPlayer(p.readShort());
		final Item item = player.getInventory().get(p.readShort());
		if (affectedPlayer == null || item == null) {
			return;
		}
		if (System.currentTimeMillis() - affectedPlayer.getLastRun() < 2000) {
			player.resetPath();
			return;
		}
		player.setFollowing(affectedPlayer);
		player.setStatus(Action.USING_Item_ON_PLAYER);
		player.setWalkToAction(new WalkToMobAction(player, affectedPlayer, 1) {
			public void execute() {
				player.resetPath();
				player.resetFollowing();
				if (!player.getInventory().contains(item)
						|| !player.canReach(affectedPlayer) || player.isBusy()
						|| player.isRanging()
						|| player.getStatus() != Action.USING_Item_ON_PLAYER) {
					return;
				}
				player.resetAll();
				player.face(affectedPlayer);
				if (item.getDef().isMembersOnly()
						&& !Constants.GameServer.MEMBER_WORLD) {
					player.message(player.MEMBER_MESSAGE);
					return;
				}
				if (PluginHandler.getPluginHandler().blockDefaultAction(
						"InvUseOnPlayer",
						new Object[] { player, affectedPlayer, item }))
					return;
				switch (item.getID()) {
				default:
					player.message("Nothing interesting happens");
					break;
				}
			}
		});
	}
}