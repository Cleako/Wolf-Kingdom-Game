package com.legacy.server.plugins.listeners.action;

import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;

public interface PlayerNpcRunListener {
	 /**
     * Called when a player runs from npc
     *
     * @param p
     * @param n
     */
	 public void onPlayerNpcRun(Player p, Npc n);
}
