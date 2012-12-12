using Loon.Core.Timer;
namespace Loon.Action.Sprite.Painting {

	public class DrawableGameComponent : GameComponent, IDrawable {
	
		private bool _isInitialized;
		private bool _isVisible;
		private int _drawOrder;
	
		public DrawableGameComponent(DrawableScreen game):base(game){
			
			SetVisible(true);
		}
	
		public override void Initialize() {
			if (!_isInitialized) {
				_isInitialized = true;
				LoadContent();
			}
		}
	
		protected internal void LoadContent() {
		}
	
		protected internal void UnloadContent() {
		}
	
		public int GetDrawOrder() {
			return _drawOrder;
		}
	
		private ComponentEvent DrawOrder;
	
		public void SetDrawOrder(int value_ren) {
			_drawOrder = value_ren;
			if (DrawOrder != null) {
				DrawOrder.Invoke(this);
			}
		}
	
		public bool GetVisible() {
			return _isVisible;
		}
	
		private ComponentEvent Visible;
	
		public void SetVisible(bool value_ren) {
			if (_isVisible != value_ren) {
				_isVisible = value_ren;
				if (Visible != null) {
					Visible.Invoke(this);
				}
			}
		}
	
		public void Draw(SpriteBatch batch, GameTime gameTime) {
		}
	
		public void SetDrawOrder(ComponentEvent drawOrder) {
			DrawOrder = drawOrder;
		}
	
		public void SetVisible(ComponentEvent visible) {
			Visible = visible;
		}
	
	}
}
