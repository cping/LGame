package com.zombiedefence.free;

import java.util.ArrayList;
import java.util.Calendar;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.timer.GameTime;

public class ScreenGameplay extends Screen {
	private float accuracy;
	public Bunker AIBunker;
	private java.util.ArrayList<Bunker> AIBunkerList;
	public java.util.ArrayList<ArtilleryShell> artilleryShellList;
	private Barrier barrier;
	private java.util.ArrayList<BloodSpill> bloodSpillList;
	private java.util.ArrayList<BloodStain> bloodStainList;
	private Bombardment bombardment;
	private java.util.ArrayList<Bullet> bulletList;
	private java.util.ArrayList<BulletShell> bulletShellList;
	public Bunker bunker;
	private Button buttonArtillery;
	private Button buttonNext;
	private ControlPane controlPane;
	public static int day;
	private float demoAlpha;
	private java.util.ArrayList<Explosion> explosionList;
	private GameSave gameSave;
	private java.util.ArrayList<Grenade> grenadeList;
	public static SoundEffect grenadeSound;
	private IndLevelup indLevelup;
	public static boolean isDemoMode;
	public static boolean isToBeDeleted;
	public static int level;
	private int levelLength;
	private Vector2f[] mercenaryPosition;
	private java.util.ArrayList<Mud> mudList;
	private int numHeadShot;
	private int numKill;
	public static SoundEffect pistolSound;
	public static Random rand;
	public static SoundEffect reloadSound;
	public static SoundEffect rifleSound;
	public static int score;
	private ScoreBoard scoreBoard;
	public static SoundEffect soundBAR;
	public static SoundEffect soundBombDrop;
	public static SoundEffect soundBrowning;
	public static SoundEffect soundNambu;
	public static SoundEffect soundPPSH41;
	public static SoundEffect soundSVT40;
	public static SoundEffect soundThompson;
	public static SoundEffect soundVickers;
	public static SoundEffect soundWebley;
	public static SoundEffect soundWinchester;
	public static SoundEffect soundZombie1;
	public static LTexture t2DArtilleryShell;
	public static LTexture t2DBarrierBroken;
	public static LTexture t2DBarrierHealthBar;
	public static LTexture t2DBarrierHealthLiquid;
	public static LTexture t2DBarrierOriginal;
	public static LTexture t2DBloodSpill;
	public static LTexture t2DBloodSpillBack;
	public static LTexture t2DBloodStain;
	public static LTexture t2DBullet;
	public static LTexture t2DBulletDisplay;
	public static LTexture t2DBulletLine;
	public static LTexture t2DBulletShell;
	public static LTexture t2DBunkerAA;
	public static LTexture t2DBunkerBottom;
	public static LTexture t2DBunkerTop;
	public static LTexture t2DButtonArtillery;
	public static LTexture t2DButtonArtilleryMask;
	public static LTexture t2DButtonBack;
	public static LTexture t2DButtonNext;
	public static LTexture t2DButtonSkill;
	public static LTexture t2DControlPane;
	public static LTexture t2DControlPaneBG;
	public static LTexture t2DCover;
	public static LTexture t2DExpBar;
	public static LTexture t2DExpLiquid;
	public static LTexture t2DExplosion;
	public static LTexture t2DFireButton;
	public static LTexture t2DFiringSpark;
	public static LTexture t2DGrenade;
	public static LTexture t2DGunInField;
	public static LTexture t2DHandle;
	public static LTexture t2DHead;
	public static LTexture t2DHead2;
	public static LTexture t2DIndLevelup;
	public static LTexture t2DLowArm;
	public static LTexture t2DLowArm2;
	public static LTexture t2DLowLeg;
	public static LTexture t2DLowLeg2;
	public static LTexture t2DMarker;
	public static LTexture t2DMarker1;
	public static LTexture t2DMud;
	public static LTexture t2DRuler;
	public static LTexture t2DScoreBoard;
	public static LTexture t2DShadow;
	public static LTexture t2DSmoke;
	public static LTexture t2DTagLevelUp;
	public static LTexture t2DTNT;
	public static LTexture t2DTorso;
	public static LTexture t2DTorso2;
	public static LTexture t2DUppArm;
	public static LTexture t2DUppArm2;
	public static LTexture t2DUppLeg;
	public static LTexture t2DUppLeg2;
	private java.util.ArrayList<TagLevelUp> tagLevelUpList;
	public static SoundEffect ZhongZhengSound;
	public static float zombieBirthRate;
	private java.util.ArrayList<Zombie> zombieList;

	public ScreenGameplay() {
		super.screenPause = new ScreenPause(this, Help.GameScreen.Gameplay);
		this.levelLength = 0x960;
		zombieBirthRate = 0.009000001f;
		if (isDemoMode) {
			zombieBirthRate = 0.06566667f;
		}
		this.numHeadShot = 0;
		this.numKill = 0;
		this.accuracy = 0f;
		score = 0;
		day = 1;
		level = 0;
		isToBeDeleted = false;
		isDemoMode = false;
		this.demoAlpha = 1f;
		this.gameSave = new GameSave();
		this.mercenaryPosition = new Vector2f[] { new Vector2f(760f, 250f),
				new Vector2f(760f, 280f), new Vector2f(760f, 360f),
				new Vector2f(760f, 390f), new Vector2f(760f, 420f) };
	}

