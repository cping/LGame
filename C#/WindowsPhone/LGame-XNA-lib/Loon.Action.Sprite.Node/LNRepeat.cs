namespace Loon.Action.Sprite.Node {
	
	public class LNRepeat : LNAction {
	
		internal LNRepeat() {
	
		}
	
		protected internal LNAction _action;
	
		private int time;
	
		public static LNRepeat Action(LNAction action, int t) {
			LNRepeat repeat = new LNRepeat();
			repeat.time = t;
			repeat._action = action;
			repeat._duration = t * action.GetDuration();
			return repeat;
		}
	
		public override void SetTarget(LNNode node) {
			base._firstTick = true;
			base._isEnd = false;
			base._target = node;
			this._action.SetTarget(base._target);
		}
	
		public override void Step(float dt) {
			if (base._firstTick) {
				base._firstTick = false;
				base._elapsed = 0f;
			} else {
				base._elapsed += dt;
			}
			this._action.Step(dt);
			if (this._action.IsEnd()) {
				this._action.Start();
			}
			if (base._elapsed > base._duration) {
				base._isEnd = true;
			}
		}
	
		public override LNAction Copy() {
			return Action(_action, time);
		}
	}}
