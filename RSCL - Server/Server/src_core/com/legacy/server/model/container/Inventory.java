package com.legacy.server.model.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import com.legacy.server.Constants;
import com.legacy.server.content.achievement.AchievementSystem;
import com.legacy.server.model.entity.GroundItem;
import com.legacy.server.model.entity.Mob;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.entity.player.Prayers;
import com.legacy.server.model.world.World;
import com.legacy.server.net.rsc.ActionSender;
import com.legacy.server.sql.GameLogging;
import com.legacy.server.sql.query.logs.DeathLog;
import com.legacy.server.sql.query.logs.GenericLog;
import com.legacy.server.util.rsc.Formulae;

public class Inventory {

	/**
	 * The maximum size of an inventory
	 */
	public static final int MAX_SIZE = 30;
	/**
	 * World instance
	 */
	private static World world = World.getWorld();

	private ArrayList<Item> list = new ArrayList<Item>();

	private Player player;

	public Inventory(Player player) {
		this.player = player;
	}

	public Inventory() {
	}

	public void add(Item item) {
		add(item, true);
	}

	public void add(Item itemToAdd, boolean sendInventory) {
		synchronized (list) {
			if (itemToAdd.getAmount() <= 0) {
				return;
			}
			// TODO Achievement gather item task?? keep or remove.

			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			for (int i = 0; i < stackTrace.length; i++) {
				if (stackTrace[i].toString().contains("com.legacy.server.plugins.")) {
					AchievementSystem.checkAndIncGatherItemTasks(player, itemToAdd);
				}
			}

			if (itemToAdd.getAttribute("npcdrop", false)) {
				AchievementSystem.checkAndIncGatherItemTasks(player, itemToAdd);
			}

			if (itemToAdd.getDef().isStackable()) {
				for (int index = 0; index < list.size(); index++) {
					Item existingStack = list.get(index);
					if (itemToAdd.equals(existingStack) && existingStack.getAmount() < Integer.MAX_VALUE) {
						existingStack.setAmount(existingStack.getAmount() + itemToAdd.getAmount());
						if (sendInventory)
							ActionSender.sendInventoryUpdateItem(player, index);
						return;
					}
				}
			} else if (itemToAdd.getAmount() > 1 && !itemToAdd.getDef().isStackable()) {
				itemToAdd.setAmount(1);
			}

			if (this.full()) {
				player.message("Your Inventory is full, the " + itemToAdd.getDef().getName() + " drops to the ground!");
				world.registerItem(
						new GroundItem(itemToAdd.getID(), player.getX(), player.getY(), itemToAdd.getAmount(), player));
				GameLogging.addQuery(new GenericLog(player.getUsername() + " dropped(inventory full) "
						+ itemToAdd.getID() + " x" + itemToAdd.getAmount() + " at " + player.getLocation().toString()));
				return;
			}
			list.add(itemToAdd);
			if (sendInventory)
				ActionSender.sendInventoryUpdateItem(player, list.size() - 1);
		}
		return;
	}

	public boolean canHold(Item item) {
		synchronized (list) {
			return (MAX_SIZE - list.size()) >= getRequiredSlots(item);
		}
	}

	public boolean canHold(Item item, int addition) {
		synchronized (list) {
			return (MAX_SIZE - list.size() + addition) >= getRequiredSlots(item);
		}
	}

	public boolean contains(Item i) {
		synchronized (list) {
			return list.contains(i);
		}
	}

	public int countId(int id) {
		synchronized (list) {
			int temp = 0;
			for (Item i : list) {
				if (i.getID() == id) {
					temp += i.getAmount();
				}
			}
			return temp;
		}
	}

	public boolean full() {
		synchronized (list) {
			return list.size() >= MAX_SIZE;
		}
	}

	public Item get(int index) {
		synchronized (list) {
			if (index < 0 || index >= list.size()) {
				return null;
			}
			return list.get(index);
		}
	}

