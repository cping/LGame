/**
 * Copyright 2008 - 2015
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

import loon.BaseIO;
import loon.Json;
import loon.LSystem;
import loon.utils.Array;
import loon.utils.ArrayMap;
import loon.utils.CharUtils;
import loon.utils.MathUtils;
import loon.utils.StrBuilder;
import loon.utils.StringUtils;
import loon.utils.TArray;

/**
 * Loon默认提供的脚本解释器，无平台依赖，用来实现一种近似于Processing的线型渲染与操作，
 * 另外，RocScript中也允许使用Command中的命令.
 */
public class RocScript {

	private Command macros_executer = null;

	private TArray<IMacros> macros_listeners = new TArray<IMacros>();

	private final IScriptLog scriptLog;

	private ArrayMap waitTimes = new ArrayMap();

	private void handleError(int error) throws ScriptException {
		String[] errors = new String[UNKNOWN + 1];
		errors[SYNTAX] = "Syntax Error";
		errors[UNBALPARENS] = "(... or ...)";
		errors[DIVBYZERO] = "1/0";
		errors[EQUALEXPECTED] = "Equal Expected";
		errors[UNKOWN] = "For vars that have no value: assignments,loops";
		errors[NOTABOOL] = "Not a boolean";
		errors[NOTANUMB] = "Not a number";
		errors[NOTASTR] = " Not a string";
		errors[DUPFUNCTION] = "Two functions with same name";
		errors[ENDEXPECTED] = "Reaches end of script without end";
		errors[THENEXPECTED] = "No then after if";
		errors[DOEXPECTED] = "No then after if";
		errors[MISSQUOTE] = "Strings missing a quote";
		errors[UNKFUNCTION] = "Unknown function";
		errors[INVALIDEXP] = "Invalid Expression";
		errors[UNEXPITEM] = "Unexpeced Item";
		errors[FILENOTFOUND] = "Can't find file";
		errors[INPUTIOERROR] = "Input that fails";
		errors[EXPERR] = "For if, while and for";
		errors[FILEIOERROR] = "Can't load file";
		errors[UNKNOWN] = "Unknown error";
		String err = errors[error] + ": " + textIdx + "\nLine number: " + textLine + "\nItem: " + item + "\nItem Type: "
				+ itemType + "\ncommType: " + commType + "\npreviousitem: " + previousItem;
		if (scriptLog != null) {
			scriptLog.err(err);
		}
		throw new ScriptException(err);
	}

	private final static int MAX_TEXT_SIZE = 65535;

	private RocFunctions _rocFunctions = null;

	// 参数类型
	private final int NONE = 0; // 不存在
	private final int DELIMITER = 1; // 任意特殊符号
	private final int VARIABLE = 2; // 已经存在的变量
	private final int COMMAND = 3; // 接下来的命令
	private final int EOL = 4; // 结束一行
	private final int EOP = 5; // 结束全部文本

	// 判定参数类型
	private final int STRING = 6;
	private final int NUMBER = 7;
	private final int BOOLEAN = 8;
	private final int FUNCT = 9;

	// 脚本指令
	// 条件
	private final int UNKNCOM = 0;
	private final int PRINT = 1;
	private final int INPUT = 2;
	private final int RETURN = 3;
	private final int THEN = 4;
	private final int END = 5;
	private final int BEGIN = 6;
	private final int ELSE = 7;

	// 分支
	private final int IF = 8;
	private final int FOR = 9;
	private final int WHILE = 10;
	private final int FUNCTION = 11;
	// 延迟
	private final int WAIT = 12;
	private final int PRINTLN = 13;

	// 错误
	private final int SYNTAX = 0;
	private final int UNBALPARENS = 1;
	private final int DIVBYZERO = 2;
	private final int EQUALEXPECTED = 3;
	private final int UNKOWN = 4;
	private final int NOTABOOL = 5;
	private final int NOTANUMB = 6;
	private final int NOTASTR = 7;
	private final int DUPFUNCTION = 8;
	private final int ENDEXPECTED = 9;
	private final int THENEXPECTED = 10;
	private final int MISSQUOTE = 11;
	private final int DOEXPECTED = 12;
	private final int UNKFUNCTION = 13;
	private final int INVALIDEXP = 14;
	private final int UNEXPITEM = 15;
	private final int TOOMANYPARAMS = 16;

	private final int FILENOTFOUND = 17;
	private final int INPUTIOERROR = 18;
	private final int EXPERR = 19;
	private final int FILEIOERROR = 20;

	// 宏
	private final int MACROS = 21;
	// 未知区域
	private final int UNKNOWN = 22;

	// 宏指令设置

	private boolean initNextMacros = true;

	// 保存脚本用
	private Array<LoonFun> commands;
	// 变量
	private Array<ArrayMap> vars;
	// 函数
	private ArrayMap functs;

	private char[] _cmdcontexts;
	protected long _sleep = -1;

	private boolean _stop = false;

	private int textIdx;
	private int textLine;

	private String item, previousItem;
	private int itemType;
	private int commType;
	private int macroType = -1;
	// <=
	private final char LE = 0;
	// >=
	private final char GE = 1;
	// ==
	private final char EQ = 4;

	private final char rOps[] = { LE, GE, '<', '>', EQ };

	private String relops = new String(rOps);

	private final char AND = 0;
	private final char OR = 1;
	private final char NOT = 2;
	private final char XOR = 3;
	private final char XAND = 4;

	private final char selectOpsId[] = { AND, OR, NOT, XOR, XAND };
	private final String selectOps[] = { "and", "or", "not", "xor", "xand" };

	private String[] commTable = { "", "print", "input", "return", "then", "end", "begin", "else", "if", "for", "while",
			"function", "wait", "println" };

	private String[] macros = { "{", "}" };

	private boolean debug = true;

	private final boolean DEBUG_E = false;

	public class ScriptException extends Throwable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		String errStr;

		public ScriptException(String str) {
			this.errStr = str;
		}

