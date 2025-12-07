/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.teavm;

import loon.Log;

public class TeaLog extends Log {

	private TeaGame _game;

	public TeaLog(TeaGame g) {
		_game = g;
	}

	@Override
	protected void callNativeLog(Level level, String msg, Throwable e) {
		String lmsg = level + ": " + msg;
		if (e != null) {
			lmsg += ": " + e.getMessage();
		}
		Loon.consoleLog(lmsg);
		if (e != null) {
			_game.onError(e);
		}
	}

	@Override
	public void onError(Throwable e) {
		_game.onError(e);
	}

}
