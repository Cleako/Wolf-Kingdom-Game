package com.wk.server.plugins.npcs.portsarim;

import static com.wk.server.plugins.Functions.getNearestNpc;
import static com.wk.server.plugins.Functions.message;
import static com.wk.server.plugins.Functions.npcTalk;
import static com.wk.server.plugins.Functions.playerTalk;

import com.wk.server.Constants.Quests;
import com.wk.server.model.Point;
import com.wk.server.model.entity.GameObject;
import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.entity.player.Player;
import com.wk.server.plugins.listeners.action.ObjectActionListener;
import com.wk.server.plugins.listeners.action.TalkToNpcListener;
import com.wk.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.wk.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.wk.server.plugins.menu.Menu;
import com.wk.server.plugins.menu.Option;

public final class PortSarimSailor implements ObjectActionExecutiveListener, ObjectActionListener, TalkToNpcExecutiveListener,
		TalkToNpcListener {

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		npcTalk(p, n, "Do you want to go on a trip to Karamja?");
		Menu defaultMenu = new Menu();
		if (p.getQuestStage(Quests.DRAGON_SLAYER) == 2) {
			defaultMenu.addOption(new Option("I'd rather go to Crandor Isle") {
				@Override
				public void action() {
					npcTalk(p, n, "No I need to stay alive");
					npcTalk(p, n, "I have a wife and family to support");
				}
			});
		}
		defaultMenu.addOption(new Option("Yes please") {
			@Override
			public void action() {
				if (p.getInventory().remove(10, 30) > -1) {
					message(p, "You pay 30 gold", "You board the ship");
					p.teleport(324, 713, false);
					message(p, "The ship arrives at Karamja");
				} else {
					playerTalk(p, n,
							"Oh dear I don't seem to have enough money");
				}
			}
		});
		defaultMenu.addOption(new Option("No thankyou") {
			@Override
			public void action() {
				npcTalk(p, n, "No I need to stay alive");
				npcTalk(p, n, "I have a wife and family to support");
			}
		});
		defaultMenu.showMenu(p);
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 166 || n.getID() == 171 || n.getID() == 170;
	}

	@Override
	public void onObjectAction(GameObject arg0, String arg1, Player p) {
		Npc sailor = getNearestNpc(p, 166, 10);
		if(sailor != null) {
			sailor.initializeTalkScript(p);
		}
	}
 
	@Override
	public boolean blockObjectAction(GameObject arg0, String arg1, Player arg2) {
		return (arg0.getID() == 155 && arg0.getLocation().equals(Point.location(265, 645)))
				|| (arg0.getID() == 156 && arg0.getLocation().equals(Point.location(265, 650)))
				|| (arg0.getID() == 157 && arg0.getLocation().equals(Point.location(265, 652)));
	}
}
