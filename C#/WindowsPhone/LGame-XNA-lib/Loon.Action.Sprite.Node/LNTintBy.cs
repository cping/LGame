using Loon.Core.Graphics;
namespace Loon.Action.Sprite.Node {

	public class LNTintBy : LNAction {
	
		internal LNTintBy() {
	
		}
	
		protected internal LColor _delta;
	
		protected internal LColor _from;
	
		public static LNTintBy Action(float t, LColor c) {
			LNTintBy tint = new LNTintBy();
			tint._duration = t;
			tint._delta = c;
			return tint;
		}
	
		public override void SetTarget(LNNode node) {
			base._firstTick = true;
			base._isEnd = false;
			base._target = node;
			_from = node.GetColor();
		}
	
		public override void Update(float t) {
			int fred = _from.R;
			int fgreen = _from.G;
			int fblue = _from.B;
			int dred = _delta.R;
			int dgreen = _delta.G;
			int dblue = _delta.B;
			int r = (int) (fred + dred * t);
			int g = (int) (fgreen + dgreen * t);
			int b = (int) (fblue + dblue * t);
			base._target.SetColor(r, g, b);
		}
	
		public override LNAction Copy() {
			return Action(_duration, _delta);
		}
	
		public LNTintBy Reverse() {
			return Action(_duration, new LColor(-_delta.r, -_delta.g, -_delta.b));
		}
	}
}
