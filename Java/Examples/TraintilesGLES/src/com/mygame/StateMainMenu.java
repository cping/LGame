package com.mygame;

import java.util.ArrayList;

import loon.core.RefObject;
import loon.core.geom.RectBox;
import loon.utils.MathUtils;


public class StateMainMenu extends GameState
{

	private int exitTicks;
	private StateGame gameState;

	private Sprite mainmenu_background;
	private Button mainmenu_exit;
	private Button mainmenu_info;
	private Sprite mainmenu_logo;
	private Button mainmenu_play;
	private Sprite mainmenu_shadow;
	private Button mainmenu_sounds;
	private Sprite medalssmall;

	private Sprite menubg;
	private ArrayList<RectBox> points = new ArrayList<RectBox>();
	private int tileh;
	private int tilew;
	private int trainPos;
	private boolean trial;
	private Sprite trialGetFull;

	public StateMainMenu(GameCore parent)
	{
		super.initState(parent);
		this.mainmenu_background = new Sprite("menu", 1, 1, 9, true);
		this.mainmenu_logo = new Sprite("menu_logo", 1, 1, 9, true);
		this.mainmenu_shadow = new Sprite("menu_shadow", 1, 1, 9, true);
		this.mainmenu_play = new Button(EButtonTypes.ENormal, "btnplay", 0x12, 5);
		this.mainmenu_exit = new Button(EButtonTypes.ENormal, "exit3", 0x12, 5);
		this.mainmenu_sounds = new Button(EButtonTypes.ESwitch, "mute11", 0x12, 5);
		this.mainmenu_info = new Button(EButtonTypes.ENormal, "info", 0x12, 5);
		this.medalssmall = new Sprite("medalssmall", 3, 1, 0x12, true);
		this.trialGetFull = new Sprite("trial-getfull", 1, 1, 9, true);
		this.menubg = new Sprite("menubg", 1, 1, 9, true);
		this.mainmenu_sounds.toggle(super.game.getSettings().m_sounds);
		this.tilew = 0x4a;
		this.tileh = 0x3d;
		int y = this.tileh * 2;
		this.points.add(new RectBox(0, 0, 4, 3));
		this.points.add(new RectBox(0, this.tileh, 2, 2));
		this.points.add(new RectBox(0, y, 3, 2));
		this.points.add(new RectBox(this.tilew, y, 1, 1));
		this.points.add(new RectBox(this.tilew * 2, y, 1, 1));
		this.points.add(new RectBox(this.tilew * 3, y, 1, 1));
		this.points.add(new RectBox(this.tilew * 4, y, 1, 1));
		this.points.add(new RectBox(this.tilew * 5, y, 1, 1));
		this.points.add(new RectBox(this.tilew * 6, y, 1, 1));
		this.points.add(new RectBox(this.tilew * 7, y, 1, 1));
		this.points.add(new RectBox(this.tilew * 8, y, 6, 1));
		this.points.add(new RectBox(this.tilew * 8, this.tileh, 2, 0));
		this.points.add(new RectBox(this.tilew * 8, 0, 5, 0));
		this.points.add(new RectBox(this.tilew * 7, 0, 1, 3));
		this.points.add(new RectBox(this.tilew * 6, 0, 1, 3));
		this.points.add(new RectBox(this.tilew * 5, 0, 1, 3));
		this.points.add(new RectBox(this.tilew * 4, 0, 1, 3));
		this.points.add(new RectBox(this.tilew * 3, 0, 1, 3));
		this.points.add(new RectBox(this.tilew * 2, 0, 1, 3));
		this.points.add(new RectBox(this.tilew, 0, 1, 3));
		this.trainPos = 0x125c;
	}

	@Override
	public void activateState()
	{
		this.trial = super.game.isTrial();
		this.gameState = (StateGame) super.game.getGameState(EStates.EGameStateGame);
		super.game.startMenuMusic(false);
		super.game.setMenuMusicQuieter(false);
		this.exitTicks = -1;
	}

