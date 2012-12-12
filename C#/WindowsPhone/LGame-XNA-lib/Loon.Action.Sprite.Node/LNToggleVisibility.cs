namespace Loon.Action.Sprite.Node {
	
	
	public class LNToggleVisibility : LNAction {
	
		internal LNToggleVisibility() {
	
		}
	
		public static LNToggleVisibility Action() {
			return new LNToggleVisibility();
		}
	
		public override void Step(float dt) {
			bool visible = base._target._visible;
			base._target._visible = !visible;
			base._isEnd = true;
		}
	
		public override LNAction Copy() {
			return Action();
		}
	
	}
}
