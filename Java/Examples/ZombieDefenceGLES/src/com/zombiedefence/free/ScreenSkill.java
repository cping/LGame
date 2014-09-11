package com.zombiedefence.free;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.timer.GameTime;
import loon.utils.MathUtils;

public class ScreenSkill extends Screen {
	private Button buttonCancel;
	private Button buttonConfirm;
	private boolean isDetailShowing;
	public static boolean isInitialised;
	public static boolean isToBeDeleted;
	public static LTexture maskSkill;
	public Vector2f pTagAAGun;
	public Vector2f pTagAim;
	public Vector2f pTagArtillery;
	public Vector2f pTagBoost;
	public Vector2f pTagExtendedMag;
	public Vector2f pTagFieldRepair;
	public Vector2f pTagGunner;
	public Vector2f pTagLearning;
	public Vector2f pTagOverRepair;
	public Vector2f pTagReloading;
	public static LTexture t2DBattleEngineer;
	public static LTexture t2DButtonBack;
	public static LTexture t2DButtonPlay;
	public static LTexture t2DExtendedMag;
	public static LTexture t2DPreReqNotMet;
	public static LTexture t2DRifleman;
	public static LTexture t2DSkillDetail;
	public static LTexture t2DTagAAGun;
	public static LTexture t2DTagAim;
	public static LTexture t2DTagArtillery;
	public static LTexture t2DTagBoost;
	public static LTexture t2DTagFieldRepair;
	public static LTexture t2DTagGunner;
	public static LTexture t2DTagLearning;
	public static LTexture t2DTagOverRepair;
	public static LTexture t2DTagReload;
	public static TagAAGun tagAAGun;
	public static TagAim tagAim;
	public static TagArtillerySupport tagArtillery;
	public static TagBoost tagBoost;
	public static TagExtendedMag tagExtendedMag;
	public static TagFieldRepair tagFieldRepair;
	public static TagGunner tagGunner;
	public static TagLearning tagLearning;
	public static TagOverRepair tagOverRepair;
	public static TagReloading tagReloading;
	private Button tagSelected;

	public ScreenSkill() {
		super.screenPause = new ScreenPause(this, Help.GameScreen.Skill);
		this.isDetailShowing = false;
		this.iScreen = 0;
		isInitialised = false;
		isToBeDeleted = false;
		this.pTagAAGun = new Vector2f(440f, 420f);
		this.pTagAim = new Vector2f(460f, 240f);
		this.pTagLearning = new Vector2f(327f, 367f);
		this.pTagGunner = new Vector2f(655f, 367f);
		this.pTagOverRepair = new Vector2f(327f, 196f);
		this.pTagFieldRepair = new Vector2f(655f, 196f);
		this.pTagReloading = new Vector2f(327f, 196f);
		this.pTagExtendedMag = new Vector2f(655f, 196f);
		this.pTagArtillery = new Vector2f(327f, 196f);
		this.pTagBoost = new Vector2f(655f, 196f);
	}

