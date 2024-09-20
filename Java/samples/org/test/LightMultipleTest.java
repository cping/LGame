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
import loon.action.sprite.ImageBackground;
import loon.canvas.LColor;
import loon.opengl.light.Light2D;
import loon.opengl.light.PointLight;
import loon.opengl.light.Light2D.LightType;

public class LightMultipleTest extends Stage {

	@Override
	public void create() {

		// 设定背景精灵
		add(new ImageBackground("back1.png"));

		// 创建并获得精灵的公共光源，模式为多光源
		Light2D light2d = ELF().createGlobalLight(LightType.Multiple);

		// 设置光源位置
		PointLight light1 = new PointLight(135f, 136f, 0.14f);
		PointLight light2 = new PointLight(35f, 36f, 0.14f);
		PointLight light3 = new PointLight(75f, 236f, 0.24f);
		PointLight light4 = new PointLight(285f, 176f, 0.11f);

		// 注入光源
		light2d.addLights(light1, light2, light3, light4);
		// 光源摇曳
		light2d.setSway(true);
		light1.set(LColor.red, 99, 99, 0.3f);

		// 当在屏幕按下时
		up((x, y) -> {

			// 反转光源开启状态
			light2d.setEnabled(!light2d.isEnabled());

		});
	}

}