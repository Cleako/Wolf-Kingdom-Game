package com.wk.server.net.rsc.handlers;

import com.wk.server.external.NPCDef;
import com.wk.server.model.action.WalkToMobAction;
import com.wk.server.model.entity.Mob;
import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.entity.player.Player;
import com.wk.server.model.world.World;
import com.wk.server.net.Packet;
import com.wk.server.net.rsc.OpcodeIn;
import com.wk.server.net.rsc.PacketHandler;
import com.wk.server.plugins.PluginHandler;

public final class NpcCommand implements PacketHandler {

	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Player player) throws Exception {
		int pID = p.getID();
		int serverIndex = p.readShort();
		if (player.isBusy()) {
			return;
		}
		final int click = pID == OpcodeIn.NPC_COMMAND1.getOpcode() ? 0 : 1;
		player.click = click;
		final Mob affectedMob = world.getNpc(serverIndex);
		final Npc affectedNpc = (Npc) affectedMob;
		if (affectedNpc == null || affectedMob == null || player == null)
			return;

		player.setFollowing(affectedNpc);
		player.setWalkToAction(new WalkToMobAction(player, affectedMob, 1) {
			public void execute() {
				player.resetFollowing();
				player.resetPath();
				if (player.isBusy() || player.isRanging()
						|| !player.canReach(affectedNpc)) {
					return;
				}
				player.resetAll();
				NPCDef def = affectedNpc.getDef();
				String command = (click == 0 ? def.getCommand1() : def
						.getCommand2()).toLowerCase();
				affectedNpc.resetPath();
				if (PluginHandler.getPluginHandler().blockDefaultAction(
						"NpcCommand", new Object[] { affectedNpc, command, player })) {
					return;
				}
			}
		});
		return;
	}

}
