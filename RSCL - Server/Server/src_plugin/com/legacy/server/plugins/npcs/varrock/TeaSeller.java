package com.legacy.server.plugins.npcs.varrock;

import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.showMenu;

import com.legacy.server.model.Shop;
import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.GroundItem;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.world.World;
import com.legacy.server.net.rsc.ActionSender;
import com.legacy.server.plugins.ShopInterface;
import com.legacy.server.plugins.listeners.action.PickupListener;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.PickupExecutiveListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class TeaSeller implements ShopInterface,
		TalkToNpcExecutiveListener, TalkToNpcListener, PickupExecutiveListener,
		PickupListener {

	private final Shop shop = new Shop(false, 30000, 100, 60, 2, new Item(739,
			20));

	@Override
	public boolean blockPickup(final Player p, final GroundItem i) {
		return i.getID() == 1285;
	}

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == 780;
	}

	@Override
	public Shop[] getShops() {
		return new Shop[] { shop };
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void onPickup(final Player p, final GroundItem i) {
		if (i.getID() == 1285) {
			final Npc n = World.getWorld().getNpcById(780);
			if (n == null) {
				return;
			}
			n.face(p);
			npcTalk(p, n, "hey ! get your hands off that tea !",
					"that's for display purposes only",
					"Im not running a charity here !");
		}
	}

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		npcTalk(p, n, "Greetings!",
				"Are you in need of refreshment ?");

		final String[] options = new String[] { "Yes please", "No thanks",
				"What are you selling?" };
		int option = showMenu(p,n, options);
		switch (option) {
		case 0:
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
			break;
		case 1:
			npcTalk(p, n, "Well if you're sure",
					"You know where to come if you do !");
			break;
		case 2:
			npcTalk(p, n, "Only the most delicious infusion",
					"Of the leaves of the tea plant",
					"Grown in the exotic regions of this world...",
					"Buy yourself a cup !");
			break;
		}
	}

}
