package com.legacy.server.plugins.listeners.action;

import com.legacy.server.model.entity.player.Player;

public interface PlayerAttackListener {

    public void onPlayerAttack(Player p, Player affectedmob);
}