	@Override
	public void Draw(SpriteBatch batch) {
		if (isInitialised) {
			super.Draw(batch);
			if (!super.isPaused) {
				float num = 0f;
				if (this.tagSelected != null) {
					num = 0.6f;
				}
				batch.draw(super.maskTexture, 0f, 0f,
						Global.Pool.getColor(1f, 1f, 1f, num));
				if (Help.profession == Help.Profession.BattleEngineer) {
					batch.draw(t2DBattleEngineer, 60f, 90f);
				} else if (Help.profession == Help.Profession.Rifleman) {
					batch.draw(t2DRifleman, 60f, 90f, LColor.white);
				}
				Vector2f v = Screen.myFont.getOrigin("" + Help.AvailSkillPoint);
				batch.drawString(Screen.myFont, "" + Help.AvailSkillPoint,
						740f, 450f, LColor.wheat, 0f, v.x, v.y, 1f);
				v = Screen.ariel14.getOrigin("" + Help.numSkill1);
				batch.drawString(Screen.ariel14, "" + Help.numSkill1,
						this.pTagAAGun.sub(30f, 5f), LColor.wheat, 0f, v, 1f);
				v = Screen.ariel14.getOrigin("" + Help.numSkill2);
				batch.drawString(Screen.ariel14, "" + Help.numSkill2,
						this.pTagAim.sub(30f, 5f), LColor.wheat, 0f, v, 1f);
				v = Screen.ariel14.getOrigin("" + Help.numSkill3);
				batch.drawString(Screen.ariel14, "" + Help.numSkill3,
						this.pTagLearning.sub(30f, 5f), LColor.wheat, 0f, v, 1f);
				v = Screen.ariel14.getOrigin("" + Help.numSkill4);
				batch.drawString(Screen.ariel14, "" + Help.numSkill4,
						this.pTagGunner.sub(30f, 5f), LColor.wheat, 0f, v, 1f);
				if (Help.profession == Help.Profession.BattleEngineer) {
					v = Screen.ariel14.getOrigin("" + Help.numSkill5);
					batch.drawString(Screen.ariel14, "" + Help.numSkill5,
							this.pTagOverRepair.sub(30f, 5f), LColor.wheat, 0f,
							v, 1f);
					v = Screen.ariel14.getOrigin("" + Help.numSkill6);
					batch.drawString(Screen.ariel14, "" + Help.numSkill6,
							this.pTagFieldRepair.sub(30f, 5f), LColor.wheat,
							0f, v, 1f);
				} else if (Help.profession == Help.Profession.Rifleman) {
					v = Screen.ariel14.getOrigin("" + Help.numSkill7);
					batch.drawString(Screen.ariel14, "" + Help.numSkill7,
							this.pTagReloading.sub(30f, 5f), LColor.wheat, 0f,
							v, 1f);
					v = Screen.ariel14.getOrigin("" + Help.numSkill8);
					batch.drawString(Screen.ariel14, "" + Help.numSkill8,
							this.pTagExtendedMag.sub(30f, 5f), LColor.wheat,
							0f, v, 1f);
				} else if (Help.profession == Help.Profession.Commander) {
					v = Screen.ariel14.getOrigin("" + Help.numSkill9);
					batch.drawString(Screen.ariel14, "" + Help.numSkill9,
							this.pTagArtillery.sub(30f, 5f), LColor.wheat, 0f,
							v, 1f);
					v = Screen.ariel14.getOrigin("" + Help.numSkill10);
					batch.drawString(Screen.ariel14, "" + Help.numSkill10,
							this.pTagBoost.sub(30f, 5f), LColor.wheat, 0f, v,
							1f);
				}
				for (Button button : super.buttonList) {
					if (!button.isPrerequisiteMet) {
						RectBox sourceRectangle = null;
						batch.draw(maskSkill, button.position, sourceRectangle,
								LColor.white, 0f, button.origin, 1f,
								SpriteEffects.None);
					}
				}
				if (this.tagSelected != null) {
					batch.draw(t2DSkillDetail, 397f, 240f, null, LColor.white,
							0f, (t2DSkillDetail.getWidth() / 2),
							(t2DSkillDetail.getHeight() / 2), 1f,
							SpriteEffects.None);
					batch.draw(this.tagSelected.texture, 210f, 190f,
							LColor.white);
					batch.drawString(Screen.myFont,
							this.tagSelected.description, 200f, 130f,
							LColor.wheat);
					batch.drawString(Screen.ariel14,
							this.tagSelected.subDescription, 320f, 190f,
							LColor.wheat);
					batch.drawString(Screen.ariel14,
							this.tagSelected.reqDescription, 320f, 275f,
							LColor.wheat);
					this.buttonCancel.Draw(batch);
					if (this.isDetailShowing) {
						this.buttonConfirm.Draw(batch);
					} else {
						batch.draw(t2DPreReqNotMet, 220f, 270f, null,
								Global.Pool.getColor(1f, 1f, 1f, 0.7f),
								MathUtils.toDegrees(-0.3926991f), 0f, 0f, 1f,
								SpriteEffects.None);
					}
				}
			}
		}
	}

