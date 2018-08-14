package com.wk.server.plugins.listeners.executive;

import com.wk.server.model.entity.player.Player;

public interface WithdrawExecutiveListener {
    /**
     * Return true if you wish to prevent a user from withdrawing an item
     */
    public void blockWithdraw(Player p, int itemID, int amount);
}
