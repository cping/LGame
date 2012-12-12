using Loon.Utils;
namespace Loon.Action.Sprite.Node {
	

	public abstract class LNAction {
	
		public LNAction() {
			this._firstTick = true;
			this._isPause = false;
		}
	
		protected internal Easing _easing;
	
		protected internal float _duration;
	
		protected internal float _elapsed;
	
		protected internal bool _firstTick;
	
		protected internal bool _isEnd;
	
		protected internal bool _isPause;
	
		protected internal LNNode _target;
	
		public void AssignTarget(LNNode node) {
			this._target = node;
		}
	
		public void Pause() {
			this._isPause = true;
		}
	
		public void Resume() {
			this._isPause = false;
		}
	
		public virtual void SetTarget(LNNode node) {
			this._firstTick = true;
			this._isEnd = false;
			this._target = node;
		}
	
		public abstract LNAction Copy();
	
		public void Start() {
			this.SetTarget(this._target);
		}
	
		public virtual void Step(float dt) {
			if (!this._isPause) {
				if (this._firstTick) {
					this._firstTick = false;
					this._elapsed = 0f;
				} else {
					this._elapsed += dt;
				}
				float fx = 0;
				if (_easing != null) {
					fx = _easing.Ease((this._elapsed / this._duration), 1f);
				} else {
					fx = MathUtils.Min((this._elapsed / this._duration), 1f);
				}
				this.Update(fx);
			}
		}
	
		public void Reset() {
			this._elapsed = 0;
			this._isEnd = false;
		}
	
		public void Stop() {
			this._isEnd = true;
		}
	
		public virtual void Update(float time) {
		}
	
		public float GetDuration() {
			return this._duration;
		}
	
		public float GetElapsed() {
			return this._elapsed;
		}
	
		public bool IsEnd() {
			return this._isEnd;
		}
	
		public Easing GetEasing() {
			return _easing;
		}
	
		public void SetEasing(Easing e) {
			this._easing = e;
		}
	}
}