	@Override
	public void backButtonPressed()
	{
		super.game.exit();
	}

	private int checkCloser(int pos, int to, int current)
	{
		int num = (int) MathUtils.distance((float) pos, (float) to);
		if (num < current)
		{
			return num;
		}
		return current;
	}

	@Override
	public void deactivateState()
	{
	}

	@Override
	public void paint(Painter painter)
	{
		int num = super.game.getW();
		int num2 = super.game.getH();
		for (int i = 0; i < num; i += this.menubg.getWidth())
		{
			this.menubg.Paint(painter, (float) i, 0f, 0, 0f);
		}
		int num4 = super.game.getTick();
		int y = num2 - ((this.mainmenu_play.getH() * 60) / 100);
		int num6 = (this.mainmenu_play.getH() * 60) / 100;
		int x = (this.mainmenu_play.getH() * 60) / 100;
		this.mainmenu_background.Paint(painter, 30f, 10f, 0, 0f);
		int num8 = GameUtils.sin(num4 * 7);
		float num9 = 139f;
		float num10 = 65f + (((float)(num8 * 5)) / 8192f);
		int num11 = 0x30;
		int num12 = 0x20;
		int trackx = 0x37;
		int tracky = 0xd0;
		int num15 = (this.points.size() * 0x3e8) / 3;
		for (int j = 0; j < 3; j++)
		{
			int rpos = this.trainPos + (num15 * j);
			int num18 = (rpos / 0x3e8) % this.points.size();
			int pos = (rpos - 780) % (this.points.size() * 0x3e8);
			int current = this.checkCloser(pos, 0x3e8, this.points.size() * 0x3e8);
			current = this.checkCloser(pos, 0xbb8, current);
			current = this.checkCloser(pos, 0x2af8, current);
			current = this.checkCloser(pos, 0x32c8, current);
			current = this.checkCloser(pos, 0x5208, current);
			int num21 = 0;
			if (current < 0x5dc)
			{
				num21 = (450 * (0x5dc - current)) / 0x5dc;
			}
			int num22 = num21 / 8;
			int num23 = num21;
			int num24 = rpos % 0x3e8;
			if (((num18 >= 0) && ((num18 < 4) || ((num18 == 4) && (num24 < 400)))) || (num18 > 13))
			{
				this.paintMenuTrain(painter, ((rpos - 780) - 800) - num23, ETrainTypes.ETrainCarriage, j, trackx, tracky);
				this.paintMenuTrain(painter, (rpos - 780) - num22, ETrainTypes.ETrainCoal, j, trackx, tracky - 6);
				this.paintMenuTrain(painter, rpos, ETrainTypes.ETrainEngine, j, trackx, tracky);
			}
			else
			{
				this.paintMenuTrain(painter, rpos, ETrainTypes.ETrainEngine, j, trackx, tracky);
				this.paintMenuTrain(painter, (rpos - 780) - num22, ETrainTypes.ETrainCoal, j, trackx, tracky - 6);
				this.paintMenuTrain(painter, ((rpos - 780) - 800) - num23, ETrainTypes.ETrainCarriage, j, trackx, tracky);
			}
		}
		this.mainmenu_shadow.Paint(painter, (float) num11, (float) num12, 0);
		this.mainmenu_logo.Paint(painter, num9, num10, 0);
		if (super.game.getSettings().m_levels.get(14) > 0)
		{
			this.medalssmall.Paint(painter, 622f, 302f, 0);
		}
		if (super.game.getSettings().m_levels.get(0x1d) > 0)
		{
			this.medalssmall.Paint(painter, 678f, 288f, 1);
		}
		if (super.game.getSettings().m_levels.get(0x2c) > 0)
		{
			this.medalssmall.Paint(painter, 733f, 276f, 2);
		}
		if (this.mainmenu_sounds.paint(painter, super.game, num - x, y) && (this.exitTicks == -1))
		{
			boolean flag = this.mainmenu_sounds.isToggled();
			super.game.getSettings().m_sounds = flag;
			if (flag)
			{
				
				super.game.startMenuMusic(false);
				super.game.doButtonPressSound();
			}
			else
			{
				super.game.stopMenuMusic(false);
	
			}
			super.game.getSettings().Save();
		}
		if (this.mainmenu_play.paint(painter, super.game, num / 2, y) && (this.exitTicks == -1))
		{
			super.game.clearMouseStatus();
			super.game.changeState(EStates.EGameStateMainLevelSelect);
			super.game.doButtonPressSound();
		}
		if (this.mainmenu_exit.paint(painter, super.game, x, y) && (this.exitTicks == -1))
		{
			super.game.clearMouseStatus();
			this.exitTicks = 0;
			super.game.exit();
			super.game.doButtonPressSound();
		}
		if (this.mainmenu_info.paint(painter, super.game, num - x, num6) && (this.exitTicks == -1))
		{
			super.game.changeState(EStates.EGameStateGameEnd);
			super.game.doButtonPressSound();
		}
		if (this.trial)
		{
			int num25 = 0x10;
			int num26 = super.game.getMouseX();
			int num27 = super.game.getMouseY();
			boolean flag2 = (num26 < ((this.trialGetFull.getWidth() + num25) + num25)) && (num27 < ((this.trialGetFull.getHeight() + num25) + num25));
			int num28 = num25;
			if (flag2 && super.game.isMouseDown())
			{
				num28 += 4;
			}
			this.trialGetFull.Paint(painter, (float) num28, (float) num28, 0);
			if (super.game.isMouseUp() && flag2)
			{
				super.game.clearMouseStatus();
				super.game.setValue(EValues.EValueTrialClickedFrom, 100);
				super.game.changeState(EStates.EGameStateTrial);
			}
		}
	}

