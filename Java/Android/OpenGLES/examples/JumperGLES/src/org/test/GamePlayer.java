package org.test;

import loon.action.sprite.SpriteBatch;
import loon.utils.MathUtils;

import org.test.common.SpriteAnim;
import org.test.common.Tools;

public class GamePlayer extends Entity
{
	private boolean m_bInBounceSlowMotion;
	private boolean m_bInExitableForceField;
	private boolean m_bInHiSpeed;
	private boolean m_bMovingOut;

	private EEmotionState m_EmotionBase = EEmotionState.values()[0];
	private EEmotionState m_EmotionCurrent = EEmotionState.values()[0];
	private float m_fAbsoluteMaxJumpHeight;
	private float m_fBodyMoveDirY;
	private float m_fBodyMoveMaxDeltaY;
	private float m_fBounceBigDist;
	private float m_fBounceBigHeight;
	private float m_fBounceSmallDist;
	private float m_fBounceSmallHeight;
	private float m_fCurrentBounceFallingSpeedScale;
	private float m_fCurrentBounceStartY;
	private float m_fCurrentJumpDist;
	private float m_fCurrentJumpDistDelta;
	private float m_fCurrentJumpHeight;

	private float m_fJumpBigDist;
	private float m_fJumpBigHeight;
	private float m_fJumpSmallDist;
	private float m_fJumpSmallHeight;
	private int m_iLastLevelTryCount;
	private int m_iLevelsCompleted;
	private int m_iLevelTryCount;
	private SpriteAnim m_pActiveLegsSpriteAnim;
	private BouncePad m_pBouncePad;
	private ForceField m_pInForceField;
	private SpriteAnim m_pLegsMoveSpriteAnim;
	private SpriteAnim m_pLegsStaticSpriteAnim;
	private EState m_State = EState.values()[0];

	public GamePlayer()
	{
		this.m_fBodyMoveMaxDeltaY = 3f;
	}

	private void BeginEmotionState(EEmotionState newEmotionState)
	{
		this.m_EmotionCurrent = newEmotionState;
		super.SetSpriteAnimCurrentFrame(this.m_EmotionCurrent.getValue());
	}

	public final void BeginPlaySession()
	{
		this.m_iLevelsCompleted = 0;
		this.m_iLastLevelTryCount = 0;
	}

	public final void BeginState(EState newState)
	{
		if (this.m_State != newState)
		{
			EState state = this.m_State;
			SpriteAnim pActiveLegsSpriteAnim = this.m_pActiveLegsSpriteAnim;
			this.m_State = newState;
			if (((this.m_State == EState.State_Idle) || (this.m_State == EState.State_Falling)) || (this.m_State == EState.State_FallingOut))
			{
				this.m_pActiveLegsSpriteAnim = this.m_pLegsStaticSpriteAnim;
				this.m_pActiveLegsSpriteAnim.SetCurrentFrame(0, false);
			}
			else if (this.m_State == EState.State_Move)
			{
				this.m_pActiveLegsSpriteAnim = this.m_pLegsMoveSpriteAnim;
				this.m_pActiveLegsSpriteAnim.Reset();
				this.m_pActiveLegsSpriteAnim.Play();
				switch (state)
				{
					case State_JumpSmall:
					case State_JumpBig:
					case State_Falling:
						this.m_fBodyMoveDirY = -1f;
						super.SetSpriteDeltaPositionY(this.m_fBodyMoveMaxDeltaY, false);
						break;
				default:
					break;
				}
			}
			else if (((this.m_State == EState.State_JumpSmall) || (this.m_State == EState.State_JumpBig)) || (this.m_State == EState.State_Bounce))
			{
				this.m_pActiveLegsSpriteAnim = this.m_pLegsStaticSpriteAnim;
				this.m_pActiveLegsSpriteAnim.SetCurrentFrame(1, false);
				super.SetSpriteDeltaPositionY(0f, false);
				if (this.m_State == EState.State_Bounce)
				{
					super.SetRotation(0f);
				}
			}
			else if (this.m_State == EState.State_InForceField)
			{
				this.m_pActiveLegsSpriteAnim = this.m_pLegsStaticSpriteAnim;
				this.m_pActiveLegsSpriteAnim.SetCurrentFrame(2, false);
				super.SetRotation(0f);
			}
			else if (this.m_State == EState.State_Collided)
			{
				this.m_pActiveLegsSpriteAnim = this.m_pLegsStaticSpriteAnim;
				this.m_pActiveLegsSpriteAnim.SetCurrentFrame(0, false);
				super.SetRotation(0f);
				super.SetSpriteDeltaPositionY(0f, false);
			}
			else if (this.m_State == EState.State_LevelFailed)
			{
				this.m_pActiveLegsSpriteAnim = this.m_pLegsStaticSpriteAnim;
				this.m_pActiveLegsSpriteAnim.SetCurrentFrame(0, false);
				super.SetSpriteDeltaPositionY(this.m_fBodyMoveMaxDeltaY, false);
			}
			switch (state)
			{
				case State_JumpSmall:
				case State_JumpBig:
					super.SetRotation(0f);
					break;

				case State_Falling:
					this.m_pInForceField = null;
					break;

				case State_Bounce:
					this.EndBounce();
					break;
			default:
				break;
			}
			if (((pActiveLegsSpriteAnim != this.m_pActiveLegsSpriteAnim) && (pActiveLegsSpriteAnim != null)) && (this.m_pActiveLegsSpriteAnim != null))
			{
				this.m_pActiveLegsSpriteAnim.SetPosition(pActiveLegsSpriteAnim.GetPositionX(), pActiveLegsSpriteAnim.GetPositionY());
			}
		}
	}

