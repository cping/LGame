package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.action.sprite.painting.DrawableScreen;
import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.input.LInputFactory.Key;
import loon.core.input.LInputFactory.Touch;
import loon.core.input.LKey;
import loon.core.input.LTouch;
import loon.core.timer.GameTime;
import loon.utils.MathUtils;

public class KingdomDefence extends DrawableScreen {
	private LTexture arrow;
	private float arrowRotation;
	private float arrowScale;
	private ParallaxingBackground bgLayer0;
	private ParallaxingBackground bgLayer1;
	private ParallaxingBackground bgLayer2;
	private Castle castle;
	private int castleLevel;
	private LevelChooseMenu chooseLevelMenu;

	private GameState currentState;
	private java.util.ArrayList<Enemy> enemies;
	private LTexture enemyExplosionTex_lv1;
	private LTexture enemyExplosionTex_lv2;
	private LTexture enemyExplosionTex_lv3;
	private LTexture enemyExplosionTex_lv4;
	private LTexture enemyExplosionTex_lv5;
	private java.util.ArrayList<Projectile> enemyProjectiles;
	private LTexture enemyProjectileTex_lv1;
	private LTexture enemyProjectileTex_lv2;
	private LTexture enemyProjectileTex_lv3;
	private LTexture enemyProjectileTex_lv4;
	private LTexture enemyProjectileTex_lv5;
	private LTexture enemyTexture_lv1;
	private LTexture enemyTexture_lv2;
	private LTexture enemyTexture_lv3;
	private LTexture enemyTexture_lv4;
	private LTexture enemyTexture_lv5;

	private java.util.ArrayList<Animation> explosions;

	private float fireTime = 0f;

	private LFont font;

	private HelpMenu helpMenu;
	public boolean isDragging;
	private int killedEnemy;
	private ParallaxingBackground landLayer;
	private float levelStartTime = 0f;
	private LoseMenu loseMenu;
	private LTexture mainBackground;
	private MainMenu mainMenu;
	private final float maxDragDelta = new Vector2f(480f, 800f).len();
	private PauseMenu pauseMenu;
	private Player player;
	private int playerLevel;
	private java.util.ArrayList<Projectile> playerProjectiles;
	private float previousFireTime = 0f;

	private float previousSpawnTime = 0f;
	private float previousSpawnTime1 = 0f;
	private float previousSpawnTime2 = 0f;

	private LTexture projectileHitLand;
	private LTexture projectileTexture;

	private int StageLevel;

	private Terrain terrain;
	private VictoryMenu victoryMenu;

	private WinMenu winMenu;

	public KingdomDefence() {

	}

	private void AddEnemy(int enemylevel) {
		Animation animation = new Animation();
		switch (enemylevel) {
		case 1:
			animation.Initialize(this.enemyTexture_lv1, Vector2f.Zero, 0x29,
					60, 8, 60, LColor.white, 1f, true);
			break;

		case 2:
			animation.Initialize(this.enemyTexture_lv2, Vector2f.Zero, 0x3d,
					70, 4, 60, LColor.white, 1f, true);
			break;

		case 3:
			animation.Initialize(this.enemyTexture_lv3, Vector2f.Zero, 110,
					0x63, 4, 60, LColor.white, 1f, true);
			break;

		case 4:
			animation.Initialize(this.enemyTexture_lv4, Vector2f.Zero, 0x8f,
					130, 9, 60, LColor.white, 1f, true);
			break;

		case 5:
			animation.Initialize(this.enemyTexture_lv5, Vector2f.Zero, 0xc3,
					0xc4, 4, 120, LColor.white, 1f, true);
			break;
		}
		Vector2f position = new Vector2f(
				LSystem.screenRect.width + (animation.FrameWidth / 2),
				this.terrain
						.getHeight(
								(float) (LSystem.screenRect.width + (animation.FrameWidth / 2)),
								this.StageLevel)
						- (animation.FrameHeight / 2));
		Enemy item = new Enemy();
		item.Initialize(animation, position, enemylevel);
		this.enemies.add(item);
	}

	private void AddExplosion(Vector2f position) {
		Animation item = new Animation();
		item.Initialize(this.projectileHitLand, position, 0x2d, 40, 15, 0x2d,
				LColor.white, 1f, false);
		this.explosions.add(item);
	}

	private void AddExplosion(Vector2f position, int enemylevel) {
		Animation item = new Animation();
		LTexture texture = null;
		item.Initialize(texture, position, 0x86, 0x86, 12, 0x2d, LColor.white,
				1f, false);
		switch (enemylevel) {
		case 1:

			item.Initialize(this.enemyExplosionTex_lv1, position, 0x86, 0x86,
					12, 0x2d, LColor.white, 1f, false);
			break;

		case 2:

			item.Initialize(this.enemyExplosionTex_lv2, position, 0x86, 0x86,
					12, 0x2d, LColor.white, 1f, false);
			break;

		case 3:

			item.Initialize(this.enemyExplosionTex_lv3, position, 0x86, 0x86,
					12, 0x2d, LColor.white, 1f, false);
			break;

		case 4:

			item.Initialize(this.enemyExplosionTex_lv4, position, 0x86, 0x86,
					12, 0x2d, LColor.white, 1f, false);
			break;

		case 5:

			item.Initialize(this.enemyExplosionTex_lv5, position, 0x86, 0x86,
					12, 0x2d, LColor.white, 1f, false);
			break;
		}
		this.explosions.add(item);
	}

