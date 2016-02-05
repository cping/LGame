package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

public class ScreenLevelup2 extends Screen {
	public java.util.ArrayList<Weapon> mercenaryList;
	private java.util.ArrayList<MoneyDeductionTag> moneyDeductionTagList;
	private ScrollablePane scrollablePane;

	private LTexture t2DButtonBack;
	private LTexture t2DButtonBuy;
	private LTexture t2DButtonHire;
	private LTexture t2DButtonNext;
	private LTexture t2DButtonRepair;
	private LTexture t2DEmpty;
	private LTexture t2DMachinegunner1;
	private LTexture t2DMachinegunner2;
	private LTexture t2DMercenaryHired;
	private LTexture t2DMercenaryTitle;
	private LTexture t2DPane;
	private LTexture t2DPistolman;
	private LTexture t2DRifleman1;
	private LTexture t2DRifleman2;
	private LTexture t2DTitle;
	private LTexture t2DVerticalLine;
	public Weapon tempWeapon;

	public ScreenLevelup2() {
		super.screenPause = new ScreenPause(this, Help.GameScreen.LevelUp2);
	}

	@Override
	public void Draw(SpriteBatch batch) {
		super.Draw(batch);
		if (!super.isPaused) {
			;
			this.scrollablePane.Draw(batch);
			batch.drawString(Screen.myFont, "$" + Help.money, 705f, 380f,
					LColor.white);
			batch.draw(this.t2DTitle, 10f, 20f);
			batch.draw(this.t2DVerticalLine, 280f, 85f);
			batch.draw(this.t2DMercenaryTitle, 320f, 85f);
			batch.draw(this.t2DMercenaryHired, 530f, 105f);
			for (int i = 0; i < (5 - this.mercenaryList.size()); i++) {
				batch.draw(this.t2DEmpty, 610f, (float) (0x163 - (0x30 * i)));
			}
			for (Button button : super.buttonList) {
				button.Draw(batch);
			}
			for (int j = 0; j < Help.numGrenade; j++) {
				batch.draw(
						ScreenGameplay.t2DGrenade,
						new Vector2f(
								(float) (10 + (j * ScreenGameplay.t2DGrenade
										.getWidth())), 0f).add(50f, 320f),
						null, LColor.white, 0f, 0f, 0f, 1.4f,
						SpriteEffects.None);
			}
			batch.drawString(Screen.myFont, (new Integer(
					(int) Help.barrierHealth)).toString(), 210f, 90f,
					LColor.white);
			batch.drawString(Screen.myFont, Help.numGrenade + "", 210f, 270f,
					LColor.wheat);
			for (Weapon weapon : this.mercenaryList) {
				weapon.Draw(batch);
			}
			for (MoneyDeductionTag tag : this.moneyDeductionTagList) {
				tag.Draw(batch);
			}
		}
	}

	@Override
	public void LoadContent() {
		super.LoadContent();
		super.screenPause.LoadContent();
		ScreenGameplay.rand = new Random();
		ScreenGameplay.pistolSound = new SoundEffect();
		ScreenGameplay.rifleSound = new SoundEffect();
		super.bgTexture = Global.Load("ScratchBG");
		this.t2DPane = Global.Load("ScrollablePane");
		this.t2DButtonNext = Global.Load("ButtonNext");
		this.t2DButtonBack = Global.Load("ButtonBack");
		this.t2DButtonRepair = Global.Load("RepairBarrier");
		this.t2DButtonHire = Global.Load("ButtonHire");
		this.t2DButtonBuy = Global.Load("GrenadePlate");
		this.t2DRifleman1 = Global.Load("MercenaryRifleman1");
		this.t2DRifleman2 = Global.Load("MercenaryRifleman2");
		this.t2DPistolman = Global.Load("MercenaryPistol");
		this.t2DMachinegunner1 = Global.Load("MercenaryMachineGunner1");
		this.t2DMachinegunner2 = Global.Load("MercenaryMachineGunner2");
		this.t2DMercenaryHired = Global.Load("Mercenary Hired");
		this.t2DEmpty = Global.Load("Empty");
		this.t2DTitle = Global.Load("TitleLevelup2");
		this.t2DVerticalLine = Global.Load("VerticalLine");
		this.t2DMercenaryTitle = Global.Load("HireMercenary");
		super.buttonList.add(new Button(this.t2DButtonNext, new Vector2f(750f,
				450f), 0f, Help.ButtonID.Proceed, 15));
		super.buttonList.add(new Button(this.t2DButtonBack, new Vector2f(50f,
				450f), 0f, Help.ButtonID.Back, 15));
		super.buttonList.add(new Button(this.t2DButtonRepair, new Vector2f(
				145f, 160f), 0f, Help.ButtonID.Rate, 0));
		super.buttonList.add(new Button(this.t2DButtonHire, new Vector2f(500f,
				275f), 0f, Help.ButtonID.Buy, 0));
		super.buttonList.add(new Button(this.t2DButtonBuy, new Vector2f(145f,
				340f), 0f, Help.ButtonID.Buy2, 0));
		this.scrollablePane = new ScrollablePane(this.t2DPane, new Vector2f(
				310f, 110f));
		this.scrollablePane.AddItem(new Weapon(this.t2DPistolman,
				ScreenGameplay.soundNambu, new Vector2f(0f, 0f), "Pistolman",
				8, 60, 10, 4, 0.1396263f, 15));
		this.scrollablePane.AddItem(new Weapon(this.t2DRifleman1,
				ScreenGameplay.ZhongZhengSound, new Vector2f(0f, 0f),
				"Rifleman", 8, 90, 30, 8, 0.03490658f, 0x15));
		this.scrollablePane.AddItem(new Weapon(this.t2DRifleman2,
				ScreenGameplay.soundSVT40, new Vector2f(0f, 0f), "Rifleman",
				10, 90, 10, 0x10, 0.03490658f, 0x2d));
		this.scrollablePane.AddItem(new Weapon(this.t2DMachinegunner1,
				ScreenGameplay.soundThompson, new Vector2f(0f, 0f),
				"Shotgunner", 30, 60, 3, 0x1b, 0.1745329f, 0x55));
		this.scrollablePane.AddItem(new Weapon(this.t2DMachinegunner2,
				ScreenGameplay.soundPPSH41, new Vector2f(0f, 0f),
				"Machinegunner", 60, 90, 2, 0x23, 0.08726647f, 0x69));
		this.moneyDeductionTagList = new java.util.ArrayList<MoneyDeductionTag>();
		this.mercenaryList = new java.util.ArrayList<Weapon>(5);
	}

