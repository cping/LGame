using loon.geom;
using loon.opengl;
using loon.utils;
using loon.utils.reply;
using Microsoft.Xna.Framework.Graphics;

namespace loon
{
    public abstract class Graphics
    {

		private class DefaultRender : RenderTarget
		{

			internal readonly Graphics _graphics;

			public DefaultRender(Graphics gfx) : base(gfx, null)
			{
				_graphics = gfx;
			}

			public override int Id()
			{
				return _graphics.DefaultFramebuffer();
			}

			public override int Width()
			{
				return _graphics.viewPixelWidth;
			}

			public override int Height()
			{
				return _graphics.viewPixelHeight;
			}

			public override float Xscale()
			{
				return _graphics.game.setting.Scaling() ? LSystem.GetScaleWidth() : _graphics.scale.factor;
			}

			public override float Yscale()
			{
				return _graphics.game.setting.Scaling() ? LSystem.GetScaleHeight() : _graphics.scale.factor;
			}

			public override bool Flip()
			{
				return true;
			}

			public override LTexture Texture()
			{
				return null;
			}

		}

		protected internal readonly LGame game;
        protected internal readonly Dimension viewSizeM = new Dimension();

        protected internal Scale scale = null;
        protected internal int viewPixelWidth, viewPixelHeight;

        private Display display = null;
        private Affine2f affine = null, lastAffine = null;
        private Matrix4 viewMatrix = null;
        private Array<Matrix4> matrixsStack = new Array<Matrix4>();

        private LTexture colorTex;

        protected internal GL20 gl;

        protected internal readonly RenderTarget defaultRenderTarget;
        private class DisposePort : UnitPort
        {

            internal readonly LRelease _release;

            internal DisposePort(LRelease r)
            {
                this._release = r;
            }


            public override void OnEmit()
            {
                _release.Close();
            }

        }

		protected internal Graphics(LGame game, GL20 gl, Scale scale)
		{
			this.game = game;
			this.gl = gl;
			this.scale = scale;
			this.defaultRenderTarget = new DefaultRender(this);
		}

		public virtual Matrix4 GetViewMatrix()
        {
			return ViewMatrix;
        }

		public virtual Matrix4 ViewMatrix
		{
			get
			{
				display = game.Display();
				Dimension view = LSystem.viewSize;
				if (viewMatrix == null)
				{
					viewMatrix = new Matrix4();
					viewMatrix.SetToOrtho2D(0, 0, view.Width(), view.Height());
				}
				else if (display != null && display.GL != null && !(affine = display.GL.Tx).Equals(lastAffine))
				{
					viewMatrix = affine.ToViewMatrix4();
					lastAffine = affine;
				}
				return viewMatrix;
			}
		}

		public virtual Scale Scale()
		{
			return scale;
		}


		public virtual void Save()
		{
			if (viewMatrix != null)
			{
				matrixsStack.Add(viewMatrix = viewMatrix.Cpy());
			}
		}

		public virtual void Restore()
		{
			viewMatrix = matrixsStack.Pop();
		}


		protected internal virtual int DefaultFramebuffer()
		{
			return 0;
		}
		protected internal virtual void ViewportChanged(Scale scale, int viewWidth, int viewHeight)
		{
			Display d = game.Display();
			LSystem.viewSize.SetSize((int)(viewWidth / LSystem.GetScaleWidth()), (int)(viewHeight / LSystem.GetScaleHeight()));
			if (viewMatrix != null)
			{
				LSystem.viewSize.GetMatrix().Mul(viewMatrix);
			}
			this.scale = scale;
			this.viewPixelWidth = viewWidth;
			this.viewPixelHeight = viewHeight;
			this.viewSizeM.width = game.setting.Scaling() ? LSystem.InvXScaled(viewPixelWidth) : scale.InvScaled(viewPixelWidth);
			this.viewSizeM.height = game.setting.Scaling() ? LSystem.InvXScaled(viewPixelHeight) : scale.InvScaled(viewPixelHeight);
			if (d != null)
			{
				d.Resize(LSystem.viewSize.GetWidth(), LSystem.viewSize.GetHeight());
			}
		}
		public abstract Dimension ScreenSize();

