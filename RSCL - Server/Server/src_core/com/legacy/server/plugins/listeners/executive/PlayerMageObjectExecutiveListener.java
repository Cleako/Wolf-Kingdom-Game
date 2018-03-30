package com.legacy.server.plugins.listeners.executive;

import com.legacy.server.external.SpellDef;
import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.player.Player;

public interface PlayerMageObjectExecutiveListener {
    public boolean blockPlayerMageObject(Player player, GameObject obj, SpellDef spell);
}

