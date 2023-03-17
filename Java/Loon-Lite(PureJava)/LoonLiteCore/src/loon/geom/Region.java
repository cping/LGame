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

import loon.utils.MathUtils;
import loon.utils.StringKeyValue;

/**
 * 用以保存两个数值的区间
 */
public class Region {

	private int start;
	private int end;

	public Region(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public Region setStart(int start) {
		this.start = start;
		return this;
	}

	public Region setEnd(int end) {
		this.end = end;
		return this;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public int random() {
		return (int) (start + (MathUtils.random() * (end - start)));
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("Region");
		builder.kv("start", start)
		.comma()
		.kv("end", end);
		return builder.toString();
	}

}
