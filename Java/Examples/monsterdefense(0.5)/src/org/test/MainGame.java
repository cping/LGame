package org.test;

import loon.LTexture;
import loon.LTextures;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableScreen;
import loon.canvas.LColor;
import loon.events.ActionKey;
import loon.events.GameKey;
import loon.events.GameTouch;
import loon.events.LTouchCollection;
import loon.events.SysInputFactory;
import loon.events.SysKey;
import loon.events.SysTouch;
import loon.font.LFont;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

public class MainGame extends DrawableScreen {
	public boolean allLoaded;
	public LTexture buttonStatistics;

	public int currentLevel;
	public LTouchCollection currentToucheState = new LTouchCollection();
	public LTouchCollection previouseToucheState = new LTouchCollection();
	private LColor defaultSceneColorFadeOut = new LColor(LColor.white);
	private Vector2f dragResult = new Vector2f(-1f, -1f);
	public RectBox fullScreenRect;
	public boolean gameDataLoaded;
	public CGameLoopScreen gameLoopScreen;
	public EGMODE GameMode = EGMODE.values()[0];

	public float globalScreenTimer;
	public CHelpScreen helpScreen;
	public LFont iconFont;

	public String language;
	public EGMODE lastGameMode = EGMODE.values()[0];
	public CLevel[] level;
	private int[][][] levelArray;
	public CLevelChooserScreen levelChooserScreen;
	public int levels = 0x13;
	private CWaypoints[] levelWayPointsEnemy1;
	public Vector2f MAXTILES;
	private CMenuScreen menuScreen;
	public boolean noSound;

	public CRandom random;
	public Vector2f scalePos;
	public Vector2f screenSize;
	public LFont smalFont;
	private CSplashScreen splashScreen;
	public LTexture standardBackTexture;
	public LFont standartFont;
	public CStatistics statistics;
	public Vector2f TILESIZE;

	boolean isOSUI;
	private Vector2f touchResult = new Vector2f(-1f, -1f);

	public MainGame() {
		this.scalePos = new Vector2f(1f, 1f);
		this.TILESIZE = new Vector2f(57f, 57f);
		this.MAXTILES = new Vector2f(14f, 8f);
		this.screenSize = new Vector2f(800f, 480f);
		this.fullScreenRect = new RectBox(0, 0, 800, 480);
		SysInputFactory.startTouchCollection();
	}

	private LColor white = new LColor(LColor.white);

	@Override
	public void draw(SpriteBatch batch) {
		if (!isOnLoadComplete()) {
			return;
		}
		switch (this.GameMode) {
		case GMODE_GAME:
			this.gameLoopScreen.draw(batch, white);
			break;

		case GMODE_LEVELCHOOSER:
			this.levelChooserScreen.draw(batch, white);
			break;

		case GMODE_STATISTICS:
			this.statistics.draw(batch, white);
			break;

		case GMODE_COMERCIAL:

			break;

		case GMODE_MENU:
			this.menuScreen.draw(batch, white);
			break;

		case GMODE_HOWTOPLAY:
			this.helpScreen.draw(batch, white);
			break;

		case GMODE_SPLASHSCREEN:
			this.splashScreen.draw(batch, white);
			break;
		default:
			break;
		}
		if (this.defaultSceneColorFadeOut.a > 0) {
			switch (this.lastGameMode) {
			case GMODE_GAME:
				this.gameLoopScreen.draw(batch, this.defaultSceneColorFadeOut);
				break;

			case GMODE_LEVELCHOOSER:
				this.levelChooserScreen.draw(batch,
						this.defaultSceneColorFadeOut);
				break;

			case GMODE_STATISTICS:
				this.statistics.draw(batch, this.defaultSceneColorFadeOut);
				break;

			case GMODE_COMERCIAL:

				break;

			case GMODE_MENU:
				this.menuScreen.draw(batch, this.defaultSceneColorFadeOut);
				break;

			case GMODE_HOWTOPLAY:
				this.helpScreen.draw(batch, this.defaultSceneColorFadeOut);
				break;

			case GMODE_SPLASHSCREEN:
				this.splashScreen.draw(batch, this.defaultSceneColorFadeOut);
				break;
			default:
				break;
			}
		}
	}

	public final void fillArray(int[][] array, int startX, int startY,
			int endX, int endY) {
		if (startX < endX) {
			for (int i = startX; i <= endX; i++) {
				array[i][startY] = 1;
			}
		} else {
			for (int j = endX; j <= startX; j++) {
				array[j][startY] = 1;
			}
		}
		if (startY < endY) {
			for (int k = startY; k <= endY; k++) {
				array[startX][k] = 1;
			}
		} else {
			for (int m = endY; m <= startY; m++) {
				array[startX][m] = 1;
			}
		}
	}

	public final Vector2f getCurrentTouchPos() {
		this.touchResult.x = -1f;

		if (SysTouch.isDown()) {
			this.touchResult = SysTouch.getLocation().cpy();
		}

		return this.touchResult;
	}

	public final Vector2f getDragDelta() {
		if (SysTouch.isDrag()) {
			this.dragResult = new Vector2f(getTouchDX(), getTouchDY());
		}
		return dragResult;
	}

	public final boolean isPressedAnyButton() {
		return SysKey.isKeyPressed(SysKey.A) || SysKey.isKeyPressed(SysKey.B);
	}

	public final boolean isPressedBack() {
		return KeyValue.isPressed();
	}

	public final boolean isPressedBackOrB() {
		return this.isPressedBack();
	}

