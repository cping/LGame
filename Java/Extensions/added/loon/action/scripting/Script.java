package loon.action.scripting;

import java.util.ArrayList;

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
public class Script {
	
	private String name;

	private ArrayList<Function> functions = new ArrayList<Function>();

	public static final int COMPLETE_SCRIPT = 0;

	public static final int COMPLETE_FUNCTION = 1;

	public static final int UNCOMPLETE_FUNCTION = 2;

	public String getName() {
		return name;
	}

	public Script(String name) {
		this.name = name;
	}

	public int size() {
		return functions.size();
	}

	public Function getFunction(int index) {
		return functions.get(index);
	}

	public void add(Function fun) {
		functions.add(fun);
	}
}
