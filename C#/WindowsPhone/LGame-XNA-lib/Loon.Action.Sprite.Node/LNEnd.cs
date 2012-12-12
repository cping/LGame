namespace Loon.Action.Sprite.Node {

	public class LNEnd : LNAction {
	
		internal LNEnd() {
	
		}
	
		public static LNEnd Action() {
			LNEnd action = new LNEnd();
			return action;
		}
	
		public override void Step(float dt) {
			base._isEnd = true;
			_target.StopAllAction();
		}
	
		public override LNAction Copy() {
			return Action();
		}
	
		public LNAction Reverse() {
			return Action();
		}
	}
}
