package com.wk.server.sql.query.logs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.wk.server.Constants;
import com.wk.server.sql.query.Query;

public final class LoginLog extends Query {

	private final int player;
	private final String ip;

	public LoginLog(int player, String ip) {
		super("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "logins`(`playerID`, `ip`, `time`) VALUES(?, ?, ?)");
		this.player = player;
		this.ip = ip;
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, player);
		statement.setString(2, ip);
		statement.setLong(3, time);
		return statement;
	}

	@Override
	public Query build() {
		return this;
	}

}
