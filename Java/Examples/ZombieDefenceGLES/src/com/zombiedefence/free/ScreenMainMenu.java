package com.zombiedefence.free;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.geom.Vector2f;
import loon.core.graphics.opengl.LTexture;
import loon.core.input.LInputFactory;
import loon.core.input.LInputFactory.Key;
import loon.core.timer.GameTime;

public class ScreenMainMenu extends Screen {

	private Button buttonPlay;
	private Button buttonRate;
	private Button buttonResume;
	private Button buttonShare;

	public boolean isGameExiting;
	private boolean isLoadBoardShowing;
	private boolean isMenuReady;
	private LoadBoard loadBoard;
	private int menuAnimationLength;
	private Vector2f positionButtonPlay;
	private Vector2f positionButtonRate;
	private Vector2f positionButtonResume;
	private Vector2f positionButtonShare;
	private Random rand;
	public static GameSave savedGame;
	private LTexture t2DBGWindow;
	private LTexture t2DButtonCancel;
	private LTexture t2DButtonConfirm;

	private LTexture t2DButtonPlay;
	private LTexture t2DButtonRate;
	private LTexture t2DButtonResume;
	private LTexture t2DButtonShare;

	private LTexture t2DLoadBoard;
	private java.util.ArrayList<Zombie> zombieList;
	public static SoundEffect[] zombieNoise = new SoundEffect[4];

	public ScreenMainMenu() {
		this.positionButtonPlay = new Vector2f(650f, 220f);
		this.positionButtonResume = new Vector2f(670f, 150f);
		this.positionButtonRate = new Vector2f(710f, 370f);
		this.positionButtonShare = new Vector2f(700f, 300f);
		this.isMenuReady = false;
		this.isLoadBoardShowing = false;
		this.menuAnimationLength = 60;
		this.rand = new Random();
		savedGame = new GameSave();
		savedGame.day = 1;
	}

	@Override
	public void Draw(SpriteBatch batch) {
		batch.draw(this.t2DBGWindow, 100f, 120f);
		for (Zombie zombie : this.zombieList) {
			zombie.Draw(batch);
		}
		super.Draw(batch);
		if (this.isLoadBoardShowing) {
			batch.draw(super.maskTexture, 0f, 0f, null,
					Global.Pool.getColor(1f, 1f, 1f, 0.6f), 0f, 0f, 0f, 1f,
					SpriteEffects.None);
			this.loadBoard.Draw(batch);
			super.buttonList.get(super.buttonList.size() - 2).Draw(batch);
			super.buttonList.get(super.buttonList.size() - 1).Draw(batch);
		}
	}

	public final void EmailMe() {

	}

	public final void FollowOnTwitter() {

	}

	@Override
	public void LoadContent() {
		super.LoadContent();
		super.bgTexture = Global.Load("MainMenu");
		this.t2DBGWindow = Global.Load("BGField");
		this.t2DButtonPlay = Global.Load("ButtonNewGame");
		this.t2DButtonResume = Global.Load("ButtonResume");
		this.t2DButtonRate = Global.Load("ButtonRate");
		this.t2DButtonShare = Global.Load("ButtonShare");
		this.t2DButtonConfirm = Global.Load("ButtonConfirm");
		this.t2DButtonCancel = Global.Load("ButtonCancel");

	
		this.t2DLoadBoard = Global.Load("LoadBoard");
		zombieNoise[0] = new SoundEffect();
		zombieNoise[1] = new SoundEffect();
		zombieNoise[2] = new SoundEffect();
		zombieNoise[3] = new SoundEffect();
		this.zombieList = new java.util.ArrayList<Zombie>();

	
	}

	public final void RateThisApp() {

	}

	public final void ShareInMarketplace() {

	}

