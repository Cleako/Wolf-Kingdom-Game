package com.wk.server.net.rsc.handlers;

import com.wk.server.Constants;
import com.wk.server.Server;
import com.wk.server.event.MiniEvent;
import com.wk.server.model.container.Item;
import com.wk.server.model.entity.player.Player;
import com.wk.server.model.world.World;
import com.wk.server.net.Packet;
import com.wk.server.net.rsc.ActionSender;
import com.wk.server.net.rsc.PacketHandler;
import com.wk.server.plugins.PluginHandler;

public class ItemActionHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Player player) throws Exception {

		int idx = (int) p.readShort();
		if (player == null || player.getInventory() == null) {
			return;
		}

		if (idx < 0 || idx >= player.getInventory().size()) {
			player.setSuspiciousPlayer(true);
			return;
		}
		final Item item = player.getInventory().get(idx);

		if (item == null || item.getDef().getCommand().equals("")) {
			player.setSuspiciousPlayer(true);
			return;
		}

		if (!player.getLocation().isMembersWild() && item.getDef().isMembersOnly()) {
			player.message("Members content can only be used in wild levels: " + World.membersWildStart + " - " + World.membersWildMax);
			return;
		}
		
		if (item.getDef().isMembersOnly() && !Constants.GameServer.MEMBER_WORLD) {
			player.message("You need to be a member to use this object");
			return;
		}

		if (player.isBusy()) {
			if (player.inCombat()) {
				player.message("You can't do that whilst you are fighting");
			}
			return;
		}

		player.resetAll();

		if (PluginHandler.getPluginHandler().blockDefaultAction("InvAction",
				new Object[] { item, player })) {
			return;
		}

		if (item.getID() == 1263 && !player.isSleeping()) {
			ActionSender.sendEnterSleep(player);
			player.startSleepEvent(false);
			// player.resetPath(); - real rsc.
			return;
		}

		if (item.getDef().getCommand().equalsIgnoreCase("bury")) {
			if(item.getID() == 1308 || item.getID() == 1648 || item.getID() == 1793 || item.getID() == 1871 || item.getID() == 2257) {
				player.message("You can't bury noted bones");
				return;
			}
			player.setBusyTimer(650);
			player.message("You dig a hole in the ground");
			Server.getServer().getEventHandler()
					.add(new MiniEvent(player) {
						public void action() {
							owner.message("You bury the "
									+ item.getDef().getName().toLowerCase());
							owner.getInventory().remove(item);
							switch (item.getID()) {
							case 20: // Bones
								owner.incExp(5, Math.ceil(3.75), true); // 3.75
								break;
							case 604: // Bat bones
								owner.incExp(5, Math.ceil(4.5), true); // 4.5
								break;
							case 413: // Big bones
								owner.incExp(5, Math.ceil(12.5), true); // 12.5
								break;
							case 814: // Dragon bones
								owner.incExp(5, Math.ceil(60), true); // 60
								break;
							case 2256: // Soul of Greatwood
								owner.incExp(5, Math.ceil(800), true); // 800
								break;
							}
						}
					});
		} else {
			switch (item.getID()) {
			case 387: // Disk of Returning
				player.message("The disk doesn't seem to work here.");
				break;
			default:
				player.message("Nothing interesting happens");
				return;
			}
		}
	}
}
