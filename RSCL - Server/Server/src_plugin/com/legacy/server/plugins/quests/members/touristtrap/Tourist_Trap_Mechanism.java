package com.legacy.server.plugins.quests.members.touristtrap;

import static com.legacy.server.plugins.Functions.FLETCHING;
import static com.legacy.server.plugins.Functions.SMITHING;
import static com.legacy.server.plugins.Functions.addItem;
import static com.legacy.server.plugins.Functions.getNearestNpc;
import static com.legacy.server.plugins.Functions.hasItem;
import static com.legacy.server.plugins.Functions.message;
import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.playerTalk;
import static com.legacy.server.plugins.Functions.removeItem;
import static com.legacy.server.plugins.Functions.showMenu;
import static com.legacy.server.plugins.Functions.sleep;
import static com.legacy.server.plugins.Functions.spawnNpc;

import com.legacy.server.Constants;
import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.DropListener;
import com.legacy.server.plugins.listeners.action.InvUseOnItemListener;
import com.legacy.server.plugins.listeners.action.InvUseOnNpcListener;
import com.legacy.server.plugins.listeners.action.InvUseOnObjectListener;
import com.legacy.server.plugins.listeners.action.ObjectActionListener;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.action.UnWieldListener;
import com.legacy.server.plugins.listeners.executive.DropExecutiveListener;
import com.legacy.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;
import com.legacy.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.legacy.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.legacy.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.legacy.server.util.rsc.DataConversions;

public class Tourist_Trap_Mechanism implements UnWieldListener, InvUseOnNpcListener, InvUseOnNpcExecutiveListener, ObjectActionListener, ObjectActionExecutiveListener, InvUseOnObjectListener, InvUseOnObjectExecutiveListener, InvUseOnItemListener, InvUseOnItemExecutiveListener, DropListener, DropExecutiveListener, TalkToNpcListener, TalkToNpcExecutiveListener {

	public static int MERCENARY = 668;
	public static int TECHNICAL_PLANS = 1060;
	public static int BEDABIN_NOMAD_GUARD = 703;
	public static int MERCENARY_INSIDE = 670;
	public static int PINE_APPLE = 1058;
	public static int MINING_CAVE = 963;
	public static int MINING_CART = 976;
	public static int MINING_CAVE_BACK = 964;
	public static int TRACK = 974;
	public static int MINING_BARREL = 967;
	public static int LIFT_PLATFORM = 977;
	public static int ANA = 554;
	public static int LIFT_UP = 966;
	public static int MINING_CART_ABOVE = 1025;
	public static int CART_DRIVER = 711;

	class RepeatLift {
		public static final int USETHIS = 0;
		public static final int THING = 1;
	}
	class CartDriver {
		public static final int PSSST = 0;
		public static final int PSSST2 = 1;
		public static final int PSSST3 = 2;
		public static final int PSSST4 = 3;
		public static final int PSSSTFINAL = 4;
		public static final int NICECART = 5;
		public static final int WAGON = 6;
		public static final int HELPYOU = 7;
		public static final int WONDERIF = 8;
		public static final int HECKOUT = 9;
	}

	@Override
	public void onUnWield(Player player, Item item) {
		if((item.getID() == 1022 || item.getID() == 1023) && (player.getLocation().inTouristTrapCave()) && player.getQuestStage(Constants.Quests.TOURIST_TRAP) != -1) {
			Npc n = getNearestNpc(player, MERCENARY, 5);
			if(n != null) {
				n.teleport(player.getX(), player.getY());
				player.teleport(player.getX(), player.getY());
				sleep(650);
				npcTalk(player,n, "Oi! What are you doing down here?",
						"You're no slave!");
				n.startCombat(player);
			} else {
				player.teleport(player.getX(), player.getY());
				Npc newNpc = spawnNpc(MERCENARY, player.getX(), player.getY(), 30000);
				sleep(650);
				npcTalk(player,newNpc, "Oi! What are you doing down here?",
						"You're no slave!");
				newNpc.startCombat(player);
			}
		}
	}

