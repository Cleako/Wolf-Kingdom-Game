package com.legacy.server.plugins.npcs.draynor;

import static com.legacy.server.plugins.Functions.addItem;
import static com.legacy.server.plugins.Functions.hasItem;
import static com.legacy.server.plugins.Functions.message;
import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.playerTalk;
import static com.legacy.server.plugins.Functions.removeItem;
import static com.legacy.server.plugins.Functions.showMenu;

import com.legacy.server.Constants;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.world.World;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class Aggie implements TalkToNpcListener,
		TalkToNpcExecutiveListener {

	private static final int SKIN_PASTE = 0;
	private static final int FROGS = 1;
	private static final int MADWITCH = 2;

	private static final int RED_DYE = 4;
	private static final int DONT_HAVE = 5;
	private static final int WITHOUT_DYE = 6;
	private static final int YELLOW_DYE = 7;
	private static final int BLUE_DYE = 8;
	private static final int DYES = 9;
	private static final int MAKEME = 10;
	private static final int HAPPY = 11;

	public World world = World.getWorld();

	@Override
	public void onTalkToNpc(Player player, final Npc npc) {
		aggieDialogue(player, npc, -1);
	}

	public void aggieDialogue(Player p, Npc n, int cID) {
		if (cID == -1) {
			npcTalk(p, n, "What can I help you with?");
			if (p.getQuestStage(Constants.Quests.PRINCE_ALI_RESCUE) == 2) {
				int choice = showMenu(p, n, new String[] {
						"Could you think of a way to make pink skin paste",
						"What could you make for me",
						"Cool, do you turn people into frogs?",
						"You mad old witch you can't help me",
						"Can you make dyes for me please" });
				if (choice == 0) {
					aggieDialogue(p, n, Aggie.SKIN_PASTE);
				} else if (choice == 1) {
					aggieDialogue(p, n, Aggie.MAKEME);
				} else if (choice == 2) {
					aggieDialogue(p, n, Aggie.FROGS);
				} else if (choice == 3) {
					aggieDialogue(p, n, Aggie.MADWITCH);
				} else if (choice == 4) {
					aggieDialogue(p, n, Aggie.DYES);
				}
			} else {
				int choiceOther = showMenu(p, n, new String[] {
						"What could you make for me",
						"Cool, do you turn people into frogs?",
						"You mad old witch you can't help me",
						"Can you make dyes for me please" });
				if (choiceOther == 0) {
					aggieDialogue(p, n, Aggie.MAKEME);
				} else if (choiceOther == 1) {
					aggieDialogue(p, n, Aggie.FROGS);
				} else if (choiceOther == 2) {
					aggieDialogue(p, n, Aggie.MADWITCH);
				} else if (choiceOther == 3) {
					aggieDialogue(p, n, Aggie.DYES);
				}
			}

			return;
		}
		switch (cID) {
		case Aggie.DYES:
			npcTalk(p, n,
					"What sort of dye would you like? Red, yellow or Blue?");
			int menu13 = showMenu(p, n, new String[] {
					"What do you need to make some red dye please",
					"What do you need to make some yellow dye please",
					"What do you need to make some blue dye please",
					"No thanks, I am happy the colour I am" });
			if (menu13 == 0) {
				aggieDialogue(p, n, Aggie.RED_DYE);
			} else if (menu13 == 1) {
				aggieDialogue(p, n, Aggie.YELLOW_DYE);
			} else if (menu13 == 2) {
				aggieDialogue(p, n, Aggie.BLUE_DYE);
			} else if (menu13 == 3) {
				aggieDialogue(p, n, Aggie.HAPPY);
			}
			break;
		case Aggie.SKIN_PASTE:
			if (hasItem(p, 181) && hasItem(p, 136) && hasItem(p, 50)
					&& hasItem(p, 236)) {
				npcTalk(p, n,
						"Yes I can, you have the ingredients for it already");
				npcTalk(p, n, "Would you like me to mix you some?");
				int menu = showMenu(p, n, new String[] {
						"Yes please, mix me some skin paste",
						"No thankyou, I don't need paste" });
				if (menu == 0) {
					npcTalk(p, n,
							"That should be simple, hand the things to Aggie then");
					message(p,
							"You hand ash, flour, water and redberries to Aggie",
							"She tips it into a cauldron and mutters some words");
					removeItem(p, 181, 1);
					removeItem(p, 136, 1);
					removeItem(p, 141, 1);// Is it done i believe so :O
					removeItem(p, 236, 1);
					npcTalk(p, n,
							"Tourniquet, Fenderbaum, Tottenham, MonsterMunch, MarbleArch");
					message(p, "Aggie hands you the skin paste");
					addItem(p, 240, 1);
					npcTalk(p, n, "There you go dearie, your skin potion",
							"That will make you look good at the Varrock dances");
				}
			} else {
				npcTalk(p,
						n,
						"Why, its one of my most popular potions",
						"The women here, they like to have smooth looking skin",
						"and I must admit, some of the men buy it too",
						"I can make it for you, just get me whats needed");
				playerTalk(p, n, "What do you need to make it?");
				npcTalk(p, n, "Well deary, you need a base for the paste",
						"That's a mix of ash, flour and water",
						"Then you need red berries to colour it as you want",
						"bring me those four items and I will make you some");
			}
			break;
		case Aggie.FROGS:
			npcTalk(p,
					n,
					"Oh, not for years, but if you meet a talking chicken,",
					"You have probably met the professor in the Manor north of here",
					"A few years ago it was flying fish, that machine is a menace");
			break;
		case Aggie.MADWITCH:
			npcTalk(p, n, "Oh, you like to call a witch names, do you?");
			message(p,
					"Aggie waves her hands about, and you seem to be 20 coins poorer");
			removeItem(p, 10, 20);
			npcTalk(p, n,
					"Thats a fine for insulting a witch, you should learn some respect");
			break;
		case Aggie.MAKEME:
			npcTalk(p,
					n,
					"I mostly just make what i find pretty",
					"I sometimes make dye for the womens clothes, brighten the place up",
					"I can make red, yellow, and blue dyes",
					"Would you like some?");
			int menu2 = showMenu(p, n, new String[] {
					"What do you need to make some red dye please",
					"What do you need to make some yellow dye please",
					"What do you need to make some blue dye please",
					"No thanks, I am happy the colour I am" });
			if (menu2 == 0) {
				aggieDialogue(p, n, Aggie.RED_DYE);
			} else if (menu2 == 1) {
				aggieDialogue(p, n, Aggie.YELLOW_DYE);
			} else if (menu2 == 2) {
				aggieDialogue(p, n, Aggie.BLUE_DYE);
			} else if (menu2 == 3) {
				aggieDialogue(p, n, Aggie.HAPPY);
			}
			break;
		case Aggie.YELLOW_DYE:
			npcTalk(p,
					n,
					"Yellow is a strange colour to get, comes from onion skins",
					"I need 2 onions, and 5 coins to make yellow");
			int menu4 = showMenu(p, n, new String[] {
					"Okay, make me some yellow dye please",
					"I don't think I have all the ingredients yet",
					"I can do without dye at that price" });
			if (menu4 == 0) {
				if (!hasItem(p, 241, 2) && hasItem(p, 10, 5)) {
					message(p,
							"You don't have enough onions to make the yellow dye!");
					playerTalk(p, n, "Oh dear. I don't have any money");
				} else {
					message(p, "You hand the onions and payment to Aggie");
					removeItem(p, 241, 2);
					removeItem(p, 10, 5);
					message(p,
							"she takes a yellow bottle from nowhere and hands it to you");
					addItem(p, 239, 1);
				}
			} else if (menu4 == 1) {
				aggieDialogue(p, n, Aggie.DONT_HAVE);
			} else if (menu4 == 2) {
				aggieDialogue(p, n, Aggie.WITHOUT_DYE);
			}
			break;
		case Aggie.RED_DYE:
			npcTalk(p, n, "3 Lots of red berries, and 5 coins, to you");
			int menu3 = showMenu(p, n, new String[] {
					"Okay, make me some red dye please",
					"I don't think I have all the ingredients yet",
					"I can do without dye at that price" });
			if (menu3 == 0) {
				if (!hasItem(p, 236, 3)) {
					message(p,
							"You don't have enough berries to make the red dye!");
					removeItem(p, 10, 5);
					message(p, "Oh dear. I don't have any money");
				} else {
					message(p, "You hand the berries and payment to Aggie");
					removeItem(p, 236, 3);
					removeItem(p, 10, 5);
					message(p,
							"she takes a red bottle from nowhere and hands it to you");
					addItem(p, 238, 1);
				}
			} else if (menu3 == 1) {
				aggieDialogue(p, n, Aggie.DONT_HAVE);
			} else if (menu3 == 2) {
				aggieDialogue(p, n, Aggie.WITHOUT_DYE);
			}
			break;
		case Aggie.BLUE_DYE:
			npcTalk(p, n, "2 woad leaves, and 5 coins, to you");
			int menu6 = showMenu(p, n, new String[] {
					"Okay, make me some blue dye please",
					"I don't think I have all the ingredients yet",
					"I can do without dye at that price" });
			if (menu6 == 0) {
				if (!hasItem(p, 281, 2) && hasItem(p, 10, 5)) {
					message(p,
							"You don't have enough woad leaves to make the blue dye!");
					playerTalk(p, n, "Oh dear. I don't have any money");
				} else {
					message(p, "You hand the woad leaves and payment to Aggie");
					removeItem(p, 281, 2);
					removeItem(p, 10, 5);
					message(p,
							"she takes a blue bottle from nowhere and hands it to you");
					addItem(p, 272, 1);
				}
			} else if (menu6 == 1) {
				aggieDialogue(p, n, Aggie.DONT_HAVE);
			} else if (menu6 == 2) {
				aggieDialogue(p, n, Aggie.WITHOUT_DYE);
			}
			break;
		case Aggie.DONT_HAVE:
			npcTalk(p,
					n,
					"You know what you need to get now, come back when you have them",
					"goodbye for now");
			break;
		case Aggie.WITHOUT_DYE:
			npcTalk(p,
					n,
					"Thats your choice, but I would think you have killed for less",
					"I can see it in your eyes");
			break;
		case Aggie.HAPPY:
			npcTalk(p, n, "You are easily pleased with yourself then",
					"when you need dyes, come to me");
			break;
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 125;
	}
}
