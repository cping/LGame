package loon.action.avg.drama;

import loon.LSystem;
import loon.utils.StringKeyValue;
import loon.utils.StringUtils;
import loon.utils.res.TextResource;

public class CommandLink {

	private final StringKeyValue _commands;

	private int _lineCount = 0;

	public CommandLink() {
		this(null);
	}

	public CommandLink(String path) {
		this(path, LSystem.UNKNOWN);
	}

	public CommandLink(String path, String name) {
		if (!StringUtils.isNullOrEmpty(path)) {
			final String context = TextResource.get().loadText(path);
			_commands = new StringKeyValue(context.length(), name);
			_commands.addValue(context.toString());
		} else {
			_commands = new StringKeyValue(1024, name);
		}
		_lineCount = -1;
	}

	public CommandLink line(CharSequence c) {
		_commands.addValue(c);
		_commands.newLine();
		_lineCount++;
		return this;
	}

	public CommandLink clear() {
		_lineCount = 0;
		_commands.clear();
		return this;
	}

	public int getLineCount() {
		return this._lineCount;
	}

	public StringKeyValue kvcmd() {
		return _commands;
	}

	public String getValue() {
		return _commands.getValue();
	}

	@Override
	public String toString() {
		return _commands.toString();
	}

}
