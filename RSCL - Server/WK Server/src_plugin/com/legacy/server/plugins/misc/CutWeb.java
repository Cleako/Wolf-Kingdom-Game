package com.legacy.server.plugins.misc;

import static com.legacy.server.plugins.Functions.delayedSpawnObject;
import static com.legacy.server.plugins.Functions.message;
import static com.legacy.server.plugins.Functions.removeObject;

import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.InvUseOnWallObjectListener;
import com.legacy.server.plugins.listeners.executive.InvUseOnWallObjectExecutiveListener;
import com.legacy.server.util.rsc.Formulae;

public class CutWeb implements InvUseOnWallObjectListener, InvUseOnWallObjectExecutiveListener {

	public static int WEB = 24;

	@Override
	public boolean blockInvUseOnWallObject(GameObject obj, Item item, Player p) {
		if(obj.getID() == WEB) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnWallObject(GameObject obj, Item item, Player p) {
		if(obj.getID() == WEB) {
			if(item.getDef().getWieldPosition() != 4 && item.getID() != 13) {
				p.message("Nothing interesting happens");
				return;
			}	
			message(p, "You try to destroy the web...");
			if (Formulae.cutWeb()) {
				p.message("You slice through the web");
				removeObject(obj);
				delayedSpawnObject(obj.getLoc(), 30000);
			} else {
				p.message("You fail to cut through it");
			}
		}
	}
}
