namespace Loon.Action.Map {

    using System;
    using System.Runtime.CompilerServices;
    using System.Collections.Generic;
    using Loon.Java;
    using Loon.Core;
    using Loon.Action.Map;
    using Loon.Core.Geom;
    using Loon.Java.Collections;
   
	public class AStarFinderPool : Runnable {
	
		private Field2D field;
	
		private Thread pathfinderThread;
	
		private bool running;
	
		private AStarFinderPool.TaskQueue  pathQueue;

        public AStarFinderPool(int[][] maps)
            : this(new Field2D(maps))
        {
			
		}
	
		public AStarFinderPool(Field2D field_0) {
			this.pathQueue = new AStarFinderPool.TaskQueue ();
			this.field = field_0;
			this.running = true;
            this.pathfinderThread = new Thread(this, "AStarThread");
			this.pathfinderThread.Start();
		}
	
		public virtual void Run() {
			while (running) {
				try {
                    Thread.Sleep(1000000);
				} catch(Exception ex){
                    Loon.Utils.Debugging.Log.Exception(ex);
				}
				EmptyPathQueue();
			}
		}
	
		private void EmptyPathQueue() {
			AStarFinder task;
			for (; (task = pathQueue.Poll()) != null;) {
				task.Run();
			}
		}
	
		public void Stop() {
			running = true;
			pathfinderThread.Interrupt();
		}
	
		public void Search(AStarFindHeuristic heuristic, int startx, int starty,
				int endx, int endy, bool flying, bool flag,
				AStarFinderListener callback) {
			AStarFinder pathfinderTask = new AStarFinder(heuristic, field, startx,
					starty, endx, endy, flying, flag, callback);
			AStarFinder existing = pathQueue.Contains(pathfinderTask);
			if (existing != null) {
				existing.Update(pathfinderTask);
			} else {
				pathQueue.Add(pathfinderTask);
			}
			pathfinderThread.Interrupt();
		}
	
		public void Search(AStarFindHeuristic heuristic, int startx, int starty,
				int endx, int endy, bool flying, AStarFinderListener callback) {
			Search(heuristic, startx, starty, endx, endy, flying, false, callback);
		}
	
		public List<Vector2f> Search(AStarFindHeuristic heuristic,
				int startX, int startY, int endX, int endY, bool flying,
				bool flag) {
			return new AStarFinder(heuristic, field, startX, startY, endX, endY,
					flying, flag).FindPath();
		}

        public List<Vector2f> Search(AStarFindHeuristic heuristic,
				int startX, int startY, int endX, int endY, bool flying) {
			return new AStarFinder(heuristic, field, startX, startY, endX, endY,
					flying, false).FindPath();
		}
	
		internal class TaskQueue {
	
			public TaskQueue() {
                this.queue = new LinkedList();
			}

            private LinkedList queue;
	
			[MethodImpl(MethodImplOptions.Synchronized)]
			public AStarFinder Contains(AStarFinder element) {
				for (IIterator it = new IteratorAdapter(queue.GetEnumerator()); it.HasNext();) {
                    AStarFinder af = (AStarFinder)it.Next();
					if (af.Equals(element)) {
						return af;
					}
				}
				return null;
			}
	
			[MethodImpl(MethodImplOptions.Synchronized)]
			public AStarFinder Poll() {
                return (AStarFinder)queue.RemoveFirst();
			}
	
			[MethodImpl(MethodImplOptions.Synchronized)]
			public void Add(AStarFinder t) {
				Loon.Utils.CollectionUtils.Add(queue,t);
			}
		}
	}
}
