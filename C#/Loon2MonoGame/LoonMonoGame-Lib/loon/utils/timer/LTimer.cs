using loon.events;
using loon.utils.processes;

namespace loon.utils.timer
{
    public class LTimer : LRelease
    {

        private class TimerProcess : RealtimeProcess
        {

            internal LTimer timer = null;

            public TimerProcess(LTimer t)
            {
                this.timer = t;
                this.SetProcessType(GameProcessType.Time);
            }

            public override void Run(LTimerContext time)
            {
                if (timer != null)
                {
                    timer.Action(time);
                    if (timer.IsClosed() || timer.IsCompleted())
                    {
                        Kill();
                    }
                }
            }

        }

        private static LTimer _instance = null;

        public static void FreeStatic()
        {
            _instance = null;
        }

        public static LTimer GetInstance()
        {

            if (_instance == null)
            {
                lock (typeof(LTimer))
                {
                    if (_instance == null)
                    {
                        _instance = new LTimer("STATIC_TIME", 0);
                    }
                }
            }
            return _instance;

        }

        public static LTimer Get()
        {
            return GetInstance();
        }

        public static LTimer At()
        {
            return new LTimer();
        }

        public static LTimer At(long d)
        {
            return new LTimer(d);
        }

        public static LTimer At(Duration d)
        {
            return new LTimer(d);
        }

        private static int GLOBAL_ID = 0;

        private TimerProcess _process = null;
        private readonly int _idx;
        private int _maxNumberOfRepeats = -1;
        private int _numberOfTicks = 0;

        private bool _repeats = true;
        private bool _completed = false;
        private bool _closed = false;

        private float _speedFactor = 1f;

        private long _delay = 0;
        private long _currentTick = 0;
        private bool _active = true;

        private Updateable _update;

        private readonly string _name;

        public LTimer() : this(450)
        {
        }

        public LTimer(string name) : this(name, 450)
        {
        }

        public LTimer(string name, Duration d) : this(name, d == null ? 0 : d.ToMillisLong(), 1f)
        {
        }

        public LTimer(Duration d) : this(d == null ? 0 : d.ToMillisLong())
        {
        }

        public LTimer(string name, long delay) : this(name, delay, 1f)
        {
        }

        public LTimer(long delay) : this(delay, 1f)
        {
        }

        public LTimer(string name, long delay, float factor) : this(name, delay, -1, factor, true)
        {
        }

        public LTimer(long delay, float factor) : this(delay, -1, factor, true)
        {
        }

        public LTimer(long delay, int numberOfRepeats) : this(LSystem.UNKNOWN, delay, numberOfRepeats)
        {
        }

        public LTimer(string name, long delay, int numberOfRepeats) : this(name, delay, numberOfRepeats, 1f, true)
        {
        }

        public LTimer(string name, long delay, float factor, bool repeats) : this(name, delay, -1, factor, repeats)
        {
        }

        public LTimer(long delay, int numberOfRepeats, float factor, bool repeats) : this(LSystem.UNKNOWN, delay, numberOfRepeats, factor, repeats)
        {
        }

        public LTimer(string name, long delay, int numberOfRepeats, float factor, bool repeats)
        {
            this._idx = GLOBAL_ID++;
            this._name = name;
            this._closed = false;
            this.Reset(delay, numberOfRepeats, factor, repeats);
        }

        public virtual bool Action(LTimerContext context)
        {
            return Action(context.timeSinceLastUpdate);
        }

        public virtual bool Action(float delta)
        {
            return Action((long)(MathUtils.Max(delta * 1000, 8)));
        }

        public virtual bool Action(long elapsedTime)
        {
            if (this._closed)
            {
                return false;
            }
            if (this._active)
            {
                this._currentTick += (long)(elapsedTime * _speedFactor);
                if (this._maxNumberOfRepeats > -1 && this._numberOfTicks >= this._maxNumberOfRepeats)
                {
                    this._completed = true;
                }
                if (!this._completed && this._currentTick >= this._delay)
                {
                    if (this._update != null)
                    {
                        this._update.Action(this);
                    }
                    if (this._repeats)
                    {
                        this._numberOfTicks++;
                        this._currentTick = 0;
                    }
                    else
                    {
                        this._completed = true;
                    }
                    return true;
                }
            }
            return false;
        }

        public virtual TimerEvent MakeEvent()
        {
            return new TimerEvent(_currentTick);
        }

        public virtual LTimer Refresh()
        {
            return Reset();
        }

        public virtual LTimer Reset()
        {
            return this.Reset(this._delay, this._maxNumberOfRepeats, this._speedFactor, this._repeats);
        }

        public virtual LTimer Reset(long newDelay, int newNumberOfRepeats, float newFactor, bool newRepeats)
        {
            this._delay = MathUtils.Max(newDelay, 0);
            this._maxNumberOfRepeats = MathUtils.Max(newNumberOfRepeats, -1);
            this._speedFactor = MathUtils.Max(newFactor, LSystem.MIN_SECONE_SPEED_FIXED);
            this._repeats = newRepeats;
            this._active = true;
            this._completed = false;
            this._currentTick = 0;
            this._numberOfTicks = 0;
            this._speedFactor = 1f;
            return this;
        }

