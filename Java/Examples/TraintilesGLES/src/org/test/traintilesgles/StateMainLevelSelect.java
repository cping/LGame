package org.test.traintilesgles;

public class StateMainLevelSelect extends GameState
{
	private int animationLevel;
	private int animticks;
	private boolean doFadeOut;

	private Sprite levelSelect;
	private Button levelSelectBack;
	private Sprite levelsribbon;
	private Sprite lockedwhite;
	private Sprite medals;
	private Sprite medalslock;
	private Sprite medalsshine;
	private Sprite menubg;
	private Sprite numberSprite;
	private Sprite shine;
	private int soundeffect;
	private boolean trial;
	private Sprite trialFree;
	private Sprite trialFull;

	public StateMainLevelSelect(GameCore parent)
	{
		super.initState(parent);
		this.menubg = new Sprite("menubg", 1, 1, 9, true);
		this.medals = new Sprite("medals", 3, 1, 0x12, true);
		this.medalslock = new Sprite("medalslock", 1, 1, 0x12, true);
		this.lockedwhite = new Sprite("lockedwhite", 1, 1, 10, true);
		this.numberSprite = new Sprite("bignumbers2", 11, 1, 9, true);
		this.levelSelectBack = new Button(EButtonTypes.ENormal, "btnback", 9, 0);
		this.levelSelect = new Sprite("levelsbg", 3, 1, 0x12, false);
		this.levelsribbon = new Sprite("levelsribbon", 1, 1, 10, false);
		this.shine = new Sprite("shine", 1, 1, 0x12, false);
		this.medalsshine = new Sprite("medalsshine", 1, 1, 0x12, false);
		this.trialFree = new Sprite("trial-free", 1, 1, 10, false);
		this.trialFull = new Sprite("trial-full", 1, 1, 10, false);
		this.soundeffect = -1;
	}

	@Override
	public void activateState()
	{
		this.trial = super.game.isTrial();
		int num = super.game.getValue(EValues.EValueDoLevelSelectAnimation);
		if (num != -1)
		{
			this.animationLevel = num;
			super.game.setValue(EValues.EValueDoLevelSelectAnimation, -1);
			this.animticks = 0;
			this.levelSelectBack.disable(true);
		}
		else
		{
			this.animticks = -1;
			this.levelSelectBack.disable(false);
			super.game.startMenuMusic(false);
		}
		this.doFadeOut = false;
	}

	@Override
	public void backButtonPressed()
	{
		super.game.changeState(EStates.EGameStateMainMenu);
	}

	@Override
	public void deactivateState()
	{
		if (this.soundeffect != -1)
		{
			this.soundeffect = -1;
		}
	}