	public final void CollisionDetection() {
		ArrayList<Zombie> list = new ArrayList<Zombie>();
		Zombie zombie = null;
		int num = 0;
		for (Bullet bullet : this.bulletList) {
			list.clear();
			for (Zombie zombie2 : this.zombieList) {
				num = ((int) bullet.position.y)
						- ((int) (Math.tan((double) bullet.angle) * (bullet.position.x - zombie2.position.x)));
				if (((num < zombie2.positionGround.y) && (num > ((zombie2.position.y - zombie2.torso.texture
						.getHeight()) - zombie2.head.texture.getHeight())))
						&& !zombie2.isFalling) {
					zombie2.shotH = num;
					zombie2.incomingBulletAngle = bullet.angle;
					list.add(zombie2);
				}
			}
			if (list.size() > 0) {
				zombie = list.get(0);
				for (Zombie zombie3 : list) {
					if ((zombie3.position.x > zombie.position.x)
							&& !zombie3.isFalling) {
						zombie = zombie3;
					}
				}
				if (zombie != null) {
					zombie.isShot = true;
					zombie.TakeShot(bullet.power, true, zombie.shotH);
					this.bloodSpillList.add(new BloodSpill(t2DBloodSpillBack,
							new Vector2f(zombie.position.x,
									(float) zombie.shotH),
							zombie.incomingBulletAngle));
					this.bloodStainList.add(new BloodStain(t2DBloodStain,
							new Vector2f(zombie.position.x,
									zombie.positionGround.y - 20f),
							zombie.incomingBulletAngle));
				}
			}
		}
		for (Explosion explosion : this.explosionList) {
			if (explosion.isActive) {
				for (Zombie zombie4 : this.zombieList) {
					if (Vector2f.dst(zombie4.position, explosion.position) < explosion.radius) {
						zombie4.TakeShot(explosion.damage,
								explosion.position.x > zombie4.position.x, 0);
						this.bloodStainList.add(new BloodStain(t2DBloodStain,
								new Vector2f(zombie4.position.x,
										zombie4.positionGround.y - 20f),
								zombie4.incomingBulletAngle));
					}
				}
				if ((this.barrier.health > 0f)
						&& ((this.barrier.position.x - explosion.position.x) < 30f)) {
					this.barrier.health -= explosion.damage;
				}
				explosion.isActive = false;
			}
		}
		for (Zombie zombie5 : this.zombieList) {
			if (((this.barrier.position.x - zombie5.position.x) < 30f)
					&& (this.barrier.health > 0f)) {
				if (!zombie5.isWithTNT) {
					zombie5.isAttacking = true;
					this.barrier.health -= zombie5.damage;
				} else {
					zombie5.Explode();
					this.explosionList.add(new Explosion(t2DExplosion,
							zombie5.position, 100,
							((int) (rand.NextDouble() * 20.0)) + 20));
					grenadeSound.Play();
				}
				continue;
			}
			zombie5.isAttacking = false;
		}
	}

	@Override
	public void Draw(SpriteBatch batch) {
		super.Draw(batch);
		if (!super.isPaused) {
			batch.drawString(Screen.ariel18, (new Integer(day)).toString(),
					466f, 78f, LColor.white);
			batch.drawString(Screen.ariel18, (new Integer(level)).toString(),
					374f, 78f, LColor.white);
			batch.draw(t2DBarrierHealthBar, 630f, 160f, null, LColor.white, 0f,
					0f, 0f, 1f, SpriteEffects.None);
			batch.draw(t2DBarrierHealthLiquid, 630f, 160f, null, LColor.white,
					0f, 0f, 0f, this.barrier.health / Help.barrierHMax, 1f,
					SpriteEffects.None);
			batch.drawString(Screen.ariel18, (new Integer(
					(int) this.barrier.health)).toString(), 645f, 159f,
					LColor.wheat);
			batch.draw(t2DExpBar, 10f, 195f, null, LColor.white, 0f, 0f, 0f,
					1f, 1f, SpriteEffects.None);
			batch.draw(
					t2DExpLiquid,
					10f,
					195f,
					null,
					LColor.white,
					0f,
					0f,
					0f,

					(score - ((Help.initialPromoteThreshold * level) * ((float) Math
							.pow((double) Help.expMultiplyFactor,
									(double) level))))
							/ (((Help.initialPromoteThreshold * (level + 1f)) * ((float) Math
									.pow((double) Help.expMultiplyFactor,
											(double) (level + 1)))) - ((Help.initialPromoteThreshold * level) * ((float) Math
									.pow((double) Help.expMultiplyFactor,
											(double) level)))), 1f,
					SpriteEffects.None);
			this.barrier.Draw(batch);
			for (TagLevelUp up : this.tagLevelUpList) {
				up.Draw(batch);
			}
			for (BloodStain stain : this.bloodStainList) {
				stain.Draw(batch);
			}
			for (Zombie zombie : this.zombieList) {
				zombie.Draw(batch);
			}
			for (Grenade grenade : this.grenadeList) {
				grenade.Draw(batch);
			}
			for (Explosion explosion : this.explosionList) {
				explosion.Draw(batch);
			}
			for (Mud mud : this.mudList) {
				mud.Draw(batch);
			}
			this.bunker.Draw(batch);
			this.indLevelup.Draw(batch);
			batch.drawString(Screen.ariel18, Help.AvailSkillPoint + "", 71f,
					160f, LColor.white);
			batch.draw(t2DFireButton, 0f, 420f, null, LColor.white, 0f, 0f, 0f,
					1f, 1f, SpriteEffects.None);
			if (this.buttonArtillery != null) {
				batch.draw(
						t2DButtonArtilleryMask,
						this.buttonArtillery.position
								.sub(this.buttonArtillery.origin),
						null,
						Global.Pool.getColor(0.6f, 0.6f, 0.6f, 0.6f),
						0f,
						new Vector2f(0f, 0f),
						new Vector2f(
								1f,
								1f - (((float) this.bunker.iCoolDown) / ((float) this.bunker.artilleryCoolDown))),
						SpriteEffects.None);
			}
			for (Bunker bunker : this.AIBunkerList) {
				bunker.Draw(batch);
			}
			this.controlPane.Draw(batch);
			for (Bullet bullet : this.bulletList) {
				bullet.Draw(batch);
			}
			for (BulletShell shell : this.bulletShellList) {
				shell.Draw(batch);
			}
			for (BloodSpill spill : this.bloodSpillList) {
				spill.Draw(batch);
			}
			for (ArtilleryShell shell2 : this.artilleryShellList) {
				shell2.Draw(batch);
			}
			if (isDemoMode) {
				batch.draw(super.maskTexture, new Vector2f(0f, 0f), new LColor(
						1f, 1f, 1f, demoAlpha));
			}
			if (this.scoreBoard != null) {
				batch.draw(super.maskTexture, new Vector2f(0f, 0f), null,
						new LColor(1f, 1f, 1f, 0.6f), 0f, new Vector2f(0f, 0f),
						(float) 1f, SpriteEffects.None);
				this.scoreBoard.Draw(batch);
				for (Button button : super.buttonList) {
					button.Draw(batch);
				}
			}
		}
	}

