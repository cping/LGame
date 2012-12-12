namespace Loon.Action.Sprite.Node {
	
	public class LNAlphaAction : LNAction {
	
		protected internal float _alpha;
	
		private float oldAlpha;
	
		internal LNAlphaAction() {
	
		}
	
		public static LNAlphaAction Action(float a) {
			LNAlphaAction action = new LNAlphaAction();
			action._alpha = a;
			return action;
		}
	
		public override void Step(float dt) {
			base._target.SetAlpha(this._alpha);
			base._isEnd = true;
			oldAlpha = _target._alpha;
		}
	
		public override LNAction Copy() {
			return Action(_alpha);
		}
	
		public LNAction Reverse() {
			return Action(oldAlpha);
		}
	}
}
