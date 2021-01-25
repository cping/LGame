/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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
package loon.geom;

import loon.LRelease;

public class LongTuple implements LRelease{

	public long val1;
	
	public long val2;

	public LongTuple() {
	}

	public LongTuple(long val) {
		val1 = val;
		val2 = val;
	}

	public LongTuple(long val1, long val2) {
		this.val1 = val1;
		this.val2 = val2;
	}

	public LongTuple set(long val1, long val2) {
		this.val1 = val1;
		this.val2 = val2;
		return this;
	}

	public LongTuple reverse() {
		long swap = val1;
		val1 = val2;
		val2 = swap;
		return this;
	}
	
	@Override
	public String toString() {
		return "(" + val1 + ',' + val2 + ")";
	}

	@Override
	public void close() {
		this.val1 = this.val2 = 0;
	}

}
