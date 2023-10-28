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
package loon.geom;

import loon.LRelease;
import loon.Nullable;

public class BooleanValue implements LRelease {

	private boolean value = false;

	public BooleanValue() {
		this(false);
	}

	public BooleanValue(boolean v) {
		this.set(v);
	}

	public boolean update(boolean v) {
		set(v);
		return v;
	}

	public BooleanValue set(boolean res) {
		this.value = res;
		return this;
	}

	public boolean get() {
		return value;
	}

	public boolean result() {
		return value;
	}
	
	public BooleanValue cpy() {
		return new BooleanValue(value);
	}
	
	public Nullable<Boolean> toNullable(){
		return new Nullable<Boolean>(value);
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@Override
	public void close() {
		this.value = false;
	}
}
