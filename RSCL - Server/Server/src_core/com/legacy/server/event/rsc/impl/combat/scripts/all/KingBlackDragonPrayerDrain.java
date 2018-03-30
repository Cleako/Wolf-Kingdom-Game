package com.legacy.server.event.rsc.impl.combat.scripts.all;

import com.legacy.server.event.rsc.impl.combat.scripts.OnCombatStartScript;
import com.legacy.server.model.Skills;
import com.legacy.server.model.entity.Mob;
import com.legacy.server.model.entity.npc.Npc;

/**
 * 
 * @author n0m
 * 
 */
public class KingBlackDragonPrayerDrain implements OnCombatStartScript {

	@Override
	public boolean shouldExecute(Mob attacker, Mob defender) {
		if (attacker.isNpc()) {
			Npc attackerNpc = ((Npc) attacker);
			if (attackerNpc.getID() == 477) {
				return true;
			}
		} else if (defender.isNpc()) {
			Npc defenderNpc = ((Npc) defender);
			if (defenderNpc.getID() == 477) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void executeScript(Mob attacker, Mob defender) {
		if (attacker.isPlayer()) {
			if (attacker.getSkills().getLevel(Skills.PRAYER) > 1)
				attacker.getSkills().setLevel(Skills.PRAYER, 1);

		} else if (defender.isPlayer()) {
			if (defender.getSkills().getLevel(Skills.PRAYER) > 1)
				defender.getSkills().setLevel(Skills.PRAYER, 1);
		}
		
	}

}
