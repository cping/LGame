namespace Loon.Action.Sprite.Node {

	public class LNCallFunc : LNAction {
	
		protected internal LNCallFunc.Callback  _c;
	
		internal LNCallFunc() {
	
		}
	
		public static LNCallFunc Action(LNCallFunc.Callback  c) {
			LNCallFunc func = new LNCallFunc();
			func._c = c;
			return func;
		}
	
		public override void Step(float dt) {
			this._c.Invoke();
			base._isEnd = true;
		}
	
		public interface Callback {
			void Invoke();
		}
	
		public override LNAction Copy() {
			return Action(_c);
		}
	}}
