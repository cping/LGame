package loon.stage;

import loon.LSystem;
import loon.opengl.GLEx;
import loon.opengl.RotationYBatch;
import loon.utils.Easing;
import loon.utils.MathUtils;

public class PageTransition extends EaseTransition<PageTransition> {

	protected float _alpha;
	protected boolean _close;

	protected Stage _toflip;
	protected Player _shadow;
	protected RotationYBatch _batch;

	public PageTransition close() {
		_close = true;
		_easing = Easing.JUST_EASE_INOUT;
		return this;
	}

	@Override
	public void init(Stage o, Stage n) {
		super.init(o, n);
		n.players.setDepth(_close ? 1 : -1);
		_toflip = _close ? n : o;
		_batch = new RotationYBatch(LSystem.base().graphics().gl, 0f, 0.5f, 1.5f);
		_toflip.players.setBatch(_batch);
		final float fwidth = _toflip.width(), fheight = _toflip.height()
				;
		_shadow = new Player() {
			@Override
			protected void paintImpl(GLEx g) {
				g.setAlpha(_alpha).setFillColor(0xFF000000)
						.fillRect(0, 0, fwidth / 4, fheight);
			}

			@Override
			public void update(long elapsedTime) {

			}
		};
		_toflip.players.addAt(_shadow, fwidth, 0);
		updateAngle(0);
	}

	@Override
	public boolean update(Stage o, Stage n, float elapsed) {
		updateAngle(elapsed);
		return elapsed >= _duration;
	}

	@Override
	public void complete(Stage o, Stage n) {
		super.complete(o, n);
		_shadow.close();
		n.players.setDepth(0);
		_toflip.players.setBatch(null);
	}

	@Override
	protected float defaultDuration() {
		return 1500;
	}

	@Override
	protected Easing def() {
		return Easing.JUST_EASE_IN;
	}

	protected void updateAngle(float elapsed) {
		float pct = _easing.applyClamp(0, 0.5f, elapsed,_duration);
		if (_close) {
			pct = 0.5f - pct;
		}
		_alpha = pct;
		_batch.angle = MathUtils.PI * pct;
	}

}
