using System;
using System.Threading;

namespace Loon.Core.Timer
{
    public class SystemTimer
    {

        private long lastTime = 0;

        private long virtualTime = 0;

        public SystemTimer()
        {
            Start();
        }

        public virtual void Start()
        {
            lastTime = Environment.TickCount;
            virtualTime = 0;
        }

        public virtual long SleepTimeMicros(long goalTimeMicros)
        {
            long time = goalTimeMicros - GetTimeMicros();
            if (time > 100)
            {
                try
                {
                    Thread.Sleep((int)((time + 100) >> 10));
                }
                catch
                {
                }
            }
            return GetTimeMicros();
        }

        public static long sleepTimeMicros(long goalTimeMicros, SystemTimer timer)
        {
            long time = goalTimeMicros - timer.GetTimeMicros();
            if (time > 100)
            {
                try
                {
                    Thread.Sleep((int)((time + 100) >> 10));
                }
                catch
                {
                }
            }
            return timer.GetTimeMicros();
        }

        public virtual long GetTimeMillis()
        {

            long time = Environment.TickCount;
            if (time > lastTime)
            {
                virtualTime += time - lastTime;
            }
            lastTime = time;

            return virtualTime;
        }

        public virtual long GetTimeMicros()
        {
            return GetTimeMillis() << 10;
        }

        public virtual void Stop()
        {

        }
    }

}