	public final void Bounce(BouncePad pBouncePad)
	{
		this.m_pBouncePad = pBouncePad;
		this.m_bInBounceSlowMotion = true;

		if (this.m_State == EState.State_JumpBig)
		{
			this.m_fCurrentJumpDistDelta = 0f;
			this.m_fCurrentJumpDist = this.m_fBounceBigDist;
			this.m_fCurrentJumpHeight = this.m_fBounceBigHeight;
		}
		else
		{
			this.m_fCurrentJumpDistDelta = 0f;
			this.m_fCurrentJumpDist = this.m_fBounceSmallDist;
			this.m_fCurrentJumpHeight = this.m_fBounceSmallHeight;
		}
		this.m_fCurrentBounceStartY = pBouncePad.GetPositionFromGround() + pBouncePad.GetHalfSizeY();
		this.BeginState(EState.State_Bounce);
		this.BeginEmotionState(EEmotionState.Emotion_Bounce);
		m_game.ChangeGameSpeed(-m_game.GetUnitBlockSize());
	}

	public final void CollidedWithObstacle()
	{
		this.BeginState(EState.State_Collided);
		this.BeginEmotionState(EEmotionState.Emotion_Collided);
	}

	public final void EndBounce()
	{
		this.EndBounceSlowMotion();
	}

	private void EndBounceSlowMotion()
	{
		if (this.m_bInBounceSlowMotion)
		{
			this.m_bInBounceSlowMotion = false;
			if (this.m_bInHiSpeed)
			{
				this.BeginEmotionState(EEmotionState.Emotion_HiSpeed);
			}
			else
			{
				this.BeginEmotionState(this.m_EmotionBase);
			}
			m_game.ChangeGameSpeed(m_game.GetUnitBlockSize());
		}
	}

	public final void EnterForceField(ForceField pForceField)
	{
		this.BeginState(EState.State_InForceField);
		super.SetPositionY(m_game.GetGroundPosY() - pForceField.GetPositionFromGround(), false);
		if (pForceField.IsExitable())
		{
			this.m_bInExitableForceField = true;
			m_game.ChangeGameSpeed(m_game.GetUnitBlockSize() * 2f);
		}
		else
		{
			this.m_bInExitableForceField = false;
			m_game.ChangeGameSpeed(m_game.GetUnitBlockSize() * 4f);
		}
		this.m_pInForceField = pForceField;
	}

