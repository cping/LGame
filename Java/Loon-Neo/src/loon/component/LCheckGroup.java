package loon.component;

import loon.LTexture;
import loon.canvas.LColor;
import loon.event.SysTouch;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;

public class LCheckGroup extends LComponent {

	private LCheckBox selectedBtn;

	private final TArray<LCheckBox> checks;

	private float minX = -1, minY = -1, maxX = -1, maxY = -1;

	public LCheckGroup() {
		super(0, 0, 1, 1);
		this.customRendering = false;
		this.checks = new TArray<LCheckBox>(10);
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {
		for (LCheckBox check : checks) {
			check.createUI(g);
		}
	}

	@Override
	public void update(long elapsedTime) {
		for (LCheckBox check : checks) {
			check.update(elapsedTime);
		}
	}

	public void add(LCheckBox check) {
		if (minX == -1) {
			minX = check.getX();
		}
		if (minY == -1) {
			minY = check.getY();
		}
		minX = MathUtils.min(minX, check.getX());
		minY = MathUtils.min(minY, check.getY());
		maxX += MathUtils.max(maxY, check.getWidth());
		maxY += MathUtils.max(maxY, check.getHeight());
		setLocation(minX, minY);
		setSize((int) maxX, (int) maxY);
		checks.add(check);
	}

	@Override
	public void setColor(LColor c) {
		super.setColor(c);
		for (LCheckBox check : checks) {
			check.setColor(c);
		}
	}

	public TArray<LCheckBox> getCheckBoxs() {
		return checks;
	}

	@Override
	protected void processTouchDragged() {
		super.processTouchDragged();
		for (LCheckBox check : checks) {
			check.processTouchDragged();
		}
		super.processTouchDragged();
	}

	@Override
	protected void processTouchEntered() {
		super.processTouchEntered();
		for (LCheckBox check : checks) {
			check.processTouchEntered();
		}
	}

	@Override
	protected void processTouchExited() {
		super.processTouchExited();
		for (LCheckBox check : checks) {
			check.processTouchExited();
		}
	}

	@Override
	protected void processKeyPressed() {
		super.processKeyPressed();
		for (LCheckBox check : checks) {
			check.processKeyPressed();
		}
	}

	@Override
	protected void processKeyReleased() {
		super.processKeyReleased();
		for (LCheckBox check : checks) {
			check.processKeyReleased();
		}
	}

	@Override
	protected void processTouchClicked() {
		super.processTouchClicked();
		for (LCheckBox check : checks) {
			check.processTouchClicked();
		}
	}

	@Override
	protected void processTouchPressed() {
		super.processTouchPressed();
		for (LCheckBox check : checks) {
			if (check.contains(SysTouch.getX(), SysTouch.getY())) {
				check.processTouchPressed();
				selectedBtn = check;
			}
		}
	}

	@Override
	protected void processTouchReleased() {
		super.processTouchReleased();
		if (selectedBtn != null) {
			if (selectedBtn.contains(SysTouch.getX(), SysTouch.getY())) {
				selectedBtn.processTouchReleased();
			}
			for (LCheckBox check : checks) {
				if (selectedBtn != check) {
					check.setTicked(false);
				}
			}
		}
	}

	@Override
	public String getUIName() {
		return "CheckGroup";
	}

}
