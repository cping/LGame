package com.zombiedefence.free;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.timer.GameTime;

public class ScreenLevelup extends Screen {
	private Vector2f diplayTexturePosition;
	private java.util.ArrayList<MoneyDeductionTag> moneyDeductionTagList;
	public static ScrollablePane scrollablePane;
	public static LTexture t2DAAGun;
	public static LTexture t2DBAR;
	public static LTexture t2DBarrierBroken;
	public static LTexture t2DBarrierOriginal;
	public static LTexture t2DBrowning;
	public static LTexture t2DButtonBack;
	public static LTexture t2DButtonBuy;
	public static LTexture t2DButtonNext;
	public static LTexture t2DCardAAGun;
	public static LTexture t2DCardBAR;
	public static LTexture t2DCardBrowning;
	public static LTexture t2dCardColt;
	public static LTexture t2DCardKarabiner98;
	public static LTexture t2DCardM1Garand;
	public static LTexture t2DCardNambuType14;
	public static LTexture t2DCardPPSH41;
	public static LTexture t2DCardSVT40;
	public static LTexture t2DCardTompsonM1928;
	public static LTexture t2DCardVickers;
	public static LTexture t2DCardWebleyRevolver;
	public static LTexture t2DCardWinchester;
	public static LTexture t2DCardZhongZheng;
	public static LTexture t2DColt;
	public static LTexture t2DCurrentWeaponPlate;
	public static LTexture t2DKarabiner98;
	public static LTexture t2DM1Garand;
	public static LTexture t2DNambuType14;
	public static LTexture t2DPane;
	public static LTexture t2DPlateTextures;
	public static LTexture t2DPPSH41;
	public static LTexture t2DSVT40;
	public static LTexture t2DTitle;
	public static LTexture t2DTompsonM1928;
	public static LTexture t2DVickers;
	public static LTexture t2DWebleyRevolver;
	public static LTexture t2DWinchester;
	public static LTexture t2DZhongZheng;
	public static LTexture t2DPlateTexture;
	private java.util.ArrayList<LTexture> weaponCardList;

	public ScreenLevelup() {
		super.screenPause = new ScreenPause(this, Help.GameScreen.LevelUp);
	}

	@Override
	public void Draw(SpriteBatch batch) {
		super.Draw(batch);
		if (!super.isPaused) {
			scrollablePane.Draw(batch);
			for (MoneyDeductionTag tag : this.moneyDeductionTagList) {
				tag.Draw(batch);
			}
			batch.draw(t2DTitle, 10f, 20f);
			batch.draw(t2DCurrentWeaponPlate, 640f, 90f);
			batch.draw(Help.currentWeapon.texture, 650f, 120f, null,
					LColor.white, 0f, 0f, 0f, 0.4f, SpriteEffects.None);
			batch.drawString(Screen.ariel14, Help.currentWeapon.name,
					660f, 195f, LColor.white);
			batch.draw(this.weaponCardList.get(scrollablePane.iSelectedItem),
					230f, 90f);
			if (((Weapon) scrollablePane.itemList
					.get(scrollablePane.iSelectedItem)).name
					.equalsIgnoreCase("Sd.Kfz 7/1")) {
				batch.drawString(Screen.ariel18,
						"Must've learned skill 'AAGun'",230f,
								315f, LColor.white);
			}
			batch.draw(
					scrollablePane.itemList.get(scrollablePane.iSelectedItem).texture,
					this.diplayTexturePosition, null, LColor.white, 0f,
					0f, 0f, 0.9f, SpriteEffects.None);
			batch.drawString(
					Screen.ariel14,
					((Weapon) scrollablePane.itemList
							.get(scrollablePane.iSelectedItem)).price + "",
					555f, 398f, LColor.white);
			batch.drawString(Screen.myFont, "$" + Help.money, 
					640f, 380f, LColor.white);
		}
	}

