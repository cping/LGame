using loon.utils.timer;

namespace loon.utils.processes
{

    public abstract class RealtimeProcess : GameProcess, LRelease
    {

        private static int GLOBAL_ID = 0;

        protected internal bool isDead;

        protected internal readonly string id;

        private readonly LTimer timer = new LTimer(LSystem.SECOND);

        private GameProcessType processType = GameProcessType.Other;

        private RealtimeProcessHost processHost;

        private SortedList<GameProcess> processesToFireWhenFinished;

        private static string ProcessName
        {
            get
            {
                return "Process" + (GLOBAL_ID++);
            }
        }

        public RealtimeProcess() : this(ProcessName)
        {
        }

        public RealtimeProcess(string id) : this(id, LSystem.SECOND)
        {
        }

        public RealtimeProcess(long delay) : this(ProcessName, delay)
        {
        }

        public RealtimeProcess(string id, long delay) : this(id, delay, GameProcessType.Other)
        {
        }

        public RealtimeProcess(string id, long delay, GameProcessType pt)
        {
            this.isDead = false;
            this.id = id;
            this.timer.SetDelay(delay);
            this.processType = pt;
        }

        public virtual void SetProcessHost(RealtimeProcessHost processHost)
        {
            this.processHost = processHost;
        }

        public virtual void FireThisWhenFinished(GameProcess realtimeProcess)
        {
            if (this.processesToFireWhenFinished == null)
            {
                this.processesToFireWhenFinished = new SortedList<GameProcess>();
            }
            this.processesToFireWhenFinished.Add(realtimeProcess);
        }

        public virtual void Tick(LTimerContext time)
        {
            if (timer.Action(time))
            {
                Run(time);
            }
        }

        public virtual RealtimeProcess Sleep(long delay)
        {
            timer.SetDelay(delay);
            return this;
        }

        public virtual RealtimeProcess SetDelay(long delay)
        {
            timer.SetDelay(delay);
            return this;
        }

        public virtual long GetDelay()
        {

            return timer.GetDelay();

        }

        public virtual long GetCurrentTick()
        {
            return timer.GetCurrentTick();

        }

        public virtual RealtimeProcess Interrupt()
        {
            timer.Stop();
            return this;
        }

        public virtual RealtimeProcess Stop()
        {
            timer.Stop();
            return this;
        }

        public virtual RealtimeProcess Start()
        {
            timer.Start();
            return this;
        }

        public virtual bool IsActive()
        {
            return timer.IsActive();
        }

        public virtual GameProcessType GetProcessType()
        {
            return this.processType;
        }


        public virtual void SetProcessType(GameProcessType pt)
        {
            this.processType = pt;
        }

        public abstract void Run(LTimerContext time);

        public virtual void Kill()
        {
            this.isDead = true;
        }

        public virtual bool IsDead()
        {

            return this.isDead;

        }

        public virtual string GetId()
        {

            return this.id;

        }

        public virtual void Finish()
        {
            if (!this.isDead)
            {
                Kill();
            }
            if (this.processesToFireWhenFinished != null)
            {
                for (LIterator<GameProcess> it = this.processesToFireWhenFinished.ListIterator(); it.HasNext();)
                {
                    RealtimeProcessManager.Get().AddProcess(it.Next());
                }
            }
            if (this.processHost != null)
            {
                this.processHost.ProcessFinished(this.id, this);
            }
        }

        public virtual void Close()
        {
            Finish();
        }
    }
}
