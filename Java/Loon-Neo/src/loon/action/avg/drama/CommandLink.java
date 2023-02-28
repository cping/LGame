package loon.action.avg.drama;

import loon.BaseIO;
import loon.LSystem;
import loon.utils.StringKeyValue;

public class CommandLink {

	private StringKeyValue _commands = null;

	private int _lineCount = 0;

	public CommandLink(String path) {
		this(path, LSystem.UNKNOWN);
	}

	public CommandLink(String path, String name) {
		String context = BaseIO.loadText(path);
		_commands = new StringKeyValue(context.length(), name);
		_commands.addValue(context.toString());
		_lineCount = -1;
	}

	public CommandLink() {
		_commands = new StringKeyValue(1024, LSystem.UNKNOWN);
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
