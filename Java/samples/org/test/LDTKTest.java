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
		//加载ldtk地图
		LDTKMap map = new LDTKMap("example.ldtk");
		//根据数据名称自行转换自定义类型为指定对象(我知道java反射可以自动创建类,问题是反射跨平台坑多啊，所以实现接口手动转换吧-_-……)
		/*
		 * map.getLDTKTypes().setConverFilter((name,obj)->{ return obj; });
		 */
		drawable((g, x, y) -> {
			//渲染图层level3
			map.getLevel("Level3").draw(g);
		});

	}

}
