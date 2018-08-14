package com.wk.server.net.rsc.handlers;

import com.wk.server.Constants;
import com.wk.server.model.action.WalkToMobAction;
import com.wk.server.model.container.Item;
import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.entity.player.Player;
import com.wk.server.model.states.Action;
import com.wk.server.model.world.World;
import com.wk.server.net.Packet;
import com.wk.server.net.rsc.PacketHandler;
import com.wk.server.plugins.PluginHandler;

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