	public final void Initialised() {
		switch (Help.profession) {
		case Rifleman:
			super.buttonList.add(tagAAGun);
			super.buttonList.add(tagAim);
			super.buttonList.add(tagLearning);
			super.buttonList.add(tagGunner);
			super.buttonList.add(tagReloading);
			super.buttonList.add(tagExtendedMag);
			break;

		case BattleEngineer:
			super.buttonList.add(tagAAGun);
			super.buttonList.add(tagAim);
			super.buttonList.add(tagLearning);
			super.buttonList.add(tagGunner);
			super.buttonList.add(tagOverRepair);
			super.buttonList.add(tagFieldRepair);
			break;

		case Commander:
			super.buttonList.add(tagAAGun);
			super.buttonList.add(tagAim);
			super.buttonList.add(tagLearning);
			super.buttonList.add(tagGunner);
			super.buttonList.add(tagArtillery);
			super.buttonList.add(tagBoost);
			break;
		default:
			break;
		}
		isInitialised = true;
	}

	@Override
	public void LoadContent() {
		super.LoadContent();
		super.bgTexture = Global.Load("SkillScreen");
		t2DButtonBack = Global.Load("ButtonCancel");
		t2DButtonPlay = Global.Load("ButtonConfirm");
		t2DTagReload = Global.Load("SkillFastReload");
		t2DTagAAGun = Global.Load("SkillAAGunner");
		t2DExtendedMag = Global.Load("SkillExtMag");
		t2DTagAim = Global.Load("SkillSniper");
		t2DTagOverRepair = Global.Load("SkillOverRepair");
		t2DTagFieldRepair = Global.Load("SkillFieldRepair");
		t2DTagLearning = Global.Load("SkillLearner");
		t2DTagGunner = Global.Load("SkillLeader");
		t2DTagArtillery = Global.Load("SkillArtillery");
		t2DTagBoost = Global.Load("SkillBoost");
		t2DSkillDetail = Global.Load("SkillDetailBox");
		t2DPreReqNotMet = Global.Load("PreReqNotMet");
		t2DBattleEngineer = Global.Load("BattleEngineer");
		t2DRifleman = Global.Load("Rifleman");
		maskSkill = Global.Load("MaskSkill");
		tagAAGun = new TagAAGun(t2DTagAAGun, new Vector2f(440f, 420f), 0f,
				Help.ButtonID.TagAAGun, 0);
		tagAim = new TagAim(t2DTagAim, new Vector2f(460f, 240f), 0f,
				Help.ButtonID.TagAim, 0);
		tagLearning = new TagLearning(t2DTagLearning, new Vector2f(327f, 367f),
				0f, Help.ButtonID.TagLearning, 0);
		tagGunner = new TagGunner(t2DTagGunner, new Vector2f(655f, 367f), 0f,
				Help.ButtonID.TagGunner, 0);
		tagOverRepair = new TagOverRepair(t2DTagOverRepair, new Vector2f(327f,
				196f), 0f, Help.ButtonID.TagOverRepair, 0);
		tagFieldRepair = new TagFieldRepair(t2DTagFieldRepair, new Vector2f(
				655f, 196f), 0f, Help.ButtonID.TagFieldRepair, 0);
		tagReloading = new TagReloading(t2DTagReload, new Vector2f(327f, 196f),
				0f, Help.ButtonID.TagReloading, 0);
		tagExtendedMag = new TagExtendedMag(t2DExtendedMag, new Vector2f(655f,
				196f), 0f, Help.ButtonID.TagExtendedMag, 0);
		tagArtillery = new TagArtillerySupport(t2DTagArtillery,
				this.pTagArtillery, 0f, Help.ButtonID.TagArtillery, 0);
		tagBoost = new TagBoost(t2DTagBoost, this.pTagBoost, 0f,
				Help.ButtonID.TagBoost, 0);
		super.buttonList.add(new Button(t2DButtonBack, new Vector2f(60f, 430f),
				0f, Help.ButtonID.Back, 20));
		this.buttonCancel = new Button(t2DButtonBack, new Vector2f(260f, 330f),
				0f, Help.ButtonID.No, 0);
		this.buttonConfirm = new Button(t2DButtonPlay,
				new Vector2f(530f, 330f), 0f, Help.ButtonID.Yes, 0);
		super.screenPause.LoadContent();
	}

