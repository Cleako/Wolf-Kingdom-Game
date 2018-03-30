package com.legacy.server.sql.query.logs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.legacy.server.Constants;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.sql.query.Query;

public class LiveFeedLog extends Query {
	private String feedText;
	private String username;
	
	public LiveFeedLog(Player player, String feedText) {
		super("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX
				+ "live_feeds`(`username`,`message`,`time`) VALUES(?, ?, ?)");
		this.username = player.getUsername();
		this.feedText = feedText;
	}

	@Override
	public Query build() {
		return this;
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, username);
		statement.setString(2, feedText);
		statement.setLong(3, time);
		return statement;
	}

}
