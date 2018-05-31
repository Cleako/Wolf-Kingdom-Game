package com.wk.server.plugins.misc;

import static com.wk.server.plugins.Functions.*;

import com.wk.server.model.container.Item;
import com.wk.server.model.entity.player.Player;
import com.wk.server.plugins.listeners.action.DropListener;
import com.wk.server.plugins.listeners.action.InvActionListener;
import com.wk.server.plugins.listeners.executive.DropExecutiveListener;
import com.wk.server.plugins.listeners.executive.InvActionExecutiveListener;

public class KittenToCat implements DropListener, DropExecutiveListener, InvActionListener, InvActionExecutiveListener {
	
	private static final int KITTEN = 1096;

	@Override
	public boolean blockDrop(Player p, Item i) {
		if(i.getID() == KITTEN) {
			return true;
		}
		return false;
	}

	@Override
	public void onDrop(Player p, Item i) {
		if(i.getID() == KITTEN) {
			removeItem(p, KITTEN, 1);
			message(p, 1200, "you drop the kitten");
			message(p, 0, "it's upset and runs away");
		}
	}

	@Override
	public boolean blockInvAction(Item item, Player p) {
		if(item.getID() == KITTEN) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvAction(Item item, Player p) {
		if(item.getID() == KITTEN) {
			message(p, "you softly stroke the kitten",
					"@yel@kitten:..purr..purr..");
			message(p, 600, "the kitten appreciates the attention");	
		}
	}
}
