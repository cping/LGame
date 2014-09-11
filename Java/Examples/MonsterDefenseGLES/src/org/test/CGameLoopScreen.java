package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.BlendState;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.event.ActionKey;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.input.LTouchLocation;
import loon.core.input.LInputFactory.Key;
import loon.utils.StringUtils;

public class CGameLoopScreen implements CScreen {
	private float addEnemyTime = 1f;
	private float addEnemyTimer;
	private boolean allEnemydeath;
	private LTexture backGround;
	private CAnimObject boss1Anim;
	private CEnemyType boss1Type;
	private CAnimObject boss2Anim;
	private CEnemyType boss2Type;
	private LTexture buttonPurchaseTexture;
	private int currentLevelArrayIndex;
	public int currentWaveMaxEnemy;
	private CParticle[] damageIndicator;
	private CSound electroSnd;
	public CEnemy[] enemy;
	private CAnimObject enemy1Anim;
	private CAnimObject enemy2Anim;
	private CAnimObject enemy3Anim;
	private CAnimObject enemy4Anim;
	private CAnimObject enemy5Anim;
	private CAnimObject enemy6Anim;
	private CEnemyType[] enemysLevelArray;
	private CEnemyType EnemyType1;
	private CEnemyType EnemyType2;
	private CEnemyType EnemyType3;
	private CEnemyType EnemyType4;
	private CEnemyType EnemyType5;
	private CEnemyType EnemyType6;
	private int enemyWave1 = 10;
	public CAnimObject explosionAnim;
	private Vector2f failedEnemysPos;
	private Vector2f failedEnemysPos2;
	private boolean fastForward;
	private RectBox fastForwardRect;
	public boolean freeMode;
	public CMenu freePlayMenu;
	private Vector2f gameOverTextPos;
	private float gameTime;
	private Vector2f gameTimePos;
	private Vector2f gameTimePos2;
	public LTexture healthBarTexture;
	private Vector2f highScorePos;
	private Vector2f highScorePos2;
	private Vector2f highScorePos2_1;
	private Vector2f highScorePos2_2;
	private LTexture iconFastForward0;
	private LTexture iconFastForward1;
	private LTexture iconHeart;
	private LTexture iconMaxUpgrade;
	private LTexture iconMoney;
	private LTexture iconNoUpgrade;
	private LTexture iconSellTexture;
	private LTexture iconUpgrade;
	private LTexture iconWaves;
	private int killedEnemy;
	private Vector2f killedEnemysPos;
	private Vector2f killedEnemysPos2;
	private int lastHighscore;
	public int[][] levelArray;
	public boolean levelDone;
	private Vector2f levelUnlockedPos;
	public int lives = 10;
	private Vector2f livesPos;
	private String liveString;
	private int lostEnemy;
	private MainGame mainGame;
	private int maxTower = 50;
	public int maxWaves;
	public int money;
	private int moneyBonus;
	private Vector2f MoneyPos;
	private String moneyString;
	private int neededSpawnedEnemys;
	private float newWaveTime = 5f;
	private float newWaveTimer = 5f;
	private boolean nextLevelUnlocked;
	public boolean pause;
	public CMenu pauseMenu;
	private Vector2f pausePos;
	private LTexture pauseTexture;
	private CSound pfanneSnd;
	private Vector2f pointerPos = new Vector2f();
	private LTexture pointerTexture;
	public LTexture positionCircleTexture;
	private LTexture positionSquare;
	private CSound revolverSnd;
	private CSound RocketSnd;
	private int score;
	public int selectedTower = -1;
	private boolean showTrial;
	private LTexture speedReducer;
	private LTexture speedReducerBoss;
	private CSound speedReducerSnd;
	public int spentMoney;
	private Vector2f spentMoneyPos;
	private Vector2f spentMoneyPos2;

	private int startMoney = 300;
	private float stopAlpha;
	private LTexture stopTexture;
	private Vector2f stopVector;
	public CEnemy targedEnemy;
	private LTexture textGameOverTexture;
	private LColor textShadow = LColor.black;
	private Vector2f textShadowOffset;
	private LColor tmpLColor;
	private CTower[] tower;
	private CTowerIcons towerIcons;
	private CTowerIcons towerOptionsIcons;
	private CTowerType TowerType1;
	private CTowerType TowerType2;
	private CTowerType TowerType3;
	private CTowerType TowerType4;
	private CTowerType TowerType5;
	private Vector2f trialPos;
	public int wave = 1;
	private Vector2f WavePos;
	private Vector2f waveReachedPos;
	private Vector2f wavesCompletedPos;
	private Vector2f wavesCompletedPos2;
	private String waveString;
	private Vector2f waveTimePos;
	private String waveTimeString;

	public CGameLoopScreen(MainGame game) {
		this.mainGame = game;
	}

	public final void draw(SpriteBatch batch, LColor defaultSceneLColor) {
		if ((this.levelDone || this.pause) || this.showTrial) {
			LColor color = new LColor(defaultSceneLColor);
			int v = (defaultSceneLColor.getRed() / 2);
			color.setColor(v, v, v);
			this.drawInGame(batch, color);
		} else {
			this.drawInGame(batch, defaultSceneLColor);
		}
		if (this.pause) {
			batch.draw(this.pauseTexture, this.pausePos, defaultSceneLColor);
			this.pauseMenu.draw(batch, defaultSceneLColor);
		}
		if (this.levelDone) {
			this.levelDoneDraw(batch, defaultSceneLColor);
		}
		if (this.showTrial) {
			batch.draw(this.buttonPurchaseTexture, this.trialPos,
					defaultSceneLColor);
			this.pauseMenu.draw(batch, defaultSceneLColor);
		}
	}

	public final void drawInGame(SpriteBatch batch, LColor defaultSceneLColor) {
		this.textShadow.a = defaultSceneLColor.a;
		batch.draw(this.backGround, this.mainGame.fullScreenRect,
				defaultSceneLColor);
		for (int i = 0; i < this.maxTower; i++) {
			this.tower[i].drawcircle(batch, defaultSceneLColor);
		}
		for (int j = 0; j < this.currentWaveMaxEnemy; j++) {
			if (this.enemy[j].isThere) {
				this.enemy[j].draw(batch, defaultSceneLColor);
			}
		}
		if (this.stopAlpha > 0f) {
			int v = (int) (defaultSceneLColor.getAlpha() * this.stopAlpha);
			tmpLColor.setColor(0, 0, 0, v);
			batch.draw(this.stopTexture, this.stopVector, this.tmpLColor);
		}
		for (int k = 0; k < this.maxTower; k++) {
			this.tower[k].draw(batch, defaultSceneLColor);
		}
		for (int m = 0; m < this.maxTower; m++) {
			if (this.tower[m].isThere) {
				this.tower[m].drawBullets(batch, defaultSceneLColor);
			}
		}
		for (int n = 0; n < 50; n++) {
			if (this.damageIndicator[n].isThere) {
				RectBox sourceRectBox = null;
				batch.draw(
						this.mainGame.gameLoopScreen.explosionAnim.getTexture(),
						this.damageIndicator[n].rectangle, sourceRectBox,
						this.damageIndicator[n].alpha,
						this.damageIndicator[n].rotation,
						this.damageIndicator[n].origin, SpriteEffects.None);
			}
		}
		batch.flush(BlendState.Additive);
		if (this.targedEnemy != null) {
			this.pointerPos.x = this.targedEnemy.rect.x
					- (this.pointerTexture.getWidth() / 2);
			this.pointerPos.y = this.targedEnemy.pos.y
					- this.pointerTexture.getHeight();
			batch.draw(this.pointerTexture, this.pointerPos, defaultSceneLColor);
		}
		if (this.towerIcons.visible || this.towerOptionsIcons.visible) {
			this.towerIcons.draw(batch, defaultSceneLColor);
			this.towerOptionsIcons.draw(batch, defaultSceneLColor);
		}
		batch.draw(this.iconMoney, this.MoneyPos, defaultSceneLColor);
		this.moneyString = "     " + (new Integer(this.money)).toString();
		batch.drawString(this.mainGame.standartFont, this.moneyString,
				this.MoneyPos.add(this.textShadowOffset), this.textShadow);
		batch.drawString(this.mainGame.standartFont, this.moneyString,
				this.MoneyPos, defaultSceneLColor);
		batch.draw(this.iconWaves, this.WavePos, defaultSceneLColor);
		if (this.wave <= this.maxWaves) {
			this.waveString = StringUtils.concat("      ", (new Integer(
					this.wave)).toString(), " / ", this.maxWaves);
			batch.drawString(this.mainGame.standartFont, this.waveString,
					this.WavePos.add(this.textShadowOffset), this.textShadow);
			batch.drawString(this.mainGame.standartFont, this.waveString,
					this.WavePos, defaultSceneLColor);
		} else {
			this.waveString = "      " + (new Integer(this.wave)).toString();
			batch.drawString(this.mainGame.standartFont, this.waveString,
					this.WavePos.add(this.textShadowOffset), this.textShadow);
			batch.drawString(this.mainGame.standartFont, this.waveString,
					this.WavePos, defaultSceneLColor);
		}
		if (this.newWaveTimer > 0f) {
			this.waveTimeString = "next wave in: "
					+ (new Integer((int) this.newWaveTimer)).toString();
			batch.drawString(this.mainGame.standartFont, this.waveTimeString,
					this.waveTimePos.add(this.textShadowOffset),
					this.textShadow);
			batch.drawString(this.mainGame.standartFont, this.waveTimeString,
					this.waveTimePos, defaultSceneLColor);
		}
		batch.draw(this.iconHeart, this.livesPos, defaultSceneLColor);
		this.liveString = "      " + (new Integer(this.lives)).toString();
		batch.drawString(this.mainGame.standartFont, this.liveString,
				this.livesPos.add(this.textShadowOffset), this.textShadow);
		batch.drawString(this.mainGame.standartFont, this.liveString,
				this.livesPos, defaultSceneLColor);
		if (this.fastForward) {
			batch.draw(this.iconFastForward1, this.fastForwardRect,
					defaultSceneLColor);
		} else {
			batch.draw(this.iconFastForward0, this.fastForwardRect,
					defaultSceneLColor);
		}
	}