	private void paintMenuTrain(Painter painter, int rpos, ETrainTypes type, int color, int trackx, int tracky)
	{
		int num = (rpos / 0x3e8) % this.points.size();
		int drawpos = rpos % 0x3e8;
		int x = (int) this.points.get(num).x;
		int y = (int) this.points.get(num).y;
		int width = this.points.get(num).width;
		int height = this.points.get(num).height;
		int angle = 0;
		int num8 = 0;
		int num9 = 0;
		int tilelength = 0x3e8;
		RefObject<Integer> tempRef_num8 = new RefObject<Integer>(num8);
		RefObject<Integer> tempRef_num9 = new RefObject<Integer>(num9);
		RefObject<Integer> tempRef_angle = new RefObject<Integer>(angle);
		Train.getPos(tempRef_num8, tempRef_num9, tempRef_angle, x * 10, y * 10, ETileTypes.forValue(width), drawpos, this.tilew * 10, this.tileh * 10, tilelength, EDirections.forValue(height));
		num8 = tempRef_num8.argvalue;
		num9 = tempRef_num9.argvalue;
		angle = tempRef_angle.argvalue;
		int num11 = ((GameUtils.cos(-16) * num8) - (GameUtils.sin(-16) * num9)) >> 13;
		int num12 = ((GameUtils.sin(-16) * num8) + (GameUtils.cos(-16) * num9)) >> 13;
		num11 += trackx * 10;
		num12 += tracky * 10;
		if (type == ETrainTypes.ETrainCoal)
		{
			this.gameState.paintTrain(painter, num11, num12, angle + 20,  type.getValue(), color, false, 1f, -5, true);
		}
		else
		{
			this.gameState.paintTrain(painter, num11, num12, angle + 20,  type.getValue(), color, false, 1f, 0, true);
		}
	}

	@Override
	public void tick()
	{
		this.trainPos += 40;
	}
}