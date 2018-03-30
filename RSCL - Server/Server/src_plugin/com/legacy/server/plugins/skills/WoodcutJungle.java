package com.legacy.server.plugins.skills;

import static com.legacy.server.plugins.Functions.*;

import com.legacy.server.Constants;
import com.legacy.server.external.EntityHandler;
import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.GroundItem;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.world.World;
import com.legacy.server.plugins.listeners.action.ObjectActionListener;
import com.legacy.server.plugins.listeners.action.WallObjectActionListener;
import com.legacy.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.legacy.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.legacy.server.util.rsc.DataConversions;
import com.legacy.server.util.rsc.Formulae;

public class WoodcutJungle implements ObjectActionListener,
ObjectActionExecutiveListener, WallObjectActionListener, WallObjectActionExecutiveListener {

	public static int[] JUNGLE_TREES = { 1086, 1100, 1099, 1092, 1091 };

	public static int JUNGLE_VINE = 204;

	public static int JUNGLE_TREE_STUMP = 1087;

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		if(inArray(obj.getID(), JUNGLE_TREES)) {
			return true;
		}
		if(obj.getID() == JUNGLE_TREE_STUMP) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if(inArray(obj.getID(), JUNGLE_TREES)) {
			handleJungleWoodcut(obj, p);
		}
		if(obj.getID() == JUNGLE_TREE_STUMP) {
			p.teleport(obj.getX(), obj.getY());
		}
	}

	private void handleJungleWoodcut(GameObject obj, Player p) {
		boolean canCut = true;
		if (p.getFatigue() >= 7050) {
			if(p.getFatigue() < 7500) {
				p.message("You are getting very tired, you may get stuck if you continue into the jungle.");
			} else if(p.getFatigue() >= 7500) {
				p.message("You are too tired to cut the " + (obj.getID() == JUNGLE_VINE ? "jungle vines" : "tree"));
			}
			canCut = false;
		} 
		if(!hasItem(p, 1172)) { // machete.
			message(p, 1900, "This jungle is very thick, you'll need a machette to cut through.");
			canCut = false;
		}
		if(!hasItem(p, 1163) && !hasItem(p, 1233) && p.getQuestStage(Constants.Quests.LEGENDS_QUEST) != -1) { // the radimus scrolls.
			message(p, 1900, "This jungle is far too thick, you'll need a special map to go further.");
			canCut = false;
		}
		/* TODO 50 requirement for woodcutting? */
		if(p.getSkills().getMaxStat(WOODCUT) < 50) {
			p.message("You need a woodcutting level of 50 to axe this tree");
			return;
		}
		int axeId = -1;
		if(obj.getID() != JUNGLE_VINE) {
			for (final int a : Formulae.woodcuttingAxeIDs) {
				if (p.getInventory().countId(a) > 0) {
					axeId = a;
					break;
				}
			}
			if (axeId < 0) {
				p.message("You need an axe to chop this tree down");
				return;
			}
		} else {
			axeId = 1172;
		}
		if(canCut) {
			p.setBusy(true);
			showBubble(p, new Item(axeId));
			message(p, 1300, "You swing your " + EntityHandler.getItemDef(axeId).getName().toLowerCase() + " at the " + (obj.getID() == JUNGLE_VINE ? "jungle vines" : "tree") + "...");
			if (getLog(50, p.getSkills().getLevel(8), axeId)) {
				GameObject jungleObject = p.getViewArea().getGameObject(obj.getID(), obj.getX(), obj.getY());
				if(jungleObject != null && jungleObject.getID() == obj.getID()) {
					if(obj.getID() == JUNGLE_VINE) {
						p.incExp(8, (int) 5, true);
						World.getWorld().unregisterGameObject(jungleObject);
						World.getWorld().delayedSpawnObject(obj.getLoc(), 5500); // 5.5 seconds.
						message(p, 1200, "You hack your way through the jungle.");
					} else {
						p.incExp(8, (int) 25, true);
						World.getWorld().replaceGameObject(obj, new GameObject(obj.getLocation(), JUNGLE_TREE_STUMP, obj.getDirection(), obj.getType()));
						World.getWorld().delayedSpawnObject(obj.getLoc(), 60 * 1000); // 1 minute.
					}
				}
				if(DataConversions.random(0, 10) == 8) {
					final Item log = new Item(14);
					if(!p.getInventory().full()) 
						p.getInventory().add(log);
					else 
						World.getWorld().registerItem(new GroundItem(log.getID(), p.getX(), p.getY(), log.getAmount(), p));
					p.message("You get some wood");
				}
				p.teleport(obj.getX(), obj.getY());
				if(p.getY() > 871) {
					if(obj.getID() == JUNGLE_VINE) 
						sleep(4000);
					p.message("You manage to hack your way into the Kharazi Jungle.");
				}
			} else {
				p.message("You slip and fail to hit the " + (obj.getID() == JUNGLE_VINE ? "jungle vines" : "tree"));
			}
			p.setBusy(false);
		}
	}

	private boolean getLog(int reqlevel, int woodcutLevel, int axeId) {
		int levelDiff = woodcutLevel - reqlevel;
		if (levelDiff < 0) {
			return false;
		}
		switch (axeId) {
		case 87:
			levelDiff += 0;
			break;
		case 12:
			levelDiff += 2;
			break;
		case 428:
			levelDiff += 4;
			break;
		case 88:
			levelDiff += 6;
			break;
		case 203:
			levelDiff += 8;
			break;
		case 204:
			levelDiff += 10;
			break;
		case 405:
			levelDiff += 12;
			break;
		}
		if (reqlevel == 1 && levelDiff >= 40) {
			return true;
		}
		return DataConversions.percentChance(Formulae.offsetToPercent(levelDiff));
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
		if(obj.getID() == JUNGLE_VINE) {
			return true;
		}
		return false;
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if(obj.getID() == JUNGLE_VINE) {
			handleJungleWoodcut(obj, p);
		}
	}
}
