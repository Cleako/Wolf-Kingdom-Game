package com.legacy.server.plugins.npcs.wilderness.mage_arena;

import static com.legacy.server.plugins.Functions.*;

import com.legacy.server.Server;
import com.legacy.server.event.DelayedEvent;
import com.legacy.server.event.rsc.impl.ObjectRemover;
import com.legacy.server.model.Skills;
import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.GroundItem;
import com.legacy.server.model.entity.Mob;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.world.World;
import com.legacy.server.net.rsc.ActionSender;
import com.legacy.server.plugins.listeners.action.DropListener;
import com.legacy.server.plugins.listeners.action.ObjectActionListener;
import com.legacy.server.plugins.listeners.action.PickupListener;
import com.legacy.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.DropExecutiveListener;
import com.legacy.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.legacy.server.plugins.listeners.executive.PickupExecutiveListener;
import com.legacy.server.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.legacy.server.plugins.listeners.executive.PlayerDeathExecutiveListener;
import com.legacy.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.legacy.server.plugins.listeners.executive.PlayerMageNpcExecutiveListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class Mage_Arena_MiniQuest implements TalkToNpcExecutiveListener, TalkToNpcListener, PlayerKilledNpcListener,
PlayerKilledNpcExecutiveListener, PlayerAttackNpcExecutiveListener, PlayerDeathExecutiveListener,
PlayerMageNpcExecutiveListener, ObjectActionListener, ObjectActionExecutiveListener, PickupListener,
PickupExecutiveListener, DropListener, DropExecutiveListener {

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		if (p.getSkills().getMaxStat(MAGIC) < 60) { // TODO: Enter the arena game.
			playerTalk(p, n, "hello there", "what is this place?");
			npcTalk(p, n, "do not waste my time with trivial questions!",
					"i am the great kolodion, master of battle magic", "i have an arena to run");
			playerTalk(p, n, "can i enter?");
			npcTalk(p, n, "hah, a wizard of your level..don't be absurd");
		} else if (p.getCache().hasKey("mage_arena")) {
			int stage = p.getCache().getInt("mage_arena");
			/* Started but failed. */
			if (stage == 1) {
				playerTalk(p, n, "hi");
				npcTalk(p, n, "you return young conjurer", "..you obviously have a taste for the darkside of magic",
						"let us continue with the battle...now");
				if (cantGo(p)) {
					npcTalk(p, n, "you may not take armour or weapons into the arena");
					message(p, "You cannot enter the arena...", "...while carrying armor or weapons");
					return;
				}
				movePlayer(p, 229, 130);
				setCurrentLevel(p, 0, 0);
				setCurrentLevel(p, 2, 0);
				spawnKolodion(p, p.getCache().getInt("kolodion_stage"));
				startKolodionEvent(p);

			} else if (stage >= 2) {
				playerTalk(p, n, "hello kolodion");
				npcTalk(p, n, "hey there, how are you?, enjoying the bloodshed?");
				playerTalk(p, n, "it's not bad, i've seen worse");
				int menu = showMenu(p, n,
						"i think i've had enough for now",
						"how can i use my new spells outside of the arena?");
				if(menu == 0) {
					npcTalk(p, n, "shame , you're a good battle mage",
							"hope to see you soon");
				} else if(menu == 1) {
					npcTalk(p, n, "experience my friend, experience",
							"once you've used the spell enough times in the arena...",
							"...you'll be able to use them in the rest of runescape");
					playerTalk(p, n, "good stuff");
					npcTalk(p, n, "not so good for the citizens, they won't stand a chance");
					playerTalk(p, n, "how am i doing so far?");
					if(p.getCache().hasKey("Saradomin strike_casts") && p.getCache().getInt("Saradomin strike_casts") >= 100) {
						npcTalk(p, n, "you're fully trained to use the strike spell anywhere");
					} else {
						npcTalk(p, n, "you still need to train with the strike spell...",
								"...inside the arena before you can use it outside");
					}
					if(p.getCache().hasKey("Claws of Guthix_casts") && p.getCache().getInt("Claws of Guthix_casts") >= 100) {
						npcTalk(p, n, "you're fully trained to use the claw spell anywhere");
					} else {
						npcTalk(p, n, "you still need to train with the claw spell...",
								"...inside the arena before you can use it outside");
					}
					if(p.getCache().hasKey("Flames of Zamorak_casts") && p.getCache().getInt("Flames of Zamorak_casts") >= 100) {
						npcTalk(p, n, "you're fully trained to use the flame spell anywhere");
					} else {
						npcTalk(p, n, "you still need to train with the flame spell...",
								"...inside the arena before you can use it outside");
					}
				}
			}
		} else {
			playerTalk(p, n, "hello there",
					"what is this place?");
			npcTalk(p, n, "i am the great kolodion, master of battle magic ...",
					"... and this is my battle arena",
					"top wizards travel from all over to fight here");
			int choice = showMenu(p, n, "can i fight here?", "what's the point of that?", "that's barbaric");
			if (choice == 0) {
				fight(p, n);
			} else if (choice == 1) {
				whatsthepoint(p, n);
			} else if (choice == 2) {
				barbaric(p, n);
			}
		}
	}

	public void whatsthepoint(Player p, Npc n) {
		npcTalk(p, n, "we learn how to use magic to it fullest...",
				".., how to channel forces of the cosmos into our world",
				".., but mainly I just like blasting people into dust");
		int choice = showMenu(p, n, "can i fight here?", "that's barbaric");
		if (choice == 0) {
			fight(p, n);
		} else if (choice == 1) {
			barbaric(p, n);
		}
	}

	public void barbaric(Player p, Npc n) {
		npcTalk(p, n, "nope, it's just magic, but I know what you mean", "so do you want to join us");
		int choice = showMenu(p, n, "yes indeedy", "no i don't");
		if (choice == 0) {
			fight(p, n);
		} else if (choice == 1) {
			npcTalk(p, n, "your loss");
		}
	}

	public void fight(Player p, Npc n) {
		npcTalk(p, n, "good..good, you have a healthy sense of competition",
				"remember traveller in my arena hand to hand combat is useless",
				"your strength will diminish as you enter the arena", "before i can accept you in, we must duel",
				"you may not take armour or weapons into the arena");
		int choice = showMenu(p, n, "ok let's fight", "no thanks");
		if (choice == 0) {
			if (cantGo(p)) {
				npcTalk(p, n, "you may not take armour or weapons into the arena");
				message(p, "You cannot enter the arena...", "...while carrying armor or weapons");
				return;
			}
			npcTalk(p, n, "now!");
			if (!p.getCache().hasKey("mage_arena")) {
				p.getCache().set("mage_arena", 1);
			}
			movePlayer(p, 229, 130);
			setCurrentLevel(p, 0, 0);
			setCurrentLevel(p, 2, 0);

			startKolodionEvent(p);
			spawnKolodion(p, 713);
			sleep(650);

		}
	}

	public void learnSpellEvent(Player p) {
		DelayedEvent mageArena = p.getAttribute("mageArenaEvent", null);
		DelayedEvent mageArenaEvent = new DelayedEvent(p, 1900) {
			@Override
			public void run() {
				/* Player logged out. */
				if (!owner.isLoggedIn() || owner.isRemoved()) {
					stop();
					return;
				}
				if(!owner.getLocation().inMageArena()) {
					stop();
					return;
				}
				if(owner.inCombat()) {
					return;
				}
				if (owner.getSkills().getLevel(Skills.ATTACK) > 0 || owner.getSkills().getLevel(Skills.STRENGTH) > 0) {
					owner.getSkills().setLevel(Skills.ATTACK, 0);
					owner.getSkills().setLevel(Skills.STRENGTH, 0);
				}
				Npc Guthix = getNearestNpc(p, 789, 2);
				Npc Zamorak =  getNearestNpc(p, 790, 2);
				Npc Saradomin =  getNearestNpc(p, 791, 2);
				String[] randomMessage = { "@yel@zamorak mage: feel the wrath of zamarok", "@yel@saradomin mage: feel the wrath of Saradomin", "@yel@guthix mage: feel the wrath of guthix" };
				if (Guthix != null && Guthix.withinRange(owner, 1)) {
					godSpellObject(owner, 33);
					p.message(randomMessage[2]);
					if(getCurrentLevel(owner, HITS) < 20) {
						owner.damage(2);
					} else {
						owner.damage(getCurrentLevel(owner, HITS) / 10);
					}
				} else if (Zamorak != null && Zamorak.withinRange(owner, 1)) {
					godSpellObject(owner, 35);
					p.message(randomMessage[0]);
					if(getCurrentLevel(owner, HITS) < 20) {
						owner.damage(2);
					} else {
						owner.damage(getCurrentLevel(owner, HITS) / 10);
					}
				} else if (Saradomin != null && Saradomin.withinRange(owner, 1)) {
					godSpellObject(owner, 34);
					p.message(randomMessage[1]);
					if(getCurrentLevel(owner, HITS) < 20) {
						owner.damage(2);
					} else {
						owner.damage(getCurrentLevel(owner, HITS) / 10);
					}
				}
			}
		};
		if (mageArena != null) {
			if (mageArena.shouldRemove()) {
				p.setAttribute("mageArenaEvent", mageArenaEvent);
				Server.getServer().getEventHandler().add(mageArenaEvent);
			}
		} else {
			p.setAttribute("mageArenaEvent", mageArenaEvent);
			Server.getServer().getEventHandler().add(mageArenaEvent);
		}
	}

	private void startKolodionEvent(Player p) {
		DelayedEvent kolE = p.getAttribute("kolodionEvent", null);
		DelayedEvent kolodionEvent = new DelayedEvent(p, 650) {
			@Override
			public void run() {
				Npc npc = owner.getAttribute("spawned_kolodion");
				if (npc == null) {
					return;
				}
				/* Player logged out. */
				if (!owner.isLoggedIn() || owner.isRemoved()) {
					npc.remove();
					stop();
					return;
				}
				/* Npc has been removed from the world. */
				if (!World.getWorld().hasNpc(npc)) {
					stop();
					return;
				}
				/* Player has left the area */
				if (!npc.withinRange(owner)) {
					npc.remove();
					stop();
					return;
				}
				if (owner.inCombat()) {
					return;
				}
				if (!npc.withinRange(owner, 8)) {
					return;
				}
				if (random(0, 5) != 2) {
					return;
				}
				int spell_type = random(0, 2);
				switch (spell_type) {
				case 0:
					godSpellObject(owner, 33);
					break;
				case 1:
					godSpellObject(owner, 34);
					break;
				case 2:
					godSpellObject(owner, 35);
					break;
				}
				String[] randomMessage = { "this is your end", "die fool", "feel the power of elements" };
				npcYell(owner, npc, randomMessage[random(0, randomMessage.length - 1)]);
				owner.damage(random(3, 13));

			}
		};
		if (kolE != null) {
			if (kolE.shouldRemove()) {
				p.setAttribute("kolodionEvent", kolodionEvent);
				Server.getServer().getEventHandler().add(kolodionEvent);
			}
		} else {
			p.setAttribute("kolodionEvent", kolodionEvent);
			Server.getServer().getEventHandler().add(kolodionEvent);
		}
	}

	public void spawnKolodion(Player player, int id) {
		player.setAttribute("spawned_kolodion", spawnNpc(id, 227, 130, 300000, player));
		player.getCache().set("kolodion_stage", id);
		player.message("kolodion blasts you with his staff " + (id != 713 ? "again" : ""));
		player.damage(random(7, 15));
		startKolodionEvent(player);
		ActionSender.sendTeleBubble(player, player.getX(), player.getY(), true);
	}

	private boolean cantGo(Player p) {
		for (Item item : p.getInventory().getItems()) {
			String name = item.getDef().getName().toLowerCase();
			if (name.contains("dagger") || name.contains("scimitar") || name.contains("bow") || name.contains("mail")
					|| (name.contains("sword") && !name.equalsIgnoreCase("Swordfish")
							&& !name.equalsIgnoreCase("Burnt Swordfish") && !name.equalsIgnoreCase("Raw Swordfish"))
					|| name.contains("mace") || name.contains("helmet") || name.contains("axe")
					|| name.contains("arrow") || name.contains("bow") || name.contains("spear")
					|| name.contains("battlestaff")) {

				return true;
			}
		}
		return false;
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 712;
	}

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		if (n.getID() >= 757 && n.getID() <= 760 || n.getID() == 713) {
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if (n.getID() >= 757 && n.getID() <= 760 || n.getID() == 713) {
			n.remove();

			if (n.getID() == 713) {
				message(p, "kolodion slumps to the floor...");
				message(p, "..his body begins to grow and changes form", "he becomes an intimidating ogre");
				spawnKolodion(p, 757);
			} else if (n.getID() == 757) {
				message(p, "kolodion slumps to the floor once more...",
						"..but again his body begins to grow and changes form", "he becomes enormous spider");
				spawnKolodion(p, n.getID() + 1);
			} else if (n.getID() == 758) {
				message(p, "kolodion slumps to the floor once more...",
						"..but again his body begins to grow as he changes form", "he becomes ethereal being");
				spawnKolodion(p, n.getID() + 1);
			} else if (n.getID() == 759) {
				message(p, "kolodion slumps to the floor motionless..",
						"..but again his body begins to grow as he changes form", "...larger this time",
						"He becomes a vicious demon");
				spawnKolodion(p, n.getID() + 1);
			} else if (n.getID() == 760) {
				message(p, "kolodion slumps to the floor motionless..", "he slowly rises to his feet in his true form");
				message(p, "@yel@Kolodion: \"well done young adventurer\"",
						"@yel@Kolodion: \"you truly are a worthy battle mage\"");
				p.teleport(446, 3370);
				Npc kolodion = getNearestNpc(p, 712, 5);
				if (kolodion == null) {
					p.message("error kolodion not found. better luck next tiem!");
					return;
				}
				p.message("kolodion teleports you to his cave");
				playerTalk(p, kolodion, "what now kolodion, how can i learn some of those spells");
				npcTalk(p, kolodion, "these spells are gifts from gods", "first you must choose which god",
						"...you will represent in the mage arena");
				playerTalk(p, kolodion, "cool");
				npcTalk(p, kolodion, "step into the magic pool, it will carry you to the chamber");
				playerTalk(p, kolodion, "the chamber?");

				npcTalk(p, kolodion, "there you must decide your loyalty");
				playerTalk(p, kolodion, "ok kolodion, thanks for the battle");
				npcTalk(p, kolodion, "remember young mage, you must use the spells...",
						"many times in the arena before you can use them outside");
				playerTalk(p, kolodion, "no problem");
				p.getCache().set("mage_arena", 2);
				p.getCache().remove("kolodion_stage");
			}
		}
	}

	@Override
	public boolean blockPlayerMageNpc(final Player p, final Npc n) {
		if (n.getID() >= 757 && n.getID() <= 760 || n.getID() == 713) {
			if (!n.getAttribute("spawnedFor", null).equals(p)) {
				p.message("that mage is busy.");
				return true;
			}
		}
		return false;
	}

	public void godSpellObject(Mob affectedMob, int spell) {
		switch (spell) {
		case 33:
			GameObject guthix = new GameObject(affectedMob.getLocation(), 1142, 0, 0);
			World.getWorld().registerGameObject(guthix);
			Server.getServer().getGameEventHandler().add(new ObjectRemover(guthix, 2));
			break;
		case 34:
			GameObject sara = new GameObject(affectedMob.getLocation(), 1031, 0, 0);
			World.getWorld().registerGameObject(sara);
			Server.getServer().getGameEventHandler().add(new ObjectRemover(sara, 2));
			break;
		case 35:
			GameObject zammy = new GameObject(affectedMob.getLocation(), 1036, 0, 0);
			World.getWorld().registerGameObject(zammy);
			Server.getServer().getGameEventHandler().add(new ObjectRemover(zammy, 2));
			break;
		case 47:
			GameObject charge = new GameObject(affectedMob.getLocation(), 1147, 0, 0);
			World.getWorld().registerGameObject(charge);
			Server.getServer().getGameEventHandler().add(new ObjectRemover(charge, 2));
			break;
		}
	}

	@Override
	public void onDrop(Player p, Item i) {
		removeItem(p, i);
		p.message("As you drop the " + i.getDef().getName() + ", it vanishes.");
	}

	@Override
	public boolean blockDrop(Player p, Item i) {
		return i.getID() >= 1213 && i.getID() <= 1218;
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == 1019 || obj.getID() == 1020 || obj.getID() == 1027
				|| obj.getID() == 1152 || obj.getID() == 1153 || obj.getID() == 1154;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		if (obj.getID() == 1019 || obj.getID() == 1020) {
			player.message("you open the gate ...");
			player.message("... and walk through");
			doGate(player, obj);
			if (player.getCache().hasKey("mage_arena") && player.getCache().getInt("mage_arena") == 4) {
				learnSpellEvent(player);
			}
		} else if (obj.getID() == 1027) {
			if (player.getY() >= 120) {
				player.message("you pass through the mystical barrier");
				movePlayer(player, 228, 118);
				Npc kolodion = player.getAttribute("spawned_kolodion", null);
				if (kolodion != null) {
					kolodion.remove();
				}
			} else {
				if (player.getCache().hasKey("mage_arena") && player.getCache().getInt("mage_arena") >= 4) {
					message(player, "the barrier is checking your person for weapons");
					if (!cantGo(player)) {
						movePlayer(player, 228, 120);
					} else {
						message(player, "You cannot enter the arena...", "...while carrying weapons or armour");
					}
				} else {
					player.message("you cannot enter without the permission of kolodion");
				}
			}
		} else if (obj.getID() == 1152) {
			if (player.getCache().hasKey("mage_arena") && player.getCache().getInt("mage_arena") >= 2) {
				message(player, "you kneel begin to chant to saradomin");
				if (!alreadyHasCape(player)) {
					message(player, "you feel a rush of energy charge through your veins",
							"...and a cape appears before you");
					addItem(player, 1214, 1);
				} else {
					message(player, "but there is no response");
				}
			}
			if (player.getCache().hasKey("mage_arena") && player.getCache().getInt("mage_arena") == 2) {
				player.getCache().set("mage_arena", 3);
			}
		} else if (obj.getID() == 1153) {
			if (player.getCache().hasKey("mage_arena") && player.getCache().getInt("mage_arena") >= 2) {
				message(player, "you kneel begin to chant to guthix");
				if (!alreadyHasCape(player)) {
					message(player, "you feel a rush of energy charge through your veins",
							"...and a cape appears before you");
					addItem(player, 1215, 1);
				} else {
					message(player, "but there is no response");
				}
			}
			if (player.getCache().hasKey("mage_arena") && player.getCache().getInt("mage_arena") == 2) {
				player.getCache().set("mage_arena", 3);
			}
		} else if (obj.getID() == 1154) {
			if (player.getCache().hasKey("mage_arena") && player.getCache().getInt("mage_arena") >= 2) {
				message(player, "you kneel begin to chant to zamorak");
				if (!alreadyHasCape(player)) {
					message(player, "you feel a rush of energy charge through your veins",
							"...and a cape appears before you");
					addItem(player, 1213, 1);
				} else {
					message(player, "but there is no response");
				}
			}
			if (player.getCache().hasKey("mage_arena") && player.getCache().getInt("mage_arena") == 2) {
				player.getCache().set("mage_arena", 3);
			}
		}
	}

	private boolean alreadyHasCape(Player player) {
		for (Item item : player.getInventory().getItems()) {
			if (item.getID() == 1213 || item.getID() == 1214 || item.getID() == 1215) {
				return true;
			}
		}
		for (Item item : player.getBank().getItems()) {
			if (item.getID() == 1213 || item.getID() == 1214 || item.getID() == 1215) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean blockPlayerAttackNpc(Player p, Npc n) {
		if (n.getID() >= 757 && n.getID() <= 760 || n.getID() == 713) {
			if (!n.getAttribute("spawnedFor", null).equals(p)) {
				p.message("that mage is busy.");
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean blockPlayerDeath(Player p) {
		if (p.getAttribute("spawned_kolodion", null) != null) {
			p.setAttribute("spawned_kolodion", null);
		}
		return false;
	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		if (i.getID() == 1213 || i.getID() == 1214 || i.getID() == 1215) {
			return true;
		}
		return false;
	}

	@Override
	public void onPickup(Player p, GroundItem i) {
		if (i.getID() == 1213 || i.getID() == 1214 || i.getID() == 1215) {
			if (alreadyHasCape(p)) {
				p.message("you may only possess one sacred cape at a time");
			} else {
				Item Item = new Item(i.getID(), i.getAmount());
				World.getWorld().unregisterItem(i);
				p.playSound("takeobject");
				p.getInventory().add(Item);
			}
		}

	}
}