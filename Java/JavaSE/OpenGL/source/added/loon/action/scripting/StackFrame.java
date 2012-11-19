package loon.action.scripting;

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
public class StackFrame {

	private Script script;

	private int count = 0;

	private int wait = 0;

	private int programCounter = 0;

	private Function currentFunction;

	public StackFrame(Script script) {
		this.script = script;
		this.programCounter = 0;
	}

	public void newTick() {
		count++;
	}

	public boolean hasNext() {
		return ((wait <= count) && (programCounter < script.size()));
	}

	public Function next() {
		currentFunction = script.getFunction(programCounter);
		if (currentFunction instanceof WaitFunction) {
			count = 0;
			wait = ((WaitFunction) currentFunction).getWait();
		}
		programCounter++;
		return currentFunction;
	}

	public boolean isComplete() {
		return script == null ? true : (programCounter >= script.size());
	}

	public Script getScript() {
		return script;
	}

}
