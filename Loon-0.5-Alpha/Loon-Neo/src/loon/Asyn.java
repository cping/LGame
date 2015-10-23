/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon;

import java.util.ArrayList;
import java.util.List;

import loon.utils.reply.Act;
import loon.utils.reply.GoPromise;
import loon.utils.reply.Port;

public abstract class Asyn {

	public static class Default extends Asyn {
		private final List<Runnable> pending = new ArrayList<>();
		private final List<Runnable> running = new ArrayList<>();
		protected final Log log;

		public Default(Log log, Act<? extends Object> frame) {
			this.log = log;
			frame.connect(new Port<Object>() {
				public void onEmit(Object unused) {
					dispatch();
				}
			}).setPriority(Short.MAX_VALUE);
		}

		@Override
		public boolean isAsyncSupported() {
			return false;
		}

		@Override
		public void invokeAsync(Runnable action) {
			throw new UnsupportedOperationException();
		}

		@Override
		public synchronized void invokeLater(Runnable action) {
			pending.add(action);
		}

		private void dispatch() {
			synchronized (this) {
				running.addAll(pending);
				pending.clear();
			}

			for (int ii = 0, ll = running.size(); ii < ll; ii++) {
				Runnable action = running.get(ii);
				try {
					action.run();
				} catch (Exception e) {
					log.warn("invokeLater Runnable failed: " + action, e);
				}
			}
			running.clear();
		}
	}

	public abstract void invokeLater(Runnable action);

	public <T> GoPromise<T> deferredPromise() {
		return new GoPromise<T>() {
			@Override
			public void succeed(final T value) {
				invokeLater(new Runnable() {
					public void run() {
						superSucceed(value);
					}
				});
			}

			@Override
			public void fail(final Throwable cause) {
				invokeLater(new Runnable() {
					public void run() {
						superFail(cause);
					}
				});
			}

			private void superSucceed(T value) {
				super.succeed(value);
			}

			private void superFail(Throwable cause) {
				super.fail(cause);
			}
		};
	}

	public abstract boolean isAsyncSupported();

	public void invokeAsync(Runnable action) {
		throw new UnsupportedOperationException();
	}
}
