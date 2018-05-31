package com.wk.server.plugins.listeners.action;

import com.wk.server.model.entity.player.Player;

public interface CommandListener {
    public void onCommand(String command, String[] args, Player player);
}
