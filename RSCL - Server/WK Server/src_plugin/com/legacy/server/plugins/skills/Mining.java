package com.legacy.server.plugins.skills;

import static com.legacy.server.plugins.Functions.*;

import java.util.Arrays;

import com.legacy.server.Constants;
import com.legacy.server.Constants.Quests;
import com.legacy.server.event.custom.BatchEvent;
import com.legacy.server.external.EntityHandler;
import com.legacy.server.external.ObjectMiningDef;
import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.world.World;
import com.legacy.server.plugins.listeners.action.ObjectActionListener;
import com.legacy.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.legacy.server.util.rsc.DataConversions;
import com.legacy.server.util.rsc.Formulae;

public final class Mining implements ObjectActionListener,
ObjectActionExecutiveListener {

	static int[] ids;

	static {
		ids = new int[] { 176, 100, 101, 102, 103, 104, 105, 106, 107, 108,
				109, 110, 111, 112, 113, 114, 115, 195, 196, 210, 211 };
		Arrays.sort(ids);
	}

	@Override
	public void onObjectAction(final GameObject object, String command,
			Player owner) {
		if (object.getID() == 269) {
			if (command.equalsIgnoreCase("mine")) {
				if (hasItem(owner, getAxe(owner))) {
					if(owner.getSkills().getMaxStat(14) >= 50) {
						owner.message("you manage to dig a way through the rockslide");
						if (owner.getX() <= 425) {
							owner.teleport(428, 438);
						} else {
							owner.teleport(425, 438);
						}
					} else {
						owner.message("you need a level of 50 mining to clear the rockslide");
					}
				} else {
					owner.message("you need a pickaxe to clear the rockslide");
				}
			} else if (command.equalsIgnoreCase("prospect")) {
				owner.message("these rocks contain nothing intersting");
				owner.message("they are just in the way");
			}
		} 
		else if(object.getID() == 770) {
			if (hasItem(owner, getAxe(owner))) {
				owner.setBusyTimer(1600);
				message(owner, "you mine the rock", "and break of several large chunks");
				addItem(owner, 986, 1);
			} else {
				owner.message("you need a pickaxe to mine this rock");
			}
		}
		else if(object.getID() == 1026) { // watchtower - rock of dalgroth
			if (command.equalsIgnoreCase("mine")) {
				if(owner.getQuestStage(Constants.Quests.WATCHTOWER) == 9) {
					if (!hasItem(owner, getAxe(owner))) {
						owner.message("You need a pickaxe to mine the rock");
						return;
					}
					if(owner.getSkills().getMaxStat(14) < 40) {
						owner.message("you need a level of 40 mining to mine this crystal out");
						return;
					}
					if(hasItem(owner, 1154)) {
						playerTalk(owner, null, "I already have this crystal",
								"There is no benefit to getting another");
						return;
					}
					owner.message("You have a swing at the rock!");
					message(owner, "You swing your pick at the rock...");
					owner.message("A crack appears in the rock and you prize a crystal out");
					addItem(owner, 1154, 1);
				} else {
					playerTalk(owner, null, "I can't touch it...",
						"Perhaps it is linked with the shaman some way ?");
				}
			} else if (command.equalsIgnoreCase("prospect")) {
				message(owner, "You examine the rock for ores...");
				owner.message("This rock contains a crystal!");
			}
		}
		else {
			handleMining(object, owner, owner.click);
		}
	}

	public static int getAxe(Player p) {
		int lvl = p.getSkills().getLevel(14);
		for (int i = 0; i < Formulae.miningAxeIDs.length; i++) {
			if (p.getInventory().countId(Formulae.miningAxeIDs[i]) > 0) {
				if (lvl >= Formulae.miningAxeLvls[i]) {
					return Formulae.miningAxeIDs[i];
				} 
			}
		}
		return -1;
	}

	public void handleMining(final GameObject object, Player owner, int click) {
		if (owner.isBusy()) {
			return;
		}
		if (!owner.withinRange(object, 1)) {
			return;
		}

		final ObjectMiningDef def = EntityHandler.getObjectMiningDef(object.getID());
		final int axeId = getAxe(owner);
		int retrytimes = -1;
		final int mineLvl = owner.getSkills().getLevel(14);
		int reqlvl = 1;
		switch (axeId) {
		case 156:
			retrytimes = 1;
			break;
		case 1258:
			retrytimes = 2;
			break;
		case 1259:
			retrytimes = 3;
			reqlvl = 6;
			break;
		case 1260:
			retrytimes = 5;
			reqlvl = 21;
			break;
		case 1261:
			retrytimes = 8;
			reqlvl = 31;
			break;
		case 1262:
			retrytimes = 12;
			reqlvl = 41;
			break;
		}
		
		if(owner.isPremiumSubscriber())
			retrytimes *= 2.0;
		else if(owner.isSubscriber() && !owner.isPremiumSubscriber())
			retrytimes *= 1.5;
		
		if (def == null || def.getRespawnTime() < 1 || (def.getOreId() == 315 && owner.getQuestStage(Quests.FAMILY_CREST) < 6)) {
			if (axeId < 0 || reqlvl > mineLvl) {
				message(owner, "You need a pickaxe to mine this rock",
						"You do not have a pickaxe which you have the mining level to use");
				return;
			}
			owner.setBusyTimer(2000);
			owner.message("You swing your pick at the rock...");
			sleep(2000);
			owner.message("There is currently no ore available in this rock");
			return;
		}
		if (owner.click == 1) {
			owner.setBusyTimer(2000);
			owner.message("You examine the rock for ores...");
			sleep(2000);
			if (def == null || def.getRespawnTime() < 1) {
				owner.message("There is currently no ore available in this rock");
				return;
			}
			owner.message("This rock contains "	+ new Item(def.getOreId()).getDef().getName());
			if (owner.getLocation().onTutorialIsland()
					&& owner.getCache().hasKey("tutorial")
					&& owner.getCache().getInt("tutorial") == 45) {
				message(owner,
						"Sometimes you won't find the ore but trying again may find it",
						"If a rock contains a high level ore",
						"You will not find it until you increase your mining level");
				owner.getCache().set("tutorial", 50);
			}
			return;
		}
		if (axeId < 0 || reqlvl > mineLvl ) {
			message(owner, "You need a pickaxe to mine this rock",
					"You do not have a pickaxe which you have the mining level to use");
			return;
		}
		if (owner.getFatigue() >= 7500) {
			owner.message("You are too tired to mine this rock");
			return;
		}
		owner.playSound("mine");
		showBubble(owner, new Item(1258));
		owner.message("You swing your pick at the rock...");
		owner.setBatchEvent(new BatchEvent(owner, 2000, retrytimes) {
			@Override
			public void action() {
				final Item ore = new Item(def.getOreId());
				if (Formulae.getOre(def, owner.getSkills().getLevel(14), axeId) && mineLvl >= def.getReqLevel()) {
					if (DataConversions.random(0, 200) == 1) {
						Item gem = new Item(Formulae.getGem(), 1);
						owner.incExp(14, 100, true);
						owner.getInventory().add(gem);
						owner.message("You just found a" + gem.getDef().getName().toLowerCase().replaceAll("uncut", "") + "!");
						interrupt();
					} else {
						owner.getInventory().add(ore);
						owner.message("You manage to obtain some " + ore.getDef().getName().toLowerCase());
						owner.incExp(14, def.getExp(), true);
						interrupt();
						GameObject obj = owner.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
						if(obj != null && obj.getID() == object.getID()) {
							GameObject newObject = new GameObject(object.getLocation(), 98, object.getDirection(), object.getType());
							World.getWorld().replaceGameObject(object, newObject);
							World.getWorld().delayedSpawnObject(obj.getLoc(), def.getRespawnTime() * 1000);
						}
					}
				} else {
					if (owner.getLocation().onTutorialIsland()
							&& owner.getCache().hasKey("tutorial")
							&& owner.getCache().getInt("tutorial") == 50) {
						owner.message("You fail to make any real impact on the rock");
					} else {
						owner.message("You only succeed in scratching the rock");
						if(getRepeatFor() > 1) {
							GameObject checkObj = owner.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
							if(checkObj == null) {
								interrupt();
							}
						}
					}
				}
				if(!isCompleted()) {
					showBubble(owner, new Item(1258));
					owner.message("You swing your pick at the rock...");
				}
				
			}
		});
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
			Player player) {
		if ((command.equals("mine") || command.equals("prospect")) && obj.getID() != 588) {
			return true;
		}
		return false;
	}
}