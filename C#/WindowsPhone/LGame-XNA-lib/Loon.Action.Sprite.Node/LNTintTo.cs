using Loon.Core.Graphics;
namespace Loon.Action.Sprite.Node {
	
	public class LNTintTo : LNAction {
	
		internal LNTintTo() {
	
		}
	
		protected internal LColor _to;
	
		protected internal LColor _from;
	
		public static LNTintTo Action(float t, LColor c) {
			LNTintTo tint = new LNTintTo();
			tint._duration = t;
			tint._to = c;
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
			int tred = _to.R;
			int tgreen = _to.G;
			int tblue = _to.B;
			int r = (int) (fred + (tred - fred) * t);
			int g = (int) (fgreen + (tgreen - fgreen) * t);
			int b = (int) (fblue + (tblue - fblue) * t);
			if (r == tred && g == tgreen && b == tblue) {
				base._isEnd = true;
			} else {
				base._target.SetColor(r, g, b);
			}
		}
	
		public override LNAction Copy() {
			return Action(_duration, _to);
		}
	}
}