	public final CEnemy getFarestEnemy(Vector2f pos, float maxlenght) {
		CEnemy enemy = null;
		float num2 = 0f;
		if ((this.targedEnemy != null) && this.targedEnemy.isThere) {
			Vector2f vector = (this.targedEnemy.pos
					.add(this.targedEnemy.origin)).sub(pos);
			if (vector.lengthSquared() <= maxlenght) {
				return this.targedEnemy;
			}
		}
		for (int i = 0; i < this.currentWaveMaxEnemy; i++) {
			if (this.enemy[i].isThere) {
				float num = ((this.enemy[i].pos.add(this.enemy[i].origin))
						.sub(pos)).lengthSquared();
				if ((num > num2) && (num < maxlenght)) {
					num2 = num;
					enemy = this.enemy[i];
				}
			}
		}
		return enemy;
	}

	public final String getLevelTimeString() {
		int gameTime = (int) this.gameTime;
		int num2 = gameTime / 60;
		gameTime -= num2 * 60;
		return StringUtils.concat(new Integer(num2).toString(), " min ",
				gameTime, " sec ");
	}

	public final CEnemy getNearestEnemy(Vector2f pos, float maxlenght) {
		CEnemy enemy = null;
		float num2 = 1E+08f;
		if ((this.targedEnemy != null) && this.targedEnemy.isThere) {
			Vector2f vector = (this.targedEnemy.pos
					.add(this.targedEnemy.origin)).sub(pos);
			if (vector.lengthSquared() <= maxlenght) {
				return this.targedEnemy;
			}
		}
		for (int i = 0; i < this.currentWaveMaxEnemy; i++) {
			if (this.enemy[i].isThere) {
				float num = ((this.enemy[i].pos.add(this.enemy[i].origin)
						.sub(pos))).lengthSquared();
				if (num < num2) {
					num2 = num;
					enemy = this.enemy[i];
				}
			}
		}
		if (num2 > maxlenght) {
			enemy = null;
		}
		return enemy;
	}

	public final void indicateDamage(Vector2f pos) {
		for (int i = 0; i < 50; i++) {
			if (!this.damageIndicator[i].isThere) {
				int width = this.mainGame.random.Next(20, 0x39);
				this.damageIndicator[i].init(pos, width, width);
				return;
			}
		}
	}

	public final void initGameLoop(CLevel level) {
		this.levelArray = level.levelArray;
		this.backGround = LTextures.loadTexture(level.backGround);
		this.maxWaves = level.maxWaves;
		this.levelDone = false;
		CWaypoints wayPoints = level.waypoints
				.generateWaypointsWithOffset(
						(28.5f * this.mainGame.scalePos.y)
								- (((float) this.enemy1Anim.getTexture()
										.getWidth()) / 2f),
						(28.5f * this.mainGame.scalePos.y)
								- (((float) this.enemy1Anim.getTexture()
										.getHeight()) / 2f));
		CWaypoints waypoints2 = level.waypoints
				.generateWaypointsWithOffset(
						(28.5f * this.mainGame.scalePos.y)
								- (((float) this.enemy2Anim.getTexture()
										.getWidth()) / 2f),
						(28.5f * this.mainGame.scalePos.y)
								- (((float) this.enemy2Anim.getTexture()
										.getHeight()) / 2f));
		CWaypoints waypoints3 = level.waypoints
				.generateWaypointsWithOffset(
						(28.5f * this.mainGame.scalePos.y)
								- (((float) this.enemy5Anim.getTexture()
										.getWidth()) / 2f),
						(28.5f * this.mainGame.scalePos.y)
								- (((float) this.enemy5Anim.getTexture()
										.getHeight()) / 2f));
		CWaypoints waypoints4 = level.waypoints
				.generateWaypointsWithOffset(
						(28.5f * this.mainGame.scalePos.y)
								- (((float) this.boss1Anim.getTexture()
										.getWidth()) / 2f),
						(28.5f * this.mainGame.scalePos.y)
								- (((float) this.boss1Anim.getTexture()
										.getHeight()) / 2f));
		this.EnemyType1.setWaypoints(wayPoints);
		this.EnemyType2.setWaypoints(waypoints2);
		this.EnemyType3.setWaypoints(wayPoints);
		this.EnemyType4.setWaypoints(wayPoints);
		this.EnemyType5.setWaypoints(waypoints3);
		this.EnemyType6.setWaypoints(wayPoints);
		this.boss1Type.setWaypoints(waypoints4);
		this.boss2Type.setWaypoints(waypoints4);
	}

	public final void initNewWave(int wave) {
		this.currentWaveMaxEnemy = this.enemyWave1;
		this.neededSpawnedEnemys = this.currentWaveMaxEnemy;
		this.wave = wave;
		this.enemy = new CEnemy[this.currentWaveMaxEnemy];
		this.currentLevelArrayIndex = 0;
		this.enemysLevelArray = new CEnemyType[this.currentWaveMaxEnemy];
		for (int i = 0; i < this.currentWaveMaxEnemy; i++) {
			this.enemy[i] = new CEnemy(this.mainGame);
		}
		switch (wave) {
		case 0x19:
		case 0x23:
		case 3:
		case 4:
		case 5:
		case 15:
		case 0x2d:
		case 0x37:
			for (int num5 = 0; num5 < (this.currentWaveMaxEnemy / 3); num5++) {
				this.enemysLevelArray[num5] = this.EnemyType1;
			}
			for (int num6 = this.currentWaveMaxEnemy / 3; num6 < ((this.currentWaveMaxEnemy / 3) * 2); num6++) {
				this.enemysLevelArray[num6] = this.EnemyType2;
			}
			for (int num7 = (this.currentWaveMaxEnemy / 3) * 2; num7 < this.currentWaveMaxEnemy; num7++) {
				this.enemysLevelArray[num7] = this.EnemyType4;
			}
			return;

		case 30:
		case 10:
		case 20:
		case 40:
		case 50:
		case 60:
		case 70:
		case 110:
		case 120:
		case 130:
		case 80:
		case 90:
		case 100:
		case 140:
		case 150:
		case 160:
		case 190:
		case 200:
		case 170:
		case 180:
			for (int num3 = 0; num3 < (this.currentWaveMaxEnemy / 2); num3++) {
				this.enemysLevelArray[num3] = this.EnemyType1;
			}
			for (int num4 = this.currentWaveMaxEnemy / 2; num4 < this.currentWaveMaxEnemy; num4++) {
				this.enemysLevelArray[num4] = this.EnemyType3;
			}
			switch (wave) {
			case 30:
				this.enemysLevelArray[this.currentWaveMaxEnemy - 1] = this.boss2Type;
				return;

			case 40:
				this.enemysLevelArray[this.currentWaveMaxEnemy - 1] = this.boss1Type;
				return;

			case 50:
				this.enemysLevelArray[this.currentWaveMaxEnemy - 1] = this.boss2Type;
				return;

			case 10:
				this.enemysLevelArray[this.currentWaveMaxEnemy - 1] = this.boss1Type;
				return;

			case 20:
				this.enemysLevelArray[this.currentWaveMaxEnemy - 1] = this.boss1Type;
				return;

			case 60:
				this.enemysLevelArray[this.currentWaveMaxEnemy - 1] = this.boss1Type;
				return;

			case 70:
				this.enemysLevelArray[this.currentWaveMaxEnemy - 1] = this.boss1Type;
				return;

			case 80:
				this.enemysLevelArray[this.currentWaveMaxEnemy - 1] = this.boss1Type;
				return;

			case 90:
				this.enemysLevelArray[this.currentWaveMaxEnemy - 1] = this.boss1Type;
				return;

			case 100:
				this.enemysLevelArray[this.currentWaveMaxEnemy - 1] = this.boss1Type;
				return;

			case 200:
				this.enemysLevelArray[this.currentWaveMaxEnemy - 1] = this.boss2Type;
				return;
			}
			this.enemysLevelArray[this.currentWaveMaxEnemy - 1] = this.boss1Type;
			return;

		case 1:
		case 2:
			for (int num2 = 0; num2 < this.currentWaveMaxEnemy; num2++) {
				this.enemysLevelArray[num2] = this.EnemyType1;
			}
			return;
		}
		float num8 = ((float) this.currentWaveMaxEnemy) / 6f;
		for (int j = 0; j < num8; j++) {
			this.enemysLevelArray[j] = this.EnemyType1;
		}
		for (int k = (int) num8; k < (num8 * 2f); k++) {
			this.enemysLevelArray[k] = this.EnemyType2;
		}
		for (int m = (int) (num8 * 2f); m < (num8 * 3f); m++) {
			this.enemysLevelArray[m] = this.EnemyType3;
		}
		for (int n = (int) (num8 * 3f); n < (num8 * 4f); n++) {
			this.enemysLevelArray[n] = this.EnemyType4;
		}
		for (int num13 = (int) (num8 * 4f); num13 < (num8 * 5f); num13++) {
			this.enemysLevelArray[num13] = this.EnemyType5;
		}
		for (int num14 = (int) (num8 * 5f); num14 < (num8 * 6f); num14++) {
			this.enemysLevelArray[num14] = this.EnemyType6;
		}
	}

