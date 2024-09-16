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
import loon.opengl.light.LightShapeSystem;

public class LightShapesTest extends Stage {

	@Override
	public void create() {

		// 构建一个光源几何图形管理器,光源图形大小20,物理空间位置大小与Stage大小一致
		final LightShapeSystem shapes = new LightShapeSystem(20, 0, 0, getWidth(), getHeight());
		// 添加一个举行光源对象到指定位置
		shapes.addLightRect(100, 75, 50, 50);
		shapes.addLightRect(200, 175, 50, 50);
		shapes.addLightRect(50, 150, 100, 25);
		// 添加一个不规则多边形光源
		shapes.addLightPoly(new float[] { 300, 200, 350, 225, 315, 225, 275, 250 });
		// 绘制当前光线几何图形管理系统中的遮挡关系到屏幕(渲染步骤非必需，可以优化成更恰当的画面渲染，或完全不显示，仅依据getLightShowShapes改变游戏即可)
		drawable((g, x, y) -> {
			shapes.drawDebug(g);
		});
		// 变更触屏位置为光源照射点
		up((x, y) -> {
			shapes.updateLight(x, y);
		});
		// 获得当前允许现实的图形(具体游戏中会根据此集合反馈结果采取行动，比如让集合内的对象攻击主角,而没有照射到的保持静止)
		// shapes.getLightShowShapes();
	}

}
