package com.zombiedefence.free;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableScreen;
import loon.core.LSystem;
import loon.core.geom.Vector3f;
import loon.core.input.LKey;
import loon.core.input.LTouch;
import loon.core.timer.GameTime;

public class MainGame extends DrawableScreen {

	public static int age;

	public static int phase;

	public static Vector3f result;
	private ScreenDay screenDay;
	private ScreenFail screenFail;
	private ScreenGameplay screenGameplay;
	private ScreenInstruction screenInstruction;
	private ScreenLevelup screenLevelup;
	private ScreenLevelup2 screenLevelup2;
	private ScreenMainMenu screenMainMenu;
	private ScreenProfession screenProfession;
	private ScreenPurchaseFull screenPurchaseFull;
	private ScreenSkill screenSkill;
	private SplashScreen splashScreen;

	public MainGame() {
		phase = 1;
		Help.currentGameState = Help.GameScreen.MainMenu;
	}

	@Override
	public void draw(SpriteBatch batch) {
		if (!isOnLoadComplete()) {
			return;
		}
		switch (Help.currentGameState) {
		case Gameplay:
			this.screenGameplay.Draw(batch);
			return;

		case MainMenu:
			this.screenMainMenu.Draw(batch);
			return;

		case LevelUp:
			this.screenLevelup.Draw(batch);
			return;

		case LevelUp2:
			this.screenLevelup2.Draw(batch);
			return;

		case GameOver:
			this.screenFail.Draw(batch);
			return;

		case Options:
		case About:
		case Info:
		case Result:
		case Prepare:
		case Series:
			break;

		case Instruction:
			this.screenInstruction.Draw(batch);
			return;

		case Skill:
			if (this.screenSkill == null) {
				this.screenSkill = new ScreenSkill();
				this.screenSkill.LoadContent();
				return;
			}
			if (!ScreenSkill.isToBeDeleted) {
				this.screenSkill.Draw(batch);
				return;
			}
			this.screenSkill = null;
			return;

		case Profession:
			this.screenProfession.Draw(batch);
			return;

		case Day:
			this.screenDay.Draw(batch);
			return;

		case PurchaseFull:
			this.screenPurchaseFull.Draw(batch);
			break;

		default:
			return;
		}
	}

	@Override
	public void loadContent() {
		this.splashScreen = new SplashScreen();
		this.splashScreen.LoadContent();
		this.screenLevelup = new ScreenLevelup();
		this.screenLevelup.LoadContent();
		this.screenLevelup2 = new ScreenLevelup2();
		this.screenLevelup2.LoadContent();
		this.screenGameplay = new ScreenGameplay();
		this.screenGameplay.LoadContent();
		this.screenMainMenu = new ScreenMainMenu();
		this.screenMainMenu.LoadContent();
		this.screenInstruction = new ScreenInstruction();
		this.screenInstruction.LoadContent();
		this.screenFail = new ScreenFail();
		this.screenFail.LoadContent();
		this.screenSkill = new ScreenSkill();
		this.screenSkill.LoadContent();
		this.screenProfession = new ScreenProfession();
		this.screenProfession.LoadContent();
		this.screenDay = new ScreenDay();
		this.screenDay.LoadContent();
		this.screenPurchaseFull = new ScreenPurchaseFull();
		this.screenPurchaseFull.LoadContent();

	}

	@Override
	public void unloadContent() {
	}

	@Override
	public void update(GameTime gameTime) {
		if (!isOnLoadComplete()) {
			return;
		}
		if (this.screenMainMenu.isGameExiting) {
			LSystem.exit();
		}
		switch (Help.currentGameState) {
		case Gameplay:
			this.screenGameplay.Update(gameTime);
			break;

		case MainMenu:
			this.screenMainMenu.Update(gameTime);
			break;

		case LevelUp:
			this.screenLevelup.Update(gameTime);
			break;

		case LevelUp2:
			this.screenLevelup2.Update(gameTime);
			break;

		case GameOver:
			this.screenFail.Update(gameTime);
			break;

		case Instruction:
			this.screenInstruction.Update(gameTime);
			break;

		case Skill:
			if (this.screenSkill == null) {
				this.screenSkill = new ScreenSkill();
				this.screenSkill.LoadContent();
				break;
			}
			if (!ScreenSkill.isToBeDeleted) {
				this.screenSkill.Update(gameTime);
				break;
			}
			this.screenSkill = null;
			break;

		case Profession:
			this.screenProfession.Update(gameTime);
			break;

		case Day:
			this.screenDay.Update(gameTime);
			break;

		case PurchaseFull:
			this.screenPurchaseFull.Update(gameTime);
			break;
		default:
			break;
		}
		if (ScreenGameplay.isToBeDeleted && (this.screenGameplay != null)) {
			this.screenGameplay.GameFinalize();
			this.screenGameplay = null;
		} else if (this.screenGameplay == null) {
			this.screenGameplay = new ScreenGameplay();
			this.screenGameplay.LoadContent();
		}
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