	@Override
	public void Update(GameTime gameTime) {
		super.Update(gameTime);
		this.scrollablePane.Update(super.mousePositionList);
		for (int i = 0; i < this.moneyDeductionTagList.size(); i++) {
			this.moneyDeductionTagList.get(i).Update();
			if (this.moneyDeductionTagList.get(i).isDead) {
				this.moneyDeductionTagList.remove(i);
			}
		}
		if (super.isTranAnimFinished) {
			if (super.buttonClicked != null) {
				switch (super.buttonClicked.getButtonID()) {
				case Proceed:
					Help.currentGameState = Help.GameScreen.Day;
					Help.mercenaryList = this.mercenaryList;

					super.buttonClicked = null;
					for (Button button2 : super.buttonList) {
						button2.ButtonInitialize();
					}
					break;

				case Back:
					Help.currentGameState = Help.GameScreen.LevelUp;
					super.buttonClicked = null;
					for (Button button : super.buttonList) {
						button.ButtonInitialize();
					}
					break;

				case Rate:
					if (!super.buttonClicked.isEffectTaken) {
						if ((Help.money >= 10)
								&& (Help.barrierHealth < Help.barrierHMax)) {
							Help.barrierHealth += 20f;
							Help.money -= 10;
							this.moneyDeductionTagList
									.add(new MoneyDeductionTag("-10",
											new Vector2f(640f, 15f)));
							if (Help.barrierHealth > Help.barrierHMax) {
								Help.barrierHealth = Help.barrierHMax;
							}
						}
						super.buttonClicked.isEffectTaken = true;
					}
					break;

				case Buy:
					if (!super.buttonClicked.isEffectTaken) {
						this.tempWeapon = (Weapon) this.scrollablePane.itemList
								.get(this.scrollablePane.iSelectedItem);
						if ((this.mercenaryList.size() < 5)
								&& (Help.money >= this.tempWeapon.price)) {
							this.mercenaryList.add(new Weapon(
									this.tempWeapon.texture,
									this.tempWeapon.firingSound, new Vector2f(
											640f,
											(float) (0xaf + (this.mercenaryList
													.size() * 0x30))),
									this.tempWeapon.name,
									this.tempWeapon.magSize,
									this.tempWeapon.reloadLength,
									this.tempWeapon.framesPerFire,
									this.tempWeapon.power,
									this.tempWeapon.accuracy,
									this.tempWeapon.price));
							Help.money -= this.tempWeapon.price;
							int num2 = -this.tempWeapon.price;
							this.moneyDeductionTagList
									.add(new MoneyDeductionTag((new Integer(
											num2)).toString(), new Vector2f(
											710f, 380f)));
						}
						super.buttonClicked.isEffectTaken = true;
					}
					break;

				case Buy2:
					if (!super.buttonClicked.isEffectTaken) {
						if ((Help.numGrenade < Help.maxNumGrenade)
								&& (Help.money >= 5)) {
							Help.numGrenade++;
							Help.money -= 5;
							this.moneyDeductionTagList
									.add(new MoneyDeductionTag("-5",
											new Vector2f(710f, 380f)));
						}
						super.buttonClicked.isEffectTaken = true;
					}
					break;
				default:
					break;
				}
			}
			super.isTranAnimFinished = false;
		}
		if (super.mousePositionList.size() > 0) {
			super.mousePositionList.clear();
		}
	}
}