namespace Loon.Action.Sprite.Node {
	
	public class LNFlipX : LNAction {
	
		protected internal bool _flipX;
	
		internal LNFlipX() {
	
		}
	
		public static LNFlipX Action(bool fx) {
			LNFlipX flipx = new LNFlipX();
			flipx._flipX = fx;
			return flipx;
		}
	
		public override void SetTarget(LNNode node) {
			base._firstTick = true;
			base._isEnd = false;
			base._target = node;
			if (base._target   is  LNSprite) {
				((LNSprite) base._target).SetFlipX(_flipX);
			}
		}
	
		public override void Update(float t) {
			base._isEnd = true;
			if (base._target   is  LNSprite) {
				((LNSprite) base._target).SetFlipX(_flipX);
			}
		}
	
		public override LNAction Copy() {
			return Action(_flipX);
		}
	
		public LNFlipX Reverse() {
			return LNFlipX.Action(!_flipX);
		}
	}}