		@Override
		public String toString() {
			return errStr;
		}
	}

	class LoonFun {
		int loc, comm = 0, line;

		@Override
		public String toString() {
			return commTable[comm];
		}
	}

	class ForLoop extends LoonFun {
		String vName;
		int expLoc, itLoc;

		public ForLoop(String n, int exp, int it, int lo, int lin) {
			comm = FOR;
			vName = n;

			expLoc = exp;
			itLoc = it;
			loc = lo;
			line = lin;
		}

		public ForLoop() {
			comm = FOR;
		}
	}

	class WhileLoop extends LoonFun {
		int expLoc, line;

		public WhileLoop(int exp, int lo, int lin) {
			comm = WHILE;
			expLoc = exp;
			loc = lo;
			line = lin;
		}

		public WhileLoop() {
			comm = WHILE;
		}
	}

	class Function extends LoonFun {
		int backLoc;
		TArray<String> lists;

		public Function(int l, int bLoc, TArray<String> pars) {
			comm = FUNCTION;
			backLoc = bLoc;
			loc = l;
			lists = pars;
		}

		public Function() {
			comm = FUNCTION;
		}
	}

	class IfStat extends LoonFun {
		boolean done;

		public IfStat(boolean d) {
			comm = IF;
			done = d;
		}
	}

	private void splitFlag(String src, StrBuilder out, char flag) {
		char[] chars = src.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char ch = chars[i];
			if (ch == flag) {
				out.append(flag);
				if (i + 1 < chars.length && chars[i + 1] != LSystem.LF) {
					out.append(LSystem.LS);
				}
			} else {
				out.append(ch);
			}
		}
	}

	private String filterCommand(String cmd) {
		StrBuilder sbr = new StrBuilder(cmd.length());
		boolean tflag = true;
		char lastChar = 0;
		for (int i = 0; i < cmd.length(); i++) {
			char ch = cmd.charAt(i);
			if (ch == LSystem.SINGLE_QUOTE && (lastChar != '\\')) {
				tflag = !tflag;
			}
			if (tflag) {
				sbr.append(CharUtils.toLower(ch));
			} else {
				sbr.append(ch);
			}
			lastChar = ch;
		}
		return sbr.toString();
	}

	private String filtrScript(String script) {
		StrBuilder out = new StrBuilder();
		String[] context = StringUtils.split(script, new char[] { LSystem.CR, LSystem.LF, LSystem.TF }, true);
		for (String c : context) {
			if (c == null) {
				continue;
			}

			String cmd = filterCommand(c).trim();

			if (cmd.startsWith("print") && cmd.indexOf(LSystem.DOUBLE_QUOTES) != -1
					&& cmd.indexOf(LSystem.COMMA) == -1) {
				char[] chars = cmd.toCharArray();
				boolean flag = false;
				for (int i = 0; i < chars.length; i++) {
					if (chars[i] == LSystem.DOUBLE_QUOTES) {
						flag = !flag;
					}
					if (!flag) {
						if (chars[i] == '+') {
							chars[i] = LSystem.COMMA;
						}
					}
				}
				cmd = new String(chars);
			}
			if (cmd.indexOf(LSystem.DELIM_START) != -1) {
				splitFlag(cmd, out, LSystem.DELIM_START);
			} else {
				out.append(cmd);
			}
			out.append(LSystem.LS);
		}
		return out.toString();
	}

	/**
	 * 构建脚本
	 * 
	 * @param script
	 * @param useFile
	 * @throws ScriptException
	 */
	public RocScript(String script, boolean useFile) throws ScriptException {
		this(new DefScriptLog(), script, useFile);
	}

	/**
	 * 构建脚本
	 * 
	 * @param log
	 * @param script
	 * @param useFile
	 * @throws ScriptException
	 */
	public RocScript(IScriptLog log, String script, boolean useFile) throws ScriptException {
		this._rocFunctions = new RocFunctions();
		this.scriptLog = log;
		debug("Loading file...");
		char[] charlist = new char[MAX_TEXT_SIZE];
		int size = 0;
		// 文件名导入
		if (useFile) {
			try {
				String text = BaseIO.loadText(script);
				charlist = filtrScript(text).toCharArray();
				size = charlist.length;
			} catch (Throwable exc) {
				handleError(FILEIOERROR);
			}
			// 当读取到文件尾部时，后退一位
			if (charlist[size - 1] == (char) 26) {
				size--;
			}
		} else {
			charlist = filtrScript(script).toCharArray();
			size = charlist.length;
		}
		if (size > MAX_TEXT_SIZE) {
			size = MAX_TEXT_SIZE;
		}
		if (size != -1) {
			_cmdcontexts = new char[size];
			System.arraycopy(charlist, 0, _cmdcontexts, 0, size);
		}
	}

	private void debug(String s) {
		if (debug) {
			if (commands != null) {
				for (int i = 0; i < commands.size(); i++)
					scriptLog.line("\t");
			}
			scriptLog.err("> " + s);
		}
	}

	private void debug(String[] strs) {
		if (debug) {
			String str = LSystem.EMPTY;
			if (commands != null) {
				for (int i = 0; i < commands.size(); i++)
					str += "\t";
			}
			for (String s : strs) {
				scriptLog.err(str + s);
			}
		}
	}

	/**
	 * 指定脚本程序
	 * 
	 * @param de
	 * @throws ScriptException
	 */
	public void call(boolean de) {
		debug("Running script...");
		setDebug(de);
		// 初始化寄存器
		vars = new Array<ArrayMap>();
		vars.add(new ArrayMap());
		functs = new ArrayMap();
		commands = new Array<LoonFun>();
		item = null;
		textIdx = 0;
		textLine = 1;
		itemType = 0;
		commType = 0;
		macroType = -1;
	}

	/**
	 * 重新运行脚本
	 */
	public void reset() {
		call(this.debug);
		resetWait();
	}

	public void setDebug(boolean de) {
		this.debug = de;
	}

	public boolean isDebug() {
		return this.debug;
	}

	public void stop() {
		_stop = true;
	}

	/**
	 * 执行脚本命令
	 * 
	 * @return
	 * @throws ScriptException
	 */
	public Object next() throws ScriptException {
		try {
			debug("Starting script...");
			if (_sleep != -1) {
				return null;
			}
			for (; !isCompleted() && nextItem() && !_stop;) {
				if (item != null) {
					item = item.trim();
				}
				switch (itemType) {
				// 变量
				case VARIABLE:
					assignVar();
					break;
				// 函数
				case FUNCT:
					execFunct();
					nextItem();
					break;
				// 具体表达式变量
				case COMMAND:
					switch (commType) {
					case PRINT:
						print();
						break;
					case PRINTLN:
						println();
						break;
					case INPUT:
						break;
					case IF:
						execIf();
						break;
					case FOR:
						execFor();
						break;
					case END:
						if (endCommand(false)) {
							return null;
						}
						break;
					case WHILE:
						execWhile();
						break;
					case RETURN:
						debug("Returning");
						nextItem();
						Object o = analysis();
						endCommand(true);
						return o;
					case FUNCTION:
						newFunction();
						break;
					case ELSE:
						execElse();
						break;
					case WAIT:
						debug("waiting");
						break;
					}
					debug("Done with command");
					break;
				// 宏指令
				case MACROS:
					switch (macroType) {
					case 0:
						callMacros();
						itemType = EOL;
						break;
					case 1:
						itemType = EOL;
						break;
					}
					break;
				}

				// 判定解析完毕
				if (itemType != EOL && itemType != EOP) {
					handleError(UNEXPITEM);
				}
				if (_sleep != -1) {
					return null;
				}
			}
		} finally {
			previousItem = item;
		}

		return null;
	}

	public Object running() throws ScriptException {
		try {
			debug("Starting script...");

			if (nextItem() && !_stop) {
				if (item != null) {
					item = item.trim();
				}
				switch (itemType) {
				// 变量
				case VARIABLE:
					assignVar();
					break;
				// 函数
				case FUNCT:
					execFunct();
					nextItem();
					break;
				// 具体表达式变量
				case COMMAND:
					switch (commType) {
					case PRINT:
						print();
						break;
					case PRINTLN:
						println();
						break;
					case INPUT:
						break;
					case IF:
						execIf();
						break;
					case FOR:
						execFor();
						break;
					case END:
						if (endCommand(false)) {
							return null;
						}
						break;
					case WHILE:
						execWhile();
						break;
					case RETURN:
						debug("Returning");
						nextItem();
						Object o = analysis();
						endCommand(true);
						return o;
					case FUNCTION:
						newFunction();
						break;
					case ELSE:
						execElse();
						break;
					case WAIT:
						debug("waiting");
						break;
					}
					debug("Done with command");
					break;
				// 宏指令
				case MACROS:
					switch (macroType) {
					case 0:
						callMacros();
						itemType = EOL;
						break;
					case 1:
						itemType = EOL;
						break;
					}
					break;
				}

				// 判定解析完毕
				if (itemType != EOL && itemType != EOP) {
					handleError(UNEXPITEM);
				}
			}
		} finally {
			previousItem = item;
		}

		return null;
	}

	public void setCallMacros(boolean f) {
		this.initNextMacros = f;
	}

	public boolean isCallMacros() {
		return this.initNextMacros;
	}

	private void callMacros() throws ScriptException {
		nextItem();
		debug("Macros:");
		debug(item);
		macrosCommand(this.item, this.textLine);
	}

	public void setMacrosListener(TArray<IMacros> list) {
		if (list == null) {
			return;
		}
		this.macros_listeners = list;
	}

	public void setMacrosListener(IMacros... args) {
		if (args == null) {
			return;
		}
		for (int i = 0; i < args.length; i++) {
			this.macros_listeners.add(args[i]);
		}
	}

	public void setMacrosListener(IMacros macros) {
		addMacrosListener(macros);
	}

	public void addMacrosListener(IMacros macros) {
		if (this.macros_listeners != null && macros != null) {
			this.macros_listeners.add(macros);
		}
	}

	private void macrosCommand(String context, int id) {
		if (!initNextMacros) {
			return;
		}
		Command.resetCache();
		String[] res = StringUtils.split(context, LSystem.LF);
		if (macros_executer == null) {
			macros_executer = new Command("script" + id, res);
		} else {
			macros_executer.formatCommand("script" + id, res);
		}
		for (int i = 0; i < vars.size(); i++) {
			ArrayMap maps = vars.get(i);
			macros_executer.setVariables(maps);
		}
		if (scriptLog != null) {
			scriptLog.info("Syncing...");
		}
		for (; macros_executer.next();) {
			String result = macros_executer.doExecute();
			if (result == null) {
				continue;
			}
			if (macros_listeners != null) {
				for (IMacros macros_listener : macros_listeners) {
					macros_listener.call(scriptLog, textLine, macros_executer, result);
				}
			}
		}
		if (scriptLog != null) {
			scriptLog.info("Synchro is completed.");
		}
		vars.add(macros_executer.getVariables());
	}

	private void println() throws ScriptException {
		debug("Println");
		String lastDelim = LSystem.EMPTY;
		while (nextItem() && itemType != EOL && itemType != EOP) {
			scriptLog.info(analysis());
			lastDelim = item;
			if (lastDelim.equals(",")) {
				scriptLog.line(" ");
			} else if (lastDelim.equals(";")) {
				scriptLog.line("\t");
			} else if (itemType != EOL && itemType != EOP) {
				handleError(SYNTAX);
			} else {
				break;
			}
		}
	}

	private void print() throws ScriptException {
		debug("Print");
		String lastDelim = LSystem.EMPTY;
		while (nextItem() && itemType != EOL && itemType != EOP) {
			scriptLog.line(analysis());
			lastDelim = item;
			if (lastDelim.equals(",")) {
				scriptLog.line(" ");
			} else if (lastDelim.equals(";")) {
				scriptLog.line("\t");
			} else if (itemType != EOL && itemType != EOP) {
				handleError(SYNTAX);
			} else {
				break;
			}
		}
	}

	private void execIf() throws ScriptException {
		debug("If select");
		boolean result = false;

		nextItem();

		try {
			Object o = analysis();
			if (o instanceof Boolean) {
				result = (boolean) o;
			} else if (o instanceof Number) {
				result = Double.parseDouble(o.toString()) > 0;
			}
		} catch (ClassCastException exc) {
			handleError(NOTABOOL);
			return;
		}

		vars.add(new ArrayMap());
		commands.add(new IfStat(result));

		if (result) {
			if (commType != THEN) {
				handleError(THENEXPECTED);
				return;
			}
			nextItem();

		} else {
			nextEnd();
		}
	}

	private void execElse() throws ScriptException {
		debug("Else select");
		LoonFun c = commands.last();

		if (c.comm != IF) {
			handleError(SYNTAX);
			return;
		}

		if (!((IfStat) c).done) {
			nextItem();

			if (commType == IF) {

				nextItem();

				boolean result;

				try {
					result = (boolean) analysis();
				} catch (ClassCastException exc) {
					handleError(NOTABOOL);
					return;
				}

				if (result) {
					((IfStat) c).done = true;

					if (commType != THEN) {
						handleError(THENEXPECTED);
						return;
					}
					nextItem();

				} else {
					nextEnd();
				}
				return;
			}
		} else {
			nextEnd();
		}
	}

	// 循环执行
	private void execFor() throws ScriptException {
		debug("For Loop");
		double i;
		int expLoc, ittLoc, loc;
		String vname;

		nextItem();
		vname = item;

		// =
		nextItem();
		if (item.equals("=")) {
			nextItem();
			try {
				i = (double) analysis();
			} catch (ClassCastException exc) {
				handleError(EXPERR);
				return;
			}
			vars.last().put(vname, i);
		}

		// ,
		if (!item.equals(",")) {
			handleError(SYNTAX);
			return;
		}

		expLoc = textIdx;
		nextItem();

		try {
			if (!(boolean) analysis()) {
				vars.add(new ArrayMap());
				commands.add(new ForLoop());
				nextEnd();
				return;
			}
		} catch (ClassCastException exc) {
			handleError(NOTABOOL);
		}

		if (!item.equals(",")) {
			handleError(SYNTAX);
			return;
		}

		ittLoc = textIdx;
		nextItem();

		analysis();

		if (commType != BEGIN) {
			handleError(DOEXPECTED);
			return;
		}

		loc = textIdx;
		nextItem();

		ForLoop newfor;

		try {
			newfor = new ForLoop(vname, expLoc, ittLoc, loc, textLine);
		} catch (ClassCastException exc) {
			handleError(EXPERR);
			return;
		}

		vars.add(new ArrayMap());
		commands.add(newfor);
	}

	private void execWhile() throws ScriptException {
		debug("While Loop");
		int expLoc;

		expLoc = textIdx;

		nextItem();

		try {
			if (!(boolean) analysis()) {
				vars.add(new ArrayMap());
				commands.add(new WhileLoop());
				nextEnd();
				return;
			}
		} catch (ClassCastException exc) {
			handleError(NOTABOOL);
		}

		if (commType != BEGIN) {
			handleError(DOEXPECTED);
			return;
		}

		int loc = textIdx;
		nextItem();

		WhileLoop loop = new WhileLoop(expLoc, loc, textLine);

		vars.add(new ArrayMap());
		commands.add(loop);
	}

	private void newFunction() throws ScriptException {
		debug("New Function");

		String fName;

		nextItem();

		if (!(Character.isLetter(item.charAt(0)))) {
			handleError(UNKOWN);
		}

		fName = item;

		nextItem();

		if (!item.equals("(")) {
			handleError(SYNTAX);
		}

		nextItem();

		TArray<String> lists = new TArray<String>();

		if (!item.equals(")")) {
			if (Character.isLetter(item.charAt(0))) {
				lists.add(item);
				while (nextItem() && item.equals(",")) {
					nextItem();
					lists.add(item);
				}
				if (!item.equals(")")) {
					handleError(SYNTAX);
				}

			} else {
				handleError(UNKOWN);
				return;
			}
		}

		nextItem();

		if (!(commType == BEGIN)) {
			handleError(SYNTAX);
		}

		Function f = new Function(textIdx, -1, lists);

		functs.put(fName.toLowerCase(), f);
		vars.add(new ArrayMap());
		commands.add(f);

		nextEnd();
		nextItem();

	}

	private Object execFunct() throws ScriptException {
		debug("Execute Function");

		if (_rocFunctions._system_functs.contains(item.trim().toLowerCase())) {

			String key = item;
			nextItem();
			if (!item.equals("(")) {
				handleError(UNBALPARENS);
				return null;
			}
			nextItem();
			String value = LSystem.EMPTY;

			if (!item.equals(")")) {
				while (item.indexOf(")") == -1) {
					value += item;
					nextItem();

				}
				if (!item.equals(")")) {
					handleError(UNBALPARENS);
					return null;
				}
			}

			if (value.length() > 0 && value.indexOf(",") == -1) {
				if (value.indexOf("\"") == -1 && value.indexOf("/") == -1 && !isNumber(value)) {
					String tmp = getVarVal(value).toString();
					if (!LSystem.UNKNOWN.equalsIgnoreCase(tmp)) {
						value = tmp;
					}
				}
			} else if (value.indexOf(",") != -1) {
				String[] split = StringUtils.split(value, LSystem.COMMA);
				StrBuilder sbr = new StrBuilder();
				for (String s : split) {
					if (s.indexOf("\"") == -1 && value.indexOf("/") == -1 && !isNumber(s)) {
						String tmp = getVarVal(s).toString();
						if (!LSystem.UNKNOWN.equalsIgnoreCase(tmp)) {
							sbr.append(tmp.toString());
						} else {
							sbr.append(s);
						}
					} else {
						sbr.append(s);
					}
					sbr.append(LSystem.COMMA);
				}
				value = sbr.toString();
				if (value.endsWith(",")) {
					value = value.substring(0, value.length() - 1);
				}

			}
			Object reuslt = _rocFunctions.getValue(this, key, value);
			return reuslt == null ? LSystem.UNKNOWN : reuslt;
		}

		Function f = (Function) functs.get(item.toLowerCase());

		nextItem();
		if (!item.equals("(")) {
			handleError(UNBALPARENS);
			return null;
		}

		nextItem();

		ArrayMap newVars = new ArrayMap();

		int i = 0;

		if (!item.equals(")")) {
			newVars.put(f.lists.get(i++), analysis());
			while (item.equals(",")) {
				nextItem();
				newVars.put(f.lists.get(i++), analysis());
			}

			if (f.lists.size < i) {
				handleError(TOOMANYPARAMS);
			}

			if (!item.equals(")")) {
				handleError(UNBALPARENS);
				return null;
			}
		}

		f.backLoc = textIdx;
		vars.add(newVars);
		commands.add(f);

		textIdx = f.loc;

		return next();
	}

	private boolean endCommand(boolean force) throws ScriptException {
		debug("End LoonFun");

		if (force) {
			while (commands.last().comm != FUNCTION) {
				commands.pop();
			}
		}

		LoonFun p = commands.last();
		if (p == null) {
			handleError(SYNTAX);
		}
		switch (p.comm) {
		case IF:
			nextItem();
			passBack();
			commands.pop();
			return false;
		case FOR:
			int loc = textIdx;
			ForLoop f = (ForLoop) p;
			if (f.loc > 0) {
				textIdx = f.itLoc;
				nextItem();
				vars.last().put(f.vName, (double) analysis());
				textIdx = f.expLoc;
				nextItem();

				if ((boolean) analysis()) {
					textIdx = f.loc;
					textLine = f.line;
				} else {
					passBack();
					commands.pop();
					textIdx = loc;
				}
			} else {
				passBack();
				commands.pop();
			}
			nextItem();
			return false;

		case WHILE:
			loc = textIdx;
			WhileLoop w = (WhileLoop) p;
			if (w.loc > 0) {
				textIdx = w.expLoc;
				nextItem();

				if ((boolean) analysis()) {
					textIdx = w.loc;
					textLine = w.line;
				} else {
					passBack();
					commands.pop();
					textIdx = loc;
				}
			} else {
				passBack();
				commands.pop();
			}
			nextItem();
			return false;
		case FUNCTION:
			loc = textIdx;
			Function funct = (Function) p;
			if (funct.backLoc > 0) {
				textIdx = funct.backLoc;
			}
			passBack();
			commands.pop();

			return true;
		}
		return false;
	}

	private void nextEnd() throws ScriptException {
		debug("Next end");
		int count = 1;
		while (count > 0 && nextItem()) {
			if (commType > 7 && commType != PRINTLN) {
				count++;
				debug("Find End: " + count);
			}
			if (commType == END) {
				count--;
				debug("Find End: " + count);
			}
			if (commType == ELSE) {
				if (count == 1) {
					execElse();
					return;
				} else if (nextItem() && commType == IF) {
					debug("Find End: " + count);
				}
			}
		}
		if (commType != END) {
			handleError(ENDEXPECTED);
			return;
		}

		endCommand(false);
	}

	/**
	 * 检查脚本是否解析完毕
	 * 
	 * @return
	 */
	public boolean isCompleted() {
		return this.itemType == EOP;
	}

	/**
	 * 单纯判定脚本命令是否存在，以及向下移动一次命令行
	 * 
	 * @return
	 * @throws ScriptException
	 */
	private boolean nextItem() throws ScriptException {
		boolean result = nextCommand();
		debug(new String[] { "Item: " + item, "CommandStack: " + commands, "Type: " + itemType });

		return result;
	}

	/**
	 * 分步检索脚本命令
	 * 
	 * @return
	 * @throws ScriptException
	 */
	private boolean nextCommand() throws ScriptException {
		if (itemType == MACROS && macroType != -1) {
			char flag = macros[1].toCharArray()[0];
			StrBuilder sbr = new StrBuilder(1024);
			while (textIdx < _cmdcontexts.length) {
				char ch = _cmdcontexts[textIdx++];
				if (flag == ch) {
					break;
				}
				sbr.append(ch);
			}
			item = sbr.toString();
			itemType = EOL;
			macroType = -1;
			return true;
		}

		char ch = LSystem.SPACE;

		item = LSystem.EMPTY;
		itemType = NONE;
		commType = UNKNCOM;
		macroType = -1;

		while (textIdx < _cmdcontexts.length && isSpaceOrTab(_cmdcontexts[textIdx])) {
			textIdx++;
		}

		if (textIdx >= _cmdcontexts.length) {
			itemType = EOP;
			item = " ";
			return false;
		}

		if (_cmdcontexts[textIdx] == LSystem.CR) {
			textIdx += 2;
			itemType = EOL;
			item = " ";
			textLine++;
			return true;
		}

		ch = _cmdcontexts[textIdx];

		if (ch == '#' || (ch == '/' && textIdx + 1 < _cmdcontexts.length && _cmdcontexts[textIdx + 1] == '/')) {
			while (textIdx < _cmdcontexts.length && _cmdcontexts[textIdx] != LSystem.CR) {
				textIdx++;
			}
			textIdx += 2;
			itemType = EOL;
			item = " ";
			textLine++;
			return true;
		}

		if (ch == '<' || ch == '>' || ch == '=') {
			switch (ch) {
			case '<':
				if (_cmdcontexts[textIdx + 1] == '=') {
					item = String.valueOf(LE);
					textIdx += 2;
				} else {
					item = "<";
					textIdx++;
				}
				break;
			case '>':
				if (_cmdcontexts[textIdx + 1] == '=') {
					item = String.valueOf(GE);
					textIdx += 2;
				} else {
					item = ">";
					textIdx++;
				}
				break;
			case '=':
				if (_cmdcontexts[textIdx + 1] == '=') {
					item = String.valueOf(EQ);
					textIdx += 2;
				} else {
					item = "=";
					textIdx++;
				}
				break;

			}
			itemType = DELIMITER;
			return true;
		}

		if (isDelim(ch)) {
			item += _cmdcontexts[textIdx];
			textIdx++;
			itemType = DELIMITER;
			return true;
		} else if (ch == '"') {
			textIdx++;
			ch = _cmdcontexts[textIdx];
			while (ch != '"' && ch != LSystem.CR) {
				item += ch;
				textIdx++;
				ch = _cmdcontexts[textIdx];
			}
			if (ch == LSystem.CR) {
				handleError(MISSQUOTE);
				return false;
			}
			textIdx++;
			itemType = STRING;
			return true;
		} else {
			while (textIdx < _cmdcontexts.length && !isDelim(_cmdcontexts[textIdx])) {
				item += _cmdcontexts[textIdx];
				textIdx++;
			}
			if (isNumber(item)) {
				itemType = NUMBER;
				return true;
			} else if (isBoolean(item)) {
				itemType = BOOLEAN;
				return true;
			} else {
				// 匹配命令
				itemType = lookup(item);
				if (itemType == UNKNCOM) {
					itemType = VARIABLE;
				}
				if (commType == WAIT) {
					item = LSystem.EMPTY;
					int count = 0;
					while (textIdx < _cmdcontexts.length) {
						ch = _cmdcontexts[textIdx];
						if ((ch == LSystem.SPACE) || (ch == LSystem.LF) || (ch == LSystem.TF) || (ch == LSystem.CR)) {
							count++;
						}
						if (count > 1) {
							break;
						}
						if (ch != LSystem.SPACE) {
							item += _cmdcontexts[textIdx];
						}
						textIdx++;
					}
					long sleep = 0;
					if (isNumber(item)) {
						sleep = (long) Double.parseDouble(item);
					} else {
						sleep = getWaitTime(item);
					}
					if (sleep <= 0) {
						sleep = 1;
					}
					_sleep = sleep;
					nextItem();
				}
				return true;
			}
		}
	}

	public long getWaitTime(String item) {
		if (waitTimes == null) {
			waitTimes = new ArrayMap();
		}
		if (waitTimes.size() == 0) {
			waitTimes.put("mesc", LSystem.MSEC);
			waitTimes.put("second", LSystem.SECOND);
			waitTimes.put("minute", LSystem.MINUTE);
			waitTimes.put("hour", LSystem.HOUR);
			waitTimes.put("day", LSystem.DAY);
		}
		return (long) waitTimes.get(item.toLowerCase());
	}

	public long waitSleep() {
		return _sleep;
	}

	public void resetWait() {
		this._sleep = -1;
	}

	private Object analysis() throws ScriptException {
		debug("Analysis");
		Object result;

		if (item.equals(EOL) || item.equals(EOP)) {
			handleError(EXPERR);
		}
		result = evalExp1();

		debug("Analysis end: " + result);

		return result;
	}

	private Object evalExp1() throws ScriptException {
		Object result, pResult;
		double l_temp, r_temp;
		boolean lb, rb;
		String ls, rs;
		char op;
		String str;

		result = evalExp2();

		op = item.charAt(0);
		str = item.toLowerCase();

		while (isRelOp(op) || isBoolOp(str)) {
			nextItem();

			if (isNumber(result)) {
				pResult = evalExp2();
				if (isRelOp(op)) {
					if (isNumber(result)) {
						l_temp = (double) result;
						r_temp = (double) pResult;

						switch (op) {
						case '<':
							result = (l_temp < r_temp);
							break;
						case LE:
							result = (l_temp <= r_temp);
							break;
						case '>':
							result = (l_temp > r_temp);
							break;
						case GE:
							result = (l_temp >= r_temp);
							break;
						case EQ:
							result = (l_temp == r_temp);
							break;
						}
					} else {
						handleError(NOTANUMB);
						result = null;
					}
				}
			} else if (isBoolean(result)) {
				pResult = evalExp1();
				if (isBoolOp(str)) {
					if (isBoolean(result)) {

						lb = (boolean) result;
						rb = (boolean) pResult;
						switch (str) {
						case "and":
							result = (lb && rb);
							break;
						case "or":
							result = (lb || rb);
							break;
						case "xor":
							result = (lb ^ rb);
							break;
						case "xand":
							result = (lb == rb);
							break;
						}
					} else {
						handleError(NOTABOOL);
						result = null;
					}
				}
			} else {
				if (isRelOp(op)) {
					pResult = evalExp2();
					if (!isNumber(result)) {
						rs = (String) pResult;
						ls = (String) result;
						double test = (ls.compareTo(rs));

						switch (op) {
						case '<':
							result = test < 0;
							break;
						case LE:
							result = test <= 0;
							break;
						case '>':
							result = test > 0;
							break;
						case GE:
							result = test >= 0;
							break;
						case EQ:
							result = test == 0;
							break;
						}
					} else {
						handleError(NOTASTR);
						result = null;
					}
				}
			}
			op = item.charAt(0);
			str = item.toLowerCase();
		}

		if (DEBUG_E) {
			debug("1: " + result);
		}
		return result;
	}

	private Object evalExp2() throws ScriptException {
		char op;
		Object result;
		Object pResult;

		result = evalExp3();

		while ((op = item.charAt(0)) == '+' || op == '-') {
			nextItem();
			pResult = evalExp3();

			if (isNumber(result)) {
				if (isNumber(pResult)) {
					switch (op) {
					case '-':
						result = (double) result - (double) pResult;
						break;
					case '+':
						result = (double) result + (double) pResult;
						break;
					}
				} else {
					handleError(NOTANUMB);
					return null;
				}
			} else if (!isBoolean(result)) {
				if (!isNumber(pResult) && !isBoolean(pResult)) {
					switch (op) {
					case '-':
						handleError(INVALIDEXP);
					case '+':
						result = (String) result + (String) pResult;
						break;
					}
				} else {
					handleError(NOTASTR);
					return null;
				}
			}
		}
		if (DEBUG_E) {
			debug("2: " + result);
		}
		return result;
	}

	private Object evalExp3() throws ScriptException {
		char op;
		Object result;
		Object partialResult;

		result = evalExp4();

		while ((op = item.charAt(0)) == '*' || op == '/' || op == '%') {
			if (!isNumber(result)) {
				handleError(NOTANUMB);
				return null;
			}

			nextItem();
			partialResult = evalExp4();

			if (!isNumber(partialResult)) {
				handleError(NOTANUMB);
				return null;
			}

			switch (op) {
			case '*':
				result = (double) result * (double) partialResult;
				break;
			case '/':
				if ((double) partialResult == 0.0)
					handleError(DIVBYZERO);
				result = (double) result / (double) partialResult;
				break;
			case '%':
				if ((double) partialResult == 0.0)
					handleError(DIVBYZERO);
				result = (double) result % (double) partialResult;
				break;
			}
		}
		if (DEBUG_E) {
			debug("3: " + result);
		}
		return result;
	}

	private Object evalExp4() throws ScriptException {
		Object result;
		Object partialResult;
		double ex;
		double t;

		result = evalExp5();

		if (item.equals("^")) {
			if (!isNumber(result)) {
				handleError(NOTANUMB);
				return null;
			}

			nextItem();
			partialResult = evalExp4();

			if (!isNumber(partialResult)) {
				handleError(NOTANUMB);
				return null;
			}

			ex = (double) result;
			if ((double) partialResult == 0.0) {
				result = 1.0;
			} else {
				for (t = (double) partialResult - 1; t > 0; t--) {
					result = (double) result * ex;
				}
			}
		}
		if (DEBUG_E) {
			debug("4: " + result);
		}
		return result;
	}

	private Object evalExp5() throws ScriptException {
		Object result;
		String op = item;

		if (item.equals("-") || item.toLowerCase().equals(selectOps[NOT])) {

			nextItem();

			result = evalExp6();
			if (isNumber(result)) {
				if (op.equals("-"))
					result = -(double) result;
				else {
					handleError(NOTABOOL);
					return null;
				}
			} else if (isBoolean(result)) {
				if (op.toLowerCase().equals(selectOps[NOT]))
					result = !(boolean) result;
				else {
					handleError(NOTANUMB);
					return null;
				}
			} else {
				handleError(INVALIDEXP);
				return null;
			}
		} else {

			result = evalExp6();

		}
		if (DEBUG_E) {
			debug("5: " + result);
		}
		return result;
	}

	private Object evalExp6() throws ScriptException {
		Object result;

		if (item.equals("(")) {
			nextItem();
			result = evalExp1();
			if (!item.equals(")")) {
				handleError(UNBALPARENS);
			}
			nextItem();
			return result;
		} else {

			result = atom();

			nextItem();
		}
		if (DEBUG_E) {
			debug("6: " + result);
		}
		return result;
	}

	private Object atom() throws ScriptException {

		switch (itemType) {
		case FUNCT:
			return execFunct();
		case NUMBER:
			try {
				return Double.parseDouble(item);
			} catch (NumberFormatException exc) {
				handleError(NOTANUMB);
			}
		case VARIABLE:
			Object o = getVarVal(item);
			if (o instanceof Number) {
				return (Number) o;
			} else if (isNumber(o)) {
				if (DEBUG_E) {
					debug("atom: " + o.toString());
				}
				return Double.parseDouble(o.toString());
			}
			if (o instanceof Boolean) {
				return (boolean) o;
			} else if (isBoolean(o)) {
				return StringUtils.toBoolean((String) o);
			}
			return o;
		case BOOLEAN:
			return StringUtils.toBoolean(item);
		case STRING:
			return item;
		default:
			return null;
		}
	}

	private final static String _delimString = " \r,<>+-/*%^=();#";

	private boolean isDelim(char c) {
		if ((_delimString.indexOf(c) != -1)) {
			return true;
		}
		return false;
	}

	boolean isSpaceOrTab(char c) {
		if (c == LSystem.SPACE || c == LSystem.TF) {
			return true;
		}
		return false;
	}

	protected boolean isRelOp(char c) {
		if (relops.indexOf(c) != -1) {
			return true;
		}
		return false;
	}

	protected boolean isBoolOp(String str) {
		for (int i = 0; i < selectOps.length; i++) {
			if (selectOps[i].equals(str)) {
				return true;
			}
		}
		return false;
	}

	protected boolean isBoolOpId(int id) {
		for (int i = 0; i < selectOpsId.length; i++) {
			if (selectOpsId[i] == id) {
				return true;
			}
		}
		return false;
	}

	protected boolean isBoolean(Object o) {
		if (o == null) {
			return false;
		}
		String str = o.toString().toLowerCase();
		return StringUtils.isBoolean(str) || MathUtils.isNan(str);
	}

	protected boolean isNumber(Object o) {
		if (o == null) {
			return false;
		}
		String str = o.toString();
		return MathUtils.isNan(str);
	}

	private int lookup(String str) {
		int i;

		str = StringUtils.rtrim(str.toLowerCase());
		// 判定是否已定义的变量
		for (int j = 0; j < vars.size(); j++) {
			ArrayMap tm = vars.get(j);
			if (tm.containsKey(str)) {
				return VARIABLE;
			}
		}

		// 判定是否已定义的函数
		if (functs.containsKey(str) || _rocFunctions._system_functs.contains(str.trim().toLowerCase())) {
			return FUNCT;
		}
		// 判定是否分支指令
		for (i = 0; i < selectOps.length; i++) {
			if (selectOps[i].equals(str)) {
				return DELIMITER;
			}
		}
		// 判定是否存在于命令表中
		for (i = 0; i < commTable.length; i++) {
			if (commTable[i].equals(str)) {
				commType = i;
				return COMMAND;
			}
		}
		// 判定是否为宏指令
		for (i = 0; i < macros.length; i++) {
			if (macros[i].equals(str)) {
				macroType = i;
				return MACROS;
			}
		}
		return UNKNCOM;
	}

	private void assignVar() throws ScriptException {
		debug("Assign variable");
		String varName = item;
		if (!Character.isLetter(varName.charAt(0))) {
			handleError(UNKOWN);
			return;
		}
		nextItem();

		if (!item.equals("=")) {
			handleError(EQUALEXPECTED);
			return;
		}

		nextItem();

		Object obj = analysis();

		vars.last().put(varName, obj);
	}

	private void passBack() {
		ArrayMap tvars = vars.pop();

		for (int i = 0; i < tvars.size(); i++) {
			String str = (String) tvars.getKey(i);
			if (vars.last().containsKey(str)) {
				vars.last().put(str, tvars.get(str));
			}
		}
	}

	private Object[] findVar(String vname) {
		Object o = null;
		int idx = vname.lastIndexOf('.');
		if (idx == -1) {
			return null;
		}

		String name = vname.substring(0, idx);

		for (int i = 0; i < vars.size(); i++) {
			ArrayMap tm = vars.get(i);
			if (tm.containsKey(name)) {
				o = tm.get(name);
				break;
			}
		}
		if (o != null) {

			String method = vname.substring(idx + 1, vname.length());

			return new Object[] { name, method, o };
		}
		return findVar(name);
	}

	/**
	 * 查询指定的json对象元素（暂时未使用递归，预防有不愿展露的数据被穷举出来）
	 * 
	 * @param value
	 * @param vname
	 * @param method
	 * @return
	 */
	private Object queryJson(Object value, String vname, String method) {
		int start = 0;
		int end = 0;
		Object o = null;
		Json.Object json = (Json.Object) value;
		if (json.containsKey(method)) {
			o = json.getObject(method);
		}
		if (!vname.equals(method)) {
			start = vname.indexOf(method);
			int size = start + method.length() + 1;
			String packName = LSystem.EMPTY;
			if (size >= vname.length()) {
				packName = method;
			} else {
				packName = vname.substring(size, vname.length());
			}
			json = (Json.Object) o;
			if (packName.indexOf(LSystem.BRACKET_START) != -1 && packName.indexOf(LSystem.BRACKET_END) != -1) {
				if (packName.indexOf(LSystem.DOT) == -1) {
					start = packName.indexOf(LSystem.BRACKET_START);
					end = packName.indexOf(LSystem.BRACKET_END);
					String idxName = packName.substring(start + 1, end);
					packName = packName.substring(0, start);
					if (json.containsKey(packName)) {
						o = json.getObject(packName);
						if (o != null && o instanceof Json.Array) {
							Json.Array arrays = (Json.Array) o;
							int idx = 0;
							try {
								idx = (int) Double.parseDouble(idxName);
							} catch (Throwable ex) {
								idx = 0;
							}
							if (idx < arrays.length()) {
								o = arrays.getObject(idx);
							} else {
								o = null;
							}
						}
					}
				} else {
					String[] split = StringUtils.split(packName, LSystem.DOT);
					Object obj = null;
					for (String n : split) {
						if (obj == null) {
							if (n.indexOf(LSystem.BRACKET_START) != -1 && n.indexOf(LSystem.BRACKET_END) != -1) {
								if (n.indexOf(LSystem.DOT) == -1) {
									start = n.indexOf(LSystem.BRACKET_START);
									end = n.indexOf(LSystem.BRACKET_END);
									String idxName = n.substring(start + 1, end);
									n = n.substring(0, start);
									if (json.containsKey(n)) {
										obj = json.getObject(n);
										if (obj != null && obj instanceof Json.Array) {
											Json.Array arrays = (Json.Array) obj;
											int idx = 0;
											try {
												idx = (int) Double.parseDouble(idxName);
											} catch (Throwable ex) {
												idx = 0;
											}
											if (idx < arrays.length()) {
												obj = arrays.getObject(idx);
											} else {
												obj = null;
											}
										}
									}
								}
							} else {
								if (packName.indexOf(LSystem.DOT) == -1) {
									if (o != null && o instanceof Json.Object) {
										if (json.containsKey(packName)) {
											o = json.getObject(packName);
										}
									}
								} else {
									if (o != null && o instanceof Json.Object) {
										String[] splits = StringUtils.split(packName, LSystem.COMMA);
										Object v = null;
										for (String s : splits) {
											if (v == null) {
												if (json.containsKey(s)) {
													v = json.getObject(s);
												}
											} else {
												if (v instanceof Json.Object) {
													if (((Json.Object) v).containsKey(s)) {
														v = ((Json.Object) v).getObject(s);
													}
												}
											}
										}
									}
								}
							}
						} else {
							if (n.indexOf(LSystem.BRACKET_START) != -1 && n.indexOf(LSystem.BRACKET_END) != -1) {
								if (n.indexOf(LSystem.DOT) == -1) {
									start = n.indexOf(LSystem.BRACKET_START);
									end = n.indexOf(LSystem.BRACKET_END);
									String idxName = n.substring(start + 1, end);
									n = n.substring(0, start);
									if (((Json.Object) obj).containsKey(n)) {
										obj = ((Json.Object) obj).getObject(n);
										if (obj != null && obj instanceof Json.Array) {
											Json.Array arrays = (Json.Array) obj;
											int idx = 0;
											try {
												idx = (int) Double.parseDouble(idxName);
											} catch (Throwable ex) {
												idx = 0;
											}
											if (idx < arrays.length()) {
												obj = arrays.getObject(idx);
											} else {
												obj = null;
											}
										}
									}
								}
							} else if (n.indexOf(LSystem.DOT) == -1) {
								if (obj != null && obj instanceof Json.Object) {
									if (((Json.Object) obj).containsKey(n)) {
										obj = ((Json.Object) obj).getObject(n);
									}
								}
							} else {
								if (obj != null && obj instanceof Json.Object) {
									String[] splits = StringUtils.split(n, LSystem.COMMA);
									Object v = null;
									for (String s : splits) {
										if (v == null) {
											if (((Json.Object) obj).containsKey(s)) {
												v = ((Json.Object) obj).getObject(s);
											}
										} else {
											if (v instanceof Json.Object) {
												if (((Json.Object) v).containsKey(s)) {
													v = ((Json.Object) v).getObject(s);
												}
											}
										}
									}
								}
							}

						}
					}
					o = obj;
				}

			} else {
				if (packName.indexOf(LSystem.DOT) == -1) {
					if (o != null && o instanceof Json.Object) {
						if (json.containsKey(packName)) {
							o = json.getObject(packName);
						}
					}
				} else {
					if (o != null && o instanceof Json.Object) {
						String[] split = StringUtils.split(packName, LSystem.COMMA);
						Object v = null;
						for (String n : split) {
							if (v == null) {
								if (json.containsKey(n)) {
									v = json.getObject(n);
								}
							} else {
								if (v instanceof Json.Object) {
									if (((Json.Object) v).containsKey(n)) {
										v = ((Json.Object) v).getObject(n);
									}
								}
							}
						}
					}
				}
			}
		}
		return o;
	}

	private Object getVarVal(String vname) throws ScriptException {
		if (vname.indexOf(LSystem.SINGLE_QUOTE) == 0 || vname.indexOf(LSystem.DOUBLE_QUOTES) == 0) {
			if (vname.length() > 2) {
				return vname.substring(1, vname.length() - 1);
			}
			return vname;
		}
		if (!Character.isLetter(vname.charAt(0))) {
			handleError(UNKOWN);
			return 0;
		}
		Object o = null;

		for (int i = 0; i < vars.size(); i++) {
			ArrayMap tm = vars.get(i);
			if (tm.containsKey(vname)) {
				o = tm.get(vname);
			}
		}

		if (o == null) {
			o = findVar(vname);
			if (o != null) {
				Object[] result = (Object[]) o;
				String method = (String) result[1];
				Object value = result[2];
				if (value instanceof Json.Object) {
					o = queryJson(value, vname, method);
				} else {
					o = value;
				}
			}
		}

		if (o == null) {
			o = LSystem.UNKNOWN;
		}
		debug("Get var: " + o);
		return o;
	}

	public RocFunctions getFunctions() {
		return _rocFunctions;
	}

	public void setFunctions(RocFunctions roc) {
		this._rocFunctions = roc;
	}

	/**
	 * 在脚本外本注入变量
	 * 
	 * @param name
	 * @param obj
	 */
	public void addVar(String name, Object obj) {
		if (vars != null) {
			vars.last().put(name, obj);
		}
	}

	/**
	 * 获得指定名称的脚本变量
	 * 
	 * @param name
	 * @return
	 */
	public Object getLastVar(String name) {
		if (vars != null) {
			return vars.last().get(name);
		}
		return null;
	}

	/**
	 * 获得指定名称的脚本变量
	 * 
	 * @param name
	 * @return
	 */
	public Object getFirstVar(String name) {
		if (vars != null) {
			return vars.first().get(name);
		}
		return null;
	}

	/**
	 * 获得指定名称的脚本变量
	 * 
	 * @param name
	 * @return
	 */
	public Object getVar(String name) {
		if (vars != null) {
			Object result = null;
			for (; vars.hashNext();) {
				ArrayMap map = vars.next();
				if (map != null) {
					result = map.get(name);
					if (result != null) {
						return result;
					}
				}
			}
			vars.stopNext();
			return vars.last().get(name);
		}
		return null;
	}

	@Override
	public String toString() {
		return "{" + new String(_cmdcontexts) + "}";
	}
}
