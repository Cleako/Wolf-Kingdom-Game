package com.wk.server.model.entity.player;

import java.net.InetSocketAddress;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wk.server.Constants;
import com.wk.server.Server;
import com.wk.server.content.achievement.Achievement;
import com.wk.server.content.achievement.AchievementSystem;
import com.wk.server.content.achievement.AchievementTask;
import com.wk.server.content.clan.Clan;
import com.wk.server.content.clan.ClanInvite;
import com.wk.server.event.DelayedEvent;
import com.wk.server.event.custom.BatchEvent;
import com.wk.server.event.rsc.impl.FireCannonEvent;
import com.wk.server.event.rsc.impl.PoisonEvent;
import com.wk.server.event.rsc.impl.PrayerDrainEvent;
import com.wk.server.event.rsc.impl.ProjectileEvent;
import com.wk.server.event.rsc.impl.RangeEvent;
import com.wk.server.event.rsc.impl.ThrowingEvent;
import com.wk.server.login.LoginRequest;
import com.wk.server.model.Cache;
import com.wk.server.model.MenuOptionListener;
import com.wk.server.model.Point;
import com.wk.server.model.PrivateMessage;
import com.wk.server.model.Shop;
import com.wk.server.model.Skills;
import com.wk.server.model.action.WalkToAction;
import com.wk.server.model.container.Bank;
import com.wk.server.model.container.Inventory;
import com.wk.server.model.container.Item;
import com.wk.server.model.entity.GameObject;
import com.wk.server.model.entity.GroundItem;
import com.wk.server.model.entity.Mob;
import com.wk.server.model.entity.npc.Npc;
import com.wk.server.model.states.Action;
import com.wk.server.model.states.CombatState;
import com.wk.server.model.world.World;
import com.wk.server.net.Packet;
import com.wk.server.net.rsc.ActionSender;
import com.wk.server.net.rsc.PacketHandler;
import com.wk.server.net.rsc.PacketHandlerLookup;
import com.wk.server.plugins.PluginHandler;
import com.wk.server.plugins.QuestInterface;
import com.wk.server.plugins.menu.Menu;
import com.wk.server.sql.GameLogging;
import com.wk.server.sql.query.logs.LiveFeedLog;
import com.wk.server.util.IPTrackerPredicate;
import com.wk.server.util.rsc.DataConversions;
import com.wk.server.util.rsc.Formulae;
import com.wk.server.util.rsc.MessageType;

import io.netty.channel.Channel;

/**
 * A single player.
 */  
public final class Player extends Mob {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	public int IRON_MAN_MODE = 0;
	public int IRON_MAN_RESTRICTION = 1;
	public int IRON_MAN_HC_DEATH = 0;

	public int getIronMan() {
		return IRON_MAN_MODE;
	}

	public int getIronManRestriction() {
		return IRON_MAN_RESTRICTION;
	}

	public void setIronMan(int i) {
		this.IRON_MAN_MODE = i;
	}

	public int getHCIronmanDeath() {
		return IRON_MAN_HC_DEATH;
	}

	public void setHCIronmanDeath(int i) {
		this.IRON_MAN_HC_DEATH = i;
	}

	public void updateHCIronman(int int1) {
		this.IRON_MAN_MODE = int1;
		this.IRON_MAN_HC_DEATH = int1;
	}

	public void setIronManRestriction(int i) {
		this.IRON_MAN_RESTRICTION = i;
	}

	public boolean isIronMan(int mode) {
		if (mode == 1 && getIronMan() == 1) {
			return true;
		} else if (mode == 2 && getIronMan() == 2) {
			return true;
		} else if (mode == 3 && getIronMan() == 3) {
			return true;
		}
		return false;
	}

	private FireCannonEvent cannonEvent = null;

	public void resetCannonEvent() {
		if (cannonEvent != null) {
			cannonEvent.stop();
		}
		cannonEvent = null;
	}

	public boolean isCannonEventActive() {
		return cannonEvent != null;
	}

	public void setCannonEvent(FireCannonEvent event) {
		cannonEvent = event;
	}

	private long consumeTimer = 0;

	public boolean cantConsume() {
		return consumeTimer - System.currentTimeMillis() > 0;
	}

	public void setConsumeTimer(long l) {
		consumeTimer = System.currentTimeMillis() + l;
	}

	private int appearanceID;

	private HashMap<Long, Integer> knownPlayersAppearanceIDs = new HashMap<Long, Integer>();

	public int getAppearanceID() {
		return appearanceID;
	}

	public void incAppearanceID() {
		appearanceID++;
	}

	public boolean requiresAppearanceUpdateFor(Player p) {
		for (Entry<Long, Integer> entry : knownPlayersAppearanceIDs.entrySet()) {
			if (entry.getKey() == p.getUsernameHash()) {
				if (entry.getValue() != p.getAppearanceID()) {
					knownPlayersAppearanceIDs.put(p.getUsernameHash(), p.getAppearanceID());
					return true;
				} else {
					return false;
				}
			}
		}
		knownPlayersAppearanceIDs.put(p.getUsernameHash(), p.getAppearanceID());
		return true;
	}

	public HashMap<Long, Integer> getKnownPlayerAppearanceIDs() {
		return knownPlayersAppearanceIDs;
	}

	public void write(Packet o) {
		if (channel != null) {
			synchronized (outgoingPacketsLock) {
				outgoingPackets.add(o);
			}
		}
	}

	public final String MEMBER_MESSAGE = "This feature is only available for members only";

	public final static Item[] STARTER_ITEMS = { new Item(87), new Item(166), new Item(132) };

	private LinkedHashSet<Player> localPlayers = new LinkedHashSet<Player>();
	private LinkedHashSet<Npc> localNpcs = new LinkedHashSet<Npc>();
	private LinkedHashSet<GameObject> localObjects = new LinkedHashSet<GameObject>();
	private LinkedHashSet<GameObject> localWallObjects = new LinkedHashSet<GameObject>();
	private LinkedHashSet<GroundItem> localGroundItems = new LinkedHashSet<GroundItem>();

	public LinkedHashSet<Npc> getLocalNpcs() {
		return localNpcs;
	}

	public LinkedHashSet<GameObject> getLocalWallObjects() {
		return localWallObjects;
	}

	public LinkedHashSet<GameObject> getLocalGameObjects() {
		return localObjects;
	}

	public LinkedHashSet<Player> getLocalPlayers() {
		return localPlayers;
	}

	public LinkedHashSet<GroundItem> getLocalGroundItems() {
		return localGroundItems;
	}
	private ArrayDeque<Point> locationsToClear = new ArrayDeque<Point>();

	public ArrayDeque<Point> getLocationsToClear() {
		return locationsToClear;
	}

	/**
	 * Prayers
	 */
	private Prayers prayers;

