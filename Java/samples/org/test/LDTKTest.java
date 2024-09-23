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
import loon.action.map.ldtk.LDTKMap;

public class LDTKTest extends Stage {

	@Override
	public void create() {
		// 加载ldtk地图
		LDTKMap map = new LDTKMap("example.ldtk");
		// 根据数据名称自行转换自定义类型为指定对象(我知道java反射可以自动创建类,问题是反射跨平台坑多啊，所以实现接口手动转换吧-_-……)
		/*
		 * map.getLDTKTypes().setConverFilter((name,obj)->{ return obj; });
		 */
		/*
		 * drawable((g, x, y) -> { //渲染图层level3 map.getLevel("Level3").draw(g,25,25);
		 * });
		 */
		// System.out.println(map.getLevel("SomeLevel").getTileLayers().get(1).getField2D());

		// 设定需要显示的id名，可以填写多个
		map.setDrawLevelNames("SomeLevel");
		// 改变层级SomeLevel位置
		// map.getLevel("SomeLevel").pos(240, 0);
		// map.setScale(1.2f);
		// 渲染其它角色或图像可于此处
		map.drawable((g, x, y) -> {
			g.drawString("Testing", 80, 70);
		});
		// 设定显示的层名,可以填写多个
		// map.setDrawLevelNames("Level2");
		// 直接作为精灵添加到游戏中
		add(map);
		up((x, y) -> {
			// 返回level名SomeLevel下当前触屏像素点对应的layer层级1的type
			System.out.println(map.getLevel("SomeLevel").getPixelsAtFieldType(1, x, y));
		});

	}

}
