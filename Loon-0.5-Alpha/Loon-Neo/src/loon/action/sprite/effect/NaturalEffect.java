package loon.action.sprite.effect;

import loon.LObject;
import loon.LSystem;
import loon.LTexture;
import loon.action.sprite.ISprite;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.opengl.LTexturePack;
import loon.utils.MathUtils;
import loon.utils.timer.LTimer;

public class NaturalEffect extends LObject implements ISprite {

	public static enum NaturalType {
		Rain, Snow, Petal;
	}

	/**
	 * 自由场景特效
	 */
	private static final long serialVersionUID = 1L;

	private LTexturePack pack;

	private int x, y, width, height, count, layer;

	private LTimer timer;

	private IKernel[] kernels;

	private boolean visible = true;

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
		return NaturalEffect.getSnowEffect(count, x, y,
				LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
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
	public static NaturalEffect getSnowEffect(int count, int x, int y, int w,
			int h) {
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
		return NaturalEffect.getRainEffect(count, x, y,
				LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
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
	public static NaturalEffect getRainEffect(int count, int x, int y, int w,
			int h) {
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
		return NaturalEffect.getPetalEffect(count, x, y,
				LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
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
	public static NaturalEffect getPetalEffect(int count, int x, int y, int w,
			int h) {
		return new NaturalEffect(NaturalType.Petal, count, 1, x, y, w, h);
	}

	public NaturalEffect(NaturalType ntype, int count, int limit) {
		this(ntype, count, limit, 0, 0);
	}

	public NaturalEffect(NaturalType ntype, int count, int limit, int x, int y) {
		this(ntype, count, limit, x, y, LSystem.viewSize.getWidth(),
				LSystem.viewSize.getHeight());
	}

	public NaturalEffect(NaturalType ntype, int count, int limit, int x, int y,
			int w, int h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		this.count = count;
		this.timer = new LTimer(80);
		this.pack = new LTexturePack(LSystem.FRAMEWORK_IMG_NAME + "natural.txt");
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
		}
	}

	public void update(long elapsedTime) {
		if (visible && timer.action(elapsedTime)) {
			for (int i = 0; i < count; i++) {
				kernels[i].update();
			}
		}
	}

	public void createUI(GLEx g) {
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

	public float getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

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

	public LTexture getBitmap() {
		return null;
	}

	public void close() {
		this.visible = false;
		if (kernels != null) {
			int size = kernels.length;
			for (int i = 0; i < size; i++) {
				kernels[i].close();
				kernels[i] = null;
			}
		}
		if (pack != null) {
			pack.close();
		}
	}
}
