package com.legacy.server.plugins.quests.members;

import static com.legacy.server.plugins.Functions.addItem;
import static com.legacy.server.plugins.Functions.doDoor;
import static com.legacy.server.plugins.Functions.hasItem;
import static com.legacy.server.plugins.Functions.message;
import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.playerTalk;
import static com.legacy.server.plugins.Functions.removeItem;
import static com.legacy.server.plugins.Functions.showMenu;

import com.legacy.server.Constants;
import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.world.World;
import com.legacy.server.plugins.QuestInterface;
import com.legacy.server.plugins.listeners.action.InvUseOnObjectListener;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.action.WallObjectActionListener;
import com.legacy.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.legacy.server.plugins.listeners.executive.WallObjectActionExecutiveListener;

public class DruidicRitual implements QuestInterface,TalkToNpcListener,
		TalkToNpcExecutiveListener, WallObjectActionListener,
		WallObjectActionExecutiveListener, InvUseOnObjectListener,
		InvUseOnObjectExecutiveListener {

	@Override
	public int getQuestId() {
		return Constants.Quests.DRUIDIC_RITUAL;
	}

	@Override
	public String getQuestName() {
		return "Druidic ritual (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player p) {
		p.message("Well done you have completed the druidic ritual quest");
		p.incQuestPoints(4);
		p.message("@gre@You have gained 4 quest points!");
		p.incQuestExp(15, 250);
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == 204) {
			return true;
		}
		if (n.getID() == 205) {
			return true;
		}
		return false;
	}

	class kaqemeex {
		public static final int STONE_CIRCLE = 0;
		public static final int ON_MY_WAY_NOW = 1;
		public static final int SEARCH_OF_QUEST = 2;
	}

	private void kaqemeexDialogue(Player p, Npc n, int cID) {
		if (cID == -1) {
			switch (p.getQuestStage(this)) {
			case 0:
				npcTalk(p, n, "What brings you to our holy Monument");
				int first = showMenu(p, n, new String[] { "Who are you?",
						"I'm in search of a quest", "Did you build this?" });
				if (first == 0) {
					npcTalk(p, n, "We are the druids of Guthix",
							"We worship our God at our famous stone circles");
					int third = showMenu(
							p,
							n,
							new String[] {
									"What about the stone circle full of dark wizards?",
									"So whats so good about Guthix",
									"Well I'll be on my way now" });
					if (third == 0) {
						kaqemeexDialogue(p, n, kaqemeex.STONE_CIRCLE);
					} else if (third == 1) {
						npcTalk(p, n, "Guthix is very important to this world",
								"He is the God of nature and balance",
								"He is in the trees and he is in the rock");
					} else if (third == 2) {
						kaqemeexDialogue(p, n, kaqemeex.ON_MY_WAY_NOW);
					}
				} else if (first == 1) {
					kaqemeexDialogue(p, n, kaqemeex.SEARCH_OF_QUEST);
				} else if (first == 2) {
					npcTalk(p,
							n,
							"Well I didn't build it personally",
							"Our forebearers did",
							"The first druids of Guthix built many stone circles 800 years ago",
							"Only 2 that we know of remain",
							"And this is the only 1 we can use any more");
					int second = showMenu(
							p,
							n,
							new String[] {
									"What about the stone circle full of dark wizards?",
									"I'm in search of a quest",
									"Well I'll be on my way now" });
					if (second == 0) {
						kaqemeexDialogue(p, n, kaqemeex.STONE_CIRCLE);
					} else if (second == 1) {
						kaqemeexDialogue(p, n, kaqemeex.SEARCH_OF_QUEST);
					} else if (second == 2) {
						kaqemeexDialogue(p, n, kaqemeex.ON_MY_WAY_NOW);
					}
				}
				break;
			case 1:
			case 2:
				playerTalk(p, n, "Hello again");
				npcTalk(p,
						n,
						"You need to speak to Sanfew in the village south of here",
						"To continue with your quest");
				break;
			case 3:
				npcTalk(p, n, "I've heard you were very helpful to Sanfew");
				npcTalk(p, n,
						"I will teach you the herblaw you need to know now");
				p.sendQuestComplete(Constants.Quests.DRUIDIC_RITUAL);
				break;
			case -1:
				npcTalk(p, n, "Hello how is the herblaw going?");
				int endMenu = showMenu(p, n, "Very well thankyou", "I need more practice at it");
				if (endMenu == 0) {
					// NOTHING
				} else if (endMenu == 1) {
					// NOTHING
				}
				break;
			}
		}
		switch (cID) {
		case kaqemeex.SEARCH_OF_QUEST:
			npcTalk(p,
					n,
					"We can teach you some of our skill if you complete a quest",
					"We are skilled in the art of herblaw");

			int first = showMenu(p, n, "Ok, I will help you",
					"No I will not bother");
			if (first == 0) {
				npcTalk(p, n,
						" Go speak to Sanfew to the south, he will help you in your quest");
				p.updateQuestStage(getQuestId(), 1);
			} else if (first == 1) {
				// NOTHING
			}
			break;
		case kaqemeex.STONE_CIRCLE:
			npcTalk(p,
					n,
					"That used to be our stone circle",
					"Unfortunatley  many years ago dark wizards cast a wicked spell on it",
					"Corrupting it for their own evil purposes",
					"and making it useless for us",
					"We need someone who will go on a quest for us",
					"to help us purify the circle of Varrock");
			int four = showMenu(p, n, "Ok, I will try and help",
					"No that doesn't sound very interesting",
					"So is there anything in this for me?");
			if (four == 0) {
				npcTalk(p, n, "Ok go and speak to our Elder druid, Sanfew");
				p.updateQuestStage(getQuestId(), 1);
			} else if (four == 1) {
				npcTalk(p, n,
						"Well suit yourself, we'll have to find someone else");
			} else if (four == 2) {
				npcTalk(p, n, "We are skilled in the art of herblaw",
						"We can teach you some of our skill if you complete your quest");
			}
			break;
		case kaqemeex.ON_MY_WAY_NOW:
			npcTalk(p, n, "good bye");
			break;
		}
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == 204) {
			kaqemeexDialogue(p, n, -1); // kaqemeex
		}
		if (n.getID() == 205) {
			switch (p.getQuestStage(this)) {
			case 1:
				npcTalk(p, n, "What can I do for you young 'un?");
				int first = showMenu(
						p,
						n, "I've been sent to help purify the varrock stone circle",
								"Actually I don't need to speak to you" );
				if (first == 0) {
					npcTalk(p,
							n,
							"Well what I'm struggling with",
							"Is the meats I needed for the sacrifice to Guthix",
							"I need the raw meat from 4 different animals",
							"Which all need to be dipped in the cauldron of thunder");
					int second = showMenu(p, n,
							"Where can I find this cauldron?",
							"Ok I'll do that then");
					if (second == 0) {
						npcTalk(p, n,
								"It is in the mysterious underground halls",
								"which are somewhere in the woods to the south of here");
						p.updateQuestStage(getQuestId(), 2);
					} else if (second == 1) {
						p.updateQuestStage(getQuestId(), 2);
					}
				} else if (first == 1) {
					message(p, "Sanfew grunts");
				}
				break;
			case 2:
				npcTalk(p, n, "Have you got what I need yet?");
				if (hasItem(p, 508) && hasItem(p, 505) && hasItem(p, 506)
						&& hasItem(p, 507)) {
					playerTalk(p, n, "Yes I have everything");
					message(p, "You give the meats to Sanfew");
					removeItem(p, 508, 1);
					removeItem(p, 505, 1);
					removeItem(p, 506, 1);
					removeItem(p, 507, 1);
					npcTalk(p,
							n,
							"Thank you, that has brought us much closer to reclaiming our stone circle",
							"Now go and talk to kaqemeex",
							"He will show you what you need to know about herblaw");
					p.updateQuestStage(getQuestId(), 3);
				} else {
					playerTalk(p, n, "no not yet");
					int menu = showMenu(p, n,
							"What was I meant to be doing again?",
							"I'll get on with it" );
					if (menu == 0) {
						npcTalk(p, n,
								"I need the raw meat from 4 different animals",
								"Which all need to be dipped in the cauldron of thunder");
						int secondMenu = showMenu(p, n,
								"Where can I find this cauldron?",
								"Ok I'll do that then");
						if (secondMenu == 0) {
							npcTalk(p,
									n,
									"It is in the mysterious underground halls",
									"which are somewhere in the woods to the south of here");
						} else if (secondMenu == 1) {
							// NOTHING
						}
					} else if (menu == 1) {
						// NOTHING
					}
				}
				break;
			case 3:
			case -1:
				npcTalk(p, n, "What can I do for you young 'un?");
				int finalMenu = showMenu(
						p,
						n,
						new String[] {
								"Have you any more work for me, to help reclaim the circle?",
								"Actually I don't need to speak to you" });
				if (finalMenu == 0) {
					npcTalk(p, n, "Not at the moment",
							"I need to make some more preparations myself now");
				} else if (finalMenu == 1) {
					// NOTHING
				}
				break;
			}
		}

	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click,
			Player player) {
		if (obj.getID() == 63 && obj.getY() == 3332) {
			return true;
		}
		if (obj.getID() == 64 && (obj.getY() == 3336 || obj.getY() == 3332)) {
			return true;
		}
		return false;
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 63 && obj.getY() == 3332) {
			Npc suit = World.getWorld().getNpc(206, 374, 374, 3330, 3334);
			if (suit != null && !(p.getX() <= 373)) {
				p.message("Suddenly the suit of armour comes to life!");
				suit.setChasing(p);
			} else {
				doDoor(obj, p);
			}
		}
		if (obj.getID() == 64 && (obj.getY() == 3336 || obj.getY() == 3332)) {
			doDoor(obj, p);
		}
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item,
			Player player) {
		if (obj.getID() == 236
				&& (item.getID() == 133 || item.getID() == 503
						|| item.getID() == 504 || item.getID() == 502)) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if (obj.getID() == 236
				&& (item.getID() == 133 || item.getID() == 503
						|| item.getID() == 504 || item.getID() == 502)) {
			if (item.getID() == 133) {
				message(p, "You dip the chicken in the cauldron");
				p.getInventory().remove(133, 1);
				
				addItem(p, 508, 1);
			}
			if (item.getID() == 502) {
				message(p, "You dip the bear meat in the cauldron");
				p.getInventory().remove(502, 1);
				
				addItem(p, 505, 1);
			}
			if (item.getID() == 503) {
				message(p, "You dip the rat meat in the cauldron");
				p.getInventory().remove(503, 1);
				
				addItem(p, 506, 1);
			}
			if (item.getID() == 504) {
				message(p, "You dip the beef in the cauldron");
				p.getInventory().remove(504, 1);
				
				addItem(p, 507, 1);
			}
		}
	}
}
