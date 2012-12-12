namespace Loon.Action.Sprite.Node {
	
	public class LNRotateTo : LNAction {
	
		internal LNRotateTo() {
	
		}
	
		protected internal float _diff;
	
		protected internal float _orgAngle;
	
		protected internal float _tarAngle;
	
		public static LNRotateTo Action(float duration, float angle) {
			LNRotateTo to = new LNRotateTo();
			to._tarAngle = angle;
			to._duration = duration;
			return to;
		}
	
		public override void SetTarget(LNNode node) {
			base._firstTick = true;
			base._isEnd = false;
			base._target = node;
			this._orgAngle = node.GetRotation();
			this._diff = this._tarAngle - this._orgAngle;
		}
	
		public override void Update(float t) {
			if (t == 1f) {
				base._isEnd = true;
				base._target.SetRotation(this._tarAngle);
			} else {
				base._target.SetRotation((t * this._diff) + this._orgAngle);
			}
		}
	
		public override LNAction Copy() {
			return Action(_duration, _tarAngle);
		}
	}}
