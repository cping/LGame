using Loon.Core.Timer;
namespace Loon.Action {
	
	public abstract class ActionEvent {
	
		private LTimer timer;
	
		private ActionListener actionListener;
	
		internal bool firstTick, isComplete, isInit;
	
		internal ActionBind original;
	
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
	
		public virtual void Start(ActionBind o) {
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
	
		public void SetTag(object t) {
			this.tag = t;
		}
	
		public void SetComplete(bool c) {
			this.isComplete = c;
		}
	
		public ActionListener GetActionListener() {
			return actionListener;
		}
	
		public void SetActionListener(ActionListener a) {
			this.actionListener = a;
		}
	
		public void SetOffset(float x, float y) {
			this.offsetX = x;
			this.offsetY = y;
		}
	
		public float GetOffsetX() {
			return offsetX;
		}
	
		public void SetOffsetX(float o) {
			this.offsetX = o;
		}
	
		public float GetOffsetY() {
			return offsetY;
		}
	
		public void SetOffsetY(float o) {
			this.offsetY = o;
		}
	}
}
