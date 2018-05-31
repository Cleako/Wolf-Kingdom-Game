package com.wk.server.model;

import com.wk.server.model.entity.Mob;
import com.wk.server.model.entity.player.Player;
import com.wk.server.net.rsc.ActionSender;
import com.wk.server.sql.GameLogging;
import com.wk.server.sql.query.logs.LiveFeedLog;
import com.wk.server.util.rsc.Formulae;

public class Skills {

	private static final int LEVEL_LIMIT = 1000;
	public static long[] experienceArray;

	public static final int SKILL_COUNT = 18;

	public static final double MAXIMUM_EXP = 200000000;

	public static final String[] SKILL_NAME = { "attack", "defense", "strength", "hits", "ranged", "prayer", "magic",
			"cooking", "woodcut", "fletching", "fishing", "firemaking", "crafting", "smithing", "mining", "herblaw",
			"agility", "thieving" };

	public static final int ATTACK = 0, DEFENCE = 1, STRENGTH = 2, HITPOINTS = 3, RANGE = 4, PRAYER = 5, MAGIC = 6,
			COOKING = 7, WOODCUTTING = 8, FLETCHING = 9, FISHING = 10, FIREMAKING = 11, CRAFTING = 12, SMITHING = 13,
			MINING = 14, HERBLORE = 15, AGILITY = 16, THIEVING = 17, SLAYER = 18, FARMING = 19, RUNECRAFTING = 20;

	private Mob mob;

	private int[] levels = new int[SKILL_COUNT];
	private double[] exps = new double[SKILL_COUNT];

	/**
	 * Creates a skills object.
	 * 
	 * @param mob
	 *            The player whose skills this object represents.
	 */
	public Skills(Mob mob) {
		this.mob = mob;
		for (int i = 0; i < SKILL_COUNT; i++) {
			levels[i] = 1;
			exps[i] = 0;
		}
		levels[3] = 10;
		exps[3] = 1154;
	}

	/**
	 * Gets the total level.
	 * 
	 * @return The total level.
	 */
	public int getTotalLevel() {
		int total = 0;
		for (int i = 0; i < levels.length; i++) {
			total += getMaxStat(i);
		}
		return total;
	}

	public int getCombatLevel() {
		return Formulae.getCombatlevel(getMaxStats());
	}

	public void setSkill(int skill, int level, double exp) {
		levels[skill] = level;
		exps[skill] = exp;
		sendUpdate(skill);
	}

	public void setLevel(int skill, int level) {
		levels[skill] = level;
		if (levels[skill] <= 0) {
			levels[skill] = 0;
		}
		sendUpdate(skill);
	}

	public void setExperience(int skill, double exp) {
		int oldLvl = getMaxStat(skill);
		exps[skill] = exp;
		int newLvl = getMaxStat(skill);
		if (oldLvl != newLvl) {
			mob.getUpdateFlags().setAppearanceChanged(true);
		}
		sendUpdate(skill);
	}

	public void incrementLevel(int skill) {
		levels[skill]++;
		sendUpdate(skill);
	}

	public void decrementLevel(int skill) {
		levels[skill]--;
		if (levels[skill] <= 0)
			levels[skill] = 0;

		sendUpdate(skill);
	}

	public void increaseLevel(int skill, int amount) {
		if (levels[skill] == 0) {
			amount = 0;
		}
		if (amount > levels[skill]) {
			amount = levels[skill];
		}
		levels[skill] = levels[skill] + amount;
		sendUpdate(skill);
	}

	public void subtractLevel(int skill, int amount) {
		subtractLevel(skill, amount, true);
	}

	public void subtractLevel(int skill, int amount, boolean update) {
		levels[skill] = levels[skill] - amount;
		if (levels[skill] <= 0) {
			levels[skill] = 0;
		}

		if (update)
			sendUpdate(skill);
	}


	public int getLevel(int skill) {
		return levels[skill];
	}

	public static int getLevelForExperience(double experience, int limit) {
		for (int level = 0; level < limit - 1; level++) {
			if (experience >= experienceArray[level])
				continue;
			return (level + 1);
		}
		return limit;
	}

	public static double experienceForLevel(int level) {
		int lvlArrayIndex = level - 2;
		if (lvlArrayIndex == -1)
			return 0;
		if (lvlArrayIndex < 0 || lvlArrayIndex > experienceArray.length)
			return 0;
		return experienceArray[lvlArrayIndex];
	}

	public double getExperience(int skill) {
		return exps[skill];
	}

	public void addExperience(int skill, double exp) {
		int oldLevel = getMaxStat(skill);
		exps[skill] += exp;
		if (exps[skill] > MAXIMUM_EXP) {
			exps[skill] = MAXIMUM_EXP;
		}
		int newLevel = getMaxStat(skill);
		int levelDiff = newLevel - oldLevel;

		if (levelDiff > 0) {
			levels[skill] += levelDiff;
			// TODO: Maybe a level up listener?
			if (mob.isPlayer()) {
				Player player = (Player) mob;
				if (newLevel >= 90 && newLevel <= 98) {
					GameLogging.addQuery(new LiveFeedLog(player,
							"has achieved level-" + newLevel + " in " + SKILL_NAME[skill] + "!"));
				} else if (newLevel == 99) {
					GameLogging.addQuery(new LiveFeedLog(player, "has achieved the maximum level of " + newLevel
							+ " in " + SKILL_NAME[skill] + ", congratulations!"));
				}
				player.message("@gre@You just advanced " + levelDiff + " " + SKILL_NAME[skill] + " level"
						+ (levelDiff > 1 ? "s" : "") + "!");
				ActionSender.sendSound((Player) mob, "advance");
			}

			mob.getUpdateFlags().setAppearanceChanged(true);
		}

		sendUpdate(skill);
	}

	public void sendUpdate(int skill) {
		if (mob.isPlayer()) {
			Player player = (Player) mob;
			ActionSender.sendStat(player, skill);
		}
	}

	public int[] getMaxStats() {
		int[] maxStats = new int[SKILL_COUNT];
		for (int skill = 0; skill < maxStats.length; skill++) {
			maxStats[skill] = getMaxStat(skill);
		}
		return maxStats;
	}

	public int getMaxStat(int skill) {
		return getLevelForExperience(getExperience(skill), mob instanceof Player ? 99 : LEVEL_LIMIT);
	}

	public void normalize() {
		for (int i = 0; i < 18; i++) {
			levels[i] = getMaxStat(i);
		}
		if (mob.isPlayer()) {
			ActionSender.sendStats((Player) mob);
		}
	}

	public void setLevelTo(int skill, int level) {
		exps[skill] = experienceForLevel(level);
		levels[skill] = level;
	}

	public int[] getLevels() {
		return levels;
	}

	public double[] getExperiences() {
		return exps;
	}

	public void loadExp(double[] xp) {
		this.exps = xp;
	}

	public void loadLevels(int[] lv) {
		this.levels = lv;
	}

	static {
		long i = 0;
		experienceArray = new long[LEVEL_LIMIT + 5];
		for (int j = 0; j < LEVEL_LIMIT + 5; j++) {
			int k = j + 1;
			long i1 = (long) ((double) k + 300D * Math.pow(2D, (double) k / 7D));
			i += i1;
			experienceArray[j] = (i & 0xfffffffc) / 4;
		}
	}
}