	@Override
	public void paint(Painter painter)
	{
		int w = super.game.getW();
		int h = super.game.getH();
		for (int i = 0; i < w; i += this.menubg.getWidth())
		{
			this.menubg.Paint(painter, (float) i, 0f, 0, 0f);
		}
		int num4 = this.levelSelect.getWidth();
		int num5 = this.levelSelect.getHeight();
		int num6 = (num4 * 80) / 100;
		int num7 = (num5 * 80) / 100;
		int num8 = (w - (3 * num4)) / 4;
		int num9 = 10;
		int ribbonY = (-num5 / 2) + 15;
		int num11 = num8;
		int num12 = -30;
		int num13 = -30;
		boolean flag = this.animticks != -1;
		float x = 1f;
		int num15 = num11 + (num4 / 2);
		int num16 = num9 + (num5 / 2);
		painter.save();
		painter.translate((float) num15, (float) num16);
		boolean flag2 = GameUtils.isInside(super.game.getMouseX(), super.game.getMouseY(), num15 - (num6 / 2), num16 - (num7 / 2), num6, num7);
		boolean flag3 = (!flag && flag2) && super.game.isMouseDown();
		boolean flag4 = (!flag && flag2) && super.game.isMouseUp();
		if (flag4 || flag3)
		{
			x = 0.8f;
		}
		else
		{
			x = 0.95f + (((float) GameUtils.sin(super.game.getTick() * 3)) / 163840f);
		}
		painter.scale(x, x);
		this.levelSelect.Paint(painter, 0f, 0f, 0);
		if (flag4)
		{
			super.game.clearMouseStatus();
			super.game.setValue(EValues.EValueSelectedMainLevel, 0);
			super.game.changeState(EStates.EGameStateLevelSelect);
			super.game.doButtonPressSound();
		}
		this.levelsribbon.Paint(painter, 0f, (float) ribbonY, 0, 0f);
		if (this.trial)
		{
			this.trialFree.Paint(painter, 0f, (float) ribbonY, 0);
		}
		else
		{
			this.paintNumbers(painter, 0, -(this.levelsribbon.getWidth() / 2), ribbonY, this.levelsribbon.getWidth(), this.levelsribbon.getHeight());
		}
		boolean flag5 = super.game.getSettings().m_levels.get(14) > 0;
		boolean animMedal = (this.animticks != -1) && (this.animationLevel == 14);
		int pos = !animMedal ? 0 : (((this.animticks - 60) << 10) / 80);
		int num18 = 0;
		int y = num12;
		if (flag5)
		{
			this.paintMedal(painter, num18, y, animMedal, pos, 0);
		}
		painter.restore();
		painter.save();
		num15 = w / 2;
		flag2 = GameUtils.isInside(super.game.getMouseX(), super.game.getMouseY(), num15 - (num6 / 2), num16 - (num7 / 2), num6, num7);
		flag3 = (!flag && flag2) && super.game.isMouseDown();
		flag4 = (!flag && flag2) && super.game.isMouseUp();
		boolean flag7 = super.game.getSettings().m_levels.get(4) < 1;
		boolean flag8 = (this.animticks != -1) && (this.animationLevel == 4);
		pos = !flag8 ? 0 : (((this.animticks - 60) << 10) / 80);
		if (flag7)
		{
			x = 0.8f;
			painter.setOpacity(0.7f);
		}
		else if (flag4 || flag3)
		{
			x = 0.8f;
		}
		else
		{
			x = 0.95f + (((float) GameUtils.sin((super.game.getTick() * 3) + 0x41)) / 163840f);
			if (flag8)
			{
				if (pos < 0x300)
				{
					x = 0.8f;
					painter.setOpacity(0.7f);
				}
				else
				{
					float num20 = x - 0.8f;
					x = 0.8f + ((num20 * (pos - 0x300)) / 256f);
					painter.setOpacity(0.7f + (((pos - 0x300) * 0.3f) / 256f));
				}
			}
		}
		painter.translate((float) num15, (float) num16);
		painter.scale(x, x);
		this.levelSelect.Paint(painter, 0f, 0f, 1);
		painter.setOpacity(1.0f);
		if (!flag7 && flag4)
		{
			super.game.clearMouseStatus();
			if (this.trial)
			{
				super.game.setValue(EValues.EValueTrialClickedFrom, 0);
				super.game.changeState(EStates.EGameStateTrial);
			}
			else
			{
				super.game.setValue(EValues.EValueSelectedMainLevel, 1);
				super.game.changeState(EStates.EGameStateLevelSelect);
			}
			super.game.doButtonPressSound();
		}
		this.levelsribbon.Paint(painter, 0f, (float) ribbonY, 0, 0f);
		if (flag7 || flag8)
		{
			GameUtils.initRandom(((this.animticks + 0x200) * 0xabe63) ^ 0x3eba348);
			int num21 = (GameUtils.sin((pos * 180) / 0x400) * 0x20) >> 13;
			float num22 = num21;
			num21 /= 4;
			int num23 = ((pos <= 0) || (num21 < 1)) ? 0 : (((GameUtils.getRandom() >> 3) % num21) - (num21 / 2));
			int num24 = ((pos <= 0) || (num21 < 1)) ? 0 : (((GameUtils.getRandom() >> 3) % num21) - (num21 / 2));
			float num25 = (pos <= 0) ? 0f : (((GameUtils.sin(pos / 3) * num22) / 8192f) - (num22 / 2f));
			int num26 = (h - num13) + this.medalslock.getHeight();
			int num27 = (pos < 0x300) ? 0 : (((0x2000 - GameUtils.sin((((pos - 0x300) * 90) / 0x100) + 90)) * num26) >> 13);
			if (pos > 0x300)
			{
				this.paintStars(painter, 0, num13 + (this.medalslock.getHeight() / 10), (pos - 0x300) << 2);
			}
			this.medalslock.Paint(painter, (float) num23, (float)((num13 + num24) + num27), 0, num25 - (((float) num27) / 3f));
			if (pos > 0x200)
			{
				painter.setOpacity(((float)(0x400 - pos)) / 512f);
			}
			if (!this.trial)
			{
				this.lockedwhite.Paint(painter, 0f, (float)(ribbonY + 6), 0);
			}
		}
		if (!flag7 && !this.trial)
		{
			if (flag8)
			{
				painter.setOpacity((pos < 0x200) ? 0.0f : (((float)(pos - 0x200)) / 512f));
			}
			this.paintNumbers(painter, 1, -(this.levelsribbon.getWidth() / 2), ribbonY, this.levelsribbon.getWidth(), this.levelsribbon.getHeight());
			painter.setOpacity(1.0f);
		}
		if (this.trial)
		{
			painter.setOpacity(1.0f);
			this.trialFull.Paint(painter, 0f, (float) ribbonY, 0);
		}
		flag5 = super.game.getSettings().m_levels.get(0x1d) > 0;
		animMedal = (this.animticks != -1) && (this.animationLevel == 0x1d);
		pos = !animMedal ? 0 : (((this.animticks - 60) << 10) / 80);
		num18 = 0;
		y = num12;
		if (flag5)
		{
			this.paintMedal(painter, num18, y, animMedal, pos, 1);
		}
		painter.restore();
		painter.save();
		num15 = (w - num11) - (num4 / 2);
		flag7 = super.game.getSettings().m_levels.get(0x13) < 1;
		flag8 = (this.animticks != -1) && (this.animationLevel == 0x13);
		flag2 = GameUtils.isInside(super.game.getMouseX(), super.game.getMouseY(), num15 - (num6 / 2), num16 - (num7 / 2), num6, num7);
		flag3 = (!flag && flag2) && super.game.isMouseDown();
		flag4 = (!flag && flag2) && super.game.isMouseUp();
		pos = !flag8 ? 0 : (((this.animticks - 60) << 10) / 80);
		if (flag7)
		{
			x = 0.8f;
			painter.setOpacity(0.7f);
		}
		else if (flag4 || flag3)
		{
			x = 0.8f;
		}
		else
		{
			x = 0.95f + (((float) GameUtils.sin((super.game.getTick() * 3) + 130)) / 163840f);
			if (flag8)
			{
				if (pos < 0x300)
				{
					x = 0.8f;
					painter.setOpacity(0.7f);
				}
				else
				{
					float num28 = x - 0.8f;
					x = 0.8f + ((num28 * (pos - 0x300)) / 256f);
					painter.setOpacity(0.7f + (((pos - 0x300) * 0.3f) / 256f));
				}
			}
		}
		painter.translate((float) num15, (float) num16);
		painter.scale(x, x);
		this.levelSelect.Paint(painter, 0f, 0f, 2);
		painter.setOpacity(1.0f);
		if (!flag7 && flag4)
		{
			super.game.clearMouseStatus();
			if (super.game.isTrial())
			{
				super.game.changeState(EStates.EGameStateTrial);
			}
			else
			{
				super.game.setValue(EValues.EValueSelectedMainLevel, 2);
				super.game.changeState(EStates.EGameStateLevelSelect);
			}
			super.game.doButtonPressSound();
		}
		this.levelsribbon.Paint(painter, 0f, (float) ribbonY, 0, 0f);
		if (flag7 || flag8)
		{
			GameUtils.initRandom(((this.animticks + 0x200) * 0xabe63) ^ 0x3eba348);
			int num29 = (GameUtils.sin((pos * 180) / 0x400) * 0x20) >> 13;
			float num30 = num29;
			num29 /= 4;
			int num31 = ((pos <= 0) || (num29 < 1)) ? 0 : (((GameUtils.getRandom() >> 3) % num29) - (num29 / 2));
			int num32 = ((pos <= 0) || (num29 < 1)) ? 0 : (((GameUtils.getRandom() >> 3) % num29) - (num29 / 2));
			float num33 = (pos <= 0) ? 0f : (((GameUtils.sin(pos / 3) * num30) / 8192f) - (num30 / 2f));
			int num34 = (h - num13) + this.medalslock.getHeight();
			int num35 = (pos < 0x300) ? 0 : (((0x2000 - GameUtils.sin((((pos - 0x300) * 90) / 0x100) + 90)) * num34) >> 13);
			if (pos > 0x300)
			{
				this.paintStars(painter, 0, num13 + (this.medalslock.getHeight() / 10), (pos - 0x300) << 2);
			}
			this.medalslock.Paint(painter, (float) num31, (float)((num13 + num32) + num35), 0, num33 - (((float) num35) / 3f));
			if (pos > 0x200)
			{
				painter.setOpacity(((float)(0x400 - pos)) / 512f);
			}
			if (!this.trial)
			{
				this.lockedwhite.Paint(painter, 0f, (float)(ribbonY + 6), 0);
			}
		}
		if (!flag7 && !this.trial)
		{
			if (flag8)
			{
				painter.setOpacity((pos < 0x200) ? 0f : (((float)(pos - 0x200)) / 512f));
			}
			this.paintNumbers(painter, 2, -(this.levelsribbon.getWidth() / 2), ribbonY, this.levelsribbon.getWidth(), this.levelsribbon.getHeight());
			painter.setOpacity(1.0f);
		}
		if (this.trial)
		{
			this.trialFull.Paint(painter, 0f, (float) ribbonY, 0);
		}
		flag5 = super.game.getSettings().m_levels.get(0x2c) > 0;
		animMedal = (this.animticks != -1) && (this.animationLevel == 0x2c);
		pos = !animMedal ? 0 : (((this.animticks - 60) << 10) / 80);
		num18 = 0;
		y = num12;
		if (flag5)
		{
			this.paintMedal(painter, num18, y, animMedal, pos, 2);
		}
		painter.restore();
		if (this.levelSelectBack.paint(painter, super.game, this.levelSelectBack.getW() / 12, h - ((this.levelSelectBack.getH() * 13) / 12)))
		{
			super.game.doButtonPressSound();
			super.game.changeState(EStates.EGameStateMainMenu);
			super.game.clearMouseStatus();
		}
		if ((flag && this.doFadeOut) && (this.animticks > 30))
		{
			if (this.animticks >= 60)
			{
				
			}
			else
			{
		
			}
		}
	}

