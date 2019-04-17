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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
package loon.action.avg.drama;

import loon.LRelease;
import loon.LSystem;
import loon.utils.Calculator;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.StringUtils;
import loon.utils.TArray;

/**
 * 指令分析器
 *
 */
public abstract class Conversion implements Expression {

	private static final int MAX_LENGTH = 128;

	public static final int STACK_VARIABLE = 11;

	public static final int STACK_RAND = -1;

	public static final int STACK_NUM = 0;

	final Exp exp = new Exp();

	public static final boolean isCondition(String s) {
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

	public static final boolean isOperator(char c) {
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

	public static final String updateOperator(String context) {
		if (context != null && (StringUtils.startsWith(context, '"') || StringUtils.startsWith(context, '\''))) {
			return context;
		}
		int size = context.length();
		StringBuffer sbr = new StringBuffer(size * 2);
		boolean notFlag = false;
		boolean operator;
		for (int i = 0; i < size; i++) {
			char ch = context.charAt(i);
			if (ch == '"' || ch == '\'') {
				notFlag = !notFlag;
			}
			if (notFlag) {
				sbr.append(ch);
				continue;
			}
			if (ch == ' ') {
				if (i > 0 && context.charAt(i - 1) != ' ') {
					sbr.append(FLAG);
				}
			} else {
				operator = isOperator(ch);
				if (i > 0) {
					if (operator && !isOperator(context.charAt(i - 1))) {
						if (context.charAt(i - 1) != ' ') {
							sbr.append(FLAG);
						}
					}
				}
				sbr.append(ch);
				if (i < size - 1) {
					if (operator && !isOperator(context.charAt(i + 1))) {
						if (context.charAt(i + 1) != ' ') {
							sbr.append(FLAG);
						}
					}
				}
			}
		}
		return sbr.toString().trim();
	}

	public static final TArray<String> splitToList(final String string, final char tag) {
		return new TArray<String>(StringUtils.split(string, tag));
	}

	public static class Exp implements LRelease {

		private ObjectMap<String, Compute> computes = new ObjectMap<String, Compute>();

		private char[] expChr;

		private boolean closed;

		private boolean exp(String exp) {
			return exp.indexOf("+") != -1 || exp.indexOf("-") != -1 || exp.indexOf("*") != -1 || exp.indexOf("/") != -1
					|| exp.indexOf("%") != -1;
		}

		public float parse(Object v) {
			return parse(v.toString());
		}

		public float parse(String v) {
			if (!exp(v)) {
				if (MathUtils.isNan(v)) {
					return Float.parseFloat(v);
				} else {
					LSystem.error("Conversion " + v + " not parse !");
				}
			}
			return eval(v);
		}

		private void evalFloatValue(Compute compute, int stIdx, int lgt, float sign) {
			if (expChr[stIdx] == '$') {
				String label = new String(expChr, stIdx + 1, lgt - 1);
				if (label.equals("rand")) {
					compute.push(0, STACK_RAND);
				} else {
					int idx;
					try {
						idx = Integer.valueOf(label) - 1;
					} catch (NumberFormatException e) {
						compute.push(0, STACK_NUM);
						return;
					}
					compute.push(0, STACK_VARIABLE + idx);
				}
			} else {
				try {
					compute.push(Float.parseFloat(new String(expChr, stIdx, lgt)) * sign, STACK_NUM);
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
						compute.setOperator(Calculator.MULTIPLY);
						break;
					case '/':
						evalExp(compute, stIdx, op[0]);
						evalExp(compute, op[0] + 1, edIdx);
						compute.setOperator(Calculator.DIVIDE);
						break;
					case '%':
						evalExp(compute, stIdx, op[0]);
						evalExp(compute, op[0] + 1, edIdx);
						compute.setOperator(Calculator.MODULO);
						break;
					}
				}
			} else {
				if (op[1] == stIdx) {
					switch (expChr[op[1]]) {
					case '-':
						evalFloatValue(compute, stIdx + 1, edIdx - stIdx - 1, -1);
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
						compute.setOperator(Calculator.ADD);
						break;
					case '-':
						evalExp(compute, stIdx, op[1]);
						evalExp(compute, op[1] + 1, edIdx);
						compute.setOperator(Calculator.SUBTRACT);
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
				case Calculator.ADD:
					return n1 + n2;
				case Calculator.SUBTRACT:
					return n1 - n2;
				case Calculator.MULTIPLY:
					return n1 * n2;
				case Calculator.DIVIDE:
					return n1 / n2;
				case Calculator.MODULO:
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
						stack[stkIdx] = MathUtils.random();
						stkIdx++;
						break;
					default:
						if (opr[i] >= STACK_VARIABLE) {
							stkIdx++;
						} else {
							stack[stkIdx - 2] = calcOp(opr[i], stack[stkIdx - 2], stack[stkIdx - 1]);
							stkIdx--;
						}
						break;
					}
				}
				return stack[0];
			}
		}

		public boolean isClosed() {
			return closed;
		}

		@Override
		public void close() {
			if (computes != null) {
				computes.clear();
			}
			closed = true;
		}

	}

}
