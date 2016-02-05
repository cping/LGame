package org.test;

public class CSound
{
	private boolean isPlay;
	private MainGame mainGame;

	public CSound(MainGame mainGame, String file)
	{
		this.mainGame = mainGame;
	}

	public final void play()
	{
		if (!this.mainGame.noSound)
		{
			this.isPlay = true;
		}
	}

	public final void update()
	{
		if (this.isPlay)
		{
			this.isPlay = false;
		}
	}
}