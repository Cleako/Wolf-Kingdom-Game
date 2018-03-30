package com.legacy.server.event.custom;

import com.legacy.server.event.DelayedEvent;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.net.rsc.ActionSender;

public abstract class BatchEvent extends DelayedEvent {

	private int repeatFor;
	private int repeated;
	
	public BatchEvent(Player owner, int delay, int repeatFor) {
		super(owner, delay);
		this.repeatFor = repeatFor;
		ActionSender.sendProgressBar(owner, delay, repeatFor);
		owner.setBusyTimer(delay + 200);
	}

	@Override
	public void run() {
		if (repeated < getRepeatFor()) {
			owner.setBusyTimer(delay + 200);
			action();
			repeated++;
			if(repeated < getRepeatFor()) {
				ActionSender.sendProgress(owner, repeated);
			} else {
				interrupt();
			}
		}
	}

	public abstract void action();
	
	public boolean isCompleted() {
		return (repeated + 1) >= getRepeatFor() || !matchRunning;
	}
	
	public void interrupt() {
		ActionSender.sendRemoveProgressBar(owner);
		owner.setBusyTimer(0);
		owner.setBatchEvent(null);
		matchRunning = false;
	}

	public int getRepeatFor() {
		return repeatFor;
	}
	
	public void setRepeatFor(int i) {
		repeatFor = i;
	}
}
