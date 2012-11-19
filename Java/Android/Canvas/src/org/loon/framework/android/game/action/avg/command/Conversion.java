package org.loon.framework.android.game.action.avg.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.loon.framework.android.game.core.LRelease;
import org.loon.framework.android.game.core.LSystem;
import org.loon.framework.android.game.utils.NumberUtils;
import org.loon.framework.android.game.utils.StringUtils;

/**
 * Copyright 2008 - 2011
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */

public abstract class Conversion implements Expression {

	private static final int MAX_LENGTH = 128;

	public static final int STACK_VARIABLE = 11;

	public static final int STACK_RAND = -1;

	public static final int STACK_NUM = 0;

	public static final int PLUS = 1;

	public static final int MINUS = 2;

	public static final int MULTIPLE = 3;

	public static final int DIVISION = 4;

	public static final int MODULO = 5;

	final Exp exp = new Exp();

	static boolean isCondition(String s) {
		if (s.equalsIgnoreCase("==")) {
			return true;
		} else if (s.equalsIgnoreCase("!=")) {
			return true;
		} else if (s.equalsIgnoreCase(">=")) {
			return true;
		} else if (s.equalsIgnoreCase("<=")) {
			return true;
		} else if (s.equalsIgnoreCase(">")) {
			return true;
		} else if (s.equalsIgnoreCase("<")) {
			return true;
		}
		return false;
	}

	static boolean isOperator(char c) {
		switch (c) {
		case '+':
			return true;
		case '-':
			return true;
		case '*':
			return true;
		case '/':
			return true;
		case '<':
			return true;
		case '>':
			return true;
		case '=':
			return true;
		case '%':
			return true;
		case '!':
			return true;
		}
		return false;
	}

	public static String updateOperator(String context) {
		if (context != null
				&& (context.startsWith("\"") || context.startsWith("'"))) {
			return context;
		}
		int size = context.length();
		StringBuffer sbr = new StringBuffer(size * 2);
		char[] chars = context.toCharArray();
		boolean notFlag = false;
		boolean operator;
		for (int i = 0; i < size; i++) {
			if (chars[i] == '"' || chars[i] == '\'') {
				notFlag = !notFlag;
			}
			if (notFlag) {
				sbr.append(chars[i]);
				continue;
			}
			if (chars[i] == ' ') {
				if (i > 0 && chars[i - 1] != ' ') {
					sbr.append(FLAG);
				}
			} else {
				operator = isOperator(chars[i]);
				if (i > 0) {
					if (operator && !isOperator(chars[i - 1])) {
						if (chars[i - 1] != ' ') {
							sbr.append(FLAG);
						}
					}
				}
				sbr.append(chars[i]);
				if (i < size - 1) {
					if (operator && !isOperator(chars[i + 1])) {
						if (chars[i + 1] != ' ') {
							sbr.append(FLAG);
						}
					}
				}
			}
		}
		return sbr.toString().trim();
	}

	public static List<String> splitToList(final String string, final String tag) {
		return Arrays.asList(StringUtils.split(string, tag));
	}

	public static class Exp implements LRelease {

		private HashMap<String, Compute> computes = new HashMap<String, Compute>();

		private char[] expChr;

		private boolean exp(String exp) {
			return exp.indexOf("+") != -1 || exp.indexOf("-") != -1
					|| exp.indexOf("*") != -1 || exp.indexOf("/") != -1
					|| exp.indexOf("%") != -1;
		}

		public float parse(Object v) {
			return parse(v.toString());
		}

		public float parse(String v) {
			if (!exp(v)) {
				if (NumberUtils.isNan(v)) {
					return Float.parseFloat(v);
				} else {
					throw new RuntimeException(v + " not parse !");
				}
			}
			return eval(v);
		}

		private void evalFloatValue(Compute compute, int stIdx, int lgt,
				float sign) {
			if (expChr[stIdx] == '$') {
				String label = new String(expChr, stIdx + 1, lgt - 1);
				if (label.equals("rand")) {
					compute.push(0, STACK_RAND);
				} else {
					int idx;
					try {
						idx = new Integer(label).intValue() - 1;
					} catch (NumberFormatException e) {
						compute.push(0, STACK_NUM);
						return;
					}
					compute.push(0, STACK_VARIABLE + idx);
				}
			} else {
				try {
					compute.push(new Float(new String(expChr, stIdx, lgt))
							.floatValue()
							* sign, STACK_NUM);
				} catch (NumberFormatException e) {
					compute.push(0, STACK_NUM);
				}
			}
		}

