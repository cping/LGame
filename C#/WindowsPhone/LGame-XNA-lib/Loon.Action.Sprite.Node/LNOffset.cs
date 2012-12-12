using Loon.Core.Geom;
namespace Loon.Action.Sprite.Node {
	
	public class LNOffset : LNAction {
	
		internal LNOffset() {
	
		}
	
		protected internal Vector2f _offset;
	
		public static LNOffset Action(float sx, float sy) {
			LNOffset off = new LNOffset();
			off._offset = new Vector2f(sx, sy);
			return off;
		}
	
		public static LNOffset Action(Vector2f v) {
			LNOffset off = new LNOffset();
			off._offset = v;
			return off;
		}
	
		public override void Step(float t) {
			base._target.SetOffset(_offset);
		}
	
		public override void Update(float time) {
			if (time == 1f) {
				base.Reset();
			}
		}
	
		public override LNAction Copy() {
			return Action(_offset);
		}
	
		public LNAction Reverse() {
			return Action(0, 0);
		}
	}
}
