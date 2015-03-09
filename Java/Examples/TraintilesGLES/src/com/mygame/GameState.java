package com.mygame;


public class GameState
{
	protected GameCore game;

	public void activateState()
	{
	}

	public void backButtonPressed()
	{
	}

	public void deactivateState()
	{
	}

	public void gameHidden()
	{
	}

	protected void initState(GameCore parent)
	{
		this.game = parent;
	}

	public void paint(Painter painter)
	{
	}

	public void tick()
	{
	}
}