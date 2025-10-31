package org.test.crazyjumpergles.common;

import loon.LTexture;
import loon.LTextures;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.BlendState;
import loon.utils.reply.ObjRef;

public class Sprite {

	protected boolean m_bColorBlend;

	protected boolean m_bEnableAlphaTest;

	protected boolean m_bEnableBlending = true;

	protected LColor m_Color = new LColor(0xff, 0xff, 0xff, 0xff);

	protected float m_fAlphaTestValue = 0.1f;

	protected float m_fRotationAngle;

	protected int m_iFlip;

	protected Vector2f m_Position = new Vector2f(0f, 0f);

	protected LTexture m_pTexture;

	protected RectBox m_rcTexSrcRegion = Tools.MakeInvalidRect();

	protected Vector2f m_Size = new Vector2f(100f, 100f);

	protected String textureName = "";

	public Sprite() {

	}

	public void AddTexture(LTexture pTexRes) {
		this.m_pTexture = pTexRes;
	}

	public void AddTextureByName(String texname, boolean bAutoLoad) {
		this.textureName = texname;
		if (bAutoLoad) {
			this.AddTexture(LTextures.loadTexture("assets/" + this.textureName + ".png"));
		}
	}

	public final void AddTextureByNamePostfix2Platform(String name, String ext, boolean bAutoLoad) {
		String texname = name + "." + ext;
		this.AddTextureByName(texname, bAutoLoad);
	}

	public final void AddTextureByNamePostfix3Platform(String name, String ext, boolean bAutoLoad) {
		String texname = name + "." + ext;
		this.AddTextureByName(texname, bAutoLoad);
	}

	public final void EnableAlphaTest(boolean bEnable) {
		this.m_bEnableAlphaTest = bEnable;
	}

	public final void EnableBlending(boolean bEnable) {
		this.m_bEnableBlending = bEnable;
	}

	public final void EnableColorBlend(boolean bEnable) {
		this.m_bColorBlend = bEnable;
	}

	public final LColor GetColor() {
		return this.m_Color;
	}

	public final float GetColorAlpha() {
		return this.m_Color.a;
	}

	public final int GetFlip() {
		return this.m_iFlip;
	}

	public final float GetHalfSizeX() {
		return (this.m_Size.x * 0.5f);
	}

	public final float GetHalfSizeY() {
		return (this.m_Size.y * 0.5f);
	}

	public final void GetPosition(ObjRef<Vector2f> rPos) {
		rPos.set(this.m_Position);
	}

	public final float GetPositionX() {
		return this.m_Position.x;
	}

	public final float GetPositionY() {
		return this.m_Position.y;
	}

	public final float GetRotationAngle() {
		return this.m_fRotationAngle;
	}

	public final void GetSize(ObjRef<Float> rX, ObjRef<Float> rY) {
		rX.set(this.m_Size.x);
		rY.set(this.m_Size.y);
	}

	public final float GetSizeX() {
		return this.m_Size.x;
	}

	public final float GetSizeY() {
		return this.m_Size.y;
	}

	public final LTexture GetTexture() {
		return this.m_pTexture;
	}

	public boolean IsLoaded() {
		return (this.m_pTexture != null);
	}

	public void Release() {
		this.Unload();
	}

	public void Reload() {
		if (this.textureName.length() > 0) {
			this.m_pTexture = LTextures.loadTexture("assets/" + this.textureName + ".png");
		}
	}

	public void Render(SpriteBatch batch) {
		if (this.m_bEnableBlending) {
			if (this.m_bColorBlend) {
				batch.setBlendState(BlendState.Additive);
			} else {
				batch.setBlendState(BlendState.NonPremultiplied);
			}
		} else {
			batch.setBlendState(BlendState.NonPremultiplied);
		}
		if (!this.IsLoaded()) {
			this.Reload();
		}
		if (Tools.IsInvalidRect(this.m_rcTexSrcRegion)) {
			RenderQuad(batch, this.m_Position.x, this.m_Position.y, this.m_Size.x, this.m_Size.y, this.m_fRotationAngle,
					this.m_pTexture, null, this.m_iFlip, this.m_Color);
		} else {
			RenderQuad(batch, this.m_Position.x, this.m_Position.y, this.m_Size.x, this.m_Size.y, this.m_fRotationAngle,
					this.m_pTexture, this.m_rcTexSrcRegion, this.m_iFlip, this.m_Color);
		}
	}