	@Override
	public void LoadContent() {
		super.LoadContent();
		ScreenGameplay.rand = new Random();
		ScreenGameplay.rifleSound = new SoundEffect();
		ScreenGameplay.soundThompson = new SoundEffect();
		ScreenGameplay.soundNambu = new SoundEffect();
		ScreenGameplay.ZhongZhengSound = new SoundEffect();
		ScreenGameplay.pistolSound = new SoundEffect();
		ScreenGameplay.soundWebley = new SoundEffect();
		ScreenGameplay.soundWinchester = new SoundEffect();
		ScreenGameplay.grenadeSound = new SoundEffect();
		ScreenGameplay.soundBAR = new SoundEffect();
		ScreenGameplay.reloadSound = new SoundEffect();
		ScreenGameplay.soundPPSH41 = new SoundEffect();
		ScreenGameplay.soundSVT40 = new SoundEffect();
		ScreenGameplay.soundVickers = new SoundEffect();
		ScreenGameplay.soundBrowning = new SoundEffect();
		super.bgTexture = Global.Load("ScratchBG");
		t2DTitle = Global.Load("TitleLevelup1");
		t2DButtonBack = Global.Load("ButtonBack");
		t2DButtonNext = Global.Load("ButtonNext");
		t2DButtonBuy = Global.Load("ButtonBuy");
		t2DPane = Global.Load("ScrollablePane");
		t2DSVT40 = Global.Load("Svt40");
		t2DPPSH41 = Global.Load("PPSH41");
		t2DWinchester = Global.Load("Winchester");
		t2DColt = Global.Load("Colt 1908");
		t2DNambuType14 = Global.Load("NambuType14");
		t2DWebleyRevolver = Global.Load("WebleyRevolver");
		t2DKarabiner98 = Global.Load("Karabiner98");
		t2DZhongZheng = Global.Load("ZhongZheng");
		t2DM1Garand = Global.Load("M1Garand");
		t2DBAR = Global.Load("BAR");
		t2DTompsonM1928 = Global.Load("TompsonM1928");
		t2DVickers = Global.Load("Vickers");
		t2DBrowning = Global.Load("Browning");
		t2DAAGun = Global.Load("SdKfz");
		t2DCardPPSH41 = Global.Load("PPSH41Card");
		t2DCardWinchester = Global.Load("WinchesterCard");
		t2DCardNambuType14 = Global.Load("NambuCard");
		t2DCardWebleyRevolver = Global.Load("WebleyRevolverCard");
		t2DCardKarabiner98 = Global.Load("Karabiner98kCard");
		t2DCardZhongZheng = Global.Load("ZhongZhengCard");
		t2DCardM1Garand = Global.Load("M1GarandCard");
		t2DCardBAR = Global.Load("BARCard");
		t2DCardTompsonM1928 = Global.Load("ThompsonCard");
		t2DCardSVT40 = Global.Load("SVT40Card");
		t2DCardVickers = Global.Load("VickerCard");
		t2DCardAAGun = Global.Load("SdKfz7Card");
		t2dCardColt = Global.Load("ColtCard");
		t2DCardBrowning = Global.Load("BrowningCard");
		ScreenGameplay.t2DBunkerAA = Global.Load("BunkerAAGun");
		t2DBarrierBroken = Global.Load("Barrier_Broken");
		t2DPlateTexture = Global.Load("WeaponPlate");
		t2DCurrentWeaponPlate = Global.Load("CurrentWeaponPlate");
		super.buttonList.add(new Button(t2DButtonNext,
				new Vector2f(750f, 450f), 0f, Help.ButtonID.Proceed, 15));
		super.buttonList.add(new Button(t2DButtonBuy, new Vector2f(720f, 310f),
				0f, Help.ButtonID.Buy, 0));
		super.screenPause.LoadContent();
		scrollablePane = new ScrollablePane(t2DPane, new Vector2f(50f, 90f));
		this.weaponCardList = new java.util.ArrayList<LTexture>();
		scrollablePane.AddItem(new Weapon(t2DNambuType14,
				ScreenGameplay.soundNambu, new Vector2f(0f, 0f), "NambuType14",
				7, 30, 10, 4, 0.1396263f, 0x2d));
		this.weaponCardList.add(t2DCardNambuType14);
		scrollablePane.AddItem(new Weapon(t2DColt, ScreenGameplay.soundNambu,
				new Vector2f(0f, 0f), "Colt 1908", 7, 30, 12, 6, 0.122173f,
				0x37));
		this.weaponCardList.add(t2dCardColt);
		scrollablePane.AddItem(new Weapon(t2DZhongZheng,
				ScreenGameplay.ZhongZhengSound, new Vector2f(0f, 0f),
				"ZhongZheng", 1, 0x2d, 10, 15, 0.05235988f, 80));
		this.weaponCardList.add(t2DCardZhongZheng);
		scrollablePane.AddItem(new Weapon(t2DKarabiner98,
				ScreenGameplay.rifleSound, new Vector2f(0f, 0f),
				"Karabiner98K", 1, 30, 10, 0x10, 0.05235988f, 0x5f));
		this.weaponCardList.add(t2DCardKarabiner98);
		scrollablePane.AddItem(new Weapon(t2DWebleyRevolver,
				ScreenGameplay.soundWebley, new Vector2f(0f, 0f), "Webley", 6,
				30, 15, 15, 0.1396263f, 110));
		this.weaponCardList.add(t2DCardWebleyRevolver);
		scrollablePane.AddItem(new Weapon(t2DWinchester,
				ScreenGameplay.soundWinchester, new Vector2f(0f, 0f),
				"Winchester", 2, 60, 15, 30, 0.2617994f, 140));
		this.weaponCardList.add(t2DCardWinchester);
		scrollablePane.AddItem(new Weapon(t2DM1Garand,
				ScreenGameplay.pistolSound, new Vector2f(0f, 0f), "M1Garand",
				8, 50, 9, 0x10, 0.08726647f, 0x109));
		this.weaponCardList.add(t2DCardM1Garand);
		scrollablePane.AddItem(new Weapon(t2DSVT40, ScreenGameplay.soundSVT40,
				new Vector2f(0f, 0f), "SVT40", 10, 50, 10, 0x10, 0.08726647f,
				290));
		this.weaponCardList.add(t2DCardSVT40);
		scrollablePane
				.AddItem(new Weapon(t2DBAR, ScreenGameplay.soundBAR,
						new Vector2f(0f, 0f), "BAR", 20, 80, 6, 0x12,
						0.122173f, 0x18b));
		this.weaponCardList.add(t2DCardBAR);
		scrollablePane.AddItem(new Weapon(t2DPPSH41,
				ScreenGameplay.soundPPSH41, new Vector2f(220f, 0f), "PPSH41",
				30, 80, 3, 0x11, 0.1396263f, 0x221));
		this.weaponCardList.add(t2DCardPPSH41);
		scrollablePane.AddItem(new Weapon(t2DTompsonM1928,
				ScreenGameplay.soundThompson, new Vector2f(0f, 0f),
				"TompsonM1928", 30, 80, 4, 0x16, 0.122173f, 590));
		this.weaponCardList.add(t2DCardTompsonM1928);
		scrollablePane.AddItem(new Weapon(t2DVickers,
				ScreenGameplay.soundVickers, new Vector2f(0f, 0f), "Vickers",
				0x7d, 120, 4, 0x23, 0.122173f, 0x3d9));
		this.weaponCardList.add(t2DCardVickers);
		scrollablePane.AddItem(new Weapon(t2DBrowning,
				ScreenGameplay.soundBrowning, new Vector2f(0f, 0f),
				"Browning M1919", 0x7d, 120, 3, 0x2a, 0.122173f, 0x52d));
		this.weaponCardList.add(t2DCardBrowning);
		scrollablePane.AddItem(new Weapon(t2DAAGun, ScreenGameplay.soundNambu,
				new Vector2f(0f, 0f), "Sd.Kfz 7/1", 360, 150, 1, 0x3a,
				0.1570796f, 0x6d6));
		this.weaponCardList.add(t2DCardAAGun);
		this.moneyDeductionTagList = new java.util.ArrayList<MoneyDeductionTag>();
		for (DrawableObject obj2 : scrollablePane.itemList) {
			obj2.scale = new Vector2f(0.3f, 0.3f);
		}
		Help.currentWeapon = (Weapon) scrollablePane.itemList.get(0);
		Help.AIWeapon = (Weapon) scrollablePane.itemList.get(2);
		this.diplayTexturePosition = new Vector2f(240f, 100f);
	}

