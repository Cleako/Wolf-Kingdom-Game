package com.legacy.server.net.rsc.handlers;

import com.legacy.server.Constants;
import com.legacy.server.model.action.WalkToMobAction;
import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.states.Action;
import com.legacy.server.model.world.World;
import com.legacy.server.net.Packet;
import com.legacy.server.net.rsc.PacketHandler;
import com.legacy.server.plugins.PluginHandler;

public class NpcUseItem implements PacketHandler {
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
		int npcIndex = p.readShort();
		final Npc affectedNpc = world.getNpc(npcIndex);
		final Item item = player.getInventory().get(p.readShort());
		if (affectedNpc == null || item == null) {
			return;
		}
		player.setFollowing(affectedNpc);
		player.setStatus(Action.USING_Item_ON_NPC);
		player.setWalkToAction(new WalkToMobAction(player, affectedNpc, 1) {
			public void execute() {
				player.resetPath();
				player.resetFollowing();
				if (!player.getInventory().contains(item) || player.isBusy()
						|| player.isRanging() || !player.canReach(affectedNpc)
						|| affectedNpc.isBusy()
						|| player.getStatus() != Action.USING_Item_ON_NPC) {
					return;
				}
				player.resetAll();

				if (PluginHandler.getPluginHandler().blockDefaultAction(
						"InvUseOnNpc",
						new Object[] { player, affectedNpc, item }))
					return;

				switch (affectedNpc.getID()) {

				default:
					player.message("Nothing interesting happens");
					break;
				}
				if (item.getDef().isMembersOnly()
						&& !Constants.GameServer.MEMBER_WORLD) {
					player.message(player.MEMBER_MESSAGE);
					return;
				}
			}
		});
	}
}
