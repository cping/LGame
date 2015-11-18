package loon.stage;

import loon.LSystem;
import loon.opengl.RotationYBatch;
import loon.utils.Easing;
import loon.utils.MathUtils;

public class FlipTransition extends EaseTransition<FlipTransition> {

	protected boolean _flipped, _unflip;
	protected RotationYBatch _obatch, _nbatch;

	public FlipTransition unflip() {
		_unflip = true;
		return this;
	}

	@Override
	public void init(Stage o, Stage n) {
		super.init(o, n);
		n.players.setDepth(-1);
		_obatch = new RotationYBatch(LSystem.base().graphics().gl, 0.5f, 0.5f,
				1);
		o.players.setBatch(_obatch);
		_nbatch = new RotationYBatch(LSystem.base().graphics().gl, 0.5f, 0.5f,
				1);
		n.players.setBatch(_nbatch);
	}

	@Override
	public boolean update(Stage o, Stage n, float elapsed) {
		float pct = _easing.applyClamp(0, 1,elapsed, _duration);
		if (pct >= 0.5f && !_flipped) {
			n.players.setDepth(0);
			o.players.setDepth(-1);
			_flipped = true;
		}
		if (_unflip) {
			pct = -pct;
		}
		_obatch.angle = MathUtils.PI * pct;
		_nbatch.angle = MathUtils.PI * (pct - 1);
		return elapsed >= _duration;
	}

	@Override
	public void complete(Stage o, Stage n) {
		super.complete(o, n);
		o.players.setDepth(0);
		o.players.setBatch(null);
		n.players.setDepth(0);
		n.players.setBatch(null);
	}

	@Override
	protected Easing def() {
		return Easing.TIME_LINEAR;
	}
}
