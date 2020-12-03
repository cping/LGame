/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AndroidScreenReceiver extends BroadcastReceiver {

	protected boolean screenLocked;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction() == Intent.ACTION_SCREEN_OFF) {
			onLocked();
		} else if (intent.getAction() == Intent.ACTION_SCREEN_ON) {
			android.app.KeyguardManager keyguard = (android.app.KeyguardManager) context
					.getSystemService(Context.KEYGUARD_SERVICE);
			if (!keyguard.isKeyguardLocked()) {
				onUnlocked();
			}
		} else if (intent.getAction() == Intent.ACTION_USER_PRESENT) {
			onUnlocked();
		}
	}

	private void onLocked() {
		screenLocked = true;
	}

	private void onUnlocked() {
		screenLocked = false;
		if (Loon.self != null) {
			Loon.self.gameView().onResume();
		}
	}

	public boolean isScreenLocked() {
		return screenLocked;
	}

}
