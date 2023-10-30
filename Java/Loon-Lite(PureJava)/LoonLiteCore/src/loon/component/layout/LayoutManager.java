/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.component.layout;

import loon.LObject;
import loon.Screen;
import loon.action.ActionBind;
import loon.component.LClickButton;
import loon.component.LComponent;
import loon.component.LContainer;
import loon.events.ClickListener;
import loon.geom.BoxSize;
import loon.geom.Circle;
import loon.geom.Line;
import loon.geom.Point;
import loon.geom.SizeValue;
import loon.geom.Triangle2f;
import loon.geom.Vector2f;
import loon.utils.MathUtils;
import loon.utils.TArray;

public abstract class LayoutManager {

	/**
	 * 构建一个三角区域,让集合中的动作元素尽可能填充这一三角区域
	 * 
	 * @param root
	 * @param objs
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public final static void elementsTriangle(final Screen root, final TArray<ActionBind> objs, float x, float y,
			float w, float h) {
		elementsTriangle(root, objs, Triangle2f.at(x, y, w, h), 1, 0f, 0f);
	}

	/**
	 * 构建一个三角区域,让集合中的动作元素尽可能填充这一三角区域
	 * 
	 * @param root
	 * @param objs
	 * @param triangle
	 * @param stepRate
	 */
	public final static void elementsTriangle(final Screen root, final TArray<ActionBind> objs, Triangle2f triangle,
			int stepRate) {
		elementsTriangle(root, objs, triangle, stepRate, 0f, 0f);
	}

	/**
	 * 构建一个三角区域,让集合中的动作元素尽可能填充这一三角区域
	 * 
	 * @param root
	 * @param objs
	 * @param triangle
	 * @param stepRate
	 * @param offsetX
	 * @param offsetY
	 */
	public final static void elementsTriangle(final Screen root, final TArray<ActionBind> objs, Triangle2f triangle,
			int stepRate, float offsetX, float offsetY) {
		TArray<Point> p1 = Line.at(triangle.getX1(), triangle.getY1(), triangle.getY1(), triangle.getY2())
				.getBresenhamPoints(stepRate);
		TArray<Point> p2 = Line.at(triangle.getX2(), triangle.getY2(), triangle.getX3(), triangle.getY3())
				.getBresenhamPoints(stepRate);
		TArray<Point> p3 = Line.at(triangle.getX3(), triangle.getY3(), triangle.getX1(), triangle.getY1())
				.getBresenhamPoints(stepRate);
		p1.pop();
		p2.pop();
		p3.pop();

		TArray<Point> list = new TArray<Point>(p1);
		list.addAll(p2);
		list.addAll(p3);

		int step = p1.size / objs.size;
		int pl = 0;
		float newX = 0;
		float newY = 0;
		for (int i = 0; i < objs.size; i++) {
			ActionBind obj = objs.get(i);
			Point point = list.get(MathUtils.floor(pl));
			newX = point.x + offsetX;
			newY = point.y + offsetY;
			obj.setLocation(newX, newY);
			if (!root.contains(obj)) {
				root.add(obj);
			}
			pl += step;
		}
	}

	/**
	 * 构建一个线性区域,让集合中的动作元素延续这一线性对象按照指定的初始坐标到完结坐标线性排序
	 * 
	 * @param root
	 * @param objs
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public final static void elementsLine(final Screen root, final TArray<ActionBind> objs, float x1, float y1,
			float x2, float y2) {
		elementsLine(root, objs, x1, y1, x2, y2, 2f, 2f);
	}

	/**
	 * 构建一个线性区域,让集合中的动作元素延续这一线性对象按照指定的初始坐标到完结坐标线性排序
	 * 
	 * @param root
	 * @param objs
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param offsetX
	 * @param offsetY
	 */
	public final static void elementsLine(final Screen root, final TArray<ActionBind> objs, float x1, float y1,
			float x2, float y2, float offsetX, float offsetY) {
		elementsLine(root, objs, Line.at(x1, y1, x2, y2), offsetX, offsetY);
	}

	/**
	 * 构建一个线性区域,让集合中的动作元素延续这一线性对象按照指定的初始坐标到完结坐标线性排序
	 * 
	 * @param root
	 * @param objs
	 * @param line
	 * @param offsetX
	 * @param offsetY
	 */
	public final static void elementsLine(final Screen root, final TArray<ActionBind> objs, Line line, float offsetX,
			float offsetY) {
		Vector2f pos = line.getDirectionValue();
		int size = objs.size;
		float newX = line.x;
		float newY = line.y;
		float offX = pos.x * offsetX;
		float offY = pos.y * offsetY;
		float locX = 0;
		float locY = 0;
		for (int i = 0; i < size; i++) {
			ActionBind obj = objs.get(i);
			locX = pos.x * obj.getWidth();
			locY = pos.y * obj.getHeight();
			newX += (locX + offX);
			newY += (locY + offY);
			obj.setLocation(newX - locX, newY - locY);
			if (!root.contains(obj)) {
				root.add(obj);
			}
		}
	}

