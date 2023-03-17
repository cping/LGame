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
package loon.utils;

import loon.ActionCounter;
import loon.Counter;
import loon.Director.Origin;
import loon.Director.Position;
import loon.EmptyObject;
import loon.LObject;
import loon.LSystem;
import loon.LTexture;
import loon.LimitedCounter;
import loon.Log.Level;
import loon.ZIndex;
import loon.action.sprite.Entity;
import loon.action.sprite.Sprite;
import loon.canvas.LColor;
import loon.events.Updateable;
import loon.geom.Affine2f;
import loon.geom.BooleanValue;
import loon.geom.FloatValue;
import loon.geom.IntValue;
import loon.geom.PointF;
import loon.geom.PointI;
import loon.geom.RectBox;
import loon.geom.RectF;
import loon.geom.RectI;
import loon.geom.Vector2f;
import loon.utils.timer.Duration;
import loon.utils.timer.Interval;

/**
 * 辅助用类,实现了一些常见的数值转换功能,可以在Screen中直接调用
 *
 */
public class HelperUtils {

	public static boolean isNull(Object obj)
	{
		return obj == null;
	}

	public static boolean isNotNull(Object obj)
	{
		return obj != null;
	}

	public static boolean areEqual(Object first, Object second)
	{
		return first != second;
	}

	public static boolean areNotEqual(Object first, Object second)
	{
		return first == second;
	}

	public final static <T> boolean contains(T key, TArray<T> list) {
		for (T o : list) {
			if (key == null && o == null) {
				return true;
			}
			if (o == key || (o != null && o.equals(key))) {
				return true;
			}
		}
		return false;
	}

	public final static boolean contains(Object key, Object... objs) {
		for (Object o : objs) {
			if (key == null && o == null) {
				return true;
			}
			if (o == key || (o != null && o.equals(key))) {
				return true;
			}
		}
		return false;
	}

	public final static Sprite createSprite(String path) {
		return new Sprite(path);
	}

	public final static Sprite createSprite(LTexture tex2d) {
		return new Sprite(tex2d);
	}

	public final static Sprite createSprite(String path, float scale) {
		Sprite spr = new Sprite(path);
		spr.setScale(scale);
		return spr;
	}

	public final static Sprite createSprite(LTexture tex2d, float scale) {
		Sprite spr = new Sprite(tex2d);
		spr.setScale(scale);
		return spr;
	}

	public final static Sprite createSprite(String path, Vector2f pos) {
		Sprite spr = new Sprite(path);
		spr.setLocation(pos);
		return spr;
	}

	public final static Sprite createSprite(LTexture tex2d, Vector2f pos) {
		Sprite spr = new Sprite(tex2d);
		spr.setLocation(pos);
		return spr;
	}

	public final static Entity createEntity(String path) {
		return new Entity(path);
	}

	public final static Entity createEntity(LTexture tex2d) {
		return new Entity(tex2d);
	}

	public final static Entity createEntity(String path, float scale) {
		Entity spr = new Entity(path);
		spr.setScale(scale);
		return spr;
	}

	public final static Entity createEntity(LTexture tex2d, float scale) {
		Entity spr = new Entity(tex2d);
		spr.setScale(scale);
		return spr;
	}

	public final static Entity createEntity(String path, Vector2f pos) {
		Entity spr = new Entity(path);
		spr.setLocation(pos);
		return spr;
	}

	public final static Entity createEntity(LTexture tex2d, Vector2f pos) {
		Entity spr = new Entity(tex2d);
		spr.setLocation(pos);
		return spr;
	}

	public final static TArray<Sprite> createMultiSprite(String[] path, Vector2f[] pos) {
		return createMultiSprite(path, pos, 1f);
	}

	public final static TArray<Sprite> createMultiSprite(String[] path, Vector2f[] pos, float scale) {
		if (StringUtils.isEmpty(path)) {
			return new TArray<>();
		}
		final int size = path.length;
		TArray<Sprite> list = new TArray<>(path.length);
		for (int i = 0; i < size; i++) {
			Sprite sprite = createSprite(path[i], pos[i]);
			sprite.setScale(scale);
		}
		return list;
	}

	public final static TArray<Entity> createMultiEntity(String[] path, Vector2f[] pos) {
		return createMultiEntity(path, pos, 1f);
	}

