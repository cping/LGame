namespace Loon.Action.Sprite.Node {
	
	public class LNCallFuncND : LNAction {
	
		protected internal LNCallFuncND.Callback  _c;
	
		protected internal object _data;
	
		internal LNCallFuncND() {
	
		}

        public static LNCallFuncND Action(LNCallFuncND.Callback c, object data)
        {
			LNCallFuncND cnd = new LNCallFuncND();
			cnd._c = c;
			cnd._data = data;
			return cnd;
		}
	
		public override void Step(float dt) {
			_c.Invoke(base._target, this._data);
			base._isEnd = true;
		}
	
		public interface Callback {
            void Invoke(LNNode node, object data);
		}
	
		public override LNAction Copy() {
			return Action(_c, _data);
		}
	}
}
