namespace Loon.Action.Sprite.Node {
	
	public class LNRotationAction : LNAction {
	
		internal LNRotationAction() {
	
		}
	
		protected internal float _rotation;
	
		public static LNRotationAction Action(float r) {
			LNRotationAction action = new LNRotationAction();
			action._rotation = r;
			return action;
		}
	
		public override void Step(float dt) {
			base._target.SetRotation(this._rotation);
			base._isEnd = true;
		}
	
		public override LNAction Copy() {
			return Action(_rotation);
		}
	}}