	public void RenderQuad(SpriteBatch batch, RectBox rcScrDstRect, LTexture pTexture, RectBox pTexSrcRect, int iFlip,
			LColor color) {
		float width = rcScrDstRect.width;
		float height = rcScrDstRect.height;
		float x = rcScrDstRect.x + (width * 0.5f);
		float y = rcScrDstRect.y + (height * 0.5f);
		this.RenderQuad(batch, x, y, width, height, 0f, pTexture, pTexSrcRect, iFlip, color);
	}

	public void RenderQuad(SpriteBatch batch, float startX, float startY, float width, float height, LTexture pTexture,
			RectBox pTexSrcRect, int iFlip, LColor color) {
		this.RenderQuad(batch, startX + (width * 0.5f), startY + (height * 0.5f), width, height, 0f, pTexture,
				pTexSrcRect, iFlip, color);
	}

	private RectBox rectangle = new RectBox();

	private Vector2f origin = new Vector2f();

	public void RenderQuad(SpriteBatch batch, float x, float y, float width, float height, float rotation,
			LTexture pTexture, RectBox pTexSrcRect, int iFlip, LColor color) {
		SpriteEffects none = SpriteEffects.None;
		if (iFlip == 1) {
			none = SpriteEffects.FlipVertically;
		} else if (iFlip == 2) {
			none = SpriteEffects.FlipHorizontally;
		}
		if (pTexture != null) {
			if (rotation == 0f) {
				rectangle.setBounds((x - (width * 0.5f)), (y - (height * 0.5f)), width, height);
				batch.draw(pTexture, rectangle, pTexSrcRect, color, 0, Vector2f.STATIC_ZERO, none);
			} else {
				origin.set((width / 2), (height / 2));
				rectangle.setBounds(x, y, width, height);
				batch.draw(pTexture, rectangle, pTexSrcRect, color, rotation, origin, none);

			}
		} else if (rotation == 0f) {
			batch.fillRect((x - (width * 0.5f)), (y - (height * 0.5f)), width, height);
		} else {
			batch.fillRect(x, y, width, height);
		}
	}

	public void Reset() {
	}

	public final void SetAlphaTestValue(float fValue) {
		this.m_fAlphaTestValue = fValue;
	}

	public final void SetColor(LColor clr) {
		this.m_Color = clr;
	}

	public final void SetColorAlpha(float fAlpha) {
		this.m_Color.a = fAlpha;
	}

	public final void SetFlip(int iFlip) {
		this.m_iFlip = iFlip;
	}

	public final void SetPosition(float x, float y) {
		this.m_Position.x = x;
		this.m_Position.y = y;
	}

	public final void SetPositionX(float x) {
		this.m_Position.x = x;
	}

	public final void SetPositionY(float y) {
		this.m_Position.y = y;
	}

	public final void SetRotationAngle(float fAngle) {
		this.m_fRotationAngle = fAngle;
	}

	public final void SetSize(float x, float y) {
		this.m_Size.x = x;
		this.m_Size.y = y;
	}

	public final void SetTexSrcRegion(int left, int right, int top, int bottom) {
		this.m_rcTexSrcRegion.x = left;
		this.m_rcTexSrcRegion.width = right - left;
		this.m_rcTexSrcRegion.y = top;
		this.m_rcTexSrcRegion.height = bottom - top;
	}

	public final void SetTexSrcRegionF(float left, float right, float top, float bottom) {
		if (this.m_pTexture != null) {
			float width = this.m_pTexture.getWidth();
			float height = this.m_pTexture.getHeight();
			this.SetTexSrcRegion((int) (width * left), (int) (width * right), (int) (height * top),
					(int) (height * bottom));
		}
	}

	public void Tick(int deltaMS) {
	}

	public void Unload() {
		if (this.m_pTexture != null) {
			this.m_pTexture = null;
		}
	}

	public enum EFlip {
		Flip_No, Flip_Vertical, Flip_Horizontal;

		public int getValue() {
			return this.ordinal();
		}

		public static EFlip forValue(int value) {
			return values()[value];
		}
	}
}