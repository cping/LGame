using loon.events;

namespace loon.utils.timer
{
    public class StopwatchTimer
    {


        private string _currentName;

        private long _from;

        private long _to;

        private long _lastStop;

        private long _target;

        public StopwatchTimer() : this("")
        {

        }

        public StopwatchTimer(string name) : this(name, 0)
        {

        }

        public StopwatchTimer(long target) : this("", target)
        {

        }

        public StopwatchTimer(string name, long target)
        {
            this._currentName = name;
            this._target = target;
            this.Reset();
        }

        public static StopwatchTimer Begin()
        {
            StopwatchTimer sw = new StopwatchTimer();
            sw.Start();
            return sw;
        }

        public static StopwatchTimer Make()
        {
            return new StopwatchTimer();
        }

        public static StopwatchTimer Run(Updateable u)
        {
            StopwatchTimer sw = Begin();
            u.Action(null);
            sw.Stop();
            return sw;
        }

        public bool IsDoneAndReset()
        {
            if (IsDone())
            {
                Reset();
                return true;
            }
            return false;
        }

        public bool IsDone()
        {
            return (CurrentTime() - _from) >= _target;
        }

        public bool IsPassedTime(long interval)
        {
            return CurrentTime() - _from >= interval;
        }

        public StopwatchTimer Reset()
        {
            Start();
            return this;
        }

        public long Start()
        {
            this._from = CurrentTime();
            this._to = this._from;
            this._lastStop = this._to;
            return this._from;
        }

        private long CurrentTime()
        {
            return TimeUtils.Millis();
        }

        public StopwatchTimer End()
        {
            return Stop();
        }

        public StopwatchTimer Stop()
        {
            this._lastStop = this._to;
            this._to = CurrentTime();
            return this;
        }

        public long GetDuration()
        {
            return this._to - this._from;
        }

        public long GetLastDuration()
        {
            return this._to - this._lastStop;
        }

        public long GetStartTime()
        {
            return this._from;
        }

        public long GetEndTime()
        {
            return this._to;
        }

        public StopwatchTimer SetName(string n)
        {
            this._currentName = n;
            return this;
        }

        public string getName()
        {
            return this._currentName;
        }

        public override string ToString()
        {
            StringKeyValue builder = new StringKeyValue("StopwatchTimer");
            builder.Kv("name", _currentName).Comma().Kv("from", _from).Comma().Kv("to", _to).Comma()
                    .Kv("lastStop", _lastStop).Comma().Kv("target", _target);
            return builder.ToString();
        }

    }
}
