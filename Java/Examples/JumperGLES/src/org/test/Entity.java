package org.test;

import org.test.common.Sprite;
import org.test.common.SpriteAnim;
import org.test.common.Tools;

import loon.action.sprite.SpriteBatch;
import loon.core.LSystem;
import loon.core.RefObject;
import loon.core.geom.Vector2f;
import loon.utils.MathUtils;

public class Entity {

	protected MainGame m_game;
	public boolean inuse;
	protected boolean m_bAlreadyInScreen;
	protected boolean m_bHasSecondCollisionBox;
	protected boolean m_bRotateToNewDir;
	protected boolean m_bRotationFromMoveDir;
	protected boolean m_bVisible;
	protected float m_fBaseRotation;
	private float m_fCollPosDeltaX2;
	private float m_fCollPosDeltaY2;
	private float m_fCollSizeX;
	private float m_fCollSizeX2;
	private float m_fCollSizeY;
	private float m_fCollSizeY2;
	protected float m_fDeltaTime;
	protected float m_fHealthPoints;
	protected float m_fHealthPointsMax;
	protected float m_fRotateToNewDirSpeed = 180f;
	private float m_fRotation;
	protected float m_fRotationSpeed;
	private float m_fSpeedX;
	private float m_fSpeedY;
	protected int m_iDeltaTime;
	protected int m_iEntitySubType = SUBTYPE_NONE;
	protected int m_iEntityType;
	protected int m_iTimeSinceSpawned;
	private Vector2f m_Position = new Vector2f(0f, 0f);
	protected Sprite m_pSprite;
	private Vector2f m_Size = new Vector2f(0f, 0f);
	private Vector2f m_SpriteDeltaPosition = new Vector2f();
	public static short SUBTYPE_NONE = 0;

	public Entity() {
		m_game = MainGame.get();
	}

	public boolean CanCollide() {
		return this.IsVisible();
	}

	private void CreateSprite(boolean bTransparent) {
		if (this.m_pSprite != null) {
			this.m_pSprite.Release();
			this.m_pSprite = null;
		}
		this.m_pSprite = new Sprite();
		this.m_pSprite.EnableBlending(bTransparent);
		this.m_pSprite.SetPosition(this.m_Position.x, this.m_Position.y);
		this.m_pSprite.SetSize(this.m_Size.x, this.m_Size.y);
	}

	public void CreateSprite(String fname, boolean bTransparent) {
		this.CreateSprite(bTransparent);
		this.m_pSprite.AddTextureByName(fname, true);
	}

	public void CreateSpriteAnim(int iAnimDuration, int iAnimLoopType,
			boolean bTransparent) {
		if (this.m_pSprite != null) {
			this.m_pSprite.Release();
			this.m_pSprite = null;
		}
		this.m_pSprite = new SpriteAnim();
		this.m_pSprite.EnableBlending(bTransparent);
		this.m_pSprite.SetPosition(this.m_Position.x, this.m_Position.y);
		this.m_pSprite.SetSize(this.m_Size.x, this.m_Size.y);
		((SpriteAnim) this.m_pSprite).SetAnimDuration((short) iAnimDuration);
		((SpriteAnim) this.m_pSprite).SetAnimLoopType((short) iAnimLoopType);
	}

	public void CreateSpriteFrame(String fname) {
		if (this.m_pSprite == null) {
			this.CreateSpriteAnim(100, 1, true);
		}
		this.m_pSprite.AddTextureByName(fname, true);
	}

	public void CreateSpriteFramePostfix2Platform(String name, String ext) {
		if (this.m_pSprite == null) {
			this.CreateSpriteAnim(100, 1, true);
		}
		this.m_pSprite.AddTextureByNamePostfix2Platform(name, ext, true);
	}

	public void CreateSpriteFramePostfix3Platform(String name, String ext) {
		if (this.m_pSprite == null) {
			this.CreateSpriteAnim(100, 1, true);
		}
		this.m_pSprite.AddTextureByNamePostfix3Platform(name, ext, true);
	}

