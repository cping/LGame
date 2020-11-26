namespace loon.utils.processes
{
	public class ProgressMonitor : ProgressListener
	{

		private readonly TArray<ProgressListener> _progressListeners = new TArray<ProgressListener>();
		private readonly ObjectMap<ProgressMonitor, ProgressListener> _childProgressMonitorToProgressListenerMap = new ObjectMap<ProgressMonitor, ProgressListener>();

		public ProgressMonitor() : this(null)
		{
		}

		public ProgressMonitor(ProgressListener p)
		{
			if (p != null)
			{
				this._progressListeners.Add(p);
			}
		}

		public override  void OnProgressChanged(int p)
		{
			int progressListenerCount = this._progressListeners.size;
			for (int i = 0; i < progressListenerCount; i++)
			{
				this._progressListeners.Get(i).OnProgressChanged(p);
			}
		}

	
		public virtual void AddChildProgressMonitor(ProgressMonitor childProgressMonitor, int rangeFrom, int rangeTo)
		{
			ProgressListener childProgressMonitorListener = new ProgressListenerImpl(this, rangeFrom, rangeTo);
			childProgressMonitor.AddProgressListener(childProgressMonitorListener);
			this._childProgressMonitorToProgressListenerMap.Put(childProgressMonitor, childProgressMonitorListener);
		}

		private class ProgressListenerImpl : ProgressListener
		{
			private readonly ProgressMonitor outerInstance;

			private readonly int rangeFrom;
			private readonly int rangeTo;

			public ProgressListenerImpl(ProgressMonitor outerInstance, int rangeFrom, int rangeTo)
			{
				this.outerInstance = outerInstance;
				this.rangeFrom = rangeFrom;
				this.rangeTo = rangeTo;
			}

		
			public override void OnProgressChanged(int pss)
			{
			
				int progress = MathUtils.Mix(rangeFrom, rangeTo, (float)pss / PROGRESS_MAX);
				outerInstance.OnProgressChanged(progress);
			}
		}

		
		public virtual void UnChildProgressMonitor(ProgressMonitor childProgressMonitor)
		{
			childProgressMonitor.RemoveProgressListener(this._childProgressMonitorToProgressListenerMap.Get(childProgressMonitor));
		}

		private void AddProgressListener(ProgressListener p)
		{
			this._progressListeners.Add(p);
		}

		
		private void RemoveProgressListener(ProgressListener p)
		{
			this._progressListeners.Add(p);
		}

	}

}
