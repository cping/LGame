using System;
using System.Runtime.CompilerServices;

namespace Loon.Core.Timer
{
    public class LTimerContext
    {
        public long timeSinceLastUpdate, millisSleepTime;

        public LTimerContext()
        {
            timeSinceLastUpdate = 0;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void SetTimeSinceLastUpdate(long timeSinceLastUpdate)
        {
            this.timeSinceLastUpdate = timeSinceLastUpdate;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public long GetTimeSinceLastUpdate()
        {
            return timeSinceLastUpdate;
        }

        public float GetMilliseconds()
        {
            return timeSinceLastUpdate / 1000f;
        }

        public long GetSleepTimeMicros()
        {
            return millisSleepTime * 1000;
        }
    }
}
