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
package loon.action.avg.drama;

import loon.LSysException;
import loon.action.avg.drama.RocScript.ScriptException;
import loon.utils.timer.Duration;
import loon.utils.timer.Task;

/**
 * 默认的脚本任务构建
 */
public class RocSTask extends Task {

	private boolean _loopScript;

	private RocScript _script;

	private Object _result = null;

	public RocSTask(CommandLink link) {
		this(link.getValue(), false, false);
	}

	public RocSTask(CommandLink link, boolean useScriptFile) {
		this(link.getValue(), useScriptFile, false);
	}

	public RocSTask(String script, boolean useScriptFile) {
		this(script, useScriptFile, false);
	}

	public RocSTask(String script, boolean useScriptFile, boolean debug) {
		this(script, useScriptFile, debug, 0);
	}

	public RocSTask(String script, boolean useScriptFile, boolean debug, long delay) {
		super(Duration.toS(delay));
		try {
			this._script = new RocScript(script, useScriptFile);
			this._script.call(debug);
			this._loopScript = false;
			this.setDelay(delay);
		} catch (ScriptException ex) {
			throw new LSysException("ROC Script load exception", ex);
		}
	}

	public Object getResult() {
		return this._result;
	}

	/**
	 * 返回脚本解释器
	 *
	 * @return
	 */
	public RocScript getScript() {
		return _script;
	}

	/**
	 * 如果此函数为true，则循环解析脚本
	 *
	 * @return
	 */
	public boolean isLoopScript() {
		return _loopScript;
	}

	/**
	 * 如果此函数为true，则循环解析脚本
	 *
	 * @param l
	 */
	public void setLoopScript(boolean l) {
		this._loopScript = l;
	}

	@Override
	public void run() {
		if (_script != null) {
			_script.resetWait();
			for (; !_script.isCompleted();) {
				try {
					_result = _script.next();
				} catch (ScriptException e) {
					e.printStackTrace();
				}
				long waitTime = _script.waitSleep();
				if (waitTime != -1) {
					if (waitTime == RocFunctions.JUMP_TYPE) {
						_script.reset();
						return;
					}
					_loop_timer.setDelay(waitTime);
					return;
				}
			}
			if (_loopScript && _script.isCompleted()) {
				_script.reset();
			}
		}
	}

}
