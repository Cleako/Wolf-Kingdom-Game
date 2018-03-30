package com.loader.rscl.frame.threads;

import java.io.IOException;
import java.net.Socket;

import com.loader.rscl.frame.AppFrame;

public class StatusChecker implements Runnable {

	private String serverIp;
	private int port;
	
	public StatusChecker(String ip, int port) {
		this.serverIp = ip;
		this.port = port;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				boolean isOnline = isOnline();
				String text = isOnline ? "Online" : "Offline";
				String color = isOnline ? "#00FF00" : "#FF0000";
				
				AppFrame.get().getStatus().setText("<html>Server Status: <span style='color:"+color+";'>"+text+"</span></html>");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			try {
				Thread.sleep(15000L);
			} catch (Exception e) {
				
			}
		}
	}
	
	public boolean isOnline() { 
	    try (Socket s = new Socket(this.serverIp, this.port)) {
	        return true;
	    } catch (IOException ex) {
	    	return false;
	    }
	}
}