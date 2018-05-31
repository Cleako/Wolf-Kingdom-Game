package com.wk.server.net.rsc;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wk.server.Constants;
import com.wk.server.Server;
import com.wk.server.login.LoginRequest;
import com.wk.server.model.Point;
import com.wk.server.model.entity.player.Player;
import com.wk.server.net.ConnectionAttachment;
import com.wk.server.net.Packet;
import com.wk.server.net.PacketBuilder;
import com.wk.server.net.RSCConnectionHandler;
import com.wk.server.plugins.PluginHandler;
import com.wk.server.sql.DatabaseConnection;
import com.wk.server.util.rsc.DataConversions;
import com.wk.server.util.rsc.LoginResponse;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

/**
 * 
 * @author n0m
 *
 */
public class LoginPacketHandler {
	
	/**
     * The asynchronous logger.
     */
    private static final Logger LOGGER = LogManager.getLogger();

	public String getString(ByteBuf payload) {
		StringBuilder bldr = new StringBuilder();
		byte b;
		while (payload.isReadable() && (b = payload.readByte()) != 10)
			bldr.append((char) b);
		return bldr.toString();
	}

	public static boolean isValidEmailAddress(String email) {
		boolean stricterFilter = true;
		String stricterFilterString = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
		String laxString = ".+@.+\\.[A-Za-z]{2}[A-Za-z]*";
		String emailRegex = stricterFilter ? stricterFilterString : laxString;
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(emailRegex);
		java.util.regex.Matcher m = p.matcher(email);
		return m.matches();
	}

