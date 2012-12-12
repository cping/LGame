namespace Loon.Action.Sprite.Node {
	
	public class LNBlink : LNAction {
	
		protected internal int _times;
	
		internal LNBlink() {
	
		}
	
		public static LNBlink Action(float duration, int times) {
			LNBlink blink = new LNBlink();
			blink._duration = duration;
			blink._times = times;
			return blink;
		}
	
		public override void Update(float time) {
			float slice = 1.0f / _times;
			float m = time % slice;
			base._target._visible = (m > slice / 2) ? true : false;
		}
	
		public override LNAction Copy() {
			return Action(_duration, _times);
		}
	}}
