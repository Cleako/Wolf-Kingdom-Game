package com.legacy.server.event.rsc.impl;

import java.util.ArrayList;

import com.legacy.server.Server;
import com.legacy.server.event.rsc.GameTickEvent;
import com.legacy.server.model.PathValidation;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.util.rsc.DataConversions;

public class FireCannonEvent extends GameTickEvent {

	public FireCannonEvent(Player player) {
		super(player, 1);
	}

	@Override
	public void run() {
		getPlayerOwner().message("searching for targets");
		
		Iterable<Npc> npcsInView = getPlayerOwner().getLocalNpcs();
		
		ArrayList<Npc> possibleTargets = new ArrayList<Npc>();
		for (Npc n : npcsInView) {
			if ((n.getLocation().inBounds(owner.getX() - 8, owner.getY() - 8, owner.getX() + 8, owner.getY() + 8))
					&& (n.getDef().isAttackable()) && PathValidation.checkPath(owner.getLocation(), n.getLocation())) {
				possibleTargets.add(n);
			}
		}
		
		if(possibleTargets.size() == 0) {
			getPlayerOwner().message("there are no available creatures to target");
			getPlayerOwner().resetCannonEvent();
			return;
		}
		
		Npc target = possibleTargets.get(DataConversions.random(0, possibleTargets.size() - 1));

		getPlayerOwner().face(target);
		int cannonBallDamage = DataConversions.random(0, 16);
		Server.getServer().getGameEventHandler().add(new ProjectileEvent(owner, target, cannonBallDamage, 5));
		getPlayerOwner().playSound("shoot");
		getPlayerOwner().getInventory().remove(1041, 1);

		if (!getPlayerOwner().getInventory().hasItemId(1041)) {
			getPlayerOwner().message("you're out of ammo");
			getPlayerOwner().resetCannonEvent();
			return;
		}
	}

}
