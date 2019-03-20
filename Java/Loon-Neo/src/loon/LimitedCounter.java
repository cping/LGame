/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon;

/**
 * 一个限制计数次数的计数器
 */
public class LimitedCounter extends Counter {
	
	private int limit;

	public LimitedCounter(int limit) {
		this.limit = limit;
	}

	public int getLimit() {
		return limit;
	}

	@Override
	public int increment() {
		if (!isLimitReached()) {
			return super.increment();
		}
		return getValue();
	}

	public int valuesUntilLimitRemains() {
		return limit - getValue();
	}

	@Override
	public void clear() {
		super.clear();
	}

	public boolean isLimitReached() {
		return getValue() == limit;
	}

}
