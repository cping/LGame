package loon.action;

import loon.action.sprite.effect.BaseEffect;

public class EffectTo extends ActionEvent {

	private BaseEffect _effect;

	public EffectTo(BaseEffect e) {
		this._effect = e;
	}

	@Override
	public void update(long elapsedTime) {
		isComplete = _effect.isCompleted();
	}

	@Override
	public void onLoad() {

	}

	@Override
	public boolean isComplete() {
		return isComplete;
	}

	@Override
	public ActionEvent cpy() {
		return new EffectTo(_effect);
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