	public void CreateSpritePostfix2Platform(String name, String ext,
			boolean bTransparent) {
		this.CreateSprite(bTransparent);
		this.m_pSprite.AddTextureByNamePostfix2Platform(name, ext, true);
	}

	public void CreateSpritePostfix3Platform(String name, String ext,
			boolean bTransparent) {
		this.CreateSprite(bTransparent);
		this.m_pSprite.AddTextureByNamePostfix3Platform(name, ext, true);
	}

	public final float GetCollPosX() {
		return (this.m_Position.x - (this.m_fCollSizeX * 0.5f));
	}

	public final float GetCollPosX2() {
		return ((this.m_Position.x + this.m_fCollPosDeltaX2) - (this.m_fCollSizeX2 * 0.5f));
	}

	public final float GetCollPosY() {
		return (this.m_Position.y - (this.m_fCollSizeY * 0.5f));
	}

	public final float GetCollPosY2() {
		return ((this.m_Position.y + this.m_fCollPosDeltaY2) - (this.m_fCollSizeY2 * 0.5f));
	}

	public final float GetCollSizeX() {
		return this.m_fCollSizeX;
	}

	public final float GetCollSizeX2() {
		return this.m_fCollSizeX2;
	}

	public final float GetCollSizeY() {
		return this.m_fCollSizeY;
	}

	public final float GetCollSizeY2() {
		return this.m_fCollSizeY2;
	}

	public final int GetEntitySubType() {
		return this.m_iEntitySubType;
	}

	public final int GetEntityType() {
		return this.m_iEntityType;
	}

	public final float GetHalfSizeX() {
		return (this.m_Size.x * 0.5f);
	}

	public final float GetHalfSizeY() {
		return (this.m_Size.y * 0.5f);
	}

	public final boolean GetHasSecondCollisionBox() {
		return this.m_bHasSecondCollisionBox;
	}

	public final float GetHealthPoints() {
		return this.m_fHealthPoints;
	}

	public final float GetHealthPointsMax() {
		return this.m_fHealthPointsMax;
	}

	public final Vector2f GetPosition() {
		return this.m_Position;
	}

	public final void GetPosition(RefObject<Vector2f> rPos) {
		rPos.argvalue = this.m_Position;
	}

	public final float GetPositionX() {
		return this.m_Position.x;
	}

	public final float GetPositionY() {
		return this.m_Position.y;
	}

	public final float GetQuarterSizeX() {
		return (this.m_Size.x * 0.25f);
	}

	public final float GetQuarterSizeY() {
		return (this.m_Size.y * 0.25f);
	}

	public final float GetRotation() {
		return this.m_fRotation;
	}

	public final void GetSize(RefObject<Float> rX, RefObject<Float> rY) {
		rX.argvalue = this.m_Size.x;
		rY.argvalue = this.m_Size.y;
	}

	public final float GetSizeX() {
		return this.m_Size.x;
	}

	public final float GetSizeY() {
		return this.m_Size.y;
	}

	public final float GetSpeedX() {
		return this.m_fSpeedX;
	}

	public final float GetSpeedY() {
		return this.m_fSpeedY;
	}

	public final int GetSpriteAnimCurrentFrame() {
		SpriteAnim pSprite = (SpriteAnim) ((this.m_pSprite instanceof SpriteAnim) ? this.m_pSprite
				: null);
		if (pSprite != null) {
			return pSprite.GetCurrentFrame();
		}
		return 0;
	}

	public final int GetSpriteAnimCurrentFrameDuration() {
		SpriteAnim pSprite = (SpriteAnim) ((this.m_pSprite instanceof SpriteAnim) ? this.m_pSprite
				: null);
		if (pSprite != null) {
			return pSprite.GetCurrentFrameDuration();
		}
		return 0;
	}

	public final int GetSpriteAnimDuration() {
		SpriteAnim pSprite = (SpriteAnim) ((this.m_pSprite instanceof SpriteAnim) ? this.m_pSprite
				: null);
		if (pSprite != null) {
			return pSprite.GetAnimDuration();
		}
		return 0;
	}

