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
package loon.lwjgl;

import loon.LSystem;
import loon.Log;
import loon.log.LogFactory;

class Lwjgl3Log extends Log {

	private boolean isInit = false;

	private loon.log.LogImpl log = null;

	public boolean init() {
		if (!isInit) {
			log = LogFactory.getInstance(LSystem.getSystemAppName());
			isInit = true;
		}
		return isInit;
	}

	@Override
	protected void callNativeLog(Level level, String msg, Throwable e) {
		if (init()) {
			log.addLogMessage(msg, level, e);
		}
	}

	@Override
	public void onError(Throwable e) {
		// if happen error the game repaint have to stop here
		// LSystem.AUTO_REPAINT = false;
	}
}
