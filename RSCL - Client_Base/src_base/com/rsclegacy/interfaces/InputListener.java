package com.rsclegacy.interfaces;

public abstract class InputListener {
	
	public boolean onCharTyped(char c, int key) {
		return false;
	}
	
	public boolean onMouseMove(int x, int y) {
		return false;
	}
	
	public boolean onMouseDown(int clickX, int clickY, int mButtonDown, int mButtonClick) {
		return false;
	}
	
}
