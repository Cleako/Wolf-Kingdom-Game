package com.legacy.server.net.rsc.handlers;

import com.legacy.server.external.GameObjectDef;
import com.legacy.server.model.Point;
import com.legacy.server.model.action.WalkToObjectAction;
import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.states.Action;
import com.legacy.server.model.world.World;
import com.legacy.server.net.Packet;
import com.legacy.server.net.rsc.OpcodeIn;
import com.legacy.server.net.rsc.PacketHandler;
import com.legacy.server.plugins.PluginHandler;

public class GameObjectAction implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Player player) {
		int pID = p.getID();
		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		player.resetAll();
		final GameObject object = player.getViewArea().getGameObject(Point.location(p.readShort(), p.readShort()));
		
		final int click = pID == OpcodeIn.OBJECT_COMMAND1.getOpcode() ? 0 : 1;
		player.click = click;
		if (object == null) {
			player.setSuspiciousPlayer(true);
			return;
		}
		player.setStatus(Action.USING_OBJECT);
		player.setWalkToAction(new WalkToObjectAction(player, object) {
			public void execute() {
				player.resetPath();
				GameObjectDef def = object.getGameObjectDef();
				if (player.isBusy() || !player.atObject(object) || player.isRanging() || def == null
						|| player.getStatus() != Action.USING_OBJECT) {
					return;
				}

				player.resetAll();
				String command = (click == 0 ? def.getCommand1() : def
						.getCommand2()).toLowerCase();
				player.face(object.getX(), object.getY());
				if (PluginHandler.getPluginHandler().blockDefaultAction(
						"ObjectAction",
						new Object[] { object, command, player })) {
					
					return;
				}
			}
		});
	}
}
