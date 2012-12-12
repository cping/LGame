using Loon.Core.Timer;
namespace Loon.Action.Sprite.Painting {

	public interface IDrawable {
	
		int GetDrawOrder();
	
		bool GetVisible();
	
		void Draw(SpriteBatch batch, GameTime gameTime);
	
	}
}
