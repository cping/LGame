namespace loon.events
{
    public class ActionKey
    {

        private Updateable _function;

        public const int NORMAL = 0;

        public const int DETECT_INITIAL_PRESS_ONLY = 1;

        private const int STATE_RELEASED = 0;

        private const int STATE_PRESSED = 1;

        private const int STATE_WAITING_FOR_RELEASE = 2;

        private readonly int mode;

        private int amount;

        private int state;

        public long elapsedTime;

        public bool isReturn;

        public ActionKey(): this(NORMAL)
        {
            
        }

        public ActionKey(int mode)
        {
            this.mode = mode;
            Reset();
        }

        public void Act()
        {
            Act(0);
        }

        public void Act(long elapsed)
        {
            this.elapsedTime = elapsed;
            if (_function != null)
            {
                _function.Action(this);
            }
        }

        public void Reset()
        {
            state = STATE_RELEASED;
            amount = 0;
        }

        public void Press()
        {
            if (state != STATE_WAITING_FOR_RELEASE)
            {
                amount++;
                state = STATE_PRESSED;
            }
        }

        public void Release()
        {
            state = STATE_RELEASED;
        }

        public bool IsPressed()
        {
            if (amount != 0)
            {
                if (state == STATE_RELEASED)
                {
                    amount = 0;
                }
                else if (mode == DETECT_INITIAL_PRESS_ONLY)
                {
                    state = STATE_WAITING_FOR_RELEASE;
                    amount = 0;
                }
                return true;
            }
            return false;
        }

        public Updateable GetFunction()
        {
            return _function;
        }

        public void SetFunction(Updateable function)
        {
            this._function = function;
        }
    }
}