	public final void GameFinalize() {
		this.LevelFinalize();
		score = 0;
		Help.money = 0;
		Help.zombieHealthMax = 20;
		Help.numGrenade = 5;
		day = 1;
		level = 0;
		Help.money = 0;
		Help.barrierHMax = 100f;
		Help.barrierHealth = Help.barrierHMax;
		Help.AvailSkillPoint = 0;
		Help.numSkill1 = 0;
		Help.numSkill2 = 0;
		Help.numSkill3 = 0;
		Help.numSkill4 = 0;
		Help.numSkill5 = 0;
		Help.numSkill6 = 0;
		Help.numSkill7 = 0;
		Help.numSkill8 = 0;
		Help.numSkill9 = 0;
		Help.numSkill10 = 0;
		ScreenSkill.isInitialised = false;
		ScreenSkill.isToBeDeleted = true;
	}

	public final void GameInitialize() {
		super.iScreen = 0;
		Help.currentBunker = new Bunker(t2DBunkerBottom, t2DBunkerTop,
				new Vector2f(760f, 320f));
		Help.currentBunker.isPlayerControlled = true;
		this.bunker = Help.currentBunker;
	}

	public final void GenerateExplosion() {
		for (Zombie zombie : this.zombieList) {
			if (zombie.isExploding) {
				this.explosionList.add(new Explosion(t2DExplosion,
						zombie.position, 100, 60));
				zombie.isExploding = false;
			}
		}
	}

	public final void GenerateZombie(int seed) {
		Random random = new Random(seed);
		if (random.NextDouble() < zombieBirthRate) {
			boolean isWithTNT = rand.NextDouble() <= 0.2;
			Zombie item = new Zombie(t2DHead, t2DTorso, t2DUppArm, t2DLowArm,
					t2DUppLeg, t2DLowLeg, new Vector2f(0f,
							240f + (((float) random.NextDouble()) * 240f)),
					isWithTNT, day);
			if (random.NextDouble() < 0.3) {
				item = new Zombie(t2DHead2, t2DTorso2, t2DUppArm2, t2DLowArm2,
						t2DUppLeg2, t2DLowLeg2, new Vector2f(0f,
								240f + (((float) random.NextDouble()) * 240f)),
						isWithTNT, day);
			}
			if (this.zombieList.isEmpty()) {
				this.zombieList.add(item);
			} else {
				for (int i = 0; i < this.zombieList.size(); i++) {
					if (item.position.y < this.zombieList.get(i).position.y) {
						this.zombieList.add(i, item);
						return;
					}
					if (i == (this.zombieList.size() - 1)) {
						this.zombieList.add(item);
						return;
					}
				}
			}
		}
	}

	public final void LevelFinalize() {
		super.iScreen = 0;
		if (Help.mercenaryList != null) {
			Help.mercenaryList.clear();
		}
		Help.barrierHealth = this.barrier.health;
		Help.currentBunker = this.bunker;
		this.zombieList.clear();
		this.numHeadShot = 0;
		this.numKill = 0;
		this.accuracy = 0f;
		this.bunker.isFreeMerAdded = false;
		this.buttonArtillery = null;
		this.scoreBoard = null;
		super.buttonList.clear();
		day++;
	}

