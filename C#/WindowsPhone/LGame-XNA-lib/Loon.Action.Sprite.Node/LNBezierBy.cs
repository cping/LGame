using Loon.Core.Geom;
namespace Loon.Action.Sprite.Node {
	
	public class LNBezierBy : LNAction {
	
		protected internal LNBezierDef _config;
	
		protected internal Vector2f _startPosition;
	
		internal LNBezierBy() {
	
		}
	
		public static LNBezierBy Action(float t, LNBezierDef c) {
			LNBezierBy bezier = new LNBezierBy();
			bezier._config = c;
			return bezier;
		}
	
		public override void SetTarget(LNNode node) {
			base._firstTick = true;
			base._isEnd = false;
			base._target = node;
			_startPosition = node.GetPosition();
		}
	
		public override void Update(float t) {
			float xa = 0;
			float xb = _config.controlPoint_1.x;
			float xc = _config.controlPoint_2.x;
			float xd = _config.endPosition.x;
	
			float ya = 0;
			float yb = _config.controlPoint_1.y;
			float yc = _config.controlPoint_2.y;
			float yd = _config.endPosition.y;
	
			float x = Node.LNBezierDef.BezierAt(xa, xb, xc, xd, t);
			float y = Node.LNBezierDef.BezierAt(ya, yb, yc, yd, t);
			_target.SetPosition(_startPosition.x + x, _startPosition.y + y);
		}
	
		public override LNAction Copy() {
			return Action(_duration, _config);
		}
	
		public virtual LNAction Reverse() {
			LNBezierDef r = new LNBezierDef();
			r.endPosition = _config.endPosition.Negate();
			r.controlPoint_1 = _config.controlPoint_2.Add(_config.endPosition
					.Negate());
			r.controlPoint_2 = _config.controlPoint_1.Add(_config.endPosition
					.Negate());
			return Action(_duration, r);
		}
	}
}
