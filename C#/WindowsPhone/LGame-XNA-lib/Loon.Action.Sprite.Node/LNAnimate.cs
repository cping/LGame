namespace Loon.Action.Sprite.Node {
	
	public class LNAnimate : LNAction {
	
		public LNAnimation _ans;
	
		public string _animName;
	
		public bool _restoreOriginalFrame;
	
		internal LNAnimate() {
	
		}
	
		public static LNAnimate Action(LNAnimation anim) {
			LNAnimate animate = new LNAnimate();
			animate._ans = anim;
			animate._duration = anim.GetDuration();
			animate._animName = anim.GetName();
			animate._restoreOriginalFrame = true;
			return animate;
		}
	
		public static LNAnimate Action(LNAnimation anim,
				bool restoreOriginalFrame) {
			LNAnimate animate = new LNAnimate();
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
			if (base._target   is  LNSprite) {
				((LNSprite) base._target).SetAnimation(_ans);
			}
		}
	
		public override void Update(float t) {
			if (base._target   is  LNSprite) {
				if (t == 1f) {
					base._isEnd = true;
					if (this._restoreOriginalFrame) {
						((LNSprite) base._target).SetFrame(0);
					}
				} else {
					((LNSprite) base._target).SetFrameTime(t);
				}
			}
		}
	
		public override LNAction Copy() {
			return Action(_ans, _restoreOriginalFrame);
		}
	}}
