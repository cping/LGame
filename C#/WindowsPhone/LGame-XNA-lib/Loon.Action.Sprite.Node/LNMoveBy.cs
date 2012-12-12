using Loon.Core.Geom;
namespace Loon.Action.Sprite.Node {
	
	public class LNMoveBy : LNMoveTo {
	
		protected internal float _lastTime;
	
		internal LNMoveBy() {
	
		}

        new public static LNMoveBy Action(float duration, Vector2f pos)
        {
			LNMoveBy by = new LNMoveBy();
			by._diff = pos;
			by._duration = duration;
			by._lastTime = 0f;
			return by;
		}

        new public static LNMoveBy Action(float duration, float dx, float dy)
        {
            return LNMoveBy.Action(duration, new Vector2f(dx, dy));
		}
	
		public override void SetTarget(LNNode node) {
			base._firstTick = true;
			base._isEnd = false;
			base._target = node;
			base._orgPos = node.GetPosition();
			base._pos = base._orgPos.Add(base._diff);
		}
	
		public override void Update(float t) {
			if (t == 1f) {
				base._isEnd = true;
				base._target.SetPosition(base._pos);
			} else {
				Vector2f position = base._target.GetPosition();
				base._target.SetPosition(base._diff.Mul((t - this._lastTime))
						.Add(position));
				this._lastTime = t;
			}
		}

        new public LNMoveBy Reverse()
        {
            return Action(_duration, _diff.Negate());
		}
	}
}