	public final void levelDoneDraw(SpriteBatch batch, LColor defaultSceneLColor) {
		if (this.lives == 0) {
			batch.draw(this.textGameOverTexture, this.gameOverTextPos,
					defaultSceneLColor);
			batch.drawString(this.mainGame.smalFont, "Missed Enemies:",
					this.failedEnemysPos, defaultSceneLColor);
			batch.drawString(this.mainGame.smalFont, (new Integer(
					this.lostEnemy)).toString(), this.failedEnemysPos2,
					defaultSceneLColor);
			batch.drawString(this.mainGame.smalFont, "Killed Enemies:",
					this.killedEnemysPos, defaultSceneLColor);
			batch.drawString(this.mainGame.smalFont, (new Integer(
					this.killedEnemy)).toString(), this.killedEnemysPos2,
					defaultSceneLColor);
			batch.drawString(this.mainGame.smalFont, "Wave:",
					this.wavesCompletedPos, defaultSceneLColor);
			batch.drawString(this.mainGame.smalFont,
					(new Integer(this.wave)).toString(),
					this.wavesCompletedPos2, defaultSceneLColor);
			batch.drawString(this.mainGame.smalFont, "Time Played",
					this.gameTimePos, defaultSceneLColor);
			batch.drawString(this.mainGame.smalFont, this.getLevelTimeString(),
					this.gameTimePos2, defaultSceneLColor);
			batch.drawString(this.mainGame.smalFont, "Spent Money:",
					this.spentMoneyPos, defaultSceneLColor);
			batch.drawString(this.mainGame.smalFont, (new Integer(
					this.spentMoney)).toString(), this.spentMoneyPos2,
					defaultSceneLColor);
			if (!this.freeMode) {
				if (this.lastHighscore > -1) {
					batch.drawString(this.mainGame.smalFont, "New Highscore",
							this.highScorePos2_1, defaultSceneLColor);
				} else {
					batch.drawString(this.mainGame.smalFont, "Score",
							this.highScorePos2_1, defaultSceneLColor);
				}
				batch.drawString(this.mainGame.smalFont, (new Integer(
						this.score)).toString(), this.highScorePos2_2,
						defaultSceneLColor);
			}
		} else {
			this.freePlayMenu.draw(batch, defaultSceneLColor);
			batch.drawString(this.mainGame.smalFont, StringUtils.concat(
					"You reached wave ",
					this.mainGame.level[this.mainGame.currentLevel].maxWaves,
					" / ",
					this.mainGame.level[this.mainGame.currentLevel].maxWaves),
					this.waveReachedPos, defaultSceneLColor);
			if (this.nextLevelUnlocked) {
				batch.drawString(this.mainGame.smalFont, "Level "
						+ (this.mainGame.currentLevel + 2) + " unlocked",
						this.levelUnlockedPos, defaultSceneLColor);
			}
			if (this.lastHighscore > -1) {
				batch.drawString(this.mainGame.smalFont, "New Highscore",
						this.highScorePos, defaultSceneLColor);
			} else {
				batch.drawString(this.mainGame.smalFont, "Score",
						this.highScorePos, defaultSceneLColor);
			}
			batch.drawString(this.mainGame.smalFont,
					(new Integer(this.score)).toString(), this.highScorePos2,
					defaultSceneLColor);
		}
	}

