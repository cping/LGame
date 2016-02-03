package org.test.crazyjumpergles;

import loon.action.sprite.SpriteBatch;

import org.test.crazyjumpergles.common.SpriteAnim;
import org.test.crazyjumpergles.common.Tools;

public class Ground {

	public static final int HoleIndex_Invalid = -1;
	private boolean m_bEndOfGround;
	private boolean m_bGroundHolesFirstTick;
	private boolean m_bLoop;
	private MainGame m_game;
	private float m_fFirstVisibleBlockDeltaPos;
	private SpriteAnim m_GroundBlockSprite;
	private java.util.ArrayList<Integer> m_GroundHoles = new java.util.ArrayList<Integer>();
	private int m_iBlocksDone;
	private int m_iFirstVisibleHoleIndex;
	private int m_iGroundSizeBlocks;
	private int m_iLastVisibleHoleIndex;
	private int m_iPreviousFirstVisibleHoleIndex;
	private int m_iTotalBlocksVisible;

	public Ground() {
		m_game = MainGame.get();
	}

	public final void AddHoleAt(int iAtBlock) {
		this.m_GroundHoles.add(iAtBlock);
	}

	public final int GetBlockAtPos(float x) {
		float num = m_game.GetUnitBlockHalfSize()
				- this.m_fFirstVisibleBlockDeltaPos;
		int num2 = (int) Math.ceil((double) ((x + num) / m_game
				.GetUnitBlockSize()));
		return (this.GetFirstVisibleBlock() + (num2 - 1));
	}

	public final float GetBlockPosOnScreen(int iBlock) {
		int num = iBlock - this.GetFirstVisibleBlock();
		return (this.m_fFirstVisibleBlockDeltaPos + (num * m_game
				.GetUnitBlockSize()));
	}

	public final int GetFirstVisibleBlock() {
		return this.m_iBlocksDone;
	}

	public final float GetFirstVisibleBlockDeltaPos() {
		return this.m_fFirstVisibleBlockDeltaPos;
	}

	public final int GetTotalVisibleBlocks() {
		return this.m_iTotalBlocksVisible;
	}

	public final boolean Init() {
		this.m_iTotalBlocksVisible = ((int) Math.ceil((MainGame.get()
				.GetScreenWidth() / m_game.GetUnitBlockSize()))) + 1;
		this.m_GroundBlockSprite = new SpriteAnim();
		this.m_GroundBlockSprite.AddTextureByName("block01", false);
		this.m_GroundBlockSprite.AddTextureByName("block02", false);
		this.m_GroundBlockSprite.AddTextureByName("block03", false);
		this.m_GroundBlockSprite.SetAnimDuration(1);
		this.m_GroundBlockSprite.SetAnimLoopType(0);
		this.m_GroundBlockSprite.EnableBlending(false);
		this.m_GroundBlockSprite.SetSize(MainGame.get().GetUnitBlockSize(),
				MainGame.get().GetUnitBlockSize());
		this.m_GroundBlockSprite.SetPositionY(MainGame.get().GetGroundPosY()
				+ m_game.GetUnitBlockHalfSize());
		this.Reset();
		return true;
	}

	public final boolean IsBlockVisible(int iBlock) {
		return ((iBlock >= this.GetFirstVisibleBlock()) && (iBlock <= (this
				.GetFirstVisibleBlock() + this.GetTotalVisibleBlocks())));
	}

	public final boolean IsCollidingWithGroundAtRightSide(float px, float py,
			float radius) {
		int blockAtPos = this.GetBlockAtPos(px);
		while (true) {
			if (!this.IsHoleBelow(blockAtPos)) {
				float rcLeft = this.GetBlockPosOnScreen(blockAtPos)
						- m_game.GetUnitBlockHalfSize();
				float rcRight = rcLeft + m_game.GetUnitBlockSize();
				float groundPosY = m_game.GetGroundPosY();
				float rcBottom = groundPosY + m_game.GetUnitBlockHalfSize();
				return Tools.isCircleIntersectingRect(px, py, radius, rcLeft,
						rcRight, groundPosY, rcBottom);
			}
			blockAtPos++;
		}
	}

