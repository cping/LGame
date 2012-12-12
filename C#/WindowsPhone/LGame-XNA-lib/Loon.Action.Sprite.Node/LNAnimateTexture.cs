namespace Loon.Action.Sprite.Node {
	
	public class LNAnimateTexture : LNAction {
	
		public LNAnimationTexture _ans;
	
		public string _animName;
	
		public bool _restoreOriginalFrame;
	
		internal LNAnimateTexture() {
	
		}
	
		public static LNAnimateTexture Action(string fileName, int width, int height) {
			return Action(new LNAnimationTexture(fileName, width, height));
		}
	
		public static LNAnimateTexture Action(string fileName, int maxFrame,
				int width, int height) {
			return Action(new LNAnimationTexture(fileName, maxFrame, width, height));
		}
	
		public static LNAnimateTexture Action(string fileName, int maxFrame,
				int width, int height, float duration) {
			return Action(new LNAnimationTexture(fileName, maxFrame, width, height,
					duration));
		}
	
		public static LNAnimateTexture Action(string aName, string fileName,
				int maxFrame, int width, int height, float duration) {
			return Action(new LNAnimationTexture(aName, fileName, maxFrame, width,
					height, duration));
		}
	
		public static LNAnimateTexture Action(LNAnimationTexture anim) {
			LNAnimateTexture animate = new LNAnimateTexture();
			animate._ans = anim;
			animate._duration = anim.GetDuration();
			animate._animName = anim.GetName();
			animate._restoreOriginalFrame = true;
			return animate;
		}
	
		public static LNAnimateTexture Action(LNAnimationTexture anim,
				bool restoreOriginalFrame) {
			LNAnimateTexture animate = new LNAnimateTexture();
			animate._ans = anim;
			animate._duration = anim.GetDuration();
			animate._animName = anim.GetName();
			animate._restoreOriginalFrame = restoreOriginalFrame;
			return animate;
		}
	
		public override void SetTarget(LNNode node) {
			base._firstTick = true;
			base._isEnd = false;
			base._target = node;
		}
	
		public override void Update(float t) {
			if (base._target   is  LNSprite) {
				if (t == 1f) {
					base.Reset();
					if (this._restoreOriginalFrame) {
						((LNSprite) base._target)
								.InitWithTexture(_ans.GetFrame(0));
					}
				} else {
					((LNSprite) base._target).InitWithTexture(_ans
							.GetFrameByTime(t));
				}
			}
		}
	
		public override LNAction Copy() {
			return Action(_ans, _restoreOriginalFrame);
		}
	}
}