	/**
	 * Bank for banked items
	 */
	private Bank bank;

	private BatchEvent batchEvent = null;

	public int lastMineTry = -1;
	/**
	 * Players cache is used to store various objects into database
	 */
	private final Cache cache = new Cache();

	/**
	 * Controls if were allowed to accept appearance updates
	 */
	private boolean changingAppearance = false;

	public int click = -1;

	/**
	 * Combat style: 0 - all, 1 - str, 2 - att, 3 - def
	 */
	private int combatStyle = 0;

	/**
	 * Added by Zerratar: Correct sleepword we are looking for! Case SenSitIvE
	 */
	public String correctSleepword = "";

	private String currentIP = "0.0.0.0";

	/**
	 * Unix time when the player logged in
	 */
	private long currentLogin = 0;

	/**
	 * DelayedEvent responsible for handling prayer drains
	 */
	private PrayerDrainEvent prayerDrainEvent;

	/**
	 * The drain rate of the prayers currently enabled
	 */
	private int drainRate = 0;
	/**
	 * Amount of fatigue - 0 to 7500
	 */
	private int fatigue = 7500, sleepStateFatigue = 7500;

	/**
	 * The main accounts group is
	 */
	private int groupID = 4;

	/**
	 * Is the player accessing their bank?
	 */
	private boolean inBank = false;

	private int incorrectSleepTries = 0;
	/**
	 * The npc we are currently interacting with
	 */
	private Npc interactingNpc = null;
	/**
	 * Atomic reference to the inventory, multiple threads use this instance and
	 * it is never changed during session.
	 */
	private AtomicReference<Inventory> inventory = new AtomicReference<Inventory>();
	/**
	 * Channel
	 */
	private Channel channel;
	/**
	 * The last menu reply this player gave in a quest
	 */
	public long lastCast = System.currentTimeMillis();
	/**
	 * Time of antidote protection from poison
	 */
	private long lastAntidote = 0;

	/**
	 * Stores the last IP address used
	 */
	private String lastIP = "0.0.0.0";
	/**
	 * Unix time when the player last logged in
	 */
	private long lastLogin = 0;
	/**
	 * Last time a 'ping' was received
	 */
	private long lastPing = System.currentTimeMillis();
	/**
	 * Time last report was sent, used to throttle reports
	 */
	private long lastReport = 0;

	/**
	 * The time of the last spell cast, used as a throttle
	 */
	private long lastSpellCast = 0;
	/**
	 * Time of last trade/duel request
	 */
	private long lastTradeDuelRequest = 0;
	/**
	 * Whether the player is currently logged in
	 */
	private boolean loggedIn = false;
	/**
	 * Is the character male?
	 */
	private boolean maleGender;
	/**
	 * The current active menu
	 */
	private Menu menu;
	/**
	 * A handler for any menu we are currently in
	 */
	private MenuOptionListener menuHandler = null;
	/**
	 * The ID of the owning account
	 */
	private int owner = 1;
	/**
	 * Received packets from this player yet to be processed.
	 */
	private final ArrayList<Packet> incomingPackets = new ArrayList<Packet>();
	private final Object incomingPacketLock = new Object();

	/**
	 * Outgoing packets from this player yet to be processed.
	 */
	private final ArrayList<Packet> outgoingPackets = new ArrayList<Packet>();
	private final Object outgoingPacketsLock = new Object();
	/**
	 * The player's password
	 */
	private String password;

	private int questionOption;
	/**
	 * Total quest points
	 */
	private int questPoints = 0;

	private final Map<Integer, Integer> questStages = new ConcurrentHashMap<>();
	/**
	 * Ranging event
	 */
	private RangeEvent rangeEvent;
	private ThrowingEvent throwingEvent;
	/**
	 * If the player is reconnecting after connection loss
	 */
	private boolean reconnecting = false;

	/**
	 * Is a trade/duel update required?
	 */
	private boolean requiresOfferUpdate = false;
	/**
	 * The shop (if any) the player is currently accessing
	 */
	private Shop shop = null;
	/**
	 * DelayedEvent used for removing players skull after 20mins
	 */
	private DelayedEvent skullEvent = null;

	private DelayedEvent chargeEvent = null;

	private DelayedEvent sleepEvent;

	private boolean sleeping = false;
	/**
	 * Players sleepword
	 */
	private String sleepword;
	/**
	 * The current status of the player
	 */
	private Action status = Action.IDLE;
	/**
	 * When the users subscription expires (or 0 if they don't have one)
	 */
	private long subscriptionExpires = 0;
	/**
	 * If the player has been sending suscicious packets
	 */
	private boolean suspiciousPlayer;

	/**
	 * The player's username
	 */
	private String username;
	/**
	 * The player's username hash
	 */
	private long usernameHash;
	/**
	 * The items being worn by the player
	 */
	private int[] wornItems = new int[12];
	/**
	 * Time when the player logged in, used to calculate the total play time.
	 */
	private long sessionStart;
	private PlayerSettings playerSettings;

	private Social social;

	private Duel duel;

	/**
	 * Constructs a new Player instance from LoginRequest
	 * 
	 * @param request
	 */
	public Player(LoginRequest request) {
		password = request.getPassword();
		usernameHash = DataConversions.usernameToHash(request.getUsername());
		username = DataConversions.hashToUsername(usernameHash);
		sessionStart = System.currentTimeMillis();

		channel = request.getChannel();

		currentIP = ((InetSocketAddress) request.getChannel().remoteAddress()).getAddress().getHostAddress();
		currentLogin = System.currentTimeMillis();

		setBusy(true);

		trade = new Trade(this);
		duel = new Duel(this);
		playerSettings = new PlayerSettings(this);
		social = new Social(this);
		prayers = new Prayers(this);

	}

	public boolean accessingBank() {
		return inBank;
	}

	public boolean accessingShop() {
		return shop != null;
	}

	public PrivateMessage getNextPrivateMessage() {
		return privateMessageQueue.poll();
	}

	public void addSkull(long timeLeft) {
		if (skullEvent == null) {
			skullEvent = new DelayedEvent(this, 1200000) {

				@Override
				public void run() {
					removeSkull();
				}
			};
			Server.getServer().getEventHandler().add(skullEvent);
			getUpdateFlags().setAppearanceChanged(true);
		}
		skullEvent.setLastRun(System.currentTimeMillis() - (1200000 - timeLeft));
	}

	public void removeCharge() {
		if (chargeEvent == null) {
			return;
		}
		chargeEvent.stop();
		chargeEvent = null;
	}

