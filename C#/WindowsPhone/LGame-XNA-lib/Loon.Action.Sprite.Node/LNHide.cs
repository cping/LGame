namespace Loon.Action.Sprite.Node {
	
	public class LNHide : LNAction {
	
		internal LNHide() {
	
		}
	
		public static LNHide Action() {
			return new LNHide();
		}
	
		public override void Step(float dt) {
			base._target._visible = false;
			base._isEnd = true;
		}
	
		public override LNAction Copy() {
			return Action();
		}
	
		public LNShow Reverse() {
			return Node.LNShow.Action();
		}
	
	}
}
