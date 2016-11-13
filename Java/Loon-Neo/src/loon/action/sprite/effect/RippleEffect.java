package loon.action.sprite.effect;

import java.util.Iterator;

import loon.LObject;
import loon.LSystem;
import loon.LTexture;
import loon.action.sprite.ISprite;
import loon.canvas.LColor;
import loon.event.LTouchArea;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.TArray;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;

public class RippleEffect extends LObject<ISprite> implements LTouchArea, BaseEffect,
		ISprite {

	public enum Model {
		OVAL, RECT;
	}

	private TArray<RippleKernel> ripples;

	private LTimer timer;

	private boolean visible, completed;

	private LColor color;

	private Model model;

	private int existTime = 25;

	private float offsetX, offsetY;

	public RippleEffect() {
		this(Model.OVAL, LColor.blue);
	}

	public RippleEffect(LColor c) {
		this(Model.OVAL, c);
	}

	public RippleEffect(Model model) {
		this(model, LColor.blue);
	}

	public RippleEffect(Model m, LColor c) {
		model = m;
		color = c;
		ripples = new TArray<RippleKernel>();
		timer = new LTimer(60);
		visible = true;
	}

	public void setDelay(long delay) {
		this.timer.setDelay(delay);
	}

	public long getDelay() {
		return this.timer.getDelay();
	}

	public boolean addRipplePoint(final float x, final float y) {
		final RippleKernel ripple = new RippleKernel(x, y, existTime);
		final RealtimeProcess update = new RealtimeProcess() {

			@Override
			public void run(LTimerContext time) {
				RippleKernel rippleOther = new RippleKernel(x, y, existTime);
				ripples.add(rippleOther);
				kill();
			}
		};
		update.setDelay(LSystem.SECOND / 5);
		RealtimeProcessManager.get().addProcess(update);
		ripples.add(ripple);
		return true;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = true;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void createUI(GLEx g) {
		createUI(g, 0, 0);
	}

	@Override
	public void createUI(GLEx g, float sx, float sy) {
		if (completed) {
			return;
		}
		if (!visible) {
			return;
		}
		int tmp = g.color();
		g.setColor(color);
		for (Iterator<RippleKernel> it = ripples.iterator(); it.hasNext();) {
			RippleKernel ripple = it.next();
			ripple.draw(g, model, _location.x + offsetX + sx, _location.y
					+ offsetY + sy);
		}
		g.setColor(tmp);
	}

	@Override
	public void update(long elapsedTime) {
		if (completed) {
			return;
		}
		if (timer.action(elapsedTime)) {
			for (Iterator<RippleKernel> it = ripples.iterator(); it.hasNext();) {
				RippleKernel ripple = it.next();
				if (ripple.isExpired()) {
					it.remove();
				}
			}
		}
	}

	@Override
	public float getWidth() {
		return getContainerWidth();
	}

	@Override
	public float getHeight() {
		return getContainerHeight();
	}

	@Override
	public boolean isCompleted() {
		return completed;
	}

	@Override
	public RectBox getCollisionBox() {
		return getCollisionArea();
	}

	@Override
	public LTexture getBitmap() {
		return null;
	}

	@Override
	public void close() {
		visible = false;
		completed = true;
	}

	public int getExistTime() {
		return existTime;
	}

	public void setExistTime(int existTime) {
		this.existTime = existTime;
	}

	@Override
	public boolean contains(float x, float y) {
		return LSystem.viewSize.contains(x, y);
	}

	@Override
	public void onAreaTouched(Event e, float touchX, float touchY) {
		if (e == Event.DOWN) {
			addRipplePoint(touchX, touchY);
		}
	}

	public float getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(float offsetX) {
		this.offsetX = offsetX;
	}

	public float getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(float offsetY) {
		this.offsetY = offsetY;
	}

}