	public final void EnterHiSpeed()
	{
		this.m_bInHiSpeed = true;
		this.BeginEmotionState(EEmotionState.Emotion_HiSpeed);
	}

	public final void EnterLowSpeed()
	{
	}

	public final void ExitForceField()
	{
		this.BeginState(EState.State_Falling);
		if (this.m_bInExitableForceField)
		{
			m_game.ChangeGameSpeed(-m_game.GetUnitBlockSize() * 2f);
		}
		else
		{
			m_game.ChangeGameSpeed(-m_game.GetUnitBlockSize() * 4f);
		}
	}

	public final void ExitHiSpeed()
	{
		this.m_bInHiSpeed = false;
		this.BeginEmotionState(this.m_EmotionBase);
	}

	public final void ExitLowSpeed()
	{
	}

	public final BouncePad GetBouncedByPad()
	{
		return this.m_pBouncePad;
	}

	public final ForceField GetInForceField()
	{
		return this.m_pInForceField;
	}

	public final int GetLevelTryCount()
	{
		return this.m_iLevelTryCount;
	}

	public final void Go()
	{
		this.BeginState(EState.State_Move);
	}

	@Override
	public boolean Init()
	{
		this.CreateSpriteAnim(1, 0, true);
		this.CreateSpriteFrame("player_standard");
		this.CreateSpriteFrame("player_happy");
		this.CreateSpriteFrame("player_beated");
		this.CreateSpriteFrame("player_tired");
		this.CreateSpriteFrame("player_hispeed");
		this.CreateSpriteFrame("player_collided");
		this.CreateSpriteFrame("player_bounce");
		super.SpriteAnimPlay();
		this.m_pLegsMoveSpriteAnim = new SpriteAnim();
		this.m_pLegsMoveSpriteAnim.EnableBlending(true);
		this.m_pLegsMoveSpriteAnim.SetSize(m_game.GetUnitBlockSize(), m_game.GetUnitBlockSize());
		this.m_pLegsMoveSpriteAnim.SetAnimDuration(300);
		this.m_pLegsMoveSpriteAnim.SetAnimLoopType(1);
		this.m_pLegsMoveSpriteAnim.AddTextureByName("player_move_legs1", false);
		this.m_pLegsMoveSpriteAnim.AddTextureByName("player_move_legs2", false);
		this.m_pLegsMoveSpriteAnim.AddTextureByName("player_move_legs3", false);
		this.m_pLegsMoveSpriteAnim.AddTextureByName("player_move_legs4", false);
		this.m_pLegsMoveSpriteAnim.Play();
		this.m_pLegsStaticSpriteAnim = new SpriteAnim();
		this.m_pLegsStaticSpriteAnim.EnableBlending(true);
		this.m_pLegsStaticSpriteAnim.SetSize(m_game.GetUnitBlockSize(), m_game.GetUnitBlockSize());
		this.m_pLegsStaticSpriteAnim.SetAnimDuration(1);
		this.m_pLegsStaticSpriteAnim.SetAnimLoopType(0);
		this.m_pLegsStaticSpriteAnim.AddTextureByName("player_idle_legs", false);
		this.m_pLegsStaticSpriteAnim.AddTextureByName("player_jump_legs", false);
		this.m_pLegsStaticSpriteAnim.AddTextureByName("player_legs_inforcefield", false);
		this.m_pLegsStaticSpriteAnim.Play();
		super.SetSize(m_game.GetUnitBlockSize(), m_game.GetUnitBlockSize(), false);
		super.SetCollSize(super.GetSizeX() * 0.95f, super.GetSizeY() * 0.95f);
		this.m_fAbsoluteMaxJumpHeight = m_game.GetUnitBlockSize() * 8f;
		this.m_fJumpSmallHeight = m_game.GetUnitBlockSize() * 3f;
		this.m_fJumpSmallDist = m_game.GetUnitBlockSize() * 6f;
		this.m_fJumpBigHeight = m_game.GetUnitBlockSize() * 4.5f;
		this.m_fJumpBigDist = m_game.GetUnitBlockSize() * 9f;
		this.m_fBounceSmallHeight = m_game.GetUnitBlockSize() * 3f;
		this.m_fBounceSmallDist = m_game.GetUnitBlockSize() * 7.5f;
		this.m_fBounceBigHeight = m_game.GetUnitBlockSize() * 4f;
		this.m_fBounceBigDist = m_game.GetUnitBlockSize() * 10f;
		this.m_fCurrentJumpDist = 0f;
		this.Reset();
		return true;
	}

