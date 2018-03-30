package com.legacy.server.plugins.quests.free;

import static com.legacy.server.plugins.Functions.addItem;
import static com.legacy.server.plugins.Functions.doDoor;
import static com.legacy.server.plugins.Functions.getNearestNpc;
import static com.legacy.server.plugins.Functions.message;
import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.playerTalk;
import static com.legacy.server.plugins.Functions.removeItem;
import static com.legacy.server.plugins.Functions.showMenu;
import static com.legacy.server.plugins.Functions.sleep;

import com.legacy.server.Constants;
import com.legacy.server.Constants.Quests;
import com.legacy.server.model.Point;
import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.entity.update.ChatMessage;
import com.legacy.server.plugins.QuestInterface;
import com.legacy.server.plugins.listeners.action.InvUseOnObjectListener;
import com.legacy.server.plugins.listeners.action.ObjectActionListener;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.action.WallObjectActionListener;
import com.legacy.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.legacy.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.legacy.server.plugins.listeners.executive.WallObjectActionExecutiveListener;

public class BlackKnightsFortress implements QuestInterface,TalkToNpcListener,
		ObjectActionListener, ObjectActionExecutiveListener,
		TalkToNpcExecutiveListener, InvUseOnObjectListener,
		InvUseOnObjectExecutiveListener, WallObjectActionExecutiveListener,
		WallObjectActionListener {
	/**
	 * Npc's associated with this quest.
	 */
	private static final int GUARD = 100;
	private static final int WITCH = 107;
	private static final int BLACK_KNIGHT = 108;
	private static final int GRELDO = 109;
	private static final int SIR_AMIK_VARZE = 110;
	/**
	 * GameObjects associated with this quest;
	 */
	private static final int LISTEN_GRILL = 148;
	private static final int DOOR_ENTRANCE = 38;
	private static final int HOLE = 154;
	/**
	 * Items associated with this quest
	 */
	private static final int IRON_CHAINMAIL = 7;
	private static final int BRONZE_MEDIUM_HELMET = 104;
	private static final int CABBAGE = 18;

	private static final Point DOOR_LOCATION = Point.location(271, 441);
	private static final Point DOOR2_LOCATION = Point.location(275, 439);
	private static final Point DOOR3_LOCATION = Point.location(278, 443);

	@Override
	public int getQuestId() {
		return Constants.Quests.BLACK_KNIGHTS_FORTRESS;
	}

	@Override
	public String getQuestName() {
		return "Black knight's fortress";
	}

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		switch (n.getID()) {
		case SIR_AMIK_VARZE:
			handleSirAmikVarze(p, n);
			break;
		}
	}

	private void handleSirAmikVarze(final Player p, final Npc n) {
		switch (p.getQuestStage(this)) {
		case 0:
			npcTalk(p, n, "I am the leader of the white knights of falador",
					"Why do you seek my audience?");
			int menu = showMenu(p, n, "I seek a quest",
					"I don't I'm just looking around");
			if (menu == 0) {
				npcTalk(p, n, "Well I need some spy work doing",
						"It's quite dangerous",
						"You will need to go into the Black Knight's fortress");
				int sub_menu = showMenu(p, n, "I laugh in the face of danger",
						"I go and cower in a corner at the first sign of danger");
				if (sub_menu == 0) {
					if (p.getQuestPoints() < 12) {
						p.message("You need 12 quest points to start this quest");
						return;
					}
					npcTalk(p,
							n,
							"Well that's good",
							"Don't get too overconfident though",
							"You've come along just right actually",
							"All of my knights are known to the black knights already",
							"Subtlety isn't exactly our strong point");
					playerTalk(p, n, "So, what needs doing?");
					npcTalk(p,
							n,
							"Well the black knights have started making strange threats to us",
							"Demanding large amounts of money and land",
							"And threatening to invade Falador if we don't pay",
							"Now normally this wouldn't be a problem",
							"But they claim to have a powerful new secret weapon",
							"What I want you to do is get inside their fortress",
							"Find out what their secret weapon is",
							"And then sabotage it", "You will be well paid");
					playerTalk(p, n, "Ok I'll give it a try");
					p.updateQuestStage(getQuestId(), 1);
				} else if (sub_menu == 1) {
					npcTalk(p, n, "Er", "well",
							"spy work does involve a little hiding in corners I suppose");
					int sub = showMenu(p, n,
							"Oh I suppose I'll give it a go then",
							"No I'm not convinced");

					if (sub == 0) {
						playerTalk(p, n, "So, what needs doing?");
						npcTalk(p,
								n,
								"Well the black knights have started making strange threats to us",
								"Demanding large amounts of money and land",
								"And threatening to invade Falador if we don't pay",
								"Now normally this wouldn't be a problem",
								"But they claim to have a powerful new secret weapon",
								"What I want you to do is get inside their fortress",
								"Find out what their secret weapon is",
								"And then sabotage it", "You will be well paid");
						playerTalk(p, n, "Ok I'll give it a try");
						p.updateQuestStage(getQuestId(), 1);
					}
				}
			} else if (menu == 1) {
				npcTalk(p, n, "Ok, don't break anything");
			}
			break;

		case 1:
			npcTalk(p, n, "How's the mission going?");
			playerTalk(p, n,
					"I haven't managed to find out what the secret weapon is yet");
			break;
		case 2:
			npcTalk(p, n, "How's the mission going?");

			playerTalk(
					p,
					n,
					"I have found out what the Black Knights' secret weapon is.",
					"It's a potion of invincibility.");

			npcTalk(p, n, "A potion of invincibility?",
					"That is grave news indeed.",
					"If you can sabotage it somehow, you will be very well paid.");

			playerTalk(
					p,
					n,
					"So when i finish this mission for you can I be a white knight?",
					"Can I wear your armour too?");

			npcTalk(p,
					n,
					"I am afraid I cannot authorize you to become a white knight",
					"unless under a situation of dire circumstance.",
					"Assisting us on a freelance basis will be well worth your time",
					"however and you will have my personal gratitude.");

			playerTalk(p, n,
					"I can't buy stuff with personal gratitude though...");
			npcTalk(p, n,
					"There is of course also the financial recompense as I said.");
			playerTalk(p, n, "OK! I'll get on to sabotaging that potion.");
			break;
		case 3:
			playerTalk(p, n,
					"I have ruined the black knight's invincibility potion",
					"That should put a stop to your problem.");

			npcTalk(p,
					n,
					"Yes we have just recieved a message from the black knights.",
					"Saying they withdraw their demands.",
					"Which confirms your story");

			playerTalk(p, n, "You said you were going to pay me");
			npcTalk(p, n, "Yes that's right");
			p.sendQuestComplete(Quests.BLACK_KNIGHTS_FORTRESS);
			break;

		case -1:
			playerTalk(p, n, "Hello Sir Amik");
			npcTalk(p, n, "Hello friend");
			break;
		}
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player player) {
		switch (obj.getID()) {
		case HOLE:
			if (item.getID() == CABBAGE && player.getQuestStage(this) == 2) {
				if(removeItem(player, CABBAGE, 1)) {
					message(player,
							"You drop a cabbage down the hole",
							"The cabbage lands in the cauldron below",
							"The mixture in the cauldron starts to froth and bubble",
							"You hear the witch groan in dismay");
					playerTalk(player, null,
							"Right I think that's successfully sabotaged the secret weapon.");
					player.updateQuestStage(this, 3);
				}
			}
			break;
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == SIR_AMIK_VARZE) {
			return true;
		}
		return false;
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item,
			Player player) {
		if (obj.getID() == HOLE) {
			return true;
		}
		return false;
	}

	@Override
	public void handleReward(Player p) {
		p.message("Sir Amik hands you 2500 coins");
		addItem(p, 10, 2500);
		p.message("Well done. You have completed the Black Knights Fortress quest");
		p.incQuestPoints(3);
		p.message("@gre@You have gained 3 quest points!");
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
			Player player) {
		if (obj.getID() == LISTEN_GRILL) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(final GameObject obj, String command,
			final Player player) {
		switch (obj.getID()) {
		case LISTEN_GRILL:
			if (player.getQuestStage(this) == 1) {
				Npc blackKnight = getNearestNpc(player, BLACK_KNIGHT, 20);
				Npc witch = getNearestNpc(player, WITCH, 20);
				Npc greldo = getNearestNpc(player, GRELDO, 20);
				if (witch == null || blackKnight == null || greldo == null) {
					return;
				}
				npcTalk(player, blackKnight,
						"So how's the secret weapon coming along?");
				npcTalk(player,
						witch,
						"The invincibility potion is almost ready",
						"It's taken me five years but it's almost ready",
						"Greldo the Goblin here",
						"Is jus going to fetch the last ingredient for me",
						"It's a specially grown cabbage",
						"Grown by my cousin Helda who lives in Draynor Manor",
						"The soil there is slightly magical",
						"And gives the cabbages slight magic properties",
						"Not to mention the trees",
						"Now remember Greldo only a Draynor Manor cabbage will do",
						"Don't get lazy and bring any old cabbage",
						"That would entirely wreck the potion");
				npcTalk(player, greldo, "Yeth Mithreth");
				player.updateQuestStage(this, 2);
			} else {
				player.message("Nothing interesting happens");
			}
			break;
		}
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click,
			Player player) {
		if (obj.getID() == 38 && obj.getLocation().equals(DOOR_LOCATION)) {
			return true;
		}
		if (obj.getID() == 39 && obj.getLocation().equals(DOOR2_LOCATION)) {
			return true;
		}
		if (obj.getID() == 40 && obj.getLocation().equals(DOOR3_LOCATION)) {
			return true;
		}
		return false;
	}

	@Override
	public void onWallObjectAction(final GameObject obj, Integer click,
			final Player player) {
		switch (obj.getID()) {
		case DOOR_ENTRANCE:
			if (obj.getLocation().equals(DOOR_LOCATION) && player.getX() <= 270) {
				if (player.getInventory().wielding(IRON_CHAINMAIL)
						&& player.getInventory().wielding(BRONZE_MEDIUM_HELMET)) {
					doDoor(obj, player);
					player.teleport(271, 441, false);
				} else {
					final Npc guard = getNearestNpc(player, GUARD, 20);
					if (guard != null) {
						guard.resetPath();
						guard.face(player);
						player.face(guard);
						npcTalk(player, guard, "Heh, you can't come in here",
								"This is a high security military installation");
						int option = showMenu(player, guard, "Yes but I work here", "Oh sorry", "So who does it belong to?");
						if (option == 0) {
							npcTalk(player,
									guard,
									"Well this is the guards entrance",
									"And I might be new here",
									"But I can tell you're not a guard",
									"You're not even wearing proper guards uniform");
									int sub_menu = showMenu(player, guard, "Pleaasse let me in",
													"So what is this uniform?");
									if (sub_menu == 0) {
										npcTalk(player, guard,
												"Go away, you're getting annoying");
									} else if (sub_menu == 1) {
										npcTalk(player,
												guard,
												"Well you can see me wearing it",
												"It's iron chain mail and a medium bronze helmet");
									}
								} else if (option == 1) {
									npcTalk(player, guard,
											"Don't let it happen again");
								} else if (option == 2) {
									npcTalk(player, guard,
											"This fortress belongs to the order of black knights known as the Kinshra");
								}
							}
					}
			} else {
				doDoor(obj, player);
			}
			break;
		case 39:
		case 40:
			if (obj.getLocation().equals(DOOR2_LOCATION)
					&& player.getX() <= 274) {
				final Npc guard = getNearestNpc(player, GUARD, 20);
				if (guard != null) {
					guard.resetPath();
					guard.face(player);
					player.face(guard);
					npcTalk(player, guard,
							"I wouldnt go in there if I was you",
							"Those black knights are in an important meeting",
							"They said they'd kill anyone who went in there");
					int option = showMenu(player, guard, "Ok I won't", "I don't care I'm going in anyway");
					if (option == 1) {
						if (player.getX() <= 274) {
							player.setBusyTimer(0);
							doDoor(obj, player);
							sleep(1200);
							Npc knight = getNearestNpc(player, 66, 5);
							if (knight != null) {
								knight.getUpdateFlags().setChatMessage(new ChatMessage(knight, "DIE INTRUDER!!", player));
								knight.setChasing(player);
							}
						}
					}
				}
			} else if (obj.getLocation().equals(DOOR2_LOCATION) && player.getX() <= 275) {
				doDoor(obj, player);
			} else if (obj.getLocation().equals(DOOR3_LOCATION)) {
				if (player.getY() <= 442) {
					Npc knight = getNearestNpc(player, 66, 4);
					if (knight != null) {
						knight.setChasing(player);
						npcTalk(player, knight, "DIE INTRUDER!!");
					}
					if (!player.inCombat()) {
						doDoor(obj, player);
					}
				} else {
					doDoor(obj, player);
				}
			}
			break;
		}
	}

	@Override
	public boolean isMembers() {
		return false;
	}
}
