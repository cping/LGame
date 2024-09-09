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
package org.test;

import loon.Stage;
import loon.utils.timer.StepList;

public class StepTest extends Stage {

	@Override
	public void create() {
		StepList steps = getSteps();

		// 基础计步
		steps.add(d -> {
			System.out.println("base loop:" + d);
			// 返回true终止循环
			return false;
		});
		// 时间记步
		steps.add((d, f) -> {
			System.out.println("timer loop:" + d + "," + f);
			return false;
		}, 1f);
		// 按帧计步
		steps.add((d, f, mf) -> {
			System.out.println("frame loop:" + d + "," + f);
			return false;
		}, 10);
	}

}
