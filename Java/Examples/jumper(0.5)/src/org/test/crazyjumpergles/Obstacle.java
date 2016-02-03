package org.test.crazyjumpergles;

public class Obstacle extends Entity
{
	protected int m_iBlockQuartersFromGround;
	protected int m_iSizeXBlockQuarters;
	protected int m_iSizeYBlockQuarters;
	protected int m_iStartPosAtBlock;
	protected int m_iStartPosDeltaBlockQuarters;

	public Obstacle()
	{
		super();
	}

	public final void Create(int iAtBlock, int iDeltaQuarters, int iSizeXQuarters, int iSizeYQuarters, int iQuartersFromGround)
	{
		String str;
		this.Reset();
		this.m_iStartPosAtBlock = iAtBlock;
		this.m_iStartPosDeltaBlockQuarters = iDeltaQuarters;
		this.m_iSizeXBlockQuarters = iSizeXQuarters;
		this.m_iSizeYBlockQuarters = iSizeYQuarters;
		this.m_iBlockQuartersFromGround = iQuartersFromGround;
		super.SetSize(this.m_iSizeXBlockQuarters * m_game.GetUnitBlockQuarterSize(), this.m_iSizeYBlockQuarters * m_game.GetUnitBlockQuarterSize(), false);
		super.SetCollSize(super.GetSizeX(), super.GetSizeY());
		if ((iQuartersFromGround > 0) && (iSizeYQuarters == 0x20))
		{
			str = "column_" + (new Integer(iSizeXQuarters)).toString();
		}
		else
		{
			str = "obstacle_" + (new Integer(iSizeXQuarters)).toString() + "x" + (new Integer(iSizeYQuarters)).toString();
		}
		super.SetSpriteAnimCurrentFrameByTexName(str);
	}

	private int GetEndPosAtBlock()
	{
		int num = (int) Math.ceil((double)((this.m_iStartPosDeltaBlockQuarters + this.m_iSizeXBlockQuarters) * 0.25f));
		return (this.m_iStartPosAtBlock + (num - 1));
	}

	@Override
	public boolean Init()
	{
		this.CreateSpriteAnim(1, 0, false);
		this.CreateSpriteFrame("obstacle_5x4");
		this.CreateSpriteFrame("obstacle_5x6");
		this.CreateSpriteFrame("obstacle_5x9");
		this.CreateSpriteFrame("obstacle_5x12");
		this.CreateSpriteFrame("obstacle_6x4");
		this.CreateSpriteFrame("obstacle_6x5");
		this.CreateSpriteFrame("obstacle_6x8");
		this.CreateSpriteFrame("obstacle_6x13");
		this.CreateSpriteFrame("obstacle_9x9");
		this.CreateSpriteFrame("obstacle_11x11");
		this.CreateSpriteFrame("obstacle_11x13");
		this.CreateSpriteFrame("obstacle_12x6");
		this.CreateSpriteFrame("obstacle_14x8");
		this.CreateSpriteFrame("obstacle_20x3");
		this.CreateSpriteFrame("obstacle_21x2");
		this.CreateSpriteFrame("column_4");
		this.CreateSpriteFrame("column_6");
		this.CreateSpriteFrame("column_8");
		this.CreateSpriteFrame("column_20");
		return true;
	}

	@Override
	public void Reset()
	{
		super.Reset();
		super.m_bVisible = false;
	}

	@Override
	public boolean Tick(int deltaMS)
	{
		if (!super.Tick(deltaMS))
		{
			return false;
		}
		Ground ground = m_game.GetGround();
		if (ground.IsBlockVisible(this.m_iStartPosAtBlock) || ground.IsBlockVisible(this.GetEndPosAtBlock()))
		{
			super.m_bVisible = true;
			float num = (ground.GetBlockPosOnScreen(this.m_iStartPosAtBlock) - m_game.GetUnitBlockHalfSize()) + (this.m_iStartPosDeltaBlockQuarters * m_game.GetUnitBlockQuarterSize());
			float num2 = this.m_iBlockQuartersFromGround * m_game.GetUnitBlockQuarterSize();
			super.SetPositionX(num + super.GetHalfSizeX(), false);
			super.SetPositionY((m_game.GetGroundPosY() - num2) - super.GetHalfSizeY(), false);
		}
		else
		{
			super.m_bVisible = false;
		}
		return true;
	}
}