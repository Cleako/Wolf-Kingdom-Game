package com.legacy.server.plugins.npcs.alkharid;

import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.showMenu;

import com.legacy.server.Constants.Quests;
import com.legacy.server.model.Shop;
import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.net.rsc.ActionSender;
import com.legacy.server.plugins.ShopInterface;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class GemTrader implements ShopInterface,
		TalkToNpcExecutiveListener, TalkToNpcListener {

	public static final int npcid = 308;

	private final Shop shop = new Shop(false, 60000 * 10, 100, 70, 3, new Item(160,
			1), new Item(159, 1), new Item(158, 0), new Item(157, 0),
			new Item(164, 1), new Item(163, 1), new Item(162, 0),
			new Item(161, 0));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == npcid;
	}

	@Override
	public Shop[] getShops() {
		return new Shop[] { shop };
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		if (n.getID() == npcid) {
			npcTalk(p, n, "good day to you " + ((p.isMale()) ? "sir"
					: "madam"), "Would you be interested in buying some gems?");

			final String[] options;
			if (p.getQuestStage(Quests.FAMILY_CREST) <= 2
					|| p.getQuestStage(Quests.FAMILY_CREST) >= 5) {
				options = new String[] { "Yes please", "No thanks" };
			} else {
				options = new String[] { "Yes please", "No thanks",
						"I'm in search of a man named adam fitzharmon" };
			}
			int option = showMenu(p, n, options);

			if (option == 0) {
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
			} else if (option == 2) {
				npcTalk(p,
						n,
						"Fitzharmon eh?",
						"Thats the name of a Varrocian noble family if I'm not mistaken",
						"I have seen a man of that persuasion about the place as of late",
						"Wearing a poncey yellow cape",
						"Came to my store, said he was after jewelry made from the perfect gold",
						"Whatever that means",
						"He's round about the desert still, looking for the perfect gold",
						"He'll be somewhere where he might get some gold I'd wager",
						"He might even be desperate enough to brave the scorpions");
				p.updateQuestStage(Quests.FAMILY_CREST, 4);
			}
		}
	}

}