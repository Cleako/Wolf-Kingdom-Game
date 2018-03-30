package com.legacy.server.plugins.npcs;

import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.showMenu;

import com.legacy.server.external.CerterDef;
import com.legacy.server.external.EntityHandler;
import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.legacy.server.util.rsc.DataConversions;

public class Certer implements TalkToNpcListener, TalkToNpcExecutiveListener {

	int[] certers = new int[] { 225, 226, 227, 466, 467, 299, 341, 369,
			370, 348, 267};

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		final CerterDef certerDef = EntityHandler.getCerterDef(n.getID());
		if (certerDef == null) {
			return;
		}
		final String[] names = certerDef.getCertNames();
		npcTalk(p, n, "Welcome to my " + certerDef.getType()
		+ " exchange stall");
		int option = showMenu(p, n, "I have some certificates to trade in",
				"I have some " + certerDef.getType() + " to trade in");
		switch (option) {
		case 0:
			p.message("What sort of certificate do you wish to trade in?");
			int index = showMenu(p, n, names);
			p.message("How many certificates do you wish to trade in?");
			int certAmount = showMenu(p, n, "One", "Two", "Three", "Four",
					"Five", "All to bank");
			int certID = certerDef.getCertID(index);
			if (certID < 0) {
				return;
			}
			int itemID = certerDef.getItemID(index);
			if (certAmount == 5) {
				if(p.isIronMan(2)) {
					p.message("As an Ultimate Iron Man. you cannot use certer to bank.");
					return;
				}
				certAmount = p.getInventory().countId(certID);
				if (certAmount <= 0) {
					p.message("You don't have any " + names[index]
							+ " certificates");
					return;
				}
				Item bankItem = new Item(itemID, certAmount * 5);
				if (p.getInventory().remove(new Item(certID, certAmount)) > -1) {
					p.message("You exchange the certificates, "
							+ bankItem.getAmount() + " "
							+ bankItem.getDef().getName()
							+ " is added to your bank");
					p.getBank().add(bankItem);
				}
			} else {
				certAmount += 1;
				int itemAmount = certAmount * 5;
				if (p.getInventory().countId(certID) < certAmount) {
					p.message("You don't have that many certificates");
					return;
				}
				if (p.getInventory().remove(certID, certAmount) > -1) {
					p.message("You exchange the certificates for "
							+ certerDef.getType() + ".");
					for (int x = 0; x < itemAmount; x++) {
						p.getInventory().add(new Item(itemID, 1));
					}
				}
			}
			break;
		case 1:
			p.message("What sort of " + certerDef.getType()
			+ " do you wish to trade in?");
			index = showMenu(p, n, names);
			p.message("How many " + certerDef.getType()
			+ " do you wish to trade in?");
			certAmount = showMenu(p, n, "5", "10", "15", "20", "25",
					"All from bank");
			certID = certerDef.getCertID(index);
			if (certID < 0) {
				return;
			}
			itemID = certerDef.getItemID(index);
			if (certAmount == 5) {
				if(p.isIronMan(2)) {
					p.message("As an Ultimate Iron Man. you cannot use certer to bank.");
					return;
				}
				certAmount = (int) (p.getBank().countId(itemID) / 5);
				int itemAmount = certAmount * 5;
				if (itemAmount <= 0) {
					p.message("You don't have any " + names[index] + " to cert");
					return;
				}
				if (p.getBank().remove(itemID, itemAmount) > -1) {
					p.message("You exchange the " + certerDef.getType() + ", "
							+ itemAmount + " "
							+ EntityHandler.getItemDef(itemID).getName()
							+ " is taken from your bank");
					p.getInventory().add(new Item(certID, certAmount));
				}
			} else {
				certAmount += 1;
				int itemAmount = certAmount * 5;
				if (p.getInventory().countId(itemID) < itemAmount) {
					p.message("You don't have that many " + certerDef.getType());
					return;
				}
				p.message("You exchange the " + certerDef.getType()
				+ " for certificates.");
				for (int x = 0; x < itemAmount; x++) {
					p.getInventory().remove(itemID, 1);
				}
				p.getInventory().add(new Item(certID, certAmount));
			}
			break;
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return DataConversions.inArray(certers, n.getID());
	}
}
