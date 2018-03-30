package com.legacy.server.sql.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.legacy.server.Constants;

public class PlayerOnlineFlagQuery extends Query {

	private int playerID;
	private boolean online;
	private String loginIP;

	public PlayerOnlineFlagQuery(int playerID, String loginIP, boolean online) {
		super("UPDATE `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "players` SET `online`=?, `login_date`=?, `login_ip`=? WHERE `id`=?");
		this.playerID = playerID;
		this.loginIP = loginIP;
		this.online = online;
	}
	
	public PlayerOnlineFlagQuery(int playerID, boolean online) {
		super("UPDATE `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "players` SET `online`=? WHERE `id`=?");
		this.playerID = playerID;
		this.online = online;
		loginIP = null;
	}
	
	@Override
	public Query build() {
		return this;
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(query);
		int id = 1;
		statement.setInt(id++, online ? 1 : 0);
		if(loginIP != null) {
			statement.setLong(id++, time);
			statement.setString(id++, loginIP);
		}
		statement.setInt(id++, playerID);
		return statement;
	}

}
