package org.test;

import loon.action.sprite.SpriteBatch;

public class ForceField extends Entity {
	protected boolean m_bExitable;
	protected float m_fAlphaChangeDir;
	protected int m_iBlockQuartersFromGround;
	protected int m_iSizeBlocks;
	protected int m_iStartPosAtBlock;
	protected int m_iStartPosDeltaBlockQuarters;

	public ForceField() {
		super();
	}

	public final void Create(int iAtBlock, int iDeltaQuarters, int iSizeBlocks,
			int iQuartersFromGround) {
		this.Reset();
		this.m_iStartPosAtBlock = iAtBlock;
		this.m_iStartPosDeltaBlockQuarters = iDeltaQuarters;
		this.m_iSizeBlocks = iSizeBlocks;
		this.m_iBlockQuartersFromGround = iQuartersFromGround;
		super.SetSize(this.m_iSizeBlocks * m_game.GetUnitBlockSize(),
				m_game.GetUnitBlockSize(), false);
		super.SetCollSize(super.GetSizeX(), super.GetSizeY() * 0.5f);
		this.m_fAlphaChangeDir = -1f;
		super.m_pSprite.SetColorAlpha(1f);
		super.SetSpriteAnimCurrentFrame(0);
	}

	public final void CreateExitable(int iAtBlock, int iDeltaQuarters,
			int iSizeBlocks, int iQuartersFromGround) {
		this.Create(iAtBlock, iDeltaQuarters, iSizeBlocks, iQuartersFromGround);
		this.m_bExitable = true;
		super.SetSpriteAnimCurrentFrame(1);
	}

	private int GetEndPosAtBlock() {
		int num = (int) Math
				.ceil((double) ((this.m_iStartPosDeltaBlockQuarters + (this.m_iSizeBlocks * 4)) * 0.25f));
		return (this.m_iStartPosAtBlock + (num - 1));
	}

	public final float GetPositionFromGround() {
		return (super.GetHalfSizeY() + (this.m_iBlockQuartersFromGround * m_game
				.GetUnitBlockQuarterSize()));
	}

	@Override
	public boolean Init() {
		this.CreateSpriteAnim(1, 0, true);
		this.CreateSpriteFrame("forcefield");
		this.CreateSpriteFrame("forcefield_exitable");
		return true;
	}

	public final boolean IsExitable() {
		return this.m_bExitable;
	}

	@Override
	public void Render(SpriteBatch batch) {
		if (super.m_bVisible) {
			float num = super.GetPositionX() - super.GetHalfSizeX();
			super.SetSize(m_game.GetUnitBlockSize(), m_game.GetUnitBlockSize(),
					false);
			for (int i = 0; i < this.m_iSizeBlocks; i++) {
				super.SetPositionX(
						(num + super.GetHalfSizeX()) + (i * super.GetSizeX()),
						false);
				super.m_pSprite.Render(batch);
			}
			super.SetSize(this.m_iSizeBlocks * m_game.GetUnitBlockSize(),
					m_game.GetUnitBlockSize(), false);
			super.SetPositionX(num + super.GetHalfSizeX(), false);
		}
	}

	@Override
	public void Reset() {
		super.Reset();
		this.m_bExitable = false;
	}

	@Override
	public boolean Tick(int deltaMS) {
		if (!super.Tick(deltaMS)) {
			return false;
		}
		Ground ground = m_game.GetGround();
		if (ground.IsBlockVisible(this.m_iStartPosAtBlock)
				|| ground.IsBlockVisible(this.GetEndPosAtBlock())) {
			super.m_bVisible = true;
			float num = (ground.GetBlockPosOnScreen(this.m_iStartPosAtBlock) - m_game
					.GetUnitBlockHalfSize())
					+ (this.m_iStartPosDeltaBlockQuarters * m_game
							.GetUnitBlockQuarterSize());
			float num2 = this.m_iBlockQuartersFromGround
					* m_game.GetUnitBlockQuarterSize();
			super.SetPositionX(num + super.GetHalfSizeX(), false);
			super.SetPositionY(
					(m_game.GetGroundPosY() - num2) - super.GetHalfSizeY(),
					false);
			float fAlpha = super.m_pSprite.GetColorAlpha()
					+ ((this.m_fAlphaChangeDir * super.m_fDeltaTime) * 4f);
			if (fAlpha >= 1f) {
				fAlpha = 1f;
				this.m_fAlphaChangeDir = -1f;
			} else if (fAlpha <= 0.3f) {
				fAlpha = 0.3f;
				this.m_fAlphaChangeDir = 1f;
			}
			super.m_pSprite.SetColorAlpha(fAlpha);
		} else {
			super.m_bVisible = false;
		}
		return true;
	}
}