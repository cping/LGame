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
package loon.utils;

public class Dice {

	public static int roll(int sides) {
		return MathUtils.nextInt(1, sides + 1);
	}

	public static int threeD6() {
		return MathUtils.nextInt(1, 6) + MathUtils.nextInt(1, 6) + MathUtils.nextInt(1, 6);
	}

	public static int D6() {
		return MathUtils.nextInt(1, 6);
	}

	public static int D10() {
		return MathUtils.nextInt(1, 10);
	}

	public static int D20() {
		return MathUtils.nextInt(1, 20);
	}

	public static int[] D20(int count) {
		int[] rolls = new int[count];
		for (int i = 0; i < count; i++) {
			rolls[i] = D20();
		}
		return rolls;
	}

}
