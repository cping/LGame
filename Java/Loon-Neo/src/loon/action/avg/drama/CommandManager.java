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

import loon.LRelease;
import loon.utils.ObjectMap;
import loon.utils.ObjectMap.Values;
import loon.utils.StringUtils;

public class CommandManager implements LRelease {

	private final ObjectMap<String, Command> _commands;

	public CommandManager() {
		_commands = new ObjectMap<String, Command>();
	}

	public CommandManager putCommand(String key, String path) {
		_commands.put(key, new Command(path));
		return this;
	}

	public CommandManager putCommand(String key, String[] cmds) {
		_commands.put(key, new Command(key, cmds));
		return this;
	}

	public String executeCommand(String key) {
		Command cmd = getCommand(key);
		if (cmd != null) {
			return cmd.doExecute();
		}
		return null;
	}

	public boolean next(String key) {
		Command cmd = getCommand(key);
		if (cmd != null) {
			return cmd.next();
		}
		return false;
	}

	public boolean gotoIndex(String key, int idx) {
		Command cmd = getCommand(key);
		if (cmd != null) {
			return cmd.gotoIndex(idx);
		}
		return false;
	}

	public boolean gotoIndex(String key, String flag) {
		Command cmd = getCommand(key);
		if (cmd != null) {
			return cmd.gotoIndex(flag);
		}
		return false;
	}

	public Command getCommand(String key) {
		if (StringUtils.isNullOrEmpty(key)) {
			return null;
		}
		return _commands.get(key);
	}

	public Command removeCommand(String key) {
		return _commands.remove(key);
	}

	@Override
	public void close() {
		for (Values<Command> iter = _commands.values(); iter.hasNext();) {
			Command cmd = iter.next();
			if (cmd != null) {
				cmd.close();
			}
		}
	}

}
