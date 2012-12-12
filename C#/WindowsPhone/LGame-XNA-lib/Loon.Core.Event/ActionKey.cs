namespace Loon.Core.Event {
	
	public class ActionKey {
	
		public const int NORMAL = 0;
	
		public const int DETECT_INITIAL_PRESS_ONLY = 1;
	
		private const int STATE_RELEASED = 0;
	
		private const int STATE_PRESSED = 1;
	
		private const int STATE_WAITING_FOR_RELEASE = 2;
	
		private int mode;
	
		private int amount;
	
		private int state;
	
		public bool isReturn;
	
		public ActionKey():this(NORMAL) {
			
		}
	
		public ActionKey(int m) {
			this.mode = m;
			Reset();
		}
	
		public virtual void Act(long elapsedTime) {
	
		}

        public virtual void Reset()
        {
			state = STATE_RELEASED;
			amount = 0;
		}

        public virtual void Press()
        {
			if (state != STATE_WAITING_FOR_RELEASE) {
				amount++;
				state = STATE_PRESSED;
			}
		}

        public virtual void Release()
        {
			state = STATE_RELEASED;
		}

        public virtual bool IsPressed()
        {
			if (amount != 0) {
				if (state == STATE_RELEASED) {
					amount = 0;
				} else if (mode == DETECT_INITIAL_PRESS_ONLY) {
					state = STATE_WAITING_FOR_RELEASE;
					amount = 0;
				}
				return true;
			}
			return false;
		}
	}
}
