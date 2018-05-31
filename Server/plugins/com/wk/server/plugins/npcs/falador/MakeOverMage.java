package com.wk.server.plugins.npcs.falador;

import static com.wk.server.plugins.Functions.hasItem;
import static com.wk.server.plugins.Functions.npcTalk;
import static com.wk.server.plugins.Functions.playerTalk;
import static com.wk.server.plugins.Functions.removeItem;
import static com.wk.server.plugins.Functions.showMenu;

import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.entity.player.Player;
import com.wk.server.model.world.World;
import com.wk.server.net.rsc.ActionSender;
import com.wk.server.plugins.listeners.action.TalkToNpcListener;
import com.wk.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class MakeOverMage implements TalkToNpcListener,
		TalkToNpcExecutiveListener {
	/**
	 * World instance
	 */
	public World world = World.getWorld();

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		npcTalk(p, n, "Are you happy with your looks?",
				"If not I can change them for the cheap cheap price",
				"Of 3000 coins");
		int opt = showMenu(p, n, "I'm happy with how I look thank you",
				"Yes change my looks please");
		if (opt == 1) {
			if (!hasItem(p, 10, 3000)) {
				playerTalk(p, n,"I'll just go get the cash");
			} else {
				removeItem(p, 10, 3000);
				p.setChangingAppearance(true);
				ActionSender.sendAppearanceScreen(p);
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 339;
	}

}
