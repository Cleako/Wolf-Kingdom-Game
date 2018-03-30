package com.legacy.server.plugins.npcs.portsarim;

import static com.legacy.server.plugins.Functions.message;
import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.sleep;

import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.legacy.server.plugins.menu.Menu;
import com.legacy.server.plugins.menu.Option;

public final class MonkOfEntrana implements TalkToNpcExecutiveListener,
		TalkToNpcListener {

	private boolean CAN_GO(Player p) {
		for (Item item : p.getInventory().getItems()) {
			String name = item.getDef().getName().toLowerCase();
			if (name.contains("dagger") || name.contains("scimitar")
					|| (name.contains("bow") && !name.contains("unstrung") && !name.contains("string")) || name.contains("mail")
					|| (name.contains("sword")
					&& !name.equalsIgnoreCase("Swordfish") && !name.equalsIgnoreCase("Burnt Swordfish") && !name.equalsIgnoreCase("Raw Swordfish"))
					|| name.contains("mace") || name.contains("helmet")
					|| name.contains("axe")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == 212;
	}

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		if(n.getLocation().inEntrana()) {
			npcTalk(p, n, "Are you looking to take passage back to port sarim?");
			final Menu defaultMenu = new Menu();
			defaultMenu.addOption(new Option("No I don't wish to go") {
				@Override
				public void action() {
				}
			});
			defaultMenu.addOption(new Option("Yes, Okay I'm ready to go") {
				@Override
				public void action() {
					message(p, "You board the ship");
					p.teleport(264, 660, false);
					sleep(2200);
					p.message("The ship arrives at Entrana");
				}
			});
			defaultMenu.showMenu(p);
			return;
		}
		npcTalk(p, n, "Are you looking to take passage to our holy island?",
				"If so your weapons and armour must be left behind");
		final Menu defaultMenu = new Menu();
		defaultMenu.addOption(new Option("No I don't wish to go") {
			@Override
			public void action() {
			}
		});
		defaultMenu.addOption(new Option("Yes, Okay I'm ready to go") {
			@Override
			public void action() {
				message(p, "The monk quickly searches you");
				if (CAN_GO(p)) {
					npcTalk(p, n, "Sorry we cannot allow you on to our island",
							"Make sure you are not carrying weapons or armour please");
				} else {
					message(p, "You board the ship");
					p.teleport(418, 570, false);
					sleep(2200);
					p.message("The ship arrives at Entrana");
				}
			}
		});
		defaultMenu.showMenu(p);
	}
}