	public final void LoadCnt() {

		this.standartFont = LFont.getFont(20);
		this.smalFont = LFont.getFont(20);
		this.iconFont = LFont.getFont(20);
		this.standardBackTexture = LTextures.loadTexture("standardBack.png");
		this.buttonStatistics = LTextures.loadTexture("menu/buttons/buttonStatistics.png");
		this.loadConfig();
		this.playTitleSong();
		this.statistics = new CStatistics("swampdefencestats", this);
		this.menuScreen = new CMenuScreen(this);
		this.menuScreen.LoadContent();
		this.levelChooserScreen = new CLevelChooserScreen(this);
		this.levelChooserScreen.LoadContent();
		this.gameLoopScreen = new CGameLoopScreen(this);
		this.gameLoopScreen.LoadContent();
		this.helpScreen = new CHelpScreen(this);
		this.helpScreen.LoadContent();

		this.levelArray = new int[this.levels][][];
		for (int i = 0; i < this.levels; i++) {
			this.levelArray[i] = new int[(int) this.MAXTILES.x][(int) this.MAXTILES.y];
			this.levelArray[i][((int) this.MAXTILES.x) - 1][((int) this.MAXTILES.y) - 1] = 2;
		}
		this.levelWayPointsEnemy1 = new CWaypoints[this.levels];
		this.levelWayPointsEnemy1[0] = new CWaypoints();
		this.levelWayPointsEnemy1[0].init(this, 10, this.TILESIZE.x,
				this.TILESIZE.y);
		this.levelWayPointsEnemy1[0].addPoint(-1, 2);
		this.fillArray(this.levelArray[0], 0, 2, 2, 2);
		this.levelWayPointsEnemy1[0].addPoint(2, 2);
		this.fillArray(this.levelArray[0], 2, 2, 2, 5);
		this.levelWayPointsEnemy1[0].addPoint(2, 5);
		this.fillArray(this.levelArray[0], 2, 5, 4, 5);
		this.levelWayPointsEnemy1[0].addPoint(4, 5);
		this.fillArray(this.levelArray[0], 4, 5, 4, 2);
		this.levelWayPointsEnemy1[0].addPoint(4, 2);
		this.fillArray(this.levelArray[0], 4, 2, 7, 2);
		this.levelWayPointsEnemy1[0].addPoint(7, 2);
		this.fillArray(this.levelArray[0], 7, 2, 7, 7);
		this.levelWayPointsEnemy1[0].addPoint(7, 7);
		this.fillArray(this.levelArray[0], 7, 7, 11, 7);
		this.levelWayPointsEnemy1[0].addPoint(11, 7);
		this.fillArray(this.levelArray[0], 11, 7, 11, 6);
		this.levelWayPointsEnemy1[0].addPoint(11, 6);
		this.fillArray(this.levelArray[0], 11, 6, 13, 6);
		this.levelWayPointsEnemy1[0].addPoint((int) this.MAXTILES.x, 6);
		this.levelArray[0][9][5] = 1;
		this.fillArray(this.levelArray[0], 11, 3, 12, 3);
		this.fillArray(this.levelArray[0], 4, 7, 5, 7);
		this.levelArray[0][1][7] = 1;
		this.fillArray(this.levelArray[0], 0, 0, ((int) this.MAXTILES.x) - 1, 0);
		this.fillArray(this.levelArray[0], 0, 0, 0, 7);
		this.fillArray(this.levelArray[0], ((int) this.MAXTILES.x) - 1, 0,
				((int) this.MAXTILES.x) - 1, 7);
		this.fillArray(this.levelArray[0], 0, 0, 6, 0);
		this.fillArray(this.levelArray[0], 0, 1, 5, 1);
		this.levelArray[0][3][2] = 1;
		this.fillArray(this.levelArray[0], 0, 3, 3, 3);
		this.fillArray(this.levelArray[0], 0, 4, 1, 4);
		this.levelArray[0][3][3] = 0;
		this.levelWayPointsEnemy1[0].fillArray(this.levelArray[0]);
		this.levelWayPointsEnemy1[1] = new CWaypoints();
		this.levelWayPointsEnemy1[1].init(this, 14, this.TILESIZE.x,
				this.TILESIZE.y);
		this.levelWayPointsEnemy1[1].addPoint(-1, 6);
		this.levelWayPointsEnemy1[1].addPoint(1, 6);
		this.levelWayPointsEnemy1[1].addPoint(1, 2);
		this.levelWayPointsEnemy1[1].addPoint(2, 2);
		this.levelWayPointsEnemy1[1].addPoint(2, 4);
		this.levelWayPointsEnemy1[1].addPoint(5, 4);
		this.levelWayPointsEnemy1[1].addPoint(5, 3);
		this.levelWayPointsEnemy1[1].addPoint(6, 3);
		this.levelWayPointsEnemy1[1].addPoint(6, 6);
		this.levelWayPointsEnemy1[1].addPoint(9, 6);
		this.levelWayPointsEnemy1[1].addPoint(9, 1);
		this.levelWayPointsEnemy1[1].addPoint(11, 1);
		this.levelWayPointsEnemy1[1].addPoint(11, 3);
		this.levelWayPointsEnemy1[1].addPoint((int) this.MAXTILES.x, 3);
		this.fillArray(this.levelArray[1], 0, 0, ((int) this.MAXTILES.x) - 1, 0);
		this.fillArray(this.levelArray[1], 0, 0, 0, 7);
		this.fillArray(this.levelArray[1], ((int) this.MAXTILES.x) - 1, 0,
				((int) this.MAXTILES.x) - 1, 7);
		this.fillArray(this.levelArray[1], 2, 7, 3, 7);
		this.fillArray(this.levelArray[1], 12, 6, 13, 6);
		this.fillArray(this.levelArray[1], 4, 1, 8, 1);
		this.levelArray[1][5][2] = 1;
		this.levelArray[1][10][3] = 1;
		this.fillArray(this.levelArray[1], 7, 2, 8, 2);
		this.fillArray(this.levelArray[1], 7, 3, 8, 3);
		this.levelArray[1][8][4] = 1;
		this.levelArray[1][12][2] = 1;
		this.fillArray(this.levelArray[1], 12, 1, 13, 1);
		this.levelWayPointsEnemy1[1].fillArray(this.levelArray[1]);
		this.levelWayPointsEnemy1[2] = new CWaypoints();
		this.levelWayPointsEnemy1[2].init(this, 12, this.TILESIZE.x,
				this.TILESIZE.y);
		this.levelWayPointsEnemy1[2].addPoint(-1, 3);
		this.levelWayPointsEnemy1[2].addPoint(3, 3);
		this.levelWayPointsEnemy1[2].addPoint(3, 2);
		this.levelWayPointsEnemy1[2].addPoint(11, 2);
		this.levelWayPointsEnemy1[2].addPoint(11, 4);
		this.levelWayPointsEnemy1[2].addPoint(4, 4);
		this.levelWayPointsEnemy1[2].addPoint(4, 6);
		this.levelWayPointsEnemy1[2].addPoint(6, 6);
		this.levelWayPointsEnemy1[2].addPoint(6, 5);
		this.levelWayPointsEnemy1[2].addPoint(9, 5);
		this.levelWayPointsEnemy1[2].addPoint(9, 6);
		this.levelWayPointsEnemy1[2].addPoint((int) this.MAXTILES.x, 6);
		this.fillArray(this.levelArray[2], 0, 0, ((int) this.MAXTILES.x) - 1, 0);
		this.fillArray(this.levelArray[2], 0, 0, 0, 7);
		this.fillArray(this.levelArray[2], ((int) this.MAXTILES.x) - 1, 0,
				((int) this.MAXTILES.x) - 1, 7);
		this.fillArray(this.levelArray[2], 1, 1, 2, 1);
		this.levelArray[2][1][5] = 1;
		this.fillArray(this.levelArray[2], 6, 3, 7, 3);
		this.levelArray[2][11][1] = 1;
		this.levelArray[2][12][1] = 1;
		this.fillArray(this.levelArray[2], 12, 2, 13, 2);
		this.fillArray(this.levelArray[2], 12, 3, 13, 3);
		this.fillArray(this.levelArray[2], 12, 4, 13, 4);
		this.fillArray(this.levelArray[2], 11, 5, 13, 3);
		this.fillArray(this.levelArray[2], 12, 7, 13, 7);
		this.levelWayPointsEnemy1[2].fillArray(this.levelArray[2]);
		this.levelWayPointsEnemy1[3] = new CWaypoints();
		this.levelWayPointsEnemy1[3].init(this, 8, this.TILESIZE.x,
				this.TILESIZE.y);
		this.levelWayPointsEnemy1[3].addPoint(-1, 6);
		this.levelWayPointsEnemy1[3].addPoint(4, 6);
		this.levelWayPointsEnemy1[3].addPoint(4, 1);
		this.levelWayPointsEnemy1[3].addPoint(11, 1);
		this.levelWayPointsEnemy1[3].addPoint(11, 3);
		this.levelWayPointsEnemy1[3].addPoint(6, 3);
		this.levelWayPointsEnemy1[3].addPoint(6, 6);
		this.levelWayPointsEnemy1[3].addPoint((int) this.MAXTILES.x, 6);
		this.fillArray(this.levelArray[3], 0, 0, ((int) this.MAXTILES.x) - 1, 0);
		this.fillArray(this.levelArray[3], 0, 0, 0, 7);
		this.fillArray(this.levelArray[3], ((int) this.MAXTILES.x) - 1, 0,
				((int) this.MAXTILES.x) - 1, 7);
		this.fillArray(this.levelArray[3], 1, 1, 1, 5);
		this.fillArray(this.levelArray[3], 2, 2, 2, 5);
		this.fillArray(this.levelArray[3], 3, 1, 3, 5);
		this.levelArray[3][12][4] = 1;
		this.levelArray[3][13][5] = 1;
		this.fillArray(this.levelArray[3], 11, 7, 13, 7);
		this.fillArray(this.levelArray[3], 1, 7, 2, 7);
		this.fillArray(this.levelArray[3], 5, 2, 5, 5);
		this.levelArray[3][6][2] = 1;
		this.levelWayPointsEnemy1[3].fillArray(this.levelArray[3]);
		this.levelWayPointsEnemy1[4] = new CWaypoints();
		this.levelWayPointsEnemy1[4].init(this, 10, this.TILESIZE.x,
				this.TILESIZE.y);
		this.levelWayPointsEnemy1[4].addPoint(-1, 6);
		this.levelWayPointsEnemy1[4].addPoint(1, 6);
		this.levelWayPointsEnemy1[4].addPoint(1, 2);
		this.levelWayPointsEnemy1[4].addPoint(3, 2);
		this.levelWayPointsEnemy1[4].addPoint(3, 6);
		this.levelWayPointsEnemy1[4].addPoint(5, 6);
		this.levelWayPointsEnemy1[4].addPoint(5, 4);
		this.levelWayPointsEnemy1[4].addPoint(11, 4);
		this.levelWayPointsEnemy1[4].addPoint(11, 6);
		this.levelWayPointsEnemy1[4].addPoint((int) this.MAXTILES.x, 6);
		this.fillArray(this.levelArray[4], 0, 0, ((int) this.MAXTILES.x) - 1, 0);
		this.fillArray(this.levelArray[4], 0, 0, 0, 7);
		this.fillArray(this.levelArray[4], ((int) this.MAXTILES.x) - 1, 0,
				((int) this.MAXTILES.x) - 1, 7);
		this.levelArray[4][1][7] = 1;
		this.fillArray(this.levelArray[4], 5, 1, 6, 1);
		this.levelArray[4][9][1] = 1;
		this.levelArray[4][10][2] = 1;
		this.fillArray(this.levelArray[4], 8, 6, 9, 6);
		this.fillArray(this.levelArray[4], 7, 7, 8, 7);
		this.fillArray(this.levelArray[4], 12, 1, 13, 1);
		this.levelArray[4][13][2] = 1;
		this.levelArray[4][10][1] = 1;
		this.levelWayPointsEnemy1[4].fillArray(this.levelArray[4]);
		this.levelWayPointsEnemy1[5] = new CWaypoints();
		this.levelWayPointsEnemy1[5].init(this, 12, this.TILESIZE.x,
				this.TILESIZE.y);
		this.levelWayPointsEnemy1[5].addPoint(-1, 6);
		this.levelWayPointsEnemy1[5].addPoint(3, 6);
		this.levelWayPointsEnemy1[5].addPoint(3, 3);
		this.levelWayPointsEnemy1[5].addPoint(2, 3);
		this.levelWayPointsEnemy1[5].addPoint(2, 2);
		this.levelWayPointsEnemy1[5].addPoint(8, 2);
		this.levelWayPointsEnemy1[5].addPoint(8, 3);
		this.levelWayPointsEnemy1[5].addPoint(7, 3);
		this.levelWayPointsEnemy1[5].addPoint(7, 5);
		this.levelWayPointsEnemy1[5].addPoint(10, 5);
		this.levelWayPointsEnemy1[5].addPoint(10, 4);
		this.levelWayPointsEnemy1[5].addPoint((int) this.MAXTILES.x, 4);
		this.fillArray(this.levelArray[5], 0, 0, ((int) this.MAXTILES.x) - 1, 0);
		this.fillArray(this.levelArray[5], 0, 0, 0, 7);
		this.fillArray(this.levelArray[5], ((int) this.MAXTILES.x) - 1, 0,
				((int) this.MAXTILES.x) - 1, 7);
		this.levelArray[5][1][1] = 1;
		this.levelArray[5][1][4] = 1;
		this.fillArray(this.levelArray[5], 1, 7, 9, 7);
		this.fillArray(this.levelArray[5], 5, 6, 6, 6);
		this.levelArray[5][6][5] = 1;
		this.fillArray(this.levelArray[5], 8, 4, 9, 4);
		this.fillArray(this.levelArray[5], 8, 6, 9, 6);
		this.fillArray(this.levelArray[5], 11, 3, 13, 3);
		this.levelArray[5][13][2] = 1;
		this.levelWayPointsEnemy1[5].fillArray(this.levelArray[5]);
		this.levelWayPointsEnemy1[6] = new CWaypoints();
		this.levelWayPointsEnemy1[6].init(this, 8, this.TILESIZE.x,
				this.TILESIZE.y);
		this.levelWayPointsEnemy1[6].addPoint(-1, 4);
		this.levelWayPointsEnemy1[6].addPoint(5, 4);
		this.levelWayPointsEnemy1[6].addPoint(5, 2);
		this.levelWayPointsEnemy1[6].addPoint(8, 2);
		this.levelWayPointsEnemy1[6].addPoint(8, 5);
		this.levelWayPointsEnemy1[6].addPoint(10, 5);
		this.levelWayPointsEnemy1[6].addPoint(10, 3);
		this.levelWayPointsEnemy1[6].addPoint((int) this.MAXTILES.x, 3);
		this.fillArray(this.levelArray[6], 0, 0, ((int) this.MAXTILES.x) - 1, 0);
		this.fillArray(this.levelArray[6], 0, 0, 0, 7);
		this.fillArray(this.levelArray[6], ((int) this.MAXTILES.x) - 1, 0,
				((int) this.MAXTILES.x) - 1, 7);
		this.fillArray(this.levelArray[6], 1, 1, 1, 3);
		this.fillArray(this.levelArray[6], 2, 1, 2, 3);
		this.fillArray(this.levelArray[6], 3, 2, 3, 3);
		this.fillArray(this.levelArray[6], 4, 2, 4, 3);
		this.fillArray(this.levelArray[6], 5, 1, 7, 1);
		this.fillArray(this.levelArray[6], 3, 1, 4, 1);
		this.levelArray[6][1][7] = 1;
		this.levelWayPointsEnemy1[6].fillArray(this.levelArray[6]);
		this.levelWayPointsEnemy1[7] = new CWaypoints();
		this.levelWayPointsEnemy1[7].init(this, 12, this.TILESIZE.x,
				this.TILESIZE.y);
		this.levelWayPointsEnemy1[7].addPoint(-1, 3);
		this.levelWayPointsEnemy1[7].addPoint(2, 3);
		this.levelWayPointsEnemy1[7].addPoint(2, 1);
		this.levelWayPointsEnemy1[7].addPoint(4, 1);
		this.levelWayPointsEnemy1[7].addPoint(4, 6);
		this.levelWayPointsEnemy1[7].addPoint(8, 6);
		this.levelWayPointsEnemy1[7].addPoint(8, 2);
		this.levelWayPointsEnemy1[7].addPoint(6, 2);
		this.levelWayPointsEnemy1[7].addPoint(6, 4);
		this.levelWayPointsEnemy1[7].addPoint(11, 4);
		this.levelWayPointsEnemy1[7].addPoint(11, 2);
		this.levelWayPointsEnemy1[7].addPoint((int) this.MAXTILES.x, 2);
		this.fillArray(this.levelArray[7], 0, 0, ((int) this.MAXTILES.x) - 1, 0);
		this.fillArray(this.levelArray[7], 0, 0, 0, 7);
		this.fillArray(this.levelArray[7], ((int) this.MAXTILES.x) - 1, 0,
				((int) this.MAXTILES.x) - 1, 7);
		this.levelArray[7][1][7] = 1;
		this.levelArray[7][2][4] = 1;
		this.levelArray[7][3][5] = 1;
		this.levelArray[7][9][5] = 1;
		this.levelArray[7][13][5] = 1;
		this.fillArray(this.levelArray[7], 10, 6, 13, 6);
		this.fillArray(this.levelArray[7], 10, 7, 13, 7);
		this.levelWayPointsEnemy1[7].fillArray(this.levelArray[7]);
		this.levelWayPointsEnemy1[8] = new CWaypoints();
		this.levelWayPointsEnemy1[8].init(this, 0x12, this.TILESIZE.x,
				this.TILESIZE.y);
		this.levelWayPointsEnemy1[8].addPoint(-1, 2);
		this.levelWayPointsEnemy1[8].addPoint(1, 2);
		this.levelWayPointsEnemy1[8].addPoint(1, 3);
		this.levelWayPointsEnemy1[8].addPoint(2, 3);
		this.levelWayPointsEnemy1[8].addPoint(2, 5);
		this.levelWayPointsEnemy1[8].addPoint(1, 5);
		this.levelWayPointsEnemy1[8].addPoint(1, 6);
		this.levelWayPointsEnemy1[8].addPoint(4, 6);
		this.levelWayPointsEnemy1[8].addPoint(4, 3);
		this.levelWayPointsEnemy1[8].addPoint(7, 3);
		this.levelWayPointsEnemy1[8].addPoint(7, 5);
		this.levelWayPointsEnemy1[8].addPoint(6, 5);
		this.levelWayPointsEnemy1[8].addPoint(6, 6);
		this.levelWayPointsEnemy1[8].addPoint(9, 6);
		this.levelWayPointsEnemy1[8].addPoint(9, 4);
		this.levelWayPointsEnemy1[8].addPoint(11, 4);
		this.levelWayPointsEnemy1[8].addPoint(11, 3);
		this.levelWayPointsEnemy1[8].addPoint((int) this.MAXTILES.x, 3);
		this.fillArray(this.levelArray[8], 0, 0, ((int) this.MAXTILES.x) - 1, 0);
		this.fillArray(this.levelArray[8], 0, 0, 0, 7);
		this.fillArray(this.levelArray[8], ((int) this.MAXTILES.x) - 1, 0,
				((int) this.MAXTILES.x) - 1, 7);
		this.levelArray[8][1][1] = 1;
		this.levelArray[8][1][7] = 1;
		this.fillArray(this.levelArray[8], 3, 4, 3, 5);
		this.fillArray(this.levelArray[8], 7, 1, 8, 1);
		this.fillArray(this.levelArray[8], 12, 1, 13, 1);
		this.fillArray(this.levelArray[8], 12, 7, 13, 7);
		this.levelWayPointsEnemy1[8].fillArray(this.levelArray[8]);
		this.levelWayPointsEnemy1[9] = new CWaypoints();
		this.levelWayPointsEnemy1[9].init(this, 12, this.TILESIZE.x,
				this.TILESIZE.y);
		this.levelWayPointsEnemy1[9].addPoint(-1, 3);
		this.levelWayPointsEnemy1[9].addPoint(2, 3);
		this.levelWayPointsEnemy1[9].addPoint(2, 2);
		this.levelWayPointsEnemy1[9].addPoint(4, 2);
		this.levelWayPointsEnemy1[9].addPoint(4, 4);
		this.levelWayPointsEnemy1[9].addPoint(6, 4);
		this.levelWayPointsEnemy1[9].addPoint(6, 6);
		this.levelWayPointsEnemy1[9].addPoint(9, 6);
		this.levelWayPointsEnemy1[9].addPoint(9, 2);
		this.levelWayPointsEnemy1[9].addPoint(11, 2);
		this.levelWayPointsEnemy1[9].addPoint(11, 4);
		this.levelWayPointsEnemy1[9].addPoint((int) this.MAXTILES.x, 4);
		this.fillArray(this.levelArray[9], 0, 0, ((int) this.MAXTILES.x) - 1, 0);
		this.fillArray(this.levelArray[9], 0, 0, 0, 7);
		this.fillArray(this.levelArray[9], ((int) this.MAXTILES.x) - 1, 0,
				((int) this.MAXTILES.x) - 1, 7);
		this.levelArray[9][6][2] = 1;
		this.levelArray[9][10][3] = 1;
		this.fillArray(this.levelArray[9], 1, 1, 2, 1);
		this.fillArray(this.levelArray[9], 8, 1, 10, 1);
		this.fillArray(this.levelArray[9], 12, 1, 13, 1);
		this.fillArray(this.levelArray[9], 13, 2, 13, 3);
		this.levelArray[9][12][3] = 1;
		this.levelWayPointsEnemy1[9].fillArray(this.levelArray[9]);
		this.levelWayPointsEnemy1[10] = new CWaypoints();
		this.levelWayPointsEnemy1[10].init(this, 12, this.TILESIZE.x,
				this.TILESIZE.y);
		this.levelWayPointsEnemy1[10].addPoint(-1, 4);
		this.levelWayPointsEnemy1[10].addPoint(2, 4);
		this.levelWayPointsEnemy1[10].addPoint(2, 2);
		this.levelWayPointsEnemy1[10].addPoint(4, 2);
		this.levelWayPointsEnemy1[10].addPoint(4, 4);
		this.levelWayPointsEnemy1[10].addPoint(6, 4);
		this.levelWayPointsEnemy1[10].addPoint(6, 2);
		this.levelWayPointsEnemy1[10].addPoint(8, 2);
		this.levelWayPointsEnemy1[10].addPoint(8, 6);
		this.levelWayPointsEnemy1[10].addPoint(10, 6);
		this.levelWayPointsEnemy1[10].addPoint(10, 4);
		this.levelWayPointsEnemy1[10].addPoint((int) this.MAXTILES.x, 4);
		this.fillArray(this.levelArray[10], 0, 0, ((int) this.MAXTILES.x) - 1,
				0);
		this.fillArray(this.levelArray[10], 0, 0, 0, 7);
		this.fillArray(this.levelArray[10], ((int) this.MAXTILES.x) - 1, 0,
				((int) this.MAXTILES.x) - 1, 7);
		this.levelArray[10][3][3] = 1;
		this.fillArray(this.levelArray[10], 1, 1, 5, 1);
		this.fillArray(this.levelArray[10], 1, 2, 1, 3);
		this.fillArray(this.levelArray[10], 2, 7, 6, 7);
		this.levelArray[10][5][5] = 1;
		this.levelArray[10][6][6] = 1;
		this.levelArray[10][8][1] = 1;
		this.fillArray(this.levelArray[10], 9, 1, 9, 5);
		this.fillArray(this.levelArray[10], 10, 1, 10, 2);
		this.fillArray(this.levelArray[10], 11, 1, 11, 3);
		this.fillArray(this.levelArray[10], 12, 1, 13, 1);
		this.fillArray(this.levelArray[10], 12, 3, 13, 3);
		this.levelArray[10][13][2] = 1;
		this.fillArray(this.levelArray[10], 11, 5, 11, 6);
		this.fillArray(this.levelArray[10], 12, 5, 12, 7);
		this.fillArray(this.levelArray[10], 13, 5, 13, 7);
		this.levelWayPointsEnemy1[10].fillArray(this.levelArray[10]);
		this.levelWayPointsEnemy1[11] = new CWaypoints();
		this.levelWayPointsEnemy1[11].init(this, 10, this.TILESIZE.x,
				this.TILESIZE.y);
		this.levelWayPointsEnemy1[11].addPoint(-1, 4);
		this.levelWayPointsEnemy1[11].addPoint(1, 4);
		this.levelWayPointsEnemy1[11].addPoint(1, 3);
		this.levelWayPointsEnemy1[11].addPoint(6, 3);
		this.levelWayPointsEnemy1[11].addPoint(6, 6);
		this.levelWayPointsEnemy1[11].addPoint(9, 6);
		this.levelWayPointsEnemy1[11].addPoint(9, 4);
		this.levelWayPointsEnemy1[11].addPoint(11, 4);
		this.levelWayPointsEnemy1[11].addPoint(11, 1);
		this.levelWayPointsEnemy1[11].addPoint((int) this.MAXTILES.x, 1);
		this.fillArray(this.levelArray[11], 0, 0, ((int) this.MAXTILES.x) - 1,
				0);
		this.fillArray(this.levelArray[11], 0, 0, 0, 7);
		this.fillArray(this.levelArray[11], ((int) this.MAXTILES.x) - 1, 0,
				((int) this.MAXTILES.x) - 1, 7);
		this.fillArray(this.levelArray[11], 1, 1, 10, 1);
		this.fillArray(this.levelArray[11], 1, 2, 3, 2);
		this.fillArray(this.levelArray[11], 5, 2, 7, 2);
		this.fillArray(this.levelArray[11], 3, 4, 5, 4);
		this.fillArray(this.levelArray[11], 1, 5, 5, 5);
		this.fillArray(this.levelArray[11], 1, 6, 4, 6);
		this.fillArray(this.levelArray[11], 1, 7, 2, 7);
		this.levelArray[11][8][3] = 1;
		this.levelArray[11][12][6] = 1;
		this.levelArray[11][13][7] = 1;
		this.levelWayPointsEnemy1[11].fillArray(this.levelArray[11]);
		this.levelWayPointsEnemy1[12] = new CWaypoints();
		this.levelWayPointsEnemy1[12].init(this, 10, this.TILESIZE.x,
				this.TILESIZE.y);
		this.levelWayPointsEnemy1[12].addPoint(-1, 1);
		this.levelWayPointsEnemy1[12].addPoint(2, 1);
		this.levelWayPointsEnemy1[12].addPoint(2, 2);
		this.levelWayPointsEnemy1[12].addPoint(4, 2);
		this.levelWayPointsEnemy1[12].addPoint(4, 3);
		this.levelWayPointsEnemy1[12].addPoint(8, 3);
		this.levelWayPointsEnemy1[12].addPoint(8, 5);
		this.levelWayPointsEnemy1[12].addPoint(10, 5);
		this.levelWayPointsEnemy1[12].addPoint(10, 4);
		this.levelWayPointsEnemy1[12].addPoint((int) this.MAXTILES.x, 4);
		this.fillArray(this.levelArray[12], 0, 0, ((int) this.MAXTILES.x) - 1,
				0);
		this.fillArray(this.levelArray[12], 0, 0, 0, 7);
		this.fillArray(this.levelArray[12], ((int) this.MAXTILES.x) - 1, 0,
				((int) this.MAXTILES.x) - 1, 7);
		this.levelArray[12][1][4] = 1;
		this.fillArray(this.levelArray[12], 1, 6, 2, 6);
		this.fillArray(this.levelArray[12], 11, 1, 12, 1);
		this.levelArray[12][11][7] = 1;
		this.levelWayPointsEnemy1[12].fillArray(this.levelArray[12]);
		this.levelWayPointsEnemy1[13] = new CWaypoints();
		this.levelWayPointsEnemy1[13].init(this, 12, this.TILESIZE.x,
				this.TILESIZE.y);
		this.levelWayPointsEnemy1[13].addPoint(-1, 4);
		this.levelWayPointsEnemy1[13].addPoint(2, 4);
		this.levelWayPointsEnemy1[13].addPoint(2, 2);
		this.levelWayPointsEnemy1[13].addPoint(4, 2);
		this.levelWayPointsEnemy1[13].addPoint(4, 4);
		this.levelWayPointsEnemy1[13].addPoint(5, 4);
		this.levelWayPointsEnemy1[13].addPoint(5, 5);
		this.levelWayPointsEnemy1[13].addPoint(7, 5);
		this.levelWayPointsEnemy1[13].addPoint(7, 6);
		this.levelWayPointsEnemy1[13].addPoint(10, 6);
		this.levelWayPointsEnemy1[13].addPoint(10, 2);
		this.levelWayPointsEnemy1[13].addPoint((int) this.MAXTILES.x, 2);
		this.fillArray(this.levelArray[13], 0, 0, ((int) this.MAXTILES.x) - 1,
				0);
		this.fillArray(this.levelArray[13], 0, 0, 0, 7);
		this.fillArray(this.levelArray[13], ((int) this.MAXTILES.x) - 1, 0,
				((int) this.MAXTILES.x) - 1, 7);
		this.levelArray[13][2][7] = 1;
		this.fillArray(this.levelArray[13], 9, 1, 13, 1);
		this.fillArray(this.levelArray[13], 11, 3, 13, 3);
		this.levelArray[13][12][7] = 1;
		this.levelArray[13][12][6] = 1;
		this.levelWayPointsEnemy1[13].fillArray(this.levelArray[13]);
		this.levelWayPointsEnemy1[14] = new CWaypoints();
		this.levelWayPointsEnemy1[14].init(this, 10, this.TILESIZE.x,
				this.TILESIZE.y);
		this.levelWayPointsEnemy1[14].addPoint(-1, 2);
		this.levelWayPointsEnemy1[14].addPoint(8, 2);
		this.levelWayPointsEnemy1[14].addPoint(8, 5);
		this.levelWayPointsEnemy1[14].addPoint(4, 5);
		this.levelWayPointsEnemy1[14].addPoint(4, 4);
		this.levelWayPointsEnemy1[14].addPoint(2, 4);
		this.levelWayPointsEnemy1[14].addPoint(2, 6);
		this.levelWayPointsEnemy1[14].addPoint(10, 6);
		this.levelWayPointsEnemy1[14].addPoint(10, 3);
		this.levelWayPointsEnemy1[14].addPoint((int) this.MAXTILES.x, 3);
		this.fillArray(this.levelArray[14], 0, 0, ((int) this.MAXTILES.x) - 1,
				0);
		this.fillArray(this.levelArray[14], 0, 0, 0, 7);
		this.fillArray(this.levelArray[14], ((int) this.MAXTILES.x) - 1, 0,
				((int) this.MAXTILES.x) - 1, 7);
		this.levelArray[14][11][1] = 1;
		this.levelArray[14][1][3] = 1;
		this.fillArray(this.levelArray[14], 1, 1, 5, 1);
		this.levelArray[14][12][7] = 1;
		this.levelArray[14][13][6] = 1;
		this.levelWayPointsEnemy1[14].fillArray(this.levelArray[14]);
		this.levelWayPointsEnemy1[15] = new CWaypoints();
		this.levelWayPointsEnemy1[15].init(this, 12, this.TILESIZE.x,
				this.TILESIZE.y);
		this.levelWayPointsEnemy1[15].addPoint(-1, 3);
		this.levelWayPointsEnemy1[15].addPoint(2, 3);
		this.levelWayPointsEnemy1[15].addPoint(2, 2);
		this.levelWayPointsEnemy1[15].addPoint(4, 2);
		this.levelWayPointsEnemy1[15].addPoint(4, 3);
		this.levelWayPointsEnemy1[15].addPoint(5, 3);
		this.levelWayPointsEnemy1[15].addPoint(5, 5);
		this.levelWayPointsEnemy1[15].addPoint(6, 5);
		this.levelWayPointsEnemy1[15].addPoint(6, 6);
		this.levelWayPointsEnemy1[15].addPoint(10, 6);
		this.levelWayPointsEnemy1[15].addPoint(10, 5);
		this.levelWayPointsEnemy1[15].addPoint((int) this.MAXTILES.x, 5);
		this.fillArray(this.levelArray[15], 0, 0, ((int) this.MAXTILES.x) - 1,
				0);
		this.fillArray(this.levelArray[15], 0, 0, 0, 7);
		this.fillArray(this.levelArray[15], ((int) this.MAXTILES.x) - 1, 0,
				((int) this.MAXTILES.x) - 1, 7);
		this.levelArray[15][1][7] = 1;
		this.levelArray[15][1][6] = 1;
		this.fillArray(this.levelArray[15], 3, 7, 13, 7);
		this.levelArray[15][12][6] = 1;
		this.levelArray[15][13][6] = 1;
		this.fillArray(this.levelArray[15], 11, 4, 13, 4);
		this.fillArray(this.levelArray[15], 11, 3, 13, 3);
		this.fillArray(this.levelArray[15], 9, 2, 13, 2);
		this.fillArray(this.levelArray[15], 6, 1, 13, 1);
		this.fillArray(this.levelArray[15], 11, 3, 13, 3);
		this.levelArray[15][9][3] = 1;
		this.levelArray[15][9][2] = 1;
		this.levelArray[15][8][5] = 1;
		this.levelArray[15][9][5] = 1;
		this.levelWayPointsEnemy1[15].fillArray(this.levelArray[15]);
		int index = 0x10;
		this.levelWayPointsEnemy1[index] = new CWaypoints();
		this.levelWayPointsEnemy1[index].init(this, 8, this.TILESIZE.x,
				this.TILESIZE.y);
		this.levelWayPointsEnemy1[index].addPoint(-1, 5);
		this.levelWayPointsEnemy1[index].addPoint(5, 5);
		this.levelWayPointsEnemy1[index].addPoint(5, 3);
		this.levelWayPointsEnemy1[index].addPoint(4, 3);
		this.levelWayPointsEnemy1[index].addPoint(4, 2);
		this.levelWayPointsEnemy1[index].addPoint(7, 2);
		this.levelWayPointsEnemy1[index].addPoint(7, 4);
		this.levelWayPointsEnemy1[index].addPoint((int) this.MAXTILES.x, 4);
		this.fillArray(this.levelArray[index], 0, 0,
				((int) this.MAXTILES.x) - 1, 0);
		this.fillArray(this.levelArray[index], 0, 0, 0, 7);
		this.fillArray(this.levelArray[index], ((int) this.MAXTILES.x) - 1, 0,
				((int) this.MAXTILES.x) - 1, 7);
		this.fillArray(this.levelArray[index], 1, 1, 6, 1);
		this.levelArray[index][9][1] = 1;
		this.levelArray[index][11][1] = 1;
		this.levelArray[index][12][1] = 1;
		this.fillArray(this.levelArray[index], 1, 2, 3, 2);
		this.levelArray[index][11][2] = 1;
		this.levelArray[index][12][2] = 1;
		this.levelArray[index][2][3] = 1;
		this.levelArray[index][1][4] = 1;
		this.levelArray[index][2][4] = 1;
		this.levelArray[index][8][6] = 1;
		this.levelArray[index][8][7] = 1;
		this.levelArray[index][9][6] = 1;
		this.levelArray[index][9][7] = 1;
		this.levelArray[index][12][5] = 1;
		this.levelWayPointsEnemy1[index].fillArray(this.levelArray[index]);
		index++;
		this.levelWayPointsEnemy1[index] = new CWaypoints();
		this.levelWayPointsEnemy1[index].init(this, 8, this.TILESIZE.x,
				this.TILESIZE.y);
		this.levelWayPointsEnemy1[index].addPoint(-1, 4);
		this.levelWayPointsEnemy1[index].addPoint(2, 4);
		this.levelWayPointsEnemy1[index].addPoint(2, 6);
		this.levelWayPointsEnemy1[index].addPoint(9, 6);
		this.levelWayPointsEnemy1[index].addPoint(9, 4);
		this.levelWayPointsEnemy1[index].addPoint(6, 4);
		this.levelWayPointsEnemy1[index].addPoint(6, 2);
		this.levelWayPointsEnemy1[index].addPoint((int) this.MAXTILES.x, 2);
		this.fillArray(this.levelArray[index], 0, 0,
				((int) this.MAXTILES.x) - 1, 0);
		this.fillArray(this.levelArray[index], 0, 0, 0, 7);
		this.fillArray(this.levelArray[index], ((int) this.MAXTILES.x) - 1, 0,
				((int) this.MAXTILES.x) - 1, 7);
		this.fillArray(this.levelArray[index], 1, 7, 11, 7);
		this.levelArray[index][1][1] = 1;
		this.levelArray[index][2][1] = 1;
		this.levelArray[index][5][5] = 1;
		this.levelArray[index][6][5] = 1;
		this.levelArray[index][7][5] = 1;
		this.levelArray[index][13][4] = 1;
		this.levelWayPointsEnemy1[index].fillArray(this.levelArray[index]);
		index++;
		this.levelWayPointsEnemy1[index] = new CWaypoints();
		this.levelWayPointsEnemy1[index].init(this, 8, this.TILESIZE.x,
				this.TILESIZE.y);
		this.levelWayPointsEnemy1[index].addPoint(-1, 2);
		this.levelWayPointsEnemy1[index].addPoint(8, 2);
		this.levelWayPointsEnemy1[index].addPoint(8, 4);
		this.levelWayPointsEnemy1[index].addPoint(9, 4);
		this.levelWayPointsEnemy1[index].addPoint(9, 6);
		this.levelWayPointsEnemy1[index].addPoint(11, 6);
		this.levelWayPointsEnemy1[index].addPoint(11, 4);
		this.levelWayPointsEnemy1[index].addPoint((int) this.MAXTILES.x, 4);
		this.fillArray(this.levelArray[index], 0, 0,
				((int) this.MAXTILES.x) - 1, 0);
		this.fillArray(this.levelArray[index], 0, 0, 0, 7);
		this.fillArray(this.levelArray[index], ((int) this.MAXTILES.x) - 1, 0,
				((int) this.MAXTILES.x) - 1, 7);
		this.fillArray(this.levelArray[index], 1, 4, 3, 4);
		this.levelArray[index][3][5] = 1;
		this.levelArray[index][4][1] = 1;
		this.levelArray[index][6][5] = 1;
		this.levelArray[index][6][7] = 1;
		this.levelArray[index][7][6] = 1;
		this.levelArray[index][7][7] = 1;
		this.levelArray[index][8][6] = 1;
		this.levelArray[index][8][7] = 1;
		this.levelArray[index][9][7] = 1;
		this.levelArray[index][10][2] = 1;
		this.levelArray[index][11][2] = 1;
		this.levelArray[index][12][6] = 1;
		this.levelArray[index][12][7] = 1;
		this.levelArray[index][13][1] = 1;
		this.levelArray[index][13][5] = 1;
		this.levelArray[index][13][6] = 1;
		this.levelArray[index][13][7] = 1;
		this.levelWayPointsEnemy1[index].fillArray(this.levelArray[index]);
		this.level = new CLevel[this.levels];
		this.level[0] = new CLevel(this.levelWayPointsEnemy1[0],
				this.levelArray[0], "level01.png", 20, "swampDefenceLevel1");
		if (!this.isOSUI) {
			this.level[0].locked = false;
		}
		this.level[1] = new CLevel(this.levelWayPointsEnemy1[1],
				this.levelArray[1], "level02.png", 30, "swampDefenceLevel2");
		if (this.isOSUI) {
			this.level[1].locked = false;
		}
		this.level[2] = new CLevel(this.levelWayPointsEnemy1[2],
				this.levelArray[2], "level03.png", 30, "swampDefenceLevel3");
		this.level[3] = new CLevel(this.levelWayPointsEnemy1[3],
				this.levelArray[3], "level04.png", 30, "swampDefenceLevel4");
		this.level[4] = new CLevel(this.levelWayPointsEnemy1[4],
				this.levelArray[4], "level05.png", 40, "swampDefenceLevel5");
		this.level[5] = new CLevel(this.levelWayPointsEnemy1[5],
				this.levelArray[5], "level06.png", 20, "swampDefenceLevel6");
		this.level[6] = new CLevel(this.levelWayPointsEnemy1[6],
				this.levelArray[6], "level07.png", 20, "swampDefenceLevel7");
		this.level[7] = new CLevel(this.levelWayPointsEnemy1[7],
				this.levelArray[7], "level08.png", 0x23, "swampDefenceLevel8");
		this.level[8] = new CLevel(this.levelWayPointsEnemy1[8],
				this.levelArray[8], "level09.png", 50, "swampDefenceLevel9");
		this.level[9] = new CLevel(this.levelWayPointsEnemy1[9],
				this.levelArray[9], "level10.png", 30, "swampDefenceLevel10");
		this.level[10] = new CLevel(this.levelWayPointsEnemy1[10],
				this.levelArray[10], "level11.png", 30, "swampDefenceLevel11");
		this.level[11] = new CLevel(this.levelWayPointsEnemy1[11],
				this.levelArray[11], "level12.png", 0x19, "swampDefenceLevel12");
		this.level[12] = new CLevel(this.levelWayPointsEnemy1[12],
				this.levelArray[12], "level13.png", 0x19, "swampDefenceLevel13");
		this.level[13] = new CLevel(this.levelWayPointsEnemy1[13],
				this.levelArray[13], "level14.png", 0x23, "swampDefenceLevel14");
		this.level[14] = new CLevel(this.levelWayPointsEnemy1[14],
				this.levelArray[14], "level15.png", 40, "swampDefenceLevel15");
		this.level[15] = new CLevel(this.levelWayPointsEnemy1[15],
				this.levelArray[15], "level16.png", 30, "swampDefenceLevel16");
		this.level[0x10] = new CLevel(this.levelWayPointsEnemy1[0x10],
				this.levelArray[0x10], "level17.png", 30, "swampDefenceLevel17");
		this.level[0x11] = new CLevel(this.levelWayPointsEnemy1[0x11],
				this.levelArray[0x11], "level18.png", 30, "swampDefenceLevel18");
		this.level[0x12] = new CLevel(this.levelWayPointsEnemy1[0x12],
				this.levelArray[0x12], "level19.png", 30, "swampDefenceLevel19");
		this.currentLevel = 0;
		if (this.isOSUI) {
			this.currentLevel = 1;
		}
		this.gameLoopScreen.initGameLoop(this.level[this.currentLevel]);
		this.allLoaded = true;
	}

