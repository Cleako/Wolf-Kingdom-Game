package com.wk.server;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wk.server.model.PlayerAppearance;
import com.wk.server.model.Point;
import com.wk.server.model.PrivateMessage;
import com.wk.server.model.entity.Entity;
import com.wk.server.model.entity.GameObject;
import com.wk.server.model.entity.GroundItem;
import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.entity.player.Player;
import com.wk.server.model.entity.player.PlayerSettings;
import com.wk.server.model.entity.update.Bubble;
import com.wk.server.model.entity.update.ChatMessage;
import com.wk.server.model.entity.update.Damage;
import com.wk.server.model.entity.update.Projectile;
import com.wk.server.model.entity.update.UpdateFlags;
import com.wk.server.model.world.World;
import com.wk.server.model.world.region.Region;
import com.wk.server.model.world.region.RegionManager;
import com.wk.server.net.PacketBuilder;
import com.wk.server.net.rsc.ActionSender;
import com.wk.server.sql.GameLogging;
import com.wk.server.sql.query.logs.PMLog;
import com.wk.server.util.EntityList;
import com.wk.server.util.rsc.DataConversions;

/**
 * 
 * @author n0m
 *
 */
public final class GameStateUpdater {


	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final EntityList<Player> players = World.getWorld().getPlayers();
	private final EntityList<Npc> npcs = World.getWorld().getNpcs();

	public void doUpdates() throws Exception {
		processPlayers();
		processNpcs();
		processMessageQueues();
		updateClients();
		doCleanup();
		executeWalkToActions();
		/*final int HORIZONTAL_PLANES = (World.MAX_WIDTH / RegionManager.REGION_SIZE) + 1;
		final int VERTICAL_PLANES = (World.MAX_HEIGHT / RegionManager.REGION_SIZE) + 1;
		for (int x = 0; x < HORIZONTAL_PLANES; ++x)
			for (int y = 0; y < VERTICAL_PLANES; ++y) {
				Region r = RegionManager.getRegion(x * RegionManager.REGION_SIZE, y * RegionManager.REGION_SIZE);
				if (r != null)
					for (Iterator<Player> i = r.getPlayers().iterator(); i.hasNext();) {
						if (i.next().isRemoved())
							i.remove();
					}
			}*/
	}

	public void updateClients() {
		for (Player p : players) {
			sendUpdatePackets(p);
			p.process();
		}
	}

	public void doCleanup() {// it can do the teleport at this time.
		/*
		 * Reset the update related flags and unregister npcs flagged as
		 * unregistering
		 */

		for (Npc npc : npcs) {
			npc.resetMoved();
			npc.resetSpriteChanged();
			npc.getUpdateFlags().reset();
			npc.setTeleporting(false);
		}
		/*
		 * Reset the update related flags and unregister players that are
		 * flagged as unregistered
		 */
		Iterator<Player> playerListIterator = players.iterator();
		while (playerListIterator.hasNext()) {
			Player player = playerListIterator.next();
			player.setTeleporting(false);
			player.resetSpriteChanged();
			player.getUpdateFlags().reset();
			player.resetMoved();
		}
	}

	public void executeWalkToActions() {
		for (Player p : players) {
			if (p.getWalkToAction() != null) {
				if (p.getWalkToAction().shouldExecute()) {
					p.getWalkToAction().execute();
					p.setWalkToAction(null);
				}
			}
		}
	}


	// private static final int PACKET_UPDATETIMEOUTS = 0;
	public static void sendUpdatePackets(Player p) {
		try {
			updatePlayers(p);
			updatePlayerAppearances(p);
			updateNpcs(p);
			updateNpcAppearances(p);
			updateGameObjects(p);
			updateWallObjects(p);
			updateGroundItems(p);
			sendClearLocations(p);
			updateTimeouts(p);
		} catch (Exception e) {
			LOGGER.catching(e);
			p.unregister(true, "Exception while updating player " + p.getUsername());
		}
	}

	private void processNpcs() {
		for (Npc n : npcs) {
			try {
				if (n.isUnregistering()) {
					World.getWorld().unregisterNpc(n);
					continue;
				}
				n.updatePosition();
			} catch (Exception e) {
				LOGGER.error(
						"Error while updating " + n + " at position " + n.getLocation() + " loc: " + n.getLoc());
				LOGGER.catching(e);
			}
		}
	}

