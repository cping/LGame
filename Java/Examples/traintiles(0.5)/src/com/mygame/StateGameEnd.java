package com.mygame;


public class StateGameEnd extends GameState
{
	private Button backbutton;
	private int drawtick;

	private Sprite endBush;
	private Sprite endCar;
	private Sprite endCoal;
	private Sprite endShadow;
	private Sprite endSky;
	private Sprite endStar;
	private Sprite endTile;
	private Sprite endTrain;
	private Sprite endTree;
	
	private Sprite fireworks;
	private Sprite fireworksbig;
	private int lastSound;
	private boolean levelEnd;

	private int[] particleTick = new int[15];
	private int[] particleX = new int[15];
	private int[] particleY = new int[15];

	private Sprite smoke;

	public StateGameEnd(GameCore parent)
	{
		super.initState(parent);
		this.smoke = new Sprite("smoke", 1, 1, 0x12, false);
		this.endBush = new Sprite("end_bush", 1, 1, 0x22, false);
		this.endCar = new Sprite("end_car", 1, 1, 0x22, false);
		this.endCoal = new Sprite("end_coal", 1, 1, 0x22, false);
		this.endShadow = new Sprite("end_shadow", 1, 1, 0x22, false);
		this.endSky = new Sprite("end_sky", 1, 1, 9, false);
		this.endStar = new Sprite("end_star", 1, 1, 0x22, false);
		this.endTile = new Sprite("end_tile", 1, 1, 0x21, false);
		this.endTrain = new Sprite("end_train", 1, 1, 0x22, false);
		this.endTree = new Sprite("end_tree", 1, 1, 0x22, false);
		this.fireworks = new Sprite("fireworks", 3, 1, 0x12, false);
		this.fireworksbig = new Sprite("fireworksbig", 1, 1, 0x12, false);
		this.endTile = new Sprite("end_tile", 1, 1, 0x21, false);

	}

	@Override
	public void activateState()
	{
		this.levelEnd = false;
		this.lastSound = 0;
		super.game.setMenuMusicQuieter(true);
		super.game.startMenuMusic(false);
		if (super.game.getValue(EValues.EValueDoGameEndAnimation) > 0)
		{
			this.levelEnd = true;
			super.game.setValue(EValues.EValueDoGameEndAnimation, -1);
		}
		this.drawtick = 0;
		for (int i = 0; i < 15; i++)
		{
			this.particleX[i] = -1;
		}
		if (this.levelEnd)
		{
			this.backbutton = new Button(EButtonTypes.ENormal, "btnplay", 0x24, 0);
		}
		else
		{
			this.backbutton = new Button(EButtonTypes.ENormal, "btnback", 9, 0);
		}
	}

	public final void addParticle(int x, int y)
	{
		for (int i = 0; i < 15; i++)
		{
			if (this.particleX[i] == -1)
			{
				this.particleX[i] = x;
				this.particleY[i] = y;
				this.particleTick[i] = this.drawtick;
				return;
			}
		}
	}

	@Override
	public void backButtonPressed()
	{
		if (!this.levelEnd)
		{
			super.game.changeState(EStates.EGameStateMainMenu);
		}
	}

	@Override
	public void deactivateState()
	{
	}

