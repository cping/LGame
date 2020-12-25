using loon.geom;
using loon.opengl;
using loon.utils;
using Microsoft.Xna.Framework.Graphics;

namespace loon
{
   public class LTexture : Painter, LRelease
	{

		public sealed class Format
		{

			public static Format NEAREST = new Format(true, false, false, GL.GL_NEAREST, GL.GL_NEAREST, false);

			public static Format LINEAR = new Format(true, false, false, GL.GL_LINEAR, GL.GL_LINEAR, false);

			public static Format UNMANAGED = new Format(false, false, false, GL.GL_NEAREST, GL.GL_LINEAR, false);

			public static Format DEFAULT = LINEAR;

			public readonly bool managed;

			public readonly bool repeatX, repeatY;

			public readonly int minFilter, magFilter;

			public readonly bool mipmaps;

			public Format(bool managed, bool repeatX, bool repeatY, int minFilter, int magFilter, bool mipmaps)
			{
				this.managed = managed;
				this.repeatX = repeatX;
				this.repeatY = repeatY;
				this.minFilter = minFilter;
				this.magFilter = magFilter;
				this.mipmaps = mipmaps;
			}

			public Format Repeat(bool repeatX, bool repeatY)
			{
				return new Format(managed, repeatX, repeatY, minFilter, magFilter, mipmaps);
			}

			public int ToTexWidth(int sourceWidth)
			{
				return (repeatX || mipmaps) ? GLUtils.NextPOT(sourceWidth) : sourceWidth;
			}

			public int ToTexHeight(int sourceHeight)
			{
				return (repeatY || mipmaps) ? GLUtils.NextPOT(sourceHeight) : sourceHeight;
			}

			public override string ToString()
			{
				StringKeyValue builder = new StringKeyValue("Managed");
				builder.Kv("managed", managed).Comma().Kv("repeat", (repeatX ? "x" : "") + (repeatY ? "y" : "")).Comma().Kv("filter", (minFilter + "/" + magFilter)).Comma().Kv("mipmaps", mipmaps);
				return builder.ToString();
			}
		}

		private int _id = -1;

		private int _lazyHashCode = -1;

		private Format config;

		public virtual int ID
		{
			get
			{
				return _id;
			}
		}

		public virtual Format getFormat()
		{
			return config;
		}
		private bool _forcedDeleteTexture = false;

		private bool _disabledTexture = false;

		private bool _drawing = false;

		private bool _copySize = false;

		private bool _scaleSize = false;

		private int[] _cachePixels;

		private string source;

		private int imageWidth = 1, imageHeight = 1;

		private Clip _textureClip;

		private bool _isBatch;

		protected internal string tmpLazy = "tex" + TimeUtils.Millis();

		protected internal int refCount;

		private int pixelWidth;

		private int pixelHeight;

		private Scale scale;

		private Graphics gfx;

		internal bool _closed, _disposed;

		internal IntMap<LTexture> childs;

		internal LTexture parent;

		public virtual bool IsChild()
		{
				return parent != null;
		}

		public virtual LTexture GetParent()
		{
				return parent;
		}

		public virtual int PixelWidth()
		{
			return pixelWidth;
		}

		public virtual int PixelHeight()
		{
			return pixelHeight;
		}

		internal static int _countTexture = 0;

		internal LTexture()
		{
			this._isLoaded = false;
		}

		public LTexture(Graphics gfx, int _id, Format config, int pixWidth, int pixHeight, Scale scale, float dispWidth, float dispHeight)
		{
			this.gfx = gfx;
			this._id = _id;
			this.config = config;
			this.pixelWidth = pixWidth;
			this.pixelHeight = pixHeight;
			this.scale = scale;
			this._textureClip = new Clip(0, 0, dispWidth, dispHeight, false);
			this._isLoaded = false;
			//gfx.game.PutTexture(this);
			_countTexture++;
		}

		public bool IsClosed()
        {
            return false;
        }

        public int GetId()
        {
             return _id; 
        }

		public void LoadTexture()
        {

        }

        public void Close()
        {
            throw new System.NotImplementedException();
        }

        public override LTexture Texture()
        {
            throw new System.NotImplementedException();
        }

        public override float Width()
        {
            throw new System.NotImplementedException();
        }

        public override float Height()
        {
            throw new System.NotImplementedException();
        }

        public override float GetDisplayWidth()
        {
            throw new System.NotImplementedException();
        }

        public override float GetDisplayHeight()
        {
            throw new System.NotImplementedException();
        }

        public override float Sx()
        {
            throw new System.NotImplementedException();
        }

        public override float Sy()
        {
            throw new System.NotImplementedException();
        }

        public override float Tx()
        {
            throw new System.NotImplementedException();
        }

        public override float Ty()
        {
            throw new System.NotImplementedException();
        }

        public override void AddToBatch(BaseBatch batch, uint tint, Affine2f tx, float x, float y, float width, float height)
        {
            throw new System.NotImplementedException();
        }

        public override void AddToBatch(BaseBatch batch, uint tint, Affine2f tx, float dx, float dy, float dw, float dh, float sx, float sy, float sw, float sh)
        {
            throw new System.NotImplementedException();
        }
    }
}