	public Item get(Item item) {
		synchronized (list) {
			for (int index = list.size() - 1; index >= 0; index--) {
				if (list.get(index).equals(item)) {
					return list.get(index);
				}
			}
		}
		return null;
	}

	public int getFreedSlots(Item item) {
		return (item.getDef().isStackable() && countId(item.getID()) > item.getAmount() ? 0 : 1);
	}

	public int getFreedSlots(List<Item> items) {
		int freedSlots = 0;
		for (Item item : items) {
			freedSlots += getFreedSlots(item);
		}
		return freedSlots;
	}

	public ArrayList<Item> getItems() {
		synchronized (list) {
			return list;
		}
	}

	public int getLastIndexById(int id) {
		synchronized (list) {
			for (int index = list.size() - 1; index >= 0; index--) {
				if (list.get(index).getID() == id) {
					return index;
				}
			}
		}
		return -1;
	}

	public int getRequiredSlots(Item item) {
		synchronized (list) {
			return (item.getDef().isStackable() && list.contains(item) ? 0 : 1);
		}
	}

	public int getRequiredSlots(List<Item> items) {
		int requiredSlots = 0;
		for (Item item : items) {
			requiredSlots += getRequiredSlots(item);
		}
		return requiredSlots;
	}

	public boolean hasItemId(int id) {
		synchronized (list) {
			for (Item i : list) {
				if (i.getID() == id)
					return true;
			}
		}

		return false;
	}

	public ListIterator<Item> iterator() {
		synchronized (list) {
			return list.listIterator();
		}
	}

	public void remove(int index) {
		synchronized (list) {
			Item item = get(index);
			if (item == null) {
				return;
			}
			remove(item.getID(), item.getAmount(), true);
		}
	}

	public int remove(int id, int amount, boolean sendInventory) {
		synchronized (list) {
			int size = list.size();
			ListIterator<Item> iterator = list.listIterator(size);

			for (int index = size - 1; iterator.hasPrevious(); index--) {
				Item i = iterator.previous();
				if (id == i.getID() && i != null && i.getAmount() >= amount) {
					if (i.getDef().isStackable() && amount < i.getAmount()) {
						i.setAmount(i.getAmount() - amount);
						ActionSender.sendInventoryUpdateItem(player, index);
					} else if (i.getDef().isStackable() && amount > i.getAmount()) {
						return -1;
					} else {
						if (i.isWielded()) {
							unwieldItem(i, false);
							ActionSender.sendEquipmentStats(player);
						}
						iterator.remove();
						ActionSender.sendRemoveItem(player, index);
					}
					return index;
				}
			}
		}
		return -1;
	}

	public int remove(int id, int amount) {
		return remove(id, amount, true);
	}

	public int remove(Item item) {
		return remove(item.getID(), item.getAmount(), true);
	}

	public int size() {
		synchronized (list) {
			return list.size();
		}
	}

	public void sort() {
		synchronized (list) {
			Collections.sort(list);
		}
	}

	public boolean wielding(int id) {
		synchronized (list) {
			for (Item i : list) {
				if (i.getID() == id && i.isWielded()) {
					return true;
				}
			}
		}
		return false;
	}

	public void replace(int i, int j) {
		remove(i, 1, false);
		add(new Item(j));
	}

	public int getFreeSlots() {
		return MAX_SIZE - size();
	}

	public void swap(int slot, int to) {
		if(slot <= 0 && to <= 0 && to == slot) {	
			return;
		}
		int idx = list.size() - 1;
		if(to > idx) {
			return;
		}
		Item item = get(slot);
		Item item2 = get(to);
		if (item != null && item2 != null) {
			list.set(slot, item2);
			list.set(to, item);
			ActionSender.sendInventory(player);
		}
	}

