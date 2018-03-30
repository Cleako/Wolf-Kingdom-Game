package com.legacy.server.event.rsc.impl;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.legacy.server.event.rsc.GameTickEvent;
import com.legacy.server.external.EntityHandler;
import com.legacy.server.external.PrayerDef;
import com.legacy.server.model.Skills;
import com.legacy.server.model.entity.player.Player;

public class PrayerDrainEvent extends GameTickEvent {
	
	public PrayerDrainEvent(Player owner, int delay) {
		super(owner, 1);
	}

	private ConcurrentHashMap<PrayerDef, Long> activePrayers = new ConcurrentHashMap<PrayerDef, Long>();
	
	private double partialPoints = 0.0;
	
	@Override
	public void run() {
		refreshActivePrayers();
		for(Entry<PrayerDef, Long> entry : activePrayers.entrySet()) {
			PrayerDef def = entry.getKey();
			long lastDrain = entry.getValue();
			int drainDelay = (int) (180000.0 / def.getDrainRate() * (1 + getPlayerOwner().getPrayerPoints() / 30.0));
			if(System.currentTimeMillis() - lastDrain >= drainDelay) {
				entry.setValue(System.currentTimeMillis());
				drainPrayer();
			}
		}
		if(partialPoints >= 1.0) {
			drainPrayer();
			partialPoints = 0.0;
		}
	}
	
	private void drainPrayer() {
		if (getPlayerOwner().getSkills().getLevel(Skills.PRAYER) > 0) {
			getPlayerOwner().getSkills().decrementLevel(Skills.PRAYER);
		} else {
			getPlayerOwner().getPrayers().resetPrayers();
			activePrayers.clear();
			getPlayerOwner().message("You have run out of prayer points. Return to a church to recharge");
		}
	}

	private void refreshActivePrayers() {
		for (int x = 0; x <= 13; x++) {
			PrayerDef prayer = EntityHandler.getPrayerDef(x);
			if (getPlayerOwner().getPrayers().isPrayerActivated(x) && !activePrayers.containsKey(prayer)) {
				activePrayers.put(prayer, System.currentTimeMillis());
			}
			else if(!getPlayerOwner().getPrayers().isPrayerActivated(x) && activePrayers.containsKey(prayer)) {
				double timePrayerUsed = System.currentTimeMillis() - activePrayers.get(prayer).longValue();
				double drainDelay = (180000 / (prayer.getDrainRate() * (1 + getPlayerOwner().getPrayerPoints() / 30.0)));
				partialPoints += timePrayerUsed / drainDelay;
				activePrayers.remove(prayer);
			}
			if(getPlayerOwner().getPrayers().isPrayerActivated(x) && (getPlayerOwner().getSkills().getLevel(Skills.PRAYER) < 1)) {
				getPlayerOwner().getPrayers().resetPrayers();
				getPlayerOwner().message("You have run out of prayer points. Return to a church to recharge");
				activePrayers.clear();
				break;
			}
		}
	}
}
