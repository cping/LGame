using Microsoft.Xna.Framework.Content;
using Microsoft.Xna.Framework.Graphics;

namespace loon
{
	public interface Platform
	{

		void Close();

		int GetContainerWidth();

		int GetContainerHeight();

		Platform_Orientation GetOrientation();

		LGame GetGame();

	}

	public enum Platform_Orientation
	{
		Portrait,
		Landscape
	}
}
