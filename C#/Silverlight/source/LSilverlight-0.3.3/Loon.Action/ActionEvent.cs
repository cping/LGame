namespace Loon.Action {

    using Loon.Core.Timer;

	public abstract class ActionEvent {
	
		private LTimer timer;
	
		private ActionListener actionListener;
	
		internal bool firstTick, isComplete, isInit;

        internal Event original;
	
		internal object tag;
	
		internal float offsetX, offsetY;
	
		public ActionEvent() {
			timer = new LTimer(0);
		}
	
		public long GetDelay() {
			return timer.GetDelay();
		}
	
		public void SetDelay(long d) {
			timer.SetDelay(d);
		}
	
		public void Paused(bool pause) {
			ActionControl.GetInstance().Paused(pause, original);
		}
	
		public void Step(long elapsedTime) {
			if (original == null) {
				return;
			}
			if (timer.Action(elapsedTime)) {
				if (firstTick) {
					this.firstTick = false;
					this.timer.Refresh();
				} else {
					Update(elapsedTime);
				}
				if (actionListener != null) {
					actionListener.Process(original);
				}
			}
		}
	
		public object GetOriginal() {
			return original;
		}

        public virtual void Start(Event o)
        {
			if (o == null) {
				return;
			}
			this.original = o;
			this.timer.Refresh();
			this.firstTick = true;
			this.isComplete = false;
			this.isInit = false;
			if (actionListener != null) {
				actionListener.Start(o);
			}
		}
	
		public abstract void Update(long elapsedTime);
	
		public abstract void OnLoad();
	
		public void Stop() {
			if (actionListener != null) {
				actionListener.Stop(original);
			}
		}
	
		public abstract bool IsComplete();
	
		public object GetTag() {
			return tag;
		}
	
		public void SetTag(object tag_0) {
			this.tag = tag_0;
		}
	
		public void SetComplete(bool isComplete_0) {
			this.isComplete = isComplete_0;
		}
	
		public ActionListener GetActionListener() {
			return actionListener;
		}
	
		public void SetActionListener(ActionListener actionListener_0) {
			this.actionListener = actionListener_0;
		}
	
		public void SetOffset(float x, float y) {
			this.offsetX = x;
			this.offsetY = y;
		}
	
		public float GetOffsetX() {
			return offsetX;
		}
	
		public void SetOffsetX(float offsetX_0) {
			this.offsetX = offsetX_0;
		}
	
		public float GetOffsetY() {
			return offsetY;
		}
	
		public void SetOffsetY(float offsetY_0) {
			this.offsetY = offsetY_0;
		}
	}
}