	public void addCharge(long timeLeft) {
		if (chargeEvent == null) {
			chargeEvent = new DelayedEvent(this, 6 * 60000) { 
				// 6 minutes taken from RS2.
				// the charge spell in RSC seem to be bugged, but 10 minutes most of the times.
				// sometimes you are charged for 1 hour lol.
				@Override
				public void run() {
					removeCharge();
					owner.message("@red@Your magic charge fades");
				}
			};
			Server.getServer().getEventHandler().add(chargeEvent);
		}
		chargeEvent.setLastRun(System.currentTimeMillis() - (6 * 60000 - timeLeft));
	}

	public void close() {
		getChannel().close();
	}

	public boolean canLogout() {
		return !isBusy() && System.currentTimeMillis() - getCombatTimer() > 10000
				&& System.currentTimeMillis() - getAttribute("last_shot", (long) 0) > 10000;
	}

	public boolean canReport() {
		return System.currentTimeMillis() - lastReport > 60000;
	}

	public boolean castTimer() {
		return System.currentTimeMillis() - lastSpellCast > 1250;
	}

	public void checkAndInterruptBatchEvent() {
		if (batchEvent != null) {
			batchEvent.interrupt();
			batchEvent = null;
		}
	}

	public boolean checkAttack(Mob mob, boolean missile) {
		if (mob.isPlayer()) {
			Player victim = (Player) mob;
			if ((inCombat() && getDuel().isDuelActive()) && (victim.inCombat() && victim.getDuel().isDuelActive())) {
				Player opponent = (Player) getOpponent();
				if (opponent != null && victim.equals(opponent)) {
					return true;
				}
			}
			if (!missile) {
				if (System.currentTimeMillis() - mob.getCombatTimer() < (mob.getCombatState() == CombatState.RUNNING
						|| mob.getCombatState() == CombatState.WAITING ? 3000 : 500)) {
					return false;
				}
			}

			int myWildLvl = getLocation().wildernessLevel();
			int victimWildLvl = victim.getLocation().wildernessLevel();
			if (myWildLvl < 1 || victimWildLvl < 1) {
				message("You can't attack other players here. Move to the wilderness");
				return false;
			}
			int combDiff = Math.abs(getCombatLevel() - victim.getCombatLevel());
			if (combDiff > myWildLvl) {
				message("You can only attack players within " + (myWildLvl) + " levels of your own here");
				message("Move further into the wilderness for less restrictions");
				return false;
			}
			if (combDiff > victimWildLvl) {
				message("You can only attack players within " + (victimWildLvl) + " levels of your own here");
				message("Move further into the wilderness for less restrictions");
				return false;
			}

			if (victim.getAttribute("no-aggro", false)) {
				message("You are not allowed to attack that person");
				return false;
			}
			return true;
		} else if (mob.isNpc()) {
			Npc victim = (Npc) mob;
			if (!victim.getDef().isAttackable()) {
				setSuspiciousPlayer(true);
				return false;
			}
			return true;
		}
		return true;
	}

	@Override
	public void resetCombatEvent() {
		if (inCombat()) {
			super.resetCombatEvent();
		}
	}

	public int combatStyleToIndex() {
		if (getCombatStyle() == 1) {
			return 2;
		}
		if (getCombatStyle() == 2) {
			return 0;
		}
		if (getCombatStyle() == 3) {
			return 1;
		}
		return -1;
	}

	private DelayedEvent unregisterEvent;

	private int unreadMessages, teleportStones;

	/**
	 * Unregisters this player instance from the server
	 * 
	 * @param force
	 *            - if false wait until combat is over
	 * @param reason
	 *            - reason why the player was unregistered.
	 */
	public void unregister(boolean force, final String reason) {
		if (unregistering) {
			return;
		}
		if (force || canLogout()) {
			updateTotalPlayed();
			LOGGER.info("Requesting unregistration for " + getUsername() + ": " + reason);
			unregistering = true;
		} else {
			if (unregisterEvent != null) {
				return;
			}
			final long startDestroy = System.currentTimeMillis();
			unregisterEvent = new DelayedEvent(this, 500) {
				@Override
				public void run() {
					if (owner.canLogout() || (!(owner.inCombat() && owner.getDuel().isDuelActive())
							&& System.currentTimeMillis() - startDestroy > 60000)) {
						owner.unregister(true, reason);
						matchRunning = false;
					}
				}
			};
			Server.getServer().getEventHandler().add(unregisterEvent);
		}
	}

