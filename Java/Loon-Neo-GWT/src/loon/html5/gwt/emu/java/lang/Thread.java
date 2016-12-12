/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package java.lang;

import com.google.gwt.core.client.GWT;

public class Thread implements Runnable {

	public final static int MIN_PRIORITY = 1;

	public final static int NORM_PRIORITY = 5;

	public final static int MAX_PRIORITY = 10;

	protected static final Thread CURRENT_THREAD = new Thread();

	private String name = "main";

	boolean interruptedFlag;

	private Thread() {
	}

	@Override
	public final void run() {
		throw new UnsupportedOperationException();
	}

	public boolean isInterrupted() {
		return interruptedFlag;
	}

	public void interrupt() {
		interruptedFlag = true;
	}

	public boolean isDaemon() {
		return false;
	}

	public boolean isAlive() {
		return true;
	}

	public long getId() {
		return 1;
	}

	public final native String getName()
	/*-{
		if (!this.threadName) {
			return 'main';
		}
		return this.threadName;
	}-*/;

	public int getPriority() {
		return NORM_PRIORITY;
	}

	public String toString() {
		return "Thread[" + getName() + "," + getPriority() + ",]";
	}

	public static int activeCount() {
		return 1;
	}

	public static boolean interrupted() {
		boolean itf = CURRENT_THREAD.interruptedFlag;
		CURRENT_THREAD.interruptedFlag = false;
		return itf;
	}

	public static Thread currentThread() {
		return CURRENT_THREAD;
	}

	public static void yield() {
	}

	private static native void realSleep(double millis) /*-{
		var startDate = new Date();
		var remaining = millis - (new Date() - startDate);
		while (remaining > 0) {
			var xmlHttpReq;
			try {
				if (window.ActiveXObject) {
					try {
						xmlHttpReq = new ActiveXObject("Msxml2.XMLHTTP");
					} catch (e) {
						xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
					}
				} else if (window.XMLHttpRequest) {
					xmlHttpReq = new XMLHttpRequest();
				}
			} catch (e) {
			}
			remaining = millis - (new Date() - startDate);
		}
	}-*/;

	public static void sleep(long millis) throws InterruptedException {
		// noop
	}

	public static void setDefaultUncaughtExceptionHandler(
			final Thread.UncaughtExceptionHandler javaHandler) {
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			@Override
			public void onUncaughtException(Throwable e) {
				final Thread th = new Thread() {
					@Override
					public String toString() {
						return "The only thread";
					}
				};
				javaHandler.uncaughtException(th, e);
			}
		});
	}

	public static interface UncaughtExceptionHandler {
		void uncaughtException(Thread t, Throwable e);
	}

}