	public final void LevelInitialize() {
		if (day < 0x1d) {
			zombieBirthRate *= Help.zombieBirthRateScaler;
		}
		Help.zombieHealthMax = (int) (Help.zombieHPScaler * Help.zombieHealthMax);
		this.bunker = Help.currentBunker;
		this.bunker.weapon = Help.currentWeapon;
		this.bunker.weapon.currentReloadLength = (int) (this.bunker.reloadingTimeMultiplier * this.bunker.weapon.reloadLength);
		this.bunker.weapon.currentMagSize = (int) (this.bunker.weapon.magSize * this.bunker.magSizeMultiplier);
		this.bunker.weapon.currentAccuracy = this.bunker.weapon.accuracy
				* this.bunker.AccMultiplier;
		this.barrier.health = Help.barrierHealth;
		this.AIBunkerList = new java.util.ArrayList<Bunker>();
		if (Help.mercenaryList != null) {
			for (int i = 0; i < Help.mercenaryList.size(); i++) {
				Bunker item = new Bunker(t2DBunkerBottom, t2DBunkerTop,
						this.mercenaryPosition[i]);
				item.weapon = Help.mercenaryList.get(i);
				this.AIBunkerList.add(item);
			}
		}
		super.buttonList.add(new Button(t2DButtonSkill,
				new Vector2f(50f, 140f), 0f, Help.ButtonID.Skill, 0));
		if (isDemoMode) {
			this.bunker.weapon = new Weapon(ScreenLevelup.t2DPPSH41,
					soundPPSH41, new Vector2f(220f, 0f), "PPSH41", 30, 80, 3,
					3, 0.1396263f, 0x221);
			Bunker bunker2 = new Bunker(t2DBunkerBottom, t2DBunkerTop,
					this.mercenaryPosition[2]);
			bunker2.weapon = new Weapon(ScreenLevelup.t2DPPSH41, soundPPSH41,
					new Vector2f(220f, 0f), "PPSH41", 30, 80, 3, 3, 0.1396263f,
					0x221);
			this.AIBunkerList.add(bunker2);
			this.zombieList.add(new Zombie(t2DHead, t2DTorso, t2DUppArm,
					t2DLowArm, t2DUppLeg, t2DLowLeg, new Vector2f(50f, 242f),
					false, 0x19));
			this.zombieList.add(new Zombie(t2DHead, t2DTorso, t2DUppArm,
					t2DLowArm, t2DUppLeg, t2DLowLeg, new Vector2f(100f, 250f),
					false, 0x19));
			this.zombieList.add(new Zombie(t2DHead, t2DTorso, t2DUppArm,
					t2DLowArm, t2DUppLeg, t2DLowLeg, new Vector2f(50f, 280f),
					false, 0x19));
			this.zombieList.add(new Zombie(t2DHead, t2DTorso, t2DUppArm,
					t2DLowArm, t2DUppLeg, t2DLowLeg, new Vector2f(120f, 330f),
					false, 0x19));
			this.zombieList.add(new Zombie(t2DHead2, t2DTorso2, t2DUppArm2,
					t2DLowArm2, t2DUppLeg2, t2DLowLeg2, new Vector2f(0f, 330f),
					false, 0x19));
			this.zombieList.add(new Zombie(t2DHead2, t2DTorso2, t2DUppArm2,
					t2DLowArm2, t2DUppLeg2, t2DLowLeg2,
					new Vector2f(70f, 350f), false, 0x19));
			this.zombieList.add(new Zombie(t2DHead, t2DTorso, t2DUppArm,
					t2DLowArm, t2DUppLeg, t2DLowLeg, new Vector2f(260f, 380f),
					true, 0x19));
			this.zombieList.add(new Zombie(t2DHead, t2DTorso, t2DUppArm,
					t2DLowArm, t2DUppLeg, t2DLowLeg, new Vector2f(150f, 410f),
					false, 0x19));
			this.zombieList.add(new Zombie(t2DHead2, t2DTorso2, t2DUppArm2,
					t2DLowArm2, t2DUppLeg2, t2DLowLeg2,
					new Vector2f(200f, 430f), false, 0x19));
			this.zombieList.add(new Zombie(t2DHead2, t2DTorso2, t2DUppArm2,
					t2DLowArm2, t2DUppLeg2, t2DLowLeg2,
					new Vector2f(220f, 460f), false, 0x19));
			this.zombieList.add(new Zombie(t2DHead2, t2DTorso2, t2DUppArm2,
					t2DLowArm2, t2DUppLeg2, t2DLowLeg2,
					new Vector2f(90f, 450f), false, 0x19));
		}
	}

