/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.physics;

import loon.core.geom.ShapeUtils;
import loon.core.geom.Triangle;
import loon.core.geom.Triangle2f;
import loon.core.geom.Vector2f;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class PhysicsUtils extends ShapeUtils {

	private static Vector2f tmp = new Vector2f();
	private static Vector2f[] vertices = new Vector2f[8];

	static {
		for (int i = 0; i < vertices.length; i++)
			vertices[i] = new Vector2f();
	}

	public static void createShape(Body body, FixtureDef fix, Triangle ts) {
		float[] triangle = new float[6];
		for (int i = 0; i < ts.getTriangleCount(); i++) {
			FixtureDef fixtureDef = new FixtureDef();
			PolygonShape polyShape = new PolygonShape();
			float[] pos = ts.getTrianglePoint(i, 0);
			triangle[0] = pos[0];
			triangle[1] = pos[1];
			pos = ts.getTrianglePoint(i, 1);
			triangle[2] = pos[0];
			triangle[3] = pos[1];
			pos = ts.getTrianglePoint(i, 2);
			triangle[4] = pos[0];
			triangle[5] = pos[1];
			polyShape.set(triangle);
			fixtureDef.density = fix.density;
			fixtureDef.friction = fix.friction;
			fixtureDef.isSensor = fix.isSensor;
			fixtureDef.restitution = fix.restitution;
			fixtureDef.filter.categoryBits = fix.filter.categoryBits;
			fixtureDef.filter.groupIndex = fix.filter.groupIndex;
			fixtureDef.filter.maskBits = fix.filter.maskBits;
			fixtureDef.friction = fix.friction;
			fixtureDef.shape = polyShape;
			body.createFixture(fixtureDef);
			polyShape.dispose();
		}
	}

	public static void createShape(Body body, PhysicsObject fix, Triangle ts) {
		float[] triangle = new float[6];
		for (int i = 0; i < ts.getTriangleCount(); i++) {
			FixtureDef fixtureDef = new FixtureDef();
			PolygonShape polyShape = new PolygonShape();
			float[] pos = ts.getTrianglePoint(i, 0);
			triangle[0] = pos[0];
			triangle[1] = pos[1];
			pos = ts.getTrianglePoint(i, 1);
			triangle[2] = pos[0];
			triangle[3] = pos[1];
			pos = ts.getTrianglePoint(i, 2);
			triangle[4] = pos[0];
			triangle[5] = pos[1];
			polyShape.set(triangle);
			fixtureDef.density = fix.density.get();
			fixtureDef.friction = fix.friction.get();
			fixtureDef.isSensor = fix.isSensor;
			fixtureDef.restitution = fix.restitution.get();
			fixtureDef.filter.categoryBits = fix.filter.categoryBits;
			fixtureDef.filter.groupIndex = fix.filter.groupIndex;
			fixtureDef.filter.maskBits = fix.filter.maskBits;
			fixtureDef.friction = fix.friction.get();
			fixtureDef.shape = polyShape;
			body.createFixture(fixtureDef);
			polyShape.dispose();
		}
	}

	public static void calculateShapeCenter(PolygonShape polygonShape,
			Vector2f center) {
		center.x = 0f;
		center.y = 0f;

		int vertexCount = polygonShape.getVertexCount();

		if (vertexCount == 0) {
			return;
		}

		for (int i = 0; i < vertexCount; i++) {
			polygonShape.getVertex(i, tmp);
			center.x += tmp.x;
			center.y += tmp.y;
		}

		center.x /= vertexCount;
		center.y /= vertexCount;
	}

	public static void translatePolygonShape(PolygonShape polygonShape,
			float tx, float ty) {
		if (polygonShape.getVertexCount() > vertices.length)
			throw new RuntimeException(
					"unexpected polygon shape length, should be less than "
							+ vertices.length);
		Vector2f[] newVertices = new Vector2f[polygonShape.getVertexCount()];
		for (int i = 0; i < polygonShape.getVertexCount(); i++) {
			polygonShape.getVertex(i, vertices[i]);
			vertices[i].add(tx, ty);
			newVertices[i] = vertices[i];
		}
		polygonShape.set(newVertices);
	}

	public static void translateCircleShape(CircleShape circleShape, float tx,
			float ty) {
		Vector2f position = circleShape.getPosition();
		position.add(tx, ty);
		circleShape.setPosition(position);
	}

	/**
	 * 创建一个圆形形状
	 * 
	 * @param width
	 * @return
	 */
	public static CircleShape createCircleShape(int width) {
		CircleShape circle = new CircleShape();
		circle.setRadius(width / 2f);
		return circle;
	}

	/**
	 * 创建一个方形形状
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public static PolygonShape createBoxShape(int width, int height) {
		PolygonShape poly = new PolygonShape();
		poly.setAsBox(width / 2f, height / 2f);
		return poly;
	}

	/**
	 * 创建一个三角形形状
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public static PolygonShape createTriangleShape(int width, int height) {
		PolygonShape poly = new PolygonShape();
		Triangle2f triangle = new Triangle2f();
		triangle.set(0, 0, width, height);
		poly.set(triangle.getVertexs());
		return poly;
	}

	/**
	 * 生成指定内容的FixtureDef
	 * 
	 * @param density
	 * @param restitution
	 * @param friction
	 * @return
	 */
	public static FixtureDef createFixtureDef(float density, float restitution,
			float friction) {
		return createFixtureDef(density, restitution, friction, false);
	}

	/**
	 * 生成指定内容的FixtureDef
	 * 
	 * @param density
	 * @param restitution
	 * @param friction
	 * @param isSensor
	 * @return
	 */
	public static FixtureDef createFixtureDef(float density, float restitution,
			float friction, boolean isSensor) {
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = density;
		fixtureDef.restitution = restitution;
		fixtureDef.friction = friction;
		fixtureDef.isSensor = isSensor;
		return fixtureDef;
	}

	/**
	 * 生成指定内容的方形Body
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param bodyType
	 * @param fixtureDef
	 * @return
	 */
	public static Body createBoxBody(World world, int x, int y, int w, int h,
			BodyType bodyType, FixtureDef fixtureDef) {
		return createBoxBody(world, x, y, w, h, bodyType, fixtureDef, 1);
	}

	/**
	 * 生成指定内容的方形Body
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param bodyType
	 * @param fixtureDef
	 * @param offset
	 * @return
	 */
	public static Body createBoxBody(World world, int x, int y, int w, int h,
			BodyType bodyType, FixtureDef fixtureDef, float offset) {
		BodyDef boxBodyDef = new BodyDef();
		boxBodyDef.type = bodyType;

		boxBodyDef.position.x = x / (float) offset;
		boxBodyDef.position.y = y / (float) offset;

		Body boxBody = world.createBody(boxBodyDef);
		PolygonShape boxPoly = new PolygonShape();

		boxPoly.setAsBox(w, h);
		fixtureDef.shape = boxPoly;
		boxBody.createFixture(fixtureDef);
		boxPoly.dispose();

		return boxBody;
	}

	/**
	 * 生成指定内容的多边形Body
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param bodyType
	 * @param fixtureDef
	 * @return
	 */
	public static Body createPolygonBody(World world, int x, int y, int w,
			int h, Vector2f[] vertices, BodyType bodyType, FixtureDef fixtureDef) {
		return createPolygonBody(world, x, y, w, h, vertices, bodyType,
				fixtureDef, 1);
	}

	/**
	 * 生成指定内容的多边形Body
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param bodyType
	 * @param fixtureDef
	 * @param offset
	 * @return
	 */
	public static Body createPolygonBody(World world, int x, int y, int w,
			int h, Vector2f[] vertices, BodyType bodyType,
			FixtureDef fixtureDef, float offset) {
		BodyDef boxBodyDef = new BodyDef();
		boxBodyDef.type = bodyType;

		boxBodyDef.position.x = x / (float) offset;
		boxBodyDef.position.y = y / (float) offset;

		Body boxBody = world.createBody(boxBodyDef);
		PolygonShape boxPoly = new PolygonShape();

		boxPoly.set(vertices);

		fixtureDef.shape = boxPoly;
		boxBody.createFixture(fixtureDef);
		boxPoly.dispose();

		return boxBody;
	}

	/**
	 * 生成指定内容的三角形Body
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param bodyType
	 * @param fixtureDef
	 * @return
	 */
	public static Body createTriangleBody(World world, int x, int y, int w,
			int h, BodyType bodyType, FixtureDef fixtureDef) {

		float halfWidth = w / 2;
		float halfHeight = h / 2;

		float top = -halfHeight;
		float bottom = halfHeight;
		float left = -halfHeight;
		float center = 0;
		float right = halfWidth;

		Vector2f[] vertices = { new Vector2f(center, top),
				new Vector2f(right, bottom), new Vector2f(left, bottom) };

		return createPolygonBody(world, x, y, w, h, vertices, bodyType,
				fixtureDef, 1);
	}

	/**
	 * 生成指定内容的多边形Body
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param bodyType
	 * @param fixtureDef
	 * @return
	 */
	public static Body createHexagonBody(World world, int x, int y, int w,
			int h, BodyType bodyType, FixtureDef fixtureDef) {
		float halfWidth = w / 2;
		float halfHeight = h / 2;

		float top = -halfHeight;
		float bottom = halfHeight;
		float centerX = 0;

		float left = -halfWidth + 2.5f;
		float right = halfWidth - 2.5f;
		float higher = top + 8.25f;
		float lower = bottom - 8.25f;

		Vector2f[] vertices = { new Vector2f(centerX, top),
				new Vector2f(right, higher), new Vector2f(right, lower),
				new Vector2f(centerX, bottom), new Vector2f(left, lower),
				new Vector2f(left, higher) };

		return createPolygonBody(world, x, y, w, h, vertices, bodyType,
				fixtureDef, 1);
	}
	
}
