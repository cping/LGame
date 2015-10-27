package loon.action.sprite.effect;

import java.util.ArrayList;

import loon.LObject;
import loon.LSystem;
import loon.LTexture;
import loon.action.sprite.ISprite;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.reflect.ArrayReflection;
import loon.utils.reflect.ClassReflection;
import loon.utils.timer.LTimer;

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
				LSystem.viewSize.getWidth(),LSystem.viewSize.getHeight());
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
	public static FreedomEffect getPetalEffect(int count, int x, int y, int w,
			int h) {
		return new FreedomEffect(PetalKernel.class, count, 1, x, y, w, h);
	}

	public FreedomEffect(Class<?> clazz, int count, int limit) {
		this(clazz, count, limit, 0, 0);
	}

	public FreedomEffect(Class<?> clazz, int count, int limit, int x, int y) {
		this(clazz, count, limit, x, y, LSystem.viewSize.getWidth(),
				LSystem.viewSize.getHeight());
	}

	public FreedomEffect(Class<?> clazz, int count, int limit, int x, int y,
			int w, int h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		this.count = count;
		this.timer = new LTimer(80);
		this.kernels = (IKernel[]) ArrayReflection.newInstance(clazz, count);
		try {
			loon.utils.reflect.Constructor constructor = ClassReflection
					.getDeclaredConstructor(clazz,new Class[] { int.class, int.class,
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

	public void update(long elapsedTime) {
		if (visible && timer.action(elapsedTime)) {
			for (int i = 0; i < count; i++) {
				kernels[i].update();
			}
			dirty = true;
		}
	}

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
					texture.newBatchCache();
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
			for (int i = 0; i < kernels.length; i++) {
				kernels[i].close();
				kernels[i] = null;
			}
		}
		tex2ds.clear();
	}
}
