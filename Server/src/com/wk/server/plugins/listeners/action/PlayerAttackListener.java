package com.wk.server.plugins.listeners.action;

import com.wk.server.model.entity.player.Player;

public interface PlayerAttackListener {

    public void onPlayerAttack(Player p, Player affectedmob);
}
