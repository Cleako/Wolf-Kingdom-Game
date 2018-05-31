package com.loader.rscl;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.loader.rscl.frame.AppFrame;
import com.loader.rscl.frame.popup.PopupFrame;
import com.loader.rscl.frame.threads.StatusChecker;
import com.loader.rscl.net.Downloader;

public class Launcher
{
	private static PopupFrame popup;
	private static Downloader updater;

	public static void main(final String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} 
		catch (UnsupportedLookAndFeelException e) {
			Launcher.getPopup().setMessage("" + e);
		}
		catch (ClassNotFoundException e) {
			Launcher.getPopup().setMessage("" + e);
		}
		catch (InstantiationException e) {
			Launcher.getPopup().setMessage("" + e);
		}
		catch (IllegalAccessException e) {
			Launcher.getPopup().setMessage("" + e);
		}
		final AppFrame frame = new AppFrame();
		frame.build();
		Launcher.popup = new PopupFrame();
		new Thread(new StatusChecker(Constants.SERVER_IP, Constants.SERVER_PORT)).start();
		updater = new Downloader();
		updater.init();
		updater.doneLoading();
	}

	public static PopupFrame getPopup() {
		return Launcher.popup;
	}

}
