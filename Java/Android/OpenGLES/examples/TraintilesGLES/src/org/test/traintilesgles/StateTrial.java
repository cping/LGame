package org.test.traintilesgles;

public class StateTrial extends GameState
{
	private Button backbutton;
	private EStates previousSate = EStates.values()[0];

	public StateTrial(GameCore parent)
	{
		super.initState(parent);
		this.backbutton = new Button(EButtonTypes.ENormal, "btnback", 9, 0);
	}

	@Override
	public void activateState()
	{

		super.game.startMenuMusic(false);
		if (super.game.getValue(EValues.EValueTrialClickedFrom) == 100)
		{
			this.previousSate = EStates.EGameStateMainMenu;
		}
		else if (super.game.getValue(EValues.EValueTrialClickedFrom) == 50)
		{
			this.previousSate = EStates.EGameStateLevelSelect;
		}
		else
		{
			this.previousSate = EStates.EGameStateMainLevelSelect;
		}
	}

	@Override
	public void backButtonPressed()
	{
		super.game.changeState(this.previousSate);
	}

	@Override
	public void deactivateState()
	{
	
	}

	@Override
	public void paint(Painter painter)
	{
		super.game.getW();
		int num = super.game.getH();
		if (this.backbutton.paint(painter, super.game, this.backbutton.getW() / 12, num - ((this.backbutton.getH() * 13) / 12)))
		{
			super.game.doButtonPressSound();
			super.game.changeState(this.previousSate);
			super.game.clearMouseStatus();
		}
	}

	@Override
	public void tick()
	{
		if (super.game.isMouseUp())
		{
			int num = super.game.getMouseX();
			int num2 = super.game.getMouseY();
			if ((num > 0x1b0) && (num2 > 360))
			{
				super.game.showPurchaseDialog();
			}
		}
	}
}