	private void AddProjectile(Vector2f position, Vector2f delta) {
		Projectile item = new Projectile();
		if (this.playerLevel <= 2) {
			item.Initialize(this.projectileTexture, position, this.playerLevel,
					delta);
		} else if (this.playerLevel >= 3) {
			item.Initialize(this.projectileTexture, position, this.playerLevel,
					delta);
			Projectile projectile2 = new Projectile();
			projectile2.Initialize(this.projectileTexture, position,
					this.playerLevel, delta.add(20f, 50f));
			this.playerProjectiles.add(projectile2);
			Projectile projectile3 = new Projectile();
			projectile3.Initialize(this.projectileTexture, position,
					this.playerLevel, delta.add(-20f, -50f));
			this.playerProjectiles.add(projectile3);
		}

		this.playerProjectiles.add(item);
	}

	private void AddProjectile(Vector2f position, Vector2f delta, int enemylevel) {
		Projectile item = new Projectile();
		LTexture texture = null;
		switch (enemylevel) {
		case 1:
			texture = this.enemyProjectileTex_lv1;

			break;

		case 2:
			texture = this.enemyProjectileTex_lv2;

			break;

		case 3:
			texture = this.enemyProjectileTex_lv3;

			break;

		case 4:
			texture = this.enemyProjectileTex_lv4;
			position.y += 50f;

			break;

		case 5:
			texture = this.enemyProjectileTex_lv5;

			break;
		}
		item.Initialize(texture, position, delta, enemylevel);
		this.enemyProjectiles.add(item);
	}

	private void changeCastleLevel(int level) {
		String assetName = null;
		int frameWidth = 0;
		int frameHeight = 0;
		switch (level) {
		case 1:
			this.castleLevel = 1;
			assetName = "castle_1";
			frameWidth = 0x80;
			frameHeight = 240;
			break;

		case 2:
			this.castleLevel = 2;
			assetName = "castle_2";
			frameWidth = 0x7f;
			frameHeight = 190;
			break;

		case 3:
			this.castleLevel = 3;
			assetName = "castle_3";
			frameWidth = 0xd3;
			frameHeight = 220;
			break;

		case 4:
			this.castleLevel = 4;
			assetName = "castle_4";
			frameWidth = 0xd4;
			frameHeight = 0xfd;
			break;

		case 5:
			this.castleLevel = 5;
			assetName = "castle_5";
			frameWidth = 240;
			frameHeight = 0x11a;
			break;
		}
		Animation animation = new Animation();
		LTexture texture = LTextures
				.loadTexture("assets/" + assetName + ".png");
		animation.Initialize(texture, Vector2f.Zero, frameWidth, frameHeight,
				1, 1, LColor.white, 1f, true);
		Vector2f position = new Vector2f((float) ((0 + 110) + 20),
				(float) ((0 + 350) - (frameHeight / 2)));
		this.castle.Initialize(animation, position, this.castleLevel);
	}

	private void changePlayerLevel(int level) {
		String assetName = null;
		String str2 = null;
		int frameWidth = 0;
		int frameHeight = 0;
		int frameCount = 0;
		switch (level) {
		case 1:
			this.playerLevel = 1;
			assetName = "player1";
			frameWidth = 0x33;
			frameHeight = 0x2f;
			frameCount = 8;
			str2 = "rock_ammo";
			this.fireTime = 0.5f;
			break;

		case 2:
			this.playerLevel = 2;
			assetName = "player2";
			frameWidth = 0x23;
			frameHeight = 0x2f;
			frameCount = 4;
			str2 = "arrow_ammo_p";
			this.fireTime = 0.30000001192092896f;
			break;

		case 3:
			this.playerLevel = 3;
			assetName = "player3";
			frameWidth = 50;
			frameHeight = 0x2b;
			frameCount = 1;
			str2 = "mortar_ammo";
			this.fireTime = 2f;
			break;

		case 4:
			this.playerLevel = 4;
			assetName = "player3";
			frameWidth = 50;
			frameHeight = 0x2b;
			frameCount = 1;
			str2 = "mortar_ammo";
			this.fireTime = 1f;
			break;
		}
		Animation animation = new Animation();
		LTexture texture = LTextures
				.loadTexture("assets/" + assetName + ".png");
		animation.Initialize(texture, Vector2f.Zero, frameWidth, frameHeight,
				frameCount, 30, LColor.white, 1f, true);
		Vector2f position = new Vector2f(0, 0 + (LSystem.screenRect.height / 2));
		this.player.Initialize(animation, position);
		this.projectileTexture = LTextures.loadTexture("assets/" + str2
				+ ".png");

	}