	public final boolean IsHoleBelow(int iAtBlock) {
		if (this.m_iFirstVisibleHoleIndex != -1) {
			if ((iAtBlock == this.m_GroundHoles
					.get(this.m_iFirstVisibleHoleIndex))
					|| (iAtBlock == this.m_GroundHoles
							.get(this.m_iLastVisibleHoleIndex))) {
				return true;
			}
			if (this.m_iFirstVisibleHoleIndex != this.m_iLastVisibleHoleIndex) {
				for (int i = this.m_iFirstVisibleHoleIndex; i < this.m_iLastVisibleHoleIndex; i++) {
					if (iAtBlock == this.m_GroundHoles.get(i)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public final boolean IsHoleBelow(float px) {
		return this.IsHoleBelow(this.GetBlockAtPos(px));
	}

	public final void Render(SpriteBatch batch) {
		float num = 0f;
		int firstVisibleBlock = this.GetFirstVisibleBlock();
		this.m_GroundBlockSprite.SetPositionY(MainGame.get().GetGroundPosY()
				+ m_game.GetUnitBlockHalfSize());
		while (m_bLoop) {
			if (!this.IsHoleBelow(firstVisibleBlock)) {
				this.m_GroundBlockSprite
						.SetPositionX(this.m_fFirstVisibleBlockDeltaPos + num);
				this.m_GroundBlockSprite.Render(batch);
			}
			num += m_game.GetUnitBlockSize();
			if (num >= (MainGame.get().GetScreenWidth() + m_game
					.GetUnitBlockHalfSize())) {
				return;
			}
			firstVisibleBlock++;
		}
	}

	public final void Reset() {
		this.m_bLoop = true;
		this.m_bEndOfGround = false;
		this.m_iGroundSizeBlocks = 0;
		this.m_iBlocksDone = 0;
		this.m_fFirstVisibleBlockDeltaPos = m_game.GetUnitBlockHalfSize();
		this.m_GroundHoles.clear();
		this.m_bGroundHolesFirstTick = true;
		this.m_iFirstVisibleHoleIndex = -1;
		this.m_iLastVisibleHoleIndex = -1;
		this.m_iPreviousFirstVisibleHoleIndex = -1;
		this.m_GroundBlockSprite.SetCurrentFrame(MainGame.get()
				.GetCurrentWorld(), false);
	}

	public final void SetLoop(boolean bLoop) {
		this.m_bLoop = bLoop;
	}

	public final void SetSize(int iTotalBlocks) {
		this.m_iGroundSizeBlocks = iTotalBlocks;
	}

	private void SetupVisibleHolesIndex() {
		int iPreviousFirstVisibleHoleIndex = this.m_iPreviousFirstVisibleHoleIndex;
		if (iPreviousFirstVisibleHoleIndex == -1) {
			iPreviousFirstVisibleHoleIndex = 0;
		}
		for (int i = iPreviousFirstVisibleHoleIndex; i < this.m_GroundHoles
				.size(); i++) {
			if (this.IsBlockVisible(this.m_GroundHoles.get(i))) {
				this.m_iFirstVisibleHoleIndex = this.m_iPreviousFirstVisibleHoleIndex = i;
				break;
			}
			if (this.m_GroundHoles.get(i) > (this.GetFirstVisibleBlock() + this
					.GetTotalVisibleBlocks())) {
				this.m_iFirstVisibleHoleIndex = -1;
				break;
			}
		}
		this.m_iLastVisibleHoleIndex = this.m_iFirstVisibleHoleIndex;
		if (this.m_iFirstVisibleHoleIndex != -1) {
			for (int j = this.m_iFirstVisibleHoleIndex + 1; j < this.m_GroundHoles
					.size(); j++) {
				if (!this.IsBlockVisible(this.m_GroundHoles.get(j))) {
					break;
				}
				this.m_iLastVisibleHoleIndex = j;
			}
		}
	}

	public final void Tick(int deltaMS) {
		if (this.m_bGroundHolesFirstTick) {
			this.SetupVisibleHolesIndex();
			this.m_bGroundHolesFirstTick = false;
		}
		if (((MainGame.get().GetGameState() != MainGame.EGameState.GameState_PlayFadeIn) && (MainGame
				.get().GetGameState() != MainGame.EGameState.GameState_Tutorial))
				&& !this.m_bEndOfGround) {
			this.m_fFirstVisibleBlockDeltaPos -= m_game.GetGameSpeed()
					* (((float) deltaMS) / 1000f);
			if ((this.GetBlockPosOnScreen(this.m_iGroundSizeBlocks - 1) + MainGame
					.get().GetUnitBlockHalfSize()) < m_game.GetScreenWidth()) {
				this.m_bEndOfGround = true;
				MainGame.get().EndOfGround();
				this.m_fFirstVisibleBlockDeltaPos += m_game.GetScreenWidth()
						- (this.GetBlockPosOnScreen(this.m_iGroundSizeBlocks - 1) + MainGame
								.get().GetUnitBlockHalfSize());
			}
			if (this.m_fFirstVisibleBlockDeltaPos < -MainGame.get()
					.GetUnitBlockHalfSize()) {
				this.m_fFirstVisibleBlockDeltaPos = m_game
						.GetUnitBlockHalfSize();
				this.m_iBlocksDone++;
				this.SetupVisibleHolesIndex();
				MainGame.get().OnGroundBlockDone();
			}
		}
	}
}