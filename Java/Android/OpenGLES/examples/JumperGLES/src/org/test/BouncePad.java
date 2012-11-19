package org.test;

public class BouncePad extends Entity
{
	protected EBounceDirection m_BounceDir = EBounceDirection.values()[0];
	protected int m_iBlockQuartersFromGround;
	protected int m_iSizeBlocks;
	protected int m_iStartPosAtBlock;
	protected int m_iStartPosDeltaBlockQuarters;

	public BouncePad()
	{
		super();
	}

	public final boolean CanBounce(GamePlayer pPlayer)
	{
		float positionX = pPlayer.GetPositionX();
		float positionY = pPlayer.GetPositionY();
		if ((positionX < (super.GetPositionX() - super.GetHalfSizeX())) || (positionX > (super.GetPositionX() + super.GetHalfSizeX())))
		{
			return false;
		}
		if ((this.m_BounceDir == EBounceDirection.Bounce_Up) && (positionY > super.GetPositionY()))
		{
			return false;
		}
		if ((this.m_BounceDir == EBounceDirection.Bounce_Down) && (positionY < super.GetPositionY()))
		{
			return false;
		}
		return true;
	}

	public final void Create(int iAtBlock, int iDeltaQuarters, int iQuartersFromGround)
	{
		this.Reset();
		this.m_iStartPosAtBlock = iAtBlock;
		this.m_iStartPosDeltaBlockQuarters = iDeltaQuarters;
		this.m_iSizeBlocks = 2;
		this.m_iBlockQuartersFromGround = iQuartersFromGround;
		super.SetSize(this.m_iSizeBlocks * m_game.GetUnitBlockSize(),m_game.GetUnitBlockSize(), false);
		super.SetCollSize(super.GetSizeX(), super.GetSizeY() * 0.5f);
	}

	public final EBounceDirection GetBounceDirection()
	{
		return this.m_BounceDir;
	}

	private int GetEndPosAtBlock()
	{
		int num = (int) Math.ceil((double)((this.m_iStartPosDeltaBlockQuarters + (this.m_iSizeBlocks * 4)) * 0.25f));
		return (this.m_iStartPosAtBlock + (num - 1));
	}

	public final float GetPositionFromGround()
	{
		return (super.GetHalfSizeY() + (this.m_iBlockQuartersFromGround * m_game.GetUnitBlockQuarterSize()));
	}

	@Override
	public boolean Init()
	{
		this.CreateSpriteAnim(1, 0, true);
		this.CreateSpriteFrame("bounce_pad");
		return true;
	}

	@Override
	public void Reset()
	{
		super.Reset();
	}

	public final void SetBounceDirection(EBounceDirection bounceDir)
	{
		this.m_BounceDir = bounceDir;
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

	public enum EBounceDirection
	{
		Bounce_Up,
		Bounce_Down;

		public int getValue()
		{
			return this.ordinal();
		}

		public static EBounceDirection forValue(int value)
		{
			return values()[value];
		}
	}
}