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
import loon.action.RotateTo;
import loon.action.sprite.effect.PixelGossipEffect;
import loon.canvas.LColor;

public class TaichiTest extends Stage{

	@Override
	public void create() {

		setBackground(LColor.yellow);
		
		PixelGossipEffect taichi = new PixelGossipEffect(0, 0, 200, 200);
		
		centerOn(taichi);
		// 构建一个旋转事件,速度4f
		RotateTo rotate = new RotateTo(360, 4f);
		// 循环旋转
		rotate.loop(true);
		// 让效果自身启动动画事件
		taichi.selfAction().event(rotate).start();
		add(taichi);
	
		add(MultiScreenTest.getBackButton(this, 2));
		
	}

}
