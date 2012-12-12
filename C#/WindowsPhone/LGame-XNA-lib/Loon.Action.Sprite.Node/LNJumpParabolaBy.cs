using Loon.Core.Geom;
namespace Loon.Action.Sprite.Node {
	
	public class LNJumpParabolaBy : LNAction {
	
		internal LNJumpParabolaBy() {
	
		}
	
		public float _a;
	
		public float _b;
	
		public float _c;
	
		public Vector2f _delta;
	
		public float _height;
	
		public Vector2f _refPoint;
	
		public Vector2f _startPosition;
	
		public static LNJumpParabolaBy Action(float duration, Vector2f position,
				Vector2f refPoint) {
			LNJumpParabolaBy by = new LNJumpParabolaBy();
			by._delta = position;
			by._duration = duration;
			by._refPoint = refPoint;
			return by;
		}
	
		public override void SetTarget(LNNode node) {
			base._firstTick = true;
			base._isEnd = false;
			base._target = node;
			this._startPosition = base._target._position;
			this._a = ((this._refPoint.y / this._refPoint.x) - (this._delta.y / this._delta.x))
					/ (this._refPoint.x - this._delta.x);
			this._b = (this._delta.y / this._delta.x)
					- ((this._delta.x + (2f * this._startPosition.x)) * this._a);
			this._c = (this._startPosition.y - ((this._a * this._startPosition.x) * this._startPosition.x))
					- (this._b * this._startPosition.x);
		}
	
		public override void Update(float t) {
			float num = (t * this._delta.x) + this._startPosition.x;
			float x = num;
			float y = (((this._a * x) * x) + (this._b * x)) + this._c;
			base._target.SetPosition(x, y);
		}
	
		public override LNAction Copy() {
			return Action(_duration, _delta, _refPoint);
		}
	
		public LNAction Reverse() {
			return Action(_duration, _delta.Negate(), _refPoint);
		}
	}}
