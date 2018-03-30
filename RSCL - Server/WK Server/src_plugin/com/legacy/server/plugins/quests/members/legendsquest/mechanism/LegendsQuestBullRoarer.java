package com.legacy.server.plugins.quests.members.legendsquest.mechanism;

import static com.legacy.server.plugins.Functions.*;

import com.legacy.server.Server;
import com.legacy.server.event.DelayedEvent;
import com.legacy.server.event.SingleEvent;
import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.InvActionListener;
import com.legacy.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.legacy.server.plugins.npcs.shilo.JungleForester;
import com.legacy.server.plugins.quests.members.legendsquest.npcs.LegendsQuestGujuo;
import com.legacy.server.util.rsc.DataConversions;

public class LegendsQuestBullRoarer implements InvActionListener, InvActionExecutiveListener {

	public static final int BULL_ROARER = 1177;

	private boolean inKharaziJungle(Player p) {
		return p.getLocation().inBounds(338, 869, 477, 908);
	}

	@Override
	public boolean blockInvAction(Item item, Player p) {
		if(item.getID() == BULL_ROARER) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvAction(Item item, Player p) {
		if(item.getID() == BULL_ROARER) {
			message(p, 1300, "You start to swing the bullroarer above your head.",
					"You feel a bit silly at first, but soon it makes an interesting sound.");
			if(inKharaziJungle(p)) {
				message(p, 1300, "You see some movement in the trees...");
				attractNatives(p);
			} else {
				message(p, 1300, "Nothing much seems to happen though.");
				Npc forester = getNearestNpc(p, JungleForester.JUNGLE_FORESTER, 10);
				if(forester != null) {
					npcTalk(p, forester, "You might like to use that when you get into the ",
							"Kharazi jungle, it might attract more natives...");
				}
			}
		}
	}

	void attractNatives(Player p) {
		int controlRandom = DataConversions.getRandom().nextInt(4);
		if(controlRandom == 0) {
			message(p, 1300, "...but nothing else much seems to happen.");
		} else if(controlRandom >= 1 && controlRandom <= 2) {
			message(p, 1300, "...and a tall, dark, charismatic looking native approaches you.");
			Npc gujuo = getNearestNpc(p, LegendsQuestGujuo.GUJUO, 15);
			if(gujuo == null) {
				gujuo = spawnNpc(LegendsQuestGujuo.GUJUO, p.getX(), p.getY());
				delayedRemoveGujuo(p, gujuo);
			}
			if(gujuo != null) {
				gujuo.resetPath();
				gujuo.teleport(p.getX(), p.getY());
				gujuo.initializeTalkScript(p);
				sleep(650);
				npcWalkFromPlayer(p, gujuo);
			}
		} else if(controlRandom == 3) {
			Npc nativeNpc = getMultipleNpcsInArea(p, 5, 777, 775, 521, 776);
			if(nativeNpc != null) {
				message(p, 1300, "...and a nearby " + (nativeNpc.getDef().getName().contains("bird") ? nativeNpc.getDef().getName() : "Kharazi " + nativeNpc.getDef().getName().toLowerCase()) + " takes a sudden dislike to you.");
				nativeNpc.setChasing(p);
				message(p, 0, "And attacks...");
			} else {
				attractNatives(p);
			}
		}
	}

	void delayedRemoveGujuo(Player p, Npc n) {
		try {
			Server.getServer().getEventHandler().add(new DelayedEvent(null, 60000 * 3) {
				@Override
				public void run() {
					if (!p.isLoggedIn() || p.isRemoved()) {
						n.remove();
						stop();
						return;
					}
					if (n.isRemoved()) {
						stop();
						return;
					}
					if(!inKharaziJungle(p)) {
						n.remove();
						stop();
						return;
					}
					int yell = DataConversions.random(0, 3);
					if(yell == 1) {
						npcTalk(p, n, "I must visit my people now...");
					} else if(yell == 2) {
						npcTalk(p, n, "I must go and hunt now Bwana..");
					} else if(yell == 3) {
						npcTalk(p, n, "I have to collect herbs now Bwana...");
					} else {
						npcTalk(p, n, "I have work to do Bwana, I may see you again...");
					}
					Server.getServer().getEventHandler().add(new SingleEvent(null, 1900) {
						public void action() {
							p.message("Gujuo disapears into the Kharazi jungle as swiftly as he appeared...");
							n.remove();
						}
					});
					stop();
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
