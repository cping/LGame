/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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
import loon.action.map.PathMove;
import loon.action.sprite.Sprite;
import loon.geom.Vector2f;

public class PathMoveTest extends Stage {

	@Override
	public void create() {
		// 构建一个足球精灵
		Sprite spr = new Sprite("assets/ball.png");
		spr.pos(55, 55);
		add(spr);

		// 构建一个Vector2f对象移动控制器,获得精灵的Vector2f,移动向200,200位置,每帧移动2个像素
		PathMove moving = new PathMove(spr.getPosition(), Vector2f.at(200, 200), 2f);

		// 只能向一个方向移动
		// PathMove moving = new PathMove(spr.getPosition(), Vector2f.at(200,
		// 200),2f,true);
		// 是否水平移动
		// moving.setHorizontal(true);
		// 提交组件到Loon循环中去
		moving.submit();

		// stage关闭时注销moving对象
		putRelease(moving);
		add(MultiScreenTest.getBackButton(this, 2));
	}

}
