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
package loon.fx;

import java.util.concurrent.ExecutorService;

import loon.Asyn;
import loon.Log;
import loon.utils.reply.Act;

public class JavaFXAsyn extends Asyn.Default {

	private ExecutorService pool;

	public JavaFXAsyn(ExecutorService p, Log log, Act<? extends Object> frame) {
		super(log, frame);
		this.pool = p;
	}

	@Override
	public boolean isAsyncSupported() {
		return true;
	}

	@Override
	public void invokeAsync(Runnable action) {
		pool.execute(action);
	}
}
