namespace Loon.Action.Sprite.Node {
	
	public class LNCallFuncN : LNAction {
		protected internal LNCallFuncN.Callback  _c;
	
		internal LNCallFuncN() {
	
		}
	
		public static LNCallFuncN Action(LNCallFuncN.Callback  c) {
			LNCallFuncN cn = new LNCallFuncN();
			cn._c = c;
			return cn;
		}
	
		public override void Step(float dt) {
			_c.Invoke(base._target);
			base._isEnd = true;
		}
	
		public interface Callback {
			void Invoke(LNNode node);
		}
	
		public override LNAction Copy() {
			return Action(_c);
		}
	}}
