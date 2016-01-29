package loon.action.avg.drama;

import loon.BaseIO;
import loon.LSystem;

public class CommandLink {

	private StringBuffer _commands = null;

	private int _lineCount = 0;

	public CommandLink(String fileName) {
		String context = BaseIO.loadText(fileName);
		_commands = new StringBuffer(context.length());
		_commands.append(context.toString());
		_lineCount = -1;
	}

	public CommandLink() {
		_commands = new StringBuffer(1024);
	}

	public void line(CharSequence c) {
		_commands.append(c);
		_commands.append(LSystem.LS);
		_lineCount++;
	}

	public StringBuffer clear() {
		_lineCount = 0;
		return _commands;
	}

	public int getLineCount() {
		return this._lineCount;
	}

	public StringBuffer get() {
		return _commands;
	}

	@Override
	public String toString() {
		return _commands.toString();
	}

}