	private void changeStage(int level) {
		String texturePath = null;
		String str2 = null;
		String str3 = null;
		String str4 = null;
		String assetName = null;
		switch (level) {
		case 1:
			this.StageLevel = 1;
			this.levelStartTime = 0f;
			this.previousSpawnTime = 0f;
			this.previousSpawnTime1 = 0f;
			this.previousSpawnTime2 = 0f;
			this.killedEnemy = 0x18;
			texturePath = "landlayer1";
			str2 = "bglayer0";
			str3 = "bglayer1";
			str4 = "bglayer2";
			assetName = "mainbackground1";
			break;

		case 2:
			this.StageLevel = 2;
			this.levelStartTime = 0f;
			this.previousSpawnTime = 0f;
			this.previousSpawnTime1 = 0f;
			this.previousSpawnTime2 = 0f;
			this.killedEnemy = 0x27;
			texturePath = "landlayer2";
			str2 = "bglayer0_2";
			str3 = "bglayer1";
			str4 = "bglayer2";
			assetName = "mainbackground2";
			break;

		case 3:
			this.StageLevel = 3;
			this.levelStartTime = 0f;
			this.previousSpawnTime = 0f;
			this.previousSpawnTime1 = 0f;
			this.previousSpawnTime2 = 0f;
			this.killedEnemy = 0x47;
			texturePath = "landlayer3";
			str2 = "bglayer0_3";
			str3 = "bglayer1";
			str4 = "bglayer1";
			assetName = "mainbackground3";
			break;

		case 4:
			this.StageLevel = 4;
			this.levelStartTime = 0f;
			this.previousSpawnTime = 0f;
			this.previousSpawnTime1 = 0f;
			this.previousSpawnTime2 = 0f;
			this.killedEnemy = 40;
			texturePath = "landlayer4";
			str2 = "bglayer0_4";
			str3 = "bglayer1";
			str4 = "bglayer2";
			assetName = "mainbackground4";
			break;

		case 5:
			this.StageLevel = 5;
			this.levelStartTime = 0f;
			this.previousSpawnTime = 0f;
			this.previousSpawnTime1 = 0f;
			this.previousSpawnTime2 = 0f;
			this.killedEnemy = 0x36;
			texturePath = "landlayer5";
			str2 = "bglayer0_5";
			str3 = "bglayer1";
			str4 = "bglayer2";
			assetName = "mainbackground5";
			break;
		}
		this.landLayer.Initialize("assets/" + texturePath + ".png",
				LSystem.screenRect.width, 0);
		this.bgLayer0.Initialize("assets/" + str2 + ".png",
				LSystem.screenRect.width, 0);
		this.bgLayer1.Initialize("assets/" + str3 + ".png",
				LSystem.screenRect.width, -1);
		this.bgLayer2.Initialize("assets/" + str4 + ".png",
				LSystem.screenRect.width, -2);
		this.mainBackground = LTextures.loadTexture("assets/" + assetName
				+ ".png");
		this.arrow = LTextures.loadTexture("assets/arrow.png");
	}

	public final void clearStage() {
		int num;
		for (num = this.enemies.size() - 1; num >= 0; num--) {
			this.enemies.remove(num);
		}
		for (num = this.playerProjectiles.size() - 1; num >= 0; num--) {
			this.playerProjectiles.remove(num);
		}
		for (num = this.enemyProjectiles.size() - 1; num >= 0; num--) {
			this.enemyProjectiles.remove(num);
		}
		for (num = this.explosions.size() - 1; num >= 0; num--) {
			this.explosions.remove(num);
		}
	}

	public void draw(SpriteBatch batch) {

		switch (this.currentState) {
		case inVictory:
			this.victoryMenu.Draw(batch);
			break;

		case inHelp:
			this.helpMenu.Draw(batch);
			break;

		case inMainMenu:
			this.mainMenu.Draw(batch);
			break;

		case inGame: {
			batch.draw(this.mainBackground, Vector2f.Zero, LColor.white);
			this.bgLayer1.Draw(batch);
			this.bgLayer2.Draw(batch);
			this.bgLayer0.Draw(batch);
			int num = 0;
			while (num < this.enemies.size()) {
				this.enemies.get(num).Draw(batch);
				num++;
			}
			batch.drawString(this.font, "SCORE: " + this.player.Score,
					new Vector2f((float) (0 + 10), (float) 0), LColor.white);
			batch.drawString(this.font, "DURABILITY: " + this.castle.Health,
					new Vector2f((float) (0 + 10), (float) (0 + 40)),
					LColor.white);
			this.player.Draw(batch);
			this.castle.Draw(batch);
			for (num = 0; num < this.playerProjectiles.size(); num++) {
				this.playerProjectiles.get(num).Draw(batch);
			}
			for (num = 0; num < this.enemyProjectiles.size(); num++) {
				this.enemyProjectiles.get(num).Draw(batch);
			}
			for (num = 0; num < this.explosions.size(); num++) {
				this.explosions.get(num).Draw(batch);
			}
			this.landLayer.Draw(batch);
			if (this.isDragging) {
				this.DrawDragArrow(batch, this.arrowScale, this.arrowRotation);
			}
			break;
		}
		case inPause:
			batch.draw(this.mainBackground, Vector2f.Zero, LColor.white);
			this.bgLayer1.Draw(batch);
			this.bgLayer2.Draw(batch);
			this.bgLayer0.Draw(batch);
			this.castle.Draw(batch);
			this.landLayer.Draw(batch);
			this.pauseMenu.Draw(batch);
			break;

		case inChooseLevel:
			this.chooseLevelMenu.draw(batch);
			break;

		case inLose:
			batch.draw(this.mainBackground, Vector2f.Zero, LColor.white);
			this.bgLayer1.Draw(batch);
			this.bgLayer2.Draw(batch);
			this.bgLayer0.Draw(batch);
			this.landLayer.Draw(batch);
			this.loseMenu.Draw(batch);
			break;

		case inWin:
			batch.draw(this.mainBackground, Vector2f.Zero, LColor.white);
			this.bgLayer1.Draw(batch);
			this.bgLayer2.Draw(batch);
			this.bgLayer0.Draw(batch);
			this.castle.Draw(batch);
			this.landLayer.Draw(batch);
			this.winMenu.Draw(batch);
			batch.drawString(this.font, "SCORE: " + this.player.Score,
					new Vector2f((float) (0 + 10), (float) 0), LColor.white);
			break;
		}

	}