	private void paintMedal(Painter painter, int x, int y, boolean animMedal, int pos, int frame)
	{
		GameUtils.initRandom(0x3eba348);
		if (animMedal)
		{
			painter.setOpacity(((float) pos) / 1024f);
			float angle = ((float)(GameUtils.sin(((pos * 180) >> 10) + 90) + 0x2000)) / 91f;
			this.medals.Paint(painter, (float) x, (float) y, frame, angle);
			painter.setOpacity(1.0f);
		}
		else
		{
			this.medals.Paint(painter, (float) x, (float) y, frame);
		}
		if (animMedal)
		{
			for (int i = 0; i < 0x20; i++)
			{
				int num3 = pos - ((GameUtils.getRandom() >> 4) & 0xff);
				if (num3 > 0)
				{
					int num4 = ((GameUtils.getRandom() >> 4) + num3) % 360;
					int num5 = (((this.medals.getWidth() * 0x2d) / 100) * ((num3 < 0x200) ? num3 : 0x200)) / 0x200;
					int num6 = x + ((GameUtils.cos(num4) * num5) >> 13);
					int num7 = y + ((GameUtils.sin(num4) * num5) >> 13);
					float scalex = 0.5f + ((num3 < 0x200) ? (((float) num3) / 1024f) : 0.5f);
					if (num3 > 0x200)
					{
						painter.setOpacity(((float)(0x300 - num3)) / 256f);
					}
					this.shine.PaintScaled(painter, (float) num6, (float) num7, 0, scalex, scalex);
					painter.setOpacity(1f);
				}
				else
				{
					GameUtils.getRandom();
				}
			}
			if (pos > 0x200)
			{
				int num9 = pos - 0x200;
				int num10 = (this.medals.getWidth() * 0x69) / 100;
				int num11 = (x - (num10 / 2)) + ((num9 * num10) / 0x200);
				float num12 = ((float) GameUtils.sin((num9 * 180) / 0x200)) / 8100f;
				if (num9 > 0x180)
				{
					painter.setOpacity(((float)(0x200 - num9)) / 128f);
				}
				this.medalsshine.PaintScaled(painter, (float) num11, (float) y, 0, num12, num12);
				painter.setOpacity(1f);
			}
		}
	}

