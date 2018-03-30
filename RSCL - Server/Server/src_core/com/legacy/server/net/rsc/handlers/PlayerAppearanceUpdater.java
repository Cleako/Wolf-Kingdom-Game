package com.legacy.server.net.rsc.handlers;

import com.legacy.server.model.PlayerAppearance;
import com.legacy.server.model.container.Inventory;
import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.world.World;
import com.legacy.server.net.Packet;
import com.legacy.server.net.rsc.ActionSender;
import com.legacy.server.net.rsc.PacketHandler;

public class PlayerAppearanceUpdater implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Player player) throws Exception {

		if (!player.isChangingAppearance()) {
			player.setSuspiciousPlayer(true);
			return;
		}
		player.setChangingAppearance(false);
		byte headGender = p.readByte();
		byte headType = p.readByte();
		byte bodyGender = p.readByte();

		p.readByte(); // wtf is this?

		int hairColour = (int) p.readByte();
		int topColour = (int) p.readByte();
		int trouserColour = (int) p.readByte();
		int skinColour = (int) p.readByte();

		int headSprite = headType + 1;
		int bodySprite = bodyGender + 1;

		PlayerAppearance appearance = new PlayerAppearance(hairColour,
				topColour, trouserColour, skinColour, headSprite, bodySprite);
		if (!appearance.isValid()) {
			player.setSuspiciousPlayer(true);
			return;
		}

		player.setMale(headGender == 1);

		if (player.isMale()) {
			Inventory inv = player.getInventory();
			for (int slot = 0; slot < inv.size(); slot++) {
				Item i = inv.get(slot);
				if (i.isWieldable() && i.getDef().getWieldPosition() == 1
						&& i.isWielded() && i.getDef().isFemaleOnly()) {
					player.getInventory().unwieldItem(i, false);
					ActionSender.sendInventoryUpdateItem(player, slot);
					break;
				}
			}
		}
		int[] oldWorn = player.getWornItems();
		int[] oldAppearance = player.getSettings().getAppearance().getSprites();
		player.getSettings().setAppearance(appearance);
		int[] newAppearance = player.getSettings().getAppearance().getSprites();
		for (int i = 0; i < 12; i++) {
			if (oldWorn[i] == oldAppearance[i]) {
				player.updateWornItems(i, newAppearance[i]);
			}
		}
	}
}