	public final void loadConfig() {

	}

	@Override
	public void loadContent() {
		this.random = new CRandom();
		this.splashScreen = new CSplashScreen(this);
		this.splashScreen.LoadContent();
		this.switchGameMode(EGMODE.GMODE_SPLASHSCREEN);

	}

	public final void loadGameState() {
		switchGameMode(EGMODE.GMODE_MENU);
	}

	public final void playTitleSong() {
		if (!this.noSound) {

		}
	}

	public final void saveAll() {
		if (this.allLoaded) {
			if (this.GameMode == EGMODE.GMODE_GAME) {
				if (this.gameLoopScreen.wave > this.level[this.currentLevel].maxWave) {
					this.level[this.currentLevel].maxWave = this.gameLoopScreen.wave;
				}
				this.gameLoopScreen
						.saveGameWave(this.level[this.currentLevel].filename);
				this.gameLoopScreen.pause = true;
			}
			this.saveGameState();
			this.statistics.saveStatistics();
		}
	}

	public final void saveConfig() {

	}

	public final void saveGameState() {

	}

	public final void stopTitleSong() {

	}

	public final void switchGameMode(EGMODE newGameMode) {
		if (newGameMode != this.GameMode) {
			this.globalScreenTimer = 0f;
			this.defaultSceneColorFadeOut.setColor(0xff, 0xff, 0xff, 0xff);
		}

		switch (newGameMode) {
		case GMODE_GAME:
			this.gameLoopScreen.initGameLoop(this.level[this.currentLevel]);
			break;

		case GMODE_LEVELCHOOSER:
			this.levelChooserScreen.init();
			break;

		case GMODE_MENU:
			this.menuScreen.reset();
			break;

		case GMODE_HOWTOPLAY:
			this.helpScreen.reset();
			break;

		case GMODE_SPLASHSCREEN:

			LoadCnt();

			break;
		default:
			break;
		}
		this.lastGameMode = this.GameMode;
		this.GameMode = newGameMode;
	}