	public final static TArray<Entity> createMultiEntity(String[] path, Vector2f[] pos, float scale) {
		if (StringUtils.isEmpty(path)) {
			return new TArray<>();
		}
		final int size = path.length;
		TArray<Entity> list = new TArray<>(path.length);
		for (int i = 0; i < size; i++) {
			Entity sprite = createEntity(path[i], pos[i]);
			sprite.setScale(scale);
		}
		return list;
	}

	public final static EmptyObject newEmptyObject() {
		return new EmptyObject();
	}

	public final static Vector2f makeOrigin(LObject<?> o, Origin origin) {
		return createOrigin(o, origin);
	}

	public final static TArray<Vector2f> makeOrigins(Origin origin, LObject<?>... objs) {
		TArray<Vector2f> result = new TArray<>(objs.length);
		for (LObject<?> o : objs) {
			result.add(createOrigin(o, origin));
		}
		return result;
	}

	private static Vector2f createOrigin(LObject<?> o, Origin origin) {
		Vector2f v = new Vector2f(o.x(), o.y());
		switch (origin) {
		case CENTER:
			v.set(o.getWidth() / 2f, o.getHeight() / 2f);
			return v;
		case TOP_LEFT:
			v.set(0.0f, o.getHeight());
			return v;
		case TOP_RIGHT:
			v.set(o.getWidth(), o.getHeight());
			return v;
		case BOTTOM_LEFT:
			v.set(0.0f, 0.0f);
			return v;
		case BOTTOM_RIGHT:
			v.set(o.getWidth(), 0.0f);
			return v;
		case LEFT_CENTER:
			v.set(0.0f, o.getHeight() / 2f);
			return v;
		case TOP_CENTER:
			v.set(o.getWidth() / 2f, o.getHeight());
			return v;
		case BOTTOM_CENTER:
			v.set(o.getWidth() / 2f, 0.0f);
			return v;
		case RIGHT_CENTER:
			v.set(o.getWidth(), o.getHeight() / 2f);
			return v;
		default:
			return v;
		}
	}

	public final static void setPoisiton(LObject<?> objToBePositioned, LObject<?> objStable, Position position) {
		float atp_W = objToBePositioned.getWidth();
		float atp_H = objToBePositioned.getHeight();
		float obj_X = objStable.getX();
		float obj_Y = objStable.getY();
		float obj_XW = objStable.getWidth() + obj_X;
		float obj_YH = objStable.getHeight() + obj_Y;
		setLocation(objToBePositioned, atp_W, atp_H, obj_X, obj_Y, obj_XW, obj_YH, position);
	}

	public final static void setPoisiton(LObject<?> objToBePositioned, float x, float y, float width, float height,
			Position position) {
		float atp_W = objToBePositioned.getWidth();
		float atp_H = objToBePositioned.getHeight();
		float obj_X = x;
		float obj_Y = y;
		float obj_XW = width + obj_X;
		float obj_YH = height + obj_Y;
		setLocation(objToBePositioned, atp_W, atp_H, obj_X, obj_Y, obj_XW, obj_YH, position);
	}

