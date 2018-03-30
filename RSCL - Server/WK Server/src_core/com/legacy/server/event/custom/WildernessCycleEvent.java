package com.legacy.server.event.custom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.legacy.server.Constants;
import com.legacy.server.event.DelayedEvent;
import com.legacy.server.model.world.World;
import com.legacy.server.sql.GameLogging;
import com.legacy.server.sql.query.Query;
import com.legacy.server.sql.query.ResultQuery;

public class WildernessCycleEvent extends DelayedEvent {
	
	/**
     * The asynchronous logger.
     */
    private static final Logger LOGGER = LogManager.getLogger();

	private long lastWildernessChange;

	private int lastWildernessType;

	public WildernessCycleEvent() {
		super(null, 1000);
	}

	@Override
	public void run() {
		try {
			long now = System.currentTimeMillis() / 1000;

			GameLogging.addQuery(new ResultQuery("SELECT `key`, `value` FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "player_cache` WHERE `playerID`=-1") {
				@Override
				public void onResult(ResultSet result) throws SQLException {
					if (!result.next()) {
						return;
					}
					int databaseWildType = result.getInt("key");
					lastWildernessChange = result.getLong("value");
					if (lastWildernessType != databaseWildType) {
						lastWildernessType = databaseWildType;
						World.getWorld()
								.sendWorldMessage("Wilderness rules changed: " + getFriendlyString(lastWildernessType)
										+ ". Next change in " + timeUntilChange() + " hours.");
					}
					
				}

				@Override
				public Query build() {
					return this;
				}

				@Override
				public PreparedStatement prepareStatement(Connection connection) throws SQLException {
					PreparedStatement statement = connection.prepareStatement(query);
					return statement;
				}
			});

			if (now - lastWildernessChange >= 60 * 60 * 24) {
				lastWildernessType++;
				if (lastWildernessType == 4) {
					lastWildernessType = 0;
				}
				World.getWorld().sendWorldMessage("Wilderness rules changed: " + getFriendlyString(lastWildernessType)
						+ ". Next change in " + timeUntilChange() + " hours.");

				GameLogging.addQuery(new Query("UPDATE `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "player_cache` SET `value`=?, `key`=? WHERE `playerID`='-1'") {

					@Override
					public Query build() {
						return this;
					}

					@Override
					public PreparedStatement prepareStatement(Connection connection) throws SQLException {
						PreparedStatement statement = connection.prepareStatement(query);
						statement.setLong(1, now);
						statement.setInt(2, lastWildernessType);
						return statement;
					}
				});
			}

			switch (lastWildernessType) {
			case 0:
			case 1:
				World.godSpellsStart = 60;
				World.godSpellsMax = 60;

				World.membersWildStart = 48;
				World.membersWildMax = 60;
				break;
			case 2:
				World.godSpellsStart = 0;
				World.godSpellsMax = 60;

				World.membersWildStart = 0;
				World.membersWildMax = 60;
				break;
			case 3:
				World.godSpellsStart = 60;
				World.godSpellsMax = 60;

				World.membersWildStart = 0;
				World.membersWildMax = 60;
				break;
			}

		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}

	private String getFriendlyString(int lastWildernessType) {
		String friendlyString = "";
		switch (lastWildernessType) {
		case 0:
			friendlyString = "Fully F2P";
			break;
		case 1:
			friendlyString = "Fully F2P";
			break;
		case 2:
			friendlyString = "Fully P2P /w god spells";
			break;
		case 3:
			friendlyString = "Fully P2P /w out god spells";
			break;
		}
		return friendlyString;
	}

	public String timeUntilChange() {
		long now = (System.currentTimeMillis() / 1000);
		int hours = 24 - (int) ((now - lastWildernessChange) / 60 / 60);

		int nextState = lastWildernessType + 1 == 4 ? 0 : lastWildernessType + 1;

		return "Next wilderness state change is to " + getFriendlyString(nextState) + " in " + hours + " hours";
	}
}