	public boolean insert(int slot, int to) {
		if(slot < 0 || to < 0 || to == slot) {	
			return false;
		}
		int idx = list.size() - 1;
		if(to > idx) {
			return false;
		}
		Item from = list.get(slot);
		Item[] array = list.toArray(new Item[list.size()]);
		if (slot >= array.length || from == null || to >= array.length) {
			return false;
		}
		array[slot] = null;
		if (slot > to) {
			int shiftFrom = to;
			int shiftTo = slot;
			for (int i = (to + 1); i < slot; i++) {
				if (array[i] == null) {
					shiftTo = i;
					break;
				}
			}
			Item[] slice = new Item[shiftTo - shiftFrom];
			System.arraycopy(array, shiftFrom, slice, 0, slice.length);
			System.arraycopy(slice, 0, array, shiftFrom + 1, slice.length);
		} else {
			int sliceStart = slot + 1;
			int sliceEnd = to;
			for (int i = (sliceEnd - 1); i >= sliceStart; i--) {
				if (array[i] == null) {
					sliceStart = i;
					break;
				}
			}
			Item[] slice = new Item[sliceEnd - sliceStart + 1];
			System.arraycopy(array, sliceStart, slice, 0, slice.length);
			System.arraycopy(slice, 0, array, sliceStart - 1, slice.length);
		}
		array[to] = from;
		list = new ArrayList<Item>(Arrays.asList(array));
		return true;
	}

	public void unwieldItem(Item affectedItem, boolean sound) {
		if (affectedItem == null || !affectedItem.isWieldable() || !getItems().contains(affectedItem)) {
			return;
		}

		affectedItem.setWielded(false);
		if (sound) {
			player.playSound("click");
		}
		player.updateWornItems(affectedItem.getDef().getWieldPosition(),
				player.getSettings().getAppearance().getSprite(affectedItem.getDef().getWieldPosition()));

		ActionSender.sendInventory(player);
		ActionSender.sendEquipmentStats(player);
	}

