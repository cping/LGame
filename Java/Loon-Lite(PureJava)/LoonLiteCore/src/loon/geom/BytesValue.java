/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
import loon.utils.ArrayByte;
import loon.utils.reply.Nullable;

public class BytesValue implements LRelease {

	private ArrayByte value;

	public BytesValue() {
		this(new ArrayByte(512));
	}

	public BytesValue(ArrayByte v) {
		this.set(v);
	}

	public boolean update(ArrayByte v) {
		set(v);
		return v != null;
	}

	public BytesValue set(ArrayByte v) {
		this.value = v;
		return this;
	}

	public ArrayByte result() {
		return value;
	}

	public ArrayByte cpy() {
		return value != null ? value.cpy() : null;
	}

	public Nullable<ArrayByte> toNullable(){
		return new Nullable<ArrayByte>(value);
	}
	
	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public void close() {
		value = null;
	}
}