	public final boolean IsBouncing()
	{
		return (this.m_State == EState.State_Bounce);
	}

	public final boolean IsCollided()
	{
		return (this.m_State == EState.State_Collided);
	}

	public final boolean IsFalling()
	{
		return (this.m_State == EState.State_Falling);
	}

	public final boolean IsFallingOut()
	{
		return (this.m_State == EState.State_FallingOut);
	}

	public final boolean IsIdle()
	{
		return (this.m_State == EState.State_Idle);
	}

	public final boolean IsInForceField()
	{
		return (this.m_State == EState.State_InForceField);
	}

	public final boolean IsJumping()
	{
		if ((this.m_State != EState.State_JumpSmall) && (this.m_State != EState.State_JumpBig))
		{
			return false;
		}
		return true;
	}

	public final boolean IsLevelFailed()
	{
		return (this.m_State == EState.State_LevelFailed);
	}

	public final boolean IsMoving()
	{
		return (this.m_State == EState.State_Move);
	}

	public final boolean IsMovingOut()
	{
		return this.m_bMovingOut;
	}

	@Override
	public boolean IsPlayer()
	{
		return true;
	}

	public final void LevelComplete()
	{
		this.m_iLevelsCompleted++;
		this.m_iLastLevelTryCount = this.m_iLevelTryCount;
	}

	@Override
	public void Render(SpriteBatch batch)
	{
		if (this.m_pActiveLegsSpriteAnim != null)
		{
			this.m_pActiveLegsSpriteAnim.Render(batch);
		}
		super.Render(batch);
	}

	@Override
	public void Reset()
	{
		this.m_iLevelTryCount = 0;
		this.Restart();
	}

	public final void Restart()
	{
		super.Reset();
		this.m_iLevelTryCount++;
		if (this.m_iLevelTryCount <= 3)
		{
			this.m_EmotionBase = EEmotionState.Emotion_Standard;
			if ((this.m_iLevelTryCount == 1) && (this.m_iLastLevelTryCount < 3))
			{
				int num = 0x19 + (this.m_iLevelsCompleted * 5);
				if (num > 60)
				{
					num = 60;
				}
				if (Tools.getRand(1, 100) < num)
				{
					this.m_EmotionBase = EEmotionState.Emotion_Happy;
				}
			}
		}
		else if (this.m_iLevelTryCount <= 6)
		{
			this.m_EmotionBase = EEmotionState.Emotion_Beated;
		}
		else
		{
			this.m_EmotionBase = EEmotionState.Emotion_Tired;
		}
		this.m_State = EState.State_None;
		super.m_bVisible = true;
		this.m_bMovingOut = false;
		this.m_fBodyMoveDirY = -1f;
		super.SetRotation(0f);
		super.SetSpriteDeltaPositionY(0f, false);
		this.m_pActiveLegsSpriteAnim = null;
		this.m_bInHiSpeed = false;
		this.m_bInExitableForceField = false;
		this.m_pInForceField = null;
		this.m_bInBounceSlowMotion = false;
		this.m_pBouncePad = null;
		this.BeginState(EState.State_Idle);
		this.BeginEmotionState(this.m_EmotionBase);
		super.SetPosition(m_game.GetUnitBlockSize() * 2.5f, m_game.GetGroundPosY() - super.GetHalfSizeY(), false);
	}

	public final void StartFallingOut()
	{
		this.BeginState(EState.State_FallingOut);
	}