		protected internal virtual bool IsAllowResize(int viewWidth, int viewHeight)
		{
			if (game.setting.isCheckReisze)
			{
				Dimension size = this.ScreenSize();
				if (size == null || size.width <= 0 || size.height <= 0)
				{
					return true;
				}
				if (game.setting.Landscape() && viewWidth > viewHeight)
				{
					return true;
				}
				else if (viewWidth < viewHeight)
				{
					return true;
				}
				return false;
			}
			else
			{
				return true;
			}
		}

		public LTexture CreateTexture(string path, LTexture.Format config)
		{
			System.IO.Stream stream = game.Assets().OpenStream(path);
			Texture2D texture = Texture2D.FromStream(gl._device,stream);
			int width = texture.Width;
			int height = texture.Height;
			int texWidth = scale.ScaledCeil(width);
			int texHeight = scale.ScaledCeil(height);
			if (texWidth <= 0 || texHeight <= 0)
			{
				throw new LSysException("Invalid texture size: " + texWidth + "x" + texHeight);
			}
			int id = gl.CreateTexture(texture);
			GLUtils.BindTexture(gl, id);
			gl.GLTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, config.magFilter);
			int minFilter = Mipmapify(config.minFilter, config.mipmaps);
			gl.GLTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, minFilter);
			gl.GLTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, config.repeatX ? GL.GL_REPEAT : GL.GL_CLAMP_TO_EDGE);
			gl.GLTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, config.repeatY ? GL.GL_REPEAT : GL.GL_CLAMP_TO_EDGE);
			return new LTexture(this, id, config, texWidth, texHeight, scale, width, height);
		}

		public LTexture CreateTexture(float width, float height, LTexture.Format config)
		{
			int texWidth = scale.ScaledCeil(width);
			int texHeight = scale.ScaledCeil(height);
			if (texWidth <= 0 || texHeight <= 0)
			{
				throw new LSysException("Invalid texture size: " + texWidth + "x" + texHeight);
			}
			int id = gl.CreateTexture(width,height, config.mipmaps);
			GLUtils.BindTexture(gl, id);
			gl.GLTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, config.magFilter);
			int minFilter = Mipmapify(config.minFilter, config.mipmaps);
			gl.GLTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, minFilter);
			gl.GLTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, config.repeatX ? GL.GL_REPEAT : GL.GL_CLAMP_TO_EDGE);
			gl.GLTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, config.repeatY ? GL.GL_REPEAT : GL.GL_CLAMP_TO_EDGE);
			return new LTexture(this, id, config, texWidth, texHeight, scale, width, height);
		}

		public virtual int CreateTexture(LTexture.Format config)
		{
			return CreateTexture(config, 0);
		}

		public virtual int CreateTexture(LTexture.Format config, int count)
		{
			int id = gl.GLGenTexture() + count;
			GLUtils.BindTexture(gl, id);
			GLUtils.BindTexture(gl, id);
			gl.GLTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, config.magFilter);
			int minFilter = Mipmapify(config.minFilter, config.mipmaps);
			gl.GLTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, minFilter);
			gl.GLTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, config.repeatX ? GL.GL_REPEAT : GL.GL_CLAMP_TO_EDGE);
			gl.GLTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, config.repeatY ? GL.GL_REPEAT : GL.GL_CLAMP_TO_EDGE);
			return id;
		}

		protected internal static int Mipmapify(int filter, bool mipmaps)
		{
			if (!mipmaps)
			{
				return filter;
			}
			switch (filter)
			{
				case GL.GL_NEAREST:
					return GL.GL_NEAREST_MIPMAP_NEAREST;
				case GL.GL_LINEAR:
					return GL.GL_LINEAR_MIPMAP_NEAREST;
				default:
					return filter;
			}
		}

		public void QueueForDispose(LRelease resource)
        {
            game.frame.Connect(new DisposePort(resource)).Once();
        }

		public virtual LTexture FinalColorTex()
		{
			return null;
		}
	}
}
