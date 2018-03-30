package com.legacy.server.plugins.listeners.action;

import com.legacy.server.external.SpellDef;
import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.player.Player;

public interface PlayerMageObjectListener {
    public void onPlayerMageObject(Player player, GameObject obj, SpellDef spell);
}
