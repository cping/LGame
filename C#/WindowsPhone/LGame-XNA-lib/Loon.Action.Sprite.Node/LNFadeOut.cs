namespace Loon.Action.Sprite.Node {
	
	public class LNFadeOut : LNAction {
	
		protected internal float _diff;
	
		protected internal float _orgOpacity;
	
		protected internal float _tarOpacity;
	
		internal LNFadeOut() {
	
		}
	
		public static LNFadeOut Action(float duartion) {
			LNFadeOut outs = new LNFadeOut();
			outs._tarOpacity = 0f;
			outs._duration = duartion;
			return outs;
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
	
		public LNFadeIn Reverse() {
			return Node.LNFadeIn.Action(_duration);
		}
	}
}