	public final int GetSpriteAnimFrames() {
		SpriteAnim pSprite = (SpriteAnim) ((this.m_pSprite instanceof SpriteAnim) ? this.m_pSprite
				: null);
		if (pSprite != null) {
			return pSprite.GetFramesCount();
		}
		if (this.m_pSprite != null) {
			return 1;
		}
		return 0;
	}

	public final float GetSpriteDeltaPositionX() {
		return this.m_SpriteDeltaPosition.x;
	}

	public final float GetSpriteDeltaPositionY() {
		return this.m_SpriteDeltaPosition.y;
	}

	public boolean Init() {
		return true;
	}

	public boolean IsEnemy() {
		return false;
	}

	public final boolean IsInScreen() {
		if (this.m_bAlreadyInScreen) {
			return true;
		}
		if (((this.GetPositionX() > 0f) && (this.GetPositionX() < LSystem.screenRect.width))
				&& ((this.GetPositionY() > 0f) && (this.GetPositionY() < LSystem.screenRect.height))) {
			this.m_bAlreadyInScreen = true;
			return true;
		}
		return false;
	}

	public boolean IsPlayer() {
		return false;
	}

	public boolean IsPowerUp() {
		return false;
	}

	public final boolean IsVisible() {
		return this.m_bVisible;
	}

	public boolean Load() {
		return true;
	}

	public void Release() {
		if (this.m_pSprite != null) {
			this.m_pSprite.Release();
			this.m_pSprite = null;
		}
	}

	public void Render(SpriteBatch batch) {
		if (this.m_bVisible) {
			this.UpdateRotation();
			if (this.m_pSprite != null) {
				this.m_pSprite.Render(batch);
			}
		}
	}

	public void Reset() {
		this.m_bVisible = false;
		this.m_bAlreadyInScreen = false;
		this.m_iTimeSinceSpawned = 0;
		this.m_fRotation = 0f;
	}

	protected void RotateToNewDirection() {
		this.m_bRotateToNewDir = true;
	}

	public boolean Save() {
		return true;
	}

	public final void SetCollPosDelta2(float x, float y) {
		this.m_fCollPosDeltaX2 = x;
		this.m_fCollPosDeltaY2 = y;
	}

	public final void SetCollSize(float x, float y) {
		this.m_fCollSizeX = x;
		this.m_fCollSizeY = y;
	}

	public final void SetCollSize2(float x, float y) {
		this.m_fCollSizeX2 = x;
		this.m_fCollSizeY2 = y;
	}

	public final void SetCollSizeX(float x) {
		this.m_fCollSizeX = x;
	}

	public final void SetCollSizeX2(float x) {
		this.m_fCollSizeX2 = x;
	}

	public final void SetCollSizeY(float y) {
		this.m_fCollSizeY = y;
	}

	public final void SetCollSizeY2(float y) {
		this.m_fCollSizeY2 = y;
	}

	public final void SetHealthPoints(float healthPoints) {
		this.m_fHealthPoints = healthPoints;
	}

	public final void SetHealthPointsToMax() {
		this.m_fHealthPoints = this.m_fHealthPointsMax;
	}

	public final void SetPosition(Vector2f pos, boolean bScaleByDeviceUnits) {
		this.SetPosition(pos.x, pos.y, bScaleByDeviceUnits);
	}

	public final void SetPosition(float x, float y) {
		this.SetPosition(x, y, false);
	}

	public final void SetPosition(float x, float y, boolean bScaleByDeviceUnits) {
		if (bScaleByDeviceUnits) {
			this.m_Position.x = x * m_game.GetDeviceUnitScale();
			this.m_Position.y = y * m_game.GetDeviceUnitScale();
		} else {
			this.m_Position.x = x;
			this.m_Position.y = y;
		}
		if (this.m_pSprite != null) {
			this.m_pSprite.SetPosition(this.m_Position.x, this.m_Position.y);
		}
		this.UpdateFXPosition();
	}

