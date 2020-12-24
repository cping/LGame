using loon.canvas;
using loon.geom;

namespace loon.opengl
{

	public abstract class BaseBatch : LTextureBind
	{

		protected internal ShaderSource _shader_source;

		private bool _shader_ditry = true;

		public virtual ShaderSource ShaderSource
		{
			set
			{
				if (value == null)
				{
					return;
				}
				if (value.Equals(_shader_source))
				{
					return;
				}
				this._shader_source = value;
				this._shader_ditry = true;
			}
			get
			{
				return this._shader_source;
			}
		}


		protected internal virtual bool ShaderDirty
		{
			set
			{
				this._shader_ditry = value;
			}
			get
			{
				return this._shader_ditry;
			}
		}


		public virtual void AddQuad(LTexture tex, int tint, Affine2f xf, float x, float y, float w, float h)
		{
			if (tex == null || tex.IsClosed())
			{
				return;
			}
			if (w < 1f || h < 1f)
			{
				return;
			}
			if (LColor.GetAlpha(tint) <= 0)
			{
				return;
			}
			/*
			Texture = tex;

			if (tex.Parent == null)
			{
				float u2 = tex.getFormat().repeatX ? w / tex.width() : tex.widthRatio();
				float uv = tex.getFormat().repeatY ? h / tex.height() : tex.heightRatio();
				AddQuad(tint, xf, x, y, x + w, y + h, tex.xOff(), tex.yOff(), u2, uv);
			}
			else
			{
				LTexture forefather = LTexture.firstFather(tex);
				float u2 = tex.getFormat().repeatX ? w / forefather.width() : tex.widthRatio();
				float uv = tex.getFormat().repeatY ? h / forefather.height() : tex.heightRatio();
				if ((w < forefather.width() || h < forefather.height()) && !tex.Scale)
				{
					AddQuad(tint, xf, x, y, x + w, y + h, tex.xOff(), tex.yOff(), u2, uv);
				}
				else
				{
					AddQuad(tint, xf, x, y, x + w, y + h, tex.xOff(), tex.yOff(), forefather.widthRatio(), forefather.heightRatio());
				}
			}*/
		}

		public virtual void AddQuad(LTexture tex, int tint, Affine2f xf, float dx, float dy, float dw, float dh, float sx, float sy, float sw, float sh)
		{
			if (tex == null || tex.IsClosed())
			{
				return;
			}
			if (dw < 1f || dh < 1f || sw < 1f || sh < 1f)
			{
				return;
			}
			if (LColor.GetAlpha(tint) <= 0)
			{
				return;
			}
			/*
			Texture = tex;
			if (tex.Parent == null)
			{
				float displayWidth = tex.width();
				float displayHeight = tex.height();
				float xOff = ((sx / displayWidth) * tex.widthRatio()) + tex.xOff();
				float yOff = ((sy / displayHeight) * tex.heightRatio()) + tex.yOff();
				float widthRatio = ((sw / displayWidth) * tex.widthRatio()) + xOff;
				float heightRatio = ((sh / displayHeight) * tex.heightRatio()) + yOff;
				AddQuad(tint, xf, dx, dy, dx + dw, dy + dh, xOff, yOff, widthRatio, heightRatio);
			}
			else
			{
				LTexture forefather = LTexture.firstFather(tex);
				float displayWidth = forefather.width();
				float displayHeight = forefather.height();
				float xOff = ((sx / displayWidth) * forefather.widthRatio()) + forefather.xOff() + tex.xOff();
				float yOff = ((sy / displayHeight) * forefather.heightRatio()) + forefather.yOff() + tex.yOff();
				float widthRatio = ((sw / displayWidth) * forefather.widthRatio()) + xOff;
				float heightRatio = ((sh / displayHeight) * forefather.heightRatio()) + yOff;
				AddQuad(tint, xf, dx, dy, dx + dw, dy + dh, xOff, yOff, widthRatio, heightRatio);
			}*/
		}

		public virtual void AddQuad(int tint, Affine2f xf, float left, float top, float right, float bottom, float sl, float st, float sr, float sb)
		{
			AddQuad(tint, xf.m00, xf.m01, xf.m10, xf.m11, xf.tx, xf.ty, left, top, right, bottom, sl, st, sr, sb);
		}

		public virtual void AddQuad(int tint, float m00, float m01, float m10, float m11, float tx, float ty, float left, float top, float right, float bottom, float sl, float st, float sr, float sb)
		{
			AddQuad(tint, m00, m01, m10, m11, tx, ty, left, top, sl, st, right, top, sr, st, left, bottom, sl, sb, right, bottom, sr, sb);
		}

		public abstract void AddQuad(int tint, float m00, float m01, float m10, float m11, float tx, float ty, float x1, float y1, float sx1, float sy1, float x2, float y2, float sx2, float sy2, float x3, float y3, float sx3, float sy3, float x4, float y4, float sx4, float sy4);

		protected internal BaseBatch(GL20 gl) : base(gl)
		{
		}
	}
}