	@Override
	public boolean blockInvUseOnNpc(Player p, Npc npc, Item item) {
		if(item.getID() == TECHNICAL_PLANS && npc.getID() == BEDABIN_NOMAD_GUARD && p.getQuestStage(Constants.Quests.TOURIST_TRAP) >= 7) {
			return true;
		}
		if(item.getID() == PINE_APPLE && npc.getID() == MERCENARY_INSIDE) {
			return true;
		}
		if(item.getID() == 1038 && npc.getID() == ANA) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnNpc(Player p, Npc npc, Item item) {
		if(item.getID() == TECHNICAL_PLANS && npc.getID() == BEDABIN_NOMAD_GUARD && p.getQuestStage(Constants.Quests.TOURIST_TRAP) >= 7) {
			if(npc != null) {
				npcTalk(p, npc, "Ok, you can go in, Al Shabim has told me about you.");
				p.teleport(171, 792);
			} 
		}
		if(item.getID() == PINE_APPLE && npc.getID() == MERCENARY_INSIDE) {
			removeItem(p, PINE_APPLE, 1);
			npcTalk(p,npc, "Great! Just what I've been looking for!",
					"Mmmmmmm, delicious!!",
					"Oh, this is soo nice!",
					"Mmmmm, *SLURP*",
					"Yummmm....Oh yes, this is great.");
			if(p.getQuestStage(Constants.Quests.TOURIST_TRAP) == 8) {
				p.updateQuestStage(Constants.Quests.TOURIST_TRAP, 9);
			}
		}
		if(item.getID() == 1038 && npc.getID() == ANA) {
			if(p.getQuestStage(Constants.Quests.TOURIST_TRAP) == -1) {
				p.message("You have already completed this quest.");
				return;
			}
			if(!hasItem(p, 1039)) {
				npcTalk(p,npc, "Hey, what do you think you're doing?",
						"Harumph!");
				playerTalk(p,npc, "Shush...It's for your own good!");
				message(p, "You manage to squeeze Ana into the barrel,",
						"despite her many complaints.");
				p.getInventory().replace(1038, 1039);
				if(npc != null){
					npc.remove();
				}
			} else {
				p.message("You already have Ana in a barrel, you can't get two in there!");
			}
		}
	}

	private void makeDartTip(Player p, GameObject obj) {
		if(obj.getID() == 1006) {
			if(!hasItem(p, TECHNICAL_PLANS)) {
				message(p, "This anvil is experimental...",
						"You need detailed plans of the item you want to make in order to use it.");
				return;
			}
			message(p,"Do you want to follow the technical plans?");
			int menu = showMenu(p, "Yes. I'd like to try.", "No, not just yet.");
			if(menu == 0) {
				if(p.getSkills().getMaxStat(SMITHING) < 20) {
					p.message("you need level 20 of smithing to make dart tip.");
					return;
				}
				if(!hasItem(p, 168)) {
					p.message("you need a hammer.");
					return;
				}
				message(p, "You begin experimenting in forging the weapon...",
						"You follow the plans carefully.",
						"And after a long time of careful work.");
				removeItem(p, 169, 1);
				if(succeedRate(p)) {
					message(p, "You finally manage to forge a sharp, pointed...",
							"... dart tip...");
					if(!hasItem(p, 1071)) {
						addItem(p, 1071, 1);
					}
					message(p, "You study the technical plans even more...",
							"You need to attach feathers to the tip to complete the weapon.");
				} else {
					message(p, "You waste the bronze bar through an unlucky accident.");
				}
			} else if(menu == 1) {
				p.message("You decide not follow the technical plans.");
			}
		}
	}

	private void attachFeathersToPrototype(Player p, Item i, Item i2) {
		if(i.getID() == 381 && i2.getID() == 1071 || i.getID() == 1071 && i2.getID() == 381) {
			if(!hasItem(p, 381, 10)) {
				p.message("you need 10 feathers to attach the feathers to the dart tip.");
				return;
			}
			if(p.getSkills().getMaxStat(FLETCHING) < 10) {
				p.message("you need at least level 10 of fletching.");
				return;
			}
			message(p, "You try to attach feathers to the bronze dart tip.",
					"Following the plans is tricky, but you persevere.",
					"You succesfully attach the feathers to the dart tip.");
			p.getInventory().replace(1071, 1014);
			removeItem(p, 381, 10);
			p.incExp(FLETCHING, 5.0, true);
		}
	}

	private boolean succeedRate(Player p) {
		int random = DataConversions.getRandom().nextInt(5);
		if(random == 4 || random == 3) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == 1006) {
			return true;
		}
		if(obj.getID() == MINING_CAVE || obj.getID() == MINING_CAVE_BACK) {
			return true;
		}
		if(obj.getID() == MINING_CART || obj.getID() == MINING_BARREL) {
			return true;
		}
		if(obj.getID() == TRACK || obj.getID() == LIFT_PLATFORM) {
			return true;
		}
		if(obj.getID() == LIFT_UP || obj.getID() == MINING_CART_ABOVE) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == 1006) {
			makeDartTip(p, obj);
		}
		if(obj.getID() == MINING_CAVE_BACK) {
			if(hasItem(p, 1039)) {
				failAnaInBarrel(p, null);
				return;
			}
			message(p, "You walk into the dark of the cavern...");
			p.message("And emerge in a different part of this huge underground complex.");
			p.teleport(84, 3640);
		}
		if(obj.getID() == MINING_CAVE) {
			Npc n = getNearestNpc(p, MERCENARY_INSIDE, 10);
			if(!p.getInventory().wielding(1022) && !p.getInventory().wielding(1023) && p.getQuestStage(Constants.Quests.TOURIST_TRAP) != -1) {
				p.message("This guard looks as if he's been down here a while.");
				npcTalk(p,n, "Hey, you're no slave!");
				npcTalk(p,n, "What are you doing down here?");
				n.setChasing(p);
				message(p, "More guards rush to catch you.",
						"You are roughed up a bit by the guards as you're manhandlded to a cell.");
				npcTalk(p,n, "Into the cell you go! I hope this teaches you a lesson.");
				p.teleport(89, 801);
				return;
			}
			if(p.getQuestStage(Constants.Quests.TOURIST_TRAP) >= 9 || p.getQuestStage(Constants.Quests.TOURIST_TRAP) == -1) {
				message(p, "You walk into the dark of the cavern...");
				p.message("And emerge in a different part of this huge underground complex.");
				p.teleport(76, 3640);
				return;
			}
			p.message("Two guards block your way further into the caves");
			if(n != null) {
				npcTalk(p,n, "Hey you, move away from there!");
			}
		}
		if(obj.getID() == MINING_CART) {
			if(command.equals("look")) {
				if(obj.getX() == 62 && obj.getY() == 3639) {
					p.message("This cart is being unloaded into this section of the mine.");
					p.message("Before being sent back for another load.");
				} else {
					p.message("This mine cart is being loaded up with new rocks and stone.");
					p.message("It gets sent to a different section of the mine for unloading.");
				}
			} else if(command.equals("search")) {
				p.message("You search the mine cart.");
				if(hasItem(p, 1039)) {
					p.message("There isn't enough space for both you and Ana in the cart.");
					return;
				}
				p.message("There may be just enough space to squeeze yourself into the cart.");
				p.message("Would you like to try?");
				int menu = showMenu(p, "Yes, of course.", "No thanks, it looks pretty dangerous.");
				if(menu == 0) {
					if(succeedRate(p)) {
						p.message("You succeed!");
						if(obj.getX() == 56 && obj.getY() == 3631) {
							p.teleport(62, 3640);
						} else if(obj.getX() == 62 && obj.getY() == 3639) {
							p.teleport(55, 3632);
						}
					} else {
						p.message("You fail to fit yourself into the cart in time before it starts it's journey.");
						p.message( "You fall and hurt yourself.");
						p.damage(2);
					}
				} else if(menu == 1) {
					p.message("You decide not to get into the dangerous looking mine cart.");
				}
			}
		}
		if(obj.getID() == TRACK) {
			p.message("You see that this track is too dangerous to cross.");
			p.message("High speed carts are crossing the track most of the time.");
		}
		if(obj.getID() == MINING_BARREL) {
			if(p.getCache().hasKey("ana_is_up")) {
				if(hasItem(p, 1038)) {
					p.message("You can only manage one of these at a time.");
					return;
				}
				message(p, "You find the barrel with ana in it.",
						"@gre@Ana: Let me out of here, I feel sick!");
				addItem(p, 1039, 1);
				p.getCache().remove("ana_is_up");
				return;
			}
			if(p.getCache().hasKey("ana_cart")) {
				if(hasItem(p, 1038)) {
					p.message("You can only manage one of these at a time.");
					return;
				}
				p.message("You search the barrels and find the one with Ana in it.");
				p.message("@gre@Ana: Let me out!");
				addItem(p,  1039, 1);
				p.getCache().remove("ana_cart");
				return;
			}
			if(hasItem(p, 1039)) {
				p.message("You cannot carry another barrel while you're carrying Ana.");
				return;
			}
			if(p.getCache().hasKey("ana_lift")) {
				p.message("You search for Ana, but cannot find her.");
			}
			p.message("This barrel is quite big, but you may be able to carry one. ");
			p.message("Would you like to take one?");
			int menu = showMenu(p, "Yeah, cool!", "No thanks.");
			if(menu == 0) {
				if(hasItem(p, 1038)) {
					p.message("You can only manage one of these at a time.");
				} else {
					p.message("You take the barrel, it's not that heavy, just awkward.");
					addItem(p, 1038, 1);
				}
			} else if(menu == 1) {
				p.message("You decide not to take the barrel.");
			}
		}
		if(obj.getID() == LIFT_PLATFORM) {
			Npc n = getNearestNpc(p, 690, 5);
			if(n != null) {
				if(hasItem(p, 1039)) {
					anaToLift(p, n);
					return;
				}
				npcTalk(p,n, "Hey there, what do you want?");
				int menu = showMenu(p,n,
						"What is this thing?",
						"Can I use this?");
				if(menu == 0) {
					repeatLiftDialogue(p, n, RepeatLift.THING);
				} else if(menu == 1) {
					repeatLiftDialogue(p, n, RepeatLift.USETHIS);
				}
			}
		}
		if(obj.getID() == LIFT_UP) {
			p.message("You pull on the winch");
			if(p.getCache().hasKey("ana_lift")) {
				message(p, "You see a barrel coming to the surface.",
						"Before too long you haul it onto the side.",
						"The barrel seems quite heavy and you hear a muffled sound coming from inside.");
				p.message("@gre@Ana: Get me OUT OF HERE!");
				p.getCache().remove("ana_lift");
				if(!p.getCache().hasKey("ana_is_up")) {
					p.getCache().store("ana_is_up", true);
				}
			} else {
				p.message("You pull on the winch and a heavy barrel filled with stone comes to the surface.");
			}
		}
		if(obj.getID() == MINING_CART_ABOVE) {
			p.message("You search the mine cart.");
			if(hasItem(p, 1039)) {
				message(p, "There should be enough space for Ana (in the barrel) to go on here.");
			}
			if(p.getCache().hasKey("ana_in_cart")) {
				message(p, "You can see the barrel with Ana in it on the cart already.");
			}
			message(p, "There is space on the cart for you get on, would you like to try?");
			int menu = showMenu(p,
					"Yes, I'll get on.",
					"No, I've got other plans.",
					"Attract mine cart drivers attention.");
			if(menu == 0) {
				p.message("You decide to climb onto the cart.");
				if(p.getCache().hasKey("ana_in_cart")) {
					message(p, "You hear Ana starting to bang on the barrel for her to be let out.");
					message(p, "@gre@Ana: Get me out of here, I'm suffocating!",
							"@gre@Ana: It smells like dwarven underwear in here!");
				}
				p.teleport(86, 808);
				if(p.getCache().hasKey("rescue")) {
					message(p, "As soon as you get on the cart, it starts to move.",
							"Before too long you are past the gates.",
							"You jump off the cart taking Ana with you.");
					p.teleport(106, 806);
					p.getCache().remove("rescue");
					addItem(p, 1039, 1);
				}
			} else if(menu == 1) {
				p.message("You decide not to get onto the cart.");
			} else if(menu == 2) {
				Npc cartDriver = getNearestNpc(p, CART_DRIVER, 10);
				if(cartDriver != null) {
					npcTalk(p,cartDriver, "Ahem.");
					if(p.getCache().hasKey("rescue")) {
						npcTalk(p,cartDriver, "Hurry up, get in the cart or I'll go without you!");
						return;
					}
					if(p.getCache().hasKey("ana_in_cart")) {
						getOutWithAnaInCart(p, cartDriver, -1);
						return;
					}
					if(hasItem(p, 1039)) {
						npcTalk(p,cartDriver, "What're you doing carrying that big barrel around?",
								"Put it in the back of the cart like all the others!");
						return;
					}
					p.message("The cart driver is busy loading the cart up ...");
				}
			}
		}
	}
	private void getOutWithAnaInCart(Player p, Npc n, int cID) {
		if(cID == -1) {
			message(p, "The cart driver seems to be festidiously cleaning his cart.",
					"It doesn't look as if he wants to be disturbed.");
			int menu = showMenu(p,n,
					"Hello.",
					"Nice cart.",
					"Pssst...");
			if(menu == 0) {
				npcTalk(p,n, "Can't you see I'm busy?",
						"Now get out of here!");
				int getGo = showMenu(p,n,
						"Oh, ok, sorry.",
						"Nice cart.",
						"Pssst...");
				if(getGo == 0) {
					npcTalk(p,n, "Look just leave me alone!");
					p.message("The cart driver goes back to his work.");
				} else if(getGo == 1) {
					getOutWithAnaInCart(p, n, CartDriver.NICECART);
				} else if(getGo == 2) {
					getOutWithAnaInCart(p, n, CartDriver.PSSST);
				}
			} else if(menu == 1) {
				getOutWithAnaInCart(p, n, CartDriver.NICECART);
			} else if(menu == 2) {
				getOutWithAnaInCart(p, n, CartDriver.PSSST);
			}
		} switch(cID) {
		case CartDriver.PSSST:
			message(p, "The cart driver completely ignores you.");
			int pst = showMenu(p,n,
					"Psssst...",
					"Psssssst...",
					"Pssssssssttt!!!");
			if(pst == 0) {
				getOutWithAnaInCart(p, n, CartDriver.PSSST2);
			} else if(pst == 1) {
				getOutWithAnaInCart(p, n, CartDriver.PSSST3);
			} else if(pst == 2) {
				getOutWithAnaInCart(p, n, CartDriver.PSSSTFINAL);
			}
			break;
		case CartDriver.PSSST2:
			message(p, "The driver completely ignores you.");
			int m = showMenu(p,n,
					"Psssssst...",
					"Pssst...",
					"Pssssssssttt!!!");
			if(m == 0) {
				getOutWithAnaInCart(p, n, CartDriver.PSSST3);
			} else if(m == 1) {
				getOutWithAnaInCart(p, n, CartDriver.PSSST4);
			} else if(m == 2) {
				getOutWithAnaInCart(p, n, CartDriver.PSSSTFINAL);
			}
			break;
		case CartDriver.PSSST3:
			message(p, "The driver completely ignores you.");
			int me = showMenu(p,n,
					"Psssst...",
					"Pssst...",
					"Pssssssssttt!!!");
			if(me == 0) {
				getOutWithAnaInCart(p, n, CartDriver.PSSST2);
			} else if(me == 1) {
				getOutWithAnaInCart(p, n, CartDriver.PSSST4);
			} else if(me == 2) {
				getOutWithAnaInCart(p, n, CartDriver.PSSSTFINAL);
			}

			break;
		case CartDriver.PSSST4:
			message(p, "The cart driver completely ignores you.");
			int meh = showMenu(p,n,
					"Psssst...",
					"Psssssst...",
					"Pssssssssttt!!!");
			if(meh == 0) {
				getOutWithAnaInCart(p, n, CartDriver.PSSST2);
			} else if(meh == 1) {
				getOutWithAnaInCart(p, n, CartDriver.PSSST3);
			} else if(meh == 2) {
				getOutWithAnaInCart(p, n, CartDriver.PSSSTFINAL);
			}
			break;
		case CartDriver.PSSSTFINAL:
			message(p, "The cart driver turns around quickly to face you.");
			npcTalk(p,n, "What!",
					"Can't you see I'm busy?");
			int shh = showMenu(p,n,
					"Oh, ok, sorry.",
					"Shhshhh!");
			if(shh == 0) {
				npcTalk(p,n, "Look just leave me alone!");
				p.message("The cart driver goes back to his work.");
			} else if(shh == 1) {
				npcTalk(p,n, "Shush yourself!");
				p.message("The cart driver goes back to his work.");
			}
			break;
		case CartDriver.NICECART:
			message(p, "The cart driver looks around at you and tries to weigh you up.");
			npcTalk(p,n, "Hmmm.");
			message(p, "He tuts to himself and starts checking the wheels.");
			npcTalk(p,n, "Tut !");
			int tut = showMenu(p,n,
					"I wonder if you could help me?",
					"One wagon wheel says to the other,'I'll see you around'.",
					"Can I help you at all?");
			if(tut == 0) {
				getOutWithAnaInCart(p, n, CartDriver.WONDERIF);
			} else if(tut == 1) {
				getOutWithAnaInCart(p, n, CartDriver.WAGON);
			} else if(tut == 2) {
				getOutWithAnaInCart(p, n, CartDriver.HELPYOU);
			}
			break;
		case CartDriver.WAGON:
			message(p, "The cart driver smirks a little.",
					"He starts checking the steering on the cart.");
			int go = showMenu(p,n,
					"'One good turn deserves another'",
					"Can you get me the heck out of here please?");
			if(go == 0) {
				message(p, "The cart driver smiles a bit and then turns to you.");
				npcTalk(p,n, "Are you trying to get me fired?");
				int fired = showMenu(p,n,
						"No",
						"Yes",
						"Fired...no, shot perhaps!");
				if(fired == 0) {
					npcTalk(p,n, "It certainly sounds like it, now leave me alone.",
							"If you bug me again, I'm gonna call the guards.");
					p.message("The cart driver goes back to his work.");
				} else if(fired == 1) {
					npcTalk(p,n, "And why would you want to do a crazy thing like that for?",
							"I ought to teach you a lesson!",
							"Guards! Guards!");
					// CONTINUE
				} else if(fired == 2) {
					npcTalk(p,n, "Ha ha ha! You're funny!");
					message(p, "The cart driver checks that the guards aren't watching him.");
					npcTalk(p,n, "What're you in fer?");
					int fer = showMenu(p,n,
							"Oh, I'm not supposed to be here at all actually.",
							"I'm in for murder, so you'd better get me out of here!",
							"In for a penny in for a pound.");
					if(fer == 0) {
						npcTalk(p,n, "Hmmm, interesting...let me guess.",
								"You're completely innocent...",
								"like all the other inmates in here.",
								"Ha ha ha!");
						p.message("The Cart driver goes back to his work.");
					} else if(fer == 1) {
						npcTalk(p,n, "Hmm, well, I wonder what the guards are gonna say about that!",
								"Guards! Guards!");
						// CONTINUE
					} else if(fer == 2) {
						message(p, "The cart driver laughs at your pun...");
						npcTalk(p,n, "Ha ha ha, oh Stoppit!");
						message(p, "The cart driver seems much happier now.");
						npcTalk(p,n, "What can I do for you anyway?");
						int fuckoff = showMenu(p,n,
								"Can you smuggle me out on your cart?",
								"Can you smuggle my friend Ana out on your cart?",
								"Well, you see, it's like this...");
						if(fuckoff == 0) {
							message(p, "The cart driver points at a nearby guard.");
							npcTalk(p,n, "Ask that man over there if it's OK and I'll consider it!",
									"Ha ha ha!");
							p.message("The cart driver goes back to his work, laughing to himself.");
						} else if(fuckoff == 1) {
							npcTalk(p,n, "As long as your friend is a barrel full of rocks.",
									"I don't think it would be a problem at all!",
									"Ha ha ha!");
							p.message("The cart driver goes back to his work, laughing to himself.");
						} else if(fuckoff == 2) {
							npcTalk(p,n, "yeah!");
							int likeThis = showMenu(p,n,
									"Prison riot in ten minutes, get your cart out of here!",
									"There's ten gold in it for you if you leave now - no questions asked.");
							if(likeThis == 0) {
								p.message("The cart driver seems visibly shaken...");
								npcTalk(p,n, "Oh, right..yes...yess, Ok...");
								message(p, "The cart driver quickly starts preparing the cart.");
								int lala = showMenu(p,n,
										"Good luck!",
										"You can't leave me here, I'll get killed!");
								if(lala == 0) {
									npcTalk(p, n, "Ok great!");
									if(p.getCache().hasKey("ana_in_cart")) {
										p.getCache().remove("ana_in_cart");
										p.getCache().store("rescue", true);
									}
								} else if(lala == 1) {
									npcTalk(p,n, "Oh, right...ok, you'd better jump in the cart then!",
											"Quickly!");
									if(p.getCache().hasKey("ana_in_cart")) {
										p.getCache().remove("ana_in_cart");
										p.getCache().store("rescue", true);
									}
								}
							} else if(likeThis == 1){
								npcTalk(p,n, "If you're going to bribe me, at least make it worth my while.",
										"Now, let's say 100 Gold pieces should we?",
										"Ha ha ha!");
								int ten = showMenu(p,n,
										"A hundred it is!",
										"Forget it!");
								if(ten == 0) {
									if(hasItem(p, 10, 100)) {
										npcTalk(p, n, "Great!", "Ok, get in the back of the cart then!");
										removeItem(p, 10, 100);
										if(p.getCache().hasKey("ana_in_cart")) {
											p.getCache().remove("ana_in_cart");
											p.getCache().store("rescue", true);
										}
									} else {
										playerTalk(p, n, "I'll go get the money! wait for me.");
										npcTalk(p, n, "OK great!");
									}
								} else if(ten == 1) {
									npcTalk(p,n, "Ok, fair enough!",
											"But don't bother me anymore.");
									p.message("The cart driver goes back to work.");
								}
							}
						}
					}
				}
			} else if(go == 1) {
				getOutWithAnaInCart(p, n, CartDriver.HECKOUT);
			}
			break;
		case CartDriver.HELPYOU:
			npcTalk(p,n, "I'm quite capable thanks...",
					"Now get lost before I call the guards.");
			int help = showMenu(p,n,
					"Can you get me the heck out of here please?",
					"I could help, I know a lot about carts.");
			if(help == 0) {
				getOutWithAnaInCart(p, n, CartDriver.HECKOUT);
			} else if(help == 1) {
				npcTalk(p,n, "Are you saying I don't know anything about carts?",
						"Why you cheeky little....");
				message(p, "The cart driver seems mortally offended...",
						"his temper explodes as he shouts the guards.");
				npcTalk(p,n, "Guards! Guards!");
				// CONTINUE
			}
			break;
		case CartDriver.WONDERIF:
			npcTalk(p,n, "Sorry friend, I'm busy, go bug the guards,",
					"I'm sure they'll give ya the time of day.");
			message(p, "The cart driver chuckles to himself.");
			int ok = showMenu(p,n,
					"Can I help you at all?",
					"Can you get me the heck out of here please?");
			if(ok == 0) {
				getOutWithAnaInCart(p, n, CartDriver.HELPYOU);
			} else if(ok == 1) {
				getOutWithAnaInCart(p, n, CartDriver.HECKOUT);
			}
			break;
		case CartDriver.HECKOUT:
			npcTalk(p,n, "No way, and if you bug me again, I'm gonna call the guards.");
			message(p, "The cart driver goes back to his work.");
			break;
		}

	}
	private void repeatLiftDialogue(Player p, Npc n, int cID) {
		switch(cID) {
		case RepeatLift.THING:
			npcTalk(p,n, "It is quite clearly a lift.",
					"Any fool can see that it's used to transport rock to the surface.");
			int opt = showMenu(p,n,
					"Can I use this?",
					"Ok, thanks.");
			if(opt == 0) {
				repeatLiftDialogue(p, n, RepeatLift.USETHIS);
			}
			break;
		case RepeatLift.USETHIS:
			npcTalk(p,n, "Of course not, you'd be doing me out of a job.",
					"Anyway, you haven't got any barrels that need to go to the surface.",
					"Now, move along and get some work done before you get a good beating.");
			int options = showMenu(p,n,
					"What is this thing?,",
					"Ok, thanks.");
			if(options == 0) {
				repeatLiftDialogue(p, n, RepeatLift.THING);
			}
			break;
		}
	}
	private void failAnaInBarrel(Player p, Npc n) {
		if(hasItem(p, 1039)) {
			n = spawnNpc(MERCENARY, p.getX(), p.getY(), 60000);
			sleep(650);
			npcTalk(p,n, "Hey, where d'ya think you're going with that barrel?",
					"You should know that they go out on the cart!",
					"We'd better check this out!");
			p.message("The guards prize the lid off the barrel.");
			removeItem(p, 1039, 1);
			npcTalk(p,n, "Blimey! It's a jail break!",
					"They're making a break for it!");
			Npc ana = spawnNpc(ANA, p.getX(), p.getY(), 30000);
			sleep(650);
			npcTalk(p,ana, "I could have told you we wouldn't get away with it!",
					"Now look at the mess you've caused!");
			p.message("The guards grab Ana and drag her away.");
			if(ana != null) {
				ana.remove();
			}
			npcTalk(p,n, "Hey, watch it with the hands buster.",
					"These are the upper market slaves clothes doncha know!",
					"Right, we'd better teach you a lesson as well!");
			message(p, "The guards rough you up a bit.");
			npcTalk(p,n, "Right lads, stuff him in the mining cell!",
					"Specially for our most honoured guests.");
			p.message("The guards drag you away to a cell.");
			npcTalk(p,n, "There you go, we hope you 'dig' you're stay here.",
					"Har! Har! Har!");
			if(n != null) {
				n.remove();
			}
			p.teleport(75, 3626);
		}
	}
	private void anaToLift(Player p, Npc n) {
		p.message("The guard notices the barrel (with Ana in it) that you're carrying.");
		npcTalk(p,n, "Hey, that Barrel looks heavy, do you need a hand?");
		int menu = showMenu(p, n, "Yes please.", "No thanks, I can manage.");
		if(menu == 0) {
			p.message("The guard comes over and helps you. He takes one end of the barrel.");
			npcTalk(p,n, "Blimey! This is heavy!");
			message(p, "@gre@Ana in a barrel: Why you cheeky....!",
					"The guard looks around suprised at Ana's outburst.");
			npcTalk(p,n, "What was that?");
			playerTalk(p,n, "Oh, it was nothing.");
			npcTalk(p,n, "I could have sworn I heard something!");
			p.message("@gre@Ana in a barrel: Yes you did you ignaramus.");
			npcTalk(p,n, "What was that you said?");
			int opt = showMenu(p,n,
					"I said you were very gregarious!",
					"Oh, nothing.");
			if(opt == 0) {
				message(p, "@gre@Ana in a barrel: You creep!");
				npcTalk(p,n, "Oh, right, how very nice of you to say so.");
				p.message("The guard seems flattered.");
				npcTalk(p,n, "Anyway, let's get this barrel up to the surface, plenty more work to you to do!");
				p.message("The guard places the barrel carefully on the lift platform.");
				npcTalk(p,n, "Oh, there's no one operating the lift up top, hope this barrel isn't urgent?",
						"You'd better get back to work!");
				removeItem(p, 1039, 1);
				if(!p.getCache().hasKey("ana_lift")) {
					p.getCache().store("ana_lift", true);
				}
				// use cache again maybe?
			} else if(opt == 1) {
				npcTalk(p,n, "I heard you say something, now spit it out!");
			}
		} else if(menu == 1) {
			npcTalk(p,n, "Ok, fair enough, I was only offering.");
		}
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
		if(obj.getID() == 1006 && item.getID() == 169) {
			return true;
		}
		if(obj.getID() == MINING_CART && item.getID() == 1039) {
			return true;
		}
		if(obj.getID() == LIFT_PLATFORM && item.getID() == 1039) {
			return true;
		}
		if(obj.getID() == MINING_CART_ABOVE && item.getID() == 1039) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if(obj.getID() == 1006 && item.getID() == 169) {
			if(hasItem(p, 1071)) {
				p.message("You have already made the prototype dart tip.");
				p.message("You don't need to make another one.");
			} else {
				makeDartTip(p, obj);
			}
		}
		if(obj.getID() == MINING_CART && item.getID() == 1039) {
			message(p, "You carefully place Ana in the barrel into the mine cart.",
					"Soon the cart moves out of sight and then it returns.");
			removeItem(p, 1039, 1);
			if(!p.getCache().hasKey("ana_cart")) {
				p.getCache().store("ana_cart", true);
			}
		}
		if(obj.getID() == LIFT_PLATFORM && item.getID() == 1039) {
			Npc n = getNearestNpc(p, 690, 5);
			if(n != null) {
				anaToLift(p, n);
			}
		}
		if(obj.getID() == MINING_CART_ABOVE && item.getID() == 1039) {
			message(p, "You place Ana (In the barrel) carefully on the cart.",
					"This was the last barrel to go on the cart,",
					"but the cart driver doesn't seem to be in any rush to get going.",
					"And the desert heat will soon get to Ana.");
			removeItem(p, 1039, 1);
			if(!p.getCache().hasKey("ana_in_cart")) {
				p.getCache().store("ana_in_cart", true);
			}
		}
	}

	@Override
	public boolean blockInvUseOnItem(Player p, Item item1, Item item2) {
		if(item1.getID() == 381 && item2.getID() == 1071 || item1.getID() == 1071 && item2.getID() == 381) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnItem(Player p, Item item1, Item item2) {
		if(item1.getID() == 381 && item2.getID() == 1071 || item1.getID() == 1071 && item2.getID() == 381) {
			attachFeathersToPrototype(p, item1, item2);
		}
	}

	@Override
	public boolean blockDrop(Player p, Item i) {
		if(i.getID() == 1039) {
			return true;
		}
		return false;
	}

	@Override
	public void onDrop(Player p, Item i) {
		if(i.getID() == 1039) {
			if(p.getQuestStage(Constants.Quests.TOURIST_TRAP) == -1) {
				removeItem(p, 1039, 1);
				return;
			}
			addItem(p, 1039, 1); // HIJACK
			p.message("Are you sure you want to drop this?");
			int menu = showMenu(p,
					"Yes, I'm sure.",
					"Erm, no I've had second thoughts.");
			if(menu == 0) {
				message(p, "You drop the barrel to the floor and Ana gets out.");
				removeItem(p, 1039, 1);
				Npc Ana = spawnNpc(ANA, p.getX(), p.getY(), 20000);
				sleep(650);
				npcTalk(p,Ana, "How dare you put me in that barrel you barbarian!");
				message(p, "Ana's outburst attracts the guards, they come running over.");
				Npc guard = spawnNpc(MERCENARY, p.getX(), p.getY(), 30000);
				sleep(650);
				npcTalk(p,guard, "Hey! What's going on here then?");
				guard.startCombat(p);
				message(p, "The guards drag Ana away and then throw you into a cell.");
				p.teleport(75, 3626);
			} else if(menu == 1) {
				message(p, "You think twice about dropping the barrel to the floor.");
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if(n.getID() == CART_DRIVER) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(n.getID() == CART_DRIVER) {
			if(n.getID() == CART_DRIVER) {
				if(p.getQuestStage(Constants.Quests.TOURIST_TRAP) == -1) {
					npcTalk(p,n, "Don't trouble me, can't you see I'm busy?");
					return;
				}
				if(p.getCache().hasKey("rescue")) {
					npcTalk(p,n, "Hurry up, get in the cart or I'll go without you!");
					return;
				}
				if(p.getCache().hasKey("ana_in_cart")) {
					getOutWithAnaInCart(p, n, -1);
					return;
				}
				if(hasItem(p, 1039)) {
					npcTalk(p,n, "What're you doing carrying that big barrel around?",
							"Put it in the back of the cart like all the others!");
					return;
				}
				p.message("The cart driver is busy loading the cart up ...");
			}
		}

	}
}