	private Vector2f scale = new Vector2f();

	public final void DrawDragArrow(SpriteBatch batch, float arrowScale,
			float arrowRotation) {
		scale.set(arrowScale, 0.1f);
		batch.draw(this.arrow, this.player.Position.add(0f, -40f), null,
				LColor.white, MathUtils.toDegrees(arrowRotation),
				Vector2f.Zero, scale, SpriteEffects.None);
	}

	@Override
	public void loadContent() {

		this.StageLevel = 1;
		this.levelStartTime = 0f;
		this.killedEnemy = 10;
		this.mainMenu = new MainMenu();
		this.pauseMenu = new PauseMenu();
		this.chooseLevelMenu = new LevelChooseMenu();
		this.winMenu = new WinMenu();
		this.loseMenu = new LoseMenu();
		this.victoryMenu = new VictoryMenu();
		this.helpMenu = new HelpMenu();

		this.currentState = GameState.inMainMenu;
		this.player = new Player();
		this.castle = new Castle();
		this.playerLevel = 1;
		this.castleLevel = 1;
		this.landLayer = new ParallaxingBackground();
		this.bgLayer0 = new ParallaxingBackground();
		this.bgLayer1 = new ParallaxingBackground();
		this.bgLayer2 = new ParallaxingBackground();
		this.terrain = new Terrain();
		this.enemies = new java.util.ArrayList<Enemy>();
		this.previousSpawnTime = 0f;

		this.playerProjectiles = new java.util.ArrayList<Projectile>();
		this.enemyProjectiles = new java.util.ArrayList<Projectile>();
		this.fireTime = 0.15000000596046448f;
		this.explosions = new java.util.ArrayList<Animation>();

		this.landLayer.Initialize("assets/landlayer1.png",
				LSystem.screenRect.width, 0);
		this.bgLayer0.Initialize("assets/bglayer0.png",
				LSystem.screenRect.width, 0);
		this.bgLayer1.Initialize("assets/bglayer1.png",
				LSystem.screenRect.width, -1);
		this.bgLayer2.Initialize("assets/bglayer2.png",
				LSystem.screenRect.width, -2);
		this.enemyTexture_lv1 = LTextures.loadTexture("assets/enemy_lv1.png");
		this.enemyTexture_lv2 = LTextures.loadTexture("assets/enemy_lv2.png");
		this.enemyTexture_lv3 = LTextures.loadTexture("assets/enemy_lv3.png");
		this.enemyTexture_lv4 = LTextures.loadTexture("assets/enemy_lv4.png");
		this.enemyTexture_lv5 = LTextures.loadTexture("assets/enemy_lv5.png");
		this.projectileTexture = LTextures.loadTexture("assets/rock_ammo.png");
		this.enemyProjectileTex_lv1 = LTextures
				.loadTexture("assets/rock_ammo.png");
		this.enemyProjectileTex_lv2 = LTextures
				.loadTexture("assets/arrow_ammo.png");
		this.enemyProjectileTex_lv3 = LTextures
				.loadTexture("assets/thorn_ammo.png");
		this.enemyProjectileTex_lv4 = LTextures
				.loadTexture("assets/fire_ammo.png");
		this.enemyProjectileTex_lv5 = LTextures
				.loadTexture("assets/mortar2_ammo.png");
		this.arrow = LTextures.loadTexture("assets/arrow.png");
		this.projectileHitLand = LTextures.loadTexture("assets/hitland.png");

		this.enemyExplosionTex_lv1 = LTextures
				.loadTexture("assets/explosion.png");
		this.enemyExplosionTex_lv2 = this.enemyExplosionTex_lv1;
		this.enemyExplosionTex_lv3 = this.enemyExplosionTex_lv1;
		this.enemyExplosionTex_lv4 = this.enemyExplosionTex_lv1;
		this.enemyExplosionTex_lv5 = this.enemyExplosionTex_lv1;

		this.font = LFont.getFont("黑体", 1, 28);
		this.mainMenu.Initialize();
		this.pauseMenu.Initialize();
		this.chooseLevelMenu.Initialize();
		this.winMenu.Initialize();
		this.loseMenu.Initialize();
		this.victoryMenu.Initialize();
		this.helpMenu.Initialize();
	}

	@Override
	public void unloadContent() {
	}