	@Override
	public void paint(Painter painter)
	{
		int w = super.game.getW();
		int h = super.game.getH();
		int drawtick = this.drawtick;
		int num4 = -20;
		int num5 = this.endSky.getWidth();
		for (int i = 0; i < w; i += num5)
		{
			this.endSky.Paint(painter, (float) i, 0f, 0);
		}
		int num7 = num4 - ((drawtick / 4) % num5);
		int num8 = (drawtick / 4) / num5;
		int num9 = 4;
		int num10 = this.endSky.getHeight() - num9;
		for (int j = num7; j < (w + num5); j += num5)
		{
			GameUtils.initRandom((((num8 + 750) * 0xabe63a) ^ 0xbec88e) & 0xfffffff);
			int num12 = 3 + ((GameUtils.getRandom() >> 3) % 4);
			if (num12 > 0)
			{
				for (int num13 = 0; num13 < num12; num13++)
				{
					int num14 = j + ((GameUtils.getRandom() >> 4) % num5);
					int num15 = num9 + ((GameUtils.getRandom() >> 3) % num10);
					float scalex = 0.4f + (((float)((GameUtils.getRandom() >> 3) % 0x19)) / 40f);
					float num17 = num14 - (((float)(drawtick % 4)) / 4f);
					this.endStar.PaintScaled(painter, num17, (float) num15, 0, scalex, scalex);
				}
			}
			num8++;
		}
		if (this.levelEnd)
		{
			for (int num18 = 0; num18 < 3; num18++)
			{
				int num19 = drawtick - (0x2e * num18);
				int num20 = num19 % 140;
				int num21 = num19 / 140;
				if ((num21 > 0) && (num19 > 0))
				{
					GameUtils.initRandom((((((num18 * 300) + (num21 * 3)) + 0x11) * 0xabe6) ^ 0xbec88e) & 0xfffffff);
					int num22 = (GameUtils.getRandom() >> 4) % 0x4c;
					num20 -= num22;
					if ((num20 >= 0) && (num20 < 0x40))
					{
						int num23 = ((GameUtils.getRandom() >> 4) % ((w - num5) - num5)) + num5;
						int num24 = ((GameUtils.getRandom() >> 4) % (((num10 * 2) / 3) - num5)) + num5;
						int frame = (GameUtils.getRandom() >> 4) % 3;
						float num26 = (num23 - num20) - (((float)(drawtick % 4)) / 4f);
						float num27 = num24;
						if (num20 == 0)
						{
							this.lastSound++;
							if (this.lastSound > 3)
							{
								this.lastSound = 0;
							}
						
						}
						num20 -= 0x1a;
						if (num20 > 0)
						{
							if (num20 < 10)
							{
								float num29 = 0.5f + (((float) num20) / 10f);
								this.fireworksbig.PaintScaled(painter, num26, num27, 0, num29, num29);
							}
							int num30 = 0xfa0 + ((GameUtils.getRandom() >> 4) & 0x7ff);
							for (int num31 = 0; num31 < 0x19; num31++)
							{
								int num32 = num20 - ((GameUtils.getRandom() >> 4) % 8);
								if ((num32 > 0) && (num32 < 30))
								{
									int angle = (GameUtils.getRandom() >> 4) % 360;
									int num34 = (GameUtils.sin((num32 * 90) / 30) * num5) / (num30 + ((GameUtils.getRandom() >> 4) & 0x1ff));
									float num35 = num26 + (((float)(GameUtils.cos(angle) * num34)) / 8192f);
									float num36 = num27 + (((float)(GameUtils.sin(angle) * num34)) / 8192f);
									float num37 = 1f;
									this.fireworks.PaintScaled(painter, num35, num36, frame, num37, num37);
								}
								else
								{
									GameUtils.getRandom();
									GameUtils.getRandom();
								}
							}
						}
					}
				}
			}
		}
		num5 = (this.endTile.getWidth() * 80) / 100;
		for (int k = num4 - (drawtick % num5); k < w; k += num5)
		{
			this.endTile.Paint(painter, (float) k, (float) h, 0);
		}
		int num39 = (this.endTrain.getWidth() * 0x3f) / 100;
		int num40 = (w * 2) / 5;
		int num41 = h - ((this.endTile.getHeight() * 40) / 100);
		int num42 = this.endShadow.getWidth() / 0x10;
		int num43 = this.endShadow.getHeight() / 5;
		this.endShadow.Paint(painter, (float)((num40 + num39) + num42), (float)(num41 + num43), 0);
		this.endShadow.PaintScaled(painter, (float)(num40 + num42), (float)(num41 + num43), 0, 0.5f, 1f);
		this.endShadow.Paint(painter, (float)((num40 - num39) + num42), (float)(num41 + num43), 0);
		GameUtils.initRandom(((((this.drawtick + 500) / 2) * 0xabe63) ^ 0xbec35e) & 0xfffffff);
		int num44 = (((GameUtils.getRandom() >> 3) & 0x1ff) < 150) ? ((GameUtils.getRandom() >> 3) & 1) : 0;
		this.endCar.Paint(painter, (float)(num40 - num39), (float)(num41 + num44), 0);
		num44 = (((GameUtils.getRandom() >> 3) & 0x1ff) < 150) ? ((GameUtils.getRandom() >> 3) & 1) : 0;
		this.endCoal.Paint(painter, (float) num40, (float)(num41 + num44), 0);
		num44 = (((GameUtils.getRandom() >> 3) & 0x1ff) < 150) ? ((GameUtils.getRandom() >> 3) & 1) : 0;
		this.endTrain.Paint(painter, (float)(num40 + num39), (float)(num41 + num44), 0);
		int x = num40 + ((this.endTrain.getWidth() * 80) / 100);
		int y = num41 - ((this.endTrain.getHeight() * 0x4b) / 100);
		if ((this.drawtick % 6) == 0)
		{
			this.addParticle(x, y);
		}
		int num47 = this.endTrain.getWidth();
		int num48 = this.endTrain.getWidth();
		int num49 = this.smoke.getHeight() * 2;
		for (int m = 0; m < 15; m++)
		{
			if (this.particleX[m] != -1)
			{
				int num51 = ((this.drawtick - this.particleTick[m]) << 10) / 0x2d;
				if (num51 > 0x400)
				{
					this.particleX[m] = -1;
				}
				else
				{
					int num52 = this.particleX[m];
					int num53 = this.particleY[m];
					GameUtils.initRandom((((this.particleTick[m] * 0xace6) + 0xbaab) ^ 0xe246a) & 0xfffffff);
					int num54 = num47 / 2;
					int num55 = ((GameUtils.getRandom() >> 3) % num54) - (num54 / 2);
					int num56 = ((GameUtils.getRandom() >> 3) % num54) - (num54 / 2);
					num52 += (num55 * num51) >> 10;
					num53 += (num56 * num51) >> 10;
					num52 -= (num48 * num51) >> 10;
					num53 -= (num49 * num51) >> 11;
					float num57 = ((float)(0x400 - num51)) / ((float)(0x400 + ((GameUtils.getRandom() >> 3) & 0x1ff)));
					painter.setOpacity((float) num57);
					float num58 = 0.6f + ((0.8f * num51) / ((float)(0x400 + ((GameUtils.getRandom() >> 3) & 0xff))));
					this.smoke.PaintScaled(painter, (float) num52, (float) num53, 0, num58, num58);
					painter.setOpacity(1.0f);
				}
			}
		}
		num7 = num4 - (drawtick % num5);
		num8 = drawtick / num5;
		for (int n = num7; n < (w + num5); n += num5)
		{
			GameUtils.initRandom((((num8 + 700) * 0xabe63) ^ 0xbec88e) & 0xfffffff);
			int num60 = (GameUtils.getRandom() >> 3) & 0x1ff;
			if (num60 < 200)
			{
				float num61 = (GameUtils.sin((this.drawtick * 4) + (num8 * 0x4b)) * 3.5f) / 8092f;
				if (num60 < 100)
				{
					this.endBush.Paint(painter, (float)(n + (num5 / 2)), (float)(h + 5), 0, num61);
				}
				else
				{
					this.endTree.Paint(painter, (float)(n + (num5 / 2)), (float)(h + 5), 0, num61);
				}
			}
			num8++;
		}
		if (!this.levelEnd)
		{
			
		}
		if (this.levelEnd && (this.drawtick < 80))
		{

		}
		if (this.levelEnd && (this.drawtick > 160))
		{
				
		}
		if (!this.levelEnd)
		{
			if (this.backbutton.paint(painter, super.game, this.backbutton.getW() / 12, h - ((this.backbutton.getH() * 13) / 12)))
			{
				super.game.changeState(EStates.EGameStateMainMenu);
				super.game.clearMouseStatus();
				super.game.doButtonPressSound();
			}
		}
		else if ((this.drawtick > 320) && this.backbutton.paint(painter, super.game, w - (this.backbutton.getW() / 12), h - (this.backbutton.getH() / 12)))
		{
			super.game.changeState(EStates.EGameStateMainMenu);
			super.game.clearMouseStatus();
			super.game.doButtonPressSound();
		}
	}

	@Override
	public void tick()
	{
		this.drawtick++;
	}
}