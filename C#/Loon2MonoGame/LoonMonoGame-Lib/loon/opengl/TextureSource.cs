using loon.utils.reply;

namespace loon.opengl
{
	public abstract class TextureSource
	{

		protected internal bool _isLoaded = true;

		protected internal bool _isReload = false;

		public abstract bool IsLoaded();

		public abstract Painter Draw();

		public abstract GoFuture<Painter> TileAsync();


	}
}
