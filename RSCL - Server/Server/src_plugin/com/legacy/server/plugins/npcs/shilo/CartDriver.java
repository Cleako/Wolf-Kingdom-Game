package com.legacy.server.plugins.npcs.shilo;

import static com.legacy.server.plugins.Functions.*;

import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.ObjectActionListener;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class CartDriver implements TalkToNpcListener, TalkToNpcExecutiveListener, ObjectActionListener, ObjectActionExecutiveListener {

	public static final int CART_DRIVER = 618;
	public static final int TRAVEL_CART = 768;

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if(n.getID() == CART_DRIVER) {
			return true;
		}
		return false;
	}

	private void cartRide(Player p, Npc n) {
		npcTalk(p, n, "I am offering a cart ride to Brimhaven if you're interested!",
				"It will cost 300 Gp");
		int menu = showMenu(p, n,
				"Yes please, I'd like to go to Brimhaven!",
				"No thanks.");
		if(menu == 0) {
			if(hasItem(p, 10, 300)) {
				npcTalk(p, n, "Great!",
						"Just hop into the cart then and we'll go!");
				removeItem(p, 10, 300);
				message(p, 1000, "You Hop into the cart and the driver urges the horses on.");
				p.teleport(468, 662);
				message(p, 1200, "You take a taxing journey through the jungle to Brimhaven.");
				message(p, 1200, "You feel fatigued from the journey, but at least");
				message(p, 1200, "you didn't have to walk all that distance.");
			} else {
				npcTalk(p, n, "Sorry, but it looks as if you don't have enough money.",
						"Come back and see me when you have enough for the ride.");
			}
		} else if(menu == 1) {
			npcTalk(p, n, "Ok Bwana, let me know if you change your mind.");
		}
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(n.getID() == CART_DRIVER) {
			playerTalk(p, n, "Hello!");
			npcTalk(p, n, "Hello Bwana!");
			cartRide(p, n);
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == TRAVEL_CART) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == TRAVEL_CART) {
			if(command.equalsIgnoreCase("Board")) {
				p.message("This looks like a sturdy travelling cart.");
				Npc driver = getNearestNpc(p, CART_DRIVER, 10);
				if(driver != null) {
					driver.teleport(p.getX(), p.getY());
					sleep(600); // 1 tick.
					npcWalkFromPlayer(p, driver);
					p.message("A nearby man walks over to you.");
					cartRide(p, driver);
				} else {
					p.message("The cart driver is currently busy.");
				}
			} else if(command.equalsIgnoreCase("Look")) {
				p.message("A sturdy travelling cart built for long trips through jungle areas.");
			}
		}
	}
}
