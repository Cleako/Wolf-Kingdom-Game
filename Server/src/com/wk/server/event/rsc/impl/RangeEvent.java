package com.wk.server.event.rsc.impl;

import com.wk.server.Constants;
import com.wk.server.Server;
import com.wk.server.event.rsc.GameTickEvent;
import com.wk.server.external.EntityHandler;
import com.wk.server.model.PathValidation;
import com.wk.server.model.Skills;
import com.wk.server.model.container.Item;
import com.wk.server.model.entity.GroundItem;
import com.wk.server.model.entity.Mob;
import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.entity.player.Player;
import com.wk.server.model.entity.player.Prayers;
import com.wk.server.model.world.World;
import com.wk.server.net.rsc.ActionSender;
import com.wk.server.plugins.PluginHandler;
import com.wk.server.util.rsc.DataConversions;
import com.wk.server.util.rsc.Formulae;

/**
 * 
 * @author n0m
 *
 */
public class RangeEvent extends GameTickEvent {

	private Mob target;

	public int[][] allowedArrows = { { 189, 11, 638, 639 }, // Shortbow
			{ 188, 11, 638, 639 }, // Longbow
			{ 649, 11, 638, 639 }, // Oak Shortbow
			{ 648, 11, 638, 639, 640, 641 }, // Oak Longbow
			{ 651, 11, 638, 639, 640, 641 }, // Willow Shortbow
			{ 650, 11, 638, 639, 640, 641, 642, 643 }, // Willow Longbow
			{ 653, 11, 638, 639, 640, 641, 642, 643 }, // Maple Shortbow
			{ 652, 11, 638, 639, 640, 641, 642, 643, 644, 645 }, // Maple
																	// Longbow
			{ 655, 11, 638, 639, 640, 641, 642, 643, 644, 645, 723 }, // Yew
																		// Shortbow
			{ 654, 11, 638, 639, 640, 641, 642, 643, 644, 645, 646, 647, 723 }, // Yew
																				// Longbow
			{ 657, 11, 638, 639, 640, 641, 642, 643, 644, 645, 646, 647, 723 }, // Magic
																				// Shortbow
			{ 656, 11, 638, 639, 640, 641, 642, 643, 644, 645, 646, 647, 723 } // Magic
																				// Longbow
	};

	public RangeEvent(Player owner, Mob victim) {
		super(owner, 1);
		this.setImmediate(true);
		this.target = victim;
	}

	public boolean equals(Object o) {
		if (o instanceof RangeEvent) {
			RangeEvent e = (RangeEvent) o;
			return e.belongsTo(owner);
		}
		return false;
	}

	public Mob getTarget() {
		return target;
	}

	private GroundItem getArrows(int id) {
		return target.getViewArea().getGroundItem(id, target.getLocation());
	}