	@Override
	public void Update(GameTime gameTime) {
		super.Update(gameTime);
		if (!isInitialised) {
			this.Initialised();
		}
		if (this.iScreen == 0) {
			for (Button button : super.buttonList) {
				button.CheckPrerequisite(Help.currentBunker);
			}
		}
		this.iScreen++;
		if (this.tagSelected == null) {
			if (super.isTranAnimFinished) {
				if (super.buttonClicked.getButtonID() == Help.ButtonID.Back) {
					Help.currentGameState = Help.previousGameState;
					super.buttonClicked = null;
					this.iScreen = 0;
				} else if ((super.buttonClicked != this.buttonConfirm)
						&& (super.buttonClicked != this.buttonCancel)) {
					this.tagSelected = super.buttonClicked;
					super.buttonClicked.isEffectTaken = true;
					if (super.buttonClicked.isPrerequisiteMet
							&& (Help.AvailSkillPoint > 0)) {
						super.buttonList.add(this.buttonCancel);
						super.buttonList.add(this.buttonConfirm);
						this.isDetailShowing = true;
					} else {
						super.buttonList.add(this.buttonCancel);
						this.isDetailShowing = false;
					}
				}
				super.isTranAnimFinished = false;
			}
			if (super.mousePositionList.size() > 0) {
				super.mousePositionList.clear();
			}
			return;
		}
		if (!super.isTranAnimFinished) {
			if (super.mousePositionList.size() > 0) {
				super.mousePositionList.clear();
			}
			return;
		}
		switch (super.buttonClicked.getButtonID()) {
		case Yes:
			if (super.buttonClicked.isEffectTaken) {

				super.isTranAnimFinished = false;

				if (super.mousePositionList.size() > 0) {
					super.mousePositionList.clear();
				}
				return;
			}
			this.tagSelected.ApplyEffect(Help.currentBunker);
			Help.currentBunker.skillsGained.add(this.tagSelected);
			if (this.tagSelected.description.equalsIgnoreCase("AA Gun")) {
				Help.numSkill1++;
			} else if (this.tagSelected.description
					.equalsIgnoreCase("Improve Accuracy")) {
				Help.numSkill2++;
			} else if (this.tagSelected.description
					.equalsIgnoreCase("Learning")) {
				Help.numSkill3++;
			} else if (this.tagSelected.description
					.equalsIgnoreCase("Free Gunner")) {
				Help.numSkill4++;
			} else if (this.tagSelected.description
					.equalsIgnoreCase("Over Repair")) {
				Help.numSkill5++;
			} else if (this.tagSelected.description
					.equalsIgnoreCase("Field Repair")) {
				Help.numSkill6++;
			} else if (this.tagSelected.description
					.equalsIgnoreCase("Faster Reloading")) {
				Help.numSkill7++;
			} else if (this.tagSelected.description
					.equalsIgnoreCase("Extended Magazine")) {
				Help.numSkill8++;
			} else if (this.tagSelected.description
					.equalsIgnoreCase("Artillery Support")) {
				Help.numSkill9++;
			} else if (this.tagSelected.description.equalsIgnoreCase("Boost")) {
				Help.numSkill10++;
			}
			break;

		case No:
			if (!super.buttonClicked.isEffectTaken) {
				this.isDetailShowing = false;
				super.buttonList.remove(this.buttonCancel);
				if (super.buttonList.contains(this.buttonConfirm)) {
					super.buttonList.remove(this.buttonConfirm);
				}
				this.tagSelected = null;
				super.buttonClicked.isEffectTaken = true;
			}
			super.isTranAnimFinished = false;

			if (super.mousePositionList.size() > 0) {
				super.mousePositionList.clear();
			}
			return;

		default:
			super.isTranAnimFinished = false;

			if (super.mousePositionList.size() > 0) {
				super.mousePositionList.clear();
			}
			return;
		}
		Help.AvailSkillPoint--;
		this.isDetailShowing = false;
		buttonList.subList(buttonList.size() - 2, buttonList.size()).clear();
		this.tagSelected = null;
		super.buttonClicked.isEffectTaken = true;
		for (Button button2 : super.buttonList) {
			button2.CheckPrerequisite(Help.currentBunker);
		}

	}
}