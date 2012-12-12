namespace Loon.Core.Timer
{
    public class LTimer
    {

        private bool active = true;

        private long delay;

        private long currentTick;

        public LTimer()
            : this(450)
        {

        }

        public LTimer(long delay)
        {
            this.delay = delay;
        }

        public bool Action(long elapsedTime)
        {
            if (this.active)
            {
                this.currentTick += elapsedTime;
                if (this.currentTick >= this.delay)
                {
                    this.currentTick -= this.delay;
                    return true;
                }
            }

            return false;
        }

        public bool Action(LTimerContext context)
        {
            if (this.active)
            {
                this.currentTick += context.GetTimeSinceLastUpdate();
                if (this.currentTick >= this.delay)
                {
                    this.currentTick -= this.delay;
                    return true;
                }
            }
            return false;
        }

        public void Refresh()
        {
            this.currentTick = 0;
        }

        public void SetEquals(LTimer other)
        {
            this.active = other.active;
            this.delay = other.delay;
            this.currentTick = other.currentTick;
        }

        public bool IsActive()
        {
            return this.active;
        }

        public void Start()
        {
            this.active = true;
        }

        public void Stop()
        {
            this.active = false;
        }

        public void SetActive(bool b)
        {
            this.active = b;
            this.Refresh();
        }

        public long GetDelay()
        {
            return this.delay;
        }

        public void SetDelay(long delay)
        {
            this.delay = delay;
            this.Refresh();
        }

        public long GetCurrentTick()
        {
            return this.currentTick;
        }

        public void SetCurrentTick(long tick)
        {
            this.currentTick = tick;
        }
    }
}