	@Override
	public void LoadContent() {
		super.LoadContent();
		rand = new Random((int) System.currentTimeMillis() / 1000);
		rifleSound = new SoundEffect();
		soundThompson = new SoundEffect();
		soundNambu = new SoundEffect();
		ZhongZhengSound = new SoundEffect();
		soundPPSH41 = new SoundEffect();
		pistolSound = new SoundEffect();
		soundBAR = new SoundEffect();
		grenadeSound = new SoundEffect();
		soundWebley = new SoundEffect();
		soundWinchester = new SoundEffect();
		reloadSound = new SoundEffect();
		soundSVT40 = new SoundEffect();
		soundVickers = new SoundEffect();
		soundBrowning = new SoundEffect();
		soundZombie1 = new SoundEffect();
		soundBombDrop = new SoundEffect();
		super.bgTexture = Global.Load("PlayGround");
		t2DButtonBack = Global.Load("ButtonBack");
		t2DButtonNext = Global.Load("ButtonNext");
		t2DButtonSkill = Global.Load("ButtonSkill");
		t2DButtonArtillery = Global.Load("buttonArtillery");
		t2DButtonArtilleryMask = Global.Load("buttonArtilleryMask");
		t2DIndLevelup = Global.Load("LevelupInd");
		t2DFireButton = Global.Load("FireButton");
		this.indLevelup = new IndLevelup(t2DIndLevelup, new Vector2f(50f, 140f));
		t2DBarrierOriginal = Global.Load("Barrier_Good");
		t2DBarrierBroken = Global.Load("Barrier_Broken");
		this.barrier = new Barrier(t2DBarrierOriginal, t2DBarrierBroken,
				new Vector2f(670f, 240f));
		t2DHead = Global.Load("ZombieHeadS");
		t2DTorso = Global.Load("ZombieTorsoS");
		t2DUppArm = Global.Load("ZombieUpperArmS");
		t2DLowArm = Global.Load("ZombieLowerArmS");
		t2DUppLeg = Global.Load("ZombieThighS");
		t2DLowLeg = Global.Load("ZombieLowerLegS");
		t2DHead2 = Global.Load("ZombieNHeadS");
		t2DTorso2 = Global.Load("ZombieNTorsoS");
		t2DUppArm2 = Global.Load("ZombieNUpperArmS");
		t2DLowArm2 = Global.Load("ZombieNLowerArmS");
		t2DUppLeg2 = Global.Load("ZombieNThighS");
		t2DLowLeg2 = Global.Load("ZombieNLowerLegS");
		t2DBunkerBottom = Global.Load("BunkerBottom");
		t2DBunkerTop = Global.Load("BunkerTop");
		t2DBunkerAA = Global.Load("BunkerAAGun");
		t2DMarker = Global.Load("Marker");
		t2DMarker1 = Global.Load("Marker1");
		t2DBullet = Global.Load("Bullet");
		t2DBulletLine = Global.Load("BulletLine");
		t2DBulletShell = Global.Load("BulletShell");
		t2DArtilleryShell = Global.Load("Bullet");
		t2DBloodSpill = Global.Load("BloodStain");
		t2DBloodStain = Global.Load("BloodStain");
		t2DBloodSpillBack = Global.Load("SpillingBloodBack");
		t2DShadow = Global.Load("Shadow");
		t2DExplosion = Global.Load("Explosion");
		t2DGrenade = Global.Load("Grenade");
		t2DGunInField = Global.Load("barrel");
		t2DSmoke = Global.Load("Smoke");
		t2DMud = Global.Load("Mud");
		t2DFiringSpark = Global.Load("FiringSpark");
		t2DControlPane = Global.Load("ControlPane");
		t2DControlPaneBG = Global.Load("ControlPaneBG");
		t2DBulletDisplay = Global.Load("Bullet");
		t2DCover = Global.Load("Cover");
		t2DHandle = Global.Load("Reloader");
		t2DBarrierHealthBar = Global.Load("BarrierHealthBar");
		t2DBarrierHealthLiquid = Global.Load("BarrierHealthBarLiquid");
		t2DExpBar = Global.Load("ExpBar");
		t2DExpLiquid = Global.Load("ExpBarLiquid");
		t2DTagLevelUp = Global.Load("LevelupTag");
		t2DRuler = Global.Load("Ruler");
		t2DTNT = Global.Load("TNT");
		ScreenMainMenu.zombieNoise[0] = new SoundEffect();
		ScreenMainMenu.zombieNoise[1] = new SoundEffect();
		ScreenMainMenu.zombieNoise[2] = new SoundEffect();
		ScreenMainMenu.zombieNoise[3] = new SoundEffect();
		super.screenPause.LoadContent();
		this.controlPane = new ControlPane(t2DControlPane, t2DControlPaneBG,
				t2DBulletDisplay, t2DCover, t2DHandle, new Vector2f(640f, 40f));
		t2DScoreBoard = Global.Load("ScoreBoard");
		this.tagLevelUpList = new java.util.ArrayList<TagLevelUp>();
		this.zombieList = new java.util.ArrayList<Zombie>();
		this.grenadeList = new java.util.ArrayList<Grenade>();
		this.explosionList = new java.util.ArrayList<Explosion>();
		this.mudList = new java.util.ArrayList<Mud>();
		this.bulletList = new java.util.ArrayList<Bullet>();
		this.bulletShellList = new java.util.ArrayList<BulletShell>();
		this.bloodSpillList = new java.util.ArrayList<BloodSpill>();
		this.bloodStainList = new java.util.ArrayList<BloodStain>();
		this.artilleryShellList = new java.util.ArrayList<ArtilleryShell>();
		Help.currentBunker = new Bunker(t2DBunkerBottom, t2DBunkerTop,
				new Vector2f(760f, 320f));
		Help.currentBunker.isPlayerControlled = true;
		this.bunker = Help.currentBunker;
		this.AIBunkerList = new java.util.ArrayList<Bunker>();
	}

	public final void Promote() {
		Help.AvailSkillPoint++;
		level++;
		this.tagLevelUpList.add(new TagLevelUp(t2DTagLevelUp, new Vector2f(
				160f, 180f)));
	}

	public final void SaveGame() {
		this.gameSave = new GameSave();
		this.gameSave.money = Help.money;
		this.gameSave.level = level;
		this.gameSave.day = day;
		this.gameSave.score = score;
		this.gameSave.weaponName = this.bunker.weapon.name;
		this.gameSave.availableSkillPoints = Help.AvailSkillPoint;
		if (Help.profession == Help.Profession.Rifleman) {
			this.gameSave.profession = "Rifleman";
		} else if (Help.profession == Help.Profession.BattleEngineer) {
			this.gameSave.profession = "BattleEngineer";
		} else if (Help.profession == Help.Profession.Commander) {
			this.gameSave.profession = "Commander";
		}
		for (int i = 0; i < this.bunker.skillsGained.size(); i++) {

			if (this.bunker.skillsGained.get(i).description.equals("AA Gun")) {
				this.gameSave.num1++;

			}

			else if (this.bunker.skillsGained.get(i).description
					.equals("Improve Accuracy")) {
				this.gameSave.num2++;

			}

			else if (this.bunker.skillsGained.get(i).description
					.equals("Learning")) {
				this.gameSave.num3++;

			}

			else if (this.bunker.skillsGained.get(i).description
					.equals("Free Gunner")) {
				this.gameSave.num4++;

			}

			else if (this.bunker.skillsGained.get(i).description
					.equals("Over Repair")) {
				this.gameSave.num5++;

			}

			else if (this.bunker.skillsGained.get(i).description
					.equals("Field Repair")) {
				this.gameSave.num6++;

			}

			else if (this.bunker.skillsGained.get(i).description
					.equals("Faster Reloading")) {
				this.gameSave.num7++;

			}

			else if (this.bunker.skillsGained.get(i).description
					.equals("Extended Magazine")) {
				this.gameSave.num8++;

			}

			else if (this.bunker.skillsGained.get(i).description
					.equals("Artillery Support")) {
				this.gameSave.num9++;

			}

			else if (this.bunker.skillsGained.get(i).description
					.equals("Boost")) {
				this.gameSave.num10++;
			}
		}

		this.gameSave.year =  Calendar.getInstance().get(Calendar.YEAR);
		this.gameSave.month = Calendar.getInstance().get(Calendar.MONTH);
		this.gameSave.dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		this.gameSave.hour = Calendar.getInstance().get(Calendar.HOUR);
		this.gameSave.minute = Calendar.getInstance().get(Calendar.MINUTE);
		Global.Save(this.gameSave);
	}

