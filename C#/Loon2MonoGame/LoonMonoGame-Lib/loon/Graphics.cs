using loon.geom;
using loon.opengl;
using loon.utils;
using loon.utils.reply;

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


		protected internal virtual int DefaultFramebuffer()
		{
			return 0;
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
