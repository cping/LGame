package loon.action;

import loon.action.sprite.effect.BaseEffect;

public class EffectTo extends ActionEvent {

	private BaseEffect _effect;

	public EffectTo(BaseEffect e) {
		this._effect = e;
	}

	@Override
	public void update(long elapsedTime) {
		_isCompleted = _effect.isCompleted();
	}

	@Override
	public void onLoad() {

	}

	@Override
	public boolean isComplete() {
		return _isCompleted;
	}

	@Override
	public ActionEvent cpy() {
		EffectTo eff = new EffectTo(_effect);
		eff.set(this);
		return eff;
	}

	@Override
	public ActionEvent reverse() {
		return cpy();
	}

	@Override
	public String getName() {
		return "effect";
	}

}