	private static void setLocation(LObject<?> objToBePositioned, float atp_W, float atp_H, float obj_X, float obj_Y,
			float obj_XW, float obj_YH, Position position) {
		switch (position) {
		case CENTER:
			objToBePositioned.setX((obj_XW / 2f) - atp_W / 2f);
			objToBePositioned.setY((obj_YH / 2f) - atp_H / 2f);
			break;
		case SAME:
			objToBePositioned.setLocation(obj_X, obj_Y);
			break;
		case LEFT:
			objToBePositioned.setLocation(obj_X, obj_YH / 2f - atp_H / 2f);
			break;
		case TOP_LEFT:
			objToBePositioned.setLocation(obj_X, obj_YH - atp_H);
			break;
		case TOP_LEFT_CENTER:
			objToBePositioned.setLocation(obj_X - atp_W / 2f, obj_YH - atp_H / 2f);
			break;
		case TOP_RIGHT:
			objToBePositioned.setLocation(obj_XW - atp_W, obj_YH - atp_H);
			break;
		case TOP_RIGHT_CENTER:
			objToBePositioned.setLocation(obj_XW - atp_W / 2f, obj_YH - atp_H / 2f);
			break;
		case TOP_CENTER:
			objToBePositioned.setLocation(obj_XW / 2f - atp_W / 2f, obj_YH - atp_H);
			break;
		case BOTTOM_LEFT:
			objToBePositioned.setLocation(obj_X, obj_Y);
			break;
		case BOTTOM_LEFT_CENTER:
			objToBePositioned.setLocation(obj_X - atp_W / 2f, obj_Y - atp_H / 2f);
			break;
		case BOTTOM_RIGHT:
			objToBePositioned.setLocation(obj_XW - atp_W, obj_Y);
			break;
		case BOTTOM_RIGHT_CENTER:
			objToBePositioned.setLocation(obj_XW - atp_W / 2f, obj_Y - atp_H / 2f);
			break;
		case BOTTOM_CENTER:
			objToBePositioned.setLocation(obj_XW / 2f - atp_W / 2f, obj_Y);
			break;
		case RIGHT_CENTER:
			objToBePositioned.setLocation(obj_XW - atp_W, obj_YH / 2f - atp_H / 2f);
			break;
		default:
			objToBePositioned.setLocation(objToBePositioned.getX(), objToBePositioned.getY());
			break;
		}
	}

	private final static Affine2f _trans = new Affine2f();

	public final static Vector2f local2Global(float centerX, float centerY, float posX, float posY,
			Vector2f resultPoint) {
		return local2Global(0, 1f, 1f, 0, 0, false, false, centerX, centerY, posX, posY, resultPoint);
	}

	public final static Vector2f local2Global(boolean flipX, boolean flipY, float centerX, float centerY, float posX,
			float posY, Vector2f resultPoint) {
		return local2Global(0, 1f, 1f, 0, 0, flipX, flipY, centerX, centerY, posX, posY, resultPoint);
	}

	public final static Vector2f local2Global(float rotation, float centerX, float centerY, float posX, float posY,
			Vector2f resultPoint) {
		return local2Global(rotation, 1f, 1f, 0, 0, false, false, centerX, centerY, posX, posY, resultPoint);
	}

	public final static Vector2f local2Global(float rotation, boolean flipX, boolean flipY, float centerX,
			float centerY, float posX, float posY, Vector2f resultPoint) {
		return local2Global(rotation, 1f, 1f, 0, 0, flipX, flipY, centerX, centerY, posX, posY, resultPoint);
	}

	public final static Vector2f local2Global(float rotation, float scaleX, float scaleY, boolean flipX, boolean flipY,
			float centerX, float centerY, float posX, float posY, Vector2f resultPoint) {
		return local2Global(rotation, scaleX, scaleY, 0, 0, flipX, flipY, centerX, centerY, posX, posY, resultPoint);
	}

	public final static Vector2f local2Global(float rotation, float scaleX, float scaleY, float skewX, float skewY,
			boolean flipX, boolean flipY, float centerX, float centerY, float posX, float posY, Vector2f resultPoint) {
		_trans.idt();
		if (rotation != 0) {
			_trans.translate(centerX, centerY);
			_trans.preRotate(rotation);
			_trans.translate(-centerX, -centerY);
		}
		if (flipX || flipY) {
			if (flipX && flipY) {
				Affine2f.transform(_trans, centerX, centerY, Affine2f.TRANS_ROT180);
			} else if (flipX) {
				Affine2f.transform(_trans, centerX, centerY, Affine2f.TRANS_MIRROR);
			} else if (flipY) {
				Affine2f.transform(_trans, centerX, centerY, Affine2f.TRANS_MIRROR_ROT180);
			}
		}
		if ((scaleX != 1) || (scaleY != 1)) {
			_trans.translate(centerX, centerY);
			_trans.preScale(scaleX, scaleY);
			_trans.translate(-centerX, -centerY);
		}
		if ((skewX != 0) || (skewY != 0)) {
			_trans.translate(centerX, centerY);
			_trans.preShear(skewX, skewY);
			_trans.translate(-centerX, -centerY);
		}
		if (resultPoint != null) {
			_trans.transformPoint(posX, posY, resultPoint);
			return resultPoint;
		}
		return resultPoint;
	}

