package com.legacy.server.plugins.misc;

import static com.legacy.server.plugins.Functions.*;

import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.InvActionListener;
import com.legacy.server.plugins.listeners.executive.InvActionExecutiveListener;

public class TeleportStone implements InvActionListener, InvActionExecutiveListener {

	private final int TELEPORT_STONE = 2107;

	@Override
	public boolean blockInvAction(Item item, Player player) {
		if(item.getID() == TELEPORT_STONE) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvAction(Item item, Player p) {
		if(item.getID() == TELEPORT_STONE) {
			message(p, "the stone starts shaking...");
			p.message("a magical portal opens up, where would you like to go?");
			String[] teleLoc = { "Lumbridge", "Draynor", "Falador", "Edgeville", "Varrock", "Alkharid", "Karamja", "Yanille", "Ardougne", "Catherby", "Seers", "Gnome Stronghold", "Stay here" };
			int menu = showMenu(p, teleLoc);
			if(p.getLocation().inWilderness() && System.currentTimeMillis() - p.getCombatTimer() < 10000) {
				p.message("You need to stay out of combat for 10 seconds before using a teleport.");
				return;
			}
			if (p.getLocation().wildernessLevel() >= 30 || (p.getLocation().inModRoom() && !p.isAdmin())) {
				p.message("A mysterious force blocks your teleport!");
				p.message("You can't use this teleport after level 30 wilderness");
				return;
			}
			if(p.getInventory().countId(1039) > 0) {
				p.message("You can't teleport with ana in the barrel in your inventory.");
				return;
			}
			if(p.getInventory().hasItemId(812)) {
				p.message("the plague sample is too delicate...");
				p.message("it disintegrates in the crossing");
				while(p.getInventory().countId(812) > 0) {
					p.getInventory().remove(new Item(812));
				}
			}
			switch(menu) {
			case -1:// stop them.
				return;
			case 0: // lumb
				p.teleport(125, 648);
				break;
			case 1: // dray
				p.teleport(214, 632);
				break;
			case 2: // falla
				p.teleport(304, 542);
				break;
			case 3: // edge
				p.teleport(223, 447);
				break;
			case 4: // varrock
				p.teleport(122, 509);
				break;
			case 5: // alkharid
				p.teleport(85, 691);
				break;
			case 6: // Karamja
				p.teleport(372, 706);
				break;
			case 7: // Yanille
				p.teleport(583, 747);
				break;
			case 8: // Ardougne
				p.teleport(557, 606);
				break;
			case 9: // Catherby
				p.teleport(442, 503);
				break;
			case 10: // Seers
				p.teleport(493, 456);
				break;
			case 11: // Gnome Stronghold
				p.teleport(703, 481);
				break;
			case 12:
				return;
			}
			removeItem(p, TELEPORT_STONE, 1);
			sleep(650);
			p.message("You landed in " + teleLoc[menu]);
		}
	}
}
