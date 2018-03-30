package com.legacy.server.net.rsc.handlers;

import com.legacy.server.external.DoorDef;
import com.legacy.server.external.EntityHandler;
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

public class GameObjectWallAction implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Player player) throws Exception {

		int pID = p.getID();
		int packetTwo = OpcodeIn.WALL_OBJECT_COMMAND1.getOpcode();
		
		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		
		player.resetAll();
		final GameObject object = player.getViewArea().getWallObjectWithDir(Point.location(p.readShort(), p.readShort()), p.readByte());
		final int click = pID == packetTwo ? 0 : 1;
		if (object == null) {
			player.setSuspiciousPlayer(true);
			return;
		}
		player.setStatus(Action.USING_DOOR);
		player.setWalkToAction(new WalkToObjectAction(player, object) {
			public void execute() {
				DoorDef def = object.getDoorDef();
				if (player.isBusy() || player.isRanging() || def == null
						|| player.getStatus() != Action.USING_DOOR) {
					player.message("NULL");
					return;
				}

				if (PluginHandler.getPluginHandler().blockDefaultAction(
						"WallObjectAction",
						new Object[] { object, click, player })) {
					return;
				}

				player.resetAll();
				String command = (click == 0 ? def.getCommand1() : def
						.getCommand2()).toLowerCase();
				Point telePoint = EntityHandler.getObjectTelePoint(
						object.getLocation(), command);
				if (telePoint != null) {
					player.teleport(telePoint.getX(), telePoint.getY(), false);
				}
			}
		});
	}
}
