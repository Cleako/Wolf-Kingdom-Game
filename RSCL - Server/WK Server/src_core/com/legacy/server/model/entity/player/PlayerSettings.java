package com.legacy.server.model.entity.player;

import java.util.HashMap;

import com.legacy.server.model.PlayerAppearance;
import com.legacy.server.model.world.World;
import com.legacy.server.net.rsc.ActionSender;

public class PlayerSettings {

	public static final int PRIVACY_BLOCK_CHAT_MESSAGES = 0,
							PRIVACY_BLOCK_PRIVATE_MESSAGES = 1,
							PRIVACY_BLOCK_TRADE_REQUESTS = 2,
							PRIVACY_BLOCK_DUEL_REQUESTS = 3;

	public static final int GAME_SETTING_AUTO_CAMERA = 0,
							GAME_SETTING_MOUSE_BUTTONS = 1,
							GAME_SETTING_SOUND_EFFECTS = 2;
							
	private HashMap<Long, Long> attackedBy = new HashMap<Long, Long>();
	
	private boolean[] privacySettings = new boolean[4];
	private boolean[] gameSettings = new boolean[3];
	
	private PlayerAppearance appearance;
	
	private Player player;

	public PlayerSettings(Player player) {
		this.player = player;
	}
	

	public void setPrivacySetting(int i, boolean b) {
		if(i == 1) {
			if (privacySettings[1] && !b) {
				for (Player pl : World.getWorld().getPlayers()) {
					if (!player.getSocial().isFriendsWith(pl.getUsernameHash())
							&& pl.getSocial().isFriendsWith(player.getUsernameHash())
							&& pl.getIndex() != player.getIndex()) {
						ActionSender.sendFriendUpdate(pl, player.getUsernameHash(),
								0);
					}
				}
			}
			else if (!privacySettings[1] && b) {
				for (Player pl : World.getWorld().getPlayers()) {
					if (!player.getSocial().isFriendsWith(pl.getUsernameHash())
							&& pl.getSocial().isFriendsWith(player.getUsernameHash())
							&& pl.getIndex() != player.getIndex()) {
						ActionSender.sendFriendUpdate(pl, player.getUsernameHash(),
								99);
					}
				}
			}
		}
		privacySettings[i] = b;
	}

	public void setPrivacySettings(boolean[] privacySettings) {
		this.privacySettings = privacySettings;
	}

	public boolean getPrivacySetting(int i) {
		return privacySettings[i];
	}

	public boolean[] getPrivacySettings() {
		return privacySettings;
	}

	public boolean getGameSetting(int i) {
		return gameSettings[i];
	}

	public boolean[] getGameSettings() {
		return gameSettings;
	}
	public void setGameSetting(int i, boolean b) {
		gameSettings[i] = b;
	}

	public void setGameSettings(boolean[] gameSettings) {
		this.gameSettings = gameSettings;
	}

	public PlayerAppearance getAppearance() {
		return appearance;
	}
	public void addAttackedBy(Player p) {
		attackedBy.put(p.getUsernameHash(), System.currentTimeMillis());
	}
	public HashMap<Long, Long> getAttackedBy() {
		return attackedBy;
	}
	
	public long lastAttackedBy(Player p) {
		Long time = attackedBy.get(p.getUsernameHash());
		if (time != null) {
			return time;
		}
		return 0;
	}


	public void setAppearance(PlayerAppearance pa) {
		this.appearance = pa;
	}
}
