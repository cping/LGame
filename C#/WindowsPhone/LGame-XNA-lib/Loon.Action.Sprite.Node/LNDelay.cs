namespace Loon.Action.Sprite.Node {
	
	public class LNDelay : LNAction {
	
		internal LNDelay() {
	
		}
	
		public static LNDelay Action(float duration) {
			LNDelay delay = new LNDelay();
			delay._duration = duration;
			return delay;
		}
	
		public override void Update(float t) {
			if (t == 1f) {
				base._isEnd = true;
			}
		}
	
		public override LNAction Copy() {
			return Action(_duration);
		}
	}
}
