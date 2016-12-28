package loon.action;

import loon.geom.Bezier;
import loon.geom.Vector2f;
import loon.utils.Easing.EasingMode;
import loon.utils.timer.EaseTimer;

public class BezierBy extends ActionEvent {

	protected Bezier bezier;

	protected float startX = -1, startY = -1;

	protected EaseTimer easeTimer;

	public BezierBy(float duration, Bezier b) {
		this(-1, -1, duration, EasingMode.Linear, b);
	}

	public BezierBy(float sx, float sy, float duration, Bezier b) {
		this(sx, sy, duration, EasingMode.Linear, b);
	}

	public BezierBy(float sx, float sy, float duration, EasingMode mode,
			Bezier b) {
		this.easeTimer = new EaseTimer(duration, mode);
		this.bezier = b;
		this.startX = sx;
		this.startY = sy;
	}

	@Override
	public void update(long elapsedTime) {
		easeTimer.update(elapsedTime);
		if (easeTimer.isCompleted()) {
			_isCompleted = true;
			return;
		}

		final float u = 1 - easeTimer.getProgress();
		final float tt = easeTimer.getProgress() * easeTimer.getProgress();
		final float uu = u * u;

		final float ut2 = 2 * u * easeTimer.getProgress();

		final float x = (uu * bezier.controlPoint1.x)
				+ (ut2 * bezier.controlPoint2.x) + (tt * bezier.endPosition.x);
		final float y = (uu * bezier.controlPoint1.y)
				+ (ut2 * bezier.controlPoint2.x) + (tt * bezier.endPosition.y);

		if (original != null) {
			original.setLocation(startX + x, startY + y);
		}
	}

	@Override
	public void onLoad() {
		if (original != null && startX == -1f && startY == -1f) {
			startX = original.getX();
			startY = original.getY();
		}
	}

	@Override
	public boolean isComplete() {
		return _isCompleted;
	}

	@Override
	public ActionEvent cpy() {
		BezierBy by = new BezierBy(startX, startY, easeTimer.getDuration(),
				easeTimer.getEasingMode(), bezier.cpy());
		by.set(this);
		return this;
	}

	@Override
	public ActionEvent reverse() {
		Bezier b = new Bezier();
		b.endPosition = bezier.endPosition.negate();
		b.controlPoint1 = Vector2f.addNew(bezier.controlPoint2,
				bezier.endPosition.negate());
		b.controlPoint2 = Vector2f.addNew(bezier.controlPoint1,
				bezier.endPosition.negate());
		BezierBy by = new BezierBy(startX, startY, easeTimer.getDuration(),
				easeTimer.getEasingMode(), b);
		by.set(this);
		return this;
	}

	@Override
	public String getName() {
		return "bezierby";
	}

}
