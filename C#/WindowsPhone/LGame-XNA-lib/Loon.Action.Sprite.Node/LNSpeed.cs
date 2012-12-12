namespace Loon.Action.Sprite.Node {
	
	public class LNSpeed : LNAction {
	
		internal LNSpeed() {
	
		}
	
		protected internal LNAction _other;
	
		protected internal float _speed;
	
		public static LNSpeed Action(LNAction action, float s) {
			LNSpeed speed = new LNSpeed();
			speed._other = action;
			speed._speed = s;
			return speed;
		}
	
		public override void SetTarget(LNNode node) {
			base._firstTick = true;
			base._isEnd = false;
			base._target = node;
			_other.SetTarget(node);
		}
	
		public override void Step(float dt) {
			base.Step(dt);
			_other.Step(dt * _speed);
		}
	
		public override void Update(float t) {
			_other.Update(t);
			if (_other._isEnd) {
				base._isEnd = true;
			}
		}
	
		public float GetSpeed() {
			return _speed;
		}
	
		public void SetSpeed(float speed) {
			this._speed = speed;
		}
	
		public override LNAction Copy() {
			return Action(_other, _speed);
		}
	
	}
}
