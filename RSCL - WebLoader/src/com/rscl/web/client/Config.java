package com.rscl.web.client;

import java.io.File;

public class Config {
	public static String CACHE_DIR;
	public static String MEDIA_DIR;
	public static String CACHE_URL;
	public static final String CONF_DIR = System.getProperty("user.home") + File.separator + "RSCLegacy";
	public static final String CLIENT_LOCATION = "http://rsclegacy.com/client/client.jar";

	static {
		MEDIA_DIR = Config.CACHE_DIR = String.valueOf(System.getProperty("user.home")) + File.separator + "RSCLegacy";
		CACHE_URL = "http://rsclegacy.com/client/";
	}
}

