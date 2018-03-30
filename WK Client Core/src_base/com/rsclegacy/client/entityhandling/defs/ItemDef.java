package com.rsclegacy.client.entityhandling.defs;

public class ItemDef extends EntityDef {
	public String command;
	public int basePrice;
	public int sprite;
	public boolean stackable;
	public boolean wieldable;
	public int wearableID;
	public int pictureMask;
	public boolean quest;
	public boolean membersItem;

	private int isNotedFormOf = -1;
	private int notedFormID = -1;

	public ItemDef(String name, String description, String command, int basePrice, int sprite, boolean stackable,
			boolean wieldable, int wearableID, int pictureMask, boolean membersItem, boolean quest, int id) {
		super(name, description, id);
		this.command = command;
		this.basePrice = basePrice;
		this.sprite = sprite;
		this.stackable = stackable;
		this.wieldable = wieldable;
		this.wearableID = wearableID;
		this.pictureMask = pictureMask;
		this.membersItem = membersItem;
		this.quest = quest;
		this.id = id;
	}

	public ItemDef(String name, String description, String command, int basePrice, int sprite, boolean stackable,
			boolean wieldable, int wearableID, int pictureMask, boolean membersItem, boolean quest, int notedForm, int notedFormOf,
			int id) {
		super(name, description, id);
		this.command = command;
		this.basePrice = basePrice;
		this.sprite = sprite;
		this.stackable = stackable;
		this.wieldable = wieldable;
		this.wearableID = wearableID;
		this.pictureMask = pictureMask;
		this.membersItem = membersItem;
		this.quest = quest;
		this.id = id;
		this.notedFormID = notedForm;
		this.isNotedFormOf = notedFormOf;
	}

	public ItemDef(int i, ItemDef item) {
		super(item.name, "Swap this note at any bank for the equivalent item.", i);
		this.command = item.command;
		this.basePrice = item.basePrice;
		this.sprite = 438;
		this.stackable = true;
		this.wieldable = false;
		this.pictureMask = 0;
		this.membersItem = item.membersItem;
		this.quest = item.quest;
		this.setNotedFormOf(item.id);
		this.id = i;
	}

	public String getCommand() {
		return command;
	}

	public int getSprite() {
		return sprite;
	}

	public int getBasePrice() {
		return basePrice;
	}

	public boolean isStackable() {
		return stackable;
	}

	public boolean isWieldable() {
		return wieldable;
	}

	public int getPictureMask() {
		return pictureMask;
	}

	public int getNoteItem() {
		return -1;
	}

	public int getNotedFormOf() {
		return isNotedFormOf;
	}

	public void setNotedFormOf(int notedFormOf) {
		this.isNotedFormOf = notedFormOf;
	}

	public void setNotedForm(int id) {
		this.notedFormID = id;
	}

	public int getNotedForm() {
		return notedFormID;
	}
}
