namespace Loon.Action.Sprite.Node {
	
	public class LNFrameAction : LNAction {
	
		protected internal string _animName;
	
		protected internal LNFrameStruct _fs;
	
		protected internal int _index;
	
		internal LNFrameAction() {
	
		}
	
		public static LNFrameAction Action(string aName, int idx) {
			LNFrameAction action = new LNFrameAction();
			action._animName = aName;
			action._index = idx;
			return action;
		}
	
		public static LNFrameAction Action(LNFrameStruct fs) {
			LNFrameAction action = new LNFrameAction();
			action._fs = fs;
			return action;
		}
	
		public override void Step(float dt) {
			if (this._fs == null) {
				((LNSprite) base._target).SetFrame(this._animName, this._index);
			} else {
				((LNSprite) base._target).InitWithFrameStruct(_fs);
			}
			base._isEnd = true;
		}
	
		public override LNAction Copy() {
			return Action(_fs);
		}
	}
}