	@Override
	public void Update(GameTime gameTime) {
		super.Update(gameTime);
		if (isDemoMode) {
			if (super.iScreen <= 30) {
				this.demoAlpha -= 0.04f;
			}
			if (super.iScreen >= 240) {
				this.demoAlpha += 0.03f;
			}
			if (this.demoAlpha < 0f) {
				this.demoAlpha = 0f;
			} else if (this.demoAlpha > 1f) {
				this.demoAlpha = 1f;
			}
		}
		if ((super.iScreen == 60) && isDemoMode) {
			this.bombardment = new Bombardment(12, this);
		}
		if ((super.iScreen == 300) && isDemoMode) {
			Help.currentGameState = Help.GameScreen.PurchaseFull;
			isToBeDeleted = true;
			isDemoMode = false;
		}
		if (super.iScreen == 900) {

		}
		if (super.iScreen == 0x708) {

		}
		if (super.iScreen <= 1) {

			this.LevelInitialize();
			if (Help.isFromLoadedGame) {
				Help.numSkill1 = ScreenMainMenu.savedGame.num1;
				Help.numSkill2 = ScreenMainMenu.savedGame.num2;
				Help.numSkill3 = ScreenMainMenu.savedGame.num3;
				Help.numSkill4 = ScreenMainMenu.savedGame.num4;
				Help.numSkill5 = ScreenMainMenu.savedGame.num5;
				Help.numSkill6 = ScreenMainMenu.savedGame.num6;
				Help.numSkill7 = ScreenMainMenu.savedGame.num7;
				Help.numSkill8 = ScreenMainMenu.savedGame.num8;
				Help.numSkill9 = ScreenMainMenu.savedGame.num9;
				Help.numSkill10 = ScreenMainMenu.savedGame.num10;
				for (int num = 0; num < ScreenMainMenu.savedGame.num1; num++) {
					this.bunker.skillsGained.add(new TagAAGun(
							ScreenSkill.t2DTagAAGun, new Vector2f(440f, 420f),
							0f, Help.ButtonID.TagAAGun, 0));
				}
				for (int num2 = 0; num2 < ScreenMainMenu.savedGame.num2; num2++) {
					this.bunker.skillsGained.add(new TagAim(
							ScreenSkill.t2DTagAim, new Vector2f(460f, 240f),
							0f, Help.ButtonID.TagAim, 0));
				}
				for (int num3 = 0; num3 < ScreenMainMenu.savedGame.num3; num3++) {
					this.bunker.skillsGained.add(new TagLearning(
							ScreenSkill.t2DTagLearning,
							new Vector2f(327f, 367f), 0f,
							Help.ButtonID.TagLearning, 0));
				}
				for (int num4 = 0; num4 < ScreenMainMenu.savedGame.num4; num4++) {
					this.bunker.skillsGained.add(new TagGunner(
							ScreenSkill.t2DTagGunner, new Vector2f(655f, 367f),
							0f, Help.ButtonID.TagGunner, 0));
				}
				for (int num5 = 0; num5 < ScreenMainMenu.savedGame.num5; num5++) {
					this.bunker.skillsGained.add(new TagOverRepair(
							ScreenSkill.t2DTagOverRepair, new Vector2f(327f,
									196f), 0f, Help.ButtonID.TagOverRepair, 0));
				}
				for (int num6 = 0; num6 < ScreenMainMenu.savedGame.num6; num6++) {
					this.bunker.skillsGained
							.add(new TagFieldRepair(
									ScreenSkill.t2DTagFieldRepair,
									new Vector2f(655f, 196f), 0f,
									Help.ButtonID.TagFieldRepair, 0));
				}
				for (int num7 = 0; num7 < ScreenMainMenu.savedGame.num7; num7++) {
					this.bunker.skillsGained.add(new TagReloading(
							ScreenSkill.t2DTagReload, new Vector2f(327f, 196f),
							0f, Help.ButtonID.TagReloading, 0));
				}
				for (int num8 = 0; num8 < ScreenMainMenu.savedGame.num8; num8++) {
					this.bunker.skillsGained.add(new TagExtendedMag(
							ScreenSkill.t2DExtendedMag,
							new Vector2f(655f, 196f), 0f,
							Help.ButtonID.TagExtendedMag, 0));
				}
				for (int num9 = 0; num9 < ScreenMainMenu.savedGame.num9; num9++) {
					this.bunker.skillsGained.add(new TagArtillerySupport(
							ScreenSkill.t2DTagArtillery, new Vector2f(327f,
									196f), 0f, Help.ButtonID.TagArtillery, 0));
				}
				for (int num10 = 0; num10 < ScreenMainMenu.savedGame.num10; num10++) {
					this.bunker.skillsGained.add(new TagBoost(
							ScreenSkill.t2DTagBoost, new Vector2f(655f, 196f),
							0f, Help.ButtonID.TagBoost, 0));
				}
				for (int num11 = 0; num11 < this.bunker.skillsGained.size(); num11++) {
					this.bunker.skillsGained.get(num11)
							.ApplyEffect(this.bunker);
				}
				this.barrier.health = Help.barrierHMax;
				Help.isFromLoadedGame = false;
			}
			if (super.mousePositionList.size() > 0) {
				super.mousePositionList.clear();
			}
			return;
		}
		if ((super.iScreen < this.levelLength) || (this.zombieList.size() != 0)) {
			if (super.isPaused) {
				if (super.mousePositionList.size() > 0) {
					super.mousePositionList.clear();
				}
				return;
			}
			if (super.iScreen < this.levelLength) {
				this.GenerateZombie(rand.Next());
			}
			if (score >= ((Help.initialPromoteThreshold * (level + 1)) * Math
					.pow((double) Help.expMultiplyFactor, (double) (level + 1)))) {
				this.Promote();
			}
			this.GenerateExplosion();
			this.CollisionDetection();
			this.indLevelup.Update();
			for (int num12 = 0; num12 < this.tagLevelUpList.size(); num12++) {
				this.tagLevelUpList.get(num12).Update();
				if (this.tagLevelUpList.get(num12).isDead) {
					this.tagLevelUpList.remove(num12);
				}
			}
			for (int num13 = 0; num13 < this.explosionList.size(); num13++) {
				this.explosionList.get(num13).Update();
				if (this.explosionList.get(num13).isDead) {
					this.explosionList.set(num13, null);
					this.explosionList.remove(num13);
				}
			}
			for (Zombie zombie : this.zombieList) {
				zombie.Update();
				if (zombie.position.x > 800f) {
					this.GameFinalize();
					isToBeDeleted = true;
					Help.currentGameState = Help.GameScreen.GameOver;
					break;
				}
			}
		} else {
			if (this.buttonNext == null) {
				this.buttonNext = new Button(t2DButtonNext, new Vector2f(570f,
						430f), 0f, Help.ButtonID.Proceed, 30);
				super.buttonList.add(this.buttonNext);
			}
			if (this.scoreBoard == null) {
				this.scoreBoard = new ScoreBoard(t2DScoreBoard, new Vector2f(
						400f, 290f), Screen.ariel18, this.numKill,
						this.numHeadShot, this.accuracy, day);
			}
			if (super.isTranAnimFinished) {
				switch (super.buttonClicked.getButtonID()) {
				case Proceed:
					Help.currentGameState = Help.GameScreen.LevelUp;
					this.LevelFinalize();
					this.SaveGame();

					super.buttonClicked = null;
					this.buttonNext = null;
					break;

				case Back:
					Help.currentGameState = Help.GameScreen.MainMenu;
					super.buttonClicked = null;
					break;

				default:
					super.buttonClicked = null;
					break;
				}
				super.isTranAnimFinished = false;
				for (Button button : super.buttonList) {
					button.ButtonInitialize();
				}
			}
			if (super.mousePositionList.size() > 0) {
				super.mousePositionList.clear();
			}
			return;
		}
		for (int i = 0; i < this.zombieList.size(); i++) {
			if (this.zombieList.get(i).isDead) {
				if (this.zombieList.get(i).deathType == Zombie.DeathType.HeadShot) {
					this.numHeadShot++;
					score += (int) (Help.scorePerHeadShot * this.bunker.learningMultiplier);
					Help.money += Help.scorePerHeadShot;
				}
				this.zombieList.set(i, null);
				this.zombieList.remove(i);
				this.numKill++;
				score += (int) (Help.scorePerKill * this.bunker.learningMultiplier);
				Help.money += Help.scorePerKill;
			}
		}
		for (int j = 0; j < this.grenadeList.size(); j++) {
			this.grenadeList.get(j).Update();
			if (this.grenadeList.get(j).isExploding) {
				this.explosionList.add(new Explosion(t2DExplosion,
						this.grenadeList.get(j).position, 100,
						Help.zombieHealthMax + 30));
				for (int num16 = 0; num16 < 20; num16++) {
					this.mudList.add(new Mud(t2DMud,
							this.grenadeList.get(j).position));
				}
				grenadeSound.Play();
				this.grenadeList.remove(j);
			}
		}
		for (int k = 0; k < this.mudList.size(); k++) {
			this.mudList.get(k).Update();
			if (this.mudList.get(k).isDead) {
				this.mudList.remove(k);
			}
		}
		for (int m = 0; m < this.bulletList.size(); m++) {
			this.bulletList.get(m).Update();
			if (this.bulletList.get(m).isDead) {
				this.bulletList.remove(m);
			}
		}
		for (int n = 0; n < this.bulletShellList.size(); n++) {
			this.bulletShellList.get(n).Update();
			if (this.bulletShellList.get(n).isDead) {
				this.bulletShellList.remove(n);
			}
		}
		for (int num20 = 0; num20 < this.bloodSpillList.size(); num20++) {
			this.bloodSpillList.get(num20).Update();
			if (this.bloodSpillList.get(num20).isDead) {
				this.bloodSpillList.remove(num20);
			}
		}
		for (int num21 = 0; num21 < this.bloodStainList.size(); num21++) {
			this.bloodStainList.get(num21).Update();
			if (this.bloodStainList.get(num21).isDead) {
				this.bloodStainList.remove(num21);
			}
		}
		for (int num22 = 0; num22 < this.artilleryShellList.size(); num22++) {
			this.artilleryShellList.get(num22).Update();
			if (this.artilleryShellList.get(num22).isExploding) {
				this.explosionList.add(new Explosion(t2DExplosion,
						this.artilleryShellList.get(num22).position, 100, 800));
				grenadeSound.Play();
				for (int num23 = 0; num23 < 20; num23++) {
					this.mudList.add(new Mud(t2DMud, this.artilleryShellList
							.get(num22).position));
				}
				this.artilleryShellList.remove(num22);
			}
		}
		if (this.bombardment != null) {
			this.bombardment.Update();
			if (this.bombardment.isToBeDeleted) {
				this.bombardment = null;
			}
		}
		this.bunker.Update(super.mousePositionList);
		if (this.bunker.isLaunchingGrenade) {
			this.grenadeList.add(new Grenade(t2DGrenade, this.bunker.position,
					this.bunker.grenadeTargetPosition));
			this.bunker.isLaunchingGrenade = false;
			Help.numGrenade--;
		}
		if (!this.bunker.isFreeMerAdded && (this.bunker.freeMercenary != null)) {
			Bunker item = new Bunker(t2DBunkerBottom, t2DBunkerTop,
					new Vector2f(760f, 470f));
			item.weapon = this.bunker.freeMercenary;
			this.AIBunkerList.add(item);
			this.bunker.isFreeMerAdded = true;
		}
		if (this.bunker.isArtilleryEnabled && (this.buttonArtillery == null)) {
			this.buttonArtillery = new Button(t2DButtonArtillery, new Vector2f(
					150f, 140f), 0f, Help.ButtonID.Bombard,
					this.bunker.numArtilleryHit);
			super.buttonList.add(this.buttonArtillery);
		}
		if (this.zombieList.size() != 0) {
			for (Bunker bunker2 : this.AIBunkerList) {
				bunker2.Automate(this.zombieList);
			}
		}
		this.barrier.Update();
		if ((this.barrier.health != Help.barrierHMax)
				&& (this.bunker.fieldRepair > 0f)) {
			this.barrier.health += this.bunker.fieldRepair;
			if (this.barrier.health > Help.barrierHMax) {
				this.barrier.health = Help.barrierHMax;
			}
		}
		if (this.bunker.weapon.isTriggerPulled && this.bunker.weapon.isFirable) {
			float num24 = (((float) (rand.NextDouble() - 0.5)) * 2f)
					* this.bunker.weapon.currentAccuracy;
			this.bunker.weapon.firingSound.Play();
			this.bulletList.add(new Bullet(t2DBulletLine, this.bunker.position,
					this.bunker.weapon.angle, this.bunker.weapon.power));
			this.bulletList.get(this.bulletList.size() - 1).life = 1;
			this.bulletList.get(this.bulletList.size() - 1).angle = this.bunker.aimAngle
					+ num24;
			this.bulletShellList.add(new BulletShell(t2DBulletShell,
					this.bunker.position.add(-30f, 0f),
					((int) this.bunker.position.y) + 80, rand.Next()));
			this.bunker.weapon.isFirable = false;
			this.bunker.weapon.iFire = 0;
			this.bunker.weapon.numBullet--;
		}
		for (Bunker bunker3 : this.AIBunkerList) {
			if (bunker3.weapon.isTriggerPulled && bunker3.weapon.isFirable) {
				float num25 = (((float) (rand.NextDouble() - 0.5)) * 2f)
						* this.bunker.weapon.accuracy;
				bunker3.weapon.firingSound.Play();
				this.bulletList.add(new Bullet(t2DBulletLine, bunker3.position,
						bunker3.weapon.angle, bunker3.weapon.power));
				this.bulletList.get(this.bulletList.size() - 1).life = 1;
				this.bulletList.get(this.bulletList.size() - 1).angle = bunker3.aimAngle
						+ num25;
				this.bulletShellList.add(new BulletShell(t2DBulletShell,
						bunker3.position.add(-30f, 0f),
						((int) bunker3.position.y) + 80, rand.Next()));
				bunker3.weapon.isFirable = false;
				bunker3.weapon.iFire = 0;
				bunker3.weapon.numBullet--;
			}
		}
		this.controlPane.Update1(this.bunker.weapon, Help.numGrenade);
		if (isDemoMode || !super.isTranAnimFinished) {
			if (super.mousePositionList.size() > 0) {
				super.mousePositionList.clear();
			}
			return;
		}
		if (super.buttonClicked != null) {
			switch (super.buttonClicked.getButtonID()) {
			case Skill:
				Help.currentGameState = Help.GameScreen.Skill;
				Help.previousGameState = Help.GameScreen.Gameplay;
				super.buttonClicked = null;
				break;

			case Bombard:
				if (this.bunker.isArtilleryReady) {
					this.bombardment = new Bombardment(
							this.bunker.numArtilleryHit, this);
					this.bunker.isArtilleryReady = false;
					this.bunker.iCoolDown = 0;
				}
				super.buttonClicked = null;
				//
				super.isTranAnimFinished = false;
				for (Button button2 : super.buttonList) {
					button2.ButtonInitialize();
				}

				if (super.mousePositionList.size() > 0) {
					super.mousePositionList.clear();
				}
				return;
				//
			default:
				break;
			}
		}

	}
}