	public final void paintNumbers(Painter painter, int mainLevel, int ribbonX, int ribbonY, int ribbonWidth, int ribbonHeight)
	{
		int num = mainLevel * 15;
		int frame = 0;
		for (int i = 0; i < 15; i++)
		{
			if (super.game.getSettings().m_levels.get(i + num) > 0)
			{
				frame++;
			}
		}
		int num4 = (ribbonY + ((ribbonHeight - this.numberSprite.getHeight()) / 2)) - 0x11;
		int num5 = 0;
		if (frame < 10)
		{
			num5 = ribbonX + ((ribbonWidth - (4 * this.numberSprite.getWidth())) / 2);
			this.numberSprite.Paint(painter, (float) num5, (float) num4, frame);
			num5 += this.numberSprite.getWidth();
		}
		else
		{
			num5 = ribbonX + ((ribbonWidth - (5 * this.numberSprite.getWidth())) / 2);
			this.numberSprite.Paint(painter, (float) num5, (float) num4, frame / 10);
			num5 += this.numberSprite.getWidth();
			this.numberSprite.Paint(painter, (float) num5, (float) num4, frame % 10);
			num5 += this.numberSprite.getWidth();
		}
		this.numberSprite.Paint(painter, (float) num5, (float) num4, 10);
		num5 += this.numberSprite.getWidth();
		this.numberSprite.Paint(painter, (float) num5, (float) num4, 1);
		num5 += this.numberSprite.getWidth();
		this.numberSprite.Paint(painter, (float) num5, (float) num4, 5);
	}

