package com.legacy.server.model.entity.player;

import com.legacy.server.net.rsc.ActionSender;

public class Prayers {

	public static int THICK_SKIN = 0, BURST_OF_STRENGTH = 1, CLARITY_OF_THOUGHT = 2, ROCK_SKIN = 3,
			SUPERHUMAN_STRENGTH = 4, IMPROVED_REFLEXES = 5, RAPID_RESTORE = 6, RAPID_HEAL = 7, PROTECT_ITEMS = 8,
			STEEL_SKIN = 9, ULTIMATE_STRENGTH = 10, INCREDIBLE_REFLEXES = 11, PARALYZE_MONSTER = 12,
			PROTECT_FROM_MISSILES = 13;

	private Player player;

	public Prayers(Player player) {
		this.player = player;
	}

	private boolean[] activatedPrayers = new boolean[14];

	public boolean isPrayerActivated(int pID) {
		return activatedPrayers[pID];
	}

	public void setPrayer(int pID, boolean b) {
		activatedPrayers[pID] = b;
		ActionSender.sendPrayers(player, activatedPrayers);
	}

	public void resetPrayers() {
		for (int x = 0; x < activatedPrayers.length; x++) {
			if (activatedPrayers[x]) {
				activatedPrayers[x] = false;
			}
		}
		ActionSender.sendPrayers(player, activatedPrayers);
	}
}