	@Override
	public void update(GameTime gameTime) {
		if (Key.isKeyPressed(Key.BACK)) {
			switch (this.currentState) {
			case inVictory:
				this.currentState = GameState.inMainMenu;
				break;

			case inHelp:
				this.currentState = GameState.inMainMenu;
				break;

			case inMainMenu:
				LSystem.exit();
				break;

			case inGame:
				this.currentState = GameState.inPause;
				break;

			case inPause:
				this.currentState = GameState.inGame;
				break;

			case inChooseLevel:
				this.currentState = GameState.inMainMenu;
				break;

			case inLose:
				this.currentState = GameState.inMainMenu;
				break;

			case inWin:
				this.currentState = GameState.inMainMenu;
				break;
			}
		}

		switch (this.currentState) {
		case inVictory:
			this.UpdateVictoryMenu();
			break;

		case inHelp:
			this.UpdateHelpMenu();
			break;

		case inMainMenu:
			this.UpdateMainMenu();
			break;

		case inGame:
			this.levelStartTime += gameTime.getElapsedGameTime();
			this.UpdatePlayer(gameTime);
			this.player.Update(gameTime);
			this.castle.Update(gameTime);
			this.landLayer.Update();
			this.bgLayer1.Update();
			this.bgLayer2.Update();
			this.UpdateEnemies(gameTime);
			this.UpdateCollision();
			this.UpdateProjectiles(gameTime);
			this.UpdateExplosions(gameTime);
			if (((this.player.Kills == this.killedEnemy) && (this.castle.Health > 0))
					&& (this.explosions.isEmpty())) {
				this.currentState = GameState.inWin;
				this.player.Score += this.castle.Health;
			}
			if (this.castle.Health <= 0) {
				this.castle.Health = 0;
				if (this.explosions.isEmpty()) {
					this.currentState = GameState.inLose;
				}
			}
			break;

		case inPause:
			this.UpdatePauseMenu();
			break;

		case inChooseLevel:
			this.UpdateChooseLevelMenu();
			break;

		case inLose:
			this.UpdateLoseMenu();
			break;

		case inWin:
			this.player.Update(gameTime);
			this.UpdateWinMenu();
			break;
		}
	}

	private void UpdateChooseLevelMenu() {
		if (Touch.isDown()) {
			if (this.chooseLevelMenu.Tap_Level1(Touch.x(), Touch.y())) {
				this.changeStage(1);
				this.changeCastleLevel(1);
				this.changePlayerLevel(1);
				this.currentState = GameState.inGame;
			} else if (this.chooseLevelMenu.Tap_Level2(Touch.x(), Touch.y())) {
				this.changeStage(2);
				this.changeCastleLevel(2);
				this.changePlayerLevel(1);
				this.currentState = GameState.inGame;
			} else if (this.chooseLevelMenu.Tap_Level3(Touch.x(), Touch.y())) {
				this.changeStage(3);
				this.changeCastleLevel(3);
				this.changePlayerLevel(2);
				this.currentState = GameState.inGame;
			} else if (this.chooseLevelMenu.Tap_Level4(Touch.x(), Touch.y())) {
				this.changeStage(4);
				this.changeCastleLevel(4);
				this.changePlayerLevel(3);
				this.currentState = GameState.inGame;
			} else if (this.chooseLevelMenu.Tap_Level5(Touch.x(), Touch.y())) {
				this.changeStage(5);
				this.changeCastleLevel(5);
				this.changePlayerLevel(4);
				this.currentState = GameState.inGame;
			} else if (this.chooseLevelMenu.Tap_MenuEntry_BACK(Touch.x(),
					Touch.y())) {
				this.currentState = GameState.inMainMenu;
			}
		}

	}

