namespace Loon.Action.Sprite.Node {
	
	public class LNRotateBy : LNRotateTo {
		internal LNRotateBy() {
	
		}
	
		new public static LNRotateBy Action(float duration, float angle) {
			LNRotateBy by = new LNRotateBy();
			by._diff = angle;
			by._duration = duration;
			return by;
		}
	
		public override void SetTarget(LNNode node) {
			base._firstTick = true;
			base._isEnd = false;
			base._target = node;
			base._orgAngle = node.GetRotation();
			base._tarAngle = base._diff + base._orgAngle;
		}
	}
}
