package org.test.crazyjumpergles;

public class SpeedChange extends Entity
{
	protected boolean m_bChangedSpeed;
	protected int m_iPosAtBlock;
	protected ESpeedChangeType m_SpeedChangeType = ESpeedChangeType.values()[0];

	public SpeedChange()
	{
		super();
	}

	@Override
	public boolean CanCollide()
	{
		if (!super.CanCollide())
		{
			return false;
		}
		if (this.m_bChangedSpeed)
		{
			return false;
		}
		return true;
	}

	public final void ChangeGameSpeed()
	{
		this.m_bChangedSpeed = true;
		if (this.m_SpeedChangeType == ESpeedChangeType.SpeedChange_UpStart)
		{
			m_game.EnterHiSpeed();
			m_game.ChangeGameSpeed(m_game.GetUnitBlockSize() * 2f);
		}
		else if (this.m_SpeedChangeType == ESpeedChangeType.SpeedChange_UpEnd)
		{
			m_game.ExitHiSpeed();
			m_game.ChangeGameSpeed(-m_game.GetUnitBlockSize() * 2f);
		}
		else if (this.m_SpeedChangeType == ESpeedChangeType.SpeedChange_DownStart)
		{
			m_game.EnterLowSpeed();
			m_game.ChangeGameSpeed(-m_game.GetUnitBlockSize() * 2f);
		}
		else if (this.m_SpeedChangeType == ESpeedChangeType.SpeedChange_DownEnd)
		{
			m_game.ExitLowSpeed();
			m_game.ChangeGameSpeed(m_game.GetUnitBlockSize() * 2f);
		}
	}

	public final void Create(int iAtBlock)
	{
		this.Reset();
		this.m_iPosAtBlock = iAtBlock;
	}

	public final ESpeedChangeType GetSpeedChangeType()
	{
		return this.m_SpeedChangeType;
	}

	@Override
	public boolean Init()
	{
		this.CreateSpriteAnim(1, 0, true);
		this.CreateSpriteFrame("speed_normal");
		this.CreateSpriteFrame("speed_up");
		this.CreateSpriteFrame("speed_down");
		super.SetSize(m_game.GetUnitBlockSize() * 2f, m_game.GetUnitBlockSize() * 2f, false);
		super.SetCollSize(super.GetSizeX() * 0.5f, super.GetSizeY() * 10f);
		return true;
	}

	@Override
	public void Reset()
	{
		super.Reset();
		this.m_bChangedSpeed = false;
	}

	public final void SetSpeedChangeType(ESpeedChangeType type)
	{
		this.m_SpeedChangeType = type;
		if ((this.m_SpeedChangeType == ESpeedChangeType.SpeedChange_UpEnd) || (this.m_SpeedChangeType == ESpeedChangeType.SpeedChange_DownEnd))
		{
			super.SetSpriteAnimCurrentFrame(0);
		}
		else if (this.m_SpeedChangeType == ESpeedChangeType.SpeedChange_UpStart)
		{
			super.SetSpriteAnimCurrentFrame(1);
		}
		else if (this.m_SpeedChangeType == ESpeedChangeType.SpeedChange_DownStart)
		{
			super.SetSpriteAnimCurrentFrame(2);
		}
	}

	@Override
	public boolean Tick(int deltaMS)
	{
		if (!super.Tick(deltaMS))
		{
			return false;
		}
		Ground ground = m_game.GetGround();
		if (ground.IsBlockVisible(this.m_iPosAtBlock))
		{
			super.m_bVisible = true;
			super.SetPositionX(ground.GetBlockPosOnScreen(this.m_iPosAtBlock), false);
			super.SetPositionY(m_game.GetGroundPosY() - super.GetHalfSizeY(), false);
		}
		else
		{
			super.m_bVisible = false;
		}
		return true;
	}

	public enum ESpeedChangeType
	{
		SpeedChange_UpStart,
		SpeedChange_UpEnd,
		SpeedChange_DownStart,
		SpeedChange_DownEnd;

		public int getValue()
		{
			return this.ordinal();
		}

		public static ESpeedChangeType forValue(int value)
		{
			return values()[value];
		}
	}
}