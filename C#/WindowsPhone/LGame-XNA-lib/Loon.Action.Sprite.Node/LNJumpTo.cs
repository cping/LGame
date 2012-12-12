using Loon.Core.Geom;
namespace Loon.Action.Sprite.Node {
	
	public class LNJumpTo : LNJumpBy {
	
		internal LNJumpTo() {
	
		}

        new public static LNJumpTo Action(float duration, Vector2f delta, float height,
				int jumps) {
			LNJumpTo to = new LNJumpTo();
			to._duration = duration;
			to._delta = delta;
			to._height = height;
			to._jumps = jumps;
			return to;
		}
	
		public override void SetTarget(LNNode node) {
			base._firstTick = true;
			base._isEnd = false;
			base._target = node;
			base._orgPos = node.GetPosition();
			base._delta.Set(this._delta.x - this._orgPos.x, this._delta.y
					- this._orgPos.y);
		}
	}
}
