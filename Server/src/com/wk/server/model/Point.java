package com.wk.server.model;

import java.util.ArrayList;

import com.wk.server.model.entity.WildernessLocation;
import com.wk.server.model.entity.WildernessLocation.WildState;
import com.wk.server.model.world.Area;
import com.wk.server.util.rsc.Formulae;

public class Point {
	
	private static ArrayList<WildernessLocation> wildernessLocations = new ArrayList<WildernessLocation>();
	
	public boolean isMembersWild() {
		if(inWilderness()) {
			for(WildernessLocation location : wildernessLocations) {
				if(x >= location.getMinX() && y >= location.getMinY() && x <= location.getMaxX() && y <= location.getMaxY()) {
					if(location.getWildState() == WildState.MEMBERS_WILD) {
						return true;
					} else if(location.getWildState() == WildState.FREE_WILD) {
						return false;
					}
				}
			}
			/* If its allowed in these wild levels */
			if(wildernessLevel() >= 48 && wildernessLevel() <= 56) {
				return true;
			}
			/* It is F2P */
			return false;
		}
		/* Not in wild, its P2P */
		return true;
	}
	
	public WildernessLocation getWildernessLocation() {
		for(WildernessLocation location : wildernessLocations) {
			if(x > location.getMinX() && y > location.getMinY() && x < location.getMaxX() && y < location.getMaxY()) {
				return location;
			}
		}
		return null;
	}
	
	public static Point location(int x, int y) {
		if (x < 0 || y < 0) {
			throw new IllegalArgumentException(
					"Point may not contain non negative values x:" + x + " y:"
							+ y);
		}
		return new Point(x, y);
	}

	public static boolean inWilderness(int x, int y) {
		int wild = 2203 - (y + (1776 - (944 * (int) (y / 944))));
		if (x + 2304 >= 2640) {
			wild = -50;
		}
		if (wild > 0) {
			return (1 + wild / 6) >= 1;
		}
		return false;
	}

	protected int x, y;

	protected Point() {
	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public final boolean withinRange(Point p, int radius) {
		int xDiff = this.x - p.x;
		int yDiff = this.y - p.y;

		return xDiff <= radius && xDiff >= -radius && yDiff <= radius
				&& yDiff >= -radius;
	}

	public final int getX() {
		return x;
	}

	public final int getY() {
		return y;
	}

	public int hashCode() {
		return (x << 16) | y;
	}

	public boolean inBounds(int x1, int y1, int x2, int y2) {
		return x >= x1 && x <= x2 && y >= y1 && y <= y2;
	}
	
	public boolean inHeroQuestRangeRoom() {
		return inBounds(459, 460, 672, 673);
	}

	public boolean onTutorialIsland() {
		return inBounds(190, 720, 240, 770);
	}
	public boolean inTutorialLanding() {
		return inBounds(214, 739, 221, 747);
	}

	public boolean inModRoom() {
		return inBounds(64, 1639, 80, 1643);
	}

	public boolean inWilderness() {
		return wildernessLevel() > 0;
	}
	
	public boolean inFreeWild() {
		return (wildernessLevel() >= 1 && wildernessLevel() <= 48);
	}

	public boolean inVarrock() {
		return inBounds(50, 180, 444, 565);
	}

	public boolean inEdgeville() {
		return inBounds(180, 245, 427, 472);
	}

	public boolean inBarbVillage() {
		return inBounds(180, 245, 472, 535);
	}

	public boolean inDraynor() {
		return inBounds(180, 245, 535, 715);
	}

	public boolean inLumbridge() {
		return inBounds(100, 600, 180, 670);
	}

	public boolean inAlKharid() {
		return inBounds(47, 94, 578, 733);
	}

	public boolean inFalador() {
		return inBounds(245, 338, 510, 608);
	}

	public boolean inPortSarim() {
		return inBounds(245, 355, 608, 693);
	}

	public boolean inTaverly() {
		return inBounds(338, 384, 430, 576);
	}

	public boolean inEntrana() {
		return inBounds(395, 441, 525, 573);
	}

	public boolean inCatherby() {
		return inBounds(399, 477, 476, 513);
	}

	public boolean inSeers() {
		return inBounds(477, 592, 432, 485);
	}

	public boolean inGnomeStronghold() {
		return inBounds(673, 751, 432, 537);
	}

	public boolean inArdougne() {
		return inBounds(500, 600, 537, 708);
	}

	public boolean inYanille() {
		return inBounds(528, 671, 712, 785);
	}

	public boolean inBrimhaven() {
		return inBounds(435, 522, 640, 710);
	}

	public boolean inKaramja() {
		return inBounds(333, 435, 679, 710);
	}

	public boolean inShiloVillage() {
		return inBounds(384, 431, 815, 860);
	}

	public int wildernessLevel() {
		int wild = 2203 - (y + (1776 - (944 * Formulae.getHeight(this))));
		if (x + 2304 >= 2640) {
			wild = -50;
		}
		if (wild > 0) {
			return 1 + wild / 6;
		}
		return 0;
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Point) || o == null) {
			return false;
		}

		Point p = (Point) o;
		return x == p.x && y == p.y;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	public double getDistanceTo(Point o2) {
	    int xDiff = Math.abs(getX() - o2.getX());
		int yDiff = Math.abs(getY() - o2.getY());
		return xDiff + yDiff;
	}

	public boolean inDwarfArea() {
		return inBounds(240, 432, 309, 527);
	}

	public boolean inPlatformArea() {
		return inBounds(492, 614, 498, 620);
	}

	public boolean inMageArena() {
		return inBounds(220,122,236,137);
	}

	public boolean inTouristTrapCave() {
		return inBounds(49,3600,95,3647);
	}
	
	public boolean inTouristTrapCave1() {
		return inBounds(79,3614,95,3647);
	}
	
	public boolean inTouristTrapCave2() {
		return inBounds(48,3633,78,3647);
	}
	
	public boolean inTouristTrapCave3() {
		return inBounds(49,3600,95,3647);
	}
	
	static {	
		/* Edgeville dungeon wilderness, always members wild */
		wildernessLocations.add(new WildernessLocation(WildState.MEMBERS_WILD, 195, 3206, 234, 3258));
		/* Red Dragons, always P2P */
		wildernessLocations.add(new WildernessLocation(WildState.MEMBERS_WILD, 129, 180, 163, 219));
		/* Underground Lava maze, always P2P */
		wildernessLocations.add(new WildernessLocation(WildState.MEMBERS_WILD, 243, 2988, 283, 3020));
	}

	public boolean inArea(Area area) {
		return area.inBounds(this); 
	}
}
