
using loon.geom;
using loon.utils;
using loon.utils.reply;

namespace loon.opengl
{
  public  abstract class Painter : TextureSource
	{
		public object Tag;

		public const int TOP_LEFT = 0;

		public const int TOP_RIGHT = 1;

		public const int BOTTOM_RIGHT = 2;

		public const int BOTTOM_LEFT = 3;

		public abstract LTexture Texture();

		public abstract float Width();

		public abstract float Height();

		public abstract float GetDisplayWidth();

		public abstract float GetDisplayHeight();

		public abstract float Sx();

		public abstract float Sy();

		public abstract float Tx();

		public abstract float Ty();

		public abstract void AddToBatch(BaseBatch batch, uint tint, Affine2f tx, float x, float y, float width, float height);

		public abstract void AddToBatch(BaseBatch batch, uint tint, Affine2f tx, float dx, float dy, float dw, float dh, float sx, float sy, float sw, float sh);

		public override bool IsLoaded()
		{
		
				return _isLoaded;
			
		}

		public override Painter Draw()
		{
			return this;
		}

		public override GoFuture<Painter> TileAsync()
		{
			return GoFuture<Painter>.Success(this);
		}

		public override string ToString()
		{
			StringKeyValue builder = new StringKeyValue("Painter");
			builder.Kv("size", Width() + "x" + Height()).Comma().Kv("xOff", Sx()).Comma().Kv("yOff", Sy()).Comma().Kv("widthRatio", Tx()).Comma().Kv("heightRatio", Ty());
			return builder.ToString() + " <- " + Texture();
		}
	}
}