	private void UpdateCollision() {
		RectBox rectangle2;
		int num;
		RectBox rectangle = new RectBox(((int) this.castle.Position.x)
				- (this.castle.getWidth() / 2), ((int) this.castle.Position.y)
				- (this.castle.getHeight() / 2), this.castle.getWidth(),
				this.castle.getHeight());
		for (num = 0; num < this.enemies.size(); num++) {
			rectangle2 = new RectBox(((int) this.enemies.get(num).Position.x)
					- (this.enemies.get(num).getWidth() / 2),
					((int) this.enemies.get(num).Position.y)
							- (this.enemies.get(num).getHeight() / 2),
					this.enemies.get(num).getWidth(), this.enemies.get(num)
							.getHeight());
			if (rectangle.intersects(rectangle2)) {
				this.castle.Health -= this.enemies.get(num).Damage;
				this.enemies.get(num).Active = false;
				this.player.Kills++;
				if (this.castle.Health <= 0) {
					this.castle.Active = false;
				}
			}
		}
		rectangle = new RectBox(((int) this.castle.Position.x)
				- (this.castle.getWidth() / 2), ((int) this.castle.Position.y)
				- (this.castle.getHeight() / 2), this.castle.getWidth(),
				this.castle.getHeight());
		for (num = 0; num < this.enemyProjectiles.size(); num++) {
			rectangle2 = new RectBox(
					((int) this.enemyProjectiles.get(num).Position.x)
							- (this.enemyProjectiles.get(num).getWidth() / 2),
					((int) this.enemyProjectiles.get(num).Position.y)
							- (this.enemyProjectiles.get(num).getHeight() / 2),
					this.enemyProjectiles.get(num).getWidth(),
					this.enemyProjectiles.get(num).getHeight());
			if (rectangle.intersects(rectangle2)) {
				this.castle.Health -= this.enemyProjectiles.get(num).Damage;
				this.enemyProjectiles.get(num).Active = false;
				switch (this.enemyProjectiles.get(num).enemyLevel) {
				case 1:

					break;

				case 2:

					break;

				case 3:

					break;

				case 4:

					break;

				case 5:

					break;
				}
			}
			if (rectangle2.Top() >= this.terrain.getHeight(
					(float) rectangle2.Left(), 1)) {
				this.AddExplosion(this.enemyProjectiles.get(num).Position.sub(
						0f, 20f));
				this.enemyProjectiles.get(num).Active = false;
			}
		}
		for (num = 0; num < this.playerProjectiles.size(); num++) {
			rectangle = new RectBox(
					((int) this.playerProjectiles.get(num).Position.x)
							- (this.playerProjectiles.get(num).getWidth() / 2),
					((int) this.playerProjectiles.get(num).Position.y)
							- (this.playerProjectiles.get(num).getHeight() / 2),
					this.playerProjectiles.get(num).getWidth(),
					this.playerProjectiles.get(num).getHeight());
			for (int i = 0; i < this.enemies.size(); i++) {
				rectangle2 = new RectBox(((int) this.enemies.get(i).Position.x)
						- (this.enemies.get(i).getWidth() / 2),
						((int) this.enemies.get(i).Position.y)
								- (this.enemies.get(i).getHeight() / 2),
						this.enemies.get(i).getWidth(), this.enemies.get(i)
								.getHeight());
				if (rectangle.intersects(rectangle2)) {
					Enemy local1 = this.enemies.get(i);
					local1.Health -= this.playerProjectiles.get(num).Damage;
					this.playerProjectiles.get(num).Active = false;
				}
			}
			if (rectangle.Top() >= this.terrain.getHeight(
					(float) rectangle.Left(), this.StageLevel)) {
				this.AddExplosion(this.playerProjectiles.get(num).Position.sub(
						0f, 20f));
				this.playerProjectiles.get(num).Active = false;
			}
		}
	}

	private void UpdateEnemies(GameTime gameTime) {
		switch (this.StageLevel) {
		case 1:
			if ((this.levelStartTime < 20f)
					&& ((this.levelStartTime - this.previousSpawnTime) > 1f)) {
				this.previousSpawnTime = this.levelStartTime;
				this.AddEnemy(1);
			}
			if (((this.levelStartTime > 20f) && (this.levelStartTime < 30f))
					&& ((this.levelStartTime - this.previousSpawnTime1) > 2f)) {
				this.previousSpawnTime1 = this.levelStartTime;
				this.AddEnemy(2);
			}
			break;

		case 2:
			if ((this.levelStartTime < 20f)
					&& ((this.levelStartTime - this.previousSpawnTime) > 1f)) {
				this.previousSpawnTime = this.levelStartTime;
				this.AddEnemy(1);
			}
			if (((this.levelStartTime > 20f) && (this.levelStartTime < 50f))
					&& ((this.levelStartTime - this.previousSpawnTime1) > 1.7999999523162842f)) {
				this.previousSpawnTime1 = this.levelStartTime;
				this.AddEnemy(2);
			}
			if (((this.levelStartTime > 47f) && (this.levelStartTime < 58f))
					&& ((this.levelStartTime - this.previousSpawnTime2) > 4f)) {
				this.previousSpawnTime2 = this.levelStartTime;
				this.AddEnemy(3);
			}
			break;

		case 3:
			if ((this.levelStartTime < 40f)
					&& ((this.levelStartTime - this.previousSpawnTime) > 1.2000000476837158f)) {
				this.previousSpawnTime = this.levelStartTime;
				this.AddEnemy(1);
			}
			if (((this.levelStartTime > 20f) && (this.levelStartTime < 80f))
					&& ((this.levelStartTime - this.previousSpawnTime1) > 1.7999999523162842f)) {
				this.previousSpawnTime1 = this.levelStartTime;
				this.AddEnemy(2);
			}
			if (((this.levelStartTime > 40f) && (this.levelStartTime < 56f))
					&& ((this.levelStartTime - this.previousSpawnTime2) > 4f)) {
				this.previousSpawnTime2 = this.levelStartTime;
				this.AddEnemy(3);
			}
			break;

		case 4:
			if ((this.levelStartTime < 20f)
					&& ((this.levelStartTime - this.previousSpawnTime) > 1.2000000476837158f)) {
				this.previousSpawnTime = this.levelStartTime;
				this.AddEnemy(1);
			}
			if (((this.levelStartTime > 5f) && (this.levelStartTime < 45f))
					&& ((this.levelStartTime - this.previousSpawnTime1) > 2f)) {
				this.previousSpawnTime1 = this.levelStartTime;
				this.AddEnemy(2);
			}
			if (((this.levelStartTime > 25f) && (this.levelStartTime < 56f))
					&& ((this.levelStartTime - this.previousSpawnTime2) > 10f)) {
				this.previousSpawnTime2 = this.levelStartTime;
				this.AddEnemy(4);
			}
			break;

		case 5:
			if ((this.levelStartTime < 56f)
					&& ((this.levelStartTime - this.previousSpawnTime) > 1.2000000476837158f)) {
				this.previousSpawnTime = this.levelStartTime;
				this.AddEnemy(2);
			}
			if (((this.levelStartTime > 5f) && (this.levelStartTime < 45f))
					&& ((this.levelStartTime - this.previousSpawnTime1) > 6f)) {
				this.previousSpawnTime1 = this.levelStartTime;
				this.AddEnemy(3);
			}
			if (((this.levelStartTime > 56f) && (this.levelStartTime < 57f))
					&& ((this.levelStartTime - this.previousSpawnTime2) > 10f)) {
				this.previousSpawnTime2 = this.levelStartTime;
				this.AddEnemy(5);
			}
			break;
		}
		for (int i = this.enemies.size() - 1; i >= 0; i--) {
			if (this.enemies.get(i).enemyLevel != 4) {
				this.enemies.get(i).Update(
						gameTime,
						this.terrain.getHeight(this.enemies.get(i).Position.x,
								this.StageLevel));
			} else {
				this.enemies.get(i).Update(gameTime, 180f);
			}
			if ((gameTime.getTotalGameTime() - this.enemies.get(i).previousFireTime) > this.enemies
					.get(i).fireTime * 2) {
				this.enemies.get(i).previousFireTime = gameTime
						.getTotalGameTime();
				this.AddProjectile(
						this.enemies.get(i).Position.sub((float) ((this.enemies
								.get(i).getWidth() / 2) - 5), 10f),
						this.enemies.get(i).getProjectileVector(), this.enemies
								.get(i).enemyLevel);
				if (this.enemies.get(i).enemyLevel == 5) {
					this.AddProjectile(this.enemies.get(i).Position.sub(
							(float) ((this.enemies.get(i).getWidth() / 2) - 5),
							10f), this.enemies.get(i).getProjectileVector()
							.add(50f, 30f), this.enemies.get(i).enemyLevel);
					this.AddProjectile(this.enemies.get(i).Position.sub(
							(float) ((this.enemies.get(i).getWidth() / 2) - 5),
							10f), this.enemies.get(i).getProjectileVector()
							.add(-50f, -30f), this.enemies.get(i).enemyLevel);
				}
			}
			if (!this.enemies.get(i).Active) {
				if (this.enemies.get(i).Health <= 0) {
					this.player.Kills++;
					this.player.Score += this.enemies.get(i).Value;
				}
				this.AddExplosion(this.enemies.get(i).Position,
						this.enemies.get(i).enemyLevel);
				this.enemies.remove(i);
			}
		}
	}

