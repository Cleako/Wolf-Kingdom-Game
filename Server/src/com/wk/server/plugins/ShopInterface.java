package com.wk.server.plugins;

import com.wk.server.model.Shop;

public interface ShopInterface {
	
	public Shop[] getShops();
	
	public boolean isMembers();
}