namespace loon.opengl
{
	public abstract class RenderTarget : LRelease
	{

		public static RenderTarget Create(Graphics gfx, LTexture tex)
		{
			//GL20 gl = gfx.gl;
			int fb = 0;// gl.glGenFramebuffer();
			if (fb == 0)
			{
				//throw new LSysException("Failed to gen framebuffer: " + gl.glGetError());
			}
			return new RenderTargetAnonymousInnerClass(gfx, tex, fb);
		}

		private class RenderTargetAnonymousInnerClass : RenderTarget
		{
			private LTexture tex;
			private int fb;

			public RenderTargetAnonymousInnerClass(Graphics gfx, LTexture tex, int fb) : base(gfx, tex)
			{
				this.tex = tex;
				this.fb = fb;
			}

			public override int Id()
			{
				return fb;
			}

			public override int Width()
			{
				return 0;// tex.PixelWidth();
			}

			public override int Height()
			{
				return 0;// tex.PixelHeight();
			}

			public override float Xscale()
			{
				return 0;// tex.PixelWidth() / tex.Width();
			}

			public override float Yscale()
			{
				return 0;// tex.PixelHeight() / tex.Height();
			}

			public override bool Flip()
			{
				return true;
			}

			public override LTexture Texture()
			{
				return tex;
			}
		}

		public readonly Graphics gfx;

		public readonly LTexture texture;

		public RenderTarget(Graphics gfx, LTexture tex)
		{
			this.gfx = gfx;
			this.texture = tex;
		}

		public abstract LTexture Texture();

		public abstract int Id();

		public abstract int Width();

		public abstract int Height();

		public abstract float Xscale();

		public abstract float Yscale();

		public abstract bool Flip();

		private bool disposed;

		public virtual void Bind()
		{

		}

		public virtual void Unbind()
		{

		}

		public override string ToString()
		{
			return "[id=" + Id() + ", size=" + Width() + "x" + Height() + " @ " + Xscale() + "x" + Yscale() + ", flip="
					+ Flip() + "]";
		}

		public virtual bool IsClosed()
		{
				return disposed;
		}

		public virtual void Close()
		{
			if (!disposed)
			{
				disposed = true;
			}
		}

		~RenderTarget()
		{
			if (!disposed)
			{
				gfx.QueueForDispose(this);
			}
		}

	}
}