	public void processLogin(Packet p, Channel channel) throws Exception {
		String IP = ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
		switch (p.getID()) {
		case 0:
			boolean reconnecting = p.readByte() == 1;
			int clientVersion = p.readInt();

			final String username = getString(p.getBuffer()).trim();
			final String password = getString(p.getBuffer()).trim();
		
			if (clientVersion != Constants.GameServer.CLIENT_VERSION) {
				channel.writeAndFlush(new PacketBuilder().writeByte((byte) LoginResponse.CLIENT_UPDATED).toPacket());
				channel.close();
				return;
			}
			if (Server.getServer().timeTillShutdown() > 0) {
				channel.writeAndFlush(new PacketBuilder().writeByte((byte) LoginResponse.WORLD_DOES_NOT_ACCEPT_NEW_PLAYERS).toPacket());
				channel.close();
				return;
			}
			
			ConnectionAttachment attachment = new ConnectionAttachment();
			channel.attr(RSCConnectionHandler.attachment).set(attachment);
			
			final LoginRequest request = new LoginRequest(username, password, clientVersion, channel) {
				@Override
				public void loginValidated(int response) {
					Channel channel = getChannel();
					channel.writeAndFlush(new PacketBuilder().writeByte((byte) response).toPacket());
					if(response != LoginResponse.LOGIN_SUCCESSFUL)
					{
						channel.close();
					}
				}

				@Override
				public void loadingComplete(Player loadedPlayer) {
					ConnectionAttachment attachment = (ConnectionAttachment) channel.attr(RSCConnectionHandler.attachment).get();
					// attachment.ISAAC.set(new ISAACContainer(incomingCipher,
					// outgoingCipher));
					attachment.player.set(loadedPlayer);

					if (loadedPlayer.getLastLogin() == 0L) {
						loadedPlayer.setInitialLocation(Point.location(216, 744));
						loadedPlayer.setChangingAppearance(true);
					}

					PluginHandler.getPluginHandler().handleAction("PlayerLogin", new Object[] { loadedPlayer });
					ActionSender.sendLogin(loadedPlayer);
				}
			};
			Server.getPlayerDataProcessor().addLoginRequest(request);
			break;

		case 78:
			LOGGER.info("Android registration attempt from: " + IP);

			String user = getString(p.getBuffer()).trim();
			String pass = getString(p.getBuffer()).trim();
			
			user = user.replaceAll("[^=,\\da-zA-Z\\s]|(?<!,)\\s", " ");
			pass = pass.replaceAll("[^=,\\da-zA-Z\\s]|(?<!,)\\s", "");
			
			String email = getString(p.getBuffer()).trim();

			if (user.length() < 2 || user.length() > 12) {
				channel.writeAndFlush(new PacketBuilder().writeByte((byte) 7).toPacket());
				channel.close();
				return;
			}

			if (pass.length() < 4 || pass.length() > 16) {
				channel.writeAndFlush(new PacketBuilder().writeByte((byte) 8).toPacket());
				channel.close();
				return;
			}

			if (!isValidEmailAddress(email)) {
				channel.writeAndFlush(new PacketBuilder().writeByte((byte) 6).toPacket());
				channel.close();
				return;
			}

			ResultSet set = DatabaseConnection.getDatabase().executeQuery("SELECT 1 FROM users WHERE registration_ip='" + IP
					+ "' AND registered>'" + ((System.currentTimeMillis() / 1000) - 3600) + "'");
			if (set.next()) {
				set.close();
				LOGGER.info(IP + " - Android registration failed: Registered recently.");
				channel.writeAndFlush(new PacketBuilder().writeByte((byte) 5).toPacket());
				channel.close();
				return;
			}

			set = DatabaseConnection.getDatabase().executeQuery("SELECT 1 FROM users WHERE `username`='" + user + "'");
			if (set.next()) {
				set.close();
				LOGGER.info(IP + " - Android registration failed: Forum Username already in use.");
				channel.writeAndFlush(new PacketBuilder().writeByte((byte) 2).toPacket());
				channel.close();
				return;
			}

			set = DatabaseConnection.getDatabase().executeQuery("SELECT 1 FROM users WHERE email='" + email + "'");
			if (set.next()) {
				set.close();
				LOGGER.info(IP + " - Android registration failed: E-mail address already in use.");
				channel.writeAndFlush(new PacketBuilder().writeByte((byte) 3).toPacket());
				channel.close();
				return;
			}

			set = DatabaseConnection.getDatabase().executeQuery("SELECT 1 FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "players WHERE `username`='" + user + "'");
			if (set.next()) {
				set.close();
				LOGGER.info(IP + " - Android registration failed: Character Username already in use.");
				channel.writeAndFlush(new PacketBuilder().writeByte((byte) 2).toPacket());
				channel.close();
				return;
			}
			/* Insert forum user */
			PreparedStatement statement = DatabaseConnection.getDatabase().prepareStatement(
					"INSERT INTO users (username, group_id, password, email, language, registered, registration_ip, last_visit, php_timezone) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
			
			statement.setString(1, user);
			statement.setInt(2, 4);
			statement.setString(3, sha1(pass));
			statement.setString(4, email);
			statement.setString(5, "English");
			statement.setLong(6, System.currentTimeMillis() / 1000);
			statement.setString(7, IP);
			statement.setLong(8, System.currentTimeMillis() / 1000);
			statement.setString(9, "UTC");
			statement.executeUpdate();
			statement = null;
			
			
			/* Check what's the ID of the forum account */
			statement = DatabaseConnection.getDatabase().prepareStatement("SELECT id FROM users WHERE username=? AND email=?");
			statement.setString(1, user);
			statement.setString(2, email);

			set = statement.executeQuery();

			if (!set.next()) {
				channel.writeAndFlush(new PacketBuilder().writeByte((byte) 6).toPacket());
				LOGGER.info(IP + " - Android registration failed: Forum id not found.");
				channel.close();
				return;
			}
			int ownerID = set.getInt("id");
			String newSalt = setSalt(16);
			/* Create the game character */
			try {
				statement = DatabaseConnection.getDatabase().prepareStatement(
						"INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "players` (`username`, `owner`, `pass`, `salt`, `creation_date`, `creation_ip`) VALUES (?, ?, ?, ?, ?, ?)");
				statement.setString(1, user);
				statement.setInt(2, ownerID);
				statement.setString(3, DataConversions.hmac("SHA512", newSalt + pass, Constants.GameServer.HMAC_PRIVATE_KEY));
				statement.setString(4, newSalt);
				statement.setLong(5, System.currentTimeMillis() / 1000);
				statement.setString(6, IP);
				statement.executeUpdate();
				statement = null;
				
				/* PlayerID of the player account */
				statement = DatabaseConnection.getDatabase().prepareStatement("SELECT id FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "players WHERE username=? AND owner=?");
				statement.setString(1, user);
				statement.setInt(2, ownerID);

				set = statement.executeQuery();
				
				if (!set.next()) {
					channel.writeAndFlush(new PacketBuilder().writeByte((byte) 6).toPacket());
					LOGGER.info(IP + " - Android registration failed: Player id not found.");
					return;
				}
				
				int playerID = set.getInt("id");
				
				statement = DatabaseConnection.getDatabase().prepareStatement("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "curstats` (`playerID`) VALUES (?)");
				statement.setInt(1, playerID);
				statement.executeUpdate();

				statement = DatabaseConnection.getDatabase().prepareStatement("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "experience` (`playerID`) VALUES (?)");
				statement.setInt(1, playerID);
				statement.executeUpdate();

				LOGGER.info(IP + " - Android registration successful");
				channel.writeAndFlush(new PacketBuilder().writeByte((byte) 0).toPacket());
			} catch (Exception e) {
				LOGGER.catching(e);
				channel.writeAndFlush(new PacketBuilder().writeByte((byte) 5).toPacket());
				channel.close();
			}
			break;
		}
	}
	// Convert to SHA512 + Salt(8) via forum login.
	static String sha1(String input) throws NoSuchAlgorithmException {
		MessageDigest mDigest = MessageDigest.getInstance("SHA1");
		byte[] result = mDigest.digest(input.getBytes());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < result.length; i++) {
			sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}
	// Generate a random salt with a prefered size (16).
	static String setSalt(int size) throws NoSuchAlgorithmException {
		SecureRandom random = new SecureRandom();
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			sb.append(chars.charAt(random.nextInt(chars.length())));
		}
		
		return sb.toString();
	}
}