	@Override
	public void Update(GameTime gameTime) {
		if (!this.isMenuReady) {
			if (super.iScreen == 0) {
				Global.Read(savedGame);
			}
			super.iScreen++;
			if ((super.iScreen >= (0.1f * this.menuAnimationLength))
					&& (savedGame.day > 1)) {
				if (this.buttonResume == null) {
					this.buttonResume = new Button(this.t2DButtonResume,
							new Vector2f(700f, 140f), 0f, Help.ButtonID.Resume,
							15);
					super.buttonList.add(this.buttonResume);
				}
				this.buttonResume.position.x = (this.positionButtonResume.x + 30f)
						- (30f * ((float) Math
								.sin((double) ((1.570796f * this.buttonResume.iLife) / 10f))));
				if (this.buttonResume.iLife <= 10) {
					this.buttonResume.iLife++;
				}
			}
			if (super.iScreen >= (0.2f * this.menuAnimationLength)) {
				if (this.buttonPlay == null) {
					this.buttonPlay = new Button(this.t2DButtonPlay,
							new Vector2f(650f, 210f), 0f,
							Help.ButtonID.Proceed, 15);
					super.buttonList.add(this.buttonPlay);
				}
				this.buttonPlay.position.x = (this.positionButtonPlay.x + 30f)
						- (30f * ((float) Math
								.sin((double) ((1.570796f * this.buttonPlay.iLife) / 10f))));
				if (this.buttonPlay.iLife <= 10) {
					this.buttonPlay.iLife++;
				}
			}
			if (super.iScreen >= (0.3f * this.menuAnimationLength)) {
				if (this.buttonShare == null) {
					this.buttonShare = new Button(this.t2DButtonShare,
							new Vector2f(710f, 290f), 0f, Help.ButtonID.Share,
							15);
					super.buttonList.add(this.buttonShare);
				}
				this.buttonShare.position.x = (this.positionButtonShare.x + 30f)
						- (30f * ((float) Math
								.sin((double) ((1.570796f * this.buttonShare.iLife) / 10f))));
				if (this.buttonShare.iLife <= 10) {
					this.buttonShare.iLife++;
				}
			}
			if (super.iScreen >= (0.4f * this.menuAnimationLength)) {
				if (this.buttonRate == null) {
					this.buttonRate = new Button(this.t2DButtonRate,
							new Vector2f(700f, 350f), 0f, Help.ButtonID.Rate,
							15);
					super.buttonList.add(this.buttonRate);
				}
				this.buttonRate.position.x = (this.positionButtonRate.x + 30f)
						- (30f * ((float) Math
								.sin((double) ((1.570796f * this.buttonRate.iLife) / 10f))));
				if (this.buttonRate.iLife <= 10) {
					this.buttonRate.iLife++;
				}
			}
			if (super.iScreen >= this.menuAnimationLength) {
				this.isMenuReady = true;
			}
		} else {
			super.Update(gameTime);
			if (super.isTranAnimFinished) {
				if (super.buttonClicked != null) {
					switch (super.buttonClicked.getButtonID()) {
					case Email:
						this.EmailMe();
						super.buttonClicked = null;
						break;

					case Rate:
						this.RateThisApp();
						super.buttonClicked = null;
						break;

					case Share:
						this.ShareInMarketplace();
						super.buttonClicked = null;
						break;

					case Resume:
						if (!this.isLoadBoardShowing) {
							this.isLoadBoardShowing = true;
							this.loadBoard = new LoadBoard(this.t2DLoadBoard,
									new Vector2f(400f, 250f), Screen.myFont,
									savedGame);
							super.buttonList.add(new Button(
									this.t2DButtonConfirm, new Vector2f(550f,
											400f), 0f, Help.ButtonID.Confirm,
									15));
							super.buttonList
									.add(new Button(this.t2DButtonCancel,
											new Vector2f(230f, 400f), 0f,
											Help.ButtonID.Cancel, 15));
						}
						super.buttonClicked = null;
						break;

					case Proceed:
						if (!this.isLoadBoardShowing) {
							Help.currentGameState = Help.GameScreen.Profession;
							super.iScreen = 0;
							this.isMenuReady = false;
							Help.isFromLoadedGame = false;
						}
						super.buttonClicked = null;
						break;

					case Confirm:
						super.iScreen = 0;
						this.isMenuReady = false;
						Help.isFromLoadedGame = true;
						Help.currentGameState = Help.GameScreen.LevelUp;

						ScreenGameplay.day = savedGame.day;
						ScreenGameplay.level = savedGame.level;
						ScreenGameplay.zombieBirthRate = 0.009000001f * ((float) Math
								.pow((double) Help.zombieBirthRateScaler,
										(double) (ScreenGameplay.day - 2)));
						Help.zombieHealthMax = (int) (Help.zombieHealthMax * ((float) Math
								.pow((double) Help.zombieHPScaler,
										(double) (ScreenGameplay.day - 2))));
						ScreenGameplay.score = savedGame.score;
						Help.money = savedGame.money;
						Help.AvailSkillPoint = savedGame.availableSkillPoints;
						for (DrawableObject o : ScreenLevelup.scrollablePane.itemList) {
							if (o instanceof Weapon) {
								Weapon weapon = (Weapon) o;
								if (weapon.name
										.equalsIgnoreCase(savedGame.weaponName)) {
									Help.currentWeapon = new Weapon(
											weapon.texture, weapon.firingSound,
											weapon.position, weapon.name,
											weapon.magSize,
											weapon.reloadLength,
											weapon.framesPerFire, weapon.power,
											weapon.accuracy, weapon.price);
								}
							}
						}
						if (savedGame.profession.equals("Rifleman")) {
							Help.profession = Help.Profession.Rifleman;
						} else if (savedGame.profession
								.equals("BattleEngineer")) {
							Help.profession = Help.Profession.BattleEngineer;
						} else if (savedGame.profession.equals("Commander")) {
							Help.profession = Help.Profession.Commander;
						}
						super.buttonClicked = null;
						break;

					case Twitter:
						this.FollowOnTwitter();
						super.buttonClicked = null;
						break;

					case Cancel:
						this.isLoadBoardShowing = false;
						super.buttonList.subList(super.buttonList.size() - 2,
								super.buttonList.size()).clear();
						super.buttonClicked = null;
						break;

					case Volume:
						if (SoundEffect.MasterVolume != 0.7f) {
							SoundEffect.MasterVolume = 0.7f;
						
							break;
						}
						SoundEffect.MasterVolume = 0f;
				
						break;
					default:
						break;
					}
					super.buttonClicked = null;
					super.isTranAnimFinished = false;
					for (Button button : super.buttonList) {
						button.ButtonInitialize();
					}
				}
			}
		}
		while (this.zombieList.size() < 8) {
			this.zombieList.add(new Zombie(ScreenGameplay.t2DHead2,
					ScreenGameplay.t2DTorso2, ScreenGameplay.t2DUppArm2,
					ScreenGameplay.t2DLowArm2, ScreenGameplay.t2DUppLeg2,
					ScreenGameplay.t2DLowLeg2, new Vector2f(-50f
							+ (((float) this.rand.NextDouble()) * 200f),
							240f + (((float) this.rand.NextDouble()) * 160f)),
					false, 1));
		}
		for (Zombie zombie : this.zombieList) {
			zombie.Update();
		}
		for (int i = 0; i < this.zombieList.size(); i++) {
			if (this.zombieList.get(i).position.x > 400f) {
				this.zombieList.remove(i);
			}
		}
		if (super.mousePositionList.size() > 0) {
			super.mousePositionList.clear();
		}
		if (Key.isKeyPressed(Key.BACK)) {
			if (this.isLoadBoardShowing) {
				this.isLoadBoardShowing = false;
				super.buttonList.subList(super.buttonList.size() - 2,
						super.buttonList.size()).clear();
			} else {
				this.isGameExiting = true;
			}
		}
	}
}