	public final void SetPositionX(float x) {
		this.SetPositionX(x, false);
	}

	public final void SetPositionX(float x, boolean bScaleByDeviceUnits) {
		if (bScaleByDeviceUnits) {
			this.m_Position.x = x * m_game.GetDeviceUnitScale();
		} else {
			this.m_Position.x = x;
		}
		if (this.m_pSprite != null) {
			this.m_pSprite.SetPositionX(this.m_Position.x);
		}
		this.UpdateFXPosition();
	}

	public final void SetPositionY(float y) {
		this.SetPositionY(y, false);
	}

	public final void SetPositionY(float y, boolean bScaleByDeviceUnits) {
		if (bScaleByDeviceUnits) {
			this.m_Position.y = y * m_game.GetDeviceUnitScale();
		} else {
			this.m_Position.y = y;
		}
		if (this.m_pSprite != null) {
			this.m_pSprite.SetPositionY(this.m_Position.y);
		}
		this.UpdateFXPosition();
	}

	public final void SetRotation(float fAngle) {
		this.m_fRotation = fAngle;
		if (this.m_pSprite != null) {
			this.m_pSprite.SetRotationAngle(fAngle);
		}
	}

	public final void SetSize(float x, float y, boolean bScaleByDeviceUnits) {
		if (bScaleByDeviceUnits) {
			this.m_Size.x = x * m_game.GetDeviceUnitScale();
			this.m_Size.y = y * m_game.GetDeviceUnitScale();
		} else {
			this.m_Size.x = x;
			this.m_Size.y = y;
		}
		if (this.m_pSprite != null) {
			this.m_pSprite.SetSize(this.m_Size.x, this.m_Size.y);
		}
	}

	public final void SetSpeedX(float speed) {
		this.m_fSpeedX = speed;
	}

	public final void SetSpeedY(float speed) {
		this.m_fSpeedY = speed;
	}

	public final void SetSpriteAnimCurrentFrame(int iAnimCurFrame) {
		SpriteAnim pSprite = (SpriteAnim) ((this.m_pSprite instanceof SpriteAnim) ? this.m_pSprite
				: null);
		if (pSprite != null) {
			pSprite.SetCurrentFrame(iAnimCurFrame, true);
		}
	}

	public final void SetSpriteAnimCurrentFrameByTexName(String texName) {
		SpriteAnim pSprite = (SpriteAnim) ((this.m_pSprite instanceof SpriteAnim) ? this.m_pSprite
				: null);
		if (pSprite != null) {
			pSprite.SetCurrentFrameByTexName(texName, false);
		}
	}

	public final void SetSpriteAnimCurrentFrameDuration(int iAnimDuration) {
		SpriteAnim pSprite = (SpriteAnim) ((this.m_pSprite instanceof SpriteAnim) ? this.m_pSprite
				: null);
		if (pSprite != null) {
			pSprite.SetCurrentFrameDuration((short) iAnimDuration);
		}
	}

	public void SetSpriteAnimDuration(int iAnimDuration) {
		SpriteAnim pSprite = (SpriteAnim) ((this.m_pSprite instanceof SpriteAnim) ? this.m_pSprite
				: null);
		if (pSprite != null) {
			pSprite.SetAnimDuration(iAnimDuration);
		}
	}

	public final void SetSpriteDeltaPosition(float x, float y,
			boolean bScaleByDeviceUnits) {
		if (bScaleByDeviceUnits) {
			this.m_SpriteDeltaPosition.x = x * m_game.GetDeviceUnitScale();
			this.m_SpriteDeltaPosition.y = y * m_game.GetDeviceUnitScale();
		} else {
			this.m_SpriteDeltaPosition.x = x;
			this.m_SpriteDeltaPosition.y = y;
		}
		if (this.m_pSprite != null) {
			this.m_pSprite.SetPosition(this.m_Position.x
					+ this.m_SpriteDeltaPosition.x, this.m_Position.y
					+ this.m_SpriteDeltaPosition.y);
		}
	}