        public virtual LTimer SetEquals(LTimer other)
        {
            this._delay = MathUtils.Max(other._delay, 0);
            this._maxNumberOfRepeats = MathUtils.Max(other._maxNumberOfRepeats, -1);
            this._speedFactor = MathUtils.Max(other._speedFactor, LSystem.MIN_SECONE_SPEED_FIXED);
            this._repeats = other._repeats;
            this._active = other._active;
            this._completed = other._completed;
            this._currentTick = other._currentTick;
            this._numberOfTicks = other._numberOfTicks;
            this._speedFactor = other._speedFactor;
            return this;
        }

        public virtual LTimer AddPercentage(long elapsedTime)
        {
            this._currentTick += elapsedTime;
            return this;
        }

        public virtual LTimer AddPercentage(LTimerContext context)
        {
            this._currentTick += context.timeSinceLastUpdate;
            return this;
        }

        public virtual int GetTimesRepeated()
        {

            return this._numberOfTicks;

        }

        public virtual long GetDelay()
        {
            return this._delay;
        }

        public virtual LTimer SetDelay(Duration d)
        {
            return SetDelay(d == null ? 0 : d.ToMillisLong());
        }

        public virtual LTimer SetDelay(long delay)
        {
            return Reset(delay, this._maxNumberOfRepeats, this._speedFactor, this._repeats);
        }

        public virtual LTimer SetRepeats(int amount)
        {
            return SetRepeats(amount, true);
        }

        public virtual LTimer SetRepeats(int amount, bool newRepats)
        {
            return this.Reset(this._delay, amount, this._speedFactor, newRepats);
        }

        public virtual LTimer SetActive(bool active)
        {
            this.Reset();
            this._active = active;
            return this;
        }

        public virtual bool IsActive()
        {

            return this._active;

        }

        public virtual LTimer Start()
        {
            this._active = true;
            this.SetCompleted(false);
            return this;
        }

        public virtual LTimer Stop()
        {
            this._active = false;
            this.SetCompleted(true);
            return this;
        }

        public virtual LTimer Pause()
        {
            this._active = false;
            return this;
        }

        public virtual LTimer Unpause()
        {
            this._active = true;
            return this;
        }

        public virtual bool Paused()
        {
            return !IsActive();
        }

        public virtual int GetId()
        {

            return _idx;

        }

        public virtual long GetCurrentTick()
        {

            return this._currentTick;

        }

        public virtual LTimer SetCurrentTick(long tick)
        {
            this._currentTick = tick;
            return this;
        }

        public virtual float GetPercentage()
        {

            return (float)this._currentTick / (float)this._delay;

        }

        public virtual float GetOverallPercentage()
        {

            if (this._numberOfTicks > 0)
            {
                float totalDuration = this._delay + (this._delay * this._numberOfTicks);
                float totalElapsed = this._currentTick + (this._delay * (this._numberOfTicks - this._maxNumberOfRepeats));
                return (totalElapsed / totalDuration);
            }
            else
            {
                return this.GetPercentage();
            }

        }

        public virtual float GetElapsedSeconds()
        {

            return this._currentTick * 0.001f;

        }

        public virtual float GetRemaining()
        {

            return (float)(this._delay - this._currentTick);

        }

        public virtual bool CheckInterval(long interval)
        {
            return (_currentTick / interval) > ((_currentTick - _delay) / interval);
        }

        public virtual LTimer Clamp()
        {
            if (this._currentTick > this._delay)
            {
                _currentTick = _delay;
            }
            return this;
        }

        public virtual float GetSpeedFactor()
        {

            return _speedFactor;

        }

        public virtual LTimer SetSpeedFactor(float factor)
        {
            this._speedFactor = factor;
            return this;
        }

        public virtual bool IsCompleted()
        {

            return _completed;

        }

        public virtual LTimer SetCompleted(bool completed)
        {
            this._completed = completed;
            return this;
        }

        public virtual Updateable GetUpdateable()
        {

            return _update;

        }

        public virtual LTimer SetUpdateable(Updateable u)
        {
            this._update = u;
            return this;
        }

        public virtual TimerEvent MakeTimeEvent()
        {
            return new TimerEvent(this._currentTick);
        }

        public virtual LTimer Submit()
        {
            lock (typeof(RealtimeProcessManager))
            {
                if (_process != null)
                {
                    RealtimeProcessManager.Get().Delete(_process);
                }
                if (_process == null || _process.IsDead())
                {
                    _process = new TimerProcess(this);
                }
                _process.SetDelay(0);
                RealtimeProcessManager.Get().AddProcess(_process);
            }
            return this;
        }

        public virtual LTimer Kill()
        {
            if (_process != null)
            {
                _process.Kill();
            }
            return this;
        }

        public virtual string GetName()
        {
            return _name;
        }

        public virtual bool IsClosed()
        {

            return this._closed;

        }

        public override string ToString()
        {
            StringKeyValue builder = new StringKeyValue("LTimer");
            builder.Kv("name", _name).Comma().Kv("currentTick", _currentTick).Comma().Kv("delay", _delay).Comma().Kv("factor", _speedFactor).Comma().Kv("active", _active).Comma().Kv("repeats", _repeats).Comma().Kv("maxNumberOfRepeats", _maxNumberOfRepeats).Comma().Kv("numberOfTicks", _numberOfTicks).Comma().Kv("completed", _completed);
            return builder.ToString();
        }

        public virtual void Close()
        {
            Stop();
            this._closed = true;
            if (_process != null)
            {
                _process.Close();
                _process = null;
            }
        }

    }
}
