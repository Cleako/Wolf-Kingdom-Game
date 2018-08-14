package com.wk.server.plugins.listeners.executive;

import com.wk.server.external.SpellDef;
import com.wk.server.model.entity.GameObject;
import com.wk.server.model.entity.player.Player;

public interface PlayerMageObjectExecutiveListener {
    public boolean blockPlayerMageObject(Player player, GameObject obj, SpellDef spell);
}

