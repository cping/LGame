using Loon.Core.Geom;
namespace Loon.Action.Sprite.Node {
	
	public class LNBezierTo : LNBezierBy {
	
		protected internal LNBezierDef _originalconfig;
	
		internal LNBezierTo() {
	
		}

        new public static LNBezierTo Action(float t, LNBezierDef c)
        {
			LNBezierTo bezier = new LNBezierTo();
			bezier._config = c;
			bezier._startPosition = new Vector2f();
			bezier._originalconfig = new LNBezierDef();
			bezier._originalconfig.controlPoint_1 = new Vector2f(
					c.controlPoint_1.x, c.controlPoint_1.y);
			bezier._originalconfig.controlPoint_2 = new Vector2f(
					c.controlPoint_2.x, c.controlPoint_2.y);
			bezier._originalconfig.endPosition = new Vector2f(c.endPosition.x,
					c.endPosition.y);
			return bezier;
		}
	
		public override void SetTarget(LNNode node) {
			base.SetTarget(node);
			_config.controlPoint_1 = _originalconfig.controlPoint_1
					.Sub(_startPosition);
			_config.controlPoint_2 = _originalconfig.controlPoint_2
					.Sub(_startPosition);
			_config.endPosition = _originalconfig.endPosition.Sub(_startPosition);
		}

        new public LNBezierTo Reverse()
        {
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