	public final void StartMovingOut()
	{
		this.m_bMovingOut = true;
	}

	@Override
	public boolean Tick(int deltaMS)
	{
		if (!super.Tick(deltaMS))
		{
			return false;
		}
		if (this.IsMoving())
		{
			if (m_game.IsJumpSmall())
			{
				this.BeginState(EState.State_JumpSmall);
				this.m_fCurrentJumpDistDelta = 0f;
				this.m_fCurrentJumpDist = this.m_fJumpSmallDist;
				this.m_fCurrentJumpHeight = this.m_fJumpSmallHeight;
				m_game.ClearJumpSmall();
				m_game.OnJumpSmall();
			}
			else if (m_game.IsJumpBig())
			{
				this.BeginState(EState.State_JumpBig);
				this.m_fCurrentJumpDistDelta = 0f;
				this.m_fCurrentJumpDist = this.m_fJumpBigDist;
				this.m_fCurrentJumpHeight = this.m_fJumpBigHeight;
				m_game.ClearJumpBig();
				m_game.OnJumpBig();
			}
		}
		else if (this.IsFallingOut())
		{
			super.SetPositionY(super.GetPositionY() + (m_game.GetGameSpeed() * super.m_fDeltaTime), false);
		}
		else if (this.IsFalling() || this.IsCollided())
		{
			float num = 1f;
			if (this.IsCollided())
			{
				num = 2f;
			}
			float num2 = m_game.GetGroundPosY() - (super.GetPositionY() + super.GetHalfSizeY());
			float num3 = num2 - ((m_game.GetGameSpeed() * super.m_fDeltaTime) * num);
			if (num3 <= 0f)
			{
				if (this.IsCollided())
				{
					m_game.LevelFailed();
					this.BeginState(EState.State_LevelFailed);
				}
				else
				{
					this.BeginState(EState.State_Move);
				}
				m_game.SetGroundDeltaY(0f);
				m_game.OnLanded();
				super.SetPositionY(m_game.GetGroundPosY() - super.GetHalfSizeY(), false);
				return true;
			}
			float groundMaxDeltaY = m_game.GetGroundMaxDeltaY();
			float fDeltaY = (num3 / this.m_fAbsoluteMaxJumpHeight) * groundMaxDeltaY;
			m_game.SetGroundDeltaY(fDeltaY);
			super.SetPositionY((m_game.GetGroundPosY() - super.GetHalfSizeY()) - num3, false);
		}
		else if (this.IsJumping())
		{
			this.m_fCurrentJumpDistDelta += m_game.GetGameSpeed() * super.m_fDeltaTime;
			if (this.m_fCurrentJumpDistDelta >= this.m_fCurrentJumpDist)
			{
				this.BeginState(EState.State_Move);
				m_game.OnLanded();
				m_game.SetGroundDeltaY(0f);
				super.SetPositionY(m_game.GetGroundPosY() - super.GetHalfSizeY(), false);
				return true;
			}
			float num6 = 180f - ((this.m_fCurrentJumpDistDelta / this.m_fCurrentJumpDist) * 180f);
			float num7 = ((float) Math.sin((double)(num6 *  MathUtils.DEG_TO_RAD))) * this.m_fCurrentJumpHeight;
			float num8 = m_game.GetGroundMaxDeltaY();
			float num9 = (num7 / this.m_fAbsoluteMaxJumpHeight) * num8;
			m_game.SetGroundDeltaY(num9);
			super.SetPositionY((m_game.GetGroundPosY() - super.GetHalfSizeY()) - num7, false);
			super.SetRotation((this.m_fCurrentJumpDistDelta / this.m_fCurrentJumpDist) * 360f);
		}
		else if (this.IsInForceField() && this.m_bInExitableForceField)
		{
			if (m_game.IsJumpSmall() || m_game.IsJumpBig())
			{
				this.ExitForceField();
				m_game.ClearJumpSmall();
				m_game.ClearJumpBig();
			}
		}
		else if (this.IsBouncing())
		{
			float num10;
			this.m_fCurrentJumpDistDelta += m_game.GetGameSpeed() * super.m_fDeltaTime;
			if (this.m_bInBounceSlowMotion)
			{
				float num11 = 180f - ((this.m_fCurrentJumpDistDelta / this.m_fCurrentJumpDist) * 180f);
				float num12 = ((float) Math.sin((double)(num11 * MathUtils.DEG_TO_RAD))) * this.m_fCurrentJumpHeight;
				num10 = this.m_fCurrentBounceStartY + num12;
				if (num11 < 90f)
				{
					this.EndBounceSlowMotion();
					this.m_fCurrentBounceFallingSpeedScale = 1f;
				}
			}
			else
			{
				float num13 = m_game.GetGroundPosY() - (super.GetPositionY() + super.GetHalfSizeY());
				num10 = num13 - ((m_game.GetGameSpeed() * super.m_fDeltaTime) * this.m_fCurrentBounceFallingSpeedScale);
				if (num10 <= 0f)
				{
					this.BeginState(EState.State_Move);
					m_game.OnLanded();
					m_game.SetGroundDeltaY(0f);
					super.SetPositionY(m_game.GetGroundPosY() - super.GetHalfSizeY(), false);
					return true;
				}
				this.m_fCurrentBounceFallingSpeedScale += (m_game.GetGameSpeed() * super.m_fDeltaTime) * 0.01f;
			}
			float num14 = m_game.GetGroundMaxDeltaY();
			float num15 = (num10 / this.m_fAbsoluteMaxJumpHeight) * num14;
			m_game.SetGroundDeltaY(num15);
			super.SetPositionY((m_game.GetGroundPosY() - super.GetHalfSizeY()) - num10, false);
		}
		if ((this.IsMovingOut() && !this.IsCollided()) && !this.IsLevelFailed())
		{
			super.SetPositionX(super.GetPositionX() + (m_game.GetGameSpeed() * super.m_fDeltaTime), false);
		}
		if ((this.IsIdle() || this.IsMoving()) || ((this.IsMovingOut() && !this.IsJumping()) && (!this.IsCollided() && !this.IsLevelFailed())))
		{
			float y = super.GetSpriteDeltaPositionY() + (((this.m_fBodyMoveDirY * this.m_fBodyMoveMaxDeltaY) * super.m_fDeltaTime) * 20f);
			if (y >= this.m_fBodyMoveMaxDeltaY)
			{
				y = this.m_fBodyMoveMaxDeltaY;
				this.m_fBodyMoveDirY = -1f;
			}
			else if (y <= -this.m_fBodyMoveMaxDeltaY)
			{
				y = -this.m_fBodyMoveMaxDeltaY;
				this.m_fBodyMoveDirY = 1f;
			}
			super.SetSpriteDeltaPositionY(y, false);
		}
		if (this.m_pActiveLegsSpriteAnim != null)
		{
			this.m_pActiveLegsSpriteAnim.Tick(super.m_iDeltaTime);
			this.m_pActiveLegsSpriteAnim.SetPositionX(super.GetPositionX());
			this.m_pActiveLegsSpriteAnim.SetPositionY(super.GetPositionY());
			this.m_pActiveLegsSpriteAnim.SetRotationAngle(super.GetRotation());
		}
		return true;
	}

	public enum EEmotionState
	{
		Emotion_Standard,
		Emotion_Happy,
		Emotion_Beated,
		Emotion_Tired,
		Emotion_HiSpeed,
		Emotion_Collided,
		Emotion_Bounce;

		public int getValue()
		{
			return this.ordinal();
		}

		public static EEmotionState forValue(int value)
		{
			return values()[value];
		}
	}

	public enum EState
	{
		State_None,
		State_Idle,
		State_Move,
		State_JumpSmall,
		State_JumpBig,
		State_Falling,
		State_FallingOut,
		State_InForceField,
		State_Bounce,
		State_Collided,
		State_LevelFailed;

		public int getValue()
		{
			return this.ordinal();
		}

		public static EState forValue(int value)
		{
			return values()[value];
		}
	}
}