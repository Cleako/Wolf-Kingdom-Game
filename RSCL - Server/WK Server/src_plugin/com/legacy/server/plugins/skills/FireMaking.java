package com.legacy.server.plugins.skills;

import static com.legacy.server.plugins.Functions.inArray;

import com.legacy.server.Server;
import com.legacy.server.event.SingleEvent;
import com.legacy.server.event.custom.BatchEvent;
import com.legacy.server.external.EntityHandler;
import com.legacy.server.external.FiremakingDef;
import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.GroundItem;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.entity.update.Bubble;
import com.legacy.server.model.world.World;
import com.legacy.server.plugins.listeners.action.InvUseOnGroundItemListener;
import com.legacy.server.plugins.listeners.action.InvUseOnItemListener;
import com.legacy.server.plugins.listeners.executive.InvUseOnGroundItemExecutiveListener;
import com.legacy.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;
import com.legacy.server.util.rsc.Formulae;

public class FireMaking implements InvUseOnGroundItemListener, InvUseOnGroundItemExecutiveListener, InvUseOnItemListener, InvUseOnItemExecutiveListener {

	/** LOG IDs **/
	public static int[] LOGS = { 14, 632, 633, 634, 635, 636 };
	public final static int TINDERBOX = 166;

	@Override
	public boolean blockInvUseOnGroundItem(Item myItem, GroundItem item, Player player) {
		if(myItem.getID() == TINDERBOX && inArray(item.getID(), LOGS)) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnGroundItem(Item myItem, GroundItem item, Player player) {
		switch (item.getID()) {
		case 14:
		case 632:
		case 633:
		case 634:
		case 635:
		case 636:
			handleFireMaking(item, player);
			break;
		default:
			player.message("Nothing interesting happens");
			return;
		}
	}
	
	private void handleFireMaking(final GroundItem gItem, Player player) {
		final FiremakingDef def = EntityHandler.getFiremakingDef(gItem.getID());
		
		if (def == null) {
			player.message("Nothing interesting happens");
			return;
		}
		
		if (player.getSkills().getLevel(11) < def.getRequiredLevel()) {
			player.message("You need at least " + def.getRequiredLevel() + " firemaking to light these logs");
			return;
		}
		
		if (player.getViewArea().getGameObject(gItem.getLocation()) != null) {
			player.message("You can't light a fire here");
			return;
		}
		
		player.getUpdateFlags().setActionBubble(new Bubble(player, TINDERBOX));
		player.message("You attempt to light the logs");
		
		player.setBatchEvent(new BatchEvent(player, 650, Formulae.getRepeatTimes(player, 11)) {
			@Override
			public void action() {
				if (Formulae.lightLogs(def, owner.getSkills().getLevel(11))) {
					owner.message("The fire catches and the logs begin to burn");
					World.getWorld().unregisterItem(gItem);
					
					final GameObject fire = new GameObject(gItem.getLocation(), 97, 0, 0);
					World.getWorld().registerGameObject(fire);
					
					Server.getServer().getEventHandler().add(
							new SingleEvent(null, def.getLength()) {
								@Override
								public void action() {
									if (fire != null) {
										World.getWorld().registerItem(new GroundItem(181,
												fire.getX(),
												fire.getY(),
												1, null));
										World.getWorld().unregisterGameObject(fire);
									}
								}
							});
					
					owner.incExp(11, Formulae.firemakingExp(owner.getSkills().getMaxStat(11), 25), true);
					interrupt();
					
				} else {
					owner.message("You fail to light a fire");
					owner.getUpdateFlags().setActionBubble(new Bubble(owner, TINDERBOX));
				}
			}
		});
	}

	@Override
	public boolean blockInvUseOnItem(Player player, Item item1, Item item2) {
		if(item1.getID() == TINDERBOX && inArray(item2.getID(), LOGS) || item2.getID() == TINDERBOX && inArray(item1.getID(), LOGS)) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnItem(Player player, Item item1, Item item2) {
		if(item1.getID() == TINDERBOX && inArray(item2.getID(), LOGS) || item2.getID() == TINDERBOX && inArray(item1.getID(), LOGS)) {
			player.message("I think you should put the logs down before you light them!");
		}
	}
}
