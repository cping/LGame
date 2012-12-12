namespace Loon.Action.Sprite.Node {
	
	public class LNShow : LNAction {
	
		internal LNShow() {
	
		}
	
		public static LNShow Action() {
			return new LNShow();
		}
	
		public override void Step(float dt) {
			base._target._visible = true;
			base._isEnd = true;
		}
	
		public override LNAction Copy() {
			return Action();
		}
	
		public LNHide Reverse() {
			return Node.LNHide.Action();
		}
	}
}
