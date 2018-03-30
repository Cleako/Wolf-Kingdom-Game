package com.legacy.server.plugins;

import com.legacy.server.model.Shop;

public interface ShopInterface {
	
	public Shop[] getShops();
	
	public boolean isMembers();
}