	public final void LoadContent() {
		this.currentWaveMaxEnemy = this.enemyWave1;
		this.positionCircleTexture = LTextures
				.loadTexture(this.mainGame.gfxRoot
						+ "/tower/positionCircle.png");
		this.tower = new CTower[this.maxTower];
		CAnimObject obj2 = new CAnimObject();
		CAnimObject obj3 = new CAnimObject();
		CAnimObject obj4 = new CAnimObject();
		CAnimObject obj5 = new CAnimObject();
		CAnimObject obj6 = new CAnimObject();
		CAnimObject obj7 = new CAnimObject();
		CAnimObject obj8 = new CAnimObject();
		CAnimObject obj9 = new CAnimObject();
		CAnimObject obj10 = new CAnimObject();
		CAnimObject obj11 = new CAnimObject();
		CAnimObject obj12 = new CAnimObject();
		CAnimObject obj13 = new CAnimObject();
		CAnimObject obj14 = new CAnimObject();
		CAnimObject obj15 = new CAnimObject();
		CAnimObject obj16 = new CAnimObject();
		obj2.init(
				new LTexture[] {
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower1_1_1.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower1_1_2.png") }, 2, 20, 0);
		obj3.init(
				new LTexture[] {
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower1_2_1.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower1_2_2.png") }, 2, 20, 0);
		obj4.init(
				new LTexture[] {
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower1_3_1.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower1_3_2.png") }, 2, 20, 0);
		obj5.init(
				new LTexture[] {
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower2_1_1.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower2_1_2.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower2_1_3.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower2_1_4.png") }, 4, 15, 0);
		obj6.init(
				new LTexture[] {
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower2_2_1.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower2_2_2.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower2_2_3.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower2_2_4.png") }, 4, 15, 0);
		obj7.init(
				new LTexture[] {
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower2_3_1.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower2_3_2.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower2_3_3.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower2_3_4.png") }, 4, 15, 0);
		obj8.init(
				new LTexture[] {
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower3_1_1.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower3_1_2.png") }, 2, 15, 0);
		obj9.init(
				new LTexture[] {
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower3_2_1.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower3_2_2.png") }, 2, 15, 0);
		obj10.init(
				new LTexture[] {
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower3_3_1.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower3_2_2.png") }, 2, 15, 0);
		obj11.init(
				new LTexture[] {
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower4_1_1.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower4_1_2.png") }, 2, 5, 0);
		obj12.init(
				new LTexture[] {
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower4_2_1.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower4_2_2.png") }, 2, 5, 0);
		obj13.init(
				new LTexture[] {
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower4_3_1.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower4_3_2.png") }, 2, 5, 0);
		obj14.init(
				new LTexture[] {
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower5_1_1.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower5_1_2.png") }, 2, 4, 0);
		obj15.init(
				new LTexture[] {
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower5_2_1.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower5_2_2.png") }, 2, 4, 0);
		obj16.init(
				new LTexture[] {
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower5_3_1.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\tower\\tower5_3_2.png") }, 2, 4, 0);
		CAnimObject anim = new CAnimObject();
		CAnimObject obj18 = new CAnimObject();
		CAnimObject obj19 = new CAnimObject();
		CAnimObject obj20 = new CAnimObject();
		CAnimObject obj21 = new CAnimObject();
		CAnimObject obj22 = new CAnimObject();
		CAnimObject obj23 = new CAnimObject();
		CAnimObject obj24 = new CAnimObject();
		CAnimObject obj25 = new CAnimObject();
		anim.init(
				new LTexture[] { LTextures.loadTexture(this.mainGame.gfxRoot
						+ "\\bullets\\shotRevolver.png") }, 1, 5, -1);
		obj18.init(
				new LTexture[] { LTextures.loadTexture(this.mainGame.gfxRoot
						+ "\\bullets\\shotSieb.png") }, 1, 5, -1);
		obj19.init(
				new LTexture[] { LTextures.loadTexture(this.mainGame.gfxRoot
						+ "\\bullets\\shotNudelholz.png") }, 1, 5, -1);
		obj20.init(
				new LTexture[] { LTextures.loadTexture(this.mainGame.gfxRoot
						+ "\\bullets\\shotPfanne.png") }, 1, 5, -1);
		obj21.init(
				new LTexture[] { LTextures.loadTexture(this.mainGame.gfxRoot
						+ "\\bullets\\shotRocket1.png") }, 1, 5, -1);
		obj22.init(
				new LTexture[] { LTextures.loadTexture(this.mainGame.gfxRoot
						+ "\\bullets\\shotRocket2.png") }, 1, 5, -1);
		obj23.init(
				new LTexture[] { LTextures.loadTexture(this.mainGame.gfxRoot
						+ "\\bullets\\shotRocket3.png") }, 1, 5, -1);
		obj24.init(
				new LTexture[] { LTextures.loadTexture(this.mainGame.gfxRoot
						+ "\\bullets\\shotSpeedReducer.png") }, 1, 5, -1);
		obj25.init(
				new LTexture[] {
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\bullets\\shotElectro1.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\bullets\\shotElectro2.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\bullets\\shotElectro3.png") }, 3, 5, -1);
		CAnimObject tail = new CAnimObject();
		tail.init(
				new LTexture[] { LTextures.loadTexture(this.mainGame.gfxRoot
						+ "\\bullets\\rauch1.png") }, 1, 5, -1);
		CBulletType type = new CBulletType(anim,
				200f * this.mainGame.scalePos.y, false, 0f, null, 0f);
		CBulletType type2 = new CBulletType(obj18,
				90f * this.mainGame.scalePos.y, false, 1f, null,
				80f * this.mainGame.scalePos.y);
		CBulletType type3 = new CBulletType(obj19,
				90f * this.mainGame.scalePos.y, false, 1f, null,
				80f * this.mainGame.scalePos.y);
		CBulletType type4 = new CBulletType(obj20,
				90f * this.mainGame.scalePos.y, false, 1f, null,
				80f * this.mainGame.scalePos.y);
		CBulletType type5 = new CBulletType(obj21,
				150f * this.mainGame.scalePos.y, true, 0f, tail,
				80f * this.mainGame.scalePos.y);
		CBulletType type6 = new CBulletType(obj22,
				150f * this.mainGame.scalePos.y, true, 0f, tail,
				80f * this.mainGame.scalePos.y);
		CBulletType type7 = new CBulletType(obj23,
				150f * this.mainGame.scalePos.y, true, 0f, tail,
				80f * this.mainGame.scalePos.y);
		CBulletType type8 = new CBulletType(obj25,
				100f * this.mainGame.scalePos.y, false, 0f, null, -1f);
		CBulletType type9 = new CBulletType(obj24,
				100f * this.mainGame.scalePos.y, false, 0f, null, 1f);
		this.revolverSnd = new CSound(this.mainGame, "snd\\busterSnd");
		this.pfanneSnd = new CSound(this.mainGame, "snd\\maggieSnd");
		this.RocketSnd = new CSound(this.mainGame, "snd\\JoeSnd");
		this.electroSnd = new CSound(this.mainGame, "snd\\hankSnd");
		this.speedReducerSnd = new CSound(this.mainGame, "snd\\flintSnd");
		this.TowerType1 = new CTowerType(3, 50, 1f, 0f, 1.4f, 0.1f,
				new CAnimObject[] { obj2, obj3, obj4 }, this.revolverSnd,
				80f * this.mainGame.scalePos.y, new int[] { 100, 200, 0x14f },
				new CBulletType[] { type, type, type },
				LTextures.loadTexture(this.mainGame.gfxRoot
						+ "\\icons\\towerIcon1.png"),
				LTextures.loadTexture(this.mainGame.gfxRoot
						+ "\\icons\\towerIcon1_0.png"));
		this.TowerType2 = new CTowerType(3, 0x55, 1f, 0.6f, 1.2f, 0.2f,
				new CAnimObject[] { obj5, obj6, obj7 }, this.pfanneSnd,
				90f * this.mainGame.scalePos.y, new int[] { 300, 500, 600 },
				new CBulletType[] { type2, type3, type4 },
				LTextures.loadTexture(this.mainGame.gfxRoot
						+ "\\icons\\towerIcon2.png"),
				LTextures.loadTexture(this.mainGame.gfxRoot
						+ "\\icons\\towerIcon2_0.png"));
		this.TowerType3 = new CTowerType(3, 100, 1.1f, 0.8f, 1.2f, 0.3f,
				new CAnimObject[] { obj8, obj9, obj10 }, this.RocketSnd,
				150f * this.mainGame.scalePos.y, new int[] { 600, 700, 900 },
				new CBulletType[] { type5, type6, type7 },
				LTextures.loadTexture(this.mainGame.gfxRoot
						+ "\\icons\\towerIcon3.png"),
				LTextures.loadTexture(this.mainGame.gfxRoot
						+ "\\icons\\towerIcon3_0.png"));
		this.TowerType4 = new CTowerType(3, 3, 2f, 0.8f, 1.2f, 0.1f,
				new CAnimObject[] { obj11, obj12, obj13 }, this.electroSnd,
				100f * this.mainGame.scalePos.y, new int[] { 350, 0x23f, 700 },
				new CBulletType[] { type8, type8, type8 },
				LTextures.loadTexture(this.mainGame.gfxRoot
						+ "\\icons\\towerIcon4.png"),
				LTextures.loadTexture(this.mainGame.gfxRoot
						+ "\\icons\\towerIcon4_0.png"));
		this.TowerType5 = new CTowerType(3, 7, 1f, 0.1f, 1.1f, 0.1f,
				new CAnimObject[] { obj14, obj15, obj16 },
				this.speedReducerSnd, 80f * this.mainGame.scalePos.y,
				new int[] { 180, 270, 300 }, new CBulletType[] { type9, type9,
						type9 }, LTextures.loadTexture(this.mainGame.gfxRoot
						+ "\\icons\\towerIcon5.png"),
				LTextures.loadTexture(this.mainGame.gfxRoot
						+ "\\icons\\towerIcon5_0.png"));
		this.explosionAnim = new CAnimObject();
		this.explosionAnim.init(
				new LTexture[] { LTextures.loadTexture(this.mainGame.gfxRoot
						+ "\\explosion\\explosion1.png") }, 1, 5, -1);
		this.healthBarTexture = LTextures.loadTexture(this.mainGame.gfxRoot
				+ "\\enemy\\healthBar.png");
		for (int i = 0; i < 50; i++) {
			this.tower[i] = new CTower(this.mainGame,
					this.positionCircleTexture);
		}
		this.enemy1Anim = new CAnimObject();
		this.enemy2Anim = new CAnimObject();
		this.enemy3Anim = new CAnimObject();
		this.enemy4Anim = new CAnimObject();
		this.enemy5Anim = new CAnimObject();
		this.enemy6Anim = new CAnimObject();
		this.boss1Anim = new CAnimObject();
		this.boss2Anim = new CAnimObject();
		this.boss1Anim.init(
				new LTexture[] {
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\boss1_1.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\boss1_2.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\boss1_3.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\boss1_4.png") }, 4, 8, -1);
		this.boss2Anim.init(
				new LTexture[] {
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\boss2_1.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\boss2_2.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\boss2_3.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\boss2_4.png") }, 4, 8, -1);
		this.enemy1Anim.init(
				new LTexture[] {
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\enemy1_1.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\enemy1_2.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\enemy1_3.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\enemy1_4.png") }, 4, 10, -1);
		this.enemy2Anim.init(
				new LTexture[] {
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\enemy2_1.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\enemy2_2.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\enemy2_3.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\enemy2_4.png") }, 4, 5, -1);
		this.enemy3Anim.init(
				new LTexture[] {
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\enemy3_1.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\enemy3_2.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\enemy3_3.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\enemy3_4.png") }, 4, 10, -1);
		this.enemy4Anim.init(
				new LTexture[] {
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\enemy4_1.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\enemy4_2.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\enemy4_3.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\enemy4_4.png") }, 4, 10, -1);
		this.enemy5Anim.init(
				new LTexture[] {
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\enemy5_1.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\enemy5_2.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\enemy5_3.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\enemy5_4.png") }, 4, 5, -1);
		this.enemy6Anim.init(
				new LTexture[] {
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\enemy6_1.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\enemy6_2.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\enemy6_3.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\enemy6_4.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\enemy6_5.png"),
						LTextures.loadTexture(this.mainGame.gfxRoot
								+ "\\enemy\\enemy6_6.png") }, 6, 15, -1);
		this.speedReducer = LTextures.loadTexture(this.mainGame.gfxRoot
				+ "\\enemy\\speedReducer.png");
		this.speedReducerBoss = LTextures.loadTexture(this.mainGame.gfxRoot
				+ "\\enemy\\speedReducerBoss.png");
		this.EnemyType1 = new CEnemyType(1, 0x4b, 0.35f,
				100f * this.mainGame.scalePos.y, this.enemy1Anim,
				this.speedReducer, 15, null);
		this.EnemyType2 = new CEnemyType(1, 60, 0.35f,
				150f * this.mainGame.scalePos.y, this.enemy2Anim,
				this.speedReducer, 0x11, null);
		this.EnemyType3 = new CEnemyType(1, 150, 0.35f,
				55f * this.mainGame.scalePos.y, this.enemy3Anim,
				this.speedReducer, 0x11, null);
		this.EnemyType4 = new CEnemyType(1, 50, 0.35f,
				85f * this.mainGame.scalePos.y, this.enemy4Anim,
				this.speedReducer, 0x10, null);
		this.EnemyType5 = new CEnemyType(1, 200, 0.35f,
				50f * this.mainGame.scalePos.y, this.enemy5Anim,
				this.speedReducer, 0x11, null);
		this.EnemyType6 = new CEnemyType(1, 0x55, 0.35f,
				90f * this.mainGame.scalePos.y, this.enemy6Anim,
				this.speedReducer, 0x10, null);
		this.boss1Type = new CEnemyType(2, 0x5dc, 0.17f,
				25f * this.mainGame.scalePos.y, this.boss1Anim,
				this.speedReducerBoss, 100, null);
		this.boss2Type = new CEnemyType(2, 0x5dc, 0.17f,
				25f * this.mainGame.scalePos.y, this.boss2Anim,
				this.speedReducerBoss, 100, new CBulletType[] { type5, type6,
						type7 });
		this.positionSquare = LTextures.loadTexture(this.mainGame.gfxRoot
				+ "\\icons\\positionSquare.png");
		this.iconSellTexture = LTextures.loadTexture(this.mainGame.gfxRoot
				+ "\\icons\\iconSell.png");
		this.iconUpgrade = LTextures.loadTexture(this.mainGame.gfxRoot
				+ "\\icons\\iconUpgrade.png");
		this.iconNoUpgrade = LTextures.loadTexture(this.mainGame.gfxRoot
				+ "\\icons\\iconNoUpgrade.png");
		this.iconMaxUpgrade = LTextures.loadTexture(this.mainGame.gfxRoot
				+ "\\icons\\iconMaxUpgrade.png");
		this.iconFastForward0 = LTextures.loadTexture(this.mainGame.gfxRoot
				+ "\\icons\\iconMaxSpeed0.png");
		this.iconFastForward1 = LTextures.loadTexture(this.mainGame.gfxRoot
				+ "\\icons\\iconMaxSpeed1.png");
		this.iconHeart = LTextures.loadTexture(this.mainGame.gfxRoot
				+ "\\icons\\iconHeart.png");
		this.iconMoney = LTextures.loadTexture(this.mainGame.gfxRoot
				+ "\\icons\\iconMoney.png");
		this.iconWaves = LTextures.loadTexture(this.mainGame.gfxRoot
				+ "\\icons\\iconWaves.png");
		this.pointerTexture = LTextures.loadTexture(this.mainGame.gfxRoot
				+ "\\pointer.png");
		this.textGameOverTexture = LTextures.loadTexture(this.mainGame.gfxRoot
				+ "\\textGameOver.png");
		this.pauseTexture = LTextures.loadTexture(this.mainGame.gfxRoot
				+ "\\textPause.png");
		this.stopTexture = LTextures.loadTexture(this.mainGame.gfxRoot
				+ "\\icons\\stop.png");
		this.stopVector = new Vector2f(0f, 0f);
		this.stopAlpha = 0f;
		this.initNewWave(this.wave);
		this.money = this.startMoney;
		this.buttonPurchaseTexture = LTextures
				.loadTexture(this.mainGame.gfxRoot
						+ "\\menu\\buttons\\buttonPurchase.png");
		this.damageIndicator = new CParticle[50];
		for (int j = 0; j < 50; j++) {
			this.damageIndicator[j] = new CParticle(1,
					(int) (57f * this.mainGame.scalePos.y),
					(int) (57f * this.mainGame.scalePos.y));
		}
		this.freePlayMenu = new CMenu(this.mainGame, 2);
		this.freePlayMenu
				.setMenuItem(
						0,
						new Vector2f(126f, 350f),
						LTextures
								.loadTexture("assets\\menu\\buttons\\buttonContinue.png"),
						1.7f);
		this.freePlayMenu.setMenuItem(1, new Vector2f(419f, 350f), LTextures
				.loadTexture("assets\\menu\\buttons\\buttonChooseLevel.png"),
				1.7f);
		this.pauseMenu = new CMenu(this.mainGame, 2);
		this.pauseMenu
				.setMenuItem(
						0,
						new Vector2f(126f, 350f),
						LTextures
								.loadTexture("assets\\menu\\buttons\\buttonContinue.png"),
						1.7f);
		this.pauseMenu.setMenuItem(1, new Vector2f(419f, 350f), LTextures
				.loadTexture("assets\\menu\\buttons\\buttonChooseLevel.png"),
				1.7f);
		this.gameOverTextPos = new Vector2f(535f * this.mainGame.scalePos.y,
				44f * this.mainGame.scalePos.y);
		this.waveReachedPos = new Vector2f(275f * this.mainGame.scalePos.y,
				150f * this.mainGame.scalePos.y);
		this.levelUnlockedPos = new Vector2f(300f * this.mainGame.scalePos.y,
				200f * this.mainGame.scalePos.y);
		this.failedEnemysPos = new Vector2f(100f * this.mainGame.scalePos.y,
				100f * this.mainGame.scalePos.y);
		this.killedEnemysPos = new Vector2f(100f * this.mainGame.scalePos.y,
				150f * this.mainGame.scalePos.y);
		this.wavesCompletedPos = new Vector2f(100f * this.mainGame.scalePos.y,
				200f * this.mainGame.scalePos.y);
		this.gameTimePos = new Vector2f(100f * this.mainGame.scalePos.y,
				250f * this.mainGame.scalePos.y);
		this.spentMoneyPos = new Vector2f(100f * this.mainGame.scalePos.y,
				300f * this.mainGame.scalePos.y);
		this.highScorePos = new Vector2f(270f * this.mainGame.scalePos.y,
				250f * this.mainGame.scalePos.y);
		this.failedEnemysPos2 = new Vector2f(330f * this.mainGame.scalePos.y,
				100f * this.mainGame.scalePos.y);
		this.killedEnemysPos2 = new Vector2f(330f * this.mainGame.scalePos.y,
				150f * this.mainGame.scalePos.y);
		this.wavesCompletedPos2 = new Vector2f(330f * this.mainGame.scalePos.y,
				200f * this.mainGame.scalePos.y);
		this.gameTimePos2 = new Vector2f(330f * this.mainGame.scalePos.y,
				250f * this.mainGame.scalePos.y);
		this.spentMoneyPos2 = new Vector2f(330f * this.mainGame.scalePos.y,
				300f * this.mainGame.scalePos.y);
		this.highScorePos2 = new Vector2f(450f * this.mainGame.scalePos.y,
				250f * this.mainGame.scalePos.y);
		this.highScorePos2_1 = new Vector2f(100f * this.mainGame.scalePos.y,
				350f * this.mainGame.scalePos.y);
		this.highScorePos2_2 = new Vector2f(330f * this.mainGame.scalePos.y,
				350f * this.mainGame.scalePos.y);
		this.MoneyPos = new Vector2f(30f, 10f);
		this.WavePos = new Vector2f(220f, 10f);
		this.waveTimePos = new Vector2f(520f, 10f);
		this.livesPos = new Vector2f(390f, 10f);
		this.textShadowOffset = new Vector2f(2f, 2f);
		this.fastForwardRect = new RectBox(0x2e0, 0x1a0, 0x40, 0x40);
		this.towerIcons = new CTowerIcons(this.mainGame, this.positionSquare);
		this.towerOptionsIcons = new CTowerIcons(this.mainGame);
		this.tmpLColor = LColor.white;
		this.pausePos = new Vector2f(336f * this.mainGame.scalePos.y,
				150f * this.mainGame.scalePos.y);
		this.trialPos = new Vector2f(213f * this.mainGame.scalePos.y,
				120f * this.mainGame.scalePos.y);
	}

	public final void loadGameWave(String filename) {

	}

	public final void reportDestroyEnemy(CEnemy enemy, boolean noBounty) {
		if (!noBounty) {
			this.money += enemy.bounty;
			this.mainGame.statistics.addKilledEnemys();
			this.moneyBonus += enemy.bounty;
			this.killedEnemy++;
		} else {
			this.lostEnemy++;
			this.fastForward = false;
			this.mainGame.statistics.addFailedEnemys();
		}
		for (int i = 0; i < this.maxTower; i++) {
			if (this.tower[i].isThere) {
				this.tower[i].clearTarget(enemy);
			}
		}
		if (enemy == this.targedEnemy) {
			this.targedEnemy = null;
		}
	}

	public final void reset() {
		this.targedEnemy = null;
		this.newWaveTimer = this.newWaveTime;
		this.initNewWave(1);
		this.lives = 10;
		this.money = this.startMoney;
		this.killedEnemy = 0;
		this.lostEnemy = 0;
		this.moneyBonus = 0;
		this.gameTime = 0f;
		this.towerIcons.hideIcons();
		this.towerOptionsIcons.hideIcons();
		this.freeMode = false;
		this.pause = false;
		this.levelDone = false;
		this.showTrial = false;
		for (int i = 0; i < this.maxTower; i++) {
			this.tower[i].delete();
		}
	}

	public final void saveGameWave(String filename) {

	}

	public final void setLevelDone() {
		this.levelDone = true;
		int num = this.killedEnemy * 20;
		int num2 = -this.lostEnemy * 20;
		int num3 = this.moneyBonus * 10;
		int num4 = this.wave * 600;
		this.score = ((num + num2) + num3) + num4;
		this.lastHighscore = -1;
		this.nextLevelUnlocked = false;
		if (!this.freeMode) {
			if (this.score > this.mainGame.level[this.mainGame.currentLevel].highscore) {
				this.lastHighscore = this.mainGame.level[this.mainGame.currentLevel].highscore;
				this.mainGame.level[this.mainGame.currentLevel].highscore = this.score;
			}
			if ((((this.wave - 1) == this.mainGame.level[this.mainGame.currentLevel].maxWaves) && (this.mainGame.currentLevel < (this.mainGame.levels - 1)))
					&& (!this.mainGame.isOSUI && this.mainGame.level[this.mainGame.currentLevel + 1].locked)) {
				this.nextLevelUnlocked = true;
				this.mainGame.level[this.mainGame.currentLevel + 1].locked = false;
			}
		}
		if (this.wave > this.mainGame.level[this.mainGame.currentLevel].maxWave) {
			this.mainGame.level[this.mainGame.currentLevel].maxWave = this.wave;
		}
	}

	private ActionKey action = new ActionKey(
			ActionKey.DETECT_INITIAL_PRESS_ONLY);

	public final void update(float time) {
		int num9;
		if (this.mainGame.globalScreenTimer >= 0.1) {
			if (this.mainGame.isPressedBackOrB() && this.showTrial) {
				this.mainGame
						.switchGameMode(MainGame.EGMODE.GMODE_LEVELCHOOSER);
				this.showTrial = false;
				return;
			}
			if (!pause && Key.isKeyPressed(Key.BACK) && !this.levelDone) {
				if (!action.isPressed()) {
					if (!this.pause) {
						this.pause = true;
					} else {
						this.pause = false;
					}
					action.release();
				} else {
					action.press();
				}
			}
			if (!this.pause) {
				int x;
				int y;
				RectBox rectangle;
				boolean flag;
				if (this.showTrial) {
					this.pauseMenu.update(time);
					if (this.pauseMenu.selectedItem != -1) {
						if (this.pauseMenu.ready) {
							switch (this.pauseMenu.selectedItem) {
							case 0:
								this.pauseMenu.reset();
								this.mainGame
										.switchGameMode(MainGame.EGMODE.GMODE_MENU);
								this.showTrial = false;
								return;

							case 1:
								this.pauseMenu.reset();
								this.mainGame
										.switchGameMode(MainGame.EGMODE.GMODE_LEVELCHOOSER);
								this.showTrial = false;
								return;
							}
						}
						return;
					}
					if (this.mainGame.currentToucheState.AnyTouch()
							&& !this.mainGame.previouseToucheState.AnyTouch()) {
						if (this.mainGame.getCurrentTouchPos().y < 350f) {

						}
						return;
					}
				}
				if (!this.levelDone) {
					this.mainGame.statistics.addgameTime(time);
					this.gameTime += time;
					if (this.towerOptionsIcons.visible) {
						this.towerOptionsIcons.update(time);
						this.towerOptionsIcons
								.setText(
										0,
										""
												+ this.tower[this.selectedTower]
														.getSellMoney());
						if (this.tower[this.selectedTower].level < this.tower[this.selectedTower].type.maxLevel) {
							this.towerOptionsIcons
									.setText(
											1,
											""
													+ this.tower[this.selectedTower].type.cost[this.tower[this.selectedTower].level]);
						}
						if (this.tower[this.selectedTower].level == this.tower[this.selectedTower].type.maxLevel) {
							this.towerOptionsIcons.textures[1] = this.iconMaxUpgrade;
						} else if (this.money < this.tower[this.selectedTower].type.cost[this.tower[this.selectedTower].level]) {
							this.towerOptionsIcons.textures[1] = this.iconNoUpgrade;
						} else {
							this.towerOptionsIcons.textures[1] = this.iconUpgrade;
						}
					}
					if (this.towerIcons.visible) {
						this.towerIcons.update(time);
						if (this.TowerType1.cost[0] > this.money) {
							this.towerIcons.textures[0] = this.TowerType1.iconTexture2;
						} else {
							this.towerIcons.textures[0] = this.TowerType1.iconTexture;
						}
						if (this.TowerType2.cost[0] > this.money) {
							this.towerIcons.textures[1] = this.TowerType2.iconTexture2;
						} else {
							this.towerIcons.textures[1] = this.TowerType2.iconTexture;
						}
						if (this.TowerType3.cost[0] > this.money) {
							this.towerIcons.textures[2] = this.TowerType3.iconTexture2;
						} else {
							this.towerIcons.textures[2] = this.TowerType3.iconTexture;
						}
						if (this.TowerType4.cost[0] > this.money) {
							this.towerIcons.textures[3] = this.TowerType4.iconTexture2;
						} else {
							this.towerIcons.textures[3] = this.TowerType4.iconTexture;
						}
						if (this.TowerType5.cost[0] > this.money) {
							this.towerIcons.textures[4] = this.TowerType5.iconTexture2;
						} else {
							this.towerIcons.textures[4] = this.TowerType5.iconTexture;
						}
					}
					if (!this.mainGame.previouseToucheState.AnyTouch()
							|| this.mainGame.currentToucheState.AnyTouch()) {

						num9 = this.fastForward ? 2 : 3;
						while (num9 < 4) {
							for (int n = 0; n < 50; n++) {
								if (this.damageIndicator[n].isThere) {
									this.damageIndicator[n].update(time * 6f);
								}
							}
							if (this.stopAlpha > 0f) {
								this.stopAlpha -= time * 2f;
								if (this.stopAlpha < 0f) {
									this.stopAlpha = 0f;
								}
							}
							if (this.mainGame.isOSUI && (this.wave > 40)) {
								this.showTrial = true;
								return;
							}
							if (this.newWaveTimer <= 0f) {
								if (this.allEnemydeath
										&& (this.neededSpawnedEnemys == 0)) {
									this.wave++;
									if ((this.wave > this.maxWaves)
											&& !this.freeMode) {
										this.setLevelDone();
										this.mainGame.statistics.addLevelWin();
									} else {
										this.newWaveTimer = this.newWaveTime;
										this.initNewWave(this.wave);
									}
									this.mainGame.statistics
											.addWavesCompleted();
									return;
								}
								if (this.neededSpawnedEnemys > 0) {
									if (this.addEnemyTimer <= 0f) {
										for (int num11 = 0; num11 < this.currentWaveMaxEnemy; num11++) {
											if (!this.enemy[num11].isThere) {
												this.enemy[num11]
														.init(this.enemysLevelArray[this.currentLevelArrayIndex],
																this.currentLevelArrayIndex);
												this.currentLevelArrayIndex++;
												this.neededSpawnedEnemys--;
												break;
											}
										}
										this.addEnemyTimer = this.addEnemyTime;
									} else {
										this.addEnemyTimer -= time;
									}
								}
								this.allEnemydeath = true;
								for (int num12 = 0; num12 < this.currentWaveMaxEnemy; num12++) {
									if (this.enemy[num12].isThere) {
										this.allEnemydeath = false;
										this.enemy[num12].update(time);
									}
								}
							} else {
								this.newWaveTimer -= time;
								if (this.newWaveTimer < 0f) {
									this.newWaveTimer = 0f;
								}
							}
							if (this.lives <= 0) {
								this.lives = 0;
								this.setLevelDone();
								this.mainGame.statistics.addLevelLosed();
								return;
							}
							for (int num13 = 0; num13 < this.maxTower; num13++) {
								this.tower[num13].update(time);
							}
							num9++;
						}
						this.revolverSnd.update();
						this.pfanneSnd.update();
						this.RocketSnd.update();
						this.speedReducerSnd.update();
						this.electroSnd.update();
						return;
					}
					LTouchLocation location = this.mainGame.currentToucheState
							.get(0);
					LTouchLocation location2 = this.mainGame.currentToucheState
							.get(0);
					if (this.fastForwardRect.intersects(new RectBox(
							(int) location.getPosition().x, (int) location2
									.getPosition().y, 1, 1))) {
						this.fastForward = !this.fastForward;
						return;
					}
					LTouchLocation location3 = this.mainGame.previouseToucheState
							.get(0);
					x = (int) location3.getPosition().x;
					LTouchLocation location4 = this.mainGame.previouseToucheState
							.get(0);
					y = (int) location4.getPosition().y;
					rectangle = new RectBox(x
							+ ((int) (this.mainGame.TILESIZE.x / 2f)), y
							+ ((int) (this.mainGame.TILESIZE.y / 2f)), 5, 5);
					flag = false;
					for (int k = 0; k < this.currentWaveMaxEnemy; k++) {
						if (this.enemy[k].isThere
								&& this.enemy[k].Intersects(rectangle)) {
							if (this.targedEnemy != this.enemy[k]) {
								this.targedEnemy = this.enemy[k];
								flag = true;
							} else {
								flag = true;
								this.targedEnemy = null;
							}
							break;
						}
					}
				} else {
					if (this.lives <= 0) {
						if (this.mainGame.currentToucheState.AnyTouch()
								&& !this.mainGame.previouseToucheState
										.AnyTouch()) {
							this.mainGame
									.switchGameMode(MainGame.EGMODE.GMODE_LEVELCHOOSER);
							this.freePlayMenu.reset();
						}
						if (this.mainGame.isPressedBackOrB()) {
							this.mainGame
									.switchGameMode(MainGame.EGMODE.GMODE_LEVELCHOOSER);
							this.freePlayMenu.reset();
						}
						return;
					}
					if (this.mainGame.isPressedBackOrB()) {
						this.saveGameWave(this.mainGame.level[this.mainGame.currentLevel].filename);
						this.mainGame
								.switchGameMode(MainGame.EGMODE.GMODE_LEVELCHOOSER);
						this.freeMode = true;
						this.freePlayMenu.reset();
						return;
					}
					this.freePlayMenu.update(time);
					if (this.freePlayMenu.selectedItem != -1) {
						if (!this.freePlayMenu.ready) {
							return;
						}
						switch (this.freePlayMenu.selectedItem) {
						case 0:
							this.freeMode = true;
							this.freePlayMenu.reset();
							this.levelDone = false;
							this.newWaveTimer = this.newWaveTime;
							this.initNewWave(this.wave);
							return;

						case 1:
							this.freePlayMenu.reset();
							this.levelDone = false;
							this.freeMode = true;
							this.saveGameWave(this.mainGame.level[this.mainGame.currentLevel].filename);
							if (this.wave > this.mainGame.level[this.mainGame.currentLevel].maxWave) {
								this.mainGame.level[this.mainGame.currentLevel].maxWave = this.wave;
							}
							this.mainGame
									.switchGameMode(MainGame.EGMODE.GMODE_LEVELCHOOSER);
							return;

						default:
							return;
						}
					}
					return;
				}
				if (this.towerOptionsIcons.visible) {
					LTouchLocation location5 = this.mainGame.previouseToucheState
							.get(0);
					switch (this.towerOptionsIcons.checkClick(location5
							.getPosition())) {
					case 0:
						this.money += this.tower[this.selectedTower]
								.getSellMoney();
						this.towerOptionsIcons.hideIcons();
						this.tower[this.selectedTower].delete();
						this.selectedTower = -1;
						this.targedEnemy = null;
						return;

					case 1:
						if ((this.tower[this.selectedTower].level < this.tower[this.selectedTower].type.maxLevel)
								&& (this.money >= this.tower[this.selectedTower].type.cost[this.tower[this.selectedTower].level])) {
							this.money -= this.tower[this.selectedTower].type.cost[this.tower[this.selectedTower].level];
							this.mainGame.statistics
									.addSpentMoney(this.tower[this.selectedTower].type.cost[this.tower[this.selectedTower].level]);
							this.spentMoney += this.tower[this.selectedTower].type.cost[this.tower[this.selectedTower].level];
							this.tower[this.selectedTower].upgrade();
							if (this.tower[this.selectedTower].level != this.tower[this.selectedTower].type.maxLevel) {
								this.towerOptionsIcons
										.setText(
												1,
												""
														+ this.tower[this.selectedTower].type.cost[this.tower[this.selectedTower].level]);
							} else {
								this.towerOptionsIcons.setText(1, null);
							}
							this.towerOptionsIcons.hideIcons();
							this.targedEnemy = null;
						}
						return;
					}
					this.towerOptionsIcons.hideIcons();
					this.tower[this.selectedTower].disable();
					this.selectedTower = -1;
				} else if (!this.towerIcons.visible) {
					if (!flag) {
						x -= x % ((int) this.mainGame.TILESIZE.x);
						y -= y % ((int) this.mainGame.TILESIZE.y);
						if ((((((float) y) / this.mainGame.TILESIZE.x) < this.mainGame.MAXTILES.y) && ((((float) x) / this.mainGame.TILESIZE.y) < this.mainGame.MAXTILES.x))
								&& (((((float) y) / this.mainGame.TILESIZE.y) >= 0f) && ((((float) x) / this.mainGame.TILESIZE.x) >= 0f))) {
							if (this.levelArray[x
									/ ((int) this.mainGame.TILESIZE.x)][y
									/ ((int) this.mainGame.TILESIZE.y)] == 0) {
								this.towerIcons.showIcons(new LTexture[] {
										this.TowerType1.iconTexture,
										this.TowerType2.iconTexture,
										this.TowerType3.iconTexture,
										this.TowerType3.iconTexture,
										this.TowerType4.iconTexture }, 5, x, y);
								if (!this.towerIcons.isText(0)) {
									this.towerIcons.setText(0, ""
											+ this.TowerType1.cost[0]);
									this.towerIcons.setText(1, ""
											+ this.TowerType2.cost[0]);
									this.towerIcons.setText(2, ""
											+ this.TowerType3.cost[0]);
									this.towerIcons.setText(3, ""
											+ this.TowerType4.cost[0]);
									this.towerIcons.setText(4, ""
											+ this.TowerType5.cost[0]);
								}
								if (this.TowerType1.cost[0] > this.money) {
									this.towerIcons.textures[0] = this.TowerType1.iconTexture2;
								} else {
									this.towerIcons.textures[0] = this.TowerType1.iconTexture;
								}
								if (this.TowerType2.cost[0] > this.money) {
									this.towerIcons.textures[1] = this.TowerType2.iconTexture2;
								} else {
									this.towerIcons.textures[1] = this.TowerType2.iconTexture;
								}
								if (this.TowerType3.cost[0] > this.money) {
									this.towerIcons.textures[2] = this.TowerType3.iconTexture2;
								} else {
									this.towerIcons.textures[2] = this.TowerType3.iconTexture;
								}
								if (this.TowerType4.cost[0] > this.money) {
									this.towerIcons.textures[3] = this.TowerType4.iconTexture2;
								} else {
									this.towerIcons.textures[3] = this.TowerType4.iconTexture;
								}
								if (this.TowerType5.cost[0] > this.money) {
									this.towerIcons.textures[4] = this.TowerType5.iconTexture2;
								} else {
									this.towerIcons.textures[4] = this.TowerType5.iconTexture;
								}
							} else if (this.levelArray[x
									/ ((int) this.mainGame.TILESIZE.x)][y
									/ ((int) this.mainGame.TILESIZE.y)] != 2) {
								this.stopVector.x = (x + (this.mainGame.TILESIZE.x / 2f))
										- (this.stopTexture.getWidth() / 2);
								this.stopVector.y = (y + (this.mainGame.TILESIZE.y / 2f))
										- (this.stopTexture.getHeight() / 2);
								this.stopAlpha = 1f;
							}
						}
					}
				} else {
					this.towerIcons.hideIcons();
					LTouchLocation location6 = this.mainGame.previouseToucheState
							.get(0);
					int num5 = this.towerIcons.checkClick(location6
							.getPosition());
					if (num5 != -1) {
						this.targedEnemy = null;
						for (int m = 0; m < this.maxTower; m++) {
							if (this.tower[m].isThere) {
								continue;
							}
							boolean flag2 = true;
							switch (num5) {
							case 0:
								if (this.TowerType1.cost[0] <= this.money) {
									this.tower[m].init(this.TowerType1);
									this.money -= this.TowerType1.cost[0];
									this.mainGame.statistics
											.addSpentMoney(this.TowerType1.cost[0]);
									this.spentMoney += this.TowerType1.cost[0];
									flag2 = false;
								}
								break;

							case 1:
								if (this.TowerType2.cost[0] <= this.money) {
									this.tower[m].init(this.TowerType2);
									this.money -= this.TowerType2.cost[0];
									this.mainGame.statistics
											.addSpentMoney(this.TowerType2.cost[0]);
									this.spentMoney += this.TowerType2.cost[0];
									flag2 = false;
								}
								break;

							case 2:
								if (this.TowerType3.cost[0] <= this.money) {
									this.tower[m].init(this.TowerType3);
									this.money -= this.TowerType3.cost[0];
									this.mainGame.statistics
											.addSpentMoney(this.TowerType3.cost[0]);
									this.spentMoney += this.TowerType3.cost[0];
									flag2 = false;
								}
								break;

							case 3:
								if (this.TowerType4.cost[0] <= this.money) {
									this.tower[m].init(this.TowerType4);
									this.money -= this.TowerType4.cost[0];
									this.mainGame.statistics
											.addSpentMoney(this.TowerType4.cost[0]);
									this.spentMoney += this.TowerType4.cost[0];
									flag2 = false;
								}
								break;

							case 4:
								if (this.TowerType5.cost[0] <= this.money) {
									this.tower[m].init(this.TowerType5);
									this.money -= this.TowerType5.cost[0];
									this.mainGame.statistics
											.addSpentMoney(this.TowerType5.cost[0]);
									this.spentMoney += this.TowerType5.cost[0];
									flag2 = false;
								}
								break;
							}
							if (flag2) {
								break;
							}
							this.tower[m].setPos(this.towerIcons.pos.x,
									this.towerIcons.pos.y);
							this.selectedTower = m;
							return;
						}
						return;
					}
				}
				for (int i = 0; i < this.maxTower; i++) {
					if (this.tower[i].isActivated) {
						if (this.tower[i].checkClick(rectangle)) {
							this.towerOptionsIcons.showIcons(new LTexture[] {
									this.iconSellTexture, this.iconUpgrade },
									2, (int) this.tower[i].pos.x,
									(int) this.tower[i].pos.y);
							if (this.tower[i].level != this.tower[i].type.maxLevel) {
								this.towerOptionsIcons
										.setText(
												1,
												""
														+ this.tower[i].type.cost[this.tower[i].level]);
								return;
							}
							this.towerOptionsIcons.setText(1, null);
							return;
						}
						this.tower[i].disable();
						this.selectedTower = -1;
					}
				}
				for (int j = 0; j < this.maxTower; j++) {
					if (this.tower[j].checkClick(rectangle)) {
						this.towerOptionsIcons.showIcons(new LTexture[] {
								this.iconSellTexture, this.iconUpgrade }, 2,
								(int) this.tower[j].pos.x,
								(int) this.tower[j].pos.y);
						if (this.tower[j].level != this.tower[j].type.maxLevel) {
							this.towerOptionsIcons
									.setText(
											1,
											""
													+ this.tower[j].type.cost[this.tower[j].level]);
						} else {
							this.towerOptionsIcons.setText(1, null);
						}
						this.tower[j].activate();
						this.selectedTower = j;
						if (this.towerIcons.visible) {
							this.towerIcons.hideIcons();
						}
						return;
					}
					this.tower[j].disable();
				}
				num9 = this.fastForward ? 2 : 3;
				while (num9 < 4) {
					for (int n = 0; n < 50; n++) {
						if (this.damageIndicator[n].isThere) {
							this.damageIndicator[n].update(time * 6f);
						}
					}
					if (this.stopAlpha > 0f) {
						this.stopAlpha -= time * 2f;
						if (this.stopAlpha < 0f) {
							this.stopAlpha = 0f;
						}
					}
					if (this.mainGame.isOSUI && (this.wave > 40)) {
						this.showTrial = true;
						return;
					}
					if (this.newWaveTimer <= 0f) {
						if (this.allEnemydeath
								&& (this.neededSpawnedEnemys == 0)) {
							this.wave++;
							if ((this.wave > this.maxWaves) && !this.freeMode) {
								this.setLevelDone();
								this.mainGame.statistics.addLevelWin();
							} else {
								this.newWaveTimer = this.newWaveTime;
								this.initNewWave(this.wave);
							}
							this.mainGame.statistics.addWavesCompleted();
							return;
						}
						if (this.neededSpawnedEnemys > 0) {
							if (this.addEnemyTimer <= 0f) {
								for (int num11 = 0; num11 < this.currentWaveMaxEnemy; num11++) {
									if (!this.enemy[num11].isThere) {
										this.enemy[num11]
												.init(this.enemysLevelArray[this.currentLevelArrayIndex],
														this.currentLevelArrayIndex);
										this.currentLevelArrayIndex++;
										this.neededSpawnedEnemys--;
										break;
									}
								}
								this.addEnemyTimer = this.addEnemyTime;
							} else {
								this.addEnemyTimer -= time;
							}
						}
						this.allEnemydeath = true;
						for (int num12 = 0; num12 < this.currentWaveMaxEnemy; num12++) {
							if (this.enemy[num12].isThere) {
								this.allEnemydeath = false;
								this.enemy[num12].update(time);
							}
						}
					} else {
						this.newWaveTimer -= time;
						if (this.newWaveTimer < 0f) {
							this.newWaveTimer = 0f;
						}
					}
					if (this.lives <= 0) {
						this.lives = 0;
						this.setLevelDone();
						this.mainGame.statistics.addLevelLosed();
						return;
					}
					for (int num13 = 0; num13 < this.maxTower; num13++) {
						this.tower[num13].update(time);
					}
					num9++;
				}
				this.revolverSnd.update();
				this.pfanneSnd.update();
				this.RocketSnd.update();
				this.speedReducerSnd.update();
				this.electroSnd.update();
				return;
			}
			this.pauseMenu.update(time);
			if ((this.pauseMenu.selectedItem == -1) || !this.pauseMenu.ready) {
				return;
			}
			switch (this.pauseMenu.selectedItem) {
			case 0:
				this.pauseMenu.reset();
				this.pause = false;
				return;

			case 1:
				this.pauseMenu.reset();
				this.saveGameWave(this.mainGame.level[this.mainGame.currentLevel].filename);
				if (this.wave > this.mainGame.level[this.mainGame.currentLevel].maxWave) {
					this.mainGame.level[this.mainGame.currentLevel].maxWave = this.wave;
				}
				this.mainGame
						.switchGameMode(MainGame.EGMODE.GMODE_LEVELCHOOSER);
				return;
			}
		}
		return;

	}
}