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
package loon.android;

import loon.Asyn;
import loon.LSystem;
import loon.Log;
import loon.utils.reply.Act;
import android.app.Activity;
import android.os.AsyncTask;

public class AndroidAsyn extends Asyn.Default {

	private final Activity activity;

	public AndroidAsyn(Log log, Act<? extends Object> frame, Activity activity) {
		super(log, frame);
		this.activity = activity;
	}

	protected boolean isPaused() {
		return LSystem.PAUSED;
	}

	@Override
	public void invokeLater(Runnable action) {
		if (isPaused()) {
			activity.runOnUiThread(action);
		} else {
			super.invokeLater(action);
		}
	}

	@Override
	public boolean isAsyncSupported() {
		return true;
	}

	@Override
	public void invokeAsync(final Runnable action) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				new AsyncTask<Void, Void, Void>() {
					@Override
					public Void doInBackground(Void... params) {
						try {
							action.run();
						} catch (Exception e) {
							log.warn("Async task failure [task=" + action + "]", e);
						}
						return null;
					}
				}.execute();
			}
		});
	}
}
