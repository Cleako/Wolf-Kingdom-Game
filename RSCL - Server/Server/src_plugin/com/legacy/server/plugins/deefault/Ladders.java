package com.legacy.server.plugins.deefault;

import static com.legacy.server.plugins.Functions.*;

import com.legacy.server.Constants;
import com.legacy.server.Server;
import com.legacy.server.event.ShortEvent;
import com.legacy.server.external.EntityHandler;
import com.legacy.server.model.TelePoint;
import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.world.World;
import com.legacy.server.util.rsc.Formulae;


public class Ladders {

	public boolean blockObjectAction(GameObject obj, String command,
			Player player) {
		return (command.equals("climb-down") || command.equals("go down") || command
				.equals("climb down"))
				|| command.equals("climb-up")
				|| command.equals("go up")
				|| command.equals("pull");
	}

	public void onObjectAction(GameObject obj, String command, Player player) {
		player.setBusyTimer(650);
		if (obj.getID() == 487 && !Constants.GameServer.MEMBER_WORLD) {
			player.message(player.MEMBER_MESSAGE);
			return;
		}
		if(obj.getID() == 79 && obj.getX() == 243 && obj.getY() == 95) {
			player.message("Are you sure you want to go down to this lair?");
			int menu = showMenu(player,  "Yes I take the risk!", "No stay up here.");
			if(menu == 0) {
				player.message("You climb down the manhole and land in a water lair");
				player.teleport(98, 2931);
			} else if(menu == 1) {
				player.message("You decide to stay.");
			}
			//player.message("The new dungeon is available in a couple of minutes");
			//player.message("We are doing the decoration, please stay tuned.");
			return;
		}
		if(obj.getID() == 5 && (obj.getX() == 98 && obj.getY() == 2930 || obj.getX() == 137 && obj.getY() == 2932)) {
			player.teleport(243, 96);
			player.message("You climb up the ladder");
			return;
		}
		if(obj.getID() == 629) {
			player.teleport(576, 3580);
			player.message("You go up the stairs");
			return;
		}
		if(obj.getID() == 621) {
			player.teleport(606, 3556);
			player.message("You go up the stairs");
			return;
		}
		TelePoint telePoint = EntityHandler.getObjectTelePoint(obj
				.getLocation(), command);
		if (telePoint != null) {
			player.teleport(telePoint.getX(), telePoint.getY(), false);
		}
		if(obj.getID() == 487) {
			player.message("You pull the lever");
			player.teleport(567, 3330);
			sleep(600);
			if(player.getX() == 567 && player.getY() == 3330) {
				displayTeleportBubble(player, player.getX(), player.getY(), false);
			}
			return;
		}
		else if(obj.getID() == 488) {
			player.message("You pull the lever");
			player.teleport(282, 3019);
			sleep(600);
			if(player.getX() == 282 && player.getY() == 3019) {
				displayTeleportBubble(player, player.getX(), player.getY(), false);
			}
			return;
		}
		else if(obj.getID() == 349) {
			player.message("You pull the lever");
			player.teleport(621, 596);
			sleep(600);
			if(player.getX() == 621 && player.getY() == 596) {
				displayTeleportBubble(player, player.getX(), player.getY(), false);
			}
			return;
		}
		else if(obj.getID() == 348) {
			player.message("you pull the lever");
			player.teleport(180, 128);
			displayTeleportBubble(player, player.getX(), player.getY(), false);
			sleep(600);
			if(player.getX() == 180 && player.getY() == 128) {
				displayTeleportBubble(player, player.getX(), player.getY(), false);
			}
			return;
		} else if(obj.getID() == 776) {
			if(hasItem(player, 987)) {
				player.message("The barman takes your ticket and allows you up to");
				player.message("the dormitory.");
				player.teleport(395, 2713);
				player.message("You climb up the ladder");
			} else {
				Npc kaleb = getNearestNpc(player, 621, 10);
				if(kaleb != null) {
					player.message("You need a ticket to access the dormitory");
					npcTalk(player, kaleb, "You can buy a ticket to the dormitory from me.",
							"And have a lovely nights rest.");
				} else {
					player.message("Kaleb is busy at the moment.");
				}
			}
			return;
		}
		else if (obj.getID() == 198 && obj.getX() == 251 && obj.getY() == 468) { // Prayer
			// Guild
			// Ladder
			if (player.getSkills().getMaxStat(5) < 31) {
				player.setBusy(true);
				Npc abbot = World.getWorld().getNpc(174, 249, 252, 458, 468);
				if (abbot != null) {

					npcTalk(player, abbot, "Hello only people with high prayer are allowed in here");
				} else {
					return;
				}
				Server.getServer().getEventHandler().add(
						new ShortEvent(player) {
							public void action() {
								owner.setBusy(false);
								owner.message(
										"You need a prayer level of 31 to enter");
							}
						});
			} else {
				player.teleport(251, 1411, false);
			}
			return;
		} else if (obj.getID() == 223 && obj.getX() == 274 && obj.getY() == 566) { // Mining
			// Guild
			// Ladder
			if (player.getSkills().getLevel(14) < 60) {
				player.setBusy(true);
				Npc dwarf = World.getWorld().getNpc(191, 272, 277, 563, 567);
				if (dwarf != null) {
					npcYell(player, dwarf,
							"Hello only the top miners are allowed in here");
				}
				Server.getServer().getEventHandler().add(
						new ShortEvent(player) {
							public void action() {
								owner.setBusy(false);
								owner.message(
										"You need a mining level of 60 to enter");
							}
						});
			} else {
				player.teleport(274, 3397, false);
			}
			return;
		}
		else if(obj.getID() == 342 && obj.getX() == 611 && obj.getY() == 601) {
			Npc paladinGuard = getNearestNpc(player, 323, 4);
			if(paladinGuard != null) {
				npcYell(player, paladinGuard, "Stop right there");
				paladinGuard.setChasing(player);
				return;
			}
		}

		else if (obj.getID() == 249 && obj.getX() == 98 && obj.getY() == 3537) { // lost city (Zanaris) ladder
			Npc ladderAttendant = World.getWorld().getNpc(229, 99, 99, 3537, 3537);
			if(ladderAttendant != null) {
				npcTalk(player, ladderAttendant, "This ladder leaves Zanaris",
						"It leads to near Al Kharid in your mortal realm",
						"You won't be able to return this way",
						"Are you sure you have sampled your fill of delights from our market?");
				int m = showMenu(player, ladderAttendant, "I think I'll stay down here a bit longer", "Yes, I'm ready to leave");
				if(m == 1) {
					player.message("You climb up the ladder");
					player.teleport(98, 706, false);
				}
			}
			return;
		}

		if (obj.getID() == 1187 && obj.getX() == 446 && obj.getY() == 3367) {
			player.teleport(222, 110, false);
			return;
		}

		if (obj.getID() == 331 && obj.getX() == 150 && obj.getY() == 558) {
			player.teleport(151, 1505, false);
			return;
		}
		if(obj.getID() == 6 && obj.getX() == 282 && obj.getY() == 185 && !Constants.GameServer.MEMBER_WORLD) {
			player.message(player.MEMBER_MESSAGE);
			return;
		}
		if (obj.getID() == 6 && obj.getX() == 148 && obj.getY() == 1507) {
			player.teleport(148, 563, false);
			return;
		} else if(obj.getID() == 630 && obj.getX() == 576 && obj.getY() == 3577) {
			return;
		}
		if (command.equals("climb-up") || command.equals("climb up")
				|| command.equals("go up")) {
			int[] coords = coordModifier(player, true, obj);
			player.teleport(coords[0], coords[1], false);
			player.message(
					"You " + command.replace("-", " ") + " the "
							+ obj.getGameObjectDef().getName().toLowerCase());
			return;
		} else if (command.equals("climb-down") || command.equals("climb down")
				|| command.equals("go down")) {
			int[] coords = coordModifier(player, false, obj);
			player.teleport(coords[0], coords[1], false);
			player.message(
					"You " + command.replace("-", " ") + " the "
							+ obj.getGameObjectDef().getName().toLowerCase());
			return;
		}
	}

	private int[] coordModifier(Player player, boolean up, GameObject object) {
		if (object.getGameObjectDef().getHeight() <= 1) {
			return new int[] { player.getX(),
					Formulae.getNewY(player.getY(), up) };
		}
		int[] coords = { object.getX(), Formulae.getNewY(object.getY(), up) };
		switch (object.getDirection()) {
		case 0:
			coords[1] -= (up ? -object.getGameObjectDef().getHeight() : 1);
			break;
		case 2:
			coords[0] -= (up ? -object.getGameObjectDef().getHeight() : 1);
			break;
		case 4:
			coords[1] += (up ? -1 : object.getGameObjectDef().getHeight());
			break;
		case 6:
			coords[0] += (up ? -1 : object.getGameObjectDef().getHeight());
			break;
		}
		return coords;
	}

}
