using loon.utils.reply;

namespace loon
{
    public class BaseIO
    {
		public static GoFuture<string> LoadAsynText(string path)
		{
			LGame game = LSystem.Base;
			if (game != null)
			{
				try
				{
					return game.Assets().GetText(path);
				}
				catch (System.Exception)
				{
					return null;
				}
			}
			return null;
		}

		public static System.IO.Stream OpenStream(string path)
		{
			LGame game = LSystem.Base;
			if (game != null)
			{
				try
				{
					return game.Assets().OpenStream(path);
				}
				catch (System.Exception)
				{
					return null;
				}
			}
			return null;
		}
	}
}
