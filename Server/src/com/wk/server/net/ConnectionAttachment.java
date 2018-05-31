package com.wk.server.net;

import java.util.concurrent.atomic.AtomicReference;

import com.wk.server.model.entity.player.Player;
import com.wk.server.net.rsc.ISAACContainer;

public class ConnectionAttachment {
	
	public AtomicReference<Player> player = new AtomicReference<Player>();
	
	public AtomicReference<ISAACContainer> ISAAC = new AtomicReference<ISAACContainer>();
	
}
