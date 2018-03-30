package com.legacy.server.event.rsc.impl.combat;

import com.legacy.server.Constants;
import com.legacy.server.event.rsc.GameTickEvent;
import com.legacy.server.event.rsc.impl.combat.scripts.CombatScriptLoader;
import com.legacy.server.model.Skills;
import com.legacy.server.model.entity.Mob;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.entity.player.Prayers;
import com.legacy.server.model.entity.update.Damage;
import com.legacy.server.model.states.Action;
import com.legacy.server.model.states.CombatState;
import com.legacy.server.net.rsc.ActionSender;
import com.legacy.server.plugins.PluginHandler;
import com.legacy.server.util.rsc.DataConversions;
import com.legacy.server.util.rsc.Formulae;

/***
 * 
 * @author n0m
 *
 */
public class CombatEvent extends GameTickEvent {

	protected final Mob attackerMob, defenderMob;

	public CombatEvent(Mob attacker, Mob defender) {
		super(null, 0);
		this.attackerMob = attacker;
		this.defenderMob = defender;
		CombatScriptLoader.checkAndExecuteOnStartCombatScript(attacker, defender);
	}

	protected static void onDeath(Mob killed, Mob killer) {
		if (killer.isPlayer() && killed.isNpc()) {
			if (PluginHandler.getPluginHandler().blockDefaultAction("PlayerKilledNpc",
					new Object[] { ((Player) killer), ((Npc) killed) })) {
				return;
			}
		}

		killed.setLastCombatState(CombatState.WON);
		killer.setLastCombatState(CombatState.LOST);

		if (killed.isPlayer() && killer.isPlayer()) {
			Player playerKiller = (Player) killer;
			Player playerKilled = (Player) killed;

			int exp = DataConversions.roundUp(Formulae.combatExperience(playerKilled) / 4D);
			switch (playerKiller.getCombatStyle()) {
			case 0:
				for (int x = 0; x < 3; x++) {
					playerKiller.incExp(x, exp, true);
				}
				break;
			case 1:
				playerKiller.incExp(2, exp * 3, true);
				break;
			case 2:
				playerKiller.incExp(0, exp * 3, true);
				break;
			case 3:
				playerKiller.incExp(1, exp * 3, true);
				break;
			}
			playerKiller.incExp(3, exp, true);
		}
		killer.setKillType(0);
		killed.killedBy(killer);
	}

	private int roundNumber = 0;

	public final void run() {
		setDelayTicks(2);
		Mob hitter, target = null;

		if (roundNumber++ % 2 == 0) {
			hitter = attackerMob;
			target = defenderMob;
		} else {
			hitter = defenderMob;
			target = attackerMob;
		}

		if (!combatCanContinue()) {
			hitter.setLastCombatState(CombatState.ERROR);
			target.setLastCombatState(CombatState.ERROR);
			resetCombat();
		} else {
			if(hitter.isNpc() && target.isPlayer() || target.isNpc() && hitter.isPlayer()) {
				inflictDamage(hitter, target, MeleeFormula.getDamage(hitter, target));
			} else {
				inflictDamage(hitter, target, PVPCombatFormula.calcFightHit(hitter, target));
			}
		}
	}

	public  void inflictDamage(final Mob hitter, final Mob target, int damage) {
		hitter.incHitsMade();
		if (hitter.isNpc() && target.isPlayer()) {
			Player targetPlayer = (Player) target;

			if (targetPlayer.getPrayers().isPrayerActivated(Prayers.PARALYZE_MONSTER)) {
				CombatScriptLoader.checkAndExecuteCombatScript(hitter, target);
				return;
			}
		}

		target.getSkills().subtractLevel(Skills.HITPOINTS, damage, false);
		target.getUpdateFlags().setDamage(new Damage(target, damage));
		if (target.isNpc() && hitter.isPlayer()) {
			Npc n = (Npc) target;
			Player p = ((Player) hitter);
			n.addCombatDamage(p, damage);
		}

		String combatSound = null;
		combatSound = damage > 0 ? "combat1b" : "combat1a";

		if (target.isPlayer()) {
			if (hitter.isNpc()) {
				Npc n = (Npc) hitter;
				if (DataConversions.inArray(Constants.GameServer.ARMOR_NPCS, n.getID())) {
					combatSound = damage > 0 ? "combat2b" : "combat2a";
				} else if (DataConversions.inArray(Constants.GameServer.UNDEAD_NPCS, n.getID())) {
					combatSound = damage > 0 ? "combat3b" : "combat3a";
				} else {
					combatSound = damage > 0 ? "combat1b" : "combat1a";
				}
			}
			Player opponentPlayer = ((Player) target);
			ActionSender.sendSound(opponentPlayer, combatSound);
		}
		if (hitter.isPlayer()) {
			if (target.isNpc()) {
				Npc n = (Npc) target;
				if (DataConversions.inArray(Constants.GameServer.ARMOR_NPCS, n.getID())) {
					combatSound = damage > 0 ? "combat2b" : "combat2a";
				} else if (DataConversions.inArray(Constants.GameServer.UNDEAD_NPCS, n.getID())) {
					combatSound = damage > 0 ? "combat3b" : "combat3a";
				} else {
					combatSound = damage > 0 ? "combat1b" : "combat1a";
				}
			}
			Player attackerPlayer = (Player) hitter;
			ActionSender.sendSound(attackerPlayer, combatSound);
		}

		if (target.getSkills().getLevel(3) > 0) {
			CombatScriptLoader.checkAndExecuteCombatScript(hitter, target);
		} else {
			onDeath(target, hitter);
		}
	}

	public void resetCombat() {
		if (running) {
			if (defenderMob != null) {
				if (defenderMob.isPlayer()) {
					Player player = (Player) defenderMob;
					player.setStatus(Action.IDLE);
					player.resetAll();
				}
				defenderMob.setBusy(false);
				defenderMob.setOpponent(null);
				defenderMob.setCombatEvent(null);
				defenderMob.setHitsMade(0);
				defenderMob.setSprite(4);
				defenderMob.setCombatTimer();
			}
			if (attackerMob != null) {
				if (attackerMob.isPlayer()) {
					Player player = (Player) attackerMob;
					player.setStatus(Action.IDLE);
					player.resetAll();
				}
				attackerMob.setBusy(false);
				attackerMob.setOpponent(null);
				attackerMob.setCombatEvent(null);
				attackerMob.setHitsMade(0);
				attackerMob.setSprite(4);
				attackerMob.setCombatTimer();
			}
		}
		stop();
	}

	private boolean combatCanContinue() {
		boolean bothLoggedIn = (attackerMob.isPlayer() && ((Player) attackerMob).loggedIn())
				|| (defenderMob.isPlayer() && ((Player) defenderMob).loggedIn());
		boolean removed = attackerMob.isRemoved() || defenderMob.isRemoved();
		boolean nextToVictim = attackerMob.getLocation().equals(defenderMob.getLocation());
		if (defenderMob.isNpc() && attackerMob.isNpc()) {
			return !removed && nextToVictim && running;
		}
		return bothLoggedIn && !removed && nextToVictim && running;
	}

	public Mob getAttacker() {
		return attackerMob;
	}

	public Mob getVictim() {
		return defenderMob;
	}

}
