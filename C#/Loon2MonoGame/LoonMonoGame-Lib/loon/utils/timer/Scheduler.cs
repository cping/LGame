using loon.utils.processes;

namespace loon.utils.timer
{
	public class Scheduler : LRelease
	{

		private class SchedulerProcess : RealtimeProcess
		{

			internal Scheduler sched = null;

			public SchedulerProcess(Scheduler s)
			{
				this.sched = s;
				this.SetProcessType(GameProcessType.Time);
			}

			public override void Run(LTimerContext time)
			{
				if (sched != null)
				{
					sched.Update(time);
					if (sched.Completed())
					{
						Kill();
					}
				}
			}

		}

		private readonly LTimer _loop_timer;

		private SchedulerProcess _processScheduler;

		private TArray<Interval> _scheduled = new TArray<Interval>(32);

		private int _childIndex = 0;

		private bool _removeSequenceTask = false;

		private bool _forceWaitSequence = false;

		private bool _closed = false;

		public Scheduler() : this(0L)
		{
		}

		public Scheduler(long delay) : this(LSystem.UNKNOWN, delay)
		{
		}

		public Scheduler(bool removeTask) : this(LSystem.UNKNOWN, removeTask)
		{
		}

		public Scheduler(string name, bool removeTask) : this(name, 0, removeTask, true)
		{
		}

		public Scheduler(string name, long delay) : this(name, delay, false)
		{
		}

		public Scheduler(bool removeTask, bool sequence) : this(LSystem.UNKNOWN, 0L, removeTask, sequence)
		{
		}

		public Scheduler(string name, bool removeTask, bool sequence) : this(name, 0L, removeTask, sequence)
		{
		}

		public Scheduler(string name, long delay, bool removeTask) : this(name, delay, removeTask, true)
		{
		}

		/// <summary>
		/// Scheduler事务管理器
		/// </summary>
		/// <param name="name">
		///            事务调度管理器名称 </param>
		/// <param name="delay">
		///            延迟时间(默认0) </param>
		/// <param name="removeTask">
		///            是否删除已运行的任务 </param>
		/// <param name="sequence">
		///            是否循环播放管理器中事务(此项为true,当前事务不完成不会进行下一个,若想同步进行可改为false) </param>
		public Scheduler(string name, long delay, bool removeTask, bool sequence)
		{
			this._loop_timer = new LTimer(name, delay);
			this._removeSequenceTask = removeTask;
			this._forceWaitSequence = sequence;
			this._closed = false;
			_forceWaitSequence = sequence;
		}

		public virtual bool IsActive()
		{
	
				return this._loop_timer.IsActive();
			
		}

		public virtual Scheduler Start()
		{
			this.Unpause();
			lock (typeof(RealtimeProcessManager))
			{
				if (_processScheduler != null)
				{
					RealtimeProcessManager.Get().Delete(_processScheduler);
				}
				if (_processScheduler == null || _processScheduler.IsDead())
				{
					_processScheduler = new SchedulerProcess(this);
				}
				_processScheduler.SetDelay(0);
				RealtimeProcessManager.Get().AddProcess(_processScheduler);
			}
			return this;
		}

		public virtual Scheduler Kill()
		{
			if (_processScheduler != null)
			{
				_processScheduler.Kill();
			}
			return this;
		}

		public virtual Scheduler Stop()
		{
			this.Pause();
			this.Kill();
			return this;
		}

		public virtual Scheduler Pause()
		{
			this._loop_timer.Pause();
			return this;
		}

		public virtual Scheduler Unpause()
		{
			this._loop_timer.Unpause();
			return this;
		}

		public virtual bool Paused()
		{
			return !IsActive();
		}

		public virtual bool Add(Interval sched)
		{
			return _scheduled.Add(sched);
		}

		public virtual bool Remove(Interval sched)
		{
			return _scheduled.Remove(sched);
		}

		public virtual Interval RemoveIndex(int idx)
		{
			return _scheduled.RemoveIndex(idx);
		}

		public virtual Interval GetIndex(int idx)
		{
			return _scheduled.Get(idx);
		}

		public virtual TArray<Interval> FindName(string name)
		{
			TArray<Interval> result = new TArray<Interval>();
			for (int i = _scheduled.size - 1; i > -1; i--)
			{
				Interval u = _scheduled.Get(i);
				if (u != null && name.Equals(u.GetName()))
				{
					result.Add(u);
				}
			}
			return result.Reverse();
		}

		public virtual Scheduler RemoveName(string name)
		{
			for (int i = _scheduled.size - 1; i > -1; i--)
			{
				Interval u = _scheduled.Get(i);
				if (u != null && name.Equals(u.GetName()))
				{
					_scheduled.RemoveIndex(i);
				}
			}
			return this;
		}

		public virtual Scheduler Clear()
		{
			_scheduled.Clear();
			return this;
		}

		public virtual bool Completed()
		{
			bool c = _scheduled.IsEmpty();
			if (c)
			{
				return true;
			}
			else
			{
				int size = _scheduled.size;
				int count = 0;
				for (int i = _scheduled.size - 1; i > -1; i--)
				{
					Interval u = _scheduled.Get(i);
					if (u != null && u.Completed())
					{
						count++;
					}
				}
				c = (count >= size);
			}
			return c;
		}

		public virtual void Update(LTimerContext context)
		{
			if (_closed)
			{
				return;
			}
			if (_loop_timer.Action(context))
			{
				if (_scheduled.size > 0)
				{
					bool seq = (_forceWaitSequence && _removeSequenceTask);
					int index = seq ? 0 : MathUtils.Max(0, _childIndex);
					Interval i = _scheduled.Get(index);
					if (i != null)
					{
						if (i._loop_timer.Action(context))
						{
							i.Loop();
						}
						if (_forceWaitSequence)
						{
							if (i.Completed())
							{
								if (_removeSequenceTask)
								{
									_scheduled.RemoveFirst();
								}
								else
								{
									_childIndex++;
								}
							}
							else if (i.Completed() && !seq)
							{
								_childIndex++;
							}
						}
						else
						{
							if (_removeSequenceTask)
							{
								_scheduled.RemoveFirst();
							}
							else
							{
								_childIndex++;
							}
						}
					}
					if (_childIndex >= _scheduled.size)
					{
						_childIndex = 0;
					}
				}
			}
		}

		public virtual Scheduler Reset()
		{
			_childIndex = 0;
			_loop_timer.Reset();
			for (int i = _scheduled.size - 1; i > -1; i--)
			{
				Interval u = _scheduled.Get(i);
				if (u != null)
				{
					u._loop_timer.Reset();
				}
			}
			return this;
		}

		public virtual Scheduler SetIndex(int idx)
		{
			this._childIndex = idx;
			this._forceWaitSequence = false;
			return this;
		}


		public virtual int GetIndex()
		{
		
				return this._childIndex;
			
		}

		public virtual int Size()
		{
			return _scheduled.size;
		}

		public virtual long GetDelay()
		{
	
				return _loop_timer.GetDelay();
			
		}

		public virtual Scheduler SetDelay(long delay)
		{
			_loop_timer.SetDelay(delay);
			return this;
		}

		public virtual string GetName()
		{
		
				return this._loop_timer.GetName();
			
		}

		public virtual LTimer CurrentTimer()
		{
			return _loop_timer;
		}

		public virtual bool IsClosed()
		{
			
				return this._closed;
			
		}

		public virtual void Close()
		{
			Clear();
			Stop();
			_closed = true;
		}

	}


}