	@Override
	public void Update(GameTime gameTime) {

		super.Update(gameTime);
		scrollablePane.Update(super.mousePositionList);
		for (int i = 0; i < this.moneyDeductionTagList.size(); i++) {
			this.moneyDeductionTagList.get(i).Update();
			if (this.moneyDeductionTagList.get(i).isDead) {
				this.moneyDeductionTagList.remove(i);
			}
		}
		if (!super.isTranAnimFinished) {
			if (super.mousePositionList.size() > 0) {
				super.mousePositionList.clear();
			}
			return;
		}
		switch (super.buttonClicked.getButtonID()) {
		case Proceed:
			Help.currentGameState = Help.GameScreen.LevelUp2;
			super.buttonClicked = null;
			for (Button button2 : super.buttonList) {
				button2.ButtonInitialize();
			}

			super.isTranAnimFinished = false;

			if (super.mousePositionList.size() > 0) {
				super.mousePositionList.clear();
			}
			return;

		case Back:
			Help.currentGameState = Help.GameScreen.MainMenu;
			super.buttonClicked = null;
			for (Button button : super.buttonList) {
				button.ButtonInitialize();
			}
			super.isTranAnimFinished = false;

			if (super.mousePositionList.size() > 0) {
				super.mousePositionList.clear();
			}
			return;

		case Buy: {
			if (super.buttonClicked.isEffectTaken) {
				super.isTranAnimFinished = false;

				if (super.mousePositionList.size() > 0) {
					super.mousePositionList.clear();
				}
				return;
			}
			Weapon weapon = (Weapon) scrollablePane.itemList
					.get(scrollablePane.iSelectedItem);
			if ((Help.money < weapon.price)
					|| !(Help.currentWeapon.name != weapon.name)) {
				super.isTranAnimFinished = false;

				if (super.mousePositionList.size() > 0) {
					super.mousePositionList.clear();
				}
				return;
			}
			if (weapon.name.equalsIgnoreCase("Sd.Kfz 7/1")) {
				for (Button button3 : Help.currentBunker.skillsGained) {
					if (button3.getButtonID() == Help.ButtonID.TagAAGun) {
						Help.currentWeapon = new Weapon(weapon.texture,
								weapon.firingSound, weapon.position,
								weapon.name, weapon.magSize,
								weapon.reloadLength, weapon.framesPerFire,
								weapon.power, weapon.accuracy, weapon.price);
						Help.money -= weapon.price;
						int num2 = -weapon.price;
						this.moneyDeductionTagList.add(new MoneyDeductionTag(
								num2 + "", new Vector2f(680f, 380f)));
						if (scrollablePane.iSelectedItem == (scrollablePane.itemList
								.size() - 1)) {
							Help.currentBunker.texture = ScreenGameplay.t2DBunkerAA;
						} else {
							Help.currentBunker.texture = ScreenGameplay.t2DBunkerBottom;
						}
						break;
					}
				}
			} else {
				Help.currentWeapon = new Weapon(weapon.texture,
						weapon.firingSound, weapon.position, weapon.name,
						weapon.magSize, weapon.reloadLength,
						weapon.framesPerFire, weapon.power, weapon.accuracy,
						weapon.price);
				Help.money -= weapon.price;
				int num3 = -weapon.price;
				this.moneyDeductionTagList.add(new MoneyDeductionTag(num3 + "",
						new Vector2f(680f, 380f)));
				if (scrollablePane.iSelectedItem == (scrollablePane.itemList
						.size() - 1)) {
					Help.currentBunker.texture = ScreenGameplay.t2DBunkerAA;
				} else {
					Help.currentBunker.texture = ScreenGameplay.t2DBunkerBottom;
				}
			}
			break;
		}
		default:
			super.isTranAnimFinished = false;

			if (super.mousePositionList.size() > 0) {
				super.mousePositionList.clear();
			}
			return;
		}
		super.buttonClicked.isEffectTaken = true;

	}
}