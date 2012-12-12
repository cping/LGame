namespace Loon.Action.Sprite.Node {

	public class LNFlipY : LNAction {
	
		protected internal bool _flipY;
	
		internal LNFlipY() {
	
		}
	
		public static LNFlipY Action(bool fy) {
			LNFlipY flipy = new LNFlipY();
			flipy._flipY = fy;
			return flipy;
		}
	
		public override void SetTarget(LNNode node) {
			base._firstTick = true;
			base._isEnd = false;
			base._target = node;
			if (base._target   is  LNSprite) {
				((LNSprite) base._target).SetFlipY(_flipY);
			}
		}
	
		public override void Update(float t) {
			base._isEnd = true;
			if (base._target   is  LNSprite) {
				((LNSprite) base._target).SetFlipY(_flipY);
			}
		}
	
		public override LNAction Copy() {
			return Action(_flipY);
		}
	
		public LNFlipY Reverse() {
			return LNFlipY.Action(!_flipY);
		}
	}
}
