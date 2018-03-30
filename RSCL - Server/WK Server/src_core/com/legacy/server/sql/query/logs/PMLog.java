package com.legacy.server.sql.query.logs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.legacy.server.Constants;
import com.legacy.server.sql.query.Query;

public final class PMLog extends Query {

	private final String sender, message, reciever;
	
	public PMLog(String sender, String message, String reciever) {
		super("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "private_message_logs`(`sender`, `message`, `reciever`, `time`) VALUES(?, ?, ?, ?)");
		this.sender = sender;
		this.message = message;
		this.reciever = reciever;
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, sender);
		statement.setString(2, message);
		statement.setString(3, reciever);
		statement.setLong(4, time);
		return statement;
	}

	@Override
	public Query build() {
		return this;
	}

}
