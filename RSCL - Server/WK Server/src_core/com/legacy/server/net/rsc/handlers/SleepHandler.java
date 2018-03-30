package com.legacy.server.net.rsc.handlers;

import com.legacy.server.Server;
import com.legacy.server.event.SingleEvent;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.world.World;
import com.legacy.server.net.Packet;
import com.legacy.server.net.rsc.ActionSender;
import com.legacy.server.net.rsc.PacketHandler;
import com.legacy.server.sql.GameLogging;
import com.legacy.server.sql.query.logs.GenericLog;

public final class SleepHandler implements PacketHandler {

	public void handlePacket(Packet p, Player player) throws Exception {

		String sleepword = p.readString().trim();
		if (sleepword.equalsIgnoreCase("-null-")) {
			player.incrementSleepTries();

			Server.getServer()
					.getEventHandler()
					.add(new SingleEvent(player, player
							.getIncorrectSleepTimes() * 1000) {
						@Override
						public void action() {
							ActionSender.sendEnterSleep(owner);
						}
					});
		} else {
			if (!player.isSleeping()) {
				return;
			}
			if (sleepword.equalsIgnoreCase(player.getSleepword())) {
				ActionSender.sendWakeUp(player, true, false);
				player.resetSleepTries();
			} else {
				ActionSender.sendIncorrectSleepword(player);
				player.incrementSleepTries();
				if (player.getIncorrectSleepTimes() > 5) {
					World.getWorld().sendModAnnouncement(player.getUsername() + " has failed sleeping captcha " + player.getIncorrectSleepTimes() + " times!");
					GameLogging.addQuery(new GenericLog(player.getUsername() + " has failed sleeping captcha " + player.getIncorrectSleepTimes() + " times!"));
				}

				Server.getServer().getEventHandler()
						.add(new SingleEvent(player, player
								.getIncorrectSleepTimes() * 1000) {
							@Override
							public void action() {
								ActionSender.sendEnterSleep(owner);
							}
						});
			}
		}
	}
}
