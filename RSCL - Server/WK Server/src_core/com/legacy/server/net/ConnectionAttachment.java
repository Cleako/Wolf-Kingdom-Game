package com.legacy.server.net;

import java.util.concurrent.atomic.AtomicReference;

import com.legacy.server.model.entity.player.Player;
import com.legacy.server.net.rsc.ISAACContainer;

public class ConnectionAttachment {
	
	public AtomicReference<Player> player = new AtomicReference<Player>();
	
	public AtomicReference<ISAACContainer> ISAAC = new AtomicReference<ISAACContainer>();
	
}
