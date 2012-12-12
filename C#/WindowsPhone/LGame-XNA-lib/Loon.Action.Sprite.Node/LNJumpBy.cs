using Loon.Core.Geom;
using Loon.Utils;
namespace Loon.Action.Sprite.Node {
	
	public class LNJumpBy : LNAction {
	
		internal LNJumpBy() {
	
		}
	
		protected internal Vector2f _delta;
	
		protected internal float _height;
	
		protected internal int _jumps;
	
		protected internal Vector2f _orgPos;
	
		public static LNJumpBy Action(float duration, float d, float height,
				int jumps) {
			return Action(duration, new Vector2f(d, d), height, jumps);
		}
	
		public static LNJumpBy Action(float duration, Vector2f delta, float height,
				int jumps) {
			LNJumpBy by = new LNJumpBy();
			by._duration = duration;
			by._delta = delta;
			by._height = height;
			by._jumps = jumps;
			return by;
		}
	
		public override void SetTarget(LNNode node) {
			base._firstTick = true;
			base._isEnd = false;
			base._target = node;
			this._orgPos = node.GetPosition();
		}
	
		public override void Update(float t) {
			if (t == 1f) {
				base._isEnd = true;
				base._target.SetPosition(this._delta.x + this._orgPos.x,
						this._delta.y + this._orgPos.y);
			} else {
				float num = this._height
						* MathUtils.Abs(MathUtils
								.Sin(((t * 3.141593f) * this._jumps)));
				num += this._delta.y * t;
				float num2 = this._delta.x * t;
				base._target.SetPosition(num2 + this._orgPos.x, num
						+ this._orgPos.y);
			}
		}
	
		public override LNAction Copy() {
			return Action(_duration, _delta, _height, _jumps);
		}
	
		public LNJumpBy Reverse() {
			return Action(_duration, _delta.Negate(), _height, _jumps);
		}
	}
}
