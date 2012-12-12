using Loon.Core.Timer;
namespace Loon.Action.Sprite.Painting {
	
	public interface IUpdateable {
	
		void Update(GameTime gameTime);
	
		bool GetEnabled();
	
		int GetUpdateOrder();
	
	}
}
