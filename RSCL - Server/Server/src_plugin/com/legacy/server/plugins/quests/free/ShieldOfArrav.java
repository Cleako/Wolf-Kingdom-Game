package com.legacy.server.plugins.quests.free;

import static com.legacy.server.plugins.Functions.addItem;
import static com.legacy.server.plugins.Functions.doDoor;
import static com.legacy.server.plugins.Functions.getNearestNpc;
import static com.legacy.server.plugins.Functions.hasItem;
import static com.legacy.server.plugins.Functions.message;
import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.playerTalk;
import static com.legacy.server.plugins.Functions.removeItem;
import static com.legacy.server.plugins.Functions.showBubble;
import static com.legacy.server.plugins.Functions.showMenu;

import com.legacy.server.Constants;
import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.GroundItem;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.world.World;
import com.legacy.server.plugins.QuestInterface;
import com.legacy.server.plugins.listeners.action.InvActionListener;
import com.legacy.server.plugins.listeners.action.InvUseOnObjectListener;
import com.legacy.server.plugins.listeners.action.InvUseOnWallObjectListener;
import com.legacy.server.plugins.listeners.action.ObjectActionListener;
import com.legacy.server.plugins.listeners.action.PickupListener;
import com.legacy.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.action.WallObjectActionListener;
import com.legacy.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.legacy.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.legacy.server.plugins.listeners.executive.InvUseOnWallObjectExecutiveListener;
import com.legacy.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.legacy.server.plugins.listeners.executive.PickupExecutiveListener;
import com.legacy.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.legacy.server.plugins.listeners.executive.WallObjectActionExecutiveListener;

