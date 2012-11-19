namespace Loon.Action {
	
	public class ActionControl {
	
		private static ActionControl instanceAction;
	
		private Actions actions;
	
		private bool pause;
	
		public static ActionControl GetInstance() {
			if (instanceAction != null) {
				return instanceAction;
			}
			 lock (typeof(ActionControl)) {
						if (instanceAction == null) {
							instanceAction = new ActionControl();
						}
						return instanceAction;
					}
		}
	
		private void Call(long elapsedTime) {
			if (pause || actions.GetCount() == 0) {
				return;
			}
			actions.Update(elapsedTime);
		}
	
		public static void Update(long elapsedTime) {
			if (instanceAction != null) {
				instanceAction.Call(elapsedTime);
			}
		}
	
		private ActionControl() {
			actions = new Actions();
		}

        public void AddAction(ActionEvent action, Event obj, bool paused)
        {
			actions.AddAction(action, obj, paused);
		}

        public void AddAction(ActionEvent action, Event obj)
        {
			AddAction(action, obj, false);
		}

        public void RemoveAllActions(Event actObject)
        {
			actions.RemoveAllActions(actObject);
		}
	
		public int GetCount() {
			return actions.GetCount();
		}

        public void RemoveAction(object tag, Event actObject)
        {
			actions.RemoveAction(tag, actObject);
		}
	
		public void RemoveAction(ActionEvent action) {
			actions.RemoveAction(action);
		}

        public ActionEvent GetAction(object tag, Event actObject)
        {
			return actions.GetAction(tag, actObject);
		}

        public void Stop(Event actObject)
        {
			actions.Stop(actObject);
		}

        public void Start(Event actObject)
        {
			actions.Start(actObject);
		}

        public void Paused(bool pause_0, Event actObject)
        {
			actions.Paused(pause_0, actObject);
		}
	
		public bool IsPause() {
			return pause;
		}
	
		public void SetPause(bool p) {
			this.pause = p;
		}
	
		public void Clear() {
			actions.Clear();
		}
	
		public void StopAll() {
			Clear();
			Stop();
		}
	
		public void Stop() {
			pause = true;
		}
	
	}
}
