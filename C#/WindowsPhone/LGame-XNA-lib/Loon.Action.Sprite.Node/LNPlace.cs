using Loon.Core.Geom;
namespace Loon.Action.Sprite.Node {
	
	public class LNPlace : LNAction {

		internal LNPlace() {
	
		}
	
		protected internal Vector2f _pos;
	
		public static LNPlace Action(Vector2f pos) {
			LNPlace place = new LNPlace();
			place._pos = pos;
			return place;
		}
	
		public override void Step(float dt) {
			base._target.SetPosition(this._pos);
			base._isEnd = true;
		}
	
		public override LNAction Copy() {
			return Action(_pos);
		}
	}
}
