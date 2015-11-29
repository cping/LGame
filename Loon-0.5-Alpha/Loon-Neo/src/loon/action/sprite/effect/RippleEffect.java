package loon.action.sprite.effect;

import java.util.Iterator;

import loon.LObject;
import loon.LTexture;
import loon.action.sprite.ISprite;
import loon.canvas.LColor;
import loon.event.LTouchArea;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.TArray;
import loon.utils.timer.LTimer;

public class RippleEffect extends LObject implements LTouchArea, BaseEffect,
		ISprite {

	public enum Model {
		OVAL, RECT;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TArray<RippleKernel> ripples;

	private LTimer timer;

	private boolean visible, completed;

	private LColor color;

	private Model model;

	public RippleEffect() {
		this(Model.OVAL, LColor.blue);
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

	public boolean onTouch(float x, float y) {
		RippleKernel ripple = new RippleKernel(x, y);
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
		if (!visible) {
			return;
		}
		int tmp = g.color();
		g.setColor(color);
		for (Iterator<RippleKernel> it = ripples.iterator(); it.hasNext();) {
			RippleKernel ripple = it.next();
			ripple.draw(g, model, _location.x, _location.y);
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
	public int getWidth() {
		return getContainerWidth();
	}

	@Override
	public int getHeight() {
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
	}

	@Override
	public boolean contains(float x, float y) {
		return true;
	}

	@Override
	public void onAreaTouched(Event e, float touchX, float touchY) {
		if (e == Event.DOWN) {
			onTouch(touchX, touchY);
		}
	}

}
