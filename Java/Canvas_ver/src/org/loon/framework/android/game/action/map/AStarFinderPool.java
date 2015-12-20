package org.loon.framework.android.game.action.map;

import java.util.Iterator;
import java.util.LinkedList;

import org.loon.framework.android.game.core.geom.Vector2D;


/**
 * Copyright 2008 - 2010
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class AStarFinderPool implements Runnable {

	private Field2D field;

	private Thread pathfinderThread;

	private boolean running;

	private TaskQueue pathQueue = new TaskQueue();

	public AStarFinderPool(int[][] maps) {
		this(new Field2D(maps));
	}

	public AStarFinderPool(Field2D field) {
		this.field = field;
		this.running = true;
		this.pathfinderThread = new Thread(this);
		this.pathfinderThread.start();
	}

	public void run() {
		while (running) {
			try {
				Thread.sleep(1000000);
			} catch (InterruptedException ex) {
			}
			emptyPathQueue();
		}
	}

	private void emptyPathQueue() {
		AStarFinder task;
		for (; (task = pathQueue.poll()) != null;) {
			task.run();
		}
	}

	public void stop() {
		running = true;
		pathfinderThread.interrupt();
	}

	public void search(int startx, int starty, int endx, int endy,
			boolean flying, boolean flag, AStarFinderListener callback) {
		AStarFinder pathfinderTask = new AStarFinder(field, startx, starty,
				endx, endy, flying, flag, callback);
		AStarFinder existing = pathQueue.contains(pathfinderTask);
		if (existing != null) {
			existing.update(pathfinderTask);
		} else {
			pathQueue.add(pathfinderTask);
		}
		pathfinderThread.interrupt();
	}

	public void search(int startx, int starty, int endx, int endy,
			boolean flying, AStarFinderListener callback) {
		search(startx, starty, endx, endy, flying, false, callback);
	}

	public LinkedList<Vector2D> search(int startX, int startY, int endX,
			int endY, boolean flying, boolean flag) {
		return new AStarFinder(field, startX, startY, endX, endY, flying, flag)
				.findPath();
	}

	public LinkedList<Vector2D> search(int startX, int startY, int endX,
			int endY, boolean flying) {
		return new AStarFinder(field, startX, startY, endX, endY, flying, false)
				.findPath();
	}

	class TaskQueue {

		private LinkedList<AStarFinder> queue = new LinkedList<AStarFinder>();

		public synchronized AStarFinder contains(AStarFinder element) {
			for (Iterator<AStarFinder> it = queue.iterator(); it.hasNext();) {
				AStarFinder af = it.next();
				if (af.equals(element)) {
					return af;
				}
			}
			return null;
		}

		public synchronized AStarFinder poll() {
			return queue.poll();
		}

		public synchronized void add(AStarFinder t) {
			queue.add(t);
		}
	}
}