	/**
	 * 构建一个圆形区域,让集合中的动作元素围绕这一圆形对象按照指定的startAngle到endAngle范围环绕
	 * 
	 * @param root
	 * @param objs
	 * @param cx
	 * @param cy
	 * @param radius
	 */
	public final static void elementsCircle(final Screen root, final TArray<ActionBind> objs, float cx, float cy,
			float radius) {
		elementsCircle(root, objs, Circle.at(cx, cy, radius), -1f, -1f, 0f, 0f);
	}

	/**
	 * 构建一个圆形区域,让集合中的动作元素围绕这一圆形对象按照指定的startAngle到endAngle范围环绕
	 * 
	 * @param root
	 * @param objs
	 * @param circle
	 * @param startAngle
	 * @param endAngle
	 */
	public final static void elementsCircle(final Screen root, final TArray<ActionBind> objs, Circle circle,
			float startAngle, float endAngle) {
		elementsCircle(root, objs, circle, startAngle, endAngle, 0f, 0f);
	}

	/**
	 * 构建一个圆形区域,让集合中的动作元素围绕这一圆形对象按照指定的startAngle到endAngle范围环绕
	 * 
	 * @param root
	 * @param objs
	 * @param circle
	 * @param startAngle
	 * @param endAngle
	 * @param offsetX
	 * @param offsetY
	 */
	public final static void elementsCircle(final Screen root, final TArray<ActionBind> objs, Circle circle,
			float startAngle, float endAngle, float offsetX, float offsetY) {
		if (startAngle == -1f) {
			startAngle = 0;
		}
		if (endAngle == -1f) {
			endAngle = 6.28f;
		}
		int size = objs.size;
		float angle = startAngle;
		float angleStep = (endAngle - startAngle) / size;
		for (int i = 0; i < size; i++) {
			float newX = circle.x + ((circle.getRadius() * MathUtils.cos(angle)) + circle.getRadius());
			float newY = circle.y + ((circle.getRadius() * MathUtils.sin(angle)) + circle.getRadius());
			ActionBind obj = objs.get(i);
			obj.setLocation(newX + offsetX, newY + offsetY);
			if (!root.contains(obj)) {
				root.add(obj);
			}
			angle += angleStep;
		}
	}

	/**
	 * 把指定动作对象进行布局在指定的RectBox范围内部署,并注入Screen
	 * 
	 * @param root
	 * @param objs
	 * @param rectView
	 */
	public final static void elements(final Screen root, final TArray<ActionBind> objs, BoxSize rectView) {
		elements(root, objs, rectView, 1f, 1f);
	}

	/**
	 * 把指定动作对象进行布局在指定的RectBox范围内部署,并注入Screen
	 * 
	 * @param root
	 * @param objs
	 * @param rectView
	 * @param cellWidth
	 * @param cellHeight
	 */
	public final static void elements(final Screen root, final TArray<ActionBind> objs, BoxSize rectView,
			float cellWidth, float cellHeight) {
		elements(root, objs, rectView, 1f, 1f, 2f, 2f);
	}

	/**
	 * 把指定动作对象进行布局在指定的RectBox范围内部署,并注入Screen
	 * 
	 * @param root       Screen对象
	 * @param objs       要布局的对象集合
	 * @param rectView   显示范围
	 * @param cellWidth  单独对象的默认width(如果对象有width,并且比cellWidth大,则以对象自己的为主)
	 * @param cellHeight 单独对象的默认height(如果对象有width,并且比cellWidth大,则以对象自己的为主)
	 * @param offsetX    显示坐标偏移x轴
	 * @param offsetY    显示坐标偏移y轴
	 */
	public final static void elements(final Screen root, final TArray<ActionBind> objs, BoxSize rectView,
			float cellWidth, float cellHeight, float offsetX, float offsetY) {
		float blockWidth;
		float blockHeight;
		float x = rectView.getX(), y = rectView.getY();
		for (int i = 0; i < objs.size; i++) {
			ActionBind obj = objs.get(i);
			blockWidth = MathUtils.max(cellWidth, obj.getWidth());
			blockHeight = MathUtils.max(cellHeight, obj.getHeight());
			obj.setLocation(x + offsetX, y + offsetY);
			if (!root.contains(obj)) {
				root.add(obj);
			}
			y += blockHeight + offsetY;
			if (y >= rectView.getHeight()) {
				y = rectView.getY();
				x += blockWidth + offsetX;
			}
			if (x >= rectView.getWidth()) {
				x = rectView.getX();
			}
		}
	}