	public void updateTotalPlayed() {
		if (cache.hasKey("total_played")) {
			long oldTotal = cache.getLong("total_played");
			long sessionLength = oldTotal + (System.currentTimeMillis() - sessionStart);
			cache.store("total_played", sessionLength);
		} else {
			cache.store("total_played", System.currentTimeMillis() - sessionStart);
		}
		sessionStart = System.currentTimeMillis();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Player) {
			Player p = (Player) o;
			return usernameHash == p.getUsernameHash();
		}
		return false;
	}

	public void checkEquipment() {
		ListIterator<Item> iterator = getInventory().iterator();
		for (int slot = 0; iterator.hasNext(); slot++) {
			Item item = iterator.next();
			if (item.isWielded()) {
				int requiredLevel = item.getDef().getRequiredLevel();
				int requiredSkillIndex = item.getDef().getRequiredSkillIndex();
				if (getSkills().getMaxStat(item.getDef().getRequiredSkillIndex()) < item.getDef().getRequiredLevel()) {
					message("You are not a high enough level to use this item");
					message("You need to have a " + Formulae.statArray[requiredSkillIndex] + " level of "
							+ requiredLevel);
					item.setWielded(false);
					updateWornItems(item.getDef().getWieldPosition(),
							getSettings().getAppearance().getSprite(item.getDef().getWieldPosition()));
					ActionSender.sendInventoryUpdateItem(this, slot);
				}
			}
		}
		ActionSender.sendEquipmentStats(this);
	}

	/**
	 * Restricts P2P stuff in F2P wilderness.
	 */
	public void unwieldMembersItems() {
		if (getLocation().inWilderness() && (!getLocation().isMembersWild())) {
			boolean found = false;
			for (Item i : getInventory().getItems()) {

				if (i.isWielded() && i.getDef().isMembersOnly()) {
					getInventory().unwieldItem(i, true);
					found = true;
				}
				if (i.getID() == 2109 && i.isWielded()) {
					getInventory().unwieldItem(i, true);
				}
			}
			if (found) {
				message("Members objects can only be wield above wild level P2P Gate " + World.membersWildStart + " - "
						+ World.membersWildMax);

				ActionSender.sendInventory(this);
				ActionSender.sendEquipmentStats(this);
			}
			for (int i = 0; i < 3; i++) {
				int min = skills.getLevel(i);
				int max = skills.getMaxStat(i);
				int baseStat = min > max ? max : min;
				int newStat = baseStat + DataConversions.roundUp((max / 100D) * 10) + 2;
				if (min > newStat || (min > max && (i == 1 || i == 0))) {
					skills.setLevel(i, max);
				}
			}
			if (skills.getLevel(Skills.RANGE) > skills.getMaxStat(Skills.RANGE)) {
				skills.setLevel(Skills.RANGE, skills.getMaxStat(Skills.RANGE));
			}
		}

	}
	private int bankSize = 200;

	public int getBankSize() {
		return bankSize;
	}

	public void setBankSize(int size) {
		this.bankSize = size;
	}

	public Bank getBank() {
		return bank;
	}

	public Cache getCache() {
		return cache;
	}

	public long getCastTimer() {
		return lastSpellCast;
	}

	public int getClick() {
		return click;
	}

	@Override
	public int getCombatStyle() {
		return combatStyle;
	}

	public String getCorrectSleepword() {
		return correctSleepword;
	}

	public String getCurrentIP() {
		return currentIP;
	}

	public long getCurrentLogin() {
		return currentLogin;
	}

	public int getDaysSinceLastLogin() {
		long now = Calendar.getInstance().getTimeInMillis() / 1000;
		return (int) ((now - lastLogin) / 86400);
	}

	public int getDaysSubscriptionLeft() {
		long now = (System.currentTimeMillis() / 1000);
		if (subscriptionExpires == 0 || now >= subscriptionExpires) {
			return 0;
		}
		double days = (double) (subscriptionExpires - now) / (double) 86400;
		if (days > 0.0 && days < 1.0) {
			return 1;
		}
		return (int) Math.round(days);
	}

	public PrayerDrainEvent getDrainer() {
		return prayerDrainEvent;
	}

	public int getDrainRate() {
		return drainRate;
	}

	public int getFatigue() {
		return fatigue;
	}

	public int getGroupID() {
		return groupID;
	}

	public int getIncorrectSleepTimes() {
		return incorrectSleepTries;
	}

	public Npc getInteractingNpc() {
		return interactingNpc;
	}

	public Inventory getInventory() {
		return inventory.get();
	}

	public String getLastIP() {
		return lastIP;
	}

	public long getLastLogin() {
		return lastLogin;
	}

	public long getLastPing() {
		return lastPing;
	}

	public int getMagicPoints() {
		int points = 1;
		for (Item item : getInventory().getItems()) {
			if (item.isWielded()) {
				points += item.getDef().getMagicBonus();
			}
		}
		return points < 1 ? 1 : points;
	}

	public Menu getMenu() {
		return menu;
	}

	public MenuOptionListener getMenuHandler() {
		return menuHandler;
	}

	public int getMinutesMuteLeft() {
		long now = System.currentTimeMillis();
		return (int) ((getMuteExpires() - now) / 60000);
	}

	public long getMuteExpires() {
		if (getCache().hasKey("mute_expires"))
			return getCache().getLong("mute_expires");
		else
			return 0;
	}

	public int getOption() {
		return questionOption;
	}

	public int getOwner() {
		return owner;
	}

	public String getPassword() {
		return password;
	}

	public int getPrayerPoints() {
		int points = 1;
		for (Item item : getInventory().getItems()) {
			if (item.isWielded()) {
				points += item.getDef().getPrayerBonus();
			}
		}
		return points < 1 ? 1 : points;
	}

	public int getQuestPoints() {
		return questPoints;
	}

	public int getQuestStage(int id) {
		if (getQuestStages().containsKey(id)) {
			return getQuestStages().get(id);
		}
		return 0;
	}

	public int getQuestStage(QuestInterface q) {
		if (getQuestStages().containsKey(q.getQuestId())) {
			return getQuestStages().get(q.getQuestId());
		}
		return 0;
	}

	public int getRangeEquip() {
		for (Item item : getInventory().getItems()) {
			if (item.isWielded() && (DataConversions.inArray(Formulae.bowIDs, item.getID())
					|| DataConversions.inArray(Formulae.xbowIDs, item.getID()))) {
				return item.getID();
			}
		}
		return -1;
	}

	public int getThrowingEquip() {
		for (Item item : getInventory().getItems()) {
			if (item.isWielded() && (DataConversions.inArray(Formulae.throwingIDs, getEquippedWeaponID()) && item.getDef().getWieldPosition() == 4)) {
				return item.getID();
			}
		}
		return -1;
	}

	public RangeEvent getRangeEvent() {
		return rangeEvent;
	}

	public ThrowingEvent getThrowingEvent() {
		return throwingEvent;
	}

	public String getRankHeader() {
		if (isAdmin()) {
			return "@yel@";
		} else if (groupID == 2) {
			return "@gre@";
		}
		return "";
	}

	public Channel getChannel() {
		return channel;
	}

	public Shop getShop() {
		return shop;
	}

	public DelayedEvent getSkullEvent() {
		return skullEvent;
	}

	public DelayedEvent getChargeEvent() {
		return chargeEvent;
	}

	public int getSkullTime() {
		if (isSkulled() && getSkullType() == 1) {
			return skullEvent.timeTillNextRun();
		}
		return 0;
	}

	public int getChargeTime() {
		if (isCharged()) {
			return chargeEvent.timeTillNextRun();
		}
		return 0;
	}

	public String getSleepword() {
		return sleepword;
	}

	public int getSpellWait() {
		return DataConversions.roundUp((1600 - (System.currentTimeMillis() - lastSpellCast)) / 1000D);
	}

	public Action getStatus() {
		return status;
	}

	public long getSubscriptionExpires() {
		return subscriptionExpires;
	}

	public String getUsername() {

		return username;
	}

	public long getUsernameHash() {
		if (getAttribute("fakeuser", null) != null) {
			return DataConversions.usernameToHash((String) getAttribute("fakeuser", null));
		}
		return usernameHash;
	}

	@Override
	public int getArmourPoints() {
		int points = 1;
		for (Item item : getInventory().getItems()) {
			if (item.isWielded()) {
				points += item.getDef().getArmourBonus();
			}
		}
		return points < 1 ? 1 : points;
	}

	@Override
	public int getWeaponAimPoints() {
		int points = 1;
		for (Item item : getInventory().getItems()) {
			if (item.isWielded()) {
				points += item.getDef().getWeaponAimBonus();
			}
		}
		return points < 1 ? 1 : points;
	}

	@Override
	public int getWeaponPowerPoints() {
		int points = 1;
		for (Item item : getInventory().getItems()) {
			if (item.isWielded()) {
				points += item.getDef().getWeaponPowerBonus();
			}
		}
		return points < 1 ? 1 : points;
	}

	public int[] getWornItems() {
		return wornItems;
	}

	public void handleWakeup() {
		fatigue = sleepStateFatigue;
		ActionSender.sendFatigue(this);
	}

	public void incQuestExp(int i, double amount) {
		skills.addExperience(i, amount);
	}

	public double getExperienceRate(int skill) {
		double multiplier = 0;
		/**
		 * Skilling Experience Rate
		 **/
		if (skill >= 4 && skill <= 17) {
			multiplier = Constants.GameServer.SKILLING_EXP_RATE; // 2.0 default.
			if (isSubscriber()) {
				multiplier += Constants.GameServer.SUBSCRIBER_EXP_RATE;// 1.0+ boost (combat and skilling).
			}
			if (isPremiumSubscriber()) {
				multiplier += Constants.GameServer.PREMIUM_EXP_RATE; // 0.5+ boost skilling 
			}
			if (getLocation().inWilderness() && !getLocation().inBounds(220, 108, 225, 111)) {
				multiplier += Constants.GameServer.WILDERNESS_BOOST; // 0.5+ boost in wild.
				if (isSkulled()) {
					multiplier += Constants.GameServer.SKULL_BOOST; // 1.0+ boost with skull.
				}
			}
		}
		/**
		 * Combat Experience Rate
		 **/
		else if (skill >= 0 && skill <= 3) { // Attack, Strength, Defense & HP bonus.
			multiplier = Constants.GameServer.COMBAT_EXP_RATE; // 3.0 default. // 8.0
			if(isSubscriber()) {
				multiplier += 2.0; // 1.0+ subscriber combat boost. // 10
			}
			if (isPremiumSubscriber()) {
				multiplier += 2.0; // 1.0+ premium combat boost. // 12
			}
			if (getLocation().inWilderness()) {
				multiplier += Constants.GameServer.WILDERNESS_BOOST; // 0.5+ boost in wild.
				if (isSkulled()) {
					multiplier += Constants.GameServer.SKULL_BOOST; // 1.0+ boost with skull.
				}
			}
		}

		/**
		 * Double Experience
		 **/
		if(Constants.GameServer.IS_DOUBLE_EXP) {
			multiplier *= 2;
		}

		/**
		 * Experience Elixir
		 **/
		if(getCache().hasKey("elixir_time")) {
			if (getElixir() <= 0) {
				getCache().remove("elixir_time");
				ActionSender.sendElixirTimer(this, 0);
			} else {
				multiplier += 1.0;
			}
		}

		return multiplier;
	}

	public void incExp(int skill, double hpXP, boolean useFatigue) {
		if (useFatigue) {
			if (fatigue >= 7500) {
				ActionSender.sendMessage(this, "@gre@You are too tired to gain experience, get some rest!");
				return;
			}
			if (fatigue >= 7200) {
				ActionSender.sendMessage(this, "@gre@You start to feel tired, maybe you should rest soon.");
			}
			if (skill >= 3 && useFatigue) {
				int famt = (int) ((8 * hpXP / 5) / 3);
				if (isSubscriber()) {
					famt = famt / 2;
				}
				fatigue += famt;
				ActionSender.sendFatigue(this);
			}
			if (fatigue > 7500) {
				fatigue = 7500;
			}
		}
		if (getLocation().onTutorialIsland()) {
			if (skills.getExperience(skill) + hpXP > 200) {
				if (skill != 3) {
					skills.setExperience(skill, 200);
				} else {
					skills.setExperience(skill, 1200);
				}
			}
		}

		hpXP *= getExperienceRate(skill);
		skills.addExperience(skill, hpXP);
	}

	public void incQuestPoints(int amount) {
		setQuestPoints(getQuestPoints() + amount);
	}

	public void incrementSleepTries() {
		incorrectSleepTries++;
	}

	public boolean isAdmin() {
		return groupID == 1;
	}

	public boolean isChangingAppearance() {
		return changingAppearance;
	}

	public boolean isAntidoteProtected() {
		return System.currentTimeMillis() - lastAntidote < 90000;
	}

	public boolean isInBank() {
		return inBank;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public boolean isMale() {
		return maleGender;
	}

	public boolean isMaleGender() {
		return maleGender;
	}

	public boolean isMod() {
		return groupID == 2 || isAdmin();
	}

	public boolean isMuted() {
		if (getMuteExpires() == 0)
			return false;
		if (getMuteExpires() == -1)
			return true;

		return getMuteExpires() - System.currentTimeMillis() > 0;
	}

	public boolean isRanging() {
		return rangeEvent != null || throwingEvent != null;
	}

	public boolean isReconnecting() {
		return reconnecting;
	}

	public boolean isRequiresOfferUpdate() {
		return requiresOfferUpdate;
	}

	public boolean isSkulled() {
		return skullEvent != null;
	}

	public boolean isCharged() {
		return chargeEvent != null;
	}

	public int getSkullType() {
		int type = 0;
		if (isSkulled()) {
			type = 1;
		}
		return type;
	}

	public boolean isSleeping() {
		return sleeping;
	}

	public boolean isSubscriber() {
		if (isMod() || isAdmin())
			return false;

		if (getDaysSubscriptionLeft() == 0) {
			return false;
		}
		return groupID == 6;
	}

	public boolean isSuspiciousPlayer() {
		return suspiciousPlayer;
	}

	@Override
	public void killedBy(Mob mob) {
		if (!loggedIn) {
			return;
		}

		ActionSender.sendSound(this, "death");
		ActionSender.sendDied(this);

		if (getAttribute("projectile", null) != null) {
			ProjectileEvent projectileEvent = getAttribute("projectile");
			projectileEvent.setCanceled(true);
		}
		getSettings().getAttackedBy().clear();

		getCache().store("last_death", System.currentTimeMillis());

		Player player = mob instanceof Player ? (Player) mob : null;
		boolean stake = getDuel().isDuelActive() || (player != null && player.getDuel().isDuelActive());

		if (player != null) {
			player.message("You have defeated " + getUsername() + "!");
			ActionSender.sendSound(player, "victory");
			if (player.getLocation().inWilderness()) {
				int id = -1;
				if(player.getKillType() == 0) {
					id = player.getEquippedWeaponID();
					if (id == -1 || id == 59 || id == 60)
						id = 16;
				} else if(player.getKillType() == 1) {
					id = -1;
				} else if(player.getKillType() == 2) {
					id = -2;
				}
				world.sendKilledUpdate(this.getUsernameHash(), player.getUsernameHash() , id);
				player.incKills();
				this.incDeaths();
				GameLogging.addQuery(new LiveFeedLog(player, "has PKed <strong>" + this.getUsername() + "</strong>"));
			} else if (stake) {
				GameLogging.addQuery(new LiveFeedLog(player,
						"has just won a stake against <strong>" + this.getUsername() + "</strong>"));
			}
		}
		if (stake) {
			getDuel().dropOnDeath();
		} else {
			getInventory().dropOnDeath(mob);
		}
		if(isIronMan(3)) {
			updateHCIronman(1);
			ActionSender.sendIronManMode(this);
			GameLogging.addQuery(new LiveFeedLog(this, "has died and lost the HC Iron Man Rank!"));
		}
		removeSkull(); // destroy
		resetCombatEvent();
		world.registerItem(new GroundItem(20, getX(), getY(), 1, player));
		if((!getCache().hasKey("death_location_x") && !getCache().hasKey("death_location_y")) || getDaysSubscriptionLeft() <= 0) {
			setLocation(Point.location(122, 647), true);
		} else {
			setLocation(Point.location(getCache().getInt("death_location_x"), getCache().getInt("death_location_y")), true);
		}
		setTeleporting(true);
		ActionSender.sendWorldInfo(this);
		ActionSender.sendEquipmentStats(this);
		ActionSender.sendInventory(this);

		resetPath();
		this.cure();
		prayers.resetPrayers();
		skills.normalize();
	}


	public int getEquippedWeaponID() {
		for (Item i : getInventory().getItems()) {
			if (i.isWielded() && (i.getDef().getWieldPosition() == 4))
				return i.getID();
		}
		return -1;
	}

	public boolean loggedIn() {
		return loggedIn;
	}

	public void message(String string) {
		resetMenuHandler();
		setOption(-1);
		ActionSender.sendMessage(this, string);
	}

	public void playerServerMessage(MessageType type, String string) {
		ActionSender.sendPlayerServerMessage(this, type, string);
	}

	public void teleport(int x, int y) {
		teleport(x, y, false);
	}

	private Queue<PrivateMessage> privateMessageQueue = new LinkedList<PrivateMessage>();

	private long lastSave = System.currentTimeMillis();

	public void addPrivateMessage(PrivateMessage privateMessage) {
		if (getPrivateMessageQueue().size() < 2) {
			getPrivateMessageQueue().add(privateMessage);
		}
	}

	public void addToPacketQueue(Packet e) {
		ping();
		synchronized (incomingPacketLock) {
			incomingPackets.add(e);
		}
	}

	public void ping() {
		lastPing = System.currentTimeMillis();
	}

	public void playSound(String sound) {
		ActionSender.sendSound(this, sound);
	}

	private int actionsMouseStill = 0;
	private long lastMouseMoved = 0;

	public void checkForMouseMovement(boolean movedMouse) {
		if (!movedMouse) {
			actionsMouseStill++;

			float minutesFlagged = (float) (System.currentTimeMillis() - lastMouseMoved) / (float) 60000;
			if (actionsMouseStill >= 30 && minutesFlagged >= 1) {
				String string = "Check " + getUsername() + "! " + actionsMouseStill
						+ " actions with mouse still. Mouse was last moved " + String.format("%.02f", minutesFlagged)
						+ " mins ago";

				for (Player p : World.getWorld().getPlayers()) { 
					if (p.isMod()) {
						p.message("@red@Server@whi@: " + string);
					}
				}
				setSuspiciousPlayer(true);
			}
		} else {
			actionsMouseStill = 0;
			lastMouseMoved = System.currentTimeMillis();
		}
	}

	public void process() {
		if (System.currentTimeMillis() - lastSave >= 300000) {
			save();
			lastSave = System.currentTimeMillis();
		}
	}

	public void processIncomingPackets() {
		if (!channel.isOpen() && !channel.isWritable()) {
			return;
		}
		synchronized (incomingPacketLock) {
			for (Packet p : incomingPackets) {
				PacketHandler ph = PacketHandlerLookup.get(p.getID());
				if (ph != null && p.getBuffer().readableBytes() >= 0) {
					try {
						ph.handlePacket(p, this);
					} catch (Exception e) {
						LOGGER.catching(e);
						unregister(false, "Malformed packet!");
					}
				}
			}
			incomingPackets.clear();
		}
	}

	public void sendOutgoingPackets() {
		if (!channel.isOpen() && !channel.isWritable()) {
			return;
		}
		synchronized (outgoingPacketsLock) {
			for (Packet outgoing : outgoingPackets) {
				channel.writeAndFlush(outgoing);
			}
			// channel.flush();

			outgoingPackets.clear();
		}
	}

	public void removeSkull() {
		if (skullEvent == null) {
			return;
		}
		skullEvent.stop();
		skullEvent = null;
		getUpdateFlags().setAppearanceChanged(true);
	}

	public boolean requiresOfferUpdate() {
		return requiresOfferUpdate;
	}

	public void resetAll() {
		resetAllExceptTradeOrDuel();
		getTrade().resetAll();
		getDuel().resetAll();
	}

	public void resetAllExceptDueling() {
		resetAllExceptTradeOrDuel();
		getTrade().resetAll();
	}

	private void resetAllExceptTradeOrDuel() {
		resetCannonEvent();
		setAttribute("bank_pin_entered", "cancel");
		setWalkToAction(null);
		if (getMenu() != null) {
			menu = null;
		}
		if (getMenuHandler() != null) {
			resetMenuHandler();
		}
		if (accessingBank()) {
			resetBank();
		}
		if (accessingShop()) {
			resetShop();
		}
		if (isFollowing()) {
			resetFollowing();
		}
		if (isRanging()) {
			resetRange();
		}
		setInteractingNpc(null);
		setStatus(Action.IDLE);
	}

	public void resetAllExceptTrading() {
		resetAllExceptTradeOrDuel();
		getDuel().resetAll();
	}

	public void resetBank() {
		setAccessingBank(false);
		ActionSender.hideBank(this);
	}

	public void resetMenuHandler() {
		menu = null;
		menuHandler = null;
		ActionSender.hideMenu(this);
	}

	public void resetRange() {
		if (rangeEvent != null) {
			rangeEvent.stop();
			rangeEvent = null;
		}
		if(throwingEvent != null) {
			throwingEvent.stop();
			throwingEvent = null;
		}
		setStatus(Action.IDLE);
	}

	public void resetShop() {
		if (shop != null) {
			shop.removePlayer(this);
			shop = null;
			ActionSender.hideShop(this);
		}
	}

	public void resetSleepTries() {
		incorrectSleepTries = 0;
	}

	public void save() {
		Server.getPlayerDataProcessor().addSaveRequest(this);
	}

	public void sendMemberErrorMessage() {
		message(MEMBER_MESSAGE);
	}

	public void sendQuestComplete(int questId) { // REMEMBER THIS
		if (getQuestStage(questId) != -1) {
			world.getQuest(questId).handleReward(this);
			updateQuestStage(questId, -1);
			ActionSender.sendStats(this);
			GameLogging.addQuery(new LiveFeedLog(this,
					"just completed <strong><font color=#00FF00>" + world.getQuest(questId).getQuestName()
					+ "</font></strong> quest! He now has <strong><font color=#E1E100>" + this.getQuestPoints()
					+ "</font></strong> quest points"));
		}
	}

	public void setAccessingBank(boolean b) {
		inBank = b;
	}

	public void setAccessingShop(Shop shop) {
		this.shop = shop;
		if (shop != null) {
			shop.addPlayer(this);
		}
	}

	public void setBank(Bank b) {
		bank = b;
	}

	public void setBatchEvent(BatchEvent batchEvent) {
		if (batchEvent != null) {
			this.batchEvent = batchEvent;
			Server.getServer().getEventHandler().add(batchEvent);
		}
	}

	public void setCastTimer() {
		lastSpellCast = System.currentTimeMillis();
	}

	public void setChangingAppearance(boolean b) {
		changingAppearance = b;
	}

	public void setAntidoteProtection() {
		lastAntidote = System.currentTimeMillis();
	}

	public void setClick(int click) {
		this.click = click;
	}

	public void setCombatStyle(int style) {
		combatStyle = style;
	}

	public void setCorrectSleepword(String correctSleepword) {
		this.correctSleepword = correctSleepword;
	}

	public void setCurrentIP(String currentIP) {
		this.currentIP = currentIP;
	}

	public void setCurrentLogin(long currentLogin) {
		this.currentLogin = currentLogin;
	}

	public void setDrainRate(int rate) {
		drainRate = rate;
	}

	public void setFatigue(int fatigue) {
		this.fatigue = fatigue;
		ActionSender.sendFatigue(this);
	}

	public void setGroupID(int id) {
		groupID = id;
	}

	public void setInBank(boolean inBank) {
		this.inBank = inBank;
	}

	public void setInteractingNpc(Npc interactingNpc) {
		this.interactingNpc = interactingNpc;
	}

	public void setInventory(Inventory i) {
		inventory.set(i);
	}

	public void setLastIP(String ip) {
		lastIP = ip;
	}

	public void setLastLogin(long l) {
		lastLogin = l;
	}

	public void setLastReport() {
		lastReport = System.currentTimeMillis();
	}

	public void setLastReport(long lastReport) {
		this.lastReport = lastReport;
	}

	public void setLoggedIn(boolean loggedIn) {
		if (loggedIn) {
			currentLogin = System.currentTimeMillis();
			if (getCache().hasKey("poisoned")) {
				startPoisonEvent();
				PoisonEvent poisonEvent = getAttribute("poisonEvent", null);
				poisonEvent.setPoisonPower(getCache().getInt("poisoned"));
			}
			prayerDrainEvent = new PrayerDrainEvent(this, Integer.MAX_VALUE);
			Server.getServer().getGameEventHandler().add(prayerDrainEvent);
			Server.getServer().getGameEventHandler().add(statRestorationEvent);
		}
		this.loggedIn = loggedIn;
	}

	public void setMale(boolean male) {
		maleGender = male;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public void setMenuHandler(MenuOptionListener menuHandler) {
		menuHandler.setOwner(this);
		this.menuHandler = menuHandler;
	}

	public void setMuteExpires(long l) {
		getCache().store("mute_expires", l);
	}

	public void setOption(int option) {
		this.questionOption = option;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	public void setQuestStage(int q, int stage) {
		getQuestStages().put(q, stage);
	}

	public void updateQuestStage(int q, int stage) {
		getQuestStages().put(q, stage);
		ActionSender.sendQuestInfo(this, q, stage);
	}

	public void updateQuestStage(QuestInterface q, int stage) {
		getQuestStages().put(q.getQuestId(), stage);
		ActionSender.sendQuestInfo(this, q.getQuestId(), stage);
	}

	private Map<Integer, Integer> achievements = new ConcurrentHashMap<>();

	public Map<Integer, Integer> getAchievements() {
		return achievements;
	}

	public void setAchievementStatus(int achid, int status) { 
		getAchievements().put(achid, status); 

		AchievementSystem.achievementListGUI(this, achid, status);
	} 

	public void updateAchievementStatus(Achievement ach, int status) { 
		getAchievements().put(ach.getId(), status); 

		AchievementSystem.achievementListGUI(this, ach.getId(), status);
	} 

	public int getAchievementStatus(int id) {
		if (getAchievements().containsKey(id)) {
			return getAchievements().get(id);
		}
		return 0;
	}


	public void setRangeEvent(RangeEvent event) {
		if (rangeEvent != null) {
			rangeEvent.stop();
		}
		rangeEvent = event;
		setStatus(Action.RANGING_MOB);
		Server.getServer().getGameEventHandler().add(rangeEvent);
	}

	public void setThrowingEvent(ThrowingEvent event) {
		if (throwingEvent != null) {
			throwingEvent.stop();
		}
		throwingEvent = event;
		setStatus(Action.RANGING_MOB);
		Server.getServer().getGameEventHandler().add(throwingEvent);
	}

	public void setReconnecting(boolean reconnecting) {
		this.reconnecting = reconnecting;
	}

	public void setRequiresOfferUpdate(boolean b) {
		requiresOfferUpdate = b;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public void setSkulledOn(Player player) {
		player.getSettings().addAttackedBy(this);
		if (System.currentTimeMillis() - getSettings().lastAttackedBy(player) > 1200000) {
			addSkull(1200000);
		}
		player.getUpdateFlags().setAppearanceChanged(true);
	}

	public void setSkullEvent(DelayedEvent skullEvent) {
		this.skullEvent = skullEvent;
	}

	public void setChargeEvent(DelayedEvent chargeEvent) {
		this.chargeEvent = chargeEvent;
	}

	public void setSleeping(boolean isSleeping) {
		this.sleeping = isSleeping;
	}

	public void setSleepword(String sleepword) {
		this.sleepword = sleepword;
	}

	public void setSpellFail() {
		lastSpellCast = System.currentTimeMillis() + 20000;
	}

	public void setStatus(Action a) {
		status = a;
	}

	public void setSubscriptionExpires(long expires) {
		subscriptionExpires = expires;
	}

	public void setSuspiciousPlayer(boolean suspicious) {
		suspiciousPlayer = suspicious;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setUsernameHash(long usernameHash) {
		this.usernameHash = usernameHash;
	}

	public void setWornItems(int[] worn) {
		wornItems = worn;
		getUpdateFlags().setAppearanceChanged(true);
	}

	public void startSleepEvent(final boolean bed) {
		sleepEvent = new DelayedEvent(this, 600) {
			@Override
			public void run() {
				if (owner.isRemoved() || sleepStateFatigue == 0 || !sleeping) {
					matchRunning = false;
					return;
				}

				if (bed) {
					owner.sleepStateFatigue -= 2100;
				} else {
					owner.sleepStateFatigue -= 431;
				}

				if (owner.sleepStateFatigue < 0) {
					owner.sleepStateFatigue = 0;
				}
				ActionSender.sendSleepFatigue(owner, owner.sleepStateFatigue);
			}
		};
		sleepStateFatigue = fatigue;
		ActionSender.sendSleepFatigue(this, sleepStateFatigue);
		Server.getServer().getEventHandler().add(sleepEvent);
	}

	public void teleport(int x, int y, boolean bubble) {
		if (bubble && PluginHandler.getPluginHandler().blockDefaultAction("Teleport", new Object[] { this })) {
			return;
		}
		if (inCombat()) {
			combatEvent.resetCombat();
		}
		for (Player p : getViewArea().getPlayersInView()) {
			if (bubble) {
				ActionSender.sendTeleBubble(p, getX(), getY(), false);
			}
		}
		if (bubble) {
			ActionSender.sendTeleBubble(this, getX(), getY(), false);
		}
		setLocation(Point.location(x, y), true);
		resetPath();
		ActionSender.sendWorldInfo(this);
	}

	@Override
	public void setLocation(Point p, boolean teleported) {
		if (getSkullType() == 2)
			getUpdateFlags().setAppearanceChanged(true);
		else if (getSkullType() == 0)
			getUpdateFlags().setAppearanceChanged(true);
		if(!super.getLocation().inWilderness() && p.inWilderness())
		{
			if (!canUsePool()) {
				message("You must wait for " + secondsUntillPool() + " seconds after death to walk in wilderness");
				getWalkingQueue().setPath(null);
				return;
			}
		}
		/*if(super.getLocation() != null && p != null)
		{
			if(super.getLocation().inWilderness() && !p.inWilderness())
			{
				World.getWildernessIPTracker().remove(getCurrentIP());
			}
		}*/

		super.setLocation(p, teleported);

	}

	@Override
	public String toString() {
		return "[Player:" + username + "]";
	}

	public boolean tradeDuelThrottling() {
		long now = System.currentTimeMillis();
		if (now - lastTradeDuelRequest > 1000) {
			lastTradeDuelRequest = now;
			return false;
		}
		return true;
	}

	public void updateWornItems(int index, int id) {
		wornItems[index] = id;
		getUpdateFlags().setAppearanceChanged(true);
	}

	public Queue<PrivateMessage> getPrivateMessageQueue() {
		return privateMessageQueue;
	}

	public Map<Integer, Integer> getQuestStages() {
		return questStages;
	}

	public void setQuestPoints(int questPoints) {
		this.questPoints = questPoints;
	}

	/** KILLS N DEATHS **/
	private int kills = 0;
	private int deaths = 0;
	private WalkToAction walkToAction;

	public int getKills() {
		return kills;
	}

	public void setKills(int i) {
		this.kills = i;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int i) {
		this.deaths = i;
	}

	public void incDeaths() {
		deaths++;
	}

	public void incKills() {
		kills++;
	}

	public void addKill(boolean add) {
		if (!add) {
			kills++;
		}
	}

	public void setWalkToAction(WalkToAction action) {
		this.walkToAction = action;
	}

	public WalkToAction getWalkToAction() {
		return walkToAction;
	}

	private long premiumSubscriptionExpires;

	private Trade trade;

	public int premiumSubDaysLeft() {
		long now = (System.currentTimeMillis() / 1000);
		if (premiumSubscriptionExpires == 0 || now >= premiumSubscriptionExpires) {
			return 0;
		}
		double days = (double) (premiumSubscriptionExpires - now) / (double) 86400;
		if (days > 0.0 && days < 1.0) {
			return 1;
		}
		return (int) Math.round(days);
	}

	public boolean isPremiumSubscriber() {
		return premiumSubDaysLeft() > 0;
	}

	public long getPremiumExpires() {
		return premiumSubscriptionExpires;
	}

	public void setPremiumExpires(long long1) {
		this.premiumSubscriptionExpires = long1;
	}
	public int getElixir() {
		if (getCache().hasKey("elixir_time")) {
			int now = (int) (System.currentTimeMillis() / 1000);
			int time = ((int) getCache().getLong("elixir_time") - now);
			return (time < 0) ? 0 : time;
		}
		return 0;
	}
	public void addElixir(int seconds) {
		long elixirTime = seconds;
		long now = System.currentTimeMillis() / 1000;
		long experience = (now + elixirTime);
		getCache().store("elixir_time", experience);
	}

	public void removeElixir() {
		if(getCache().hasKey("elixir_time"))
			getCache().remove("elixir_time");

		ActionSender.sendElixirTimer(this, 0);
	}

	public int getGlobalBlock() {
		if (getCache().hasKey("setting_block_global")) {
			return getCache().getInt("setting_block_global");
		}
		return 1;
	}

	public boolean getClanInviteSetting() {
		if (getCache().hasKey("p_block_invites")) {
			return getCache().getBoolean("p_block_invites");
		}
		return true;
	}

	public boolean isPlayer() {
		return true;
	}

	public boolean isNpc() {
		return false;
	}

	public boolean holdNChoose() {
		if (getCache().hasKey("setting_android_holdnchoose")) {
			return getCache().getBoolean("setting_android_holdnchoose");
		}
		return true;
	}

	public PlayerSettings getSettings() {
		return playerSettings;
	}

	public Social getSocial() {
		return social;
	}

	public Prayers getPrayers() {
		return prayers;
	}

	public int getIcon() {
		if (groupID == 1)
			return 2;
		if (groupID == 2)
			return 1;
		if (isIronMan(1))
			return 5;
		if (isIronMan(2))
			return 6;
		if (isIronMan(3))
			return 7;
		return 0;
	}

	public Trade getTrade() {
		return trade;
	}

	public Duel getDuel() {
		return duel;
	}
	private int databaseID;

	public int getDatabaseID() {
		return databaseID;
	}

	public void setDatabaseID(int i) {
		this.databaseID = i;
	}
	public int getUnreadMessages() {
		return unreadMessages + 1;
	}
	public void setUnreadMessages(int unreadMessages) {
		this.unreadMessages = unreadMessages;
	}
	public int getTeleportStones() {
		return teleportStones;
	}
	public void setTeleportStones(int stones) {
		this.teleportStones = stones;
	}

	private Clan clan;
	private ClanInvite activeClanInvitation;

	public void setClan(Clan clan) {
		this.clan = clan;
		getUpdateFlags().setAppearanceChanged(true);
	}

	public Clan getClan() {
		return clan;
	}

	public void setActiveClanInvite(ClanInvite inv) {
		activeClanInvitation = inv;
	}

	public ClanInvite getActiveClanInvite() {
		return activeClanInvitation;
	}
	public long secondsUntillPool() {
		return (90 - ((System.currentTimeMillis() - (getCache().hasKey("last_death") ? getCache().getLong("last_death") : 0)) / 1000));
	}
	public boolean canUsePool() {
		return System.currentTimeMillis() - (getCache().hasKey("last_death") ? getCache().getLong("last_death") : 0) > 90000;
	}

}
