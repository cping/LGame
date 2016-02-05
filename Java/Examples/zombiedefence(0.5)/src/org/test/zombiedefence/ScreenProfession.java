package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

public class ScreenProfession extends Screen {

	private LTexture t2DButtonConfirm;
	private LTexture t2DCardBattleEngineer;
	private LTexture t2DCardCommander;
	private LTexture t2DCardRifleman;
	private LTexture t2DTitle;

	public ScreenProfession() {
		super.screenPause = new ScreenPause(this, Help.GameScreen.Profession);
	}

	@Override
	public void Draw(SpriteBatch batch) {
		super.Draw(batch);
		if (!super.isPaused) {
			batch.draw(this.t2DTitle, 10f, 20f);
			batch.draw(this.t2DCardRifleman, 20f, 100f);
			batch.draw(this.t2DCardBattleEngineer, 280f, 100f);
			batch.draw(this.t2DCardCommander, 540f, 100f);
			for (Button button : super.buttonList) {
				button.Draw(batch);
			}
		}
	}

	@Override
	public void LoadContent() {
		super.LoadContent();
		super.bgTexture = Global.Load("ScratchBG");
		this.t2DButtonConfirm = Global.Load("ButtonChoose");
		this.t2DCardRifleman = Global.Load("ProfessionRifleman");
		this.t2DCardBattleEngineer = Global.Load("ProfessionBattleEngineer");
		this.t2DCardCommander = Global.Load("ProfessionCommander");
		this.t2DTitle = Global.Load("TitleProfession");
		super.buttonList.add(new Button(this.t2DButtonConfirm, new Vector2f(
				220f, 340f), 0f, Help.ButtonID.Option1, 15));
		super.buttonList.add(new Button(this.t2DButtonConfirm, new Vector2f(
				480f, 340f), 0f, Help.ButtonID.Option2, 15));
		super.buttonList.add(new Button(this.t2DButtonConfirm, new Vector2f(
				740f, 340f), 0f, Help.ButtonID.Option3, 15));
		super.screenPause.LoadContent();
	}

	@Override
	public void Update(GameTime gameTime) {
		super.Update(gameTime);
		if (super.isTranAnimFinished) {
			if (super.buttonClicked != null) {
				switch (super.buttonClicked.getButtonID()) {
				case Option1:
					Help.profession = Help.Profession.Rifleman;
					for (DrawableObject o : ScreenLevelup.scrollablePane.itemList) {
						if (o instanceof Weapon) {
							Weapon weapon = (Weapon) o;
							if (weapon.name.equals("Karabiner98K")) {
								Help.currentWeapon = new Weapon(weapon.texture,
										weapon.firingSound, weapon.position,
										weapon.name, weapon.magSize,
										weapon.reloadLength,
										weapon.framesPerFire, weapon.power,
										weapon.accuracy, weapon.price);
							}
						}
					}
					Help.currentGameState = Help.GameScreen.Instruction;
					super.buttonClicked = null;
					break;

				case Option2:
					Help.profession = Help.Profession.BattleEngineer;
					for (DrawableObject o : ScreenLevelup.scrollablePane.itemList) {
						if (o instanceof Weapon) {
							Weapon weapon2 = (Weapon) o;
							if (weapon2.name.equals("NambuType14")) {
								Help.currentWeapon = new Weapon(
										weapon2.texture, weapon2.firingSound,
										weapon2.position, weapon2.name,
										weapon2.magSize, weapon2.reloadLength,
										weapon2.framesPerFire, weapon2.power,
										weapon2.accuracy, weapon2.price);
							}
						}
					}
					Help.currentGameState = Help.GameScreen.Instruction;
					super.buttonClicked = null;
					break;

				case Option3:
					Help.currentGameState = Help.GameScreen.PurchaseFull;
					ScreenGameplay.isToBeDeleted = true;
					super.buttonClicked = null;
					break;

				case Back:
					Help.currentGameState = Help.GameScreen.MainMenu;
					super.buttonClicked = null;
					break;
				default:
					break;
				}
			}
			super.buttonClicked = null;
			super.isTranAnimFinished = false;
		}
		if (super.mousePositionList.size() > 0) {
			super.mousePositionList.clear();
		}
	}
}