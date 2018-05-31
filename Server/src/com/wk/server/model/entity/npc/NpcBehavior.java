package com.wk.server.model.entity.npc;

import com.wk.server.Constants;
import com.wk.server.model.Point;
import com.wk.server.model.Skills;
import com.wk.server.model.entity.Mob;
import com.wk.server.model.entity.player.Player;
import com.wk.server.model.states.CombatState;
import com.wk.server.net.rsc.ActionSender;
import com.wk.server.util.rsc.DataConversions;

public class NpcBehavior {

	protected long lastMovement;

	protected Npc npc;

	protected Mob target;

	public NpcBehavior(Npc npc) {
		this.npc = npc;
	}

	enum State {
		ROAM, AGGRO, COMBAT, RETREAT;
	}

	private State state = State.ROAM;

	public void tick() {
		
		if (state == State.ROAM) {
			
			if (npc.inCombat()) {
				state = State.COMBAT;
				return;
			} else if (npc.isBusy()) {
				return;
			}
			
			target = null;
			if (System.currentTimeMillis() - lastMovement > 3000 && npc.finishedPath()) {
				lastMovement = System.currentTimeMillis();
				int rand = DataConversions.random(0, 1);
				if(!npc.isBusy() && rand == 1 && !npc.isRemoved()) {
					int newX = DataConversions.random(npc.getLoc().minX(), npc.getLoc().maxX());
					int newY = DataConversions.random(npc.getLoc().minY(), npc.getLoc().maxY());
					npc.walk(newX, newY);
				}
			}
			if (System.currentTimeMillis() - npc.getCombatTimer() > 3000 && ((npc.getDef().isAggressive())
					|| (npc.getDef().isAggressive() && npc.getLocation().inWilderness()))) {
				for (Player p : npc.getViewArea().getPlayersInView()) {
					// 5 tiles away aggro for UndeadOne, rest should be 1 tile (RSC) we are using 2 tiles.
					if (!canAggro(p) || !p.withinRange(npc, (npc.getID() == 542 ? 5 : 2))) {
						continue;
					}
					state = State.AGGRO;
					target = p;
					break;
				}
			}
		} else if (state == State.AGGRO) {
			if (target == null || npc.isRespawning() || npc.isRemoved() || target.isRemoved() || target.inCombat()) {
				state = State.ROAM;
				return;
			}
			if (target.getX() < (npc.getLoc().minX() - 4) || target.getX() > (npc.getLoc().maxX() + 4)
					|| target.getY() < (npc.getLoc().minY() - 4) || target.getY() > (npc.getLoc().maxY() + 4)) {
				state = State.ROAM;
				return;
			}

			if (npc.inCombat() && npc.getOpponent() != target) {
				target = npc.getOpponent();
				state = State.COMBAT;
			}
			lastMovement = System.currentTimeMillis();
			npc.walkToEntity(target.getX(), target.getY());
			if (npc.withinRange(target, 1) && npc.canReach(target) && !target.inCombat()
					&& !(System.currentTimeMillis()
							- target.getCombatTimer() < (target.getCombatState() == CombatState.RUNNING
									|| target.getCombatState() == CombatState.WAITING ? 3000 : 1500))) {
				npc.startCombat(target);
				state = State.COMBAT;
			}
		} else if (state == State.COMBAT) {
			target = npc.getOpponent();
			if (target == null || npc.isRemoved() || target.isRemoved()) {
				state = State.ROAM;
				return;
			}
			if (npc.inCombat()) {
				if (DataConversions.inArray(Constants.GameServer.NPCS_THAT_DO_RETREAT, npc.getID())
						&& npc.getSkills().getLevel(Skills.HITPOINTS) <= Math
								.ceil(npc.getSkills().getMaxStat(Skills.HITPOINTS) * 0.20)
						&& npc.getSkills().getLevel(Skills.HITPOINTS) > 0 && npc.getOpponent().getHitsMade() >= 3) {
					
					
					state = State.RETREAT;
					if (npc.getOpponent().isPlayer()) {
						Player victimPlayer = ((Player) npc.getOpponent());
						victimPlayer.resetAll();
						victimPlayer.message("Your opponent is retreating");
						ActionSender.sendSound(victimPlayer, "retreat");
					}
					npc.setLastCombatState(CombatState.RUNNING);
					npc.getOpponent().setLastCombatState(CombatState.WAITING);
					npc.resetCombatEvent();

					Point walkTo = Point.location(DataConversions.random(npc.getLoc().minX(), npc.getLoc().maxX()),
							DataConversions.random(npc.getLoc().minY(), npc.getLoc().maxY()));
					npc.walk(walkTo.getX(), walkTo.getY());
				}
			} else if (!npc.inCombat()) {
				if (npc.getDef().isAggressive() || (npc.getDef().isAggressive() && npc.getLocation().inWilderness())) {
					state = State.AGGRO;
				} else {
					state = State.ROAM;
				}
			}

		} else if (state == State.RETREAT) {
			if (npc.finishedPath())
				state = State.ROAM;
		}
	}

	private boolean canAggro(Mob p) {
		boolean outOfBounds = !p.getLocation().inBounds(npc.getLoc().minX - 4, npc.getLoc().minY - 4,
				npc.getLoc().maxX + 4, npc.getLoc().maxY + 4);

		boolean playerOccupied = p.inCombat();
		boolean playerCombatTimeout = System.currentTimeMillis()
				- p.getCombatTimer() < (p.getCombatState() == CombatState.RUNNING
						|| p.getCombatState() == CombatState.WAITING ? 3000 : 1500);

		boolean shouldAttack = p.getCombatLevel() <= ((npc.getNPCCombatLevel() * 2) + 1)
				|| npc.getLocation().inWilderness();

		boolean closeEnough = npc.canReach(p);

		boolean revenantsTarget = false;
		return closeEnough && shouldAttack && !p.getAttribute("no-aggro", false) && !outOfBounds && !playerOccupied
				&& !playerCombatTimeout && !p.warnedToMove() && !revenantsTarget;
	}

	public State getBehaviorState() {
		return state;
	}

	public boolean isChasing() {
		return state == State.AGGRO;
	}

	public Player getChasedPlayer() {
		if (target.isPlayer())
			return (Player) target;

		return null;
	}

	public Mob getChaseTarget() {
		return target;
	}

	public void setChasing(Player player) {
		state = State.AGGRO;
		this.target = player;
	}

	public void onKill(Mob killed) {

	}
}