	public final void SetSpriteDeltaPositionX(float x,
			boolean bScaleByDeviceUnits) {
		if (bScaleByDeviceUnits) {
			this.m_SpriteDeltaPosition.x = x * m_game.GetDeviceUnitScale();
		} else {
			this.m_SpriteDeltaPosition.x = x;
		}
		if (this.m_pSprite != null) {
			this.m_pSprite.SetPositionX(this.m_Position.x
					+ this.m_SpriteDeltaPosition.x);
		}
	}

	public final void SetSpriteDeltaPositionY(float y,
			boolean bScaleByDeviceUnits) {
		if (bScaleByDeviceUnits) {
			this.m_SpriteDeltaPosition.y = y * m_game.GetDeviceUnitScale();
		} else {
			this.m_SpriteDeltaPosition.y = y;
		}
		if (this.m_pSprite != null) {
			this.m_pSprite.SetPositionY(this.m_Position.y
					+ this.m_SpriteDeltaPosition.y);
		}
	}

	public void SetSubType(int subType) {
		this.m_iEntitySubType = subType;
	}

	public final void SpriteAnimPlay() {
		SpriteAnim pSprite = (SpriteAnim) ((this.m_pSprite instanceof SpriteAnim) ? this.m_pSprite
				: null);
		if (pSprite != null) {
			pSprite.Play();
		}
	}

	public final void SpriteAnimReset() {
		SpriteAnim pSprite = (SpriteAnim) ((this.m_pSprite instanceof SpriteAnim) ? this.m_pSprite
				: null);
		if (pSprite != null) {
			pSprite.Reset();
		}
	}

	public final void SpriteAnimStop() {
		SpriteAnim pSprite = (SpriteAnim) ((this.m_pSprite instanceof SpriteAnim) ? this.m_pSprite
				: null);
		if (pSprite != null) {
			pSprite.Stop();
		}
	}

	public boolean Tick(int deltaMS) {
		this.m_iDeltaTime = deltaMS;
		this.m_fDeltaTime = ((float) deltaMS) / 1000f;
		this.m_iTimeSinceSpawned += deltaMS;
		if (this.m_pSprite != null) {
			this.m_pSprite.Tick(deltaMS);
		}
		return true;
	}

	protected void UpdateFXPosition() {
	}

	protected void UpdateRotation() {
		if (!LSystem.isPaused) {
			if (this.m_bRotateToNewDir) {
				float fRotation = this.m_fRotation;
				float num2 = (MathUtils.atan2(this.m_fSpeedY,
						( this.m_fSpeedX)) * MathUtils.RAD_TO_DEG)
						- this.m_fBaseRotation;
				float num3 = num2 - fRotation;
				float fAngle = this.m_fRotation;
				if (num3 > 0f) {
					fAngle = fRotation
							+ (this.m_fRotateToNewDirSpeed * this.m_fDeltaTime);
					if (fAngle > num2) {
						fAngle = num2;
						this.m_bRotateToNewDir = false;
					}
					this.SetRotation(fAngle);
				} else if (num3 < 0f) {
					fAngle = fRotation
							- (this.m_fRotateToNewDirSpeed * this.m_fDeltaTime);
					if (fAngle < num2) {
						fAngle = num2;
						this.m_bRotateToNewDir = false;
					}
					this.SetRotation(fAngle);
				} else {
					this.m_bRotateToNewDir = false;
				}
			} else if (this.m_bRotationFromMoveDir) {
				this.SetRotation((( MathUtils.atan2( this.m_fSpeedY,
					this.m_fSpeedX)) * MathUtils.RAD_TO_DEG)
						- this.m_fBaseRotation);
			} else if (this.m_fRotationSpeed != 0f) {
				float angle = this.m_fRotation
						+ (this.m_fRotationSpeed * this.m_fDeltaTime);
				RefObject<Float> tempRef_angle = new RefObject<Float>(angle);
				Tools.ClampAngle(tempRef_angle);
				angle = tempRef_angle.argvalue;
				this.SetRotation(angle);
			}
		}
	}
}