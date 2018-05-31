package com.wk.server.event.custom;

import com.wk.server.event.DelayedEvent;
import com.wk.server.model.Shop;

public final class ShopRestockEvent extends DelayedEvent {

	private final Shop shop;

	public ShopRestockEvent(Shop shop) {
		super(null, shop.getRespawnRate());
		this.shop = shop;
	}

	@Override
	public void run() {
		shop.restock();
	}

}