		private void evalExp(Compute compute, int stIdx, int edIdx) {
			int op[] = new int[] { -1, -1 };
			while (expChr[stIdx] == '(' && expChr[edIdx - 1] == ')') {
				stIdx++;
				edIdx--;
			}
			for (int i = edIdx - 1; i >= stIdx; i--) {
				char c = expChr[i];
				if (c == ')') {
					do {
						i--;
					} while (expChr[i] != '(');
				} else if (op[0] < 0 && (c == '*' || c == '/' || c == '%')) {
					op[0] = i;
				} else if (c == '+' || c == '-') {
					op[1] = i;
					break;
				}
			}
			if (op[1] < 0) {
				if (op[0] < 0) {
					evalFloatValue(compute, stIdx, edIdx - stIdx, 1);
				} else {
					switch (expChr[op[0]]) {
					case '*':
						evalExp(compute, stIdx, op[0]);
						evalExp(compute, op[0] + 1, edIdx);
						compute.setOperator(MULTIPLE);
						break;
					case '/':
						evalExp(compute, stIdx, op[0]);
						evalExp(compute, op[0] + 1, edIdx);
						compute.setOperator(DIVISION);
						break;
					case '%':
						evalExp(compute, stIdx, op[0]);
						evalExp(compute, op[0] + 1, edIdx);
						compute.setOperator(MODULO);
						break;
					}
				}
			} else {
				if (op[1] == stIdx) {
					switch (expChr[op[1]]) {
					case '-':
						evalFloatValue(compute, stIdx + 1, edIdx - stIdx - 1,
								-1);
						break;
					case '+':
						evalFloatValue(compute, stIdx + 1, edIdx - stIdx - 1, 1);
						break;
					}
				} else {
					switch (expChr[op[1]]) {
					case '+':
						evalExp(compute, stIdx, op[1]);
						evalExp(compute, op[1] + 1, edIdx);
						compute.setOperator(PLUS);
						break;
					case '-':
						evalExp(compute, stIdx, op[1]);
						evalExp(compute, op[1] + 1, edIdx);
						compute.setOperator(MINUS);
						break;
					}
				}
			}
		}

		public float eval(String exp) {
			Compute compute = computes.get(exp);
			if (compute == null) {
				expChr = new char[exp.length()];
				int ecIdx = 0;
				boolean skip = false;
				StringBuffer buf = new StringBuffer(exp);
				int depth = 0;
				boolean balance = true;
				char ch;
				for (int i = 0; i < buf.length(); i++) {
					ch = buf.charAt(i);
					switch (ch) {
					case ' ':
					case '\n':
						skip = true;
						break;
					case ')':
						depth--;
						if (depth < 0)
							balance = false;
						break;
					case '(':
						depth++;
						break;
					}
					if (skip) {
						skip = false;
					} else {
						expChr[ecIdx] = ch;
						ecIdx++;
					}
				}
				if (depth != 0 || !balance) {
					return 0;
				}
				compute = new Compute();
				evalExp(compute, 0, ecIdx);
				computes.put(exp, compute);
			}
			return compute.calc();
		}

		final private class Compute {

			private float[] num = new float[MAX_LENGTH];

			private int[] opr = new int[MAX_LENGTH];

			private int idx;

			private float[] stack = new float[MAX_LENGTH];

			public Compute() {
				idx = 0;
			}

			private float calcOp(int op, float n1, float n2) {
				switch (op) {
				case PLUS:
					return n1 + n2;
				case MINUS:
					return n1 - n2;
				case MULTIPLE:
					return n1 * n2;
				case DIVISION:
					return n1 / n2;
				case MODULO:
					return n1 % n2;
				}
				return 0;
			}

			public void setOperator(int op) {
				if (idx >= MAX_LENGTH) {
					return;
				}
				if (opr[idx - 1] == STACK_NUM && opr[idx - 2] == STACK_NUM) {
					num[idx - 2] = calcOp(op, num[idx - 2], num[idx - 1]);
					idx--;
				} else {
					opr[idx] = op;
					idx++;
				}
			}

			public void push(float nm, int vr) {
				if (idx >= MAX_LENGTH) {
					return;
				}
				num[idx] = nm;
				opr[idx] = vr;
				idx++;
			}

			public final float calc() {
				int stkIdx = 0;
				for (int i = 0; i < idx; i++) {
					switch (opr[i]) {
					case STACK_NUM:
						stack[stkIdx] = num[i];
						stkIdx++;
						break;
					case STACK_RAND:
						stack[stkIdx] = LSystem.random.nextFloat();
						stkIdx++;
						break;
					default:
						if (opr[i] >= STACK_VARIABLE) {
							stkIdx++;
						} else {
							stack[stkIdx - 2] = calcOp(opr[i],
									stack[stkIdx - 2], stack[stkIdx - 1]);
							stkIdx--;
						}
						break;
					}
				}
				return stack[0];
			}
		}

		public void dispose() {
			if (computes != null) {
				computes.clear();
			}
		}
	}

}