package com.wk.server.plugins.npcs.edgeville;

import static com.wk.server.plugins.Functions.npcTalk;
import static com.wk.server.plugins.Functions.playerTalk;
import static com.wk.server.plugins.Functions.showMenu;

import com.wk.server.Constants.Quests;
import com.wk.server.model.Shop;
import com.wk.server.model.container.Item;
import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.entity.player.Player;
import com.wk.server.net.rsc.ActionSender;
import com.wk.server.plugins.ShopInterface;
import com.wk.server.plugins.listeners.action.TalkToNpcListener;
import com.wk.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class OziachsRunePlateShop  implements ShopInterface,
TalkToNpcListener, TalkToNpcExecutiveListener {

	private final Shop shop = new Shop(false, 30000, 100, 60, 2, new Item(401,
			1));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == 187 && p.getQuestStage(Quests.DRAGON_SLAYER) == -1;
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
		playerTalk(p, n, "I have slain the dragon");
		npcTalk(p, n, "Well done");
		final int option = showMenu(p, n, new String[] {
				"Can I buy a rune plate mail body now please?", "Thank you" });
		if (option == 0) {
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		} 
	}
}