//no i didnt where its handled....
public class ShieldOfArrav implements QuestInterface,InvUseOnWallObjectListener,
		InvUseOnWallObjectExecutiveListener, PlayerKilledNpcListener,
		PlayerKilledNpcExecutiveListener, PickupExecutiveListener, PickupListener,
		InvActionListener, InvActionExecutiveListener, TalkToNpcListener,
		ObjectActionListener, ObjectActionExecutiveListener,
		TalkToNpcExecutiveListener, InvUseOnObjectListener,
		InvUseOnObjectExecutiveListener, WallObjectActionExecutiveListener,
		WallObjectActionListener {

	class QuestItems {
		public static final int BOOK = 30;// book
		public static final int COINS = 10;// ID: 10 coins
		public static final int KEY = 48; // ID: 48 key (phoenix gang key)
		public static final int SCROLL = 49; // ID: 49 scroll
		public static final int BLACKARM_BROKENSHIELD = 53;// ID: 53 broken
		// shield (blackarm
		// gang half)
		public static final int PHOENIX_BROKENSHIELD = 54; // ID: 54 broken
		// shield (phoenix
		// gang half)
		public static final int CERTIFICATE = 61;// certificate
		public static final int FUR = 146; // ID: 146 fur
		public static final int PHOENIX_CROSSBOW = 59;
	}

	class QuestObjects {
		public static final int BOOKCASE = 67; // ID: 67 bookcase - coords:
		// 132,455
		public static final int DOOR = 19; // ID: 19 door- coords 110,3370
		public static final int DOOR2 = 20; // ID: 20 door - coords: 103,532
	}

	class QuestNpcs {
		public static final int RELDO = 20; // 20 Reldo- coords: 131,457
		public static final int BARAEK = 26; // ID: 26 Baraek- coords 127,506
		public static final int MAN = 24; // ID: 24 Man- coords: 111,3367
		public static final int JONNY = 25; // ID: 25 Jonny the beard- coords:
		// 123,523
		public static final int KING = 42; // ID: 42 king- coords: 126,474
		public static final int CURATOR = 39; // ID: 39 Curator- coords: 101,488
		public static final int TRAMP = 28; // ID: 28 Tramp- coords 132, 527
		public static final int WEAPONMASTER = 37; // ID: 37 weaponsmaster-
		// coords: 105,1477
		public static final int THIEF = 64; // ID: 64 Thief- coords: 110,3375 (4
		// in phoenix gang building)
		public static final int KATRINE = 27; // ID: 27 Katrine- coords: 149,534
	}

	public static final int BLACK_ARM = 0;
	public static final int PHOENIX_GANG = 1;

	@Override
	public int getQuestId() {
		return Constants.Quests.SHIELD_OF_ARRAV;
	}

	@Override
	public String getQuestName() {
		return "Shield of Arrav";
	}

	@Override
	public void handleReward(Player p) {
		p.message("Well done, you have completed the shield of Arrav quest");
		p.message("@gre@You have gained 1 quest point!");
		p.incQuestPoints(1);
		addItem(p, 10, 600);
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item,
			Player player) {
		return false;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player player) {
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == QuestNpcs.KATRINE;
	}

	@Override
	// i meant, where is the plugin handled
	public boolean blockObjectAction(GameObject obj, String command,
			Player player) {
		if (obj.getID() == 81) {
			return true;
		} else if (obj.getID() == 85) {
			return true;
		} else if (obj.getID() == 67) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		switch (obj.getID()) {
		case 67:
			if (player.getQuestStage(this) == 1) {
				playerTalk(player, null, "Aha the shield of Arrav");
				playerTalk(player, null, "That was what I was looking for");
				message(player, "You take the book from the bookcase");
				addItem(player, 30, 1);
			} else {
				player.message("A large collection of books");
			}
			break;
		case 81:
			if (player.getBank().contains(new Item(54))
					|| player.getInventory().contains(new Item(54))) {
				message(player, "You search the chest", "You find nothing.");
				return;
			}
			message(player, "You search the chest",
					"You find half a shield which you take");
			addItem(player, 54, 1);
			break;
		case 85:
			if (player.getBank().contains(new Item(53))
					|| player.getInventory().contains(new Item(53))) {
				message(player, "You search the cupboard", "You find nothing.");
				return;
			}
			message(player, "You search the cupboard",
					"You find half a shield which you take");
			addItem(player, 53, 1);
			break;
		}
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		switch (n.getID()) {
		case QuestNpcs.KATRINE:
			katrineDialogue(p, n, -1);
			break;
		}
	}

	public void katrineDialogue(Player p, Npc n, int cID) {
		if (cID == -1) {
			switch (p.getQuestStage(this)) {
			case 2:
			case 3:
				playerTalk(p, n, "What is this place?");
				npcTalk(p, n, "It's a private business. Can I help you at all?");
				int choice = showMenu(p, n, new String[] {
						"I've heard you're the Black Arm Gang.",
						"What sort of business?",
						"I'm looking for fame and riches" });
				if (choice == 0) {
					katrineDialogue(p, n, Katrine.BLACKARM);
				} else if (choice == 1) {
					npcTalk(p, n,
							"A small, family business. We give financial advice to other companies.");
				} else if (choice == 2) {
					npcTalk(p, n,
							"And you expect to find it in the back streets of varrock?");
				}
				break;
			case 4:
				if (!hasItem(p, QuestItems.PHOENIX_CROSSBOW, 2)) {
					npcTalk(p, n, "Have you got those crossbows for me yet?");
					playerTalk(p, n, "No, I haven't found them yet.");
					npcTalk(p,
							n,
							"I need two crossbows stolen from the phoenix gang weapon stash,",
							"Which if you head east for a bit,",
							"Is a building on the south side of the road.",
							"Come back when you got 'em");
				} else {
					npcTalk(p, n, "Have you got those crossbows for me yet?");
					playerTalk(p, n, "Yes I have");
					removeItem(p, 59, 2);
					npcTalk(p, n,
							"Ok. You can join our gang now. Feel free to enter");
					p.updateQuestStage(this, 5);
				}
				break;
			case 5:
			case -1:
				if (p.getCache().hasKey("arrav_gang")) {
					if (p.getCache().getInt("arrav_gang") == BLACK_ARM
							&& p.getQuestStage(Constants.Quests.HEROS_QUEST) >= 1) {
						playerTalk(p, n, "Hey");
						npcTalk(p, n, "Hey");
						if (hasItem(p, 585)) {
							int choice3 = showMenu(p, n,
									"Who are all those people in there?",
									"I have a candlestick now");
							if (choice3 == 0) {
								npcTalk(p, n,
										"They're just various rogues and thieves");
								playerTalk(p, n, "They don't say a lot");
								npcTalk(p, n, "Nope");
							} else if (choice3 == 1) {
								npcTalk(p, n, "Wow is it really it?");
								p.message("Katrine takes hold of the candlestick and examines it");
								removeItem(p, 585, 1);
								npcTalk(p,
										n,
										"This really is a fine bit of thievery",
										"Thieves have been trying to get hold of this 1 for a while",
										"You wanted to be ranked as master thief didn't you?",
										"Well I guess this just about ranks as good enough");
								playerTalk(p, n,
										"Katrine gives you a master thief armband");
								addItem(p, 586, 1);
							}
							return;
						}
						int choice2 = showMenu(
								p,
								n,
								new String[] {
										"Who are all those people in there?",
										"Is there anyway I can get the rank of master thief?" });
						if (choice2 == 0) {
							npcTalk(p, n,
									"They're just various rogues and thieves");
							playerTalk(p, n, "They don't say a lot");
							npcTalk(p, n, "Nope");
						} else if (choice2 == 1) {
							npcTalk(p,
									n,
									"Master thief? We are the ambitious one aren't we?",
									"Well you're going to have do something pretty amazing");
							playerTalk(p, n, "Anything you can suggest?");
							npcTalk(p,
									n,
									"Well some of the most coveted prizes in thiefdom right now",
									"Are in the  pirate town of Brimhaven on Karamja",
									"The pirate leader Scarface Pete",
									"Has a pair of extremely rare valuable candlesticks",
									"His security is very good",
									"We of course have gang members in a town like Brimhaven",
									"They may be able to help you",
									"visit our hideout in the alleyway on palm street",
									"To get in you will need to tell them the word four leafed clover");
							if (!p.getCache().hasKey("blackarm_mission")) {
								p.getCache().store("blackarm_mission", true);
							}
						}
					} else if (p.getCache().getInt("arrav_gang") == BLACK_ARM
							&& (p.getQuestStage(Constants.Quests.HEROS_QUEST) != 1 && p
									.getQuestStage(Constants.Quests.HEROS_QUEST) != 2)
							|| (p.getCache().getInt("arrav_gang") == BLACK_ARM && p
									.getQuestStage(Constants.Quests.HEROS_QUEST) == -1)) {
						playerTalk(p, n, "Hey");
						npcTalk(p, n, "Hey");
						int choice1 = showMenu(p, n, new String[] {
								"Who are all those people in there?",
								"Teach me to be a top class criminal" });
						if (choice1 == 0) {
							npcTalk(p, n,
									"They're just various rogues and thieves");
							playerTalk(p, n, "They don't say a lot");
							npcTalk(p, n, "Nope");
						} else if (choice1 == 1) {
							npcTalk(p, n, "Teach yourself");
						}
					} else {
						npcTalk(p, n, "You've got some guts coming here",
								"Phoenix guy");
						p.message("Katrine Spits");
						npcTalk(p, n, "Now go away",
								"Or I'll make sure you 'aven't got those guts anymore");
					}
				}
				break;
			}
			return;
		}
		switch (cID) {
		case Katrine.BLACKARM:
			npcTalk(p, n, "Who told you that?");
			int choice = showMenu(p, n, new String[] {
					"I'd rather not reveal my sources",
					"It was the tramp outside.",
					"Everyone knows - it's no great secret." });
			if (choice == 0) {
				npcTalk(p, n,
						"Yes, I can understand that. So what do you want with us?");
			} else if (choice == 1) {
				npcTalk(p,
						n,
						"Is that guy still out there? he's getting to be a nuisance...",
						"Remind me to send someone to kill him.",
						"So now you've found us, what do you want?");
			} else if (choice == 2) {
				npcTalk(p, n, "I thought we were safe back here!");
				playerTalk(p, n, "Oh no, not at all...It's so obvious!",
						"Even the town guard have caught on...");
				npcTalk(p,
						n,
						"Wow! We must be obvious!",
						"I guess they'll be expecting bribes again soon in that case.",
						"Thanks for the information.",
						"Is there anything else you want to tell me?");
			}
			int choice1 = showMenu(p, n, new String[] {
					"I want to become a member of your gang.",
					"I want some hints for becoming a thief.",
					"I'm looking for the door out of here." });
			if (choice1 == 0) {
				katrineDialogue(p, n, Katrine.MEMBER);
			} else if (choice1 == 1) {
				npcTalk(p,
						n,
						"Well, I'm sorry luv, I'm not giving away any of my secrets.",
						"Not to people who ain't black arm members anyway");
			} else if (choice1 == 2) {
				p.message("Katrine groans.");
				npcTalk(p, n, "Try... the one you just came in?");
			}
			break;
		case Katrine.MEMBER:
			npcTalk(p,
					n,
					"How unusual.",
					"Normally we recruit for our gang by watching local thugs and thieves in action.",
					"People don't normally waltz in here saying 'hello, can i play'.",
					"How can I be sure you can be trusted?");
			int choice11 = showMenu(p, n, new String[] {
					"Well, you can give me a try can't you?",
					"Well, people tell me I have an honest face." });
			if (choice11 == 0) {
				npcTalk(p, n, "I'm not so sure.");
			} else if (choice11 == 1) {
				npcTalk(p, n, "... How unusual.",
						"Someone honest wanting to join a gang of thieves.",
						"Excuse me if I remain unconvinced");
			}
			katrineDialogue(p, n, Katrine.GIVETRY);
			break;
		case Katrine.GIVETRY:
			npcTalk(p,
					n,
					"Thinking about it... I may have a solution actually.",
					"Our rival gang - the phoenix gang - has a weapons stash a little east of here.",
					"We're fresh out of crossbows",
					"So if you could steal a couple of crossbows for us",
					"It would be very much appreciated",
					"Then I'll be happy to call you a blackarm");
			playerTalk(p, n,
					"Sounds simple enough. Any particular reason you need two of them?");
			npcTalk(p,
					n,
					"I have an idea for framing a local merchant who is refusing to pay",
					"Our very reasonable 'keep-your-life-pleasant' insurance rates.",
					"I need two phoenix crossbows",
					"One to kill somebody important with",
					"And the other to hide in the merchants house",
					"Where the local law can find it!",
					"When they find it, they'll suspect him of murdering",
					"The target for the phoenix gang and hopefully, arrest the whole gang!",
					"Leaving us as the only thieves gang in varrock! Brillant, eh?");
			playerTalk(p, n,
					"Yeah, brillant. So who are you planning to murder?");
			npcTalk(p,
					n,
					"I haven't decided yet, but it'll need to be somebody important.",
					"Say, why you being so nosey?",
					"You aren't with the law are you?");
			playerTalk(p, n, "No, no! Just curious.");
			npcTalk(p, n,
					"You'd better just keep your mouth shut about this plan",
					"Or i'll make sure it stays shut for you.",
					"Now are you going to go get those crossbows or not?");
			int choice3 = showMenu(p, n, new String[] { "Ok, no problem.",
					"Sounds a little tricky. Got anything easier?" });
			if (choice3 == 0) {
				npcTalk(p,
						n,
						"Great!",
						"You'll find the phoenix gang's weapon stash just next to a temple",
						"Due east of here.");
				p.getCache().set("arrav_gang", BLACK_ARM);
				p.updateQuestStage(this, 4);
			} else if (choice3 == 1) {
				npcTalk(p, n, "If you're not up to a little bit of danger",
						"I don't think you've got anything to offer our gang.");
			}
			break;
		}
	}

	class Katrine {
		public static final int GIVETRY = 4;
		public static final int MEMBER = 3;
		public static final int BLACKARM = 0;
	}

	@Override
	public boolean blockInvAction(Item item, Player player) {
		return item.getID() == QuestItems.BOOK;
	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		if ((i.getX() == 107 || i.getX() == 105) && i.getY() == 1476) {
			if (p.getCache().hasKey("arrav_gang")) {
				if (p.getCache().getInt("arrav_gang") == BLACK_ARM) {
					Npc weaponMaster = getNearestNpc(p, QuestNpcs.WEAPONMASTER, 20);
					if (weaponMaster != null) {
						return true;
					}
				}
			}
		}
		return false;
	}// lets take a look at it later

	@Override
	public void onInvAction(Item item, Player player) {
		switch (item.getID()) {
		case QuestItems.BOOK:
			message(player,
					"The shield of Arrav",
					"By A.R.Wright",
					"Arrav is probably the best known hero of the 4th age.",
					"One surviving artifact from the 4th age is a fabulous shield.",
					"This shield is believed to have once belonged to Arrav",
					"And is now indeed known as the shield of Arrav",
					"For 150 years it was the prize piece in the royal museum of Varrock.",
					"However in the year 143 of the 5th age",
					"A gang of thieves called the phoenix gang broke into the museum",
					"And stole the shield.",
					"King Roald VII put a 1200 gold reward on the return of the shield.",
					"The thieves who stole the shield",
					"Have now become the most powerful crime gang in Varrock.",
					"The reward for the return of the shield still stands.");
			if(!player.getCache().hasKey("read_arrav")) {
				player.getCache().store("read_arrav", true);
			}
			break;
		}
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if (p.getQuestStage(this) == 4) {
			World.getWorld().registerItem(
					new GroundItem(QuestItems.SCROLL, n.getX(), n.getY(), 1, p));// brb
			// shop
			// alright
			// bro
			// :)
			n.killedBy(p);
		} else {
			n.killedBy(p);
		}
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 21 && obj.getY() == 533) {
			if (p.getCache().getInt("arrav_gang") == BLACK_ARM) {
				if (p.getY() >= 533) {
					doDoor(obj, p);
					p.teleport(148, 532, false);
				} else {
					doDoor(obj, p);
					p.teleport(148, 533, false);
				}
			} else {
				p.message("The door is securely locked");
			}
			return;
		} else if (obj.getID() == 19 && obj.getY() == 3370) {
			Npc man = getNearestNpc(p, 24, 20);
			if (p.getCache().hasKey("arrav_gang")) {
				if (p.getCache().getInt("arrav_gang") == PHOENIX_GANG) { // I
					// fixed
					// this.
					if (p.getQuestStage(this) != 5) {
						if (man != null) {
							man.initializeTalkScript(p);// brb need to take dogs out okii
						}
					} else {
						if (p.getY() <= 3369) {
							doDoor(obj, p);
							p.teleport(p.getX(), p.getY() + 1, false);
						} else {
							doDoor(obj, p);
							p.teleport(p.getX(), p.getY() - 1, false);
						}
					}
				} else if (p.getCache().getInt("arrav_gang") == BLACK_ARM) {// well,
					// u
					// see,
					// the
					// problem
					// with
					// this
					// is
					// that
					// its
					// gonna
					// throw
					// exception
					// if
					// it
					// doesnt
					// exist
					if (man != null) {
						npcTalk(p, man, "hey get away from there",
								"Black arm dog");
						man.setChasing(p);
					}
				}
			} else {
				if(man != null) {
					man.initializeTalkScript(p);
				}
			}
			return;
		} else if (obj.getID() == 20 && obj.getY() == 532) {
			if (p.getY() <= 531 || p.getY() >= 531) {
				if (p.getInventory().hasItemId(48)) {
					p.message("The door is locked");
					p.message("You need to use your key to open it");
					return;
				}
				p.message("The door is locked");
			}
			return;
		}
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click,
			Player player) {
		if (obj.getID() == 19 && obj.getY() == 3370) {
			return true;
		}
		if (obj.getID() == 21 && player.getCache().hasKey("arrav_gang")
				&& player.getCache().getInt("arrav_gang") == BLACK_ARM) {
			return true;
		}
		if (obj.getID() == 20 && obj.getY() == 532) {
			return true;
		}
		return false;
	}

	@Override
	public boolean blockInvUseOnWallObject(GameObject obj, Item item,
			Player player) {
		if (item.getID() == QuestItems.KEY && obj.getID() == 20
				&& obj.getY() == 532) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnWallObject(GameObject obj, Item item, Player player) {
		if (item.getID() == QuestItems.KEY && obj.getID() == 20
				&& obj.getY() == 532) {
			showBubble(player, item);
			if (player.getY() <= 531) {
				doDoor(obj, player);
				player.teleport(player.getX(), player.getY() + 1, false);
			} else {
				doDoor(obj, player);
				player.teleport(player.getX(), player.getY() - 1, false);
			}
		}

	}

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		if (n.getID() == QuestNpcs.JONNY)
			return true;

		return false;
	}

	@Override
	public void onPickup(Player p, GroundItem i) {
		if ((i.getX() == 107 || i.getX() == 105) && i.getY() == 1476) {
			if (p.getCache().hasKey("arrav_gang")) {
				if (p.getCache().getInt("arrav_gang") == BLACK_ARM) {
					Npc weaponMaster = getNearestNpc(p, QuestNpcs.WEAPONMASTER, 20);
					if (weaponMaster != null) {
						npcTalk(p, weaponMaster, "Hey Thief!");
						weaponMaster.setChasing(p);
					}
				}
			}
		}
	}
}
