package loon.action.sprite.effect;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

import loon.action.sprite.ISprite;
import loon.core.LObject;
import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.timer.LTimer;
import loon.utils.MathUtils;


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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
public class FreedomEffect extends LObject implements ISprite {

	/**
	 * 自由场景特效
	 */
	private static final long serialVersionUID = 1L;

	private int x, y, width, height, count, layer;

	private LTimer timer;

	private LTexture texture;

	private IKernel[] kernels;

	private boolean visible = true, dirty;

	private ArrayList<LTexture> tex2ds = new ArrayList<LTexture>(10);

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
				int no = MathUtils.random(0, limit);
				kernels[i] = (IKernel) constructor.newInstance(new Object[] {
						new Integer(no), new Integer(w), new Integer(h) });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(long elapsedTime) {
		if (visible && timer.action(elapsedTime)) {
			for (int i = 0; i < count; i++) {
				kernels[i].update();
			}
			dirty = true;
		}
	}

	@Override
	public void createUI(GLEx g) {
		if (visible) {
			if (dirty) {
				tex2ds.clear();
				for (int i = 0; i < count; i++) {
					texture = kernels[i].get();
					if (!tex2ds.contains(texture)) {
						tex2ds.add(texture);
						texture.glBegin();
					}
					kernels[i].draw(g);
				}
				for (int i = 0; i < tex2ds.size(); i++) {
					texture = tex2ds.get(i);
					texture.newBatchCache(true);
					texture.postLastBatchCache();
				}
				dirty = false;
			} else {
				for (int i = 0; i < tex2ds.size(); i++) {
					texture = tex2ds.get(i);
					texture.postLastBatchCache();
				}
			}
		}
	}

	public long getDelay() {
		return timer.getDelay();
	}

	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public float getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	@Override
	public float getY() {
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

	@Override
	public int getLayer() {
		return layer;
	}

	@Override
	public void setLayer(int layer) {
		this.layer = layer;
	}

	@Override
	public RectBox getCollisionBox() {
		return getRect(x, y, width, height);
	}

	@Override
	public int x() {
		return x;
	}

	@Override
	public int y() {
		return y;
	}

	@Override
	public LTexture getBitmap() {
		return null;
	}

	@Override
	public void dispose() {
		this.visible = false;
		if (kernels != null) {
			for (int i = 0; i < kernels.length; i++) {
				kernels[i].dispose();
				kernels[i] = null;
			}
		}
		tex2ds.clear();
	}
}
