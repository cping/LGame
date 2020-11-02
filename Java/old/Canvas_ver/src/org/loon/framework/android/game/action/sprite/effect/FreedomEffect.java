package org.loon.framework.android.game.action.sprite.effect;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

import org.loon.framework.android.game.action.sprite.ISprite;
import org.loon.framework.android.game.core.LObject;
import org.loon.framework.android.game.core.LSystem;
import org.loon.framework.android.game.core.geom.RectBox;
import org.loon.framework.android.game.core.graphics.device.LGraphics;
import org.loon.framework.android.game.core.timer.LTimer;

import android.graphics.Bitmap;

/**
 * Copyright 2008 - 2009
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class FreedomEffect extends LObject implements ISprite {

	/**
	 * 自由场景特效
	 */
	private static final long serialVersionUID = 1L;

	private int x, y, width, height, count, layer;

	private LTimer timer;

	private IKernel[] kernels;

	private boolean visible = true;

	/**
	 * 返回默认数量的飘雪
	 * 
	 * @return
	 */
	public static FreedomEffect getSnowEffect() {
		return FreedomEffect.getSnowEffect(60);
	}

	/**
	 * 返回指定数量的飘雪
	 * 
	 * @param count
	 * @return
	 */
	public static FreedomEffect getSnowEffect(int count) {
		return FreedomEffect.getSnowEffect(count, 0, 0);
	}

	/**
	 * 返回指定数量的飘雪
	 * 
	 * @param count
	 * @param x
	 * @param y
	 * @return
	 */
	public static FreedomEffect getSnowEffect(int count, int x, int y) {
		return FreedomEffect.getSnowEffect(count, x, y,
				LSystem.screenRect.width, LSystem.screenRect.height);
	}

	/**
	 * 返回指定数量的飘雪
	 * 
	 * @param count
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public static FreedomEffect getSnowEffect(int count, int x, int y, int w,
			int h) {
		return new FreedomEffect(SnowKernel.class, count, 4, x, y, w, h);
	}

	/**
	 * 返回默认数量的落雨
	 * 
	 * @return
	 */
	public static FreedomEffect getRainEffect() {
		return FreedomEffect.getRainEffect(60);
	}

	/**
	 * 返回指定数量的落雨
	 * 
	 * @param count
	 * @return
	 */
	public static FreedomEffect getRainEffect(int count) {
		return FreedomEffect.getRainEffect(count, 0, 0);
	}

	/**
	 * 返回指定数量的落雨
	 * 
	 * @param count
	 * @param x
	 * @param y
	 * @return
	 */
	public static FreedomEffect getRainEffect(int count, int x, int y) {
		return FreedomEffect.getRainEffect(count, x, y,
				LSystem.screenRect.width, LSystem.screenRect.height);
	}

	/**
	 * 返回指定数量的落雨
	 * 
	 * @param count
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public static FreedomEffect getRainEffect(int count, int x, int y, int w,
			int h) {
		return new FreedomEffect(RainKernel.class, count, 3, x, y, w, h);
	}

	/**
	 * 返回指定数量的樱花
	 * 
	 * @return
	 */
	public static FreedomEffect getPetalEffect() {
		return FreedomEffect.getPetalEffect(25);
	}

	/**
	 * 返回指定数量的樱花
	 * 
	 * @param count
	 * @return
	 */
	public static FreedomEffect getPetalEffect(int count) {
		return FreedomEffect.getPetalEffect(count, 0, 0);
	}

	/**
	 * 返回指定数量的樱花
	 * 
	 * @param count
	 * @param x
	 * @param y
	 * @return
	 */
	public static FreedomEffect getPetalEffect(int count, int x, int y) {
		return FreedomEffect.getPetalEffect(count, x, y,
				LSystem.screenRect.width, LSystem.screenRect.height);
	}

	/**
	 * 返回指定数量的樱花
	 * 
	 * @param count
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public static FreedomEffect getPetalEffect(int count, int x, int y, int w,
			int h) {
		return new FreedomEffect(PetalKernel.class, count, 1, x, y, w, h);
	}

	public FreedomEffect(Class<?> clazz, int count, int limit) {
		this(clazz, count, limit, 0, 0);
	}

	public FreedomEffect(Class<?> clazz, int count, int limit, int x, int y) {
		this(clazz, count, limit, x, y, LSystem.screenRect.width,
				LSystem.screenRect.height);
	}

	public FreedomEffect(Class<?> clazz, int count, int limit, int x, int y,
			int w, int h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		this.count = count;
		this.timer = new LTimer(80);
		this.kernels = (IKernel[]) Array.newInstance(clazz, count);
		try {
			Constructor<?> constructor = clazz
					.getDeclaredConstructor(new Class[] { int.class, int.class,
							int.class });
			for (int i = 0; i < count; i++) {
				int no = LSystem.getRandom(0, limit);
				kernels[i] = (IKernel) constructor.newInstance(new Object[] {
						new Integer(no), new Integer(w), new Integer(h) });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createUI(LGraphics g) {
		if (visible) {
			for (int i = 0; i < count; i++) {
				kernels[i].draw(g);
			}
		}
	}

	public long getDelay() {
		return timer.getDelay();
	}

	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void update(long elapsedTime) {
		if (visible && timer.action(elapsedTime)) {
			for (int i = 0; i < count; i++) {
				kernels[i].move();
			}
		}
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public double getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public IKernel[] getKernels() {
		return kernels;
	}

	public void setKernels(IKernel[] kernels) {
		this.kernels = kernels;
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public RectBox getCollisionBox() {
		return getRect(x, y, width, height);
	}

	public int x() {
		return x;
	}

	public int y() {
		return y;
	}

	public float getAlpha() {
		return 0;
	}

	public Bitmap getBitmap() {
		return null;
	}

	public void dispose() {

	}
}