	private void paintStars(Painter painter, int x, int y, int pos)
	{
		if (pos > 0x400)
		{
			pos = 0x400;
		}
		int startvalue = 0x3eba348;
		GameUtils.initRandom(startvalue);
		for (int i = 0; i < 0x20; i++)
		{
			int num3 = pos - ((GameUtils.getRandom() >> 4) & 0xff);
			if (num3 > 0)
			{
				int angle = (GameUtils.getRandom() >> 4) % 360;
				int num5 = (this.medals.getWidth() * num3) / 0x300;
				int num6 = x + ((GameUtils.cos(angle) * num5) >> 13);
				int num7 = y + ((GameUtils.sin(angle) * num5) >> 13);
				float scalex = 1f + (((float) num3) / 2048f);
				if (num3 > 0x200)
				{
					painter.setOpacity(((float)(0x300 - num3)) / 256f);
				}
				this.shine.PaintScaled(painter, (float) num6, (float) num7, 0, scalex, scalex);
				painter.setOpacity(1.0f);
			}
			else
			{
				GameUtils.getRandom();
			}
		}
	}

	@Override
	public void tick()
	{
		if (this.animticks != -1)
		{
			this.animticks++;
			if ((this.animticks == 30) && ((this.animationLevel == 4) || (this.animationLevel == 0x13)))
			{
			
			}
			if ((this.animticks == 40) && (((this.animationLevel == 14) || (this.animationLevel == 0x1d)) || (this.animationLevel == 0x2c)))
			{
			
			}
			int num = this.doFadeOut ? 60 : (((this.animationLevel == 4) || (this.animationLevel == 0x13)) ? 140 : 140);
			if (this.animticks > num)
			{
				if (this.doFadeOut)
				{
					this.animationLevel = -1;
					super.game.changeState(EStates.EGameStateGameEnd);
				}
				else if (super.game.getValue(EValues.EValueDoGameEndAnimation) > 0)
				{
					this.animationLevel = -1;
					this.doFadeOut = true;
					this.animticks = 0;
				}
				else
				{
					this.animticks = -1;
					this.levelSelectBack.disable(false);
					super.game.clearMouseStatus();
					super.game.startMenuMusic(false);
				}
			}
		}
	}
}