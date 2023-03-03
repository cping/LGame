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
package application;

import loon.utils.MathUtils;

public class Test2 {

	public static float interval(float src, float dst, float v, float found, float invalid) {
		if ((src + v == dst) || (src - v) == dst) {
			return found;
		}
		float result = (dst - src);
		if (result > 0) {
			return result <= v ? found : invalid;
		} else if (result < 0) {
			return (-result) <= v ? found : invalid;
		} else {
			return found;
		}
	}

	public static void main(String[] args) {
		float a = 1.11f;
		float b = 1f;
		System.out.println(interval(a, b, 0.1f, 3, 4));
	}

}
