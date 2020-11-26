using loon.utils.timer;
using System;

namespace loon.utils.processes
{
    public class RealtimeProcessManager : RealtimeProcessEvent, IArray, LRelease
    {

        private static RealtimeProcessManager instance;

        private SortedList<GameProcess> processes;

        public static void FreeStatic()
        {
            instance = null;
        }

        public static RealtimeProcessManager Get()
        {
            if (instance == null)
            {
                lock (typeof(RealtimeProcessManager))
                {
                    if (instance == null)
                    {
                        instance = new RealtimeProcessManager();
                    }
                }
            }
            return instance;
        }

        private RealtimeProcessManager()
        {
            this.processes = new SortedList<GameProcess>();
        }

        public static RealtimeProcessManager NewProcess()
        {
            return new RealtimeProcessManager();
        }

        public virtual void AddProcess(GameProcess realtimeProcess)
        {
            lock (this.processes)
            {
                this.processes.Add(realtimeProcess);
            }
        }

        public virtual bool ContainsProcess(GameProcess realtimeProcess)
        {
            lock (this.processes)
            {
                return this.processes.Contains(realtimeProcess);
            }
        }

        public virtual void Tick(LTimerContext time)
        {
            if (processes.size > 0)
            {
                SortedList<GameProcess> toBeUpdated;
                lock (this.processes)
                {
                    toBeUpdated = new SortedList<GameProcess>(this.processes);
                }
                SortedList<GameProcess> deadProcesses = new SortedList<GameProcess>();
                try
                {
                    for (LIterator<GameProcess> it = toBeUpdated.ListIterator(); it.HasNext();)
                    {
                        GameProcess realtimeProcess = it.Next();
                        if (realtimeProcess != null)
                        {
                            lock (realtimeProcess)
                            {
                                realtimeProcess.Tick(time);
                                if (realtimeProcess.IsDead())
                                {
                                    deadProcesses.Add(realtimeProcess);
                                }
                            }
                        }
                    }
                    if (deadProcesses.size > 0)
                    {
                        for (LIterator<GameProcess> it = deadProcesses.ListIterator(); it.HasNext();)
                        {
                            GameProcess realtimeProcess = it.Next();
                            if (realtimeProcess != null)
                            {
                                lock (realtimeProcess)
                                {
                                    realtimeProcess.Finish();
                                }
                            }
                        }
                        lock (this.processes)
                        {
                            this.processes.RemoveAll(deadProcesses);
                        }
                    }
                }
                catch (System.Exception cause)
                {
                    //LSystem.Error("Process dispatch failure", cause);
                }
            }
        }

        public virtual TArray<GameProcess> Find(string id)
        {
            TArray<GameProcess> list = new TArray<GameProcess>();
            if (processes != null && processes.size > 0)
            {
                lock (this.processes)
                {
                    for (LIterator<GameProcess> it = processes.ListIterator(); it.HasNext();)
                    {
                        GameProcess p = it.Next();
                        if (p != null && (string.ReferenceEquals(p.GetId(), id) || p.GetId().Equals(id)))
                        {
                            list.Add(p);
                        }
                    }
                }
            }
            return list;
        }

        public virtual TArray<GameProcess> Find(GameProcessType pt)
        {
            TArray<GameProcess> list = new TArray<GameProcess>();
            if (processes != null && processes.size > 0)
            {
                lock (this.processes)
                {
                    for (LIterator<GameProcess> it = processes.ListIterator(); it.HasNext();)
                    {
                        GameProcess p = it.Next();
                        if (p != null && p.GetProcessType() == pt)
                        {
                            list.Add(p);
                        }
                    }
                }
            }
            return list;
        }

        public virtual TArray<GameProcess> Delete(GameProcessType pt)
        {
            TArray<GameProcess> list = new TArray<GameProcess>();
            if ((object)pt == null)
            {
                return list;
            }
            if (processes != null && processes.size > 0)
            {
                lock (this.processes)
                {
                    TArray<GameProcess> ps = new TArray<GameProcess>(processes);
                    for (int i = 0; i < ps.size; i++)
                    {
                        GameProcess p = ps.Get(i);
                        if (p != null)
                        {
                            if (p.GetProcessType() == pt)
                            {
                                p.Kill();
                                processes.Remove(p);
                                list.Add(p);
                            }
                        }
                    }
                }
            }
            return list;
        }

        public virtual TArray<GameProcess> Delete(GameProcess process)
        {
            TArray<GameProcess> list = new TArray<GameProcess>();
            if (process == null)
            {
                return list;
            }
            if (processes != null && processes.size > 0)
            {
                lock (this.processes)
                {
                    TArray<GameProcess> ps = new TArray<GameProcess>(processes);
                    for (int i = 0; i < ps.size; i++)
                    {
                        GameProcess p = ps.Get(i);
                        if (p != null)
                        {
                            if (process == p || string.ReferenceEquals(process.GetId(), p.GetId()) || process.GetId().Equals(p.GetId()))
                            {
                                p.Kill();
                                processes.Remove(p);
                                list.Add(p);
                            }
                        }
                    }
                }
            }
            return list;
        }

        public virtual TArray<GameProcess> Delete(string id)
        {
            TArray<GameProcess> list = new TArray<GameProcess>();
            if (processes != null && processes.size > 0)
            {
                lock (this.processes)
                {
                    TArray<GameProcess> ps = new TArray<GameProcess>(processes);
                    for (int i = 0; i < ps.size; i++)
                    {
                        GameProcess p = ps.Get(i);
                        if (p != null)
                        {
                            if (string.ReferenceEquals(p.GetId(), id) || p.GetId().Equals(id))
                            {
                                p.Kill();
                                processes.Remove(p);
                                list.Add(p);
                            }
                        }
                    }
                }
            }
            return list;
        }

        public virtual TArray<GameProcess> DeleteIndex(string id)
        {
            TArray<GameProcess> list = new TArray<GameProcess>();
            if (processes != null && processes.size > 0)
            {
                lock (this.processes)
                {
                    TArray<GameProcess> ps = new TArray<GameProcess>(processes);
                    for (int i = 0; i < ps.size; i++)
                    {
                        GameProcess p = ps.Get(i);
                        if (p != null)
                        {
                            if (string.ReferenceEquals(p.GetId(), id) || p.GetId().IndexOf(id, StringComparison.Ordinal) != -1)
                            {
                                p.Kill();
                                processes.Remove(p);
                                list.Add(p);
                            }
                        }
                    }
                }
            }
            return list;
        }

        public virtual bool HasEvents()
        {
            return !IsEmpty();
        }

        public virtual int Size()
        {
            return processes.size;
        }

        public virtual void Clear()
        {
            processes.Clear();
        }

        public virtual bool IsEmpty()
        {

            return processes.size == 0;

        }

        public virtual void Dispose()
        {
            Close();
        }

        public virtual void Close()
        {
            if (processes != null && processes.size > 0)
            {
                lock (this.processes)
                {
                    TArray<GameProcess> ps = new TArray<GameProcess>(processes);
                    for (int i = 0; i < ps.size; i++)
                    {
                        GameProcess p = ps.Get(i);
                        if (p != null)
                        {
                            lock (p)
                            {
                                p.Finish();
                            }
                        }
                    }
                    processes.Clear();
                }
            }
        }

    }
}
