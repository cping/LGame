namespace Loon.Action.Sprite.Node {
	
	public class LNRepeatForever : LNAction {

		internal LNRepeatForever() {
	
		}
	
		protected internal LNAction _action;
	
		public static LNRepeatForever Action(LNAction action) {
			LNRepeatForever forever = new LNRepeatForever();
			forever._action = action;
			return forever;
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
				base._elapsed = 0f;
			}
		}
	
		public override LNAction Copy() {
			return Action(_action);
		}
	}
}
