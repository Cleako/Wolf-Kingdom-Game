package com.legacy.server.plugins.quests.members.legendsquest.obstacles;

import static com.legacy.server.plugins.Functions.*;

import com.legacy.server.model.Skills;
import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.ObjectActionListener;
import com.legacy.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.legacy.server.plugins.quests.members.shilovillage.ShiloVillageUtils;
import com.legacy.server.util.rsc.DataConversions;
import com.legacy.server.util.rsc.Formulae;

public class LegendsQuestCaveAgility implements ObjectActionListener, ObjectActionExecutiveListener {

	public static final int ROCK_HEWN_STAIRS_1 = 1114;
	public static final int ROCK_HEWN_STAIRS_2 = 1123;
	public static final int ROCK_HEWN_STAIRS_3 = 1124;
	public static final int ROCK_HEWN_STAIRS_4 = 1125;

	public static final int ROCKY_WALKWAY_1 = 558;
	public static final int ROCKY_WALKWAY_2 = 559;
	public static final int ROCKY_WALKWAY_3 = 560;
	public static final int ROCKY_WALKWAY_4 = 561;

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		return inArray(obj.getID(), ROCK_HEWN_STAIRS_1, ROCK_HEWN_STAIRS_2, ROCK_HEWN_STAIRS_3, ROCK_HEWN_STAIRS_4, ROCKY_WALKWAY_1, ROCKY_WALKWAY_2, ROCKY_WALKWAY_3, ROCKY_WALKWAY_4);
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		p.setBusy(true);
		switch (obj.getID()) {
		case ROCKY_WALKWAY_1:
		case ROCKY_WALKWAY_2:
		case ROCKY_WALKWAY_3:
		case ROCKY_WALKWAY_4:
			if(p.getX() == obj.getX() && p.getY() == obj.getY()) {
				p.message("You're standing there already!");
				p.setBusy(false);
				return;
			}
			if(Formulae.failCalculation(p, Skills.AGILITY, 50)) {
				p.message("You manage to keep your balance.");
				p.teleport(obj.getX(), obj.getY());
				p.incExp(AGILITY, 5.0, true);
			} else {
				p.teleport(421, 3699);
				p.message("You slip and fall...");
				int failScene = DataConversions.random(0, 10);
				if(failScene == 3) {
					p.message("...but you luckily avoid any damage.");
				} else if(failScene == 5) {
					p.damage(31);
					p.message("...and take some major damage.");
				} else {
					p.damage(DataConversions.random(3, 25));
					p.message("...and take damage.");
				}
				p.incExp(AGILITY, 1.25, true);
			}
			break;
		case ROCK_HEWN_STAIRS_4:
			if(getCurrentLevel(p, AGILITY) < 50) {
				p.message("You need an agility level of 50 to step these stairs");
				p.setBusy(false);
				return;
			}
			if(Formulae.failCalculation(p, Skills.AGILITY, 50)) {
				if(p.getX() <= 419) {
					p.message("You climb down the steps.");
					p.teleport(421, 3707);
					sleep(600);
					p.incExp(AGILITY, 5.0, true);
					p.teleport(423, 3707);
				} else {
					p.message("You climb up the stairs.");
					p.teleport(421, 3707);
					sleep(600);
					p.incExp(AGILITY, 5.0, true);
					p.teleport(419, 3707);
				}
			} else {
				p.message("You slip and fall...");
				p.damage(3);
				p.teleport(421, 3707);
				sleep(600);
				p.incExp(AGILITY, 1.25, true);
				p.teleport(423, 3707);
			}
			break;
		case ROCK_HEWN_STAIRS_3:
			if(getCurrentLevel(p, AGILITY) < 50) {
				p.message("You need an agility level of 50 to step these stairs");
				p.setBusy(false);
				return;
			}
			if(Formulae.failCalculation(p, Skills.AGILITY, 50)) {
				if(p.getY() <= 3702) {
					p.message("You climb down the steps.");
					p.teleport(419, 3704);
					sleep(600);
					p.incExp(AGILITY, 5.0, true);
					p.teleport(419, 3706);
				} else {
					p.message("You climb up the stairs.");
					p.teleport(419, 3704);
					sleep(600);
					p.incExp(AGILITY, 5.0, true);
					p.teleport(419, 3702);
				}
			} else {
				p.message("You slip and fall...");
				p.damage(3);
				p.teleport(419, 3704);
				sleep(600);
				p.incExp(AGILITY, 1.25, true);
				p.teleport(419, 3706);
			}
			break;
		case ROCK_HEWN_STAIRS_2:
			if(getCurrentLevel(p, AGILITY) < 50) {
				p.message("You need an agility level of 50 to step these stairs");
				p.setBusy(false);
				return;
			}
			if(Formulae.failCalculation(p, Skills.AGILITY, 50)) {
				if(p.getX() >= 426) {
					p.message("You climb down the steps.");
					p.teleport(424, 3702);
					sleep(600);
					p.incExp(AGILITY, 5.0, true);
					p.teleport(422, 3702);
				} else {
					p.message("You climb up the stairs.");
					p.teleport(424, 3702);
					sleep(600);
					p.incExp(AGILITY, 5.0, true);
					p.teleport(426, 3702);
				}
			} else {
				p.message("You slip and fall...");
				p.damage(3);
				p.teleport(424, 3702);
				sleep(600);
				p.incExp(AGILITY, 1.25, true);
				p.teleport(422, 3702);
			}
			break;
		case ROCK_HEWN_STAIRS_1:
			if(getCurrentLevel(p, AGILITY) < 50) {
				p.message("You need an agility level of 50 to step these stairs");
				p.setBusy(false);
				return;
			}
			if(Formulae.failCalculation(p, Skills.AGILITY, 50)) {
				if(p.getY() >= 3706) {
					p.message("You climb down the steps.");
					p.teleport(426, 3704);
					sleep(600);
					p.incExp(AGILITY, 5.0, true);
					p.teleport(426, 3702);
				} else {
					p.message("You climb up the stairs.");
					p.teleport(426, 3704);
					sleep(600);
					p.incExp(AGILITY, 5.0, true);
					p.teleport(426, 3706);
				}
			} else {
				p.message("You slip and fall...");
				p.damage(3);
				p.teleport(426, 3704);
				sleep(600);
				p.incExp(AGILITY, 1.25, true);
				p.teleport(426, 3702);
			}
			break;
		}
		p.setBusy(false);
	}
}
