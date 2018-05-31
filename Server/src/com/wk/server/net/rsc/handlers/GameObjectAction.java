package com.wk.server.net.rsc.handlers;

import com.wk.server.external.GameObjectDef;
import com.wk.server.model.Point;
import com.wk.server.model.action.WalkToObjectAction;
import com.wk.server.model.entity.GameObject;
import com.wk.server.model.entity.player.Player;
import com.wk.server.model.states.Action;
import com.wk.server.model.world.World;
import com.wk.server.net.Packet;
import com.wk.server.net.rsc.OpcodeIn;
import com.wk.server.net.rsc.PacketHandler;
import com.wk.server.plugins.PluginHandler;

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
