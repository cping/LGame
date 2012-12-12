namespace Loon.Action.Sprite.Node {
	
	public class LNFadeIn : LNAction {
	
		protected internal float _diff;
	
		protected internal float _orgOpacity;
	
		protected internal float _tarOpacity;
	
		internal LNFadeIn() {
	
		}
	
		public static LNFadeIn Action(float duration) {
			LNFadeIn ins = new LNFadeIn();
			ins._tarOpacity = 255f;
			ins._duration = duration;
			return ins;
		}
	
		public override void SetTarget(LNNode node) {
			base._firstTick = true;
			base._isEnd = false;
			base._target = node;
			this._orgOpacity = node._alpha * 255f;
			this._diff = this._tarOpacity - this._orgOpacity;
		}
	
		public override void Update(float t) {
			if (t == 1f) {
				base._isEnd = true;
				base._target.SetAlpha(1f);
			} else {
				base._target
						.SetAlpha(((t * this._diff) + this._orgOpacity) / 255f);
			}
		}
	
		public override LNAction Copy() {
			return Action(_duration);
		}
	
		public LNFadeOut Reverse() {
			return Node.LNFadeOut.Action(_duration);
		}
	}}
