package com.mygame;


public class StateSplash extends GameState
{
	private boolean firstPaint;
	private Sprite splashBg;
	private int ticks;

	public StateSplash(GameCore parent)
	{
		super.initState(parent);
		this.splashBg = new Sprite("splash", 1, 1, 9, false);
		this.firstPaint = true;
		super.game.setValue(EValues.EValueDoLevelSelectAnimation, -1);
		super.game.setValue(EValues.EValueDoGameEndAnimation, -1);
	}

	@Override
	public void activateState()
	{
	}

	@Override
	public void deactivateState()
	{
	}

	@Override
	public void paint(Painter painter)
	{
		if (this.firstPaint)
		{
			int num = super.game.getW();
			int num2 = super.game.getH();
			GameUtils.setScreenW(num);
			GameUtils.setScreenH(num2);
			this.firstPaint = false;
		}
		this.splashBg.Paint(painter, 0f, 0f, 0);
	}

	@Override
	public void tick()
	{
		this.ticks++;
		if (this.ticks == 3)
		{
			super.game.loadAllStates();
		}
		if (this.ticks > 0x41)
		{
			super.game.changeState(EStates.EGameStateMainMenu);
		}
	}
}