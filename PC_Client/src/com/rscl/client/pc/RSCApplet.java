package com.rscl.client.pc;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import com.rsclegacy.client.data.DataOperations;
import com.rsclegacy.client.model.Sprite;

import rsc.Config;
import rsc.mudclient;
import rsc.graphics.two.Fonts;
import rsc.multiclient.ClientPort;
import rsc.util.GenUtil;

public class RSCApplet extends Applet implements MouseListener, MouseMotionListener, KeyListener, MouseWheelListener, ComponentListener,
		ImageObserver, ClientPort, ImageProducer {

	static mudclient mudclient;
	
	public static int globalLoadingPercent = 0;
	public static String globalLoadingState = "";

	private static final long serialVersionUID = 1L;

	Font copyrightFont = new Font("Helvetica", 1, 13);
	Font copyrightFont2 = new Font("Helvetica", 0, 12);
	Font loadingFont = new Font("TimesRoman", 0, 15);

	Graphics loadingGraphics;
	Image loadingJagLogo;
	private int loadingPercent = 0;
	String loadingState = "Loading";
	private final boolean m_hb = false;
	boolean m_N = false;
	String m_p = null;

	private int height = 384;
	private int width = 512;
	private StreamAudioPlayer soundPlayer;
	protected int resizeWidth;
	protected int resizeHeight;

	private DirectColorModel imageModel;
	private Image backingImage;

	void addMouseClick(int button, int x, int y) {
		try {
			
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "e.Q(" + x + ',' + "dummy" + ',' + button + ',' + y + ')');
		}
	}

	final void drawCenteredString(Font var1, String str, int y, boolean var4, int x, Graphics g) {
		try {
			
			FontMetrics metrics = getFontMetrics(var1);
			g.setFont(var1);
			g.drawString(str, x - metrics.stringWidth(str) / 2, y + metrics.getHeight() / 4);
		} catch (RuntimeException var9) {
			throw GenUtil.makeThrowable(var9,
					"e.LE(" + (var1 != null ? "{...}" : "null") + ',' + (str != null ? "{...}" : "null") + ',' + y + ','
							+ true + ',' + x + ',' + (g != null ? "{...}" : "null") + ')');
		}
	}

	public final boolean drawLoading(int var1) {
		try {
			
			Graphics var2 = this.getGraphics();
			if (var2 != null) {
				this.loadingGraphics = var2.create();
				this.loadingGraphics.translate(mudclient.screenOffsetX, mudclient.screenOffsetY);
				this.loadingGraphics.setColor(Color.black);
				this.loadingGraphics.fillRect(0, 0, this.width, this.height);
				this.drawLoadingScreen("Loading...", 0, var1 ^ 103);
				return true;
			} else {
				return false;
			}
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "e.ME(" + var1 + ')');
		}
	}

	@Override
	public boolean isDisplayable() {
		return super.isDisplayable();
	}

	private final void drawLoadingScreen(String state, int percent, int var3) {
		try {
			try {
				int x = (this.width - 281) / 2;
				int y = (this.height - 148) / 2;
				this.loadingGraphics.setColor(Color.black);
				this.loadingGraphics.fillRect(0, 0, this.width, this.height);
				if (!this.m_hb) {
					this.loadingGraphics.drawImage(this.loadingJagLogo, x, y, this);
				}

				x += 2;
				this.loadingPercent = percent;
				y += 90;
				this.loadingState = state;
				if (var3 <= 97) {
					this.mouseReleased((MouseEvent) null);
				}

				this.loadingGraphics.setColor(new Color(132, 132, 132));
				if (this.m_hb) {
					this.loadingGraphics.setColor(new Color(220, 0, 0));
				}

				this.loadingGraphics.drawRect(x - 2, y - 2, 280, 23);
				this.loadingGraphics.fillRect(x, y, percent * 277 / 100, 20);
				this.loadingGraphics.setColor(new Color(198, 198, 198));
				if (this.m_hb) {
					this.loadingGraphics.setColor(new Color(255, 255, 255));
				}

				this.drawCenteredString(this.loadingFont, state, 10 + y, true, 138 + x, this.loadingGraphics);
				if (!this.m_hb) {
					this.drawCenteredString(this.copyrightFont, "Created by JAGeX - visit www.jagex.com", 30 + y, true,
							x + 138, this.loadingGraphics);
					this.drawCenteredString(this.copyrightFont, "\u00a9 2001-2015 Jagex Ltd", y + 44, true, x + 138,
							this.loadingGraphics);
				} else {
					this.loadingGraphics.setColor(new Color(132, 132, 152));
					this.drawCenteredString(this.copyrightFont2, "\u00a9 2001-2015 Jagex Ltd", this.height - 20, true,
							138 + x, this.loadingGraphics);
				}

				if (null != this.m_p) {
					this.loadingGraphics.setColor(Color.white);
					this.drawCenteredString(this.copyrightFont, this.m_p, y - 120, true, x + 138, this.loadingGraphics);
				}
			} catch (Exception var6) {
				;
			}

			
		} catch (RuntimeException var7) {
			throw GenUtil.makeThrowable(var7,
					"e.FE(" + (state != null ? "{...}" : "null") + ',' + percent + ',' + var3 + ')');
		}
	}

	@Override
	public final synchronized void keyPressed(KeyEvent var1) {
		try {
			
			this.updateControlShiftState((InputEvent) var1);
			char keyChar = var1.getKeyChar();
			int keyCode = var1.getKeyCode();
			mudclient.handleKeyPress((byte) 126, (int) keyChar);
			if (keyCode == 112) {
				mudclient.interlace = !mudclient.interlace;
			}
			if (keyCode == 113) {
				Config.SIDE_MENU_OVERLAY = !Config.SIDE_MENU_OVERLAY;
			}

			// if ((char) keyChar != 78 && (char) keyChar != 77) {
			// ;
			// }
			//
			// if ((char) keyChar == 32) {
			// ;
			// }
			//
			// if ((char) keyChar != 123) {
			// ;
			// }

			mudclient.lastMouseAction = 0;
			// if ((char) keyChar != 110 && (char) keyChar != 109) {
			// ;
			// }
			//
			// if (keyCode != 40) {
			// ;
			// }

			if (keyCode == 39) {
				mudclient.keyRight = true;
			}

			// if ((char) keyChar == 125) {
			// ;
			// }
			//
			// if (keyCode == 38) {
			// ;
			// }

			if (keyCode == 37) {
				mudclient.keyLeft = true;
			}

			if (keyCode == KeyEvent.VK_UP)
				mudclient.keyUp = true;
			if (keyCode == KeyEvent.VK_DOWN)
				mudclient.keyDown = true;
			if (keyCode == KeyEvent.VK_PAGE_DOWN)
				mudclient.pageDown = true;
			if (keyCode == KeyEvent.VK_PAGE_UP)
				mudclient.pageUp = true;

			boolean hitInputFilter = false;

			for (int var5 = 0; var5 < Fonts.inputFilterChars.length(); ++var5) {
				if (Fonts.inputFilterChars.charAt(var5) == keyChar) {
					hitInputFilter = true;
					break;
				}
			}

			if (hitInputFilter && mudclient.inputTextCurrent.length() < 20) {
				mudclient.inputTextCurrent = mudclient.inputTextCurrent + (char) keyChar;
			}

			if (hitInputFilter && mudclient.chatMessageInput.length() < 80) {
				mudclient.chatMessageInput = mudclient.chatMessageInput + (char) keyChar;
			}

			// Backspace
			if (keyChar == '\b' && mudclient.inputTextCurrent.length() > 0) {
				mudclient.inputTextCurrent = mudclient.inputTextCurrent.substring(0,
						mudclient.inputTextCurrent.length() - 1);
			}

			// Backspace
			if (keyChar == '\b' && mudclient.chatMessageInput.length() > 0) {
				mudclient.chatMessageInput = mudclient.chatMessageInput.substring(0,
						mudclient.chatMessageInput.length() - 1);
			}

			if (keyChar == '\n' || keyChar == '\r') {
				mudclient.inputTextFinal = mudclient.inputTextCurrent;
				mudclient.chatMessageInputCommit = mudclient.chatMessageInput;
			}

		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "e.keyPressed(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	public final synchronized void keyReleased(KeyEvent var1) {
		try {
			updateControlShiftState((InputEvent) var1);
			
			char c = var1.getKeyChar();
			int keyCode = var1.getKeyCode();
			if ((char) c != 32) {
				;
			}

			if (keyCode == 40) {
				;
			}

			if ((char) c != 78 && (char) c != 77) {
				;
			}

			if (keyCode == 39) {
				mudclient.keyRight = false;
			}

			if ((char) c != 110 && (char) c != 109) {
				;
			}

			if ((char) c != 123) {
				;
			}

			if (keyCode == 37) {
				mudclient.keyLeft = false;
			}
			if (keyCode == KeyEvent.VK_UP)
				mudclient.keyUp = false;
			if (keyCode == KeyEvent.VK_DOWN)
				mudclient.keyDown = false;
			if (keyCode == KeyEvent.VK_PAGE_DOWN)
				mudclient.pageDown = false;
			if (keyCode == KeyEvent.VK_PAGE_UP)
				mudclient.pageUp = false;

			if (keyCode == 38) {
				;
			}

			if ((char) c == 125) {
				;
			}

		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "e.keyReleased(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	public final void keyTyped(KeyEvent var1) {
		try {
			
			updateControlShiftState((InputEvent) var1);
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "e.keyTyped(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	public final void mouseClicked(MouseEvent var1) {
		try {
			
			updateControlShiftState((InputEvent) var1);
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "e.mouseClicked(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	public final synchronized void mouseDragged(MouseEvent var1) {
		try {
			updateControlShiftState((InputEvent) var1);
			
			
			mudclient.mouseX = var1.getX() - mudclient.screenOffsetX;
			mudclient.mouseY = var1.getY() - mudclient.screenOffsetY;
			if (var1.isMetaDown()) {
				mudclient.currentMouseButtonDown = 2;
			} else {
				mudclient.currentMouseButtonDown = 1;
			}

		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "e.mouseDragged(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	public final void mouseEntered(MouseEvent var1) {
		try {
			updateControlShiftState((InputEvent) var1);
			
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "e.mouseEntered(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	public final void mouseExited(MouseEvent var1) {
		try {
			
			updateControlShiftState((InputEvent) var1);
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "e.mouseExited(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	public final synchronized void mouseMoved(MouseEvent var1) {
		try {
			updateControlShiftState((InputEvent) var1);
			
			mudclient.mouseX = var1.getX() - mudclient.screenOffsetX;
			mudclient.mouseY = var1.getY() - mudclient.screenOffsetY;
			mudclient.lastMouseAction = 0;
			mudclient.currentMouseButtonDown = 0;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "e.mouseMoved(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	public final synchronized void mousePressed(MouseEvent var1) {
		
		try {
			updateControlShiftState((InputEvent) var1);
			
			mudclient.mouseX = var1.getX() - mudclient.screenOffsetX;
			mudclient.mouseY = var1.getY() - mudclient.screenOffsetY;
			if (!var1.isMetaDown()) {
				mudclient.currentMouseButtonDown = 1;
			} else {
				mudclient.currentMouseButtonDown = 2;
			}

			mudclient.lastMouseButtonDown = mudclient.currentMouseButtonDown;
			mudclient.lastMouseAction = 0;
			mudclient.addMouseClick(mudclient.currentMouseButtonDown, mudclient.mouseX, mudclient.mouseY);
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "e.mousePressed(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	public final synchronized void mouseReleased(MouseEvent var1) {
		try {
			
			updateControlShiftState((InputEvent) var1);
			mudclient.mouseX = var1.getX() - mudclient.screenOffsetX;
			mudclient.mouseY = var1.getY() - mudclient.screenOffsetY;
			mudclient.currentMouseButtonDown = 0;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "e.mouseReleased(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}
	
	@Override
	public final void mouseWheelMoved(MouseWheelEvent var1) {
			updateControlShiftState((InputEvent) var1);
			mudclient.runScroll(var1.getWheelRotation());
	}

	@Override
	public final void paint(Graphics var1) {
		try {

			
			if (mudclient != null) {
				mudclient.rendering = true;
				if (mudclient.getGameState() == 2 && this.loadingJagLogo != null) {
					this.drawLoadingScreen(this.loadingState, this.loadingPercent, 126);
				} 
			}

		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "e.paint(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}

	boolean reposition() {
		return false;
	}

	public final void showLoadingProgress(int percent, String state) {
		try {
			

			try {
				int x = (this.width - 281) / 2;
				x += 2;
				int y = (this.height - 148) / 2;
				this.loadingState = state;
				this.loadingPercent = percent;
				y += 90;
				int progress = percent * 277 / 100;
				this.loadingGraphics.setColor(new Color(132, 132, 132));
				if (this.m_hb) {
					this.loadingGraphics.setColor(new Color(220, 0, 0));
				}

				this.loadingGraphics.fillRect(x, y, progress, 20);
				this.loadingGraphics.setColor(Color.black);
				this.loadingGraphics.fillRect(progress + x, y, 277 - progress, 20);
				this.loadingGraphics.setColor(new Color(198, 198, 198));
				if (this.m_hb) {
					this.loadingGraphics.setColor(new Color(255, 255, 255));
				}

				this.drawCenteredString(this.loadingFont, state, 10 + y, true, 138 + x, this.loadingGraphics);
			} catch (Exception var7) {
				;
			}

		} catch (RuntimeException var8) {
			throw GenUtil.makeThrowable(var8, "e.EE(" + percent + ',' + (state != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	public final void init() {
		try {
			mudclient = new mudclient(this);
			loadLogo();
			this.addMouseListener(this);
			this.addMouseMotionListener(this);
			this.addKeyListener(this);
			this.addComponentListener(this);
			this.addMouseWheelListener(this);
			soundPlayer = new StreamAudioPlayer();
		} catch (RuntimeException var2) {
			throw GenUtil.makeThrowable(var2, "client.init()");
		}
	}

	public void loadLogo() {
		byte[] jagexArchive = mudclient.unpackData("jagex.jag", "Jagex library", 0);
		if (jagexArchive != null) {
			byte[] logoData = DataOperations.loadData("logo.tga", 0, jagexArchive);
			this.loadingJagLogo = this.createLogo(logoData);
		}
	}

	public Image createLogo(byte[] data) {
		int width = data[13] * 256 + data[12];
		int height = data[15] * 256 + data[14];

		byte[] r = new byte[256];
		byte[] g = new byte[256];
		byte[] b = new byte[256];

		for (int i1 = 0; i1 < 256; i1++) {
			r[i1] = data[(20 + i1 * 3)];
			g[i1] = data[(19 + i1 * 3)];
			b[i1] = data[(18 + i1 * 3)];
		}

		IndexColorModel indexcolormodel = new IndexColorModel(8, 256, r, g, b);

		byte[] pixels = new byte[width * height];
		int pixel = 0;

		for (int y = height - 1; y >= 0; y--) {
			for (int x = 0; x < width; x++) {
				pixels[(pixel++)] = data[(786 + x + y * width)];
			}
		}

		MemoryImageSource memoryimagesource = new MemoryImageSource(width, height, indexcolormodel, pixels, 0, width);
		Image image = createImage(memoryimagesource);
		return image;
	}

	final void startApplet(int width, int height, int clientversion, int var4) {
		try {
			
			System.out.println("Started applet");
			this.width = width;
			this.height = height;
			
			mudclient.startMainThread();
		} catch (RuntimeException var12) {
			throw GenUtil.makeThrowable(var12, "e.OE(" + height + ',' + clientversion + ',' + var4 + ',' + width + ')');
		}
	}

	@Override
	public final void stop() {
		try {
			
			
			try {
				mudclient.clientBaseThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				System.exit(0);
			}
			
		} catch (RuntimeException var2) {
			throw GenUtil.makeThrowable(var2, "e.stop()");
		}
	}

	@Override
	public final void update(Graphics var1) {
		try {
			
			this.paint(var1);
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "e.update(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}

	private final void updateControlShiftState(InputEvent var1) {
		try {
			
			int mod = var1.getModifiers();
			if (mudclient == null)
				return;
			mudclient.controlPressed = (mod & Event.CTRL_MASK) != 0;
			mudclient.shiftPressed = (mod & Event.SHIFT_MASK) != 0;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "e.SE(" + (var1 != null ? "{...}" : "null") + ',' + "dummy" + ')');
		}
	}
	
	public final void start() {
		try {
			
			if (mudclient.threadState >= 0) {
				mudclient.threadState = 0;
			}
			startApplet(512, 334 + 12, Config.CLIENT_VERSION, 12);
		} catch (RuntimeException var2) {
			throw GenUtil.makeThrowable(var2, "e.start()");
		}
	}
	
	@Override
	public void componentShown(ComponentEvent e) {
	}

	@Override
	public void componentResized(ComponentEvent e) {
		mudclient.resizeWidth = e.getComponent().getWidth();
		mudclient.resizeHeight = e.getComponent().getHeight();
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	@Override
	public void initListeners() {
	}

	@Override
	public void crashed() {

	}

	@Override
	public void drawLoadingError() {
		Graphics g = this.getGraphics();
		if (g != null) {
			g.translate(mudclient.screenOffsetX, mudclient.screenOffsetY);
			g.setColor(Color.black);
			g.fillRect(0, 0, 512, 356);
			g.setFont(new Font("Helvetica", 1, 16));
			g.setColor(Color.yellow);
			byte var3 = 35;
			g.drawString("Sorry, an error has occured whilst loading RuneScape", 30, var3);
			g.setColor(Color.white);
			int var6 = var3 + 50;
			g.drawString("To fix this try the following (in order):", 30, var6);
			g.setColor(Color.white);
			var6 += 50;
			g.setFont(new Font("Helvetica", 1, 12));
			g.drawString("1: Try closing ALL open web-browser windows, and reloading", 30, var6);
			var6 += 30;
			g.drawString("2: Try clearing your web-browsers cache from tools->internet options", 30, var6);
			var6 += 30;
			g.drawString("3: Try using a different game-world", 30, var6);
			var6 += 30;
			g.drawString("4: Try rebooting your computer", 30, var6);
			var6 += 30;
			g.drawString("5: Try selecting a different version of Java from the play-game menu", 30, var6);
		}
	}

	@Override
	public void drawOutOfMemoryError() {
		Graphics g = this.getGraphics();
		if (null != g) {
			g.translate(mudclient.screenOffsetX, mudclient.screenOffsetY);
			g.setColor(Color.black);
			g.fillRect(0, 0, 512, 356);
			g.setFont(new Font("Helvetica", 1, 20));
			g.setColor(Color.white);
			g.drawString("Error - out of memory!", 50, 50);
			g.drawString("Close ALL unnecessary programs", 50, 100);
			g.drawString("and windows before loading the game", 50, 150);
			g.drawString("RuneScape needs about 48meg of spare RAM", 50, 200);
		}
	}

	@Override
	public void drawTextBox(String line2, byte var2, String line1) {
		Graphics g = this.getGraphics();
		if (null != g) {
			g.translate(mudclient.screenOffsetX, mudclient.screenOffsetY);
			Font font = new Font("Helvetica", 1, 15);
			short width = 512;
			g.setColor(Color.black);
			short height = 344;
			g.fillRect(width / 2 - 140, height / 2 - 25, 280, 50);
			g.setColor(Color.white);
			g.drawRect(width / 2 - 140, height / 2 - 25, 280, 50);
			this.drawCenteredString(font, line1, height / 2 - 10, true, width / 2, g);
			this.drawCenteredString(font, line2, 10 + height / 2, true, width / 2, g);
		}
	}

	private ImageConsumer imageProducer;

	@Override
	public void initGraphics() {
		int width = mudclient.getSurface().width2;
		int height = mudclient.getSurface().height2;

		if (width > 1 && height > 1) {
			this.imageModel = new DirectColorModel(32, 16711680, '\uff00', 255);
			this.backingImage = createImage(this);
			this.commitToImage(true);
			prepareImage(this.backingImage, this);
			this.commitToImage(true);
			prepareImage(this.backingImage, this);
			this.commitToImage(true);
			prepareImage(this.backingImage, this);
		}
	}

	private final synchronized void commitToImage(boolean var1) {
		try {
			
			if (null != this.imageProducer) {
				this.imageProducer.setPixels(0, 0, mudclient.getSurface().width2, mudclient.getSurface().height2,
						this.imageModel, mudclient.getSurface().pixelData, 0, mudclient.getSurface().width2);
				this.imageProducer.imageComplete(2);
			}
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "ua.CA(" + true + ')');
		}
	}

	@Override
	public void addConsumer(ImageConsumer arg0) {
		try {
			this.imageProducer = arg0;
			
			arg0.setDimensions(mudclient.getSurface().width2, mudclient.getSurface().height2);
			arg0.setProperties(null);
			arg0.setColorModel(this.imageModel);
			arg0.setHints(14);
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "ua.addConsumer(" + (arg0 != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	public boolean isConsumer(ImageConsumer arg0) {
		return this.imageProducer == arg0;
	}

	@Override
	public void removeConsumer(ImageConsumer arg0) {
		if (this.imageProducer == arg0) {
			this.imageProducer = null;
		}
	}

	@Override
	public void requestTopDownLeftRightResend(ImageConsumer arg0) {
		try {
			
			System.out.println("TDLR");
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3,
					"ua.requestTopDownLeftRightResend(" + (arg0 != null ? "{...}" : "null") + ')');
		}
	}

	@Override
	public void startProduction(ImageConsumer arg0) {
		this.addConsumer(arg0);
	}

	public final void draw(Graphics g, int x, int var3, int y) {
		this.commitToImage(true);
		g.drawImage(this.backingImage, x, y, this);
	}

	@Override
	public void draw() {
		draw(getGraphics(), mudclient.screenOffsetX, 256, mudclient.screenOffsetY);
	}

	@Override
	public void close() {
		stop();
	}

	@Override
	public String getCacheLocation() {
		return "../RSCLegacy/";
	}

	@Override
	public void resized() {
		imageProducer.setDimensions(mudclient.getSurface().width2, mudclient.getSurface().height2);
		initGraphics();
	}

	@Override
	public Sprite getSpriteFromByteArray(ByteArrayInputStream byteArrayInputStream) {
		try {
			BufferedImage image = ImageIO.read(byteArrayInputStream);

			int captchaWidth = image.getWidth();
			int captchaHeight = image.getHeight();

			int[] pixels = new int[image.getWidth() * image.getHeight()];

			for (int y = 0; y < image.getHeight(); y++) {
				for (int x = 0; x < image.getWidth(); x++) {
					int rgb = image.getRGB(x, y);
					pixels[x + y * image.getWidth()] = rgb;
				}
			}

			Sprite sprite = new Sprite(pixels, captchaWidth, captchaHeight);
			sprite.setSomething(captchaWidth, captchaHeight);
			sprite.setShift(0, 0);
			sprite.setRequiresShift(false);
			return sprite;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public void playSound(byte[] soundData) {
		System.out.println("play sound");
		
	}

	@Override
	public void stopSoundPlayer() {
		soundPlayer.stopPlayer();
	}

	@Override
	public void playSound(byte[] soundData, int offset, int dataLength) {
		soundPlayer.writeStream(soundData, offset, dataLength);
		
	}

	@Override
	public void drawKeyboard() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean saveCredentials(String creds) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String loadCredentials() {
		// TODO Auto-generated method stub
		return null;
	}
}