	public void run() {
		int bowID = getPlayerOwner().getRangeEquip();
		if (!getPlayerOwner().loggedIn() || getPlayerOwner().inCombat()
				|| (target.isPlayer() && !((Player) target).loggedIn())
				|| target.getSkills().getLevel(Skills.HITPOINTS) <= 0 || !getPlayerOwner().checkAttack(target, true)
				|| !getPlayerOwner().withinRange(target) || bowID < 0) {
			getPlayerOwner().resetRange();
			stop();
			return;
		}
		if (!canReach(target)) {
			getPlayerOwner().walkToEntity(target.getX(), target.getY());
			if(owner.nextStep(owner.getX(), owner.getY(), target) == null && bowID != -1) {
				getPlayerOwner().message("I can't get close enough");
				getPlayerOwner().resetRange();
				stop();
				return;
			}
		} else {
			getPlayerOwner().resetPath();

			boolean canShoot = System.currentTimeMillis() - getPlayerOwner().getAttribute("rangedTimeout", 0L) > 1900;
			if (canShoot) {
				if (!PathValidation.checkPath(getPlayerOwner().getLocation(), target.getLocation())) {
					getPlayerOwner().message("I can't get a clear shot from here");
					getPlayerOwner().resetRange();
					stop();
					return;
				}
				getPlayerOwner().face(target);
				getPlayerOwner().setAttribute("rangedTimeout", System.currentTimeMillis());

				if (target.isPlayer()) {
					Player playerTarget = (Player) target;
					if (playerTarget.getPrayers().isPrayerActivated(Prayers.PROTECT_FROM_MISSILES)) {
						getPlayerOwner().message("Player has a protection from missiles prayer active!");
						return;
					}
				}
				
				if (target.isNpc()) {
					if (PluginHandler.getPluginHandler().blockDefaultAction("PlayerRangeNpc",
							new Object[] { owner, target })) {
						getPlayerOwner().resetRange();
						stop();
						return;
					}
				}
				boolean xbow = DataConversions.inArray(Formulae.xbowIDs, bowID);
				int arrowID = -1;
				for (int aID : (xbow ? Formulae.boltIDs : Formulae.arrowIDs)) {
					int slot = getPlayerOwner().getInventory().getLastIndexById(aID);
					if (slot < 0) {
						continue;
					}
					Item arrow = getPlayerOwner().getInventory().get(slot);
					if (arrow == null) { // This shouldn't happen
						continue;
					}
					arrowID = aID;
					if (!Constants.GameServer.MEMBER_WORLD) {
						if (arrowID != 11 && arrowID != 190) {
							getPlayerOwner().message("You don't have enough ammo in your quiver");
							getPlayerOwner().resetRange();
							stop();
							return;
						}

					}
					if (arrowID != 11 && arrowID != 190) {
						if (!getPlayerOwner().getLocation().isMembersWild()) {
							getPlayerOwner().message("Members content can only be used in wild levels: "
									+ World.membersWildStart + " - " + World.membersWildMax);
							getPlayerOwner().message("You can not use this type of arrows in wilderness.");
							getPlayerOwner().resetRange();
							stop();
							return;
						}
					}

					int newAmount = arrow.getAmount() - 1;
					if (!xbow && arrowID > 0) {
						int temp = -1;

						for (int i = 0; i < allowedArrows.length; i++)
							if (allowedArrows[i][0] == getPlayerOwner().getRangeEquip())
								temp = i;

						boolean canFire = false;
						for (int i = 0; i < allowedArrows[temp].length; i++)
							if (allowedArrows[temp][i] == aID)
								canFire = true;

						if (!canFire) {
							getPlayerOwner().message("Your arrows are too powerful for your Bow.");
							getPlayerOwner().resetRange();
							return;
						}
					}

					if (newAmount <= 0) {
						getPlayerOwner().getInventory().remove(slot);
					} else {
						arrow.setAmount(newAmount);
						ActionSender.sendInventory(getPlayerOwner());
					}
					break;
				}
				if (arrowID < 0) {
					getPlayerOwner().message("You have run out of " + (xbow ? "bolts" : "arrows"));
					getPlayerOwner().resetRange();
					return;
				}
				int damage = Formulae.calcRangeHit(getPlayerOwner(),
						getPlayerOwner().getSkills().getLevel(Skills.RANGE), target.getArmourPoints(), arrowID);

				if (target.isNpc()) {
					Npc npc = (Npc) target;
					if (damage > 1 && npc.getID() == 477)
						damage = damage / 2;
					if (npc.getID() == 196) {
						getPlayerOwner().message("The dragon breathes fire at you");
						int maxHit = 65;
						if (getPlayerOwner().getInventory().wielding(420)) {
							maxHit = 10;
							getPlayerOwner().message("Your shield prevents some of the damage from the flames");
						}
						getPlayerOwner().damage(DataConversions.random(0, maxHit));
					}
				}
				if (!Formulae.looseArrow(damage)) {
					GroundItem arrows = getArrows(arrowID);
					if (arrows == null) {
						World.getWorld().registerItem(
								new GroundItem(arrowID, target.getX(), target.getY(), 1, getPlayerOwner()));
					} else {
						arrows.setAmount(arrows.getAmount() + 1);
					}
				}
				if (target.isPlayer()) {
					((Player) target).message(getPlayerOwner().getUsername() + " is shooting at you!");
				}
				ActionSender.sendSound(getPlayerOwner(), "shoot");
				if (EntityHandler.getItemDef(arrowID).getName().toLowerCase().contains("poison") && target.isPlayer()) {
					if (DataConversions.random(0, 100) <= 10) {
						target.poisonDamage = target.getSkills().getMaxStat(Skills.HITPOINTS);
						target.startPoisonEvent();
					}
				}
				Server.getServer().getGameEventHandler().add(new ProjectileEvent(getPlayerOwner(), target, damage, 2));
				owner.setKillType(2);
			}
		}
	}

	private boolean canReach(Mob mob) {
		int radius = 5;
		if (getPlayerOwner().getRangeEquip() == 59 || getPlayerOwner().getRangeEquip() == 60)
			radius = 4;
		if (getPlayerOwner().getRangeEquip() == 189)
			radius = 4;
		return getPlayerOwner().withinRange(mob, radius);
	}

}
