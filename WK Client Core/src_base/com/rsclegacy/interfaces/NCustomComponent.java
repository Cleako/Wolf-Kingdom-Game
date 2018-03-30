package com.rsclegacy.interfaces;

import rsc.mudclient;

public abstract class NCustomComponent extends NComponent {

	public NCustomComponent(mudclient client) {
		super(client);
	}
	
	@Override
	public void renderComponent() throws Exception {
		super.renderComponent();
		render();
	}
	
	public abstract void render();


}
