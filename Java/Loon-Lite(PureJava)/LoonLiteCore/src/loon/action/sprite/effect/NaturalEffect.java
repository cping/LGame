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
package loon.action.sprite.effect;

import loon.LSystem;
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.opengl.LTexturePack;
import loon.utils.MathUtils;

/**
 * 自然场景特效,默认支持将雨、雪、樱花飘落、闪电等四种自然现象渲染到Screen
 */
public class NaturalEffect extends BaseAbstractEffect {

	public static enum NaturalType {
		Rain, Snow, Petal, Thunder;
	}

	private NaturalType naturalType;

	private LightningEffect lightningEffect;

	private LTexturePack pack;

	private int count;

	private IKernel[] kernels;

	/**
	 * 返回默认数量的雷电
	 * 
	 * @return
	 */
	public static NaturalEffect getThunderEffect() {
		return NaturalEffect.getThunderEffect(30);
	}

	/**
	 * 返回指定数量的雷电
	 * 
	 * @param count
	 * @return
	 */
	public static NaturalEffect getThunderEffect(int count) {
		return NaturalEffect.getThunderEffect(count, 0, 0);
	}

	/**
	 * 返回指定数量的雷电
	 * 
	 * @param count
	 * @param x
	 * @param y
	 * @return
	 */
	public static NaturalEffect getThunderEffect(int count, int x, int y) {
		return NaturalEffect.getThunderEffect(count, x, y, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	/**
	 * 返回指定数量的雷电
	 * 
	 * @param count
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public static NaturalEffect getThunderEffect(int count, int x, int y, int w, int h) {
		return new NaturalEffect(NaturalType.Thunder, count, 4, x, y, w, h);
	}

	/**
	 * 返回默认数量的飘雪
	 * 
	 * @return
	 */
	public static NaturalEffect getSnowEffect() {
		return NaturalEffect.getSnowEffect(60);
	}

	/**
	 * 返回指定数量的飘雪
	 * 
	 * @param count
	 * @return
	 */
	public static NaturalEffect getSnowEffect(int count) {
		return NaturalEffect.getSnowEffect(count, 0, 0);
	}

	/**
	 * 返回指定数量的飘雪
	 * 
	 * @param count
	 * @param x
	 * @param y
	 * @return
	 */
	public static NaturalEffect getSnowEffect(int count, int x, int y) {
		return NaturalEffect.getSnowEffect(count, x, y, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
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
	public static NaturalEffect getSnowEffect(int count, int x, int y, int w, int h) {
		return new NaturalEffect(NaturalType.Snow, count, 4, x, y, w, h);
	}

	/**
	 * 返回默认数量的落雨
	 * 
	 * @return
	 */
	public static NaturalEffect getRainEffect() {
		return NaturalEffect.getRainEffect(60);
	}

	/**
	 * 返回指定数量的落雨
	 * 
	 * @param count
	 * @return
	 */
	public static NaturalEffect getRainEffect(int count) {
		return NaturalEffect.getRainEffect(count, 0, 0);
	}

	/**
	 * 返回指定数量的落雨
	 * 
	 * @param count
	 * @param x
	 * @param y
	 * @return
	 */
	public static NaturalEffect getRainEffect(int count, int x, int y) {
		return NaturalEffect.getRainEffect(count, x, y, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
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
	public static NaturalEffect getRainEffect(int count, int x, int y, int w, int h) {
		return new NaturalEffect(NaturalType.Rain, count, 3, x, y, w, h);
	}

	/**
	 * 返回指定数量的樱花
	 * 
	 * @return
	 */
	public static NaturalEffect getPetalEffect() {
		return NaturalEffect.getPetalEffect(25);
	}

	/**
	 * 返回指定数量的樱花
	 * 
	 * @param count
	 * @return
	 */
	public static NaturalEffect getPetalEffect(int count) {
		return NaturalEffect.getPetalEffect(count, 0, 0);
	}

	/**
	 * 返回指定数量的樱花
	 * 
	 * @param count
	 * @param x
	 * @param y
	 * @return
	 */
	public static NaturalEffect getPetalEffect(int count, int x, int y) {
		return NaturalEffect.getPetalEffect(count, x, y, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
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
	public static NaturalEffect getPetalEffect(int count, int x, int y, int w, int h) {
		return new NaturalEffect(NaturalType.Petal, count, 1, x, y, w, h);
	}

	public NaturalEffect(NaturalType ntype, int count, int limit) {
		this(ntype, count, limit, 0, 0);
	}

	public NaturalEffect(NaturalType ntype, int count, int limit, int x, int y) {
		this(ntype, count, limit, x, y, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public NaturalEffect(NaturalType ntype, int count, int limit, int x, int y, int w, int h) {
		this.naturalType = ntype;
		this.setLocation(x, y);
		this.setSize(w, h);
		this.setRepaint(true);
		this.count = count;
		if (ntype == NaturalType.Thunder) {
			this.setDelay(10);
		} else {
			this.setDelay(80);
		}
		this.pack = new LTexturePack(LSystem.getSystemImagePath() + "natural.txt");
		switch (ntype) {
		case Petal:
			this.kernels = new PetalKernel[count];
			for (int i = 0; i < count; i++) {
				int no = MathUtils.random(0, limit);
				kernels[i] = new PetalKernel(pack, no, w, h);
			}
			break;
		case Snow:
			this.kernels = new SnowKernel[count];
			for (int i = 0; i < count; i++) {
				int no = MathUtils.random(0, limit);
				kernels[i] = new SnowKernel(pack, no, w, h);
			}
			break;
		case Rain:
			this.kernels = new RainKernel[count];
			for (int i = 0; i < count; i++) {
				int no = MathUtils.random(0, limit);
				kernels[i] = new RainKernel(pack, no, w, h);
			}
			break;
		case Thunder:
			LightningEffect.get().loadLightning(pack);
			lightningEffect = LightningEffect.addRandom(count, new Vector2f(0, 0), new Vector2f(w, h), LColor.white);
		}
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (checkAutoRemove()) {
			return;
		}
		if (_timer.action(elapsedTime)) {
			if (naturalType == NaturalType.Thunder && lightningEffect != null) {
				lightningEffect.onUpdate(elapsedTime);
			} else {
				for (int i = 0; i < count; i++) {
					kernels[i].update();
				}
			}
		}
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (completedAfterBlackScreen(g, offsetX, offsetY)) {
			return;
		}
		if (naturalType == NaturalType.Thunder && lightningEffect != null) {
			lightningEffect.repaint(g, drawX(offsetX), drawY(offsetY));
		} else {
			for (int i = 0; i < count; i++) {
				kernels[i].draw(g, drawX(offsetX), drawY(offsetY));
			}
		}
	}

	public NaturalType getNaturalType() {
		return naturalType;
	}

	public IKernel[] getKernels() {
		return kernels;
	}

	public NaturalEffect setKernels(IKernel[] kernels) {
		this.kernels = kernels;
		return this;
	}

	@Override
	public NaturalEffect setAutoRemoved(boolean autoRemoved) {
		super.setAutoRemoved(autoRemoved);
		return this;
	}

	@Override
	protected void _onDestroy() {
		super._onDestroy();
		if (kernels != null) {
			int size = kernels.length;
			for (int i = 0; i < size; i++) {
				kernels[i].close();
				kernels[i] = null;
			}
		}
		if (pack != null) {
			pack.close();
			pack = null;
		}
		if (lightningEffect != null) {
			lightningEffect.close();
			lightningEffect = null;
		}
	}

}
