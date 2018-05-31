package com.wk.server.net.rsc.handlers;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import com.wk.server.model.MenuOptionListener;
import com.wk.server.model.entity.player.Player;
import com.wk.server.model.world.World;
import com.wk.server.net.Packet;
import com.wk.server.net.rsc.PacketHandler;
import com.wk.server.plugins.PluginHandler;

public class MenuReplyHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, final Player player) throws Exception {
		final MenuOptionListener menuHandler = player.getMenuHandler();
		final int option = p.readByte();
		FutureTask<Integer> task = new FutureTask<Integer>(
				new Callable<Integer>() {
					@Override
					public Integer call() throws Exception {
						if (player.getMenu() != null) {
							player.getMenu().handleReply(player, option);
						} else if (menuHandler != null) {
							final String reply = option == 30 ? ""
									: menuHandler.getOption(option);
							player.resetMenuHandler();
							if (reply == null) {
								player.setSuspiciousPlayer(true);
							} else {
								menuHandler.handleReply(option, reply);
							}
						}
						return 1;
					}
				});
		PluginHandler.getPluginHandler().getExecutor().execute(task);
	}
}