	/**
	 * Updates the messages queues for each player
	 */
	private void processMessageQueues() {
		for (Player p : players) {
			PrivateMessage pm = p.getNextPrivateMessage();
			if (pm != null) {
				Player affectedPlayer = World.getWorld().getPlayer(pm.getFriend());
				if (affectedPlayer != null) {
					if ((affectedPlayer.getSocial().isFriendsWith(p.getUsernameHash()) || !affectedPlayer.getSettings()
							.getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES))
							&& !affectedPlayer.getSocial().isIgnoring(p.getUsernameHash()) || p.isMod()) {
						ActionSender.sendPrivateMessageSent(p, affectedPlayer.getUsernameHash(), pm.getMessage());
						ActionSender.sendPrivateMessageReceived(affectedPlayer, p, pm.getMessage());
					}

					GameLogging.addQuery(new PMLog(p.getUsername(), pm.getMessage(),
							DataConversions.hashToUsername(pm.getFriend())));
				}
			}
		}
		for (Player p : players) {
			if (p.requiresOfferUpdate()) {
				ActionSender.sendTradeItems(p);
				p.setRequiresOfferUpdate(false);
			}
		}
	}

	/**
	 * Update the position of players, and check if who (and what) they are
	 * aware of needs updated
	 */
	private void processPlayers() {
		for (Player p : players) {
			if (p.isUnregistering()) {
				World.getWorld().unregisterPlayer(p);
				continue;
			}
			p.updatePosition();
			if (p.getUpdateFlags().hasAppearanceChanged()) {
				p.incAppearanceID();
			}
		}
	}

	/**
	 * Checks the player has moved within the last 5mins
	 */
	private static void updateTimeouts(Player player) {
		if (player.isRemoved() || player.getAttribute("dummyplayer", false)) {
			return;
		}
		long curTime = System.currentTimeMillis();
		int timeoutLimit = 300000;
		if (player.isSubscriber()) {
			timeoutLimit += 300000;
		}
		if (player.isPremiumSubscriber()) {
			timeoutLimit += 300000 * 2;
		}
		if (curTime - player.getLastPing() >= 30000) {
			player.unregister(false, "Ping time-out");
		} else if (player.warnedToMove()) {
			if (curTime - player.getLastMoved() >= (timeoutLimit + 60000) && player.loggedIn() && !player.isMod()) {
				player.unregister(false, "Movement time-out");
			}
		} else if (curTime - player.getLastMoved() >= timeoutLimit && !player.isMod()) {
			if (player.isSleeping()) {
				player.setSleeping(false);
				ActionSender.sendWakeUp(player, false, false);
			}
			player.message("@cya@You have been standing here for " + (timeoutLimit / 60000)
					+ " mins! Please move to a new area");
			player.warnToMove();
		}
	}

	public static void updateNpcs(Player playerToUpdate) throws Exception {
		com.wk.server.net.PacketBuilder packet = new com.wk.server.net.PacketBuilder();
		packet.setID(79);
		packet.startBitAccess();
		packet.writeBits(playerToUpdate.getLocalNpcs().size(), 8);
		for (Iterator<Npc> it$ = playerToUpdate.getLocalNpcs().iterator(); it$.hasNext();) {
			Npc localNpc = it$.next();

			if (!playerToUpdate.withinRange(localNpc) || localNpc.isRemoved() || localNpc.isTeleporting()) {
				it$.remove();
				packet.writeBits(1, 1);
				packet.writeBits(1, 1);
				packet.writeBits(3, 2);
			} else {
				if (localNpc.hasMoved()) {
					packet.writeBits(1, 1);
					packet.writeBits(0, 1);
					packet.writeBits(localNpc.getSprite(), 3);
				} else if (localNpc.spriteChanged()) {
					packet.writeBits(1, 1);
					packet.writeBits(1, 1);
					packet.writeBits(localNpc.getSprite(), 4);
				} else {
					packet.writeBits(0, 1);
				}
			}
		}
		for (Npc newNPC : playerToUpdate.getViewArea().getNpcsInView()) {
			if (playerToUpdate.getLocalNpcs().contains(newNPC) || newNPC.equals(playerToUpdate) || newNPC.isRemoved()
					|| newNPC.getID() == 194 && !playerToUpdate.getCache().hasKey("ned_hired")
					|| !playerToUpdate.withinRange(newNPC, 15)) {
				continue;
			} else if (playerToUpdate.getLocalNpcs().size() >= 255) {
				break;
			}
			byte[] offsets = DataConversions.getMobPositionOffsets(newNPC.getLocation(), playerToUpdate.getLocation());
			packet.writeBits(newNPC.getIndex(), 12);
			packet.writeBits(offsets[0], 5);
			packet.writeBits(offsets[1], 5);
			packet.writeBits(newNPC.getSprite(), 4);
			packet.writeBits(newNPC.getID(), 10);

			playerToUpdate.getLocalNpcs().add(newNPC);
		}
		packet.finishBitAccess();
		playerToUpdate.write(packet.toPacket());
	}

	public static void updatePlayers(Player playerToUpdate) throws Exception {

		com.wk.server.net.PacketBuilder positionBuilder = new com.wk.server.net.PacketBuilder();
		positionBuilder.setID(191);
		positionBuilder.startBitAccess();
		positionBuilder.writeBits(playerToUpdate.getX(), 11);
		positionBuilder.writeBits(playerToUpdate.getY(), 13);
		positionBuilder.writeBits(playerToUpdate.getSprite(), 4);
		positionBuilder.writeBits(playerToUpdate.getLocalPlayers().size(), 8);

		if (playerToUpdate.loggedIn()) {
			for (Iterator<Player> it$ = playerToUpdate.getLocalPlayers().iterator(); it$.hasNext();) {
				Player otherPlayer = it$.next();
				boolean visibleConditionOverride = otherPlayer.isVisibleTo(playerToUpdate);

				if (!playerToUpdate.withinRange(otherPlayer) || !otherPlayer.loggedIn() || otherPlayer.isRemoved()
						|| otherPlayer.isTeleporting() || otherPlayer.getAttribute("invisible", false)
						|| !visibleConditionOverride) {
					positionBuilder.writeBits(1, 1);
					positionBuilder.writeBits(1, 1);
					positionBuilder.writeBits(3, 2);
					it$.remove();
					playerToUpdate.getKnownPlayerAppearanceIDs().remove(otherPlayer.getUsernameHash());
				} else {
					if (otherPlayer.hasMoved()) {
						positionBuilder.writeBits(1, 1);
						positionBuilder.writeBits(0, 1);
						positionBuilder.writeBits(otherPlayer.getSprite(), 3);
					} else if (otherPlayer.spriteChanged()) {
						positionBuilder.writeBits(1, 1);
						positionBuilder.writeBits(1, 1);
						positionBuilder.writeBits(otherPlayer.getSprite(), 4);
					} else {
						positionBuilder.writeBits(0, 1);
					}
				}
			}

			for (Player otherPlayer : playerToUpdate.getViewArea().getPlayersInView()) {
				boolean visibleConditionOverride = otherPlayer.isVisibleTo(playerToUpdate);
				if (playerToUpdate.getLocalPlayers().contains(otherPlayer) || otherPlayer.equals(playerToUpdate)
						|| !otherPlayer.withinRange(playerToUpdate) || !otherPlayer.loggedIn()
						|| otherPlayer.isRemoved() || otherPlayer.getAttribute("invisible", false)
						|| !visibleConditionOverride) {
					continue;
				}
				byte[] offsets = DataConversions.getMobPositionOffsets(otherPlayer.getLocation(),
						playerToUpdate.getLocation());
				positionBuilder.writeBits(otherPlayer.getIndex(), 11);
				positionBuilder.writeBits(offsets[0], 5);
				positionBuilder.writeBits(offsets[1], 5);
				positionBuilder.writeBits(otherPlayer.getSprite(), 4);
				playerToUpdate.getLocalPlayers().add(otherPlayer);
				if (playerToUpdate.getLocalPlayers().size() >= 255) {
					break;
				}
			}
		}
		positionBuilder.finishBitAccess();
		playerToUpdate.write(positionBuilder.toPacket());
	}

	public static void updateNpcAppearances(Player player) {
		ConcurrentLinkedQueue<Damage> npcsNeedingHitsUpdate = new ConcurrentLinkedQueue<Damage>();
		ConcurrentLinkedQueue<ChatMessage> npcMessagesNeedingDisplayed = new ConcurrentLinkedQueue<ChatMessage>();
		ConcurrentLinkedQueue<Projectile> npcProjectilesNeedingDisplayed = new ConcurrentLinkedQueue<Projectile>();

		for (Npc npc : player.getLocalNpcs()) {
			UpdateFlags updateFlags = npc.getUpdateFlags();
			if (updateFlags.hasChatMessage()) {
				ChatMessage chatMessage = updateFlags.getChatMessage();
				npcMessagesNeedingDisplayed.add(chatMessage);
			}
			if (updateFlags.hasTakenDamage()) {
				Damage damage = updateFlags.getDamage().get();
				npcsNeedingHitsUpdate.add(damage);
			}
		}
		int updateSize = npcMessagesNeedingDisplayed.size() + npcsNeedingHitsUpdate.size()
		+ npcProjectilesNeedingDisplayed.size();
		if (updateSize > 0) {
			PacketBuilder npcAppearancePacket = new PacketBuilder();
			npcAppearancePacket.setID(104);
			npcAppearancePacket.writeShort(updateSize);

			ChatMessage chatMessage;
			while ((chatMessage = npcMessagesNeedingDisplayed.poll()) != null) {
				npcAppearancePacket.writeShort(chatMessage.getSender().getIndex());
				npcAppearancePacket.writeByte((byte) 1);
				npcAppearancePacket.writeShort(chatMessage.getRecipient() == null ? -1 : chatMessage.getRecipient().getIndex());
				npcAppearancePacket.writeString(chatMessage.getMessageString());
			}
			Damage npcNeedingHitsUpdate;
			while ((npcNeedingHitsUpdate = npcsNeedingHitsUpdate.poll()) != null) {
				npcAppearancePacket.writeShort(npcNeedingHitsUpdate.getIndex());
				npcAppearancePacket.writeByte((byte) 2);
				npcAppearancePacket.writeByte((byte) npcNeedingHitsUpdate.getDamage());
				npcAppearancePacket.writeByte((byte) npcNeedingHitsUpdate.getCurHits());
				npcAppearancePacket.writeByte((byte) npcNeedingHitsUpdate.getMaxHits());
			}
			player.write(npcAppearancePacket.toPacket());
		}
	}

	/**
	 * Handles the appearance updating for @param player
	 * 
	 * @param player
	 */
	public static void updatePlayerAppearances(Player player) {

		ArrayDeque<Bubble> bubblesNeedingDisplayed = new ArrayDeque<Bubble>();
		ArrayDeque<ChatMessage> chatMessagesNeedingDisplayed = new ArrayDeque<ChatMessage>();
		ArrayDeque<Projectile> projectilesNeedingDisplayed = new ArrayDeque<Projectile>();
		ArrayDeque<Damage> playersNeedingDamageUpdate = new ArrayDeque<Damage>();
		ArrayDeque<Player> playersNeedingAppearanceUpdate = new ArrayDeque<Player>();

		if (player.getUpdateFlags().hasBubble()) {
			Bubble bubble = player.getUpdateFlags().getActionBubble().get();
			bubblesNeedingDisplayed.add(bubble);
		}
		if (player.getUpdateFlags().hasFiredProjectile()) {
			Projectile projectileFired = player.getUpdateFlags().getProjectile().get();
			projectilesNeedingDisplayed.add(projectileFired);
		}
		if (player.getUpdateFlags().hasChatMessage() && !player.getSettings()
				.getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_CHAT_MESSAGES)) {
			ChatMessage chatMessage = player.getUpdateFlags().getChatMessage();
			chatMessagesNeedingDisplayed.add(chatMessage);
		}
		if (player.getUpdateFlags().hasTakenDamage()) {
			Damage damage = player.getUpdateFlags().getDamage().get();
			playersNeedingDamageUpdate.add(damage);
		}
		if (player.getUpdateFlags().hasAppearanceChanged()) {
			playersNeedingAppearanceUpdate.add(player);
		}
		for (Player otherPlayer : player.getLocalPlayers()) {

			UpdateFlags updateFlags = otherPlayer.getUpdateFlags();
			if (updateFlags.hasBubble()) {
				Bubble bubble = updateFlags.getActionBubble().get();
				bubblesNeedingDisplayed.add(bubble);
			}
			if (updateFlags.hasFiredProjectile()) {
				Projectile projectileFired = updateFlags.getProjectile().get();
				projectilesNeedingDisplayed.add(projectileFired);
			}
			if (updateFlags.hasChatMessage() && !player.getSettings()
					.getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_CHAT_MESSAGES)) {
				ChatMessage chatMessage = updateFlags.getChatMessage();
				chatMessagesNeedingDisplayed.add(chatMessage);
			}
			if (updateFlags.hasTakenDamage()) {
				Damage damage = updateFlags.getDamage().get();
				playersNeedingDamageUpdate.add(damage);
			}
			if (player.requiresAppearanceUpdateFor(otherPlayer))
				playersNeedingAppearanceUpdate.add(otherPlayer);

		}
		issuePlayerAppearanceUpdatePacket(player, bubblesNeedingDisplayed, chatMessagesNeedingDisplayed,
				projectilesNeedingDisplayed, playersNeedingDamageUpdate, playersNeedingAppearanceUpdate);
	}

	public static void issuePlayerAppearanceUpdatePacket(Player player, Queue<Bubble> bubblesNeedingDisplayed,
			Queue<ChatMessage> chatMessagesNeedingDisplayed, Queue<Projectile> projectilesNeedingDisplayed,
			Queue<Damage> playersNeedingDamageUpdate, Queue<Player> playersNeedingAppearanceUpdate) {
		if (player.loggedIn()) {
			int updateSize = bubblesNeedingDisplayed.size() + chatMessagesNeedingDisplayed.size()
			+ playersNeedingDamageUpdate.size() + projectilesNeedingDisplayed.size()
			+ playersNeedingAppearanceUpdate.size();

			if (updateSize > 0) {
				PacketBuilder appearancePacket = new PacketBuilder();
				appearancePacket.setID(234);
				appearancePacket.writeShort(updateSize);
				Bubble b;
				while ((b = bubblesNeedingDisplayed.poll()) != null) {
					appearancePacket.writeShort(b.getOwner().getIndex());
					appearancePacket.writeByte((byte) 0);
					appearancePacket.writeShort(b.getID());
				}
				ChatMessage cm;
				while ((cm = chatMessagesNeedingDisplayed.poll()) != null) {
					appearancePacket.writeShort(cm.getSender().getIndex());
					int chatType = cm.getRecipient() == null ? 1 : 6;
					appearancePacket.writeByte(chatType);
					if (chatType == 1) {
						if (cm.getSender() != null)
							appearancePacket
							.writeByte(cm.getSender().isPlayer() ? ((Player) cm.getSender()).getIcon() : 0);
					}
					appearancePacket.writeString(cm.getMessageString());
				}
				Damage playerNeedingHitsUpdate;
				while ((playerNeedingHitsUpdate = playersNeedingDamageUpdate.poll()) != null) {
					appearancePacket.writeShort(playerNeedingHitsUpdate.getIndex());
					appearancePacket.writeByte((byte) 2);
					appearancePacket.writeByte((byte) playerNeedingHitsUpdate.getDamage());
					appearancePacket.writeByte((byte) playerNeedingHitsUpdate.getCurHits());
					appearancePacket.writeByte((byte) playerNeedingHitsUpdate.getMaxHits());
				}
				Projectile projectile;
				while ((projectile = projectilesNeedingDisplayed.poll()) != null) {
					Entity victim = projectile.getVictim();
					if (victim.isNpc()) {
						appearancePacket.writeShort(projectile.getCaster().getIndex());
						appearancePacket.writeByte((byte) 3);
						appearancePacket.writeShort(projectile.getType());
						appearancePacket.writeShort(((Npc) victim).getIndex());
					} else if (victim.isPlayer()) {
						appearancePacket.writeShort(projectile.getCaster().getIndex());
						appearancePacket.writeByte((byte) 4);
						appearancePacket.writeShort(projectile.getType());
						appearancePacket.writeShort(((Player) victim).getIndex());
					}
				}
				Player playerNeedingAppearanceUpdate;
				while ((playerNeedingAppearanceUpdate = playersNeedingAppearanceUpdate.poll()) != null) {
					PlayerAppearance appearance = playerNeedingAppearanceUpdate.getSettings().getAppearance();
					appearancePacket.writeShort(playerNeedingAppearanceUpdate.getIndex());
					appearancePacket.writeByte((byte) 5);
					appearancePacket.writeShort(0);
					appearancePacket.writeString(playerNeedingAppearanceUpdate.getUsername());
					appearancePacket.writeString(playerNeedingAppearanceUpdate.getUsername());

					appearancePacket.writeByte((byte) playerNeedingAppearanceUpdate.getWornItems().length);
					for (int i : playerNeedingAppearanceUpdate.getWornItems()) {
						appearancePacket.writeShort(i);
					}
					appearancePacket.writeByte(appearance.getHairColour());
					appearancePacket.writeByte(appearance.getTopColour());
					appearancePacket.writeByte(appearance.getTrouserColour());
					appearancePacket.writeByte(appearance.getSkinColour());
					appearancePacket.writeByte((byte) playerNeedingAppearanceUpdate.getCombatLevel());
					appearancePacket.writeByte((byte) (playerNeedingAppearanceUpdate.getSkullType()));

					if (playerNeedingAppearanceUpdate.getClan() != null) {
						appearancePacket.writeByte(1);
						appearancePacket.writeString(playerNeedingAppearanceUpdate.getClan().getClanTag());
					} else {
						appearancePacket.writeByte(0);
					}
				}

				player.write(appearancePacket.toPacket());
			}
		}
	}

	public static void updateGameObjects(Player playerToUpdate) throws Exception {
		boolean changed = false;
		PacketBuilder packet = new PacketBuilder();
		packet.setID(48);
		for (Iterator<GameObject> it$ = playerToUpdate.getLocalGameObjects().iterator(); it$.hasNext();) {
			GameObject o = it$.next();
			if (!playerToUpdate.withinRange(o) || o.isRemoved() || !o.isVisibleTo(playerToUpdate)) {
				int offsetX = o.getX() - playerToUpdate.getX();
				int offsetY = o.getY() - playerToUpdate.getY();
				//If the object is close enough we can use regular way to remove:	
				if(offsetX > -128 && offsetY > -128 && offsetX < 128 && offsetY < 128) {
					packet.writeShort(60000);
					packet.writeByte(offsetX);
					packet.writeByte(offsetY);
					packet.writeByte(o.getDirection());
					it$.remove();
					changed = true;
				} else {
					//If it's not close enough we need to use the region clean packet
					playerToUpdate.getLocationsToClear().add(o.getLocation());
					it$.remove();
					changed = true;
				}
			}
		}

		for (GameObject newObject : playerToUpdate.getViewArea().getGameObjectsInView()) {
			if (!playerToUpdate.withinRange(newObject) || newObject.isRemoved()
					|| !newObject.isVisibleTo(playerToUpdate) || newObject.getType() != 0
					|| playerToUpdate.getLocalGameObjects().contains(newObject)) {
				continue;
			}
			packet.writeShort(newObject.getID());
			int offsetX = newObject.getX() - playerToUpdate.getX();
			int offsetY = newObject.getY() - playerToUpdate.getY();
			packet.writeByte(offsetX);
			packet.writeByte(offsetY);
			packet.writeByte(newObject.getDirection());
			playerToUpdate.getLocalGameObjects().add(newObject);
			changed = true;
		}
		if (changed)
			playerToUpdate.write(packet.toPacket());
	}

	public static void updateGroundItems(Player playerToUpdate) throws Exception {
		boolean changed = false;
		PacketBuilder packet = new PacketBuilder();
		packet.setID(99);
		for (Iterator<GroundItem> it$ = playerToUpdate.getLocalGroundItems().iterator(); it$.hasNext();) {
			GroundItem groundItem = it$.next();
			int offsetX = (groundItem.getX() - playerToUpdate.getX());
			int offsetY = (groundItem.getY() - playerToUpdate.getY());

			if(!playerToUpdate.withinRange(groundItem)) {
				if(offsetX > -128 && offsetY > -128 && offsetX < 128 && offsetY < 128) {
					packet.writeByte(255);
					packet.writeByte(offsetX);
					packet.writeByte(offsetY);
					//System.out.println("Removing " + groundItem + " with grounditem remove: " + offsetX + ", " + offsetY);
					it$.remove();
					changed = true;
				} else {
					playerToUpdate.getLocationsToClear().add(groundItem.getLocation());
					//System.out.println("Removing " + groundItem + " with region remove");
					it$.remove();
					changed = true;
				}
			} else if(groundItem.isRemoved() || !groundItem.visibleTo(playerToUpdate)) {
				packet.writeShort(groundItem.getID() + 32768);
				packet.writeByte(offsetX);
				packet.writeByte(offsetY);
				//System.out.println("Removing " + groundItem + " with isRemoved() remove: " + offsetX + ", " + offsetY);
				it$.remove();
				changed = true;
			}
		}

		for (GroundItem groundItem : playerToUpdate.getViewArea().getItemsInView()) {
			if (!playerToUpdate.withinRange(groundItem) || groundItem.isRemoved()
					|| !groundItem.visibleTo(playerToUpdate)
					|| playerToUpdate.getLocalGroundItems().contains(groundItem)) {
				continue;
			}
			packet.writeShort(groundItem.getID());
			int offsetX = groundItem.getX() - playerToUpdate.getX();
			int offsetY = groundItem.getY() - playerToUpdate.getY();
			packet.writeByte(offsetX);
			packet.writeByte(offsetY);
			playerToUpdate.getLocalGroundItems().add(groundItem);
			changed = true;

		}
		if (changed)
			playerToUpdate.write(packet.toPacket());
	}

	public static void updateWallObjects(Player playerToUpdate) throws Exception {
		boolean changed = false;
		PacketBuilder packet = new PacketBuilder();
		packet.setID(91);

		for (Iterator<GameObject> it$ = playerToUpdate.getLocalWallObjects().iterator(); it$.hasNext();) {
			GameObject o = it$.next();
			if (!playerToUpdate.withinRange(o) || (o.isRemoved() || !o.isVisibleTo(playerToUpdate))) {
				int offsetX = o.getX() - playerToUpdate.getX();
				int offsetY = o.getY() - playerToUpdate.getY();
				if(offsetX > -128 && offsetY > -128 && offsetX < 128 && offsetY < 128) {
					packet.writeShort(60000);
					packet.writeByte(offsetX);
					packet.writeByte(offsetY);
					packet.writeByte(o.getDirection());
					it$.remove();
					changed = true;
				} else {
					playerToUpdate.getLocationsToClear().add(o.getLocation());
					it$.remove();
					changed = true;
				}
			}
		}
		for (GameObject newObject : playerToUpdate.getViewArea().getGameObjectsInView()) {
			if (!playerToUpdate.withinRange(newObject) || newObject.isRemoved()
					|| !newObject.isVisibleTo(playerToUpdate) || newObject.getType() != 1
					|| playerToUpdate.getLocalWallObjects().contains(newObject)) {
				continue;
			}

			int offsetX = newObject.getX() - playerToUpdate.getX();
			int offsetY = newObject.getY() - playerToUpdate.getY();
			packet.writeShort(newObject.getID());
			packet.writeByte(offsetX);
			packet.writeByte(offsetY);
			packet.writeByte(newObject.getDirection());
			playerToUpdate.getLocalWallObjects().add(newObject);
			changed = true;
		}
		if (changed)
			playerToUpdate.write(packet.toPacket());
	}

	private static void sendClearLocations(Player p) {
		if(p.getLocationsToClear().size() > 0) {
			PacketBuilder packetBuilder = new PacketBuilder(211);
			for (Point point : p.getLocationsToClear()) {
				int offsetX = point.getX() - p.getX();
				int offsetY = point.getY() - p.getY();
				packetBuilder.writeShort(offsetX);
				packetBuilder.writeShort(offsetY);
			}
			p.getLocationsToClear().clear();
			p.write(packetBuilder.toPacket());
		}
	}
}
