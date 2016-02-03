package org.test.crazyjumpergles;

public class Star extends Entity {
	
	protected boolean m_bPickedUp;
	
	protected float m_fPosDeltaY;
	protected int m_iBlocksFromGround;
	protected int m_iPosAtBlock;


	public Star() {
		
	}

	@Override
	public boolean CanCollide() {
		if (!super.CanCollide()) {
			return false;
		}
		if (this.m_bPickedUp) {
			return false;
		}
		return true;
	}

	public final void Create(int iAtBlock, int iBlocksFromGround) {
		this.Reset();
		this.m_iPosAtBlock = iAtBlock;
		this.m_iBlocksFromGround = iBlocksFromGround;
	}

	@Override
	public boolean Init() {
		this.CreateSprite("star", true);
		super.SetSize(m_game.GetUnitBlockSize(), m_game.GetUnitBlockSize(),
				false);
		super.SetCollSize(super.GetSizeX() * 0.9f, super.GetSizeY() * 0.9f);
		return true;
	}

	public final void PickUp() {
		this.m_bPickedUp = true;
		m_game.OnPickupStar(this);
	}

	@Override
	public void Reset() {
		super.Reset();
		this.m_bPickedUp = false;
		super.m_fRotationSpeed = -200f;
		this.m_fPosDeltaY = 0f;
		super.m_pSprite.SetColorAlpha(1f);
		super.SetSize(m_game.GetUnitBlockSize(), m_game.GetUnitBlockSize(),
				false);
	}

	@Override
	public boolean Tick(int deltaMS) {
		if (!super.Tick(deltaMS)) {
			return false;
		}
		Ground ground = m_game.GetGround();
		if (ground.IsBlockVisible(this.m_iPosAtBlock)) {
			super.m_bVisible = true;
			if (this.m_bPickedUp) {
				float fAlpha = super.m_pSprite.GetColorAlpha()
						- (super.m_fDeltaTime * 3f);
				if (fAlpha <= 0f) {
					return false;
				}
				super.m_pSprite.SetColorAlpha(fAlpha);
				float num2 = (super.m_fDeltaTime * 3f)
						* m_game.GetUnitBlockSize();
				super.SetSize(super.GetSizeX() + num2, super.GetSizeY() + num2,
						false);
				this.m_fPosDeltaY += ((super.m_fDeltaTime * 3f) * m_game
						.GetUnitBlockSize()) * 2f;
			}
			float num3 = m_game.GetUnitBlockSize() * this.m_iBlocksFromGround;
			super.SetPositionX(ground.GetBlockPosOnScreen(this.m_iPosAtBlock),
					false);
			super.SetPositionY(
					(m_game.GetGroundPosY() - (m_game.GetUnitBlockHalfSize() + num3))
							- this.m_fPosDeltaY, false);
		} else {
			super.m_bVisible = false;
		}
		return true;
	}
}