package com.legacy.server.event.rsc.impl.combat.scripts.all;

import com.legacy.server.event.rsc.impl.combat.scripts.CombatScript;
import com.legacy.server.model.Skills;
import com.legacy.server.model.entity.Mob;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
/**
 * 
 * @author n0m
 *
 */
public class NpcPoisonPlayerScript implements CombatScript {

	@Override
	public void executeScript(Mob attacker, Mob victim) {
		victim.poisonDamage = attacker.getSkills().getMaxStat(Skills.HITPOINTS);
		victim.startPoisonEvent();
	}

	@Override
	public boolean shouldExecute(Mob attacker, Mob victim) {
		if(attacker.isNpc() && victim.isPlayer()) {
			Player p = (Player) victim;
			if(p.isAntidoteProtected()) {
				return false;
			}
			return (((Npc) attacker).getDef().getName().toLowerCase().contains("poison") || ((Npc) attacker).getID() == 656 || ((Npc) attacker).getID() == 421 || ((Npc) attacker).getID() == 776) && (victim.getRandom().nextInt(100) >= 90);
		}
		return false;
	}

	@Override
	public boolean shouldCombatStop() {
		return false;
	}

}
