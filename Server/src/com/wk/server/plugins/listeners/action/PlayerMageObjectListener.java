package com.wk.server.plugins.listeners.action;

import com.wk.server.external.SpellDef;
import com.wk.server.model.entity.GameObject;
import com.wk.server.model.entity.player.Player;

public interface PlayerMageObjectListener {
    public void onPlayerMageObject(Player player, GameObject obj, SpellDef spell);
}