	public final static float random() {
		return MathUtils.random();
	}

	public final static float random(float start, float end) {
		return MathUtils.random(start, end);
	}

	public final static int random(int start, int end) {
		return MathUtils.random(start, end);
	}

	public final static Calculator newCalculator() {
		return new Calculator();
	}

	public final static Counter newCounter() {
		return new Counter();
	}

	public final static LimitedCounter newLimitedCounter(int limit) {
		return new LimitedCounter(limit);
	}

	public final static ActionCounter newActionCounter(int limit, Updateable update) {
		return new ActionCounter(limit, update);
	}

	public final static void debug(String msg) {
		LSystem.debug(msg);
	}

	public final static void debug(String msg, Object... args) {
		LSystem.debug(msg, args);
	}

	public final static void debug(String msg, Throwable throwable) {
		LSystem.debug(msg, throwable);
	}

	public final static void info(String msg) {
		LSystem.info(msg);
	}

	public final static void info(String msg, Object... args) {
		LSystem.info(msg, args);
	}

	public final static void info(String msg, Throwable throwable) {
		LSystem.info(msg, throwable);
	}

	public final static void error(String msg) {
		LSystem.error(msg);
	}

	public final static void error(String msg, Object... args) {
		LSystem.error(msg, args);
	}

	public final static void error(String msg, Throwable throwable) {
		LSystem.error(msg, throwable);
	}

	public final static void reportError(String msg, Throwable throwable) {
		LSystem.reportError(msg, throwable);
	}

	public final static void d(String msg) {
		LSystem.debug(msg);
	}

	public final static void d(String msg, Object... args) {
		LSystem.debug(msg, args);
	}

	public final static void d(String msg, Throwable throwable) {
		LSystem.debug(msg, throwable);
	}

	public final static void i(String msg) {
		LSystem.info(msg);
	}

	public final static void i(String msg, Object... args) {
		LSystem.info(msg, args);
	}

	public final static void i(String msg, Throwable throwable) {
		LSystem.info(msg, throwable);
	}

	public final static void w(String msg) {
		LSystem.warn(msg);
	}

	public final static void w(String msg, Object... args) {
		LSystem.warn(msg, args);
	}

	public final static void w(String msg, Throwable throwable) {
		LSystem.warn(msg, throwable);
	}

	public final static void e(String msg) {
		LSystem.error(msg);
	}

	public final static void e(String msg, Object... args) {
		LSystem.error(msg, args);
	}

	public final static void e(String msg, Throwable throwable) {
		LSystem.error(msg, throwable);
	}

	public final static void setLogMinLevel(Level level) {
		LSystem.setLogMinLevel(level);
	}

	public final static Interval interval(final Duration d, final Updateable update) {
		return new Interval(d) {

			@Override
			public void loop() {
				if (update != null) {
					update.action(null);
				}
			}
		};
	}

	public final static Interval interval(final String name, final long delay, final Updateable update) {
		return new Interval(name, delay) {

			@Override
			public void loop() {
				if (update != null) {
					update.action(null);
				}
			}
		};
	}

	public final static Interval interval(final long delay, final int loopCount, final Updateable update) {
		return new Interval(delay, loopCount) {

			@Override
			public void loop() {
				if (update != null) {
					update.action(null);
				}
			}
		};
	}

	public final static Interval interval(final String name, final long delay, final int loopCount,
			final Updateable update) {
		return new Interval(delay, loopCount) {

			@Override
			public void loop() {
				if (update != null) {
					update.action(null);
				}
			}
		};
	}

	public final static Interval interval(final String name, final Duration d, final Updateable update) {
		return new Interval(name, d) {

			@Override
			public void loop() {
				if (update != null) {
					update.action(null);
				}
			}
		};
	}

	public final static Vector2f point() {
		return point(0f, 0f);
	}

	public final static Vector2f point(float x, float y) {
		return new Vector2f(x, y);
	}

	public final static PointF pointf() {
		return pointf(0f, 0f);
	}

	public final static PointF pointf(float x, float y) {
		return new PointF(x, y);
	}

	public final static PointI pointi() {
		return pointi(0, 0);
	}

	public final static PointI pointi(int x, int y) {
		return new PointI(x, y);
	}