	private void UpdateExplosions(GameTime gameTime) {
		for (int i = this.explosions.size() - 1; i >= 0; i--) {
			this.explosions.get(i).Update(gameTime);
			if (!this.explosions.get(i).Active) {
				this.explosions.remove(i);
			}
		}
	}

	private void UpdateHelpMenu() {
		if (Touch.isDown()
				&& this.helpMenu.Tap_MenuEntry_BACK(Touch.x(), Touch.y())) {
			this.clearStage();
			this.currentState = GameState.inMainMenu;
		}

	}

	private void UpdateLoseMenu() {

		if (Touch.isDown()) {
			if (this.loseMenu.Tap_MenuEntry_RETRY(Touch.x(), Touch.y())) {
				this.clearStage();
				this.changeStage(this.StageLevel);
				this.changeCastleLevel(this.castleLevel);
				this.changePlayerLevel(this.playerLevel);
				this.currentState = GameState.inGame;
			} else if (this.loseMenu.Tap_MenuEntry_QUIT(Touch.x(), Touch.y())) {
				this.clearStage();
				this.currentState = GameState.inMainMenu;
			}
		}

	}

	private void UpdateMainMenu() {

		if (Touch.isDown()) {
			if (this.mainMenu.Tap_MenuEntry_PLAY(Touch.x(), Touch.y())) {
				this.changeStage(1);
				this.changeCastleLevel(1);
				this.changePlayerLevel(1);
				this.currentState = GameState.inGame;
			} else if (this.mainMenu.Tap_MenuEntry_CHOOSELEVEL(Touch.x(),
					Touch.y())) {

				this.currentState = GameState.inChooseLevel;
			} else if (this.mainMenu.Tap_MenuEntry_EXIT(Touch.x(), Touch.y())) {
				LSystem.exit();
			} else if (this.mainMenu.Tap_MenuEntry_MUTE(Touch.x(), Touch.y())) {
			} else if (this.mainMenu.Tap_MenuEntry_HELP(Touch.x(), Touch.y())) {
				this.currentState = GameState.inHelp;
			}
		}

	}

	private void UpdatePauseMenu() {
		if (Touch.isDown()) {
			if (this.pauseMenu.Tap_MenuEntry_RETURN(Touch.x(), Touch.y())) {
				this.currentState = GameState.inGame;
			} else if (this.pauseMenu.Tap_MenuEntry_QUIT(Touch.x(), Touch.y())) {
				this.clearStage();
				this.currentState = GameState.inMainMenu;
			}

		}
	}

	private Vector2f first;

	private Vector2f prev;

	private Vector2f vector = new Vector2f();