	@Override
	public void unloadContent() {
	}

	@Override
	public void update(GameTime gameTime) {
		if (!isOnLoadComplete()) {
			return;
		}
		float time = gameTime.getElapsedGameTime();

		float num2 = (gameTime.getMilliseconds()) / 2f;
		if (this.globalScreenTimer < 10f) {
			this.globalScreenTimer += time;
		}
		int a = this.defaultSceneColorFadeOut.getAlpha();
		a -= num2;
		if (a >= 255) {
			this.defaultSceneColorFadeOut.setColor(
					defaultSceneColorFadeOut.getRed(),
					defaultSceneColorFadeOut.getGreen(),
					defaultSceneColorFadeOut.getAlpha(), 0xff);
		} else if (a <= 0) {
			this.defaultSceneColorFadeOut.setColor(
					defaultSceneColorFadeOut.getRed(),
					defaultSceneColorFadeOut.getGreen(),
					defaultSceneColorFadeOut.getAlpha(), 0);
		} else {
			this.defaultSceneColorFadeOut.setColor(
					defaultSceneColorFadeOut.getRed(),
					defaultSceneColorFadeOut.getGreen(),
					defaultSceneColorFadeOut.getAlpha(), a);
		}
		if (this.allLoaded && !this.gameDataLoaded) {
			this.statistics.loadStatistics();
			this.loadGameState();
			if (this.GameMode == EGMODE.GMODE_GAME) {
				this.gameLoopScreen.reset();
				this.gameLoopScreen
						.loadGameWave(this.level[this.currentLevel].filename);
			}
			this.gameDataLoaded = true;
		}

		switch (this.GameMode) {
		case GMODE_GAME:
			this.gameLoopScreen.update(time);
			break;

		case GMODE_LEVELCHOOSER:
			this.levelChooserScreen.update(time);
			break;

		case GMODE_STATISTICS:
			this.statistics.update(time);
			break;

		case GMODE_COMERCIAL:

			break;

		case GMODE_MENU:
			this.menuScreen.update(time);
			break;

		case GMODE_HOWTOPLAY:
			this.helpScreen.update(time);
			break;

		case GMODE_SPLASHSCREEN:
			this.splashScreen.update(time);
			break;
		default:
			break;
		}
		this.previouseToucheState = this.currentToucheState;
		this.currentToucheState = SysInputFactory.getTouchState();
	}