	public final static RectBox rect(float x, float y, float w, float h) {
		return new RectBox(x, y, w, h);
	}

	public final static RectF rectf(float x, float y, float w, float h) {
		return new RectF(x, y, w, h);
	}

	public final static RectI recti(int x, int y, int w, int h) {
		return new RectI(x, y, w, h);
	}

	public final static BooleanValue boolValue(boolean v) {
		return refBool(v);
	}

	public final static FloatValue floatValue(float v) {
		return refFloat(v);
	}

	public final static IntValue intValue(int v) {
		return refInt(v);
	}

	public final static BooleanValue refBool() {
		return new BooleanValue();
	}

	public final static FloatValue refFloat() {
		return new FloatValue();
	}

	public final static IntValue refInt() {
		return new IntValue();
	}

	public final static BooleanValue refBool(boolean v) {
		return new BooleanValue(v);
	}

	public final static FloatValue refFloat(float v) {
		return new FloatValue(v);
	}

	public final static IntValue refInt(int v) {
		return new IntValue(v);
	}

	public final static <T> T getValue(T val, T defval) {
		return val == null ? defval : val;
	}

	public final static float toPercent(float value, float min, float max) {
		return MathUtils.percent(value, min, max);
	}

	public final static float toPercent(float value, float min, float max, float upperMax) {
		return MathUtils.percent(value, min, max, upperMax);
	}

	public static final float toPercent(float value, float percent) {
		return MathUtils.percent(value, percent);
	}

	public static final int toPercent(int value, int percent) {
		return MathUtils.percent(value, percent);
	}

	public final static BoolArray toBoolArrayOf(boolean... arrays) {
		return new BoolArray(arrays);
	}

	public final static FloatArray toFloatArrayOf(float... arrays) {
		return new FloatArray(arrays);
	}

	public final static IntArray toIntArrayOf(int... arrays) {
		return new IntArray(arrays);
	}

	public final static boolean toOrder(ZIndex[] array) {
		if (array == null || array.length < 2) {
			return false;
		}
		final int len = array.length;
		int key = 0;
		ZIndex cur;
		for (int i = 1, j = 0; i < len; i++) {
			j = i;
			cur = array[j];
			key = array[j].getLayer();
			for (; --j > -1;) {
				if (array[j].getLayer() > key) {
					array[j + 1] = array[j];
				} else {
					break;
				}
			}
			array[j + 1] = cur;
		}
		return true;
	}

	public final static LColor toColor(int r, int g, int b, int a) {
		return new LColor(r, g, b, a);
	}

	public final static LColor toColor(int r, int g, int b) {
		return new LColor(r, g, b);
	}

	public final static LColor toColor(String c) {
		if (StringUtils.isEmpty(c)) {
			return new LColor();
		}
		return new LColor(c);
	}

	public final static double toDouble(Object o) {
		if (o == null) {
			return -1d;
		}
		if (o instanceof Short) {
			return ((Short) o).doubleValue();
		}
		if (o instanceof Integer) {
			return ((Integer) o).doubleValue();
		}
		if (o instanceof Long) {
			return ((Long) o).doubleValue();
		}
		if (o instanceof Float) {
			return ((Float) o).doubleValue();
		}
		if (o instanceof Double) {
			return ((Double) o).doubleValue();
		}
		if (o instanceof Number) {
			return ((Number) o).doubleValue();
		}
		if (o instanceof Boolean) {
			return ((Boolean) o).booleanValue() ? 1 : 0;
		}
		if (o instanceof Character) {
			Character v = (Character) o;
			char vc = v.charValue();
			String ns = String.valueOf(vc);
			if (MathUtils.isNan(ns)) {
				return Float.valueOf(ns).doubleValue();
			}
		}
		if (o instanceof String) {
			String v = (String) o;
			if (MathUtils.isNan(v)) {
				return Float.valueOf(v).doubleValue();
			}
		}
		return -1d;
	}