	private void UpdatePlayer(GameTime gameTime) {
		if (Touch.isDrag()) {
			if (first == null) {
				this.first = new Vector2f(Touch.getLocation());
			}
			this.prev = new Vector2f(Touch.getLocation());
			vector.set(this.prev.sub(this.first));
			this.arrowScale = (0.2f * vector.len()) / this.maxDragDelta;
			this.arrowRotation = 3.141593f + (MathUtils
					.atan((vector.getY() / vector.getX())));
			if (vector.getX() >= 0f) {
				this.arrowRotation += 3.141593f;
			}
			this.isDragging = true;
		} else if (Touch.isUp() && isDragging) {
			if ((gameTime.getTotalGameTime() - this.previousFireTime) > this.fireTime) {
				vector.set(this.prev.sub(this.first));
				this.previousFireTime = gameTime.getTotalGameTime();
				this.AddProjectile(this.player.Position.add(
						(this.player.getWidth() / 2), 0f), vector);
			}
			this.first = null;
			this.prev = null;
			this.isDragging = false;
		}
		switch (this.castle.level) {
		case 1:
			this.player.Position.x = (this.castle.Position.x + (this.castle
					.getWidth() / 2)) - (this.player.getWidth() / 2);
			this.player.Position.y = this.castle.Position.y - 30f;
			break;

		case 2:
			this.player.Position.x = ((this.castle.Position.x + (this.castle
					.getWidth() / 2)) - (this.player.getWidth() / 2)) + 5f;
			this.player.Position.y = this.castle.Position.y - 45f;
			break;

		case 3:
			this.player.Position.x = (this.castle.Position.x + (this.castle
					.getWidth() / 2)) - (this.player.getWidth() / 2);
			this.player.Position.y = this.castle.Position.y - 25f;
			break;

		case 4:
			this.player.Position.x = ((this.castle.Position.x + (this.castle
					.getWidth() / 2)) - (this.player.getWidth() / 2)) + 15f;
			this.player.Position.y = this.castle.Position.y - 5f;
			break;

		case 5:
			this.player.Position.x = ((this.castle.Position.x + (this.castle
					.getWidth() / 2)) - (this.player.getWidth() / 2)) + 10f;
			this.player.Position.y = this.castle.Position.y - 12f;
			break;
		}
	}

	private void UpdateProjectiles(GameTime gameTime) {
		int num;
		for (num = this.playerProjectiles.size() - 1; num >= 0; num--) {
			this.playerProjectiles.get(num).Update(gameTime);
			if (!this.playerProjectiles.get(num).Active) {
				this.playerProjectiles.remove(num);
			}
		}
		for (num = this.enemyProjectiles.size() - 1; num >= 0; num--) {
			this.enemyProjectiles.get(num).Update(gameTime);
			if (!this.enemyProjectiles.get(num).Active) {
				this.enemyProjectiles.remove(num);
			}
		}
	}

	private void UpdateVictoryMenu() {
		if (Key.isKeyPressed(Key.BACK)
				&& this.victoryMenu.Tap_MenuEntry_BACK(Touch.x(), Touch.y())) {
			this.clearStage();
			this.currentState = GameState.inMainMenu;
		}

	}

	private void UpdateWinMenu() {
		int score = this.player.Score;
		if ((this.player.Score > (0x7d0 * this.playerLevel))
				&& (this.playerLevel < 4)) {
			this.winMenu.canUpdate(0);
		}
		if ((this.player.Score > (0xbb8 * this.castleLevel))
				&& (this.castleLevel <= 4)) {
			this.winMenu.canUpdate(1);
		}
		if (Touch.isDown()) {
			if (this.winMenu.Tap_MenuEntry_UPDATEPLAYER(Touch.x(), Touch.y())) {
				if ((this.player.Score >= (0x7d0 * this.playerLevel))
						&& (this.playerLevel < 4)) {
					this.player.Score -= 0x7d0 * this.playerLevel;
					this.playerLevel++;
				}
			} else if (this.winMenu.Tap_MenuEntry_UPDATECASTLE(Touch.x(),
					Touch.y())
					&& ((this.player.Score >= (0xbb8 * this.castleLevel)) && (this.castleLevel <= 4))) {
				this.player.Score -= 0xbb8 * this.castleLevel;
				this.castleLevel++;
			}
			if ((this.player.Score < (0x7d0 * this.playerLevel))
					|| (this.playerLevel >= 4)) {
				this.winMenu.cannotUpdate(0);
			}
			if ((this.player.Score < (0xbb8 * this.castleLevel))
					|| (this.castleLevel > 4)) {
				this.winMenu.cannotUpdate(1);
			}
			if (this.winMenu.Tap_MenuEntry_QUIT(Touch.x(), Touch.y())) {
				this.clearStage();
				this.currentState = GameState.inMainMenu;
			} else if (this.winMenu.Tap_MenuEntry_NEXT(Touch.x(), Touch.y())) {
				if (this.StageLevel <= 4) {
					this.clearStage();
					this.changeStage(this.StageLevel + 1);
					this.changeCastleLevel(this.castleLevel);
					this.changePlayerLevel(this.playerLevel);
					this.player.Score += score;
					this.currentState = GameState.inGame;
				} else {
					this.currentState = GameState.inVictory;
				}
			}
		}

	}

	public final void waitStart() {
	}

	@Override
	public void pressed(LTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void released(LTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void move(LTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drag(LTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pressed(LKey e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void released(LKey e) {
		// TODO Auto-generated method stub

	}

}