	public enum EGMODE {
		GMODE_COMERCIAL(5), GMODE_END(10), GMODE_GAME(2), GMODE_GAMEOVER(7), GMODE_HOWTOPLAY(
				8), GMODE_LEVELCHOOSER(3), GMODE_MENU(6), GMODE_SPLASHSCREEN(9), GMODE_START(
				1), GMODE_STATISTICS(4);

		private int intValue;
		private static java.util.HashMap<Integer, EGMODE> mappings;

		private static java.util.HashMap<Integer, EGMODE> getMappings() {
			if (mappings == null) {
				synchronized (EGMODE.class) {
					if (mappings == null) {
						mappings = new java.util.HashMap<Integer, EGMODE>();
					}
				}
			}
			return mappings;
		}

		private EGMODE(int value) {
			intValue = value;
			EGMODE.getMappings().put(value, this);
		}

		public int getValue() {
			return intValue;
		}

		public static EGMODE forValue(int value) {
			return getMappings().get(value);
		}
	}

	@Override
	public void pressed(GameTouch e) {

	}

	@Override
	public void released(GameTouch e) {

	}

	@Override
	public void move(GameTouch e) {

	}

	@Override
	public void drag(GameTouch e) {

	}

	private ActionKey KeyValue = new ActionKey(
			ActionKey.DETECT_INITIAL_PRESS_ONLY);

	@Override
	public void pressed(GameKey e) {
		if (e.getKeyCode() == SysKey.BACK) {
			KeyValue.press();
		}
	}

	@Override
	public void released(GameKey e) {
		if (e.getKeyCode() == SysKey.BACK) {
			KeyValue.release();
		}
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void resume() {

	}

	@Override
	public void pause() {

	}

}