	public final static float toFloat(Object o) {
		if (o == null) {
			return -1f;
		}
		if (o instanceof Short) {
			return ((Short) o).floatValue();
		}
		if (o instanceof Integer) {
			return ((Integer) o).floatValue();
		}
		if (o instanceof Long) {
			return ((Long) o).floatValue();
		}
		if (o instanceof Float) {
			return ((Float) o).floatValue();
		}
		if (o instanceof Double) {
			return ((Double) o).floatValue();
		}
		if (o instanceof Number) {
			return ((Number) o).floatValue();
		}
		if (o instanceof Boolean) {
			return ((Boolean) o).booleanValue() ? 1 : 0;
		}
		if (o instanceof Character) {
			Character v = (Character) o;
			char vc = v.charValue();
			String ns = String.valueOf(vc);
			if (MathUtils.isNan(ns)) {
				return Float.valueOf(ns).floatValue();
			}
		}
		if (o instanceof String) {
			String v = (String) o;
			if (MathUtils.isNan(v)) {
				return Float.valueOf(v).floatValue();
			}
		}
		return -1f;
	}

	public final static int toInt(Object o) {
		if (o == null) {
			return -1;
		}
		if (o instanceof Short) {
			return ((Short) o).intValue();
		}
		if (o instanceof Integer) {
			return ((Integer) o).intValue();
		}
		if (o instanceof Long) {
			return ((Long) o).intValue();
		}
		if (o instanceof Float) {
			return ((Float) o).intValue();
		}
		if (o instanceof Double) {
			return ((Double) o).intValue();
		}
		if (o instanceof Number) {
			return ((Number) o).intValue();
		}
		if (o instanceof Boolean) {
			return ((Boolean) o).booleanValue() ? 1 : 0;
		}
		if (o instanceof Character) {
			Character v = (Character) o;
			char vc = v.charValue();
			String ns = String.valueOf(vc);
			if (MathUtils.isNan(ns)) {
				return Float.valueOf(ns).intValue();
			}
		}
		if (o instanceof String) {
			String v = (String) o;
			if (MathUtils.isNan(v)) {
				return Float.valueOf(v).intValue();
			}
		}
		return -1;
	}

	public final static long toLong(Object o) {
		if (o == null) {
			return -1l;
		}
		if (o instanceof Short) {
			return ((Short) o).longValue();
		}
		if (o instanceof Integer) {
			return ((Integer) o).longValue();
		}
		if (o instanceof Long) {
			return ((Long) o).longValue();
		}
		if (o instanceof Float) {
			return ((Float) o).longValue();
		}
		if (o instanceof Double) {
			return ((Double) o).longValue();
		}
		if (o instanceof Number) {
			return ((Number) o).longValue();
		}
		if (o instanceof Boolean) {
			return ((Boolean) o).booleanValue() ? 1 : 0;
		}
		if (o instanceof Character) {
			Character v = (Character) o;
			char vc = v.charValue();
			String ns = String.valueOf(vc);
			if (MathUtils.isNan(ns)) {
				return Float.valueOf(ns).longValue();
			}
		}
		if (o instanceof String) {
			String v = (String) o;
			if (MathUtils.isNan(v)) {
				return Float.valueOf(v).longValue();
			}
		}
		return -1l;
	}

	public final static String toStr(Object o) {
		if (o == null) {
			return LSystem.NULL;
		}
		if (o instanceof Short) {
			return String.valueOf(((Short) o).shortValue());
		}
		if (o instanceof Integer) {
			return String.valueOf(((Integer) o).intValue());
		}
		if (o instanceof Long) {
			return String.valueOf(((Long) o).longValue());
		}
		if (o instanceof Float) {
			return String.valueOf(((Float) o).floatValue());
		}
		if (o instanceof Double) {
			return String.valueOf(((Double) o).doubleValue());
		}
		if (o instanceof Number) {
			return String.valueOf(((Number) o).floatValue());
		}
		if (o instanceof Boolean) {
			return String.valueOf(((Boolean) o).booleanValue());
		}
		if (o instanceof Character) {
			Character v = (Character) o;
			char vc = v.charValue();
			return String.valueOf(vc);
		}
		if (o instanceof String) {
			String v = (String) o;
			if (MathUtils.isNan(v)) {
				if (v.indexOf('.') != -1) {
					return String.valueOf(Float.valueOf(v).floatValue());
				} else {
					return String.valueOf(Float.valueOf(v).intValue());
				}
			} else {
				return v;
			}
		}
		return o.toString();
	}

}
