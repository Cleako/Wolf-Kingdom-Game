package com.rsclegacy.client.android;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.rsclegacy.android.render.InputImpl;
import com.rsclegacy.android.render.RSCBitmapSurfaceView;
import com.rsclegacy.client.model.Sprite;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import rsc.Config;
import rsc.mudclient;
import rsc.multiclient.ClientPort;

public class GameActivity extends Activity implements ClientPort {

	private InputImpl inputImpl;
	private mudclient mudclient;
	private RSCBitmapSurfaceView gameView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		gameView = new RSCBitmapSurfaceView(this);
		setMudclient(new mudclient(this));
		setContentView(gameView);

		if (mudclient.threadState >= 0) {
			mudclient.threadState = 0;
		}

		Config.F_ANDROID_BUILD = true;

		mudclient.startMainThread();

		setInputImpl(new InputImpl(mudclient, gameView));

	}

	@Override
	public boolean drawLoading(int i) {
		if (gameView == null) {
			return false;
		}
		return gameView.drawLoading(i);
	}

	@Override
	public void showLoadingProgress(int percentage, String status) {
		if (gameView != null) {
			gameView.showLoadingProgress(percentage, status);
		}
	}

	@Override
	public void initListeners() {
		if (gameView != null) {
			gameView.initListeners();
		}
	}

	@Override
	public void crashed() {
		if (gameView != null) {
			gameView.crashed();
		}
	}

	@Override
	public void drawLoadingError() {
		if (gameView != null) {
			gameView.drawLoadingError();
		}
	}

	@Override
	public void drawOutOfMemoryError() {
		if (gameView != null) {
			gameView.drawOutOfMemoryError();
		}
	}

	@Override
	public boolean isDisplayable() {
		if (gameView != null) {
			return gameView.isDisplayable();
		}
		return false;
	}

	@Override
	public void drawTextBox(String line2, byte var2, String line1) {
		if (gameView != null) {
			gameView.drawTextBox(line2, var2, line1);
		}
	}

	@Override
	public void initGraphics() {
		if (gameView != null) {
			gameView.initGraphics();
		}
	}

	@Override
	public void draw() {
		if (gameView != null) {
			gameView.draw();
		}
	}

	@Override
	public void close() {
		if (gameView != null) {
			gameView.close();
		}
	}

	@Override
	public String getCacheLocation() {
		if (gameView != null) {
			return gameView.getCacheLocation();
		}
		return null;
	}

	@Override
	public void resized() {
		if (gameView != null) {
			gameView.resized();
		}
	}

	@Override
	public Sprite getSpriteFromByteArray(ByteArrayInputStream byteArrayInputStream) {
		return gameView.getSpriteFromByteArray(byteArrayInputStream);
	}

	@Override
	public void playSound(byte[] soundData, int offset, int dataLength) {
		if(gameView != null) {
			gameView.playSound(soundData, offset, dataLength);
		}
	}

	@Override
	public void stopSoundPlayer() {
		if(gameView != null) {
			gameView.stopSoundPlayer();
		}
	}

	public mudclient getMudclient() {
		return mudclient;
	}

	public void setMudclient(mudclient mudclient) {
		this.mudclient = mudclient;
	}

	public InputImpl getInputImpl() {
		return inputImpl;
	}

	public void setInputImpl(InputImpl inputImpl) {
		this.inputImpl = inputImpl;
	}

	public void drawKeyboard() {
		InputMethodManager imm = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
		if (imm.isAcceptingText()) { // REMOVE?
			Config.F_SHOWING_KEYBOARD = true;
		} 
	}

	@Override
	public boolean saveCredentials(String creds) {
		FileOutputStream fileout;
		try {
			fileout = openFileOutput("credentials.txt", MODE_PRIVATE);

			OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
			outputWriter.write(creds);
			outputWriter.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	@Override
	public String loadCredentials() {
		try {

			FileInputStream in = openFileInput("credentials.txt");
			InputStreamReader inputStreamReader = new InputStreamReader(in);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line);
			}
			in.close();

			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
