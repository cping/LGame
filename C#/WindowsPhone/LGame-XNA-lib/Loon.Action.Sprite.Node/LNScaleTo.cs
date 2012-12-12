using Loon.Core.Geom;
using Loon.Utils;
namespace Loon.Action.Sprite.Node {
	
	public class LNScaleTo : LNAction {
	
		internal LNScaleTo() {
	
		}
	
		protected internal float dt;
	
		protected internal float _deltaX, _deltaY;
	
		protected internal float _startX, _startY;
	
		protected internal float _endX, _endY;
	
		public static LNScaleTo Action(float duration, Vector2f s) {
			LNScaleTo to = new LNScaleTo();
			to._duration = duration;
			to._endX = s.x;
			to._endY = s.y;
			return to;
		}
	
		public static LNScaleTo Action(float duration, float s) {
			LNScaleTo to = new LNScaleTo();
			to._duration = duration;
			to._endX = s;
			to._endY = s;
			return to;
		}
	
		public static LNScaleTo Action(float duration, float sx, float sy) {
			LNScaleTo to = new LNScaleTo();
			to._duration = duration;
			to._endX = sx;
			to._endY = sy;
			return to;
		}
	
		public override void SetTarget(LNNode node) {
			base._firstTick = true;
			base._isEnd = false;
			base._target = node;
			_startX = base._target.GetScaleX();
			_startY = base._target.GetScaleY();
			_deltaX = _endX - _startX;
			_deltaY = _endY - _startY;
		}
	
		public override void Update(float d) {
			dt += MathUtils.Max(d, 0.01f);
			base._target.SetScale(_startX + (_deltaX * dt), _startY
					+ (_deltaY * dt));
			base._isEnd = ((_deltaX > 0) ? (base._target.GetScaleX() >= _endX)
					: (base._target.GetScaleX() <= _endX))
					&& ((_deltaY > 0) ? (base._target.GetScaleY() >= _endY)
							: (base._target.GetScaleY() <= _endY));
	
		}
	
		public override LNAction Copy() {
			return Action(_duration, _endX, _endY);
		}
	
		public LNScaleTo Reverse() {
			return Action(_duration, 1f / _endX, 1f / _endY);
		}
	}
}
