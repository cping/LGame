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
import loon.TextureNodeType;
import loon.action.sprite.Entity;
import loon.canvas.LColor;
import loon.geom.Shape;
import loon.geom.ShapeNodeType;

public class EntityToShapeTest extends Stage {

	@Override
	public void create() {
		// 构建一个Entity,位置
		Entity entity = create(TextureNodeType.Entity, "a4.png");
		// 居中显示
		centerOn(entity);
		// 注入舞台
		add(entity);
		// 获得一个基于Entity的多边形形状
		Shape newShape = entity.getShape(ShapeNodeType.Polygon);
		// 构建一个舞台渲染器
		drawable((g, x, y) -> {
			//绘制Shape，颜色为红
			g.draw(newShape, LColor.red);
		});
	}

}
