package com.wk.server.plugins.misc;

import static com.wk.server.plugins.Functions.*;

import com.wk.server.model.entity.GameObject;
import com.wk.server.model.entity.player.Player;
import com.wk.server.plugins.listeners.action.ObjectActionListener;
import com.wk.server.plugins.listeners.executive.ObjectActionExecutiveListener;

public class BananaTree implements ObjectActionExecutiveListener,
ObjectActionListener {

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == 183) {
			p.message("you pick a banana");
			addItem(p, 249, 1);
			if(!p.getCache().hasKey("banana_pick")) {
				p.getCache().set("banana_pick", 1);
			} else {
				int bananaCount = p.getCache().getInt("banana_pick");
				p.getCache().set("banana_pick", (bananaCount + 1));
				if(bananaCount >= 4) {
					replaceObjectDelayed(obj, 60000 * 8, 184); // 8 minutes respawn time.
					p.getCache().remove("banana_pick");
				}
			}

		}
		if(obj.getID() == 184) {
			p.message("there are no bananas left on the tree");
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		return obj.getID() == 183 || obj.getID() == 184;
	}

}
