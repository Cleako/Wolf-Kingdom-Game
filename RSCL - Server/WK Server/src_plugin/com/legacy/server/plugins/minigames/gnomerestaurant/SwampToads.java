package com.legacy.server.plugins.minigames.gnomerestaurant;

import static com.legacy.server.plugins.Functions.*;

import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.GroundItem;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.InvActionListener;
import com.legacy.server.plugins.listeners.action.PickupListener;
import com.legacy.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.legacy.server.plugins.listeners.executive.PickupExecutiveListener;
import com.legacy.server.util.rsc.DataConversions;

public class SwampToads implements PickupListener, PickupExecutiveListener, InvActionListener, InvActionExecutiveListener {

	public static final int SWAMP_TOAD = 895;
	public static final int TOAD_LEGS = 896;
	
	@Override
	public boolean blockInvAction(Item item, Player p) {
		if(item.getID() == SWAMP_TOAD) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvAction(Item item, Player p) {
		if(item.getID() == SWAMP_TOAD) {
			message(p, 1900, "you pull the legs off the toad");
			p.message("poor toad..at least they'll grow back");
			p.getInventory().replace(item.getID(), TOAD_LEGS);
		}
	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		if(i.getID() == SWAMP_TOAD) {
			return true;
		}
		return false;
	}

	@Override
	public void onPickup(Player p, GroundItem i) {
		if(i.getID() == SWAMP_TOAD) {
			if(DataConversions.random(0, 10) >= 3) {
				p.message("you pick up the swamp toad");
				message(p, 1900, "but it jumps out of your hands..");
				p.message("..slippery little blighters");
			} else {
				message(p, 1900, "you pick up the swamp toad");
				i.remove();
				addItem(p, 895, 1);
				p.message("you just manage to hold onto it");
			}
		}
	}
}
