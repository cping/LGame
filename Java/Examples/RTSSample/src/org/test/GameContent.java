package org.test;

import loon.action.sprite.painting.GameComponent;
import loon.core.RefManager;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.TextureUtils;
import loon.core.resource.LPKResource;

public class GameContent {
	public LTexture[] armyOverlay = new LTexture[3];
	public LTexture background, blank;

	public LTexture[] bullet = new LTexture[3];
	public LTexture[] castle = new LTexture[2];
	public Vector2f castleOrigin;

	public LTexture[] die = new LTexture[2];
	public Sound dieSound;
	public LFont gameFont;
	public int gameFontSize = 0x44;
	public LTexture gameOver;
	public LTexture gradient;
	public LTexture healthBar;
	public LTexture[] idle = new LTexture[2];
	public LTexture levelUp;
	public LTexture mainMenu;

	public LTexture menuBackground;
	public LTexture mouseOver;
	public Vector2f mouseOverOrigin;
	public Sound[] noise = new Sound[3];
	public LTexture options;
	public LTexture pathArrow;
	public LTexture pathCross;
	public java.util.Random random = new java.util.Random();
	public LTexture retry;
	public LTexture[] tutorial = new LTexture[3];
	public LTexture[] walk = new LTexture[2];
	public LTexture[] weapon = new LTexture[3];

	public RefManager refs = new RefManager();

	public LTexture load(String name) {

		// PS:0.3.3版开始打包采用PackageTools(Java).jar这个工具类，位于Tools文件夹下。

		// 让LPKResource支持压缩包读取时缓存数据（可以加快同一个压缩包的资源读取速度，缺点是将常驻内存，数据量较大时不建议缓存
		// ，调取LPKResource.FreeCache()可释放缓存）

        LPKResource.CACHE = true;

		LTexture texture = LPKResource.openTexture("assets/assets.pak", name
				+ ".png");

		// 也可以写作下法（采取此方法时，内部会调用上述操作，本质一样。好处是可以随处使用，不必显性调取LPKResource。）
		// LTexture texture = LTextures.loadTexture("assets/assets.pak->" + name
		// + ".png");

		refs.add(texture);
		return texture;
	}

	public GameContent(GameComponent screenManager) {
		this.blank = TextureUtils.createTexture(1, 1, LColor.black);
		this.menuBackground = load("menuBackground");
		this.mainMenu = load("mainMenu");
		this.options = load("options");
		this.background = load("background");
		for (int i = 0; i < this.walk.length; i++) {
			this.walk[i] = load("" + (Shape.forValue(i)) + "Walk");
			this.idle[i] = load("" + (Shape.forValue(i)) + "Idle");
			this.die[i] = load("" + (Shape.forValue(i)) + "Die");
		}
		this.mouseOver = load("mouseOver");
		this.mouseOverOrigin = (new Vector2f(this.mouseOver.getWidth(),
				this.mouseOver.getHeight()).div());
		for (int j = 0; j < this.castle.length; j++) {
			this.castle[j] = load("" + (Shape.forValue(j)) + "Castle");
		}
		this.castleOrigin = new Vector2f(
				(float) (this.castle[0].getWidth() / 2),
				(float) this.castle[0].getHeight());
		this.pathArrow = load("pathArrow");
		this.pathCross = load("pathCross");
		this.healthBar = load("healthBar").scale(20, 4);
		for (int k = 0; k < 3; k++) {
			this.weapon[k] = load("" + (RoleRank.forValue(k)) + "Weapon");
			this.bullet[k] = load("" + (RoleRank.forValue(k)) + "Bullet");
		}
		for (int m = 0; m < this.tutorial.length; m++) {
			this.tutorial[m] = load("tutotrial" + m);
		}
		this.levelUp = load("levelUp");
		this.retry = load("retry");
		this.gameOver = load("gameOver");

		this.gameFont = LFont.getFont(25);
		for (int n = 0; n < this.noise.length; n++) {
			this.noise[n] = new Sound();
		}
		this.dieSound = new Sound();
		if (GameMain.Settings.MusicEnabled) {
			this.PlayMusic();
		}
		if (GameMain.Settings.SoundEnabled) {
			Sound.MasterVolume = 1f;
		} else {
			Sound.MasterVolume = 0f;
		}
		screenManager.getGame().getGameTime().resetElapsedTime();

		LPKResource.FreeCache();
	}

	public final void PlayMusic() {

	}

	public final void UnloadContent() {
		refs.dispose();
	}
}