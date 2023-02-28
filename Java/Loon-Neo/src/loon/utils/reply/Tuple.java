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
package loon.utils.reply;

public class Tuple<V> {

	public V val1;

	public V val2;

	public Tuple() {
	}

	public Tuple(V val) {
		val1 = val;
		val2 = val;
	}

	public Tuple(V val1, V val2) {
		this.val1 = val1;
		this.val2 = val2;
	}

	public void set(V val1, V val2) {
		this.val1 = val1;
		this.val2 = val2;
	}

	public Tuple<V> reverse() {
		V swap = val1;
		val1 = val2;
		val2 = swap;

		return this;
	}
}
