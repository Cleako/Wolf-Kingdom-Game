package com.legacy.server.plugins.commands;

import com.legacy.server.Constants;
import com.legacy.server.content.clan.ClanInvite;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.snapshot.Chatlog;
import com.legacy.server.model.world.World;
import com.legacy.server.net.rsc.ActionSender;
import com.legacy.server.plugins.listeners.action.CommandListener;
import com.legacy.server.sql.GameLogging;
import com.legacy.server.sql.query.logs.ChatLog;
import com.legacy.server.util.rsc.DataConversions;
import com.legacy.server.util.rsc.MessageType;

public final class RegularPlayer implements CommandListener {
	public static final World world = World.getWorld();

	@Override
	public void onCommand(String command, String[] args, Player player) {
		if(command.equalsIgnoreCase("gang")) {
			if (player.getCache().hasKey("arrav_gang")) {
				if (player.getCache().getInt("arrav_gang") == 0) {
					player.message("You are a member of the Black Arm Gang");
				} else {
					player.message("You are a member of the Phoenix Gang");
				}
			} else {
				player.message("You are not in a gang - you need to start the shield of arrav quest");
			}
		}
		if(command.equalsIgnoreCase("ip")) {
			player.message("IP in wild: " + World.getWildernessIPTracker().ipCount(player.getCurrentIP()));
		}
		if(command.equalsIgnoreCase("wilderness")) {
			int TOTAL_PLAYERS_IN_WILDERNESS = 0;
			int PLAYERS_IN_F2P_WILD = 0;
			int PLAYERS_IN_P2P_WILD = 0;
			int EDGE_DUNGEON = 0;
			for(Player p : World.getWorld().getPlayers()) {
				if (p.getLocation().inWilderness()) {
					TOTAL_PLAYERS_IN_WILDERNESS++;
				}
				if (p.getLocation().inFreeWild() && !p.getLocation().inBounds(195, 3206, 234, 3258)) {
					PLAYERS_IN_F2P_WILD++;
				}
				if ((p.getLocation().wildernessLevel() >= 48 && p.getLocation().wildernessLevel() <= 56)) {
					PLAYERS_IN_P2P_WILD++;
				}
				if(p.getLocation().inBounds(195, 3206, 234, 3258)) {
					EDGE_DUNGEON++;
				}
			}
			
			ActionSender.sendBox(player, "There are currently @red@" + TOTAL_PLAYERS_IN_WILDERNESS + " @whi@player"+(TOTAL_PLAYERS_IN_WILDERNESS == 1 ? "" : "s") + " in wilderness % %"
					+ "F2P wilderness(Wild Lvl. 1-48) : @dre@" + PLAYERS_IN_F2P_WILD + "@whi@ player"+(PLAYERS_IN_F2P_WILD == 1 ? "" : "s") + " %"
					+ "P2P wilderness(Wild Lvl. 48-56) : @dre@" + PLAYERS_IN_P2P_WILD + "@whi@ player"+(PLAYERS_IN_P2P_WILD == 1 ? "" : "s") + " %"
					+ "Edge dungeon wilderness(Wild Lvl. 1-9) : @dre@" + EDGE_DUNGEON + "@whi@ player"+(EDGE_DUNGEON == 1 ? "" : "s") + " %"
					, false);
		}
		if (command.equalsIgnoreCase("c")) {
			if (player.getClan() == null) {
				return;
			}
			String message = "";
			for (int i = 0; i < args.length; i++) {
				message = message + args[i] + " ";
			}
			player.getClan().messageChat(player, "@cya@" + player.getUsername() + ":@whi@ " + message);
		}
		if (command.equalsIgnoreCase("clanaccept")) {
			if (player.getActiveClanInvite() != null) {
				player.getActiveClanInvite().accept();
			}
		}
		if (command.equalsIgnoreCase("claninvite")) {
			long invitePlayer = DataConversions.usernameToHash(args[0]);
			Player invited = World.getWorld().getPlayer(invitePlayer);
			if(!player.getClan().isAllowed(1, player)) {
				player.message("You are not allowed to invite into clan.");
				return;
			}
			if(invited != null) {
				ClanInvite.createClanInvite(player, invited);
			} else {
				ActionSender.sendBox(player, "Player is not online or could not be found!", false);
			}
		}
		if (command.equalsIgnoreCase("clankick")) {
			if(player.getClan() != null) {
				String playerToKick = args[0].replace("_", " ");
				if(!player.getClan().isAllowed(0, player)) {
					player.message("You are not allowed to kick.");
					return;
				}
				if(player.getClan().getLeader().getUsername().equals(playerToKick)) {
					player.message("You can't kick the leader.");
					return;
				}
				player.getClan().removePlayer(playerToKick);
			}
		}
		if (command.equalsIgnoreCase("info")) {
			player.updateTotalPlayed();
			long timePlayed = player.getCache().getLong("total_played");

			String subscriptionStatus = "";
			if (player.isSubscriber() && player.isPremiumSubscriber())
				subscriptionStatus = "@cya@Premium @whi@+ @ora@Standard@whi@";
			else if (player.isSubscriber())
				subscriptionStatus = "@ora@Standard";
			else if (player.isPremiumSubscriber())
				subscriptionStatus = "@cya@Premium";
			else
				subscriptionStatus = "Not subscribed";

			ActionSender.sendBox(player, 
			"@lre@Character Information:%" 
			+ "@gre@Last IP:@whi@ " + player.getLastIP() + " %"
			+ "@gre@Last Login:@whi@ " + player.getDaysSinceLastLogin() + " days ago %"
			+ "@gre@Total played:@whi@ " + DataConversions.getDateFromMsec(timePlayed) + " %"
			+ "@gre@Subscription Status:@whi@ " + subscriptionStatus + " %"
			+ "@gre@Subscription Expires:@whi@ " + player.getDaysSubscriptionLeft() + " days %" 
			+ "@gre@Premium Expires:@whi@ " + player.premiumSubDaysLeft() + " days % %"
			+ "@lre@Experience Rates:%"
			+ "@gre@Attack, Strength, Defense, Hits:@whi@ " + player.getExperienceRate(0) + "x %"
			+ "@gre@Prayer, Magic, Ranged:@whi@ " + player.getExperienceRate(4) + "x %"
			+ "@gre@Skill XP Rate:@whi@ " + player.getExperienceRate(11) + "x %"
			+ "@gre@Experience Elixir:@whi@ " + (player.getCache().hasKey("elixir_time") && player.getElixir() > 0 ? DataConversions.getDateFromMsec(player.getElixir() * 1000) : "--") + " %"
			+ "@gre@Double Experience: " + (Constants.GameServer.IS_DOUBLE_EXP ? "@whi@ACTIVE!! %" : "@whi@-- % %"), true);
			return;
		}
		if (command.equals("elixir")) {
			long lastElixir = 0;
			if(player.getCache().hasKey("buy_elixir")) {
				lastElixir = player.getCache().getLong("buy_elixir");
			}
			int time = (int) (86400 - ((System.currentTimeMillis() - lastElixir) / 1000));
			if (System.currentTimeMillis() - lastElixir < 24 * 60 * 60 * 1000) {
				player.message("@mag@[Elixir] @whi@You can buy next Elixir in: " + DataConversions.getDateFromMsec(time * 1000) + "!");
			} else {
				player.message("@mag@[Elixir] @whi@You can consume Elixir, visit the Apothecary in Varrock.");
			}
		}
		if (command.equalsIgnoreCase("event")) {
			if (!World.EVENT) {
				player.message("There is no event running at the moment");
				return;
			}
			if (player.getLocation().inWilderness()) {
				player.message("Please move out of wilderness first");
				return;
			} else if(player.getLocation().inModRoom()){
				player.message("You can't participate to events from here..");
				return;
			}
			if (player.getCombatLevel() > World.EVENT_COMBAT_MAX || player.getCombatLevel() < World.EVENT_COMBAT_MIN) {
				player.message("This event is only for combat level range: " + World.EVENT_COMBAT_MIN + " - "
						+ World.EVENT_COMBAT_MAX);
				return;
			}
			player.teleport(World.EVENT_X, World.EVENT_Y);
		}
		if (command.equals("g") || command.equals("p")) {
			if (player.isMuted()) {
				player.message("You are muted, you cannot send messages");
				return;
			}
			if (player.getCache().hasKey("global_mute") && (player.getCache().getLong("global_mute") - System.currentTimeMillis() > 0 || player.getCache().getLong("global_mute") == -1) && command.equals("g")) {
				long globalMuteDelay = player.getCache().getLong("global_mute");
				player.message("You are " + (globalMuteDelay == -1 ? "permanently muted" : "temporary muted for " + (int) ((player.getCache().getLong("global_mute") - System.currentTimeMillis()) / 1000 / 60) + " minutes") + " from the ::g chat.");
				return;
			}
			long sayDelay = 0;
			if (player.getCache().hasKey("say_delay")) {
				sayDelay = player.getCache().getLong("say_delay");
			}

			long waitTime = 15000;

			if (player.isPremiumSubscriber()) {
				waitTime = 5000;
			} else if (player.isSubscriber() && !player.isPremiumSubscriber()) {
				waitTime = 10000;
			} else if(player.isMod()) {
				waitTime = 0;
			}

			if (System.currentTimeMillis() - sayDelay < waitTime) {
				player.message("You can only use this command every " + (waitTime / 1000) + " seconds");
				return;
			}

			if (player.getLocation().onTutorialIsland() && !player.isMod()) {
				return;
			}

			player.getCache().store("say_delay", System.currentTimeMillis());

			String newStr = "";
			for (int i = 0; i < args.length; i++) {
				newStr += args[i] + " ";
			}
			newStr = newStr.replace('~', ' ');
			newStr = newStr.replace('@', ' ');
			String channelPrefix = command.equals("g") ? "@gr2@[General] " : "@or1@[PKing] ";
			int channel = command.equals("g") ? 1 : 2;
			for (Player p : World.getWorld().getPlayers()) {
				if (p.getSocial().isIgnoring(player.getUsernameHash()))
					continue;
				if (p.getGlobalBlock() == 3 && channel == 2) {
					continue;
				}
				if(p.getGlobalBlock() == 4 && channel == 1) {
					continue;
				}
				if (p.getGlobalBlock() != 2) {
					String header = "";
					if (player.isSubscriber())
						header = "@ora@";
					if (player.isPremiumSubscriber()) {
						header = "@pre@";
					}
					ActionSender.sendMessage(p, player, 1, MessageType.GLOBAL_CHAT, channelPrefix + "@whi@" + (player.getClan() != null ? "@cla@<" + player.getClan().getClanTag() + "> @whi@" : "") + header + player.getUsername() + ": "
							+ (channel == 1 ? "@gr2@" : "@or1@") + newStr, player.getIcon());
				}
			}
			if(command.equals("g")) {
				GameLogging.addQuery(new ChatLog(player.getUsername(), "(Global) " + newStr));
				World.getWorld().addEntryToSnapshots(new Chatlog(player.getUsername(), "(Global) " + newStr));
			} else {
				GameLogging.addQuery(new ChatLog(player.getUsername(), "(PKing) " + newStr));
				World.getWorld().addEntryToSnapshots(new Chatlog(player.getUsername(), "(PKing) " + newStr));
			}
		}
		if (command.equals("fatigue")) {
			player.setFatigue(7500);
			player.message("Your fatigue has been updated to 100%");
		}
		if (command.equals("online")) {
			int players = (int) (World.getWorld().getPlayers().size() * 1.12);
			
			

			for (Player p : World.getWorld().getPlayers()) {
				if (p.isMod() && p.getSettings().getPrivacySetting(1)) {
					players--;
				}
			}
			ActionSender.sendMessage(player, "@yel@Players Online: @whi@" + players);
			return;
		}
		if (command.equals("skull")) {
			int length = 20;
			player.addSkull(length * 60000);
			return;
		}
		if (command.equals("onlinelist")) {
			ActionSender.sendOnlineList(player);
			return;
		}
		if(command.equals("commands")) {
			ActionSender.sendBox(player, ""
					+ "@yel@Commands available: %"
					+ "Type :: before you enter your command, see the list below. % %"
					+ "@whi@::info - shows player and server information %"
					+ "@whi@::online - shows players currently online %"
					+ "@whi@::onlinelist - shows players currently online in a list %"
					+ "@whi@::skull - sets you skulled for 20 minutes %"
					+ "@whi@::fatigue - gives you 100 percent fatigue %"
					+ "@whi@::g <message> - to talk in @gr1@general @whi@global chat channel %"
					+ "@whi@::p <message> - to talk in @or1@pking @whi@global chat channel %"
					+ "@whi@::event - to enter an ongoing server event %"
					+ "@whi@::elixir - shows time left of the elixir potion effect %"
					+ "@whi@::c <message> - talk in clan chat %"
					+ "@whi@::claninvite <name> - invite player to clan %"
					+ "@whi@::clankick <name> - kick player from clan %"
					+ "@whi@::clanaccept - accept clan invitation %"
					+ "@whi@::gang - shows if you are 'Pheonix' or 'Black arm' gang %"
					+ "@whi@::wilderness - shows the wilderness activity", true);
			return;
		}
	}
}