	public void wieldItem(Item item, boolean sound) {

		int requiredLevel = item.getDef().getRequiredLevel();
		int requiredSkillIndex = item.getDef().getRequiredSkillIndex();
		if (player.getSkills().getMaxStat(item.getDef().getRequiredSkillIndex()) < item.getDef().getRequiredLevel()) {
			player.message("You are not a high enough level to use this item");
			player.message("You need to have a " + Formulae.statArray[requiredSkillIndex] + " level of " + requiredLevel);
			return;
		}
		if (item.getDef().isFemaleOnly() && player.isMale()) {
			player.message("This piece of armor is for a female only.");
			return;
		}
		if ((item.getID() == 401 || item.getID() == 407)
				&& (player.getQuestStage(Constants.Quests.DRAGON_SLAYER) != -1)) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to complete the dragon slayer quest");
			return;
		}
		if (item.getID() == 593 && player.getQuestStage(Constants.Quests.LOST_CITY) != -1) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to complete the Lost city of zanaris quest");
			return;
		}
		if (item.getID() == 594 && (player.getQuestStage(Constants.Quests.HEROS_QUEST) != -1 || player.getQuestPoints() < 56)) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to complete the Hero's guild entry quest");
			return;
		}
		/*
		 * Hacky but works for god staffs and god capes.
		 */
		if(item.getID() == 1217 && (wielding(1213) || wielding(1214))) { // try to wear guthix staff
			player.message("you may not wield this staff while wearing a cape of another god");
			return;
		}
		if(item.getID() == 1218 && (wielding(1213) || wielding(1215))) { // try to wear sara staff
			player.message("you may not wield this staff while wearing a cape of another god");
			return;
		}
		if(item.getID() == 1216 && (wielding(1214) || wielding(1215))) { // try to wear zamorak staff
			player.message("you may not wield this staff while wearing a cape of another god");
			return;
		}
		if(item.getID() == 1215 && (wielding(1216) || wielding(1218))) { // try to wear guthix cape
			player.message("you may not wear this cape while wielding staffs of the other gods");
			return;
		}
		if(item.getID() == 1214 && (wielding(1216) || wielding(1217))) { // try to wear sara cape
			player.message("you may not wear this cape while wielding staffs of the other gods");
			return;
		}
		if(item.getID() == 1213 && (wielding(1217) || wielding(1218))) { // try to wear zamorak cape
			player.message("you may not wear this cape while wielding staffs of the other gods");
			return;
		}
		/** Quest cape 112QP TODO item id **/
		/*
		if (item.getID() == 2145 && player.getQuestPoints() < 112) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to complete all the available quests");
			return;
		}*/
		/** Max skill total cape TODO item id **/
		/*if (item.getID() == 2146 && player.getSkills().getTotalLevel() < 1782) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to be level 99 in all skills");
			return;
		}*/
		/** iron men armours **/
		if ((item.getID() == 2135 || item.getID() == 2136 || item.getID() == 2137) && !player.isIronMan(1)) {
			player.message("You need to be an Iron Man to wear this");
			return;
		}
		if ((item.getID() == 2138 || item.getID() == 2139 || item.getID() == 2140) && !player.isIronMan(2)) {
			player.message("You need to be an Ultimate Iron Man to wear this");
			return;
		}
		if ((item.getID() == 2141 || item.getID() == 2142 || item.getID() == 2143) && !player.isIronMan(3)) {
			player.message("You need to be a Hardcore Iron Man to wear this");
			return;
		}
		if(item.getID() == 2254 && player.getQuestStage(Constants.Quests.LEGENDS_QUEST) != -1) {
			player.message("you have not earned the right to wear this yet");
			player.message("you need to complete the Legends Quest");
			return;
		}
		
		ArrayList<Item> items = getItems();

		for (Item i : items) {
			if (item.wieldingAffectsItem(i) && i.isWielded()) {
				unwieldItem(i, false);
			}
		}
		item.setWielded(true);
		if (sound)
			player.playSound("click");
		player.updateWornItems(item.getDef().getWieldPosition(), item.getDef().getAppearanceId());

		ActionSender.sendInventory(player);
		ActionSender.sendEquipmentStats(player);
	}

	public void dropOnDeath(Mob opponent) {
		sort();
		ListIterator<Item> iterator = iterator();
		if(!player.isIronMan(2)) {
			if (!player.isSkulled()) {
				for (int i = 0; i < 3 && iterator.hasNext(); i++) {
					if ((iterator.next()).getDef().isStackable()) {
						iterator.previous();
						break;
					}
				}
			}
		}
		if (player.getPrayers().isPrayerActivated(Prayers.PROTECT_ITEMS) && iterator.hasNext()) {
			if (iterator.next().getDef().isStackable()) {
				iterator.previous();
			}
		}
		DeathLog log = new DeathLog(player, opponent, false);
		for (; iterator.hasNext();) {
			Item item = iterator.next();
			/**
			 * ALWAYS KEEP THE GAUNTLESS (STEEL GAUNTLESS OR THE GAUNTLESS YOU
			 * ENHANCED STEEL FOR) IF YOU DIE
			 **/
			if (item.getID() >= 698 && item.getID() <= 701) {
				continue;
			}
			/**
			 * ALWAYS KEEP THE PUMPKIN HEADS IF YOU DIE
			 **/
			if (item.getID() >= 2097 && item.getID() <= 2102) {
				continue;
			}
			if (item.isWielded()) {
				player.updateWornItems(item.getDef().getWieldPosition(),
						player.getSettings().getAppearance().getSprite(item.getDef().getWieldPosition()));
				item.setWielded(false);
			}
			iterator.remove();

			log.addDroppedItem(item);
			if (item.getDef().isUntradable()) {
				world.registerItem(new GroundItem(item.getID(), player.getX(), player.getY(), item.getAmount(), player));
			} else {
				Player dropOwner = (opponent == null || !opponent.isPlayer()) ? player : (Player) opponent;
				GroundItem groundItem = new GroundItem(item.getID(), player.getX(), player.getY(), item.getAmount(), dropOwner);
				if(dropOwner.getIronMan() != 0) {
					groundItem.setAttribute("playerKill", true);
				}
				world.registerItem(groundItem);
			}
		}
		log.build();
		GameLogging.addQuery(log);
	}
}
