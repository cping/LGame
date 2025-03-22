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
import loon.action.sprite.bone.SkeletonAnimation;
import loon.action.sprite.bone.SkeletonLoader;

public class BoneAniTest extends Stage {

	@Override
	public void create() {

		// 加载骨骼动画1
		SkeletonLoader s1 = new SkeletonLoader("assets/bone/sani.txt", "assets/bone/s1m.png", "assets/bone/s2m.png");
		SkeletonAnimation s1a = s1.getSkeletonAnimation("sword");
		SkeletonAnimation s1play = s1a.createSubAnimationY(310);

		s1play.setLoop(true);

		// 加载骨骼动画2
		SkeletonLoader s2 = new SkeletonLoader("assets/bone/sanj.txt", "assets/bone/s1w.png", "assets/bone/s2w.png");
		SkeletonAnimation s2a = s2.getSkeletonAnimation("sword");
		SkeletonAnimation s2play = s2a.createSubAnimationY(310);

		s2play.setLoop(true);

		// 渲染到屏幕
		drawable((g, x, y) -> {
			s1play.update(0);
			s1play.draw(g, 125, 205);
			s2play.update(0);
			s2play.draw(g, 345, 205, 1);
		});

	}

}