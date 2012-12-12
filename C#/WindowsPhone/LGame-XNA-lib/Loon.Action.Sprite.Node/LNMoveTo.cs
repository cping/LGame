using Loon.Core.Geom;
namespace Loon.Action.Sprite.Node {

	public class LNMoveTo : LNAction {
	
		protected internal Vector2f _diff;
	
		protected internal Vector2f _orgPos;
	
		protected internal Vector2f _pos;
	
		internal LNMoveTo() {
	
		}
	
		public static LNMoveTo Action(float duration, Vector2f pos) {
			LNMoveTo to = new LNMoveTo();
			to._pos = pos;
			to._duration = duration;
			return to;
		}
	
		public static LNMoveTo Action(float duration, float dx, float dy) {
			return LNMoveTo.Action(duration, new Vector2f(dx, dy));
		}
	
		public override void SetTarget(LNNode node) {
			base._firstTick = true;
			base._isEnd = false;
			base._target = node;
			this._orgPos = node.GetPosition();
			this._diff = this._pos.Sub(this._orgPos);
		}
	
		public override void Update(float t) {
			if (t == 1f) {
				base._isEnd = true;
				base._target.SetPosition(this._pos);
			} else {
				base._target.SetPosition(this._diff.Mul(t).Add(this._orgPos));
			}
		}

        public override LNAction Copy()
        {
			return Action(_duration, _pos);
		}
	
		public virtual LNAction Reverse() {
			return Action(_duration, -_pos.x, -_pos.y);
		}
	}
}
