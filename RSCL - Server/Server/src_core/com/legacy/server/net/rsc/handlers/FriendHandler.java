package com.legacy.server.net.rsc.handlers;

import com.legacy.server.Constants;
import com.legacy.server.model.PrivateMessage;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.world.World;
import com.legacy.server.net.Packet;
import com.legacy.server.net.rsc.ActionSender;
import com.legacy.server.net.rsc.OpcodeIn;
import com.legacy.server.net.rsc.PacketHandler;
import com.legacy.server.util.rsc.DataConversions;

public final class FriendHandler implements PacketHandler {

	private final int MAX_FRIENDS = 100;

	private final int MEMBERS_MAX_FRIENDS = 200;

	public void handlePacket(Packet p, Player player) throws Exception {
		int pID = p.getID();
		
		long friend = DataConversions.usernameToHash(p.readString());

		int packetOne = OpcodeIn.SOCIAL_ADD_FRIEND.getOpcode();
		int packetTwo = OpcodeIn.SOCIAL_REMOVE_FRIEND.getOpcode();
		int packetThree = OpcodeIn.SOCIAL_ADD_IGNORE.getOpcode();
		int packetFour = OpcodeIn.SOCIAL_REMOVE_IGNORE.getOpcode();
		int packetFive = OpcodeIn.SOCIAL_SEND_PRIVATE_MESSAGE.getOpcode();
		
		Player affectedPlayer = World.getWorld().getPlayer(friend);
		if (pID == packetOne) { // Add friend
			int maxFriends = Constants.GameServer.MEMBER_WORLD ? MEMBERS_MAX_FRIENDS
					: MAX_FRIENDS;
			if (player.getSocial().friendCount() >= maxFriends) {
				player.message("Friend list is full");
				ActionSender.sendFriendList(player);
				return;
			}
//			loginSender.addFriend(user, friend);
			player.getSocial().addFriend(friend, 0, DataConversions.hashToUsername(friend));
			ActionSender.sendFriendUpdate(player, friend, 0);
			if(affectedPlayer != null && affectedPlayer.loggedIn()) {
				if(affectedPlayer.getSocial().isFriendsWith(player.getUsernameHash())) {
					ActionSender.sendFriendUpdate(affectedPlayer, player.getUsernameHash(), 99);
					ActionSender.sendFriendUpdate(player, friend, 99);
				}
				else if(!affectedPlayer.getSettings().getPrivacySetting(1)) {
					ActionSender.sendFriendUpdate(player, friend, 99);
				}
			}
		} else if (pID == packetTwo) { // Remove friend
			player.getSocial().removeFriend(friend);
			if(affectedPlayer != null && affectedPlayer.loggedIn()) {
				if(player.getSettings().getPrivacySetting(1) && affectedPlayer.getSocial().isFriendsWith(player.getUsernameHash())) {
					ActionSender.sendFriendUpdate(affectedPlayer, player.getUsernameHash(), 0);
				}
			}
		} else if (pID == packetThree) { // Add ignore
			int maxFriends = Constants.GameServer.MEMBER_WORLD ? MEMBERS_MAX_FRIENDS
					: MAX_FRIENDS;
			if (player.getSocial().ignoreCount() >= maxFriends) {
				player.message("Ignore list full");
				return;
			}
			player.getSocial().addIgnore(friend, 0);
			ActionSender.sendIgnoreList(player);
		} else if (pID == packetFour) { // Remove ignore
			player.getSocial().removeIgnore(friend);
		} else if (pID == packetFive) { // Send PM
			String message = DataConversions.getEncryptedString(p, 32576);
			player.addPrivateMessage(new PrivateMessage(player, message, friend));
		}
	}

}
