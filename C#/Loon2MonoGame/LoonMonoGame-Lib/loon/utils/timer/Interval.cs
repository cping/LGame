using loon.events;

namespace loon.utils.timer
{
    public abstract class Interval : ActionUpdate, LRelease
    {

        protected internal readonly LTimer _loop_timer;

        public Interval()
        {
            this._loop_timer = new LTimer(0L);
        }

        public Interval(long delay)
        {
            this._loop_timer = new LTimer(delay);
        }

        public Interval(Duration d)
        {
            this._loop_timer = new LTimer(d);
        }

        public Interval(string name, long delay)
        {
            this._loop_timer = new LTimer(name, delay);
        }

        public Interval(long delay, int loopCount)
        {
            this._loop_timer = new LTimer(delay, loopCount);
        }

        public Interval(string name, long delay, int loopCount)
        {
            this._loop_timer = new LTimer(name, delay, loopCount);
        }

        public Interval(string name, Duration d)
        {
            this._loop_timer = new LTimer(name, d);
        }

        public virtual Interval Start()
        {
            _loop_timer.Start();
            _loop_timer.SetUpdateable(this);
            _loop_timer.Submit();
            return this;
        }

        public virtual Interval Stop()
        {
            _loop_timer.Stop();
            _loop_timer.Kill();
            return this;
        }

        public virtual Interval Pause()
        {
            _loop_timer.Pause();
            return this;
        }

        public virtual Interval Unpause()
        {
            _loop_timer.Unpause();
            return this;
        }

        public virtual Interval SetDelay(long d)
        {
            _loop_timer.SetDelay(d);
            return this;
        }

        public virtual long GetDelay()
        {

            return _loop_timer.GetDelay();

        }

        public virtual string GetName()
        {

            return _loop_timer.GetName();

        }

        public virtual bool IsActive()
        {
            return _loop_timer.IsActive();
        }

        public virtual bool IsClosed()
        {

            return _loop_timer.IsClosed();

        }

        public virtual bool Completed()
        {
            return _loop_timer.IsCompleted();
        }

        public virtual LTimer CurrentTimer()
        {
            return _loop_timer;
        }

        public virtual void Action(object o)
        {
            Loop();
        }

        public abstract void Loop();

        public virtual void Close()
        {
            _loop_timer.Close();
        }

    }

}