	public final static void elements(final Screen root, final TArray<LObject<?>> objs, int sx, int sy, int cellWidth,
			int cellHeight, int maxHeight) {
		final int offsetX = 2;
		final int offsetY = 2;
		elements(root, objs, sx, sy, cellWidth, cellHeight, offsetX, offsetY, maxHeight);
	}

	public final static void elements(final Screen root, final TArray<LObject<?>> objs, int sx, int sy, int cellWidth,
			int cellHeight) {
		final int offsetX = 2;
		final int offsetY = 2;
		elements(root, objs, sx, sy, cellWidth, cellHeight, offsetX, offsetY,
				(root.getHeight() - cellHeight - offsetY));
	}

	public final static void elements(final Screen root, final TArray<LObject<?>> objs, int sx, int sy, int cellWidth,
			int cellHeight, int offsetX, int offsetY, int maxHeight) {
		int x = sx;
		int y = sy;
		for (int i = 0; i < objs.size; i++) {
			LObject<?> obj = objs.get(i);
			obj.setLocation(x + offsetX, y + offsetY);
			if (!root.contains(obj)) {
				root.add(obj);
			}
			y += cellHeight + offsetY;
			if (y >= maxHeight) {
				y = sy;
				x += cellWidth + offsetX;
			}
		}
	}

	public final static TArray<LClickButton> elementButtons(final Screen root, final String[] names, int sx, int sy,
			int cellWidth, int cellHeight, ClickListener listener, int maxHeight) {
		final int offsetX = 2;
		final int offsetY = 2;
		return elementButtons(root, names, sx, sy, cellWidth, cellHeight, offsetX, offsetY, listener, maxHeight);
	}

	public final static TArray<LClickButton> elementButtons(final Screen root, final String[] names, int sx, int sy,
			int cellWidth, int cellHeight, ClickListener listener) {
		final int offsetX = 2;
		final int offsetY = 2;
		return elementButtons(root, names, sx, sy, cellWidth, cellHeight, offsetX, offsetY, listener,
				(root.getHeight() - cellHeight - offsetY - sy));
	}

	public final static TArray<LClickButton> elementButtons(final Screen root, final String[] names, int sx, int sy,
			int cellWidth, int cellHeight, int offsetX, int offsetY, ClickListener listener, int maxHeight) {
		int x = sx;
		int y = sy;
		TArray<LClickButton> clicks = new TArray<LClickButton>(names.length);
		for (int i = 0; i < names.length; i++) {
			LClickButton click = LClickButton.make(names[i], cellWidth, cellHeight);
			click.setLocation(x + offsetX, y + offsetY);
			click.S(listener);
			if (!root.contains(click)) {
				root.add(click);
			}
			clicks.add(click);
			y += cellHeight + offsetY;
			if (y >= maxHeight) {
				y = sy;
				x += cellWidth + offsetX;
			}
		}
		return clicks;
	}

	protected boolean _allow = true;

	public final LayoutManager setChangeSize(boolean allow) {
		this._allow = allow;
		return this;
	}

	public final boolean isAllowChangeSize() {
		return _allow;
	}

	public final LayoutManager layoutElements(final Screen root, final LComponent... children) {
		int size = children.length;
		LayoutPort[] ports = new LayoutPort[size];
		for (int i = 0; i < size; i++) {
			ports[i] = children[i].getLayoutPort();
		}
		return layoutElements(root.getLayoutPort(), ports);
	}

	public final LayoutManager layoutElements(final LContainer root, final LComponent... children) {
		int size = children.length;
		LayoutPort[] ports = new LayoutPort[size];
		for (int i = 0; i < size; i++) {
			ports[i] = children[i].getLayoutPort();
		}
		layoutElements(root.getLayoutPort(), ports);
		return this;
	}

	public abstract LayoutManager layoutElements(LayoutPort root, LayoutPort... children);

	abstract SizeValue calculateConstraintWidth(LayoutPort root, TArray<LayoutPort> children);

	abstract SizeValue calculateConstraintHeight(LayoutPort root, TArray<LayoutPort> children);
}
