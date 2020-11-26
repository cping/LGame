using loon.utils.timer;

namespace loon.events
{

    public class TimerEvent : Updateable
    {

        private readonly long elapsedEvent;

        private object target;

        private LTimer timer;

        public TimerEvent(long elapsedTime)
        {
            this.elapsedEvent = elapsedTime;
        }

        public long GetElapsedEvent()
        {
            return elapsedEvent;
        }

        public object GetTarget()
        {
            return target;
        }

        public LTimer GetTimer()
        {
            return timer;
        }

        public void Action(object tag)
        {
            if (tag != null && tag is LTimer t)
            {
                timer = t;
            }
            target = tag;
        }

    }
}
