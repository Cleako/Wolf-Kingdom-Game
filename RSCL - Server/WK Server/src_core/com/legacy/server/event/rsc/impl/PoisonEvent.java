package com.legacy.server.event.rsc.impl;

import com.legacy.server.event.rsc.GameTickEvent;
import com.legacy.server.model.entity.Mob;
import com.legacy.server.model.entity.player.Player;
/**
 * 
 * @author n0m
 *
 */
public class PoisonEvent extends GameTickEvent {

	private Mob mob;
	
	private int poisonPower;

	public PoisonEvent(Mob owner, int poisonPower) {
		super((Player) (owner.isPlayer() ? owner : null), 32);
		this.mob = owner;
		this.poisonPower = poisonPower;
	}

	@Override
	public void run() {
		if(poisonPower < 10) {
			mob.cure();
			return;
		}
		double poisonDamage = Math.round((poisonPower / 10));
		int damage = (int) poisonDamage;
		poisonPower -= 2;
		if(mob.isPlayer()) {
			Player player = (Player)mob;
			player.message("@gr3@You @gr2@are @gr1@Poisoned! @gr2@You @gr3@lose @gr2@" + damage + " @gr1@health.");
			player.getCache().set("poisoned", poisonPower);
		}
		mob.damage(damage);
	}

	public void setPoisonPower(int int1) {
		poisonPower = int1;
	}

}
