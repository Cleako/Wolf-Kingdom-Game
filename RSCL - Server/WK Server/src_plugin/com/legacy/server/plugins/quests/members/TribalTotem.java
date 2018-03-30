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
import com.legacy.server.model.Point;
import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.world.World;
import com.legacy.server.plugins.QuestInterface;
import com.legacy.server.plugins.listeners.action.InvUseOnObjectListener;
import com.legacy.server.plugins.listeners.action.ObjectActionListener;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.action.WallObjectActionListener;
import com.legacy.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.legacy.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.legacy.server.plugins.listeners.executive.WallObjectActionExecutiveListener;

public class TribalTotem implements QuestInterface,TalkToNpcListener,
		TalkToNpcExecutiveListener, ObjectActionListener,
		ObjectActionExecutiveListener, InvUseOnObjectListener,
		InvUseOnObjectExecutiveListener, WallObjectActionListener,
		WallObjectActionExecutiveListener {

	@Override
	public int getQuestId() {
		return Constants.Quests.TRIBAL_TOTEM;
	}

	@Override
	public String getQuestName() {
		return "Tribal totem (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player p) {
		p.incQuestPoints(1);
		p.message("Well done you have completed the tribal totem quest");
		p.message("@gre@You haved gained 1 quest point!");
		p.incQuestExp(17, (75 * p.getSkills().getMaxStat(17)) + 200); // THIEVING XP.

	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == 332) {
			return true;
		}
		if (n.getID() == 335) {
			return true;
		}
		if (n.getID() == 333) {
			return true;
		}
		if (n.getID() == 334) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == 332) {
			switch (p.getQuestStage(this)) {
			case 0:
				npcTalk(p, n, "Hello I Kangai Mau", "Of the Rantuki tribe");
				int opt = showMenu(p, n,
						"And what are you doing in Brimhaven?",
						"I'm in search of adventure",
						"Who are the Rantuki tribe?");
				if (opt == 0) {
					npcTalk(p, n, "I looking for someone brave",
							"To go on important mission for me",
							"Someone skilled in thievery and sneaking about",
							"I am told I can find such people in Brimhaven");
					int sub_opt = showMenu(p, n, "Tell me of this mission",
							"Yep I have heard there are many of that type here");
					if (sub_opt == 1) {
						playerTalk(p, n, "I may be able to help");
						npcTalk(p,
								n,
								"I need someone to go on a mission",
								"To the city of Ardougne",
								"There you will need to find the house of Lord Handelmort",
								"In his house he has our tribal totem",
								"We need it back");
						int sub_menu = showMenu(p, n, "Ok I will get it back",
								"Why does he have it?",
								"How can I find Handelmort's house?");
						if (sub_menu == 0) {
							p.updateQuestStage(this, 1);
						} else if (sub_menu == 1) {
							npcTalk(p,
									n,
									"Lord Handelmort is an Ardougnese explorer",
									"Which mean he think he allowed to come and steal our stuff",
									"To put in his private museum");
							int sub_opt1 = showMenu(p, n,
									"Ok I will get it back",
									"How can I find Handlemort's house?");
							if (sub_opt1 == 0) {
								p.updateQuestStage(this, 1);
							} else if (sub_opt1 == 1) {
								playerTalk(p, n, "Ardougne is a big place");
								npcTalk(p, n, "I don't know Ardougne");
							}

						} else if (sub_menu == 1) {
							playerTalk(p, n, "Ardougne is a big place");
							npcTalk(p, n, "I don't know Ardougne");
						}

					}
				} else if (opt == 1) {
					npcTalk(p,
							n,
							"Adventure is something I may be able to give",
							"I need someone to go on a mission",
							"To the city of Ardougne",
							"There you will need to find the house of Lord Handelmort",
							"In his house he has our tribal totem",
							"We need it back");
					int sub_opt = showMenu(p, n, "Ok I will get it back",
							"Why does he have it?",
							"How can I find Handelmort's house?");
					if (sub_opt == 0) {
						p.updateQuestStage(this, 1);
					} else if (sub_opt == 1) {
						npcTalk(p,
								n,
								"Lord Handelmort is an Ardougnese explorer",
								"Which mean he think he allowed to come and steal our stuff",
								"To put in his private museum");
						int sub_opt1 = showMenu(p, n, "Ok I will get it back",
								"How can I find Handlemort's house?");
						if (sub_opt1 == 0) {
							p.updateQuestStage(this, 1);
						} else if (sub_opt1 == 1) {
							playerTalk(p, n, "Ardougne is a big place");
							npcTalk(p, n, "I don't know Ardougne");
						}

					} else if (sub_opt == 1) {
						playerTalk(p, n, "Ardougne is a big place");
						npcTalk(p, n, "I don't know Ardougne");
					}
				} else if (opt == 2) {
					npcTalk(p, n, "A proud and noble tribe of Karamja",
							"Now we are few", "Men come from across sea",
							"And settle on our hunting grounds");
				}
				break;
			case 1:
			case 2:
				npcTalk(p, n, "Have you got our totem back?");
				if (p.getInventory().countId(705) >= 1) {
					playerTalk(p, n, "Yes I have");
					npcTalk(p, n, "Thank you brave adventurer");
					p.sendQuestComplete(Constants.Quests.TRIBAL_TOTEM);
					npcTalk(p, n, "Here have some freshly cooked Karamja fish",
							"Caught specially by our people");
					removeItem(p, 705, 1);
					addItem(p, 370, 5);
				} else {
					playerTalk(p, n, "No it's not that easy");
					npcTalk(p, n, "Bah, you no good");
				}
				break;
			case -1:
				npcTalk(p, n, "greetings esteemed thief");
				break;
			}
		}
		if (n.getID() == 335) {
			npcTalk(p, n, "It's a fine day to be out in the garden isn't it?");
			int menu = showMenu(p, n, "Yes, it's very nice", "So who are you?");
			if (menu == 1) {
				npcTalk(p, n, "My name is Horacio Dobson",
						"I am the gardener to Lord Handelmort",
						"All this around you is my handywork");
				int sub_menu = showMenu(p, n,
						"So do you garden round the back too?",
						"Do you need any help?");
				if (sub_menu == 0) {
					npcTalk(p, n, " hat I do");
					playerTalk(p, n, "Doesn't all this security in this house");
					playerTalk(p, n, "get in your way?");
					npcTalk(p,
							n,
							"Ah, I'm used to all that",
							"I have my keys, the dogs knows me",
							"And I know by heart the combination to the door lock",
							"It's rather easy, it's his middle name");
					playerTalk(p, n, "Who's middle name?");
					npcTalk(p, n, "Hmm I shouldn't have said that",
							"Forget I said it");

				} else if (sub_menu == 1) {
					npcTalk(p, n, "Trying to muscle in on my job ehh?",
							"I'm happy to do this all myself");
				}
			}
		}
		if (n.getID() == 333) {
			npcTalk(p, n, "Hello there", "My name is Cromperty",
					"I am a wizard and an inventor");
			int menu = showMenu(p, n, "Two jobs, thats got to be tough",
					"So what have you invented?");
			if (menu == 0) {
				npcTalk(p, n, "Not when you combine them it isn't",
						"I invent magic things");
				int sub_menu = showMenu(p, n, "So what have you invented?",
						"Well I shall leave you to your inventing");
				if (sub_menu == 0) {
					inventedDialogue(p, n);
				}
			} else if (menu == 1) {
				inventedDialogue(p, n);
			}
		}
		if (n.getID() == 334) {
			npcTalk(p, n, "Welcome to RPDT");
			if (p.getCache().hasKey("label") && p.getQuestStage(this) == 1) {
				int menu = showMenu(p, n,
						"So when are you going to deliver this crate?",
						"Thank you, it's interesting in here");
				if (menu == 0) {
					npcTalk(p, n, "I suppose I could do it now");
					n.teleport(558, 616);
					GameObject obj = p.getViewArea().getGameObject(
							Point.location(558, 617));
					World.getWorld().unregisterGameObject(obj);
					World.getWorld().delayedSpawnObject(obj.getLoc(), 30000);
					message(p, "The employee picks up the crate");
					n.teleport(559, 612);
					message(p, "And takes it out to be delivered");
					p.getCache().remove("label");
					p.updateQuestStage(this, 2);
				}
			} else {
				playerTalk(p, n, "Thank you very much");
			}
		}
	}

	private void inventedDialogue(Player p, Npc n) {
		npcTalk(p, n,
				"My latest inevention is my patent pending teleport block",
				"Stand on this block here",
				"I do a bit of the old hocus pocus",
				"And abracadabra you end up on the other teleport block");
		int sub_menu1 = showMenu(p, n, "So where is the other block?",
				"Can I be teleported please?", "Well done, that's very clever");
		if (sub_menu1 == 0) {
			npcTalk(p,
					n,
					"I would guess somewhere between here and the wizards tower in Misthalin",
					"All I know is it hasn't got there yet",
					"Or the wizards there would have contacted me",
					"I am using the RPDT to deliver it");
			int menu3 = showMenu(p, n, "Can I be teleported please?",
					"Who are the RPDT?");
			if (menu3 == 0) {
				npcTalk(p, n, "By all means",
						"Though I don't know where you will come out",
						"Wherever the other teleport block is I suppose");
				int tp = showMenu(p, n, "Yes, that sounds good teleport me",
						"That sounds dangerous leave me here");
				if (tp == 0) {
					p.teleport(545, 577, false);
					message(p, "Cromperty takes out a small box",
							"Cromperty presses a switch on the box");
					if (p.getQuestStage(this) == 2) {
						p.teleport(560, 588, false);
					} else {
						p.teleport(558, 617, false);
					}
				}
			} else if (menu3 == 1) {
				npcTalk(p, n, "The runescape parcel delivery team");
			}
		} else if (sub_menu1 == 1) {
			npcTalk(p, n, "By all means",
					"Though I don't know where you will come out",
					"Wherever the other teleport block is I suppose");
			int tp = showMenu(p, n, "Yes, that sounds good teleport me",
					"That sounds dangerous leave me here");
			if (tp == 0) {
				p.teleport(545, 577, false);
				message(p, "Cromperty takes out a small box",
						"Cromperty presses a switch on the box");
				if (p.getQuestStage(this) == 2) {
					p.teleport(560, 588, false);
				} else {
					p.teleport(558, 617, false);
				}
			}
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
			Player player) {
		if (obj.getID() == 290 && obj.getX() == 557 && obj.getY() == 615) {
			return true;
		}
		if (obj.getID() == 290 && obj.getX() == 557 && obj.getY() == 614) {
			return true;
		}
		if (obj.getID() == 329 && obj.getX() == 559 && obj.getY() == 617) {
			return true;
		}
		if (obj.getID() == 328 && obj.getX() == 558 && obj.getY() == 617) {
			return true;
		}
		if (obj.getID() == 331 && obj.getX() == 563 && obj.getY() == 587) {
			return true;
		}
		if (obj.getID() == 333 && obj.getX() == 560 && obj.getY() == 1531) {
			return true;
		}
		if (obj.getID() == 332 && obj.getX() == 560 && obj.getY() == 1531) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == 290 && obj.getX() == 557 && obj.getY() == 615
				|| obj.getID() == 290 && obj.getX() == 557 && obj.getY() == 614) {
			p.message("The crate is empty");
		}
		if (obj.getID() == 329 && obj.getX() == 559 && obj.getY() == 617) {
			message(p, "There is a label on this crate", "It says",
					"to Lord Handelmort", "Handelmort Mansion", "Ardougne");
			if (hasItem(p, 704) || p.getCache().hasKey("label")) {
				message(p, "It doesn't seem possible to open the crate");
			} else {
				message(p, "You take the label");
				addItem(p, 704, 1);
			}
		}
		if (obj.getID() == 328 && obj.getX() == 558 && obj.getY() == 617) {
			if (p.getCache().hasKey("label")) {
				message(p, "There is a label on this crate", "It says",
						"to Lord Handelmort", "Handelmort Mansion", "Ardougne");
				return;
			}
			message(p, "Its ready to be delivered",
					"To the wizard's tower in Misthalin",
					"It doesn't seem possible to open the crate");
		}
		if (obj.getID() == 331 && obj.getX() == 563 && obj.getY() == 587) {
			if (command.equalsIgnoreCase("Search for traps")) {
				message(p, "You find a trap in the stairs",
						"You make a note of the trap's location",
						"Ready for next time you go up the stairs");
				p.getCache().store("trapy", true);
			} else if (command.equalsIgnoreCase("Go up")) {
				if (p.getCache().hasKey("trapy")) {
					p.message("You go up the stairs");
					p.getCache().remove("trapy");
					p.teleport(563, 1534, false);
				} else {
					message(p, "You go down the stairs",
							"You here a click beneath you",
							"You feel yourself falling",
							"You have fallen through a trap");
					p.teleport(563, 3418, false);
					p.damage(7);
				}
			}
		}
		if (obj.getID() == 333 && obj.getX() == 560 && obj.getY() == 1531) {
			p.message("You open the chest");
			World.getWorld().replaceGameObject(obj,
					new GameObject(obj.getLocation(), 332, obj.getDirection(),
							obj.getType()));
		}
		if (obj.getID() == 332 && obj.getX() == 560 && obj.getY() == 1531) {
			p.message("You search the chest");
			if (hasItem(p, 705)) {
				p.message("The chest is empty");
			} else {
				p.message("You find a tribal totem which you take");
				addItem(p, 705, 1);
			}
		}
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item,
			Player player) {
		if (obj.getID() == 328 && item.getID() == 704) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if (obj.getID() == 328 && item.getID() == 704) {
			p.message("You stick the label on the crate");
			playerTalk(p, null, "Now I just need someone to deliver it for me");
			removeItem(p, 704, 1);
			p.getCache().store("label", true);
		}

	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click,
			Player player) {
		if (obj.getID() == 98 && obj.getX() == 561 && obj.getY() == 586) {
			return true;
		}
		return false;
	}

	boolean firstOpt = false;
	boolean secondOpt = false;
	boolean thirdOpt = false;

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 98 && obj.getX() == 561 && obj.getY() == 586
				&& click == 0) {
			p.message("choose a position for dial 1");
			int dial = showMenu(p, "position A", "position B",
					"Position C", "Position D");
			if (dial >= 0 || dial <= 3) {
				p.message("choose a position for dial 2");
				if (dial == 1) {
					firstOpt = true;
				}
				int dial2 = showMenu(p, "position R", "position S",
						"position T", "Position U");
				if (dial2 >= 0 || dial2 <= 3) {
					p.message("choose a position for dial 3");
					if (dial2 == 0) {
						secondOpt = true;
					}
					int dial3 = showMenu(p, "position A", "position B",
							"Position C", "Position D");
					if (dial3 >= 0 || dial3 <= 3) {
						p.message("choose a position for dial 3");
						if (dial3 == 0) {
							thirdOpt = true;
						}
						int dial4 = showMenu(p, "position A",
								"position B", "Position C", "Position D");
						if (dial4 >= 0 || dial4 <= 3) {
							p.message("choose a position for dial 4");
							if (firstOpt && secondOpt && thirdOpt && dial4 == 3) {
								p.message("You here a satisfying click");
								p.message("You go through the door");
								doDoor(obj, p);
							} else {
								p.message("The door fails to open");
							}
						}
					}
				}
			}
		}
	}
}
