package com.legacy.server.plugins.listeners.action;

import com.legacy.server.model.entity.player.Player;

public interface CommandListener {
    public void onCommand(String command, String[] args, Player player);
}
