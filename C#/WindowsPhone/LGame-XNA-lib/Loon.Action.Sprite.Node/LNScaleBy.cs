namespace Loon.Action.Sprite.Node {
	
	public class LNScaleBy : LNAction {
	
		internal LNScaleBy() {
	
		}
	
		protected internal float _scaleX;
	
		protected internal float _scaleY;
	
		public static LNScaleBy Action(float scale) {
			LNScaleBy action = new LNScaleBy();
			action._scaleX = scale;
			action._scaleY = scale;
			return action;
		}
	
		public static LNScaleBy Action(float sX, float sY) {
			LNScaleBy action = new LNScaleBy();
			action._scaleX = sX;
			action._scaleY = sY;
			return action;
		}
	
		public override void Step(float dt) {
			base._target.SetScale(this._scaleX, this._scaleY);
			base._isEnd = true;
		}
	
		public override LNAction Copy() {
			return Action(_scaleX, _scaleY);